<template>
  <div class="video-upload">
    <el-upload
      class="video-uploader"
      :action="action"
      :headers="uploadHeaders"
      :show-file-list="false"
      :on-success="handleSuccess"
      :before-upload="beforeUpload"
      accept="video/*"
    >
      <video v-if="modelValue" :src="modelValue" class="video-preview" controls />
      <div v-else class="video-placeholder">
        <el-icon class="video-uploader-icon"><VideoCamera /></el-icon>
        <span>点击上传视频</span>
      </div>
    </el-upload>
    <div v-if="modelValue" class="video-actions">
      <el-button type="danger" size="small" text @click="handleRemove">删除</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { VideoCamera } from '@element-plus/icons-vue'

const props = withDefaults(defineProps<{
  modelValue?: string
  action?: string
}>(), {
  modelValue: '',
  action: '/api/common/upload/video'
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const uploadHeaders = computed(() => ({
  Authorization: 'Bearer ' + (localStorage.getItem('admin_token') || '')
}))

function beforeUpload(file: File) {
  const isVideo = file.type.startsWith('video/')
  const isLt200M = file.size / 1024 / 1024 < 200
  if (!isVideo) {
    ElMessage.error('只能上传视频文件！')
    return false
  }
  if (!isLt200M) {
    ElMessage.error('视频大小不能超过 200MB！')
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
.video-upload {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}
.video-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 280px;
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.video-uploader :deep(.el-upload):hover {
  border-color: var(--el-color-primary);
}
.video-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #8c939d;
}
.video-uploader-icon {
  font-size: 32px;
}
.video-preview {
  width: 280px;
  height: 160px;
  object-fit: contain;
}
.video-actions {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  height: 160px;
}
</style>
