<template>
  <div class="app-container monitor-app-page">
    <el-row :gutter="16" class="overview-row">
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="overview-card">
          <div class="overview-label">产品总数</div>
          <div class="overview-value">{{ overview.totalApps || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="overview-card online">
          <div class="overview-label">上架产品</div>
          <div class="overview-value">{{ overview.onlineApps || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="overview-card offline">
          <div class="overview-label">下架产品</div>
          <div class="overview-value">{{ overview.offlineApps || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="72px">
      <el-form-item label="关键词" prop="keyword">
        <el-input
          v-model="queryParams.keyword"
          placeholder="请输入产品名称或应用链接"
          clearable
          style="width: 280px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="平台" prop="storePlatform">
        <el-select v-model="queryParams.storePlatform" placeholder="请选择商店平台" clearable style="width: 220px">
          <el-option
            v-for="item in storePlatformOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
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
            type="warning"
            plain
            icon="el-icon-connection"
            size="mini"
            :disabled="multiple"
            @click="openAssignDialog(ids)"
            v-hasPermi="['monitor:app:edit']"
          >分配群组</el-button>
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
      <el-table-column label="产品名称" align="center" prop="productName" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="应用链接" align="center" prop="appLink" min-width="260" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <a class="link-type" :href="scope.row.appLink" target="_blank" rel="noopener noreferrer">{{ scope.row.appLink }}</a>
        </template>
      </el-table-column>
      <el-table-column label="商店平台" align="center" prop="storePlatform" width="120">
        <template slot-scope="scope">
          <span>{{ storePlatformLabel(scope.row.storePlatform) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="通知群组" align="center" min-width="220">
        <template slot-scope="scope">
          <span v-if="scope.row.alertChannelNames">{{ scope.row.alertChannelNames }}</span>
          <span v-else class="empty-text">未分配</span>
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
      <el-table-column label="备注" align="center" prop="remark" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
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
            icon="el-icon-connection"
            @click="openAssignDialog([scope.row.id])"
            v-hasPermi="['monitor:app:edit']"
          >分配群组</el-button>
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
      <el-form ref="form" class="app-edit-form" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16" class="app-edit-grid">
          <el-col :span="12">
            <el-form-item label="产品名称" prop="productName">
              <el-input v-model="form.productName" placeholder="请输入产品名称" />
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
          <el-col :span="24">
            <el-form-item label="应用链接" prop="appLink">
              <el-input v-model="form.appLink" type="textarea" :rows="3" resize="none" placeholder="请输入应用链接" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="通知群组" prop="alertChannelIds">
              <el-select
                v-model="form.alertChannelIds"
                multiple
                collapse-tags
                clearable
                filterable
                placeholder="可多选告警群组"
                style="width: 100%"
              >
                <el-option
                  v-for="item in alertChannelOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <div class="form-tip">一个产品支持分配到多个通知群组，未分配则不会发送群组告警。</div>
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

    <el-dialog title="导入产品" :visible.sync="importOpen" width="420px" append-to-body>
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
        :on-error="handleFileError"
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
        <el-button type="primary" :loading="upload.isUploading" :disabled="upload.isUploading" @click="submitFileForm">确定</el-button>
        <el-button :disabled="upload.isUploading" @click="importOpen = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="assignTitle" :visible.sync="assignOpen" width="520px" append-to-body>
      <el-form label-width="92px">
        <el-form-item label="通知群组">
          <el-select
            v-model="assignForm.channelIds"
            multiple
            collapse-tags
            filterable
            clearable
            placeholder="请选择要通知的群组"
            style="width: 100%"
          >
            <el-option
              v-for="item in alertChannelOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <div class="form-tip">取消所有选择后提交，表示清空这些产品的告警群组。</div>
        </el-form-item>
        <el-alert
          :closable="false"
          type="info"
          show-icon
          :title="'已选择 ' + assignForm.appIds.length + ' 个产品'"
        />
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" :loading="assignSubmitting" :disabled="assignSubmitting" @click="submitAssign">确定</el-button>
        <el-button :disabled="assignSubmitting" @click="assignOpen = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from "@/utils/auth"
import { addApp, assignAppAlertChannels, changeAppStatus, delApp, getAppFormOptions, getAppOverview, listApp, updateApp } from "@/api/monitor/app"

export default {
  name: "MonitorApp",
  dicts: ["monitor_store_type"],
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      single: true,
      multiple: true,
      ids: [],
      appList: [],
      overview: {},
      open: false,
      importOpen: false,
      assignOpen: false,
      assignSubmitting: false,
      title: "",
      assignTitle: "分配通知群组",
      alertChannelOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        keyword: undefined,
        status: undefined,
        storePlatform: undefined
      },
      form: {},
      assignForm: {
        appIds: [],
        channelIds: []
      },
      upload: {
        isUploading: false,
        updateSupport: false,
        headers: { Authorization: "Bearer " + getToken() },
        url: process.env.VUE_APP_BASE_API + "/monitor/app/importData"
      },
      rules: {
        productName: [{ required: true, message: "产品名称不能为空", trigger: "blur" }],
        appLink: [{ required: true, message: "应用链接不能为空", trigger: "blur" }],
        storePlatform: [{ required: true, message: "商店平台不能为空", trigger: "change" }]
      }
    }
  },
  computed: {
    storePlatformOptions() {
      return (this.dict.type.monitor_store_type || []).map(item => ({
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
        this.alertChannelOptions = this.normalizeOptions(data.alertChannels)
      }).catch(() => {
        this.alertChannelOptions = []
      })
    },
    normalizeOptions(options) {
      const list = Array.isArray(options) ? options : []
      return list
        .filter(item => item && item.value !== undefined && item.value !== null && item.value !== "")
        .map(item => ({
          label: item.label,
          value: Number(item.value)
        }))
    },
    storePlatformLabel(value) {
      const target = (this.dict.type.monitor_store_type || []).find(item => item.value === value)
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
    refreshAppPageData() {
      this.getFormOptions()
      this.getOverview()
      this.getList()
      window.dispatchEvent(new Event("monitor-quota-refresh"))
    },
    reset() {
      this.form = {
        id: undefined,
        productName: undefined,
        appLink: undefined,
        storePlatform: undefined,
        alertChannelIds: [],
        status: 1,
        remark: undefined
      }
      this.resetForm("form")
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = selection.length === 0
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
      this.open = true
      this.title = "新增产品"
    },
    handleUpdate(row) {
      this.reset()
      const current = row || this.appList.find(item => item.id === this.ids[0])
      if (!current) {
        return
      }
      this.form = {
        ...current,
        alertChannelIds: Array.isArray(current.alertChannelIds) ? current.alertChannelIds.map(item => Number(item)) : []
      }
      this.open = true
      this.title = "修改产品"
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
          alertChannelIds: (this.form.alertChannelIds || []).map(item => Number(item))
        }
        const request = payload.id ? updateApp(payload) : addApp(payload)
        request.then(() => {
          this.$modal.msgSuccess(payload.id ? "修改成功" : "新增成功")
          this.open = false
          this.refreshAppPageData()
        })
      })
    },
    handleDelete(row) {
      const target = row || this.appList.find(item => item.id === this.ids[0])
      if (!target) {
        return
      }
      this.$modal.confirm('确认删除产品 "' + target.productName + '" 吗？').then(() => {
        return delApp(target.id)
      }).then(() => {
        this.$modal.msgSuccess("删除成功")
        this.refreshAppPageData()
      }).catch(() => {})
    },
    handleStatusChange(row) {
      const text = row.status === 1 ? "上架" : "下架"
      this.$modal.confirm('确认将产品 "' + row.productName + '" 设置为' + text + '状态吗？').then(() => {
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
      if (this.upload.isUploading) {
        return
      }
      const files = this.$refs.upload && this.$refs.upload.uploadFiles
      if (!files || files.length === 0) {
        return
      }
      this.upload.isUploading = true
      this.$refs.upload.submit()
    },
    handleFileUploadProgress() {
      this.upload.isUploading = true
    },
    handleFileError() {
      this.upload.isUploading = false
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
      const data = response.data || {}
      this.refreshAppPageData()
      if (this.alertChannelOptions.length > 0 && Array.isArray(data.handledIds) && data.handledIds.length > 0) {
        this.openAssignDialog(data.handledIds, true)
      }
    },
    openAssignDialog(appIds, fromImport = false) {
      const normalizedIds = Array.isArray(appIds) ? appIds.filter(Boolean).map(item => Number(item)) : []
      if (normalizedIds.length === 0) {
        this.$modal.msgWarning("请先选择产品")
        return
      }
      const selectedRows = this.appList.filter(item => normalizedIds.includes(item.id))
      const currentChannelIds = fromImport || selectedRows.length !== 1
        ? []
        : ((selectedRows[0].alertChannelIds || []).map(item => Number(item)))
      this.assignForm = {
        appIds: normalizedIds,
        channelIds: currentChannelIds
      }
      this.assignTitle = fromImport ? "导入完成，分配通知群组" : "分配通知群组"
      this.assignSubmitting = false
      this.assignOpen = true
    },
    submitAssign() {
      if (this.assignSubmitting) {
        return
      }
      this.assignSubmitting = true
      assignAppAlertChannels({
        appIds: this.assignForm.appIds,
        channelIds: this.assignForm.channelIds
      }).then(() => {
        this.$modal.msgSuccess("分配成功")
        this.assignOpen = false
        this.getList()
      }).catch(() => {
      }).finally(() => {
        this.assignSubmitting = false
      })
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

.app-edit-form .el-form-item__label {
  white-space: nowrap;
}

.form-tip {
  margin-top: 6px;
  color: #909399;
  font-size: 12px;
  line-height: 1.6;
}

.empty-text {
  color: #c0c4cc;
}
</style>
