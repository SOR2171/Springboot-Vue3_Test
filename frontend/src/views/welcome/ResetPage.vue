<script setup>
import {reactive, ref} from "vue";
import {Bell, Lock, Message} from "@element-plus/icons-vue";
import {get, post} from "../../net/index.js";
import {ElMessage} from "element-plus";
import router from "../../router/index.js";

const activeStep = ref(0);
const coldTime = ref(0);
const formRef = ref()
const isCounting = ref(false)
let timer = null

const form = reactive({
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
      `/api/auth/ask-code?email=${form.email}&type=reset`,
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

function resetPassword() {
  formRef.value.validate(
      (valid) => {
        if (valid) {
          post(
              '/api/auth/reset',
              { ...form },
              () => {
                ElMessage.success('Reset successfully!')
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
  <div style="text-align: center;margin: 32px">
    <div class="title">
      Reset Password
    </div>
    <div style="margin: 32px">
      <el-steps :active="activeStep" finish-status="success" align-center>
        <el-step title="Verify Email"/>
        <el-step title="Reset Password"/>
      </el-steps>
    </div>

    <div style="margin: 32px" v-if="activeStep === 0">
      <div class="description">
        Please enter your email address.
      </div>
      <el-form :model="form" :rules="rule" ref="formRef">
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

    <div style="margin: 32px" v-if="activeStep === 1">
      <div class="description">
        Please enter new password.
      </div>
      <el-form :model="form" :rules="rule" ref="formRef">
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

      </el-form>
    </div>

    <div style="margin-top: 24px" v-if="activeStep === 0">
      <el-button @click="activeStep++" :disabled="!(form.code.toString().length === 6)" style="width: 280px"
                 type="primary">
        Go Next Step
      </el-button>
    </div>
    <div style="margin-top: 24px" v-if="activeStep === 1">
      <el-button @click="resetPassword" style="width: 280px" type="primary">
        Reset
      </el-button>
    </div>
    
    <el-divider>
      <span style="font-size: 10px;color: slategray">Wanna go back?</span>
    </el-divider>
    
    <div style="margin-top: 16px" v-if="activeStep === 0">
      <el-button @click="router.push('/')" style="width: 280px" type="default">
        Sign in
      </el-button>
    </div>
    <div style="margin-top: 24px" v-if="activeStep === 1">
      <el-button @click="activeStep--" style="width: 280px" type="default">
        Go Last Step
      </el-button>
    </div>
  </div>
</template>
<
<style scoped>
.title {
  font-size: 28px;
  font-weight: bold;
  margin-top: 169px
}

.description {
  font-size: 14px;
  color: slategray;
  margin-bottom: 32px
}
</style>