<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--侧边部门数据-->
      <el-col :xs="9" :sm="6" :md="5" :lg="4" :xl="4"></el-col>
      <!--用户数据-->
      <el-col :xs="15" :sm="18" :md="19" :lg="20" :xl="20">
        <!--工具栏-->
        <div class="head-container">
          <div v-if="crud.props.searchToggle">
            <!-- 搜索 -->
            <el-input
              v-model="query.blurry"
              clearable
              size="small"
              placeholder="输入名称或者邮箱搜索"
              style="width: 200px;"
              class="filter-item"
              @keyup.enter.native="crud.toQuery"
            />
            <el-select
              v-model="query.enabled"
              clearable
              size="small"
              placeholder="状态"
              class="filter-item"
              style="width: 90px"
              @change="crud.toQuery"
            >
              <el-option
                v-for="item in enabledTypeOptions"
                :key="item.key"
                :label="item.display_name"
                :value="item.key"
              />
            </el-select>
            <rrOperation />
          </div>
          <crudOperation show="" :permission="permission" />
        </div>
        <!--表单渲染-->
        <el-dialog append-to-body :close-on-click-modal="false" :before-close="crud.cancelCU" :visible.sync="crud.status.cu > 0" :title="crud.status.title" width="570px">
          <el-form ref="form" :inline="true" :model="form" :rules="rules" size="small" label-width="80px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" @keydown.native="keydown($event)" />
            </el-form-item>
            <el-form-item label="真实姓名" prop="realName" >
              <el-input v-model="form.realName" @keydown.native="keydown($event)" />
            </el-form-item>
            <el-form-item label="认证方式" prop="authMethod">
              <el-radio-group v-model="form.authMethod">
                <el-radio label="2">用户口令</el-radio>
                <el-radio label="1">USBKEY</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="设备号" v-if="form.authMethod == 1" >
              <el-input v-model="form.dn" :disabled="true" style="width: 69%;" />
              <el-button size="small" type="primary"  @click="handleUKey">{{ukeyBtnDesc}}</el-button>
              <el-input type="hidden" v-model="form.cert" ></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item label="电话" prop="tel">
              <el-input v-model.number="form.tel" />
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
            <el-form-item label="状态">
              <el-radio-group v-model="form.enabled" :disabled="form.id === user.id">
                <el-radio
                  v-for="item in dict.user_status"
                  :key="item.id"
                  :label="item.value"
                >{{ item.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item style="margin-bottom: 0;" label="角色" prop="roles">
              <el-select
                v-model="roleDatas"
                style="width: 437px"
                placeholder="请选择"
                @change="changeRole"
              >
                <el-option
                  v-for="item in roles"
                  :key="item.name"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <!-- <el-form-item lable="分配权限" style="width: 300px">
              <el-card class="box-card" shadow="never">
                <div slot="header" class="clearfix">
                  <el-tree
                    ref="menu"
                    lazy
                    :data="menus"
                    :default-checked-keys="menuIds"
                    :load="getMenuDatas"
                    :props="defaultProps"
                    check-strictly
                    accordion
                    show-checkbox
                    node-key="id"
                    @check="menuChange"
                  />
                </div>
              </el-card>
            </el-form-item> -->
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="text" @click="crud.cancelCU">取消</el-button>
            <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">确认</el-button>
          </div>
        </el-dialog>
        <!--表格渲染-->
        <el-table ref="table" v-loading="crud.loading" :data="crud.data" style="width: 100%;" @selection-change="crud.selectionChangeHandler">
          <el-table-column :selectable="checkboxT" type="selection" width="55" />
          <el-table-column :show-overflow-tooltip="true" prop="username" label="用户名" />
          <el-table-column :show-overflow-tooltip="true" prop="realName" label="真实姓名" />
          <el-table-column :show-overflow-tooltip="true" prop="nickName" label="昵称" />
          <el-table-column prop="gender" label="性别" :formatter="function(row, column, cellValue, index){return cellValue == 1 ? '男' : '女'}" />
          <el-table-column prop="authMethod" label="认证方式" :formatter="function(row, column, cellValue, index){return cellValue == 2 ? '用户口令' : 'USBKEY'}" />
          <el-table-column prop="role.name" label="用户类型"></el-table-column>
          <el-table-column :show-overflow-tooltip="true" prop="tel" width="100" label="电话" />
          <el-table-column :show-overflow-tooltip="true" width="135" prop="email" label="邮箱" />
          <el-table-column label="状态" align="center" prop="enabled">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.enabled"
                :disabled="user.id === scope.row.id"
                active-color="#409EFF"
                inactive-color="#F56C6C"
                @change="changeEnabled(scope.row, scope.row.enabled)"
              />
            </template>
          </el-table-column>
          <el-table-column :show-overflow-tooltip="true" prop="pwdResetTime" width="135" label="修改密码时间" />
          <el-table-column :show-overflow-tooltip="true" prop="createTime" width="135" label="创建日期" />
          <el-table-column
            v-if="checkPer(['admin','user:edit','user:del'])"
            label="操作"
            width="115"
            align="center"
            fixed="right"
          >
            <template slot-scope="scope">
              <udOperation
                :data="scope.row"
                :permission="permission"
                :disabled-dle="scope.row.id === user.id"
              />
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
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'
import Treeselect from '@riophae/vue-treeselect'
import { mapGetters } from 'vuex'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { getMenusTree, getChild } from '@/api/system/menu'
import mToken from '@/utils/mToken'
let userRoles = {}
const defaultForm = { id: null, username: null, nickName: null, gender: null, email: null, enabled: 'false', roles: [],  phone: null, dn : null, cert : null, authMethod : null}
export default {
  name: 'sysUser',
  components: { Treeselect, crudOperation, rrOperation, udOperation, pagination },
  cruds() {
    return CRUD({ title: '用户', url: 'api/users', crudMethod: { ...crudUser }, optShow: {
      add: true,
      download: false,
      reset: true
    }})
  },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  // 数据字典
  dicts: ['user_status'],
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
      height: document.documentElement.clientHeight - 180 + 'px;',
      mToken : null,
      ukeyBtnDesc : '检测UKEY',
      isDeviceExist : false,
      cert : null,
      roles: [],menuIds: [],menus: [], 
      roleDatas: null,
      defaultProps: { children: 'children', label: 'label', isLeaf: 'leaf' },
      permission: {
        add: ['admin', 'user:add'],
        edit: ['admin', 'user:edit'],
        del: ['admin', 'user:del']
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
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        authMethod : [
          { required: true, message: '请输入真实姓名', trigger: 'blur' },
        ],
        userType: [
          { required: true, message: '请选择用户类型', trigger: 'blur' },
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
    this.mToken = new mToken()
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
        id : value
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
      this.form.dn = null
      this.form.cert = null
      this.isDeviceExist = false
    },
    // 初始化编辑时候的角色与岗位
    [CRUD.HOOK.beforeToEdit](crud, form) {
      this.roleDatas = form.role.id
    },
    // 提交前做的操作
    [CRUD.HOOK.afterValidateCU](crud) {
      if (this.roleDatas == null | this.roleDatas === undefined) {
        this.$message({
          message: '角色不能为空',
          type: 'warning'
        })
        return false
      }
      crud.form.role = userRoles

      console.log(crud.form, 'form..')
      if(crud.form.authMethod == 1){
        if(!crud.form.dn){
          this.$notify({
            title: '错误',
            message: '未检测到UKEY',
            type: 'error',
            duration: 5000
          })
          return false;
        }
        if(!crud.form.cert){
          this.$notify({
            title: '错误',
            message: '证书未导出',
            type: 'error',
            duration: 5000
          })
          return false;
        }
      }

      return true
    },
    // 改变状态
    changeEnabled(data, val) {
      this.$confirm('此操作将 "' + this.dict.label.user_status[val] + '" ' + data.username + ', 是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        crudUser.edit(data).then(res => {
          this.crud.notify(this.dict.label.user_status[val] + '成功', CRUD.NOTIFICATION_TYPE.SUCCESS)
        }).catch(() => {
          data.enabled = !data.enabled
        })
      }).catch(() => {
        data.enabled = !data.enabled
      })
    },
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

    getMenuDatas(node, resolve) {
      setTimeout(() => {
        getMenusTree(node.data.id ? node.data.id : 0).then(res => {
          resolve(res)
        })
      }, 100)
    },

    menuChange(menu) {
      // 获取该节点的所有子节点，id 包含自身
      getChild(menu.id).then(childIds => {
        // 判断是否在 menuIds 中，如果存在则删除，否则添加
        if (this.menuIds.indexOf(menu.id) !== -1) {
          for (let i = 0; i < childIds.length; i++) {
            const index = this.menuIds.indexOf(childIds[i])
            if (index !== -1) {
              this.menuIds.splice(index, 1)
            }
          }
        } else {
          for (let i = 0; i < childIds.length; i++) {
            const index = this.menuIds.indexOf(childIds[i])
            if (index === -1) {
              this.menuIds.push(childIds[i])
            }
          }
        }
        this.$refs.menu.setCheckedKeys(this.menuIds)
      })
    },
    handleUKey(){
      if(!this.isDeviceExist){
        //设备存在
        if(this.checkUkeyExist()){
          this.ukeyBtnDesc = '导出证书'
        }
      }else {
        this.exportCert()
      }
    },
    checkUkeyExist(){
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
          return false;
      }
      var deviceName = token.SOF_EnumDevice()
      if (deviceName == null) {
          if (ret != 0) {
              this.$notify({
                title: '错误',
                message: '未找到任何UKEY',
                type: 'error',
                duration: 5000
              })
          }
          return false;
      }
      deviceName = deviceName[0]
      var ret = token.SOF_GetDeviceInstance(deviceName, "")
      if (ret != 0) {
          console.log("绑定应用失败，确定是否初始化Key,错误码:" + token.SOF_GetLastError());
          return false;
      }
      this.form.dn = deviceName
      this.isDeviceExist = true
      return true;
    },
    exportCert(){
      const token = this.mToken;
      //容器名称
      const containerName = 'RSA'
      //证书类型
      const cerType = 1
      const cert = token.SOF_ExportUserCert(containerName, cerType);
      if(cert){
        this.$notify({
              title: '',
              message: '证书导出成功！',
              type: 'success',
              duration: 5000
            })
      }else{
        this.$notify({
              title: '',
              message: '证书导出失败',
              type: 'warn',
              duration: 5000
            })
      }
      this.form.cert = cert;
      console.log(cert,'导出证书')
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  ::v-deep .vue-treeselect__control,::v-deep .vue-treeselect__placeholder,::v-deep .vue-treeselect__single-value {
    height: 30px;
    line-height: 30px;
  }
</style>
