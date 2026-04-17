<template>
  <div class="app-container monitor-alert-channel-page">
    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入渠道名称"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="enabled">
        <el-select v-model="queryParams.enabled" placeholder="请选择状态" clearable style="width: 160px">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
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
            v-hasPermi="['monitor:alert:channel:add']"
          >新增</el-button>
          <el-button
            type="success"
            plain
            icon="el-icon-edit"
            size="mini"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['monitor:alert:channel:edit']"
          >修改</el-button>
          <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            :disabled="single"
            @click="handleDelete"
            v-hasPermi="['monitor:alert:channel:remove']"
          >删除</el-button>
        </div>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
      </div>
    </el-form>

    <el-table v-loading="loading" :data="channelList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="渠道名称" align="center" prop="name" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="渠道类型" align="center" prop="channelType" width="120" />
      <el-table-column label="Bot Token" align="center" min-width="220" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ maskToken(scope.row.botToken) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Chat ID" align="center" prop="chatId" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enabled === 1 ? 'success' : 'info'">
            {{ scope.row.enabled === 1 ? "启用" : "停用" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="更新时间" align="center" prop="updateTime" width="170">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime || scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['monitor:alert:channel:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['monitor:alert:channel:remove']"
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

    <el-dialog :title="title" :visible.sync="open" width="680px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="渠道名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入渠道名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="enabled">
              <el-radio-group v-model="form.enabled">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="Bot Token" prop="botToken">
              <el-input v-model="form.botToken" placeholder="请输入 Telegram Bot Token" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="Chat ID" prop="chatId">
              <el-input v-model="form.chatId" placeholder="请输入 Telegram Chat ID" />
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
  </div>
</template>

<script>
import { addAlertChannel, delAlertChannel, listAlertChannel, updateAlertChannel } from "@/api/monitor/alertChannel"

export default {
  name: "MonitorAlertChannel",
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      single: true,
      ids: [],
      channelList: [],
      open: false,
      title: "",
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        enabled: undefined
      },
      form: {},
      rules: {
        name: [{ required: true, message: "渠道名称不能为空", trigger: "blur" }],
        botToken: [{ required: true, message: "Bot Token 不能为空", trigger: "blur" }],
        chatId: [{ required: true, message: "Chat ID 不能为空", trigger: "blur" }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listAlertChannel(this.queryParams).then(response => {
        this.channelList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    reset() {
      this.form = {
        id: undefined,
        name: undefined,
        botToken: undefined,
        chatId: undefined,
        enabled: 1,
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
      this.open = true
      this.title = "新增 Telegram 渠道"
    },
    handleUpdate(row) {
      this.reset()
      const current = row || this.channelList.find(item => item.id === this.ids[0])
      this.form = { ...current }
      this.open = true
      this.title = "修改 Telegram 渠道"
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
        const request = this.form.id ? updateAlertChannel(this.form) : addAlertChannel(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.id ? "修改成功" : "新增成功")
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const target = row || this.channelList.find(item => item.id === this.ids[0])
      if (!target) {
        return
      }
      this.$modal.confirm('确认删除渠道 "' + target.name + '" 吗？').then(() => {
        return delAlertChannel(target.id)
      }).then(() => {
        this.$modal.msgSuccess("删除成功")
        this.getList()
      }).catch(() => {})
    },
    maskToken(token) {
      if (!token || token.length <= 10) {
        return token || "-"
      }
      return token.slice(0, 6) + "******" + token.slice(-4)
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
