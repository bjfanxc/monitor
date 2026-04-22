<template>
  <div v-if="quota.planName" class="header-quota">
    <el-popover placement="bottom" width="320" trigger="hover" popper-class="header-quota-popover">
      <div class="header-quota__panel">
        <div class="header-quota__panel-title">套餐用量</div>
        <div class="header-quota__row">
          <span>当前套餐</span>
          <strong :class="['header-quota__plan-name', planThemeClass]">{{ quota.planName }}</strong>
        </div>
        <div class="header-quota__row">
          <span>应用额度</span>
          <strong :class="usageClass(quota.usedApps, quota.maxApps)">{{ quota.usedApps || 0 }}/{{ quota.maxApps }}</strong>
        </div>
        <div class="header-quota__row">
          <span>群组额度</span>
          <strong :class="usageClass(quota.usedAlertChannels, quota.maxAlertChannels)">{{ quota.usedAlertChannels || 0 }}/{{ quota.maxAlertChannels }}</strong>
        </div>
        <div class="header-quota__row">
          <span>扫描间隔</span>
          <strong>{{ quota.scanIntervalMinutes }} 分钟</strong>
        </div>
      </div>
      <div slot="reference" :class="['header-quota__pill', planThemeClass]">
        <span class="header-quota__plan">{{ quota.planName }}</span>
        <span :class="['header-quota__item', usageClass(quota.usedApps, quota.maxApps)]">应用 {{ quota.usedApps || 0 }}/{{ quota.maxApps }}</span>
        <span :class="['header-quota__item', usageClass(quota.usedAlertChannels, quota.maxAlertChannels)]">群组 {{ quota.usedAlertChannels || 0 }}/{{ quota.maxAlertChannels }}</span>
      </div>
    </el-popover>
  </div>
</template>

<script>
import { getCurrentQuota } from "@/api/monitor/plan"

export default {
  name: "HeaderQuota",
  data() {
    return {
      quota: {},
      loading: false
    }
  },
  computed: {
    planThemeClass() {
      const code = String(this.quota.planCode || "").toLowerCase()
      const name = String(this.quota.planName || "")
      if (code.includes("pro") || code.includes("paid") || name.includes("付费")) {
        return "theme-pro"
      }
      if (code.includes("enterprise") || name.includes("企业")) {
        return "theme-enterprise"
      }
      if (code.includes("basic") || name.includes("基础")) {
        return "theme-basic"
      }
      return "theme-test"
    }
  },
  watch: {
    "$route.path": {
      immediate: true,
      handler() {
        this.fetchQuota()
      }
    }
  },
  mounted() {
    window.addEventListener("monitor-quota-refresh", this.fetchQuota)
  },
  beforeDestroy() {
    window.removeEventListener("monitor-quota-refresh", this.fetchQuota)
  },
  methods: {
    fetchQuota() {
      if (this.loading) {
        return
      }
      this.loading = true
      getCurrentQuota().then(response => {
        this.quota = response.data || {}
      }).catch(() => {
        this.quota = {}
      }).finally(() => {
        this.loading = false
      })
    },
    usageClass(used, total) {
      const safeUsed = Number(used || 0)
      const safeTotal = Number(total || 0)
      if (!safeTotal) {
        return ""
      }
      const ratio = safeUsed / safeTotal
      if (ratio >= 1) {
        return "is-danger"
      }
      if (ratio >= 0.8) {
        return "is-warning"
      }
      return ""
    }
  }
}
</script>

<style scoped>
.header-quota {
  display: flex;
  align-items: center;
  min-width: 0;
  margin-right: 6px;
}

.header-quota__pill {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  height: 36px;
  padding: 0 12px;
  border-radius: 999px;
  border: 1px solid transparent;
  color: #24415f;
  font-size: 13px;
  white-space: nowrap;
  transition: background .2s ease, border-color .2s ease, box-shadow .2s ease;
}

.header-quota__pill.theme-test {
  background: linear-gradient(135deg, rgba(56, 132, 255, 0.08), rgba(56, 132, 255, 0.16));
  border-color: rgba(56, 132, 255, 0.18);
}

.header-quota__pill.theme-basic {
  background: linear-gradient(135deg, rgba(42, 157, 143, 0.10), rgba(42, 157, 143, 0.18));
  border-color: rgba(42, 157, 143, 0.18);
}

.header-quota__pill.theme-pro {
  background: linear-gradient(135deg, rgba(255, 196, 0, 0.16), rgba(255, 140, 0, 0.24));
  border-color: rgba(217, 130, 43, 0.28);
}

.header-quota__pill.theme-enterprise {
  background: linear-gradient(135deg, rgba(36, 40, 48, 0.14), rgba(80, 102, 220, 0.20));
  border-color: rgba(79, 70, 229, 0.24);
}

.header-quota__plan {
  font-weight: 700;
}

.header-quota__pill.theme-test .header-quota__plan,
.header-quota__plan-name.theme-test {
  color: #1f5fbf;
}

.header-quota__pill.theme-basic .header-quota__plan,
.header-quota__plan-name.theme-basic {
  color: #1d7d72;
}

.header-quota__pill.theme-pro .header-quota__plan,
.header-quota__plan-name.theme-pro {
  color: #b96a11;
}

.header-quota__pill.theme-enterprise .header-quota__plan,
.header-quota__plan-name.theme-enterprise {
  color: #4338ca;
}

.header-quota__item {
  color: #4c6076;
}

.header-quota__item.is-warning,
.header-quota__row strong.is-warning {
  color: #d9822b;
}

.header-quota__item.is-danger,
.header-quota__row strong.is-danger {
  color: #d64545;
}

.header-quota__panel-title {
  font-size: 14px;
  font-weight: 700;
  color: #122138;
  margin-bottom: 10px;
}

.header-quota__row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: #506176;
  padding: 6px 0;
}
</style>
