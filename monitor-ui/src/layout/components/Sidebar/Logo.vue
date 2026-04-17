<template>
  <div
    class="sidebar-logo-container"
    :class="{ collapse }"
    :style="{ backgroundColor: sideTheme === 'theme-dark' && navType !== 3 ? variables.menuBackground : variables.menuLightBackground }"
  >
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link is-collapse" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo" />
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo" />
        <div class="sidebar-brand">
          <h1
            class="sidebar-title"
            :style="{ color: sideTheme === 'theme-dark' && navType !== 3 ? variables.logoTitleColor : variables.logoLightTitleColor }"
          >
            {{ title }}
          </h1>
          <p class="sidebar-subtitle">Monitor Console</p>
        </div>
      </router-link>
    </transition>
  </div>
</template>

<script>
import logoImg from '@/assets/logo/monitor-mark.svg'
import variables from '@/assets/styles/variables.scss'

export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  computed: {
    variables() {
      return variables
    },
    sideTheme() {
      return this.$store.state.settings.sideTheme
    },
    navType() {
      return this.$store.state.settings.navType
    }
  },
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      logo: logoImg
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 0.8s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  height: 64px;
  background: #1a1f2e;
  overflow: hidden;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);

  .sidebar-logo-link {
    display: flex !important;
    align-items: center;
    justify-content: flex-start;
    flex-wrap: nowrap;
    height: 100%;
    width: 100%;
    padding: 0 22px;
    text-decoration: none;
  }

  .sidebar-logo-link.is-collapse {
    justify-content: center;
    padding: 0;
  }

  .sidebar-logo {
    width: 38px;
    height: 38px;
    flex: 0 0 auto;
    display: block;
  }

  .sidebar-brand {
    display: flex;
    flex-direction: column;
    justify-content: center;
    min-width: 0;
    margin-left: 12px;
  }

  .sidebar-title {
    margin: 0;
    font-size: 18px;
    font-weight: 700;
    line-height: 1.1;
    letter-spacing: 0.04em;
    color: #fff;
    white-space: nowrap;
  }

  .sidebar-subtitle {
    margin: 4px 0 0;
    font-size: 11px;
    line-height: 1;
    letter-spacing: 0.22em;
    text-transform: uppercase;
    color: rgba(255, 255, 255, 0.56);
    white-space: nowrap;
  }

  &.collapse {
    .sidebar-logo {
      width: 34px;
      height: 34px;
    }
  }
}
</style>
