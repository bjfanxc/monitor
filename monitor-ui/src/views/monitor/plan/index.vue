<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="套餐编码" prop="planCode">
        <el-input v-model="queryParams.planCode" placeholder="请输入套餐编码" clearable style="width: 220px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="套餐名称" prop="planName">
        <el-input v-model="queryParams.planName" placeholder="请输入套餐名称" clearable style="width: 220px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 180px">
          <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
      <div class="query-toolbar">
        <div class="query-toolbar__actions">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['monitor:plan:add']">新增</el-button>
          <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['monitor:plan:edit']">修改</el-button>
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['monitor:plan:remove']">删除</el-button>
        </div>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
      </div>
    </el-form>

    <el-table v-loading="loading" :data="planList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="套餐编码" align="center" prop="planCode" min-width="120" />
      <el-table-column label="套餐名称" align="center" prop="planName" min-width="140" />
      <el-table-column label="群组上限" align="center" prop="maxAlertChannels" width="110" />
      <el-table-column label="应用上限" align="center" prop="maxApps" width="110" />
      <el-table-column label="扫描间隔(分钟)" align="center" prop="scanIntervalMinutes" width="130" />
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
      <el-table-column label="默认套餐" align="center" prop="isDefault" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isDefault === 1 ? 'success' : 'info'">{{ scope.row.isDefault === 1 ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['monitor:plan:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['monitor:plan:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="640px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="套餐编码" prop="planCode">
              <el-input v-model="form.planCode" placeholder="例如 test / pro" :disabled="form.id !== undefined" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="套餐名称" prop="planName">
              <el-input v-model="form.planName" placeholder="请输入套餐名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="群组上限" prop="maxAlertChannels">
              <el-input-number v-model="form.maxAlertChannels" :min="0" :step="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应用上限" prop="maxApps">
              <el-input-number v-model="form.maxApps" :min="0" :step="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="扫描间隔(分钟)" prop="scanIntervalMinutes">
              <el-input-number v-model="form.scanIntervalMinutes" :min="1" :step="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序号" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" :step="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="默认套餐" prop="isDefault">
              <el-radio-group v-model="form.isDefault">
                <el-radio :label="1">是</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
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
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addPlan, delPlan, getPlan, listPlan, updatePlan } from "@/api/monitor/plan"

export default {
  name: "MonitorPlan",
  dicts: ["sys_normal_disable"],
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      single: true,
      multiple: true,
      ids: [],
      planList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        planCode: undefined,
        planName: undefined,
        status: undefined
      },
      form: {},
      rules: {
        planCode: [{ required: true, message: "套餐编码不能为空", trigger: "blur" }],
        planName: [{ required: true, message: "套餐名称不能为空", trigger: "blur" }],
        maxAlertChannels: [{ required: true, message: "群组上限不能为空", trigger: "blur" }],
        maxApps: [{ required: true, message: "应用上限不能为空", trigger: "blur" }],
        scanIntervalMinutes: [{ required: true, message: "扫描间隔不能为空", trigger: "blur" }],
        sortOrder: [{ required: true, message: "排序号不能为空", trigger: "blur" }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPlan(this.queryParams).then(response => {
        this.planList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    reset() {
      this.form = {
        id: undefined,
        planCode: undefined,
        planName: undefined,
        maxAlertChannels: 0,
        maxApps: 0,
        scanIntervalMinutes: 5,
        sortOrder: 0,
        isDefault: 0,
        status: "0",
        remark: undefined
      }
      this.resetForm("form")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "新增套餐"
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids[0]
      getPlan(id).then(response => {
        this.form = response.data || {}
        this.open = true
        this.title = "修改套餐"
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const request = this.form.id !== undefined ? updatePlan(this.form) : addPlan(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.id !== undefined ? "修改成功" : "新增成功")
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除套餐编号为"' + ids + '"的数据项？').then(() => {
        return delPlan(ids)
      }).then(() => {
        this.$modal.msgSuccess("删除成功")
        this.getList()
      }).catch(() => {})
    },
    cancel() {
      this.open = false
      this.reset()
    }
  }
}
</script>

<style scoped>
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
