<template>
  <div class="app-container monitor-alert-channel-page">
    <div class="binding-guide">
      <div class="binding-guide__title">绑定 Telegram 机器人步骤：</div>
      <div class="binding-guide__item">
        1. 将
        <a v-if="bindingInfo.botLink" :href="bindingInfo.botLink" target="_blank" rel="noopener noreferrer">{{ bindingInfo.botLink }}</a>
        <span v-else>平台 Telegram 机器人</span>
        拉到群成员或者频道管理员。
      </div>
      <div class="binding-guide__item">
        2. 在群里或频道里发送“{{ bindingCommandExample }}”，机器人收到 webhook 后会自动把当前 Chat ID 绑定到对应账号。
      </div>
      <div class="binding-guide__item">
        绑定后会自动出现在下方列表，无需手工填写 Chat ID。
      </div>
      <div v-if="!bindingInfo.tokenConfigured" class="binding-guide__warn">
        当前平台机器人 Token 未在系统参数中配置，请先在参数设置中维护 `monitor.telegram.botToken`。
      </div>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="72px">
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入群组/频道或绑定账号"
          clearable
          style="width: 280px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="enabled">
        <el-select v-model="queryParams.enabled" placeholder="请选择状态" clearable style="width: 220px">
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
      <el-table-column label="群组/频道" align="center" prop="name" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="绑定账号" align="center" prop="createBy" min-width="150" :show-overflow-tooltip="true" />
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

  </div>
</template>

<script>
import { delAlertChannel, getAlertBindingInfo, listAlertChannel } from "@/api/monitor/alertChannel"

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
    handleDelete(row) {
      const target = row || this.channelList.find(item => item.id === this.ids[0])
      if (!target) {
        return
      }
      this.$modal.confirm('确认删除机器人绑定 "' + target.name + '" 吗？').then(() => {
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
.binding-guide {
  margin-bottom: 16px;
  padding: 18px 20px;
  background: #ffffff;
  border: 1px solid rgba(219, 228, 239, 0.95);
  border-radius: 14px;
  box-shadow: var(--panel-shadow-soft);
}

.binding-guide__title {
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 700;
  color: #1f2a44;
}

.binding-guide__item {
  line-height: 1.9;
  color: #d03050;
  font-size: 14px;
}

.binding-guide__item a {
  color: #1f6fd2;
}

.binding-guide__warn {
  margin-top: 10px;
  color: #e67e22;
  font-size: 13px;
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
