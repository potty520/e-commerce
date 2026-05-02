<template>
  <MainLayout>
  <div class="profile-container">
    <el-card>
      <template #header>
        <h2>个人中心</h2>
      </template>
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="userInfo.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="userInfo.phone" disabled />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :label="0">未知</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="生日">
          <el-date-picker v-model="form.birthday" type="date" placeholder="选择生日" style="width: 100%" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleUpdate">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="pwd-card">
      <template #header><h2>修改密码</h2></template>
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="pwdLoading" @click="handleChangePwd">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
  </MainLayout>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateUserInfo, changePassword } from '@/api/user'
import MainLayout from '@/components/common/MainLayout.vue'

const userStore = useUserStore()

const formRef = ref()
const pwdFormRef = ref()
const loading = ref(false)
const pwdLoading = ref(false)
const pwdForm = reactive({ oldPassword: '', newPassword: '' })
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}
const form = reactive({
  nickname: '',
  email: '',
  gender: 0,
  birthday: ''
})

const userInfo = computed(() => userStore.userInfo || {})

const handleChangePwd = async () => {
  const ok = await pwdFormRef.value.validate().catch(() => false)
  if (!ok) return
  pwdLoading.value = true
  try {
    await changePassword({ ...pwdForm })
    ElMessage.success('密码已修改，请重新登录')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    userStore.logout()
    window.location.href = '/login'
  } catch (e) {
    console.error(e)
  } finally {
    pwdLoading.value = false
  }
}

const handleUpdate = async () => {
  loading.value = true
  try {
    await updateUserInfo(form)
    ElMessage.success('修改成功')
    userStore.getUserInfoAction()
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (!userStore.userInfo) {
    await userStore.getUserInfoAction()
  }
  if (userStore.userInfo) {
    form.nickname = userStore.userInfo.nickname || ''
    form.email = userStore.userInfo.email || ''
    form.gender = userStore.userInfo.gender || 0
    form.birthday = userStore.userInfo.birthday || ''
  }
})
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
}

.pwd-card {
  margin-top: 20px;
}
</style>
