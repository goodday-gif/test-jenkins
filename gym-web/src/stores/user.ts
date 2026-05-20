import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getMemberInfo } from '@/api/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('member_token') || '')
  const userInfo = ref<any>(null)
  const isLoggedIn = computed(() => !!token.value)

  async function login(data: { username: string; password: string }) {
    const res: any = await loginApi(data)
    token.value = res.token
    localStorage.setItem('member_token', res.token)
    ElMessage.success('登录成功')
    await getInfo()
    router.push('/')
  }

  async function register(data: { username: string; password: string; nickname: string; phone: string }) {
    await registerApi(data)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  }

  async function getInfo() {
    if (!token.value) return
    try {
      const res: any = await getMemberInfo()
      userInfo.value = res
    } catch {
      logout()
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('member_token')
    router.push('/login')
  }

  return { token, userInfo, isLoggedIn, login, register, getInfo, logout }
})
