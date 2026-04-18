<template>
  <div class="app-container dashboard-page" v-loading="loading">
    <section class="hero-panel">
      <div class="hero-panel__content">
        <div class="hero-panel__eyebrow">监控驾驶舱</div>
        <h1 class="hero-panel__title">{{ welcomeTitle }}</h1>
        <p class="hero-panel__summary">
          当前共监控 <strong>{{ stats.totalApps }}</strong> 个应用，其中
          <strong class="ok">{{ stats.onlineApps }}</strong> 个正常，
          <strong class="danger">{{ stats.offlineApps }}</strong> 个异常，
          今日已产生 <strong>{{ stats.todayAlerts }}</strong> 条告警。
        </p>
        <div class="hero-panel__actions">
          <el-button type="danger" icon="el-icon-warning-outline" size="mini" @click="goTo('/monitor/alert')">查看告警</el-button>
          <el-button type="primary" plain icon="el-icon-monitor" size="mini" @click="goTo('/monitor/app')">查看应用</el-button>
          <el-button type="info" plain icon="el-icon-s-operation" size="mini" @click="goTo('/monitor/job')">查看任务</el-button>
        </div>
      </div>
      <div class="hero-panel__side">
        <div class="hero-metric">
          <span class="hero-metric__label">应用健康度</span>
          <span class="hero-metric__value">{{ onlineRate }}%</span>
        </div>
        <el-progress
          :percentage="Number(onlineRate)"
          :show-text="false"
          :stroke-width="12"
          color="#22c55e"
        />
        <div class="hero-panel__tips">
          <span>最近告警 {{ alerts.length }} 条</span>
          <span>最近失败任务 {{ stats.failedJobs }} 条</span>
        </div>
      </div>
    </section>

    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="card in statCards" :key="card.key">
        <div class="metric-card" :class="card.theme">
          <div class="metric-card__icon">
            <i :class="card.icon"></i>
          </div>
          <div class="metric-card__body">
            <div class="metric-card__label">{{ card.label }}</div>
            <div class="metric-card__value">{{ card.value }}</div>
            <div class="metric-card__desc">{{ card.desc }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="panel-row">
      <el-col :xs="24" :lg="14">
        <el-card shadow="hover" class="dashboard-card">
          <div slot="header" class="dashboard-card__header">
            <span>最新告警</span>
            <el-button type="text" @click="goTo('/monitor/alert')">查看更多</el-button>
          </div>
          <div v-if="alerts.length === 0" class="empty-state">暂无告警记录</div>
          <div v-else class="table-list">
            <div v-for="item in alerts" :key="item.id" class="table-list__row">
              <div class="table-list__main">
                <div class="table-list__title">
                  <span>{{ item.productName || "-" }} / {{ item.appName || "-" }}</span>
                  <el-tag size="mini" :type="alertTagType(item.alertType)">
                    {{ alertTypeLabel(item.alertType) }}
                  </el-tag>
                </div>
                <div class="table-list__meta">
                  <span>{{ channelLabel(item.channelType) }}</span>
                  <span>{{ parseTime(item.alertTime) }}</span>
                </div>
              </div>
              <div class="table-list__extra">
                {{ summarizeMessage(item.alertMessage) }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="10">
        <el-card shadow="hover" class="dashboard-card">
          <div slot="header" class="dashboard-card__header">
            <span>最近调度结果</span>
            <el-button type="text" @click="goTo('/monitor/job')">查看任务</el-button>
          </div>
          <div v-if="jobLogs.length === 0" class="empty-state">暂无调度日志</div>
          <div v-else class="job-list">
            <div v-for="item in jobLogs" :key="item.jobLogId" class="job-list__row">
              <div class="job-list__title">{{ item.jobName || "-" }}</div>
              <div class="job-list__meta">
                <el-tag size="mini" :type="jobStatusType(item.status)">
                  {{ jobStatusLabel(item.status) }}
                </el-tag>
                <span>{{ parseTime(item.createTime) }}</span>
              </div>
              <div class="job-list__message">{{ item.jobMessage || item.invokeTarget || "-" }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="panel-row">
      <el-col :xs="24" :lg="10">
        <el-card shadow="hover" class="dashboard-card">
          <div slot="header" class="dashboard-card__header">
            <span>平台分布</span>
            <span class="dashboard-card__sub">按已配置监控应用统计</span>
          </div>
          <div v-if="platformStats.length === 0" class="empty-state">暂无平台数据</div>
          <div v-else class="platform-list">
            <div v-for="item in platformStats" :key="item.name" class="platform-list__row">
              <div class="platform-list__head">
                <span>{{ item.name }}</span>
                <span>{{ item.count }} 个</span>
              </div>
              <el-progress
                :percentage="item.percent"
                :show-text="false"
                :stroke-width="10"
                color="#3b82f6"
              />
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="14">
        <el-card shadow="hover" class="dashboard-card">
          <div slot="header" class="dashboard-card__header">
            <span>快捷入口</span>
            <span class="dashboard-card__sub">常用操作一键进入</span>
          </div>
          <div class="shortcut-grid">
            <button
              v-for="item in shortcuts"
              :key="item.path"
              class="shortcut-card"
              type="button"
              @click="goTo(item.path)"
            >
              <div class="shortcut-card__icon">
                <i :class="item.icon"></i>
              </div>
              <div class="shortcut-card__title">{{ item.title }}</div>
              <div class="shortcut-card__desc">{{ item.desc }}</div>
            </button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from "vuex"
import { getAppOverview, listApp } from "@/api/monitor/app"
import { listAlert } from "@/api/monitor/alert"
import { listJobLog } from "@/api/monitor/jobLog"

export default {
  name: "Index",
  computed: {
    ...mapGetters(["nickName"]),
    welcomeTitle() {
      return `${this.nickName || "管理员"}，欢迎回来`
    },
    onlineRate() {
      const total = Number(this.stats.totalApps || 0)
      if (!total) {
        return "0.0"
      }
      return ((Number(this.stats.onlineApps || 0) / total) * 100).toFixed(1)
    },
    statCards() {
      return [
        {
          key: "total",
          label: "监控应用总数",
          value: this.stats.totalApps,
          desc: "当前已接入的应用数量",
          icon: "el-icon-s-grid",
          theme: "theme-default"
        },
        {
          key: "online",
          label: "正常应用数",
          value: this.stats.onlineApps,
          desc: "最近扫描结果为正常",
          icon: "el-icon-success",
          theme: "theme-success"
        },
        {
          key: "offline",
          label: "异常应用数",
          value: this.stats.offlineApps,
          desc: "需要优先关注和处理",
          icon: "el-icon-warning",
          theme: "theme-danger"
        },
        {
          key: "alerts",
          label: "今日告警数",
          value: this.stats.todayAlerts,
          desc: `最近失败任务 ${this.stats.failedJobs} 条`,
          icon: "el-icon-bell",
          theme: "theme-warning"
        }
      ]
    }
  },
  data() {
    return {
      loading: false,
      stats: {
        totalApps: 0,
        onlineApps: 0,
        offlineApps: 0,
        todayAlerts: 0,
        failedJobs: 0
      },
      alerts: [],
      jobLogs: [],
      platformStats: [],
      shortcuts: [
        { title: "应用监控", desc: "查看和维护监控应用", path: "/monitor/app", icon: "el-icon-monitor" },
        { title: "告警配置", desc: "管理 Telegram 告警渠道", path: "/monitor/alertChannel", icon: "el-icon-message-solid" },
        { title: "告警记录", desc: "查看最新异常通知", path: "/monitor/alert", icon: "el-icon-warning-outline" },
        { title: "定时任务", desc: "管理扫描任务计划", path: "/monitor/job", icon: "el-icon-date" },
        { title: "调度日志", desc: "排查任务执行失败原因", path: "/monitor/job-log/index/0", icon: "el-icon-s-operation" },
        { title: "在线用户", desc: "查看当前登录会话", path: "/monitor/online", icon: "el-icon-user" }
      ]
    }
  },
  created() {
    this.loadDashboard()
  },
  methods: {
    async loadDashboard() {
      this.loading = true
      try {
        const [overviewRes, alertRes, jobLogRes, appRes] = await Promise.allSettled([
          getAppOverview(),
          listAlert({ pageNum: 1, pageSize: 8 }),
          listJobLog({ pageNum: 1, pageSize: 6 }),
          listApp({ pageNum: 1, pageSize: 200 })
        ])

        if (overviewRes.status === "fulfilled") {
          const overview = overviewRes.value.data || {}
          this.stats.totalApps = Number(overview.totalApps || 0)
          this.stats.onlineApps = Number(overview.onlineApps || 0)
          this.stats.offlineApps = Number(overview.offlineApps || 0)
        }

        if (alertRes.status === "fulfilled") {
          const rows = alertRes.value.rows || []
          this.alerts = rows
          this.stats.todayAlerts = rows.filter(item => this.isToday(item.alertTime)).length
        }

        if (jobLogRes.status === "fulfilled") {
          const rows = jobLogRes.value.rows || []
          this.jobLogs = rows
          this.stats.failedJobs = rows.filter(item => !this.isJobSuccess(item.status)).length
        }

        if (appRes.status === "fulfilled") {
          this.platformStats = this.buildPlatformStats(appRes.value.rows || [])
        }
      } finally {
        this.loading = false
      }
    },
    buildPlatformStats(rows) {
      const total = rows.length
      if (!total) {
        return []
      }
      const counter = rows.reduce((result, item) => {
        const key = this.platformLabel(item.storePlatform)
        result[key] = (result[key] || 0) + 1
        return result
      }, {})
      return Object.keys(counter).map(name => ({
        name,
        count: counter[name],
        percent: Number(((counter[name] / total) * 100).toFixed(1))
      })).sort((a, b) => b.count - a.count)
    },
    platformLabel(value) {
      if (!value) {
        return "未设置"
      }
      if (value === "google_play") {
        return "Google Play"
      }
      return value
    },
    alertTypeLabel(value) {
      if (value === "APP_OFFLINE") {
        return "下架告警"
      }
      if (value === "APP_ONLINE") {
        return "恢复通知"
      }
      return value || "未知"
    },
    alertTagType(value) {
      if (value === "APP_OFFLINE") {
        return "danger"
      }
      if (value === "APP_ONLINE") {
        return "success"
      }
      return "info"
    },
    channelLabel(value) {
      if (!value) {
        return "系统"
      }
      if (value === "telegram") {
        return "Telegram"
      }
      return value
    },
    summarizeMessage(message) {
      if (!message) {
        return "-"
      }
      return message.replace(/\s+/g, " ").slice(0, 88)
    },
    isToday(value) {
      if (!value) {
        return false
      }
      const current = new Date()
      const target = new Date(value)
      return current.getFullYear() === target.getFullYear()
        && current.getMonth() === target.getMonth()
        && current.getDate() === target.getDate()
    },
    isJobSuccess(status) {
      return String(status) === "0"
    },
    jobStatusLabel(status) {
      return this.isJobSuccess(status) ? "成功" : "失败"
    },
    jobStatusType(status) {
      return this.isJobSuccess(status) ? "success" : "danger"
    },
    goTo(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style scoped lang="scss">
.dashboard-page {
  background:
    radial-gradient(circle at top left, rgba(59, 130, 246, 0.12), transparent 28%),
    radial-gradient(circle at top right, rgba(16, 185, 129, 0.12), transparent 24%),
    #f5f8fc;
  min-height: calc(100vh - 84px);
}

.hero-panel {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding: 28px 30px;
  border-radius: 24px;
  background: linear-gradient(135deg, #0f172a 0%, #153b6d 52%, #127c7c 100%);
  color: #f8fafc;
  box-shadow: 0 22px 48px rgba(15, 23, 42, 0.18);
}

.hero-panel__content {
  flex: 1;
  min-width: 0;
}

.hero-panel__eyebrow {
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: rgba(226, 232, 240, 0.72);
}

.hero-panel__title {
  margin: 10px 0 12px;
  font-size: 32px;
  line-height: 1.2;
  color: #ffffff;
}

.hero-panel__summary {
  max-width: 720px;
  margin: 0;
  line-height: 1.9;
  font-size: 15px;
  color: rgba(248, 250, 252, 0.9);
}

.hero-panel__summary strong {
  font-size: 18px;
  color: #ffffff;
}

.hero-panel__summary .ok {
  color: #86efac;
}

.hero-panel__summary .danger {
  color: #fca5a5;
}

.hero-panel__actions {
  margin-top: 22px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hero-panel__side {
  width: 280px;
  flex-shrink: 0;
  align-self: stretch;
  padding: 22px 20px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.14);
}

.hero-metric {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 14px;
}

.hero-metric__label {
  font-size: 14px;
  color: rgba(226, 232, 240, 0.82);
}

.hero-metric__value {
  font-size: 34px;
  font-weight: 700;
  color: #ffffff;
}

.hero-panel__tips {
  margin-top: 18px;
  display: grid;
  gap: 8px;
  color: rgba(226, 232, 240, 0.86);
  font-size: 13px;
}

.stats-row,
.panel-row {
  margin-top: 16px;
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px 18px;
  border-radius: 20px;
  background: #ffffff;
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.06);
  min-height: 124px;
}

.metric-card__icon {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.metric-card__body {
  min-width: 0;
}

.metric-card__label {
  color: #64748b;
  font-size: 13px;
  margin-bottom: 6px;
}

.metric-card__value {
  font-size: 30px;
  line-height: 1;
  font-weight: 700;
  color: #0f172a;
}

.metric-card__desc {
  margin-top: 10px;
  color: #94a3b8;
  font-size: 12px;
}

.theme-default .metric-card__icon {
  background: rgba(59, 130, 246, 0.12);
  color: #2563eb;
}

.theme-success .metric-card__icon {
  background: rgba(34, 197, 94, 0.14);
  color: #16a34a;
}

.theme-danger .metric-card__icon {
  background: rgba(239, 68, 68, 0.14);
  color: #dc2626;
}

.theme-warning .metric-card__icon {
  background: rgba(245, 158, 11, 0.15);
  color: #d97706;
}

.dashboard-card {
  border: none;
  border-radius: 22px;
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.06);
}

.dashboard-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
}

.dashboard-card__sub {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
}

.empty-state {
  padding: 28px 0;
  text-align: center;
  color: #94a3b8;
  font-size: 13px;
}

.table-list__row,
.job-list__row {
  padding: 14px 0;
  border-bottom: 1px solid #eef2f7;
}

.table-list__row:last-child,
.job-list__row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.table-list__title,
.job-list__meta,
.platform-list__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.table-list__title {
  color: #0f172a;
  font-weight: 600;
}

.table-list__meta,
.table-list__extra,
.job-list__message,
.job-list__title {
  margin-top: 6px;
}

.table-list__meta,
.job-list__meta {
  color: #64748b;
  font-size: 12px;
}

.table-list__extra,
.job-list__message {
  color: #475569;
  font-size: 13px;
  line-height: 1.6;
}

.job-list__title {
  font-weight: 600;
  color: #0f172a;
}

.platform-list__row {
  margin-bottom: 18px;
}

.platform-list__row:last-child {
  margin-bottom: 0;
}

.platform-list__head {
  margin-bottom: 8px;
  font-size: 13px;
  color: #334155;
}

.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.shortcut-card {
  border: 1px solid #e2e8f0;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 18px;
  padding: 18px 16px;
  text-align: left;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.shortcut-card:hover {
  transform: translateY(-2px);
  border-color: #93c5fd;
  box-shadow: 0 12px 24px rgba(59, 130, 246, 0.12);
}

.shortcut-card__icon {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(59, 130, 246, 0.1);
  color: #2563eb;
  font-size: 20px;
}

.shortcut-card__title {
  margin-top: 14px;
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
}

.shortcut-card__desc {
  margin-top: 8px;
  line-height: 1.6;
  color: #64748b;
  font-size: 12px;
}

@media (max-width: 1199px) {
  .hero-panel {
    flex-direction: column;
  }

  .hero-panel__side {
    width: 100%;
  }
}

@media (max-width: 767px) {
  .dashboard-page {
    padding: 8px;
  }

  .hero-panel {
    padding: 22px 18px;
    border-radius: 20px;
  }

  .hero-panel__title {
    font-size: 26px;
  }

  .shortcut-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
