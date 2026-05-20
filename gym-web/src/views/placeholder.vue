<template>
  <div class="placeholder-page">
    <div class="placeholder-icon">🚧</div>
    <h2 class="gradient-text">{{ title }}</h2>
    <p>页面正在建设中，敬请期待...</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const title = computed(() => (route.meta.title as string) || '页面建设中')
</script>

<style scoped>
.placeholder-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  text-align: center;
  gap: 16px;
}

.placeholder-icon {
  font-size: 64px;
}

.placeholder-page h2 {
  font-size: 28px;
  font-weight: 700;
}

.placeholder-page p {
  color: var(--text-secondary);
  font-size: 16px;
}
</style>
