<template>
  <div class="app-container">
    <div class="head-container">
        <!-- 搜索 -->
        <el-input
          v-model="query.email"
          clearable
          size="small"
          placeholder="请输入邮箱"
          style="width: 200px;"
          class="filter-item"
          @keyup.enter.native="crud.toQuery"
        />
        <el-input
          v-model="query.nickName"
          clearable
          size="small"
          placeholder="请输入昵称"
          style="width: 200px;"
          class="filter-item"
          @keyup.enter.native="crud.toQuery"
        />
        <el-input
          v-model="query.phone"
          clearable
          size="small"
          placeholder="请输入手机号"
          style="width: 200px;"
          class="filter-item"
          @keyup.enter.native="crud.toQuery"
        />
        <el-button class="filter-item" size="mini" type="success" icon="el-icon-search" @click="crud.toQuery">搜索</el-button>
        <crudOperation show="" :permission="permission" />
    </div>
    <el-row :gutter="20">
      <!--侧边部门数据-->
      <el-col :xs="9" :sm="6" :md="5" :lg="4" :xl="4" />
      <!--用户数据-->
      <el-col :xs="15" :sm="18" :md="19" :lg="20" :xl="20">
        <!--工具栏-->
        <!-- <div class="head-container">
          <el-row>
            <el-col :span="6">
              <h2 style="color:#df4c4c;">
                当前登陆用户: {{currentUser.userName}}
              </h2>
            </el-col>
            <el-col :span="4">
              <h2 style="color:#df4c4c;">
                电话: {{currentUser.phone}}
              </h2>
            </el-col>
            <el-col :span="2">
              &nbsp;
            </el-col>
            <el-col :span="10">
              <h2 style="color:#df4c4c;">
                邮箱：{{currentUser.email}}
              </h2>
            </el-col>
          </el-row>
        </div> -->
        <!--表单渲染-->
        <el-dialog append-to-body :close-on-click-modal="false" :before-close="crud.cancelCU" :visible.sync="crud.status.cu > 0" :title="crud.status.title" width="570px">
          <el-form ref="form" :inline="true" :model="form" :rules="rules" size="small" label-width="80px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" @keydown.native="keydown($event)" />
            </el-form-item>
            <el-form-item label="认证方式" prop="authMethod">
              <el-radio-group v-model="form.authMethod">
                <el-radio label="1">用户口令</el-radio>
                <el-radio label="2">USBKEY</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item label="电话" prop="tel">
              <el-input v-model.number="form.phone" />
            </el-form-item>
            <el-form-item label="昵称" prop="nickName">
              <el-input v-model="form.nickName" @keydown.native="keydown($event)" />
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="form.gender" style="width: 178px">
                <el-radio label="1">男</el-radio>
                <el-radio label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="text" @click="crud.cancelCU">取消</el-button>
            <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">确认</el-button>
          </div>
        </el-dialog>
        <!--表格渲染-->
        <el-table ref="table" v-loading="crud.loading" :data="crud.data" style="width: 100%;" @selection-change="crud.selectionChangeHandler">
          <el-table-column :selectable="checkboxT" type="selection" width="55" />
          <el-table-column :show-overflow-tooltip="true" prop="userName" label="用户名" />
          <el-table-column :show-overflow-tooltip="true" prop="nickName" label="昵称" />
          <el-table-column prop="gender" label="性别" :formatter="function(row, column, cellValue, index){return cellValue == 1 ? '男' : '女'}" />
          <el-table-column prop="authMethod" label="认证方式" :formatter="function(row, column, cellValue, index){return cellValue == 1 ? '用户口令' : 'USBKEY'}" />
          <el-table-column :show-overflow-tooltip="true" prop="phone" width="100" label="电话" />
          <el-table-column :show-overflow-tooltip="true" width="135" prop="email" label="邮箱" />
          <el-table-column label="状态" align="center" prop="enabled" >
            <template slot-scope="scope">
              <p v-if="scope.row.enabled">
                  启用
              </p>
              <p v-else>
                禁用
              </p>
            </template>
          </el-table-column>
        </el-table>
        <!--分页组件-->
        <pagination />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import crudUser from '@/api/system/user'
