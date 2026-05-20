<template>
  <div class="profile-page">
    <h2 class="section-heading"><span class="gradient-text">个人信息</span></h2>

    <!-- 会员信息只读 -->
    <div class="info-card glass-card">
      <h3 class="card-heading">会员信息</h3>
      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">用户名</span>
          <span class="info-value">{{ userStore.userInfo?.username || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">会员等级</span>
          <span class="info-value">
            <span class="level-tag">{{ levelLabel }}</span>
          </span>
        </div>
        <div class="info-item">
          <span class="info-label">账户余额</span>
          <span class="info-value balance">¥{{ userStore.userInfo?.balance?.toFixed(2) || '0.00' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">到期时间</span>
          <span class="info-value">{{ userStore.userInfo?.expireDate || '暂无' }}</span>
        </div>
      </div>
    </div>

    <!-- 编辑个人信息 -->
    <div class="edit-card glass-card">
      <h3 class="card-heading">编辑资料</h3>
      <form @submit.prevent="handleSave" class="profile-form">
        <!-- 头像上传 -->
        <div class="form-group avatar-group">
          <label class="form-label">头像</label>
          <div class="avatar-edit">
            <el-upload
              class="avatar-uploader"
              action="/api/common/upload/image"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :on-error="handleAvatarError"
              :before-upload="beforeAvatarUpload"
              accept="image/*"
            >
              <div class="avatar-preview-wrapper" :class="{ uploading: avatarUploading }">
                <img v-if="form.avatar" :src="form.avatar" class="avatar-preview" />
                <div v-else class="avatar-preview placeholder-avatar">
                  {{ form.nickname?.charAt(0) || 'U' }}
                </div>
                <!-- 上传中 loading -->
                <div v-if="avatarUploading" class="avatar-overlay">
                  <div class="upload-loading-spinner"></div>
                  <span>上传中...</span>
                </div>
                <!-- 悬停遮罩 -->
                <div v-else class="avatar-overlay hover-overlay">
                  <span class="overlay-icon">📷</span>
                  <span>更换头像</span>
                </div>
              </div>
            </el-upload>
            <div class="avatar-tip">点击头像更换，支持 JPG / PNG，不超过 2MB</div>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label class="form-label">昵称</label>
            <input v-model="form.nickname" class="dark-input" placeholder="请输入昵称" />
          </div>
          <div class="form-group">
            <label class="form-label">手机号</label>
            <input v-model="form.phone" class="dark-input" placeholder="请输入手机号" />
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">性别</label>
          <div class="radio-group">
            <label class="radio-item" :class="{ active: form.gender === 1 }">
              <input type="radio" :value="1" v-model="form.gender" /> 男
            </label>
            <label class="radio-item" :class="{ active: form.gender === 2 }">
              <input type="radio" :value="2" v-model="form.gender" /> 女
            </label>
            <label class="radio-item" :class="{ active: form.gender === 0 }">
              <input type="radio" :value="0" v-model="form.gender" /> 保密
            </label>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-gradient" :disabled="saving">
            {{ saving ? '保存中...' : '保存修改' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateProfile } from '@/api/member'
import { ElMessage } from 'element-plus'
import type { UploadProps } from 'element-plus'

const userStore = useUserStore()
const saving = ref(false)
const avatarUploading = ref(false)

const uploadHeaders = computed(() => ({
  Authorization: 'Bearer ' + localStorage.getItem('member_token')
}))

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(rawFile.type)) {
    ElMessage.error('只能上传 JPG/PNG/GIF/WEBP 格式的图片')
    return false
  }
  if (rawFile.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  avatarUploading.value = true
  return true
}

const handleAvatarSuccess: UploadProps['onSuccess'] = async (response: any) => {
  avatarUploading.value = false
  // 响应拦截器不处理 upload 返回（因为是 el-upload 直接请求），需要手动解析
  const url = response?.data?.url || response?.url
  if (url) {
    form.avatar = url
    // 自动保存头像到后端
    try {
      await updateProfile({ avatar: url })
      await userStore.getInfo()
      ElMessage.success('头像更新成功')
    } catch {
      ElMessage.error('头像保存失败')
    }
  } else {
    ElMessage.error('上传失败，请重试')
  }
}

const handleAvatarError = () => {
  avatarUploading.value = false
  ElMessage.error('上传失败，请重试')
}

const form = reactive({
  nickname: '',
  avatar: '',
  phone: '',
  gender: 0
})

const levelLabel = computed(() => {
  const level = userStore.userInfo?.level
  if (level === 2) return '⭐ 黄金会员'
  if (level === 3) return '💎 钻石会员'
  return '🎫 普通会员'
})

function initForm() {
  const info = userStore.userInfo
  if (info) {
    form.nickname = info.nickname || ''
    form.avatar = info.avatar || ''
    form.phone = info.phone || ''
    form.gender = info.gender ?? 0
  }
}

async function handleSave() {
  if (!form.nickname?.trim()) {
    ElMessage.warning('请输入昵称')
    return
  }
  saving.value = true
  try {
    await updateProfile({ ...form })
    ElMessage.success('保存成功')
    await userStore.getInfo()
    initForm()
  } catch { /* handled */ } finally {
    saving.value = false
  }
}

onMounted(() => {
  if (userStore.userInfo) {
    initForm()
  } else {
    userStore.getInfo().then(initForm)
  }
})
</script>

<style scoped>
.profile-page { padding: 0; }

.section-heading {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 28px;
}

.info-card, .edit-card {
  padding: 28px;
  margin-bottom: 24px;
}

.info-card:hover, .edit-card:hover {
  transform: none;
}

.card-heading {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 20px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--border-color);
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-label {
  font-size: 13px;
  color: var(--text-muted);
  font-weight: 500;
}

.info-value {
  font-size: 15px;
  color: var(--text-primary);
  font-weight: 600;
}

.info-value.balance {
  color: var(--color-accent-cyan);
  font-size: 18px;
}

.level-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 3px 12px;
  border-radius: 20px;
  font-size: 12px;
  background: rgba(0, 210, 255, 0.1);
  color: var(--color-accent-cyan);
  border: 1px solid rgba(0, 210, 255, 0.15);
}

/* Form */
.profile-form {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.form-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.avatar-edit {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.avatar-uploader :deep(.el-upload) {
  border: none;
  background: transparent;
  cursor: pointer;
}

.avatar-preview-wrapper {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  padding: 3px;
  background: var(--gradient-primary);
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.avatar-preview-wrapper:hover {
  transform: scale(1.05);
  box-shadow: 0 0 24px rgba(0, 210, 255, 0.3);
}

.avatar-preview-wrapper:hover .hover-overlay {
  opacity: 1;
}

.avatar-preview-wrapper.uploading {
  pointer-events: none;
}

.avatar-preview {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-elevated);
}

.placeholder-avatar {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
}

.avatar-overlay {
  position: absolute;
  inset: 3px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  background: rgba(0, 0, 0, 0.55);
  backdrop-filter: blur(4px);
  transition: opacity 0.3s ease;
}

.hover-overlay {
  opacity: 0;
}

.overlay-icon {
  font-size: 20px;
}

.upload-loading-spinner {
  width: 24px;
  height: 24px;
  border: 3px solid rgba(255, 255, 255, 0.2);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.avatar-tip {
  font-size: 12px;
  color: var(--text-muted);
}

/* Radio */
.radio-group {
  display: flex;
  gap: 12px;
}

.radio-item {
  padding: 8px 20px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-color);
  background: rgba(255, 255, 255, 0.03);
  color: var(--text-secondary);
  font-size: 14px;
  cursor: pointer;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  gap: 6px;
}

.radio-item input[type="radio"] {
  display: none;
}

.radio-item:hover {
  border-color: var(--border-hover);
  color: var(--text-primary);
}

.radio-item.active {
  border-color: var(--color-accent-cyan);
  background: rgba(0, 210, 255, 0.08);
  color: var(--color-accent-cyan);
}

.form-actions {
  padding-top: 8px;
}

.form-actions .btn-gradient {
  min-width: 140px;
}

@media (max-width: 768px) {
  .info-grid { grid-template-columns: 1fr; }
  .form-row { grid-template-columns: 1fr; }
  .avatar-edit { flex-direction: column; align-items: flex-start; }
}
</style>
