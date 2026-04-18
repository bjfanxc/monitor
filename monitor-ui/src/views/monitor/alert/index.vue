<template>
  <div class="app-container monitor-alert-page">
    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="72px">
      <el-form-item label="关键字" prop="keyword">
        <el-input
          v-model="queryParams.keyword"
          placeholder="请输入产品名、应用名或包名"
          clearable
          style="width: 280px"
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
      <el-table-column label="产品名称" align="center" prop="productName" min-width="140" :show-overflow-tooltip="true" />
      <el-table-column label="应用名称" align="center" prop="appName" min-width="140" :show-overflow-tooltip="true" />
      <el-table-column label="包名 / Bundle ID" align="center" prop="bundleId" min-width="220" :show-overflow-tooltip="true" />
      <el-table-column label="平台" align="center" prop="storePlatform" width="120" />
      <el-table-column label="地区" align="center" prop="region" width="100" />
      <el-table-column label="渠道" align="center" prop="channelType" width="120" />
      <el-table-column label="类型" align="center" prop="alertType" width="140" />
      <el-table-column label="告警内容" align="center" prop="alertMessage" min-width="240" :show-overflow-tooltip="true" />
      <el-table-column label="告警时间" align="center" prop="alertTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.alertTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="扩展信息" align="center" min-width="200">
        <template slot-scope="scope">
          <span class="ext-json">{{ scope.row.extJson || "-" }}</span>
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
    getList() {
      this.loading = true
      listAlert(this.queryParams).then(response => {
        this.alertList = response.rows
        this.total = response.total
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

.ext-json {
  display: inline-block;
  white-space: pre-wrap;
  word-break: break-all;
  color: #606266;
}
</style>
