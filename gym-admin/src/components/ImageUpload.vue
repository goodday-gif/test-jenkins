<template>
  <div class="image-upload">
    <el-upload
      class="avatar-uploader"
      :action="action"
      :headers="uploadHeaders"
      :show-file-list="false"
      :on-success="handleSuccess"
      :before-upload="beforeUpload"
      accept="image/*"
    >
      <img v-if="modelValue" :src="modelValue" class="avatar" />
      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
    </el-upload>
    <div v-if="modelValue" class="image-actions">
      <el-button type="danger" size="small" text @click="handleRemove">删除</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const props = withDefaults(defineProps<{
  modelValue?: string
  action?: string
}>(), {
  modelValue: '',
  action: '/api/common/upload/image'
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const uploadHeaders = computed(() => ({
  Authorization: 'Bearer ' + (localStorage.getItem('admin_token') || '')
}))

function beforeUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }
  return true
}

function handleSuccess(response: any) {
  if (response.code === 200 && response.data?.url) {
    emit('update:modelValue', response.data.url)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

function handleRemove() {
  emit('update:modelValue', '')
}
</script>

<style scoped>
.image-upload {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}
.avatar-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-uploader :deep(.el-upload):hover {
  border-color: var(--el-color-primary);
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
.avatar {
  width: 120px;
  height: 120px;
  object-fit: cover;
  display: block;
}
.image-actions {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  height: 120px;
}
</style>