import { isvalidPhone } from '@/utils/validate'
import { getAll } from '@/api/system/role'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'
import store from '@/store'
import { mapGetters } from 'vuex'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
let userRoles = {}
const defaultForm = { id: null, username: null, nickName: null, gender: '男', email: null, enabled: 'false', roles: [], phone: null }
export default {
  name: 'User',
  components: { crudOperation, udOperation, pagination },
  cruds() {
    return CRUD({ title: '用户', url: 'api/users', crudMethod: { ...crudUser }, optShow: {
      add: true,
      download: false,
      reset: false
    }})
  },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  // 数据字典
  data() {
    // 自定义验证
    const validPhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入电话号码'))
      } else if (!isvalidPhone(value)) {
        callback(new Error('请输入正确的11位手机号码'))
      } else {
        callback()
      }
    }
    return {
      currentUser : store.getters.user,
      height: document.documentElement.clientHeight - 180 + 'px;',
      roles: [{id:1,name:'A'},{id:2,name:'B'}], menuIds: [], menus: [],
      roleDatas: null,
      defaultProps: { children: 'children', label: 'label', isLeaf: 'leaf' },
      permission: {
        add: ['admin', 'no-add'],
        edit: ['admin', 'no-edit'],
        del: ['admin', 'no-del']
      },
      enabledTypeOptions: [
        { key: 'true', display_name: '激活' },
        { key: 'false', display_name: '锁定' }
      ],
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        phone: [
          { required: true, trigger: 'blur', validator: validPhone }
        ]
      }
    }
  },
  computed: {
    ...mapGetters([
      'user'
    ])
  },
  created() {
    this.crud.msg.add = '新增成功，默认密码：123456'
  },
  mounted: function() {
    const that = this
    window.onresize = function temp() {
      that.height = document.documentElement.clientHeight - 180 + 'px;'
    }
  },
  methods: {
    // 禁止输入空格
    keydown(e) {
      if (e.keyCode === 32) {
        e.returnValue = false
      }
    },
    changeRole(value) {
      userRoles = {
        id: value
      }
    },
    // 新增与编辑前做的操作
    [CRUD.HOOK.afterToCU](crud, form) {
      this.getRoles()
      form.enabled = form.enabled.toString()
    },
    // 新增前将多选的值设置为空
    [CRUD.HOOK.beforeToAdd]() {
      this.roleDatas = null
    },
    // 初始化编辑时候的角色与岗位
    [CRUD.HOOK.beforeToEdit](crud, form) {
      this.roleDatas = form.role.id
    },
    // 提交前做的操作
    // [CRUD.HOOK.afterValidateCU](crud) {
    //   if (this.roleDatas == null | this.roleDatas === undefined) {
    //     this.$message({
    //       message: '角色不能为空',
    //       type: 'warning'
    //     })
    //     return false
    //   }
    //   crud.form.role = userRoles
    //   return true
    // },
    // 获取弹窗内角色数据
    getRoles() {
      getAll().then(res => {
        this.roles = res
      }).catch(() => { })
    },
    // 获取权限级别
    checkboxT(row, rowIndex) {
      return row.id !== this.user.id
    },
    getCookie() {
      const username = Cookies.get('username')
      let password = Cookies.get('phone')
      const rememberMe = Cookies.get('rememberMe')
      // 保存cookie里面的加密后的密码
      this.cookiePass = password === undefined ? '' : password
      password = password === undefined ? this.loginForm.password : password
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password,
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
        code: ''
      }
    },


  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  ::v-deep .vue-treeselect__control,::v-deep .vue-treeselect__placeholder,::v-deep .vue-treeselect__single-value {
    height: 30px;
    line-height: 30px;
  }
</style>
