<template>
  <div class="page">
    <el-card class="card">
      <template #header><h2>忘记密码</h2></template>
      <el-alert
        type="info"
        :closable="false"
        show-icon
        class="top-tip"
        title="请先完成图形验证码，再获取短信验证码；验证码存于 Redis，短信内容见后端日志（生产请接短信平台）。"
      />
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" maxlength="11" /></el-form-item>
        <el-form-item label="图形验证" prop="captchaCode">
          <div class="captcha-row">
            <el-input v-model="form.captchaCode" placeholder="右图字符" maxlength="6" clearable />
            <div class="captcha-img-wrap" title="点击换一张" @click="loadCaptcha">
              <img v-if="captchaImgSrc" :src="captchaImgSrc" alt="验证码" class="captcha-img" />
              <span v-else class="captcha-placeholder">加载中</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="短信验证" prop="smsCode">
          <div class="sms-row">
            <el-input v-model="form.smsCode" maxlength="6" placeholder="6 位验证码" clearable />
            <el-button
              type="primary"
              plain
              :disabled="smsCooldown > 0 || smsSendLoading"
              :loading="smsSendLoading"
              @click="handleSendSms"
            >
              {{ smsCooldown > 0 ? `${smsCooldown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword"><el-input v-model="form.newPassword" type="password" show-password /></el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="submit">重置密码</el-button>
          <el-button @click="$router.push('/login')">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { forgotPassword, getLoginCaptcha, sendForgotSms } from '@/api/user'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const smsSendLoading = ref(false)
const smsCooldown = ref(0)
const captchaImgSrc = ref('')
const form = reactive({
  username: '',
  phone: '',
  captchaId: '',
  captchaCode: '',
  smsCode: '',
  newPassword: ''
})

const phonePattern = /^1[3-9]\d{9}$/
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: phonePattern, message: '手机号格式不正确', trigger: 'blur' }
  ],
  captchaCode: [{ required: true, message: '请输入图形验证码', trigger: 'blur' }],
  smsCode: [{ required: true, message: '请输入短信验证码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

let cooldownTimer = null
const clearCooldown = () => {
  if (cooldownTimer) {
    clearInterval(cooldownTimer)
    cooldownTimer = null
  }
}
const startCooldown = () => {
  clearCooldown()
  smsCooldown.value = 60
  cooldownTimer = setInterval(() => {
    smsCooldown.value--
    if (smsCooldown.value <= 0) clearCooldown()
  }, 1000)
}

const loadCaptcha = async () => {
  try {
    const res = await getLoginCaptcha()
    form.captchaId = res.data.captchaId
    captchaImgSrc.value = res.data.imageBase64 ? `data:image/png;base64,${res.data.imageBase64}` : ''
    form.captchaCode = ''
  } catch (e) {
    console.error(e)
  }
}

const handleSendSms = async () => {
  try {
    await formRef.value.validateField('username')
    await formRef.value.validateField('phone')
    await formRef.value.validateField('captchaCode')
  } catch {
    return
  }
  if (!form.captchaId) {
    await loadCaptcha()
    return
  }
  smsSendLoading.value = true
  try {
    await sendForgotSms({
      username: form.username.trim(),
      phone: form.phone.trim(),
      captchaId: form.captchaId,
      captchaCode: form.captchaCode.trim()
    })
    ElMessage.success('短信已发送（演示：见后端日志）')
    startCooldown()
    await loadCaptcha()
  } catch (e) {
    console.error(e)
    await loadCaptcha()
  } finally {
    smsSendLoading.value = false
  }
}

const submit = async () => {
  const ok = await formRef.value.validate().catch(() => false)
  if (!ok) return
  loading.value = true
  try {
    await forgotPassword({ ...form })
    ElMessage.success('密码已重置，请登录')
    router.push('/login')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCaptcha()
})
onUnmounted(() => clearCooldown())
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.card {
  width: 500px;
}
.top-tip {
  margin-bottom: 16px;
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
  width: 130px;
  height: 48px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  overflow: hidden;
  flex-shrink: 0;
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
.sms-row {
  display: flex;
  gap: 10px;
  width: 100%;
}
.sms-row .el-input {
  flex: 1;
}
</style>
