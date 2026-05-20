import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getAdminInfo } from '@/api/auth'
import router from '@/router'

interface UserInfo {
  username: string
  realName: string
  avatar: string
  roleType: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('admin_token') || '')
  const userInfo = ref<UserInfo>({
    username: '',
    realName: '',
    avatar: '',
    roleType: ''
  })

  async function login(username: string, password: string) {
    const data: any = await loginApi({ username, password })
    token.value = data.token
    localStorage.setItem('admin_token', data.token)
  }

  async function getInfo() {
    const data: any = await getAdminInfo()
    userInfo.value = data
  }

  function logout() {
    token.value = ''
    userInfo.value = { username: '', realName: '', avatar: '', roleType: '' }
    localStorage.removeItem('admin_token')
    router.push('/login')
  }

  return { token, userInfo, login, getInfo, logout }
})
