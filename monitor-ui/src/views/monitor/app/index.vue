<template>
  <div class="app-container monitor-app-page">
    <el-row :gutter="16" class="overview-row">
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="overview-card">
          <div class="overview-label">应用总数</div>
          <div class="overview-value">{{ overview.totalApps || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="overview-card online">
          <div class="overview-label">上架应用</div>
          <div class="overview-value">{{ overview.onlineApps || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="overview-card offline">
          <div class="overview-label">下架应用</div>
          <div class="overview-value">{{ overview.offlineApps || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="72px">
      <el-form-item label="关键字" prop="keyword">
        <el-input
          v-model="queryParams.keyword"
          placeholder="请输入产品名称、应用名称或包名"
          clearable
          style="width: 280px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="平台" prop="storePlatform">
        <el-input
          v-model="queryParams.storePlatform"
          placeholder="请输入商店平台"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="地区" prop="region">
        <el-input
          v-model="queryParams.region"
          placeholder="请输入地区"
          clearable
          style="width: 210px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 180px">
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>

      <div class="query-toolbar">
        <div class="query-toolbar__actions">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['monitor:app:add']"
          >新增</el-button>
          <el-button
            type="success"
            plain
            icon="el-icon-edit"
            size="mini"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['monitor:app:edit']"
          >修改</el-button>
          <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            :disabled="single"
            @click="handleDelete"
            v-hasPermi="['monitor:app:remove']"
          >删除</el-button>
          <el-button
            type="warning"
            plain
            icon="el-icon-upload2"
            size="mini"
            @click="openImportDialog"
            v-hasPermi="['monitor:app:import']"
          >导入</el-button>
          <el-button
            type="info"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleDownloadTemplate"
            v-hasPermi="['monitor:app:import']"
          >模板</el-button>
        </div>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
      </div>
    </el-form>

    <el-table v-loading="loading" :data="appList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="产品名称" align="center" prop="productName" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="应用名称" align="center" prop="appName" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="包名 / Bundle ID" align="center" prop="bundleId" :show-overflow-tooltip="true" min-width="220" />
      <el-table-column label="平台" align="center" prop="storePlatform" width="120">
        <template slot-scope="scope">
          <span>{{ storePlatformLabel(scope.row.storePlatform) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="地区" align="center" prop="region" width="100" />
      <el-table-column label="所属类型" align="center" prop="ownerType" width="150">
        <template slot-scope="scope">
          <span>{{ ownerTypeLabel(scope.row.ownerType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="110">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(scope.row)"
            v-hasPermi="['monitor:app:status']"
          />
        </template>
      </el-table-column>
      <el-table-column label="最近扫描时间" align="center" prop="lastScanTime" width="170">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastScanTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" min-width="180" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['monitor:app:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['monitor:app:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="产品名称" prop="productName">
              <el-input v-model="form.productName" placeholder="请输入产品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应用名称" prop="appName">
              <el-input v-model="form.appName" placeholder="请输入应用名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="包名 / Bundle ID" prop="bundleId">
              <el-input v-model="form.bundleId" placeholder="请输入包名或 Bundle ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商店平台" prop="storePlatform">
              <el-select v-model="form.storePlatform" placeholder="请选择商店平台" style="width: 100%">
                <el-option
                  v-for="item in storePlatformOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地区" prop="region">
              <el-select v-model="form.region" placeholder="请选择地区" style="width: 100%">
                <el-option
                  v-for="item in regionOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属类型" prop="ownerType">
              <el-select v-model="form.ownerType" placeholder="请选择所属类型" style="width: 100%">
                <el-option
                  v-for="item in ownerTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :label="1">上架</el-radio>
                <el-radio :label="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="4" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="导入应用" :visible.sync="importOpen" width="420px" append-to-body>
      <el-upload
        ref="upload"
        drag
        :limit="1"
        accept=".xlsx,.xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div slot="tip" class="el-upload__tip">
          仅支持 xls、xlsx 文件
          <el-checkbox v-model="upload.updateSupport" style="margin-left: 8px">更新已存在数据</el-checkbox>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确定</el-button>
        <el-button @click="importOpen = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from "@/utils/auth"
import { addApp, changeAppStatus, delApp, getAppFormOptions, getAppOverview, listApp, updateApp } from "@/api/monitor/app"

export default {
  name: "MonitorApp",
  dicts: ["monitor_store_type", "monitor_area_type"],
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      single: true,
      ids: [],
      appList: [],
      overview: {},
      open: false,
      importOpen: false,
      title: "",
      ownerTypeOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        keyword: undefined,
        status: undefined,
        storePlatform: undefined,
        region: undefined
      },
      form: {},
      upload: {
        isUploading: false,
        updateSupport: false,
        headers: { Authorization: "Bearer " + getToken() },
        url: process.env.VUE_APP_BASE_API + "/monitor/app/importData"
      },
      rules: {
        productName: [{ required: true, message: "产品名称不能为空", trigger: "blur" }],
        appName: [{ required: true, message: "应用名称不能为空", trigger: "blur" }],
        bundleId: [{ required: true, message: "包名 / Bundle ID 不能为空", trigger: "blur" }],
        storePlatform: [{ required: true, message: "商店平台不能为空", trigger: "change" }],
        region: [{ required: true, message: "地区不能为空", trigger: "change" }],
        ownerType: [{ required: true, message: "所属类型不能为空", trigger: "change" }]
      }
    }
  },
  computed: {
    storePlatformOptions() {
      return (this.dict.type.monitor_store_type || []).map(item => ({
        label: item.label,
        value: item.value
      }))
    },
    regionOptions() {
      return (this.dict.type.monitor_area_type || []).map(item => ({
        label: item.label,
        value: item.value
      }))
    }
  },
  created() {
    this.getFormOptions()
    this.getOverview()
    this.getList()
  },
  methods: {
    getFormOptions() {
      getAppFormOptions().then(response => {
        const data = response.data || {}
        this.ownerTypeOptions = this.normalizeOptions(data.ownerTypes)
      }).catch(() => {
        this.ownerTypeOptions = []
      })
    },
    normalizeOptions(options) {
      const list = Array.isArray(options) ? options : []
      return list
        .filter(item => item && item.value !== undefined && item.value !== null && item.value !== "")
        .map(item => ({
          label: item.label,
          value: String(item.value)
        }))
    },
    ensureOptionExists(list, value) {
      if (value === undefined || value === null || value === "") {
        return list
      }
      const normalizedValue = String(value)
      if (list.some(item => item.value === normalizedValue)) {
        return list
      }
      return [...list, { label: normalizedValue, value: normalizedValue }]
    },
    storePlatformLabel(value) {
      const target = (this.dict.type.monitor_store_type || []).find(item => item.value === value)
      return target ? target.label : value
    },
    ownerTypeLabel(value) {
      const target = this.ownerTypeOptions.find(item => item.value === String(value))
      return target ? target.label : value
    },
    getOverview() {
      getAppOverview().then(response => {
        this.overview = response.data || {}
      })
    },
    getList() {
      this.loading = true
      listApp(this.queryParams).then(response => {
        this.appList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    reset() {
      this.form = {
        id: undefined,
        productName: undefined,
        appName: undefined,
        bundleId: undefined,
        storePlatform: undefined,
        region: undefined,
        ownerType: undefined,
        status: 1,
        remark: undefined
      }
      this.resetForm("form")
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleAdd() {
      this.reset()
      if (this.ownerTypeOptions.length === 1) {
        this.form.ownerType = this.ownerTypeOptions[0].value
      }
      this.open = true
      this.title = "新增应用"
    },
    handleUpdate(row) {
      this.reset()
      const current = row || this.appList.find(item => item.id === this.ids[0])
      this.form = {
        ...current,
        ownerType: current && current.ownerType !== undefined && current.ownerType !== null ? String(current.ownerType) : current.ownerType
      }
      this.ownerTypeOptions = this.ensureOptionExists(this.ownerTypeOptions, this.form.ownerType)
      this.open = true
      this.title = "修改应用"
    },
    cancel() {
      this.open = false
      this.reset()
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const payload = {
          ...this.form,
          region: this.form.region ? String(this.form.region).toUpperCase() : this.form.region,
          ownerType: this.form.ownerType ? String(this.form.ownerType) : this.form.ownerType
        }
        const request = payload.id ? updateApp(payload) : addApp(payload)
        request.then(() => {
          this.$modal.msgSuccess(payload.id ? "修改成功" : "新增成功")
          this.open = false
          this.getFormOptions()
          this.getOverview()
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const target = row || this.appList.find(item => item.id === this.ids[0])
      if (!target) {
        return
      }
      this.$modal.confirm('确认删除应用 "' + target.appName + '" 吗？').then(() => {
        return delApp(target.id)
      }).then(() => {
        this.$modal.msgSuccess("删除成功")
        this.getOverview()
        this.getList()
      }).catch(() => {})
    },
    handleStatusChange(row) {
      const text = row.status === 1 ? "上架" : "下架"
      this.$modal.confirm('确认将应用 "' + row.appName + '" 设置为' + text + "状态吗？").then(() => {
        return changeAppStatus({ id: row.id, status: row.status })
      }).then(() => {
        this.$modal.msgSuccess("状态修改成功")
        this.getOverview()
      }).catch(() => {
        row.status = row.status === 1 ? 0 : 1
      })
    },
    openImportDialog() {
      this.importOpen = true
      this.upload.isUploading = false
      this.$nextTick(() => {
        this.$refs.upload && this.$refs.upload.clearFiles()
      })
    },
    handleDownloadTemplate() {
      this.download("/monitor/app/importTemplate", {}, "monitor_app_template.xlsx")
    },
    submitFileForm() {
      this.$refs.upload.submit()
    },
    handleFileUploadProgress() {
      this.upload.isUploading = true
    },
    handleFileSuccess(response) {
      this.upload.isUploading = false
      this.importOpen = false
      this.$refs.upload.clearFiles()
      if (response.code !== 200) {
        this.$modal.msgError(response.msg || "导入失败")
        return
      }
      this.$alert(response.msg, "导入结果", { dangerouslyUseHTMLString: true })
      this.getOverview()
      this.getList()
    }
  }
}
</script>

<style scoped>
.overview-row {
  margin-bottom: 18px;
}

.overview-card {
  margin-bottom: 12px;
}

.overview-card.online {
  border-left: 4px solid #67c23a;
}

.overview-card.offline {
  border-left: 4px solid #f56c6c;
}

.overview-label {
  color: #909399;
  font-size: 13px;
}

.overview-value {
  margin-top: 10px;
  font-size: 30px;
  font-weight: 600;
  color: #303133;
}

.query-toolbar {
  width: 100%;
  margin-top: 6px;
  padding-top: 14px;
  border-top: 1px solid rgba(219, 228, 239, 0.95);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.query-toolbar__actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
</style>
