import { defineStore } from 'pinia'
import { getUserInfo, login, loginBySms, register } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    async loginAction(username, password) {
      const res = await login({ username, password })
      this.token = res.data
      localStorage.setItem('token', this.token)
      return res
    },
    async loginBySmsAction(phone, smsCode) {
      const res = await loginBySms({ phone, smsCode })
      this.token = res.data
      localStorage.setItem('token', this.token)
      return res
    },
    async registerAction(data) {
      const res = await register(data)
      this.token = res.data
      localStorage.setItem('token', this.token)
      return res
    },
    async getUserInfoAction() {
      const res = await getUserInfo()
      this.userInfo = res.data
      return res
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
    }
  }
})
