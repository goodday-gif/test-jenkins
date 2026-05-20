<template>
  <div class="password-page">
    <h2 class="section-heading"><span class="gradient-text">修改密码</span></h2>

    <div class="password-card glass-card">
      <form @submit.prevent="handleSubmit" class="password-form">
        <div class="form-group">
          <label class="form-label">原密码</label>
          <div class="input-wrapper">
            <input
              :type="showOld ? 'text' : 'password'"
              v-model="form.oldPassword"
              class="dark-input"
              placeholder="请输入原密码"
            />
            <button type="button" class="toggle-btn" @click="showOld = !showOld">
              {{ showOld ? '🙈' : '👁️' }}
            </button>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">新密码</label>
          <div class="input-wrapper">
            <input
              :type="showNew ? 'text' : 'password'"
              v-model="form.newPassword"
              class="dark-input"
              placeholder="请输入新密码"
            />
            <button type="button" class="toggle-btn" @click="showNew = !showNew">
              {{ showNew ? '🙈' : '👁️' }}
            </button>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">确认新密码</label>
          <div class="input-wrapper">
            <input
              :type="showConfirm ? 'text' : 'password'"
              v-model="form.confirmPassword"
              class="dark-input"
              placeholder="请再次输入新密码"
            />
            <button type="button" class="toggle-btn" @click="showConfirm = !showConfirm">
              {{ showConfirm ? '🙈' : '👁️' }}
            </button>
          </div>
        </div>

        <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>

        <div class="form-actions">
          <button type="submit" class="btn-gradient" :disabled="submitting">
            {{ submitting ? '提交中...' : '确认修改' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { updatePassword } from '@/api/member'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const submitting = ref(false)
const errorMsg = ref('')
const showOld = ref(false)
const showNew = ref(false)
const showConfirm = ref(false)

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

async function handleSubmit() {
  errorMsg.value = ''
  if (!form.oldPassword) {
    errorMsg.value = '请输入原密码'
    return
  }
  if (!form.newPassword) {
    errorMsg.value = '请输入新密码'
    return
  }
  if (form.newPassword.length < 6) {
    errorMsg.value = '新密码至少6个字符'
    return
  }
  if (form.newPassword !== form.confirmPassword) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }

  submitting.value = true
  try {
    await updatePassword({
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    setTimeout(() => {
      userStore.logout()
    }, 1500)
  } catch { /* handled */ } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.password-page { padding: 0; }

.section-heading {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 24px;
}

.password-card {
  padding: 32px;
  max-width: 480px;
}

.password-card:hover {
  transform: none;
}

.password-form {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
}

.input-wrapper {
  position: relative;
}

.input-wrapper .dark-input {
  padding-right: 44px;
}

.toggle-btn {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  padding: 4px;
  opacity: 0.6;
  transition: opacity var(--transition-fast);
}

.toggle-btn:hover {
  opacity: 1;
}

.error-msg {
  color: #ff4757;
  font-size: 13px;
  font-weight: 500;
  padding: 10px 14px;
  background: rgba(255, 71, 87, 0.08);
  border-radius: var(--radius-sm);
  border: 1px solid rgba(255, 71, 87, 0.15);
}

.form-actions {
  padding-top: 8px;
}

.form-actions .btn-gradient {
  min-width: 140px;
}
</style>
