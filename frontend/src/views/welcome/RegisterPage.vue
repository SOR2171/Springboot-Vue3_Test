<script setup>
import {reactive, ref} from "vue";
import {Bell, Lock, Message, User} from "@element-plus/icons-vue";
import router from "../../router/index.js";
import {get, post} from "../../net/index.js";
import {ElMessage} from "element-plus";

const coldTime = ref(0);
const formRef = ref()
const isCounting = ref(false)
let timer = null

const form = reactive({
  username: '',
  password: '',
  passwordRepeat: '',
  email: '',
  code: ''
})

const startColdTime = () => {
  isCounting.value = true
  coldTime.value = 60

  timer = setInterval(() => {
    coldTime.value--
    if (coldTime.value <= 0) {
      clearInterval(timer)
      timer = null
      isCounting.value = false
      coldTime.value = 0
    }
  }, 1000)
}

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Please input username'));
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('Only letters, numbers, or Chinese characters'));
  } else {
    callback();
  }
}

const validatePasswordRepeat = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Please repeat password'));
  } else if (value !== form.password) {
    callback(new Error('Passwords do not match'));
  } else {
    callback();
  }
}

const rule = {
  username: [
    { validator: validateUsername, trigger: ['blur', 'change'] },
    { min: 4, max: 16, message: 'Length should be 4 to 16', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Please input password', trigger: 'blur' },
    { min: 6, max: 20, message: 'Length should be 6 to 20', trigger: ['blur', 'change'] }
  ],
  passwordRepeat: [
    { validator: validatePasswordRepeat, trigger: ['blur', 'change'] },
  ],
  email: [
    { required: true, message: 'Please input email', trigger: 'blur' },
    { type: 'email', message: 'Please input a valid email', trigger: ['blur', 'change'] }
  ],
  code: [
    { required: true, message: 'Please input verification code', trigger: 'blur' }
  ]
}

function askCode() {
  get(
      `/api/auth/ask-code?email=${form.email}&type=register`,
      (response) => {
        coldTime.value = 60;
        startColdTime();
        ElMessage.success(`Verification code sent to ${form.email}`);
        console.log(response);
      },
      (err) => {
        coldTime.value = 0;
        ElMessage.error('Email address is invalid or already registered');
        console.log(err);
      });
}

function register() {
  formRef.value.validate(
      (valid) => {
        if (valid) {
          post(
              '/api/auth/register',
              { ...form },
              () => {
                ElMessage.success('Registration successfully!')
                router.push('/')
              },
              (err) => {
                ElMessage.error('Please check your information and try again.')
                console.log(err)
              }
          )
        }
      }
  )
}

</script>

<template>
  <div>
    <div style="text-align: center;margin: 32px">
      <div style="margin-top: 169px; margin-bottom: 50px">
        <div style="font-size: 28px;font-weight: bold">Register</div>
        <div style="font-size: 14px;color: slategray">welcome to join us!</div>
      </div>
      <div style="margin: 32px;">
        <el-form :model="form" :rules="rule" ref="formRef">
          <el-form-item prop="username">
            <el-input v-model="form.username" maxlength="16" type="text" placeholder="Username">
              <template #prefix>
                <el-icon>
                  <User/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="form.password" maxlength="20" type="password" placeholder="Password">
              <template #prefix>
                <el-icon>
                  <Lock/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="passwordRepeat">
            <el-input v-model="form.passwordRepeat" maxlength="20" type="password" placeholder="Repeat password">
              <template #prefix>
                <el-icon>
                  <Lock/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="email">
            <el-input v-model="form.email" type="text" placeholder="Email">
              <template #prefix>
                <el-icon>
                  <Message/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="code">
            <el-row justify="space-between">
              <el-col :span="11">
                <el-input v-model="form.code" maxlength="6" type="text" placeholder="Verify code">
                  <template #prefix>
                    <el-icon>
                      <Bell/>
                    </el-icon>
                  </template>
                </el-input>
              </el-col>

              <el-col :span="10">
                <el-button @click="askCode" :disabled="coldTime" type="success" plain style="width: 100%">
                  {{ coldTime > 0 ? `Wait for ${coldTime}s` : 'Get Code' }}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </div>
      <div style="margin-top: 24px">
        <el-button @click="register" style="width: 280px" type="primary">Register</el-button>
      </div>
      <el-divider>
        <span style="font-size: 10px;color: slategray">already have a account?</span>
      </el-divider>
      <div style="margin-top: 16px">
        <el-button @click="router.push('/')" style="width: 280px" type="default">
          Sign in
        </el-button>
      </div>
    </div>

  </div>
</template>

<style scoped>

</style>