<template>
  <div class="app-container monitor-alert-page">
    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="72px">
      <el-form-item label="关键词" prop="keyword">
        <el-input
          v-model="queryParams.keyword"
          placeholder="请输入产品名称或应用链接"
          clearable
          style="width: 320px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道" prop="channelType">
        <el-input
          v-model="queryParams.channelType"
          placeholder="请输入告警渠道"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="alertType">
        <el-input
          v-model="queryParams.alertType"
          placeholder="请输入告警类型"
          clearable
          style="width: 210px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>

      <div class="query-toolbar">
        <div class="query-toolbar__summary">告警记录列表</div>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
      </div>
    </el-form>

    <el-table v-loading="loading" :data="alertList">
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="产品名称" align="center" prop="productName" min-width="160" :show-overflow-tooltip="true" />
      <el-table-column label="应用链接" align="center" prop="appLink" min-width="260" :show-overflow-tooltip="true" />
      <el-table-column label="平台" align="center" prop="storePlatform" width="120">
        <template slot-scope="scope">
          <span>{{ storePlatformLabel(scope.row.storePlatform) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="渠道" align="center" prop="channelType" width="120" />
      <el-table-column label="类型" align="center" prop="alertType" width="140" />
      <el-table-column label="告警内容" align="center" prop="alertMessage" min-width="260" :show-overflow-tooltip="true" />
      <el-table-column label="告警时间" align="center" prop="alertTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.alertTime) }}</span>
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
import { listAlert } from "@/api/monitor/alert"

export default {
  name: "MonitorAlert",
  dicts: ["monitor_store_type"],
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      alertList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        keyword: undefined,
        channelType: undefined,
        alertType: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    storePlatformLabel(value) {
      const target = (this.dict.type.monitor_store_type || []).find(item => item.value === value)
      return target ? target.label : value
    },
    getList() {
      this.loading = true
      listAlert(this.queryParams).then(response => {
        this.alertList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
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

.query-toolbar__summary {
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
}
</style>
