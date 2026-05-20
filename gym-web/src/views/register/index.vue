<template>
  <div class="register-page">
    <!-- 动态背景 -->
    <div class="bg-effects">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
      <div class="grid-overlay"></div>
    </div>

    <!-- 注册卡片 -->
    <div class="register-card">
      <div class="card-header">
        <div class="brand">
          <span class="brand-icon">⚡</span>
          <span class="brand-name gradient-text">FitZone</span>
        </div>
        <h1>加入 FitZone</h1>
        <p class="subtitle">开始你的健身之旅</p>
      </div>

      <form class="register-form" @submit.prevent="handleRegister">
        <div class="form-row">
          <div class="form-group">
            <label>用户名</label>
            <input v-model="form.username" type="text" class="dark-input" placeholder="请输入用户名" autocomplete="username" />
          </div>
          <div class="form-group">
            <label>昵称</label>
            <input v-model="form.nickname" type="text" class="dark-input" placeholder="请输入昵称" />
          </div>
        </div>

        <div class="form-group">
          <label>手机号</label>
          <input v-model="form.phone" type="tel" class="dark-input" placeholder="请输入手机号" />
        </div>

        <div class="form-group">
          <label>密码</label>
          <input v-model="form.password" type="password" class="dark-input" placeholder="请输入密码" autocomplete="new-password" />
        </div>

        <div class="form-group">
          <label>确认密码</label>
          <input v-model="form.confirmPassword" type="password" class="dark-input" placeholder="请再次输入密码" autocomplete="new-password" />
        </div>

        <button type="submit" class="btn-gradient register-btn" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          <span v-else>注 册</span>
        </button>
      </form>

      <div class="card-footer">
        <p>已有账号？<router-link to="/login" class="link-accent">立即登录</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: ''
})

async function handleRegister() {
  if (!form.username || !form.password || !form.nickname || !form.phone) {
    ElMessage.warning('请填写所有字段')
    return
  }
  if (form.password !== form.confirmPassword) {
    ElMessage.warning('两次密码输入不一致')
    return
  }
  if (form.password.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    return
  }
  loading.value = true
  try {
    await userStore.register({
      username: form.username,
      password: form.password,
      nickname: form.nickname,
      phone: form.phone
    })
  } catch {
    // error handled in store
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-primary);
  position: relative;
  overflow: hidden;
  padding: 40px 20px;
}

/* 动态背景 */
.bg-effects {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  animation: float 8s ease-in-out infinite;
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(123, 47, 247, 0.3), transparent 70%);
  top: -100px;
  left: -100px;
  animation-duration: 9s;
}

.orb-2 {
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, rgba(0, 210, 255, 0.3), transparent 70%);
  bottom: -80px;
  right: -80px;
  animation-duration: 11s;
  animation-delay: -4s;
}

.orb-3 {
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(243, 156, 18, 0.12), transparent 70%);
  top: 40%;
  right: 30%;
  animation-duration: 13s;
  animation-delay: -6s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -20px) scale(1.05); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}

.grid-overlay {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255,255,255,0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.02) 1px, transparent 1px);
  background-size: 60px 60px;
}

/* 注册卡片 */
.register-card {
  position: relative;
  width: 500px;
  padding: 44px 40px;
  background: rgba(26, 26, 46, 0.6);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  box-shadow: 0 8px 60px rgba(0, 0, 0, 0.5);
}

.card-header {
  text-align: center;
  margin-bottom: 32px;
}

.brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 20px;
}

.brand-icon {
  font-size: 28px;
}

.brand-name {
  font-size: 26px;
  font-weight: 800;
}

.card-header h1 {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.subtitle {
  color: var(--text-secondary);
  font-size: 15px;
}

/* 表单 */
.register-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group label {
  display: block;
  color: var(--text-secondary);
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.register-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  margin-top: 6px;
  border-radius: var(--radius-sm);
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.spinner {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 底部 */
.card-footer {
  text-align: center;
  margin-top: 24px;
  color: var(--text-secondary);
  font-size: 14px;
}

.link-accent {
  color: var(--color-accent-cyan);
  font-weight: 600;
  text-decoration: none;
  transition: color var(--transition-fast);
}

.link-accent:hover {
  color: var(--color-accent-purple);
}
</style>
