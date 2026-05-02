<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>登录</h2>
      </template>
      <el-tabs v-model="activeTab" class="login-tabs" @tab-change="onTabChange">
        <el-tab-pane label="密码登录" name="password">
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="88px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="pwdForm.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="pwdForm.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="pwdLoading" style="width: 100%" @click="handlePwdLogin">登录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="短信登录" name="sms">
          <el-alert
            type="info"
            :closable="false"
            show-icon
            class="sms-tip"
            title="请先输入图形验证码，再获取短信验证码；演示环境短信码在后端控制台日志中查看。"
          />
          <el-form ref="smsFormRef" :model="smsForm" :rules="smsRules" label-width="88px" class="sms-form">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="smsForm.phone" maxlength="11" placeholder="11 位手机号" clearable />
            </el-form-item>
            <el-form-item label="图形验证" prop="captchaCode">
              <div class="captcha-row">
                <el-input v-model="smsForm.captchaCode" placeholder="右图字符" clearable maxlength="6" />
                <div class="captcha-img-wrap" title="点击换一张" @click="loadCaptcha">
                  <img v-if="captchaImgSrc" :src="captchaImgSrc" alt="验证码" class="captcha-img" />
                  <span v-else class="captcha-placeholder">加载中</span>
                </div>
              </div>
            </el-form-item>
            <el-form-item label="短信验证" prop="smsCode">
              <div class="sms-code-row">
                <el-input v-model="smsForm.smsCode" maxlength="6" placeholder="6 位短信码" clearable />
                <el-button
                  type="primary"
                  plain
                  :disabled="smsCooldown > 0 || smsSendLoading"
                  :loading="smsSendLoading"
                  @click="handleSendSms"
                >
                  {{ smsCooldown > 0 ? `${smsCooldown}s 后可重发` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="smsLoginLoading" style="width: 100%" @click="handleSmsLogin">登录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <div class="login-footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
        &nbsp;|&nbsp;
        <router-link to="/forgot">忘记密码</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getLoginCaptcha, sendLoginSms } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('password')
const pwdFormRef = ref()
const smsFormRef = ref()
const pwdLoading = ref(false)
const smsLoginLoading = ref(false)
const smsSendLoading = ref(false)
const smsCooldown = ref(0)

const pwdForm = reactive({
  username: '',
  password: ''
})

const smsForm = reactive({
  phone: '',
  captchaId: '',
  captchaCode: '',
  smsCode: ''
})

const captchaImgSrc = ref('')

const phonePattern = /^1[3-9]\d{9}$/

const pwdRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const smsRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: phonePattern, message: '手机号格式不正确', trigger: 'blur' }
  ],
  captchaCode: [{ required: true, message: '请输入图形验证码', trigger: 'blur' }],
  smsCode: [
    { required: true, message: '请输入短信验证码', trigger: 'blur' },
    { min: 6, max: 6, message: '短信验证码为 6 位', trigger: 'blur' }
  ]
}

let cooldownTimer = null

const clearCooldownTimer = () => {
  if (cooldownTimer) {
    clearInterval(cooldownTimer)
    cooldownTimer = null
  }
}

const startCooldown = () => {
  clearCooldownTimer()
  smsCooldown.value = 60
  cooldownTimer = setInterval(() => {
    smsCooldown.value -= 1
    if (smsCooldown.value <= 0) {
      clearCooldownTimer()
    }
  }, 1000)
}

const loadCaptcha = async () => {
  try {
    const res = await getLoginCaptcha()
    const d = res.data
    smsForm.captchaId = d.captchaId
    captchaImgSrc.value = d.imageBase64 ? `data:image/png;base64,${d.imageBase64}` : ''
    smsForm.captchaCode = ''
  } catch (e) {
    console.error(e)
  }
}

const onTabChange = (name) => {
  if (name === 'sms' && !smsForm.captchaId) {
    loadCaptcha()
  }
}

const handlePwdLogin = async () => {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  pwdLoading.value = true
  try {
    await userStore.loginAction(pwdForm.username, pwdForm.password)
    await userStore.getUserInfoAction()
    ElMessage.success('登录成功')
    redirectAfterLogin()
  } catch (e) {
    console.error(e)
  } finally {
    pwdLoading.value = false
  }
}

const handleSendSms = async () => {
  try {
    await smsFormRef.value.validateField('phone')
    await smsFormRef.value.validateField('captchaCode')
  } catch {
    return
  }
  if (!smsForm.captchaId) {
    ElMessage.warning('请先获取图形验证码')
    await loadCaptcha()
    return
  }
  smsSendLoading.value = true
  try {
    await sendLoginSms({
      phone: smsForm.phone.trim(),
      captchaId: smsForm.captchaId,
      captchaCode: smsForm.captchaCode.trim()
    })
    ElMessage.success('短信已发送（演示：请查看后端控制台日志中的验证码）')
    startCooldown()
    await loadCaptcha()
  } catch (e) {
    console.error(e)
    await loadCaptcha()
  } finally {
    smsSendLoading.value = false
  }
}

const handleSmsLogin = async () => {
  const valid = await smsFormRef.value.validate().catch(() => false)
  if (!valid) return
  smsLoginLoading.value = true
  try {
    await userStore.loginBySmsAction(smsForm.phone.trim(), smsForm.smsCode.trim())
    await userStore.getUserInfoAction()
    ElMessage.success('登录成功')
    redirectAfterLogin()
  } catch (e) {
    console.error(e)
  } finally {
    smsLoginLoading.value = false
  }
}

const redirectAfterLogin = () => {
  if (userStore.userInfo?.role === 1) {
    router.push('/admin/orders')
  } else {
    router.push('/home')
  }
}

onMounted(() => {
  if (activeTab.value === 'sms') {
    loadCaptcha()
  }
})

onUnmounted(() => {
  clearCooldownTimer()
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 480px;
}

.login-card h2 {
  text-align: center;
  margin: 0;
}

.login-tabs :deep(.el-tabs__header) {
  margin-bottom: 16px;
}

.sms-tip {
  margin-bottom: 16px;
}

.sms-form {
  margin-top: 4px;
}

.captcha-row {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.captcha-row .el-input {
  flex: 1;
}

.captcha-img-wrap {
  flex-shrink: 0;
  width: 130px;
  height: 48px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.captcha-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-placeholder {
  font-size: 12px;
  color: #909399;
}

.sms-code-row {
  display: flex;
  gap: 10px;
  width: 100%;
}

.sms-code-row .el-input {
  flex: 1;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.login-footer a {
  color: #409eff;
}
</style>
