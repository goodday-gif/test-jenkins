<template>
  <div class="login-page">
    <!-- 动态背景 -->
    <div class="bg-effects">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
      <div class="grid-overlay"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="card-header">
        <div class="brand">
          <span class="brand-icon">⚡</span>
          <span class="brand-name gradient-text">FitZone</span>
        </div>
        <h1>欢迎回到 FitZone</h1>
        <p class="subtitle">开启你的健身之旅</p>
      </div>

      <form class="login-form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label>用户名</label>
          <div class="input-wrapper">
            <span class="input-icon">👤</span>
            <input
              v-model="form.username"
              type="text"
              class="dark-input has-icon"
              placeholder="请输入用户名"
              autocomplete="username"
            />
          </div>
        </div>

        <div class="form-group">
          <label>密码</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input
              v-model="form.password"
              type="password"
              class="dark-input has-icon"
              placeholder="请输入密码"
              autocomplete="current-password"
            />
          </div>
        </div>

        <button type="submit" class="btn-gradient login-btn" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          <span v-else>登 录</span>
        </button>
      </form>

      <div class="card-footer">
        <p>还没有账号？<router-link to="/register" class="link-accent">立即注册</router-link></p>
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
  password: ''
})

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await userStore.login({ username: form.username, password: form.password })
  } catch {
    // error handled in store
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-primary);
  position: relative;
  overflow: hidden;
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
  background: radial-gradient(circle, rgba(0, 210, 255, 0.3), transparent 70%);
  top: -100px;
  right: -100px;
  animation-duration: 8s;
}

.orb-2 {
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, rgba(123, 47, 247, 0.3), transparent 70%);
  bottom: -80px;
  left: -80px;
  animation-duration: 10s;
  animation-delay: -3s;
}

.orb-3 {
  width: 250px;
  height: 250px;
  background: radial-gradient(circle, rgba(243, 156, 18, 0.15), transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-duration: 12s;
  animation-delay: -5s;
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

/* 登录卡片 */
.login-card {
  position: relative;
  width: 440px;
  padding: 48px 40px;
  background: rgba(26, 26, 46, 0.6);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  box-shadow: 0 8px 60px rgba(0, 0, 0, 0.5);
}

.card-header {
  text-align: center;
  margin-bottom: 36px;
}

.brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 24px;
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
.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
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

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 16px;
  z-index: 1;
}

.dark-input.has-icon {
  padding-left: 46px;
}

.login-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  margin-top: 8px;
  border-radius: var(--radius-sm);
}

.login-btn:disabled {
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
  margin-top: 28px;
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
