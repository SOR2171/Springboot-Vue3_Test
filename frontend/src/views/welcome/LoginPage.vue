<script setup>
import {reactive, ref} from "vue";
import {Lock, User} from "@element-plus/icons-vue";
import {login} from "../../net/index.js";
import router from "../../router/index.js";

const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  remember: false
})

const formRule = {
  username: [
    {required: true, message: 'please input username or email', trigger: 'blur'}
  ],
  password: [
    {required: true, message: 'please input password', trigger: 'blur'}
  ]
}

function userLogin() {
  formRef.value.validate((valid) => {
    if (valid) {
      login(form.username, form.password, form.remember, () => router.push('/index'))
    } else {
      console.log('error submit!')
      return false
    }
  })
}
</script>

<template>
  <div style="text-align: center;margin: 32px">
    <div style="margin-top: 169px">
      <div style="font-size: 28px;font-weight: bold">Login</div>
      <div style="font-size: 14px;color: slategray">Please enter your username (or your email) and your password</div>
    </div>

    <div style="margin: 32px">
      <el-form model="form" :rules="formRule" ref="formRef">
        <el-form-item>
          <el-input v-model="form.username" maxlength="63" type="text" placeholder="username or email">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-input v-model="form.password" maxlength="20" type="password" placeholder="password">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-row>
          <el-col :span="12" style="text-align: left">
            <el-form-item>
              <el-checkbox v-model="form.remember" label="remember me"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-link>
              forget password?
            </el-link>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div style="margin-top: 24px">
      <el-button @click="userLogin" style="width: 280px" type="primary">
        Sign in
      </el-button>
    </div>
    <el-divider>
      <span style="font-size: 10px;color: slategray">have no account?</span>
    </el-divider>
    <div style="margin-top: 16px">
      <el-button style="width: 280px" type="default">
        Sign up
      </el-button>
    </div>
  </div>
</template>

<style scoped>

</style>
