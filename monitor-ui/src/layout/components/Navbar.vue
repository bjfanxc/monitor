<template>
  <div class="navbar" :class="'nav' + navType">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb v-if="navType == 1" id="breadcrumb-container" class="breadcrumb-container" />
    <top-nav v-if="navType == 2" id="topmenu-container" class="topmenu-container" />
    <template v-if="navType == 3">
      <logo v-show="showLogo" :collapse="false" />
      <top-bar id="topbar-container" class="topbar-container" />
    </template>

    <div class="right-menu">
      <template v-if="device !== 'mobile'">
        <screenfull id="screenfull" class="right-menu-item hover-effect" />

        <el-tooltip content="Notifications" effect="dark" placement="bottom">
          <header-notice id="header-notice" class="right-menu-item hover-effect" />
        </el-tooltip>
      </template>

      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="hover">
        <div class="avatar-wrapper">
          <span class="user-avatar user-avatar--initial">{{ nickNameInitial }}</span>
          <span class="user-nickname">{{ nickName }}</span>
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item>Profile</el-dropdown-item>
          </router-link>
          <el-dropdown-item v-if="setting" @click.native="setLayout">
            <span>Layout</span>
          </el-dropdown-item>
          <el-dropdown-item @click.native="lockScreen">
            <span>Lock</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>Logout</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex"
import Breadcrumb from "@/components/Breadcrumb"
import TopNav from "./TopNav"
import TopBar from "./TopBar"
import Logo from "./Sidebar/Logo"
import Hamburger from "@/components/Hamburger"
import Screenfull from "@/components/Screenfull"
import HeaderNotice from "./HeaderNotice"

export default {
  components: {
    Breadcrumb,
    Logo,
    TopNav,
    TopBar,
    Hamburger,
    Screenfull,
    HeaderNotice
  },
  computed: {
    ...mapGetters([
      "sidebar",
      "device",
      "nickName"
    ]),
    nickNameInitial() {
      const source = (this.nickName || "U").trim()
      const firstChar = Array.from(source)[0] || "U"
      return firstChar.toUpperCase()
    },
    setting: {
      get() {
        return this.$store.state.settings.showSettings
      }
    },
    navType: {
      get() {
        return this.$store.state.settings.navType
      }
    },
    showLogo: {
      get() {
        return this.$store.state.settings.sidebarLogo
      }
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch("app/toggleSideBar")
    },
    setLayout() {
      this.$emit("setLayout")
    },
    lockScreen() {
      const currentPath = this.$route.fullPath
      this.$store.dispatch("lock/lockScreen", currentPath).then(() => {
        this.$router.push("/lock")
      })
    },
    logout() {
      this.$confirm("Confirm logout?", "Notice", {
        confirmButtonText: "OK",
        cancelButtonText: "Cancel",
        type: "warning"
      }).then(() => {
        this.$store.dispatch("LogOut").then(() => {
          location.href = "/index"
        })
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar.nav3 {
  .hamburger-container {
    display: none !important;
  }
}

.navbar {
  height: 64px;
  overflow: hidden;
  position: relative;
  margin: 0;
  padding: 0 18px;
  border-bottom: 1px solid rgba(219, 228, 239, 0.95);
  border-radius: 0;
  background: #ffffff;
  box-shadow: none;
  display: flex;
  align-items: center;
  box-sizing: border-box;

  .hamburger-container {
    line-height: 46px;
    height: 46px;
    width: 46px;
    cursor: pointer;
    transition: background .3s, transform .3s;
    -webkit-tap-highlight-color: transparent;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    margin-right: 10px;
    border-radius: 10px;

    &:hover {
      background: rgba(43, 108, 176, 0.08);
      transform: none;
    }
  }

  .breadcrumb-container {
    flex-shrink: 0;
  }

  .topmenu-container {
    position: absolute;
    left: 62px;
  }

  .topbar-container {
    flex: 1;
    min-width: 0;
    display: flex;
    align-items: center;
    overflow: hidden;
    margin-left: 8px;
  }

  .right-menu {
    height: 100%;
    display: flex;
    align-items: center;
    margin-left: auto;
    gap: 4px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 0 10px;
      height: 44px;
      min-width: 44px;
      font-size: 18px;
      color: #506176;
      border-radius: 10px;
      vertical-align: middle;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s, color .3s, transform .3s;

        &:hover {
          background: rgba(43, 108, 176, 0.08);
          color: #1f4f86;
          transform: none;
        }
      }
    }

    .avatar-container {
      margin-right: 0;
      padding-right: 6px;

      .avatar-wrapper {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 6px 8px;
        border-radius: 10px;
        position: relative;
      }

      .user-avatar {
        width: 34px;
        height: 34px;
        border-radius: 50%;
        border: 2px solid rgba(43, 108, 176, 0.18);
        box-shadow: none;
      }

      .user-avatar--initial {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, #1f6fd2 0%, #4b9cff 100%);
        color: #ffffff;
        font-size: 15px;
        font-weight: 700;
        letter-spacing: 0.02em;
        user-select: none;
      }

      .user-nickname {
        font-size: 14px;
        font-weight: 700;
        color: #122138;
      }
    }
  }
}
</style>
