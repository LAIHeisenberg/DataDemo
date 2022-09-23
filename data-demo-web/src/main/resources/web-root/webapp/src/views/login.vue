<template>
  <div class="login" :style="'background-image:url('+ Background +');'">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" label-position="left" label-width="0px" class="login-form">
      <h3 class="title">
        DataDemo 管理系统
      </h3>
      <el-tabs type="border-card" @tab-click="handleTabClick">
        <el-tab-pane label="用户密码登陆">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" type="text" auto-complete="off" placeholder="账号">
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" auto-complete="off" placeholder="密码" @keyup.enter.native="handleLogin">
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="code">
            <el-input v-model="loginForm.code" auto-complete="off" placeholder="验证码" style="width: 63%" @keyup.enter.native="handleLogin">
              <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
            </el-input>
            <div class="login-code">
              <img :src="codeUrl" @click="getCode">
            </div>
          </el-form-item>
          <el-form-item style="width:100%;">
            <el-button :loading="loading" size="medium" type="primary" style="width:100%;" @click.native.prevent="handleLogin">
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
          </el-form-item>
        </el-tab-pane>
        <!-- <el-tab-pane label="USBKEY登陆">
          <el-form-item prop="deviceNo">
            <el-input v-model="loginForm.deviceNo" type="text" auto-complete="off" :disabled="true" placeholder="UKEY设备号">
              <svg-icon slot="prefix" icon-class="usb" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="pinCode">
            <el-input v-model="pinCode" type="password" auto-complete="off" placeholder="请输入PIN码" >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button :loading="check_ukey_loading" size="medium" type="success" @click="checkUsbKeyExist" >
              <span v-if="!check_ukey_loading">插入U-KEY</span>
              <span v-else>查找中...</span>
            </el-button>
            <el-button :loading="loading" size="medium" type="primary" style="width:40%;" @click.native.prevent="handleUkeyLogin">
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
          </el-form-item>
        </el-tab-pane> -->
      </el-tabs>
    </el-form>
    <!--  底部  -->
    <div v-if="$store.state.settings.showFooter" id="el-login-footer">
      <span v-html="$store.state.settings.footerTxt" />
      <span v-if="$store.state.settings.caseNumber"> ⋅ </span>
      <a href="https://beian.miit.gov.cn/#/Integrated/index" target="_blank">{{ $store.state.settings.caseNumber }}</a>
    </div>
  </div>
</template>

