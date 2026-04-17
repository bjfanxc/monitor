<template>
  <div
  class="sidebar-logo-container"
    :class="{ collapse }"
    :style="{ backgroundColor: variables.menuLightBackground }"
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
            :style="{ color: variables.logoLightTitleColor }"
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
  height: 76px;
  background: #f7fafe;
  overflow: hidden;
  border-bottom: 1px solid rgba(219, 228, 239, 0.95);

  .sidebar-logo-link {
    display: flex !important;
    align-items: center;
    justify-content: flex-start;
    flex-wrap: nowrap;
    height: 100%;
    width: 100%;
    padding: 0 20px;
    text-decoration: none;
  }

  .sidebar-logo-link.is-collapse {
    justify-content: center;
    padding: 0;
  }

  .sidebar-logo {
    width: 42px;
    height: 42px;
    flex: 0 0 auto;
    display: block;
    filter: none;
  }

  .sidebar-brand {
    display: flex;
    flex-direction: column;
    justify-content: center;
    min-width: 0;
    margin-left: 14px;
  }

  .sidebar-title {
    margin: 0;
    font-size: 19px;
    font-weight: 700;
    line-height: 1.1;
    letter-spacing: 0.04em;
    color: #122138;
    white-space: nowrap;
  }

  .sidebar-subtitle {
    margin: 5px 0 0;
    font-size: 11px;
    line-height: 1;
    letter-spacing: 0.22em;
    text-transform: uppercase;
    color: rgba(77, 93, 115, 0.72);
    white-space: nowrap;
  }

  &.collapse {
    .sidebar-logo {
      width: 36px;
      height: 36px;
    }
  }
}
</style>
