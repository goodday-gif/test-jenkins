<template>
  <div class="news-detail-page">
    <div class="detail-container">
      <div v-if="loading" class="loading-state">加载中...</div>
      <template v-else-if="article">
        <!-- 返回 -->
        <button class="back-btn" @click="$router.push('/news')">
          ← 返回资讯列表
        </button>

        <!-- 文章头 -->
        <div class="article-header">
          <span class="type-tag" :class="article.type === 1 ? 'announce' : 'info'">
            {{ article.type === 1 ? '公告' : '资讯' }}
          </span>
          <h1 class="article-title gradient-text">{{ article.title }}</h1>
          <div class="article-meta">
            <span v-if="article.createTime || article.publishTime">
              📅 {{ article.createTime || article.publishTime }}
            </span>
            <span v-if="article.author">✍️ {{ article.author }}</span>
          </div>
        </div>

        <!-- 文章内容 -->
        <div class="article-content glass-card">
          <div class="article-body" v-html="article.content"></div>
        </div>

        <!-- 底部返回 -->
        <div class="bottom-actions">
          <button class="btn-gradient" @click="$router.push('/news')">← 返回资讯列表</button>
        </div>
      </template>
      <div v-else class="empty-state">
        <div class="empty-icon">😕</div>
        <p>文章不存在或已被删除</p>
        <router-link to="/news" class="empty-link">返回资讯列表 →</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getAnnouncementDetail } from '@/api/announcement'

const route = useRoute()
const article = ref<any>(null)
const loading = ref(true)

onMounted(async () => {
  const id = Number(route.params.id)
  if (!id) { loading.value = false; return }
  try {
    const res: any = await getAnnouncementDetail(id)
    article.value = res
  } catch { /* handled */ } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.news-detail-page {
  min-height: 60vh;
  padding: 40px 0 80px;
}

.detail-container {
  max-width: 820px;
  margin: 0 auto;
  padding: 0 40px;
}

.back-btn {
  background: none;
  border: none;
  color: var(--text-secondary);
  font-size: 14px;
  cursor: pointer;
  padding: 8px 0;
  margin-bottom: 24px;
  transition: color var(--transition-fast);
  font-family: var(--font-family);
}

.back-btn:hover {
  color: var(--color-accent-cyan);
}

/* Header */
.article-header {
  margin-bottom: 32px;
}

.type-tag {
  display: inline-block;
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 16px;
}

.type-tag.announce {
  background: rgba(0, 210, 255, 0.1);
  color: var(--color-accent-cyan);
}

.type-tag.info {
  background: rgba(123, 47, 247, 0.1);
  color: var(--color-accent-purple);
}

.article-title {
  font-size: 32px;
  font-weight: 900;
  line-height: 1.3;
  margin-bottom: 16px;
}

.article-meta {
  display: flex;
  gap: 20px;
  color: var(--text-muted);
  font-size: 14px;
}

/* Content */
.article-content {
  padding: 36px;
  margin-bottom: 32px;
}

.article-content:hover {
  transform: none;
}

.article-body {
  color: var(--text-secondary);
  font-size: 15px;
  line-height: 1.9;
}

.article-body :deep(h1),
.article-body :deep(h2),
.article-body :deep(h3) {
  color: var(--text-primary);
  margin: 24px 0 12px;
}

.article-body :deep(p) {
  margin-bottom: 16px;
}

.article-body :deep(img) {
  max-width: 100%;
  border-radius: var(--radius-md);
  margin: 16px 0;
}

.article-body :deep(a) {
  color: var(--color-accent-cyan);
}

.article-body :deep(blockquote) {
  border-left: 3px solid var(--color-accent-cyan);
  padding-left: 16px;
  margin: 16px 0;
  color: var(--text-muted);
}

/* Bottom */
.bottom-actions {
  text-align: center;
}

/* Empty & Loading */
.loading-state { text-align: center; padding: 80px 0; color: var(--text-muted); font-size: 16px; }
.empty-state { text-align: center; padding: 80px 0; color: var(--text-muted); font-size: 16px; }
.empty-icon { font-size: 48px; margin-bottom: 16px; }
.empty-link { display: inline-block; margin-top: 16px; color: var(--color-accent-cyan); font-size: 14px; }
</style>