<script>
import { encrypt } from '@/utils/rsaEncrypt'
import Config from '@/settings'
import { getCodeImg,generatePreSignCode } from '@/api/login'
import Cookies from 'js-cookie'
import qs from 'qs'
import Background from '@/assets/images/background.webp'
import mToken from '@/utils/mToken'
import {_Base64encode,_Base64decode} from '@/utils/base64'
export default {
  name: 'Login',
  data() {
    return {
      Background: Background,
      codeUrl: '',
      cookiePass: '',
      pinCode : null,
      loginForm: {
        username: '',
        password: '',
        code: '',
        uuid: '',
        deviceNo : null,
        sign : null,
        authMethod : 2,
      },
      loginRules: {
        // username: [{ required: true, trigger: 'blur', message: '用户名不能为空' }],
        // password: [{ required: true, trigger: 'blur', message: '密码不能为空' }],
        // code: [{ required: true, trigger: 'change', message: '验证码不能为空' }],
      },
      loading: false,
      check_ukey_loading : false,
      redirect: undefined,
      mToken : null,
      preSignCode : null,
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        const data = route.query
        if (data && data.redirect) {
          this.redirect = data.redirect
          delete data.redirect
          if (JSON.stringify(data) !== '{}') {
            this.redirect = this.redirect + '&' + qs.stringify(data, { indices: false })
          }
        }
      },
      immediate: true
    }
  },
  created() {
    // 获取验证码
    this.getCode()
    // 获取用户名密码等Cookie
    this.getCookie()
    // token 过期提示
    this.point()
    this.mToken = new mToken("mTokenPlugin")
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.codeUrl = res.img
        this.loginForm.uuid = res.uuid
      })
    },
    getPreSignCode() {
      const parentThis = this;
      generatePreSignCode().then(res => {
          parentThis.preSignCode = res.code
      })
    },
    getCookie() {
      const username = Cookies.get('username')
      let password = Cookies.get('password')
      // 保存cookie里面的加密后的密码
      this.cookiePass = password === undefined ? '' : password
      password = password === undefined ? this.loginForm.password : password
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password,
        code: ''
      }
    },
    handleTabClick(obj) {
      console.log(obj.index)
      if(obj.index == 0){
        //用户密码登陆
        this.loginForm.authMethod = 2
        this.getCode()
      }else {
        this.loginForm.authMethod = 1
        this.getPreSignCode()
      }
    },
    handleLogin() {
      const parentThis = this
      this.$refs.loginForm.validate(valid => {
        const user = {
          username: parentThis.loginForm.username,
          password: parentThis.loginForm.password,
          code: parentThis.loginForm.code,
          uuid: parentThis.loginForm.uuid,
          sign : parentThis.loginForm.sign,
          preSignCode : parentThis.preSignCode,
          authMethod : parentThis.loginForm.authMethod,
          dn : parentThis.loginForm.deviceNo,
        }
        if (user.password !== this.cookiePass) {
          user.password = encrypt(user.password)
        }
        if (valid) {
          this.loading = true
          this.$store.dispatch('Login', user).then(() => {
            this.loading = false
            this.$router.push({ path: this.redirect || '/' })
          }).catch(() => {
            this.loading = false
            this.getCode()
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    handleUkeyLogin(){
      this.loginForm.authMethod = 1
      const token = this.mToken
      const ret = token.SOF_Login(this.pinCode);
      if(!this.loginForm.deviceNo){
        this.$notify({
            title: '警告',
            message: '未查找到UKEY！ 请插入UKEY后点击<<插入U-KEY>>按钮',
            type: 'warning',
            duration: 2000
          })
          return;
      }
      if (ret != 0) {
        var retryCount = token.SOF_GetPinRetryCount();
        this.$notify({
            title: '警告',
            message: 'PIN码错误，剩余重试次数'+retryCount,
            type: 'warning',
            duration: 2000
          })
          return;
      }
      this.signData()
      this.handleLogin()
    },
    checkUsbKeyExist() {
      const token = this.mToken;
      //设备型号  GM3000
      var ret = token.SOF_LoadLibrary(token.GM3000);
      if (ret != 0) {
          this.$notify({
          title: '错误',
          message: "加载控件失败,错误码:" + token.SOF_GetLastError(),
          type: 'error',
          duration: 5000
        })
          return;
      }
      var deviceName = token.SOF_EnumDevice()
      if (!deviceName) {
        this.$notify({
          title: '错误',
          message: '未找到任何UKEY',
          type: 'error',
          duration: 5000
        })
        return;
      }
      deviceName = deviceName[0]
      var ret = token.SOF_GetDeviceInstance(deviceName, "")
      if (ret != 0) {
          console.log("绑定应用失败，确定是否初始化Key,错误码:" + token.SOF_GetLastError());
          return;
      }
      this.loginForm.deviceNo = deviceName
    },
    signData() {    
      const token = this.mToken;
      //签名证书 cerType = 1
      var cerType = 1
      //加密算法SHA1
      var DigestMethod = 2
      var inData = this.preSignCode;
      token.SOF_SetDigestMethod(Number(DigestMethod));
      //容器名称 
      var containerName = 'RSA'
      var signed = token.SOF_SignData(containerName, cerType, _Base64encode(inData), inData.length, 0);
      if (signed != null && signed != ""){
        console.log(signed)
      }else{
        this.$notify({
          title: '错误',
          message: "签名失败,错误码:" + token.SOF_GetLastError(),
          type: 'error',
          duration: 5000
        })
      }
      this.loginForm.sign = signed
    },
    point() {
      const point = Cookies.get('point') !== undefined
      if (point) {
        this.$notify({
          title: '提示',
          message: '当前登录状态已过期，请重新登录！',
          type: 'warning',
          duration: 5000
        })
        Cookies.remove('point')
      }
    },
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
  .login {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    background-size: cover;
  }
  .title {
    margin: 0 auto 30px auto;
    text-align: center;
    color: #707070;
  }

  .login-form {
    border-radius: 6px;
    background: #ffffff;
    width: 385px;
    padding: 25px 25px 5px 25px;
    .el-input {
      height: 38px;
      input {
        height: 38px;
      }
    }
    .input-icon{
      height: 39px;width: 14px;margin-left: 2px;
    }
  }
  .login-tip {
    font-size: 13px;
    text-align: center;
    color: #bfbfbf;
  }
  .login-code {
    width: 33%;
    display: inline-block;
    height: 38px;
    float: right;
    img{
      cursor: pointer;
      vertical-align:middle
    }
  }
</style>
