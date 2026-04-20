<template>
  <div class="app-container monitor-alert-channel-page">
    <div class="guide-grid">
      <el-card shadow="never" class="guide-card">
        <div class="guide-card__title">方式一：Webhook 绑定平台机器人</div>
        <div class="guide-card__desc">适合快速添加群组。只要把平台机器人拉进群里并发送绑定指令，系统会自动识别并创建告警群组。</div>
        <div class="guide-card__steps">
          <div>1. 把平台机器人拉进群组或频道，并授予发送消息权限。</div>
          <div>2. 在群里发送：<code>{{ bindingCommandExample }}</code></div>
          <div>3. 发送后群组会自动出现在下方列表，无需手动填写 Chat ID。</div>
        </div>
        <div class="guide-card__extra">
          <span v-if="bindingInfo.botLink">平台机器人地址：<a :href="bindingInfo.botLink" target="_blank" rel="noopener noreferrer">{{ bindingInfo.botLink }}</a></span>
          <span v-else>请先在系统参数里维护平台机器人地址。</span>
        </div>
      </el-card>

      <el-card shadow="never" class="guide-card">
        <div class="guide-card__title">方式二：自定义手动添加</div>
        <div class="guide-card__desc">适合已经准备好群组 Chat ID 和自有机器人 Token 的场景，可直接手动创建告警群组。</div>
        <div class="guide-card__steps">
          <div>1. 填写一个便于识别的群组名称。</div>
          <div>2. 填写群组 Chat ID。</div>
          <div>3. 填写你自己的机器人 Token，系统会使用该机器人向群组发送告警。</div>
        </div>
        <el-button type="primary" size="mini" icon="el-icon-plus" @click="handleAdd" v-hasPermi="['monitor:alert:channel:add']">自定义添加</el-button>
      </el-card>
    </div>

    <el-alert
      v-if="!bindingInfo.tokenConfigured"
      :closable="false"
      type="warning"
      show-icon
      title="当前平台机器人 Token 未配置，Webhook 绑定和平台默认发送能力都会受影响，请先维护系统参数 monitor.telegram.botToken。"
      class="page-alert"
    />

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="72px">
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入群组名或绑定账号"
          clearable
          style="width: 280px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="enabled">
        <el-select v-model="queryParams.enabled" placeholder="请选择状态" clearable style="width: 180px">
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
      <el-table-column label="群组名称" align="center" prop="name" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="添加方式" align="center" min-width="140">
        <template slot-scope="scope">
          <el-tag :type="scope.row.accessMode === 'webhook' ? 'success' : 'warning'">
            {{ scope.row.accessMode === 'webhook' ? '平台机器人 Webhook' : '自定义手动添加' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="绑定账号" align="center" prop="createBy" min-width="140" :show-overflow-tooltip="true" />
      <el-table-column label="Chat ID" align="center" prop="chatId" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="Token来源" align="center" min-width="120">
        <template slot-scope="scope">
          <span>{{ scope.row.accessMode === 'custom' ? '自定义机器人' : '平台默认' }}</span>
        </template>
      </el-table-column>
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

    <el-dialog :title="title" :visible.sync="open" width="620px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="添加方式" prop="accessMode">
          <el-radio-group v-model="form.accessMode">
            <el-radio label="webhook">平台机器人 Webhook</el-radio>
            <el-radio label="custom">自定义手动添加</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="群组名称" prop="name">
          <el-input v-model="form.name" placeholder="例如：GooglePlay 告警群" />
        </el-form-item>
        <el-form-item label="Chat ID" prop="chatId">
          <el-input v-model="form.chatId" placeholder="例如：-1001234567890" />
          <div class="form-tip">如果你不知道 Chat ID，建议优先使用“平台机器人 Webhook 绑定”方式。</div>
        </el-form-item>
        <el-form-item
          v-if="form.accessMode === 'custom'"
          label="机器人Token"
          prop="botToken"
          :rules="[{ required: true, message: '机器人Token不能为空', trigger: 'blur' }]"
        >
          <el-input v-model="form.botToken" placeholder="请输入你自己的机器人 Token" show-password />
          <div class="form-tip">自定义手动添加会使用你自己的机器人发送消息，因此 Token 为必填项。</div>
        </el-form-item>
        <el-form-item label="状态" prop="enabled">
          <el-radio-group v-model="form.enabled">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="可填写群组用途、负责人等" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addAlertChannel, delAlertChannel, getAlertBindingInfo, listAlertChannel, updateAlertChannel } from "@/api/monitor/alertChannel"

export default {
  name: "MonitorAlertChannel",
  computed: {
    bindingCommandExample() {
      const keyword = this.bindingInfo.bindKeyword || "绑定机器人"
      return `${keyword}:你的用户名或邮箱`
    }
  },
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
      bindingInfo: {
        botLink: "",
        botUsername: "",
        bindKeyword: "",
        tokenConfigured: true
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        enabled: undefined
      },
      form: {},
      rules: {
        accessMode: [{ required: true, message: "请选择添加方式", trigger: "change" }],
        name: [{ required: true, message: "群组名称不能为空", trigger: "blur" }],
        chatId: [{ required: true, message: "Chat ID 不能为空", trigger: "blur" }]
      }
    }
  },
  created() {
    this.getBindingInfo()
    this.getList()
  },
  methods: {
    getBindingInfo() {
      getAlertBindingInfo().then(response => {
        this.bindingInfo = {
          botLink: response.data.botLink || "",
          botUsername: response.data.botUsername || "",
          bindKeyword: response.data.bindKeyword || "",
          tokenConfigured: response.data.tokenConfigured !== false
        }
      })
    },
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
        accessMode: "custom",
        name: undefined,
        chatId: undefined,
        botToken: undefined,
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
      this.title = "新增告警群组"
    },
    handleUpdate(row) {
      this.reset()
      const current = row || this.channelList.find(item => item.id === this.ids[0])
      if (!current) {
        return
      }
      this.form = {
        ...current,
        accessMode: current.accessMode || "custom"
      }
      this.open = true
      this.title = "修改告警群组"
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
        const payload = { ...this.form }
        const request = payload.id ? updateAlertChannel(payload) : addAlertChannel(payload)
        request.then(() => {
          this.$modal.msgSuccess(payload.id ? "修改成功" : "新增成功")
          this.open = false
          this.getList()
          this.getBindingInfo()
        })
      })
    },
    handleDelete(row) {
      const target = row || this.channelList.find(item => item.id === this.ids[0])
      if (!target) {
        return
      }
      this.$modal.confirm('确认删除告警群组 "' + target.name + '" 吗？').then(() => {
        return delAlertChannel(target.id)
      }).then(() => {
        this.$modal.msgSuccess("删除成功")
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.guide-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.guide-card {
  border-radius: 14px;
  border: 1px solid rgba(219, 228, 239, 0.95);
}

.guide-card__title {
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: 700;
  color: #1f2a44;
}

.guide-card__desc {
  margin-bottom: 10px;
  color: #55657d;
  line-height: 1.8;
}

.guide-card__steps {
  margin-bottom: 12px;
  color: #334155;
  line-height: 1.9;
  font-size: 13px;
}

.guide-card__extra {
  color: #64748b;
  font-size: 12px;
}

.guide-card__extra a {
  color: #1f6fd2;
}

.page-alert {
  margin-bottom: 16px;
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

.form-tip {
  margin-top: 6px;
  color: #909399;
  font-size: 12px;
  line-height: 1.6;
}

@media (max-width: 960px) {
  .guide-grid {
    grid-template-columns: 1fr;
  }
}
</style>
