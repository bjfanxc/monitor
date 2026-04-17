<template>
  <div class="login">
    <div class="login-shell">
      <div class="login-brand">
        <p class="eyebrow">Monitor UI</p>
        <h1>{{ title }}</h1>
        <p class="brand-copy">更轻盈的后台界面，更清晰的状态感知体验。</p>
      </div>
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <h3 class="title">{{ title }}</h3>
        <p class="subtitle">欢迎回来，请登录继续使用控制台</p>
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            type="text"
            auto-complete="off"
            placeholder="账号"
          >
            <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            auto-complete="off"
            placeholder="密码"
            @keyup.enter.native="handleLogin"
          >
            <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="code" v-if="captchaEnabled">
          <el-input
            v-model="loginForm.code"
            auto-complete="off"
            placeholder="验证码"
            style="width: 63%"
            @keyup.enter.native="handleLogin"
          >
            <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
          </el-input>
          <div class="login-code">
            <img :src="codeUrl" @click="getCode" class="login-code-img"/>
          </div>
        </el-form-item>
        <div class="login-options">
          <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
          <router-link v-if="register" class="link-type" :to="'/register'">立即注册</router-link>
        </div>
        <el-form-item style="width:100%;">
          <el-button
            :loading="loading"
            size="medium"
            type="primary"
            class="login-submit"
            @click.native.prevent="handleLogin"
          >
            <span v-if="!loading">登录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'
import defaultSettings from '@/settings'

export default {
  name: "Login",
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      footerContent: defaultSettings.footerContent,
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true,
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(() => {})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100%;
  padding: 40px 24px 88px;
  background:
    radial-gradient(circle at left top, rgba(52, 135, 218, 0.28), transparent 28%),
    radial-gradient(circle at 80% 18%, rgba(38, 153, 123, 0.2), transparent 24%),
    linear-gradient(135deg, rgba(11, 18, 32, 0.9), rgba(22, 40, 68, 0.82)),
    url("../assets/images/login-background.jpg") center/cover no-repeat;
}

.login-shell {
  position: relative;
  z-index: 1;
  width: min(980px, 100%);
  display: grid;
  grid-template-columns: 1.1fr 420px;
  gap: 28px;
  align-items: stretch;
}

.login-brand,
.login-form {
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(18px);
  box-shadow: 0 28px 60px rgba(4, 10, 20, 0.28);
}

.login-brand {
  padding: 40px;
  border-radius: 28px;
  color: #eef6ff;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;

  .eyebrow {
    margin: 0 0 12px;
    font-size: 13px;
    letter-spacing: 0.26em;
    text-transform: uppercase;
    color: rgba(220, 233, 248, 0.72);
  }

  h1 {
    margin: 0;
    font-size: 42px;
    line-height: 1.12;
    font-weight: 700;
    letter-spacing: 0.02em;
  }

  .brand-copy {
    margin: 18px 0 0;
    max-width: 340px;
    font-size: 16px;
    line-height: 1.8;
    color: rgba(230, 239, 250, 0.8);
  }
}

.title {
  margin: 0;
  text-align: left;
  color: #122138;
  font-size: 28px;
}

.subtitle {
  margin: 10px 0 26px;
  color: #6b7a90;
  font-size: 14px;
}

.login-form {
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.9);
  width: 100%;
  padding: 34px 30px 20px;

  .el-input {
    height: 46px;

    input {
      height: 46px;
    }
  }

  .input-icon {
    height: 46px;
    width: 16px;
    margin-left: 4px;
    color: #7b8ba2;
  }
}

.login-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 4px 0 24px;
}

.login-submit {
  width: 100%;
  height: 46px;
  font-size: 15px;
}

.login-code {
  width: 33%;
  height: 46px;
  float: right;
  overflow: hidden;
  border-radius: 12px;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-login-footer {
  height: 44px;
  line-height: 44px;
  position: fixed;
  bottom: 12px;
  width: 100%;
  text-align: center;
  color: rgba(255, 255, 255, 0.82);
  font-size: 12px;
  letter-spacing: 0.08em;
}

.login-code-img {
  height: 46px;
  width: 100%;
  object-fit: cover;
}

@media (max-width: 980px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .login-brand {
    min-height: 220px;
  }
}

@media (max-width: 540px) {
  .login {
    padding: 20px 14px 72px;
  }

  .login-form,
  .login-brand {
    border-radius: 22px;
    padding-left: 20px;
    padding-right: 20px;
  }

  .login-brand h1 {
    font-size: 32px;
  }
}
</style>
