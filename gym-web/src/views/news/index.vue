<template>
  <div class="news-page">
    <!-- 页头 -->
    <section class="page-hero">
      <h1 class="page-title"><span class="gradient-text">健身资讯</span></h1>
      <p class="page-subtitle">最新的健身动态、专业知识与健康生活方式分享</p>
    </section>

    <!-- 筛选 -->
    <section class="filter-section">
      <div class="section-inner">
        <div class="filter-tags">
          <button class="tag-btn" :class="{ active: !filterType }" @click="filterType = ''; page = 1; loadList()">全部</button>
          <button class="tag-btn" :class="{ active: filterType === '1' }" @click="filterType = '1'; page = 1; loadList()">📢 公告</button>
          <button class="tag-btn" :class="{ active: filterType === '2' }" @click="filterType = '2'; page = 1; loadList()">📰 资讯</button>
        </div>
      </div>
    </section>

    <!-- 文章列表 -->
    <section class="content-section">
      <div class="section-inner">
        <div v-if="loading" class="loading-state">加载中...</div>
        <div v-else-if="!list.length" class="empty-state">
          <div class="empty-icon">📰</div>
          <p>暂无相关资讯</p>
        </div>
        <div v-else class="news-list">
          <div
            class="news-card glass-card"
            v-for="item in list" :key="item.id"
            @click="$router.push(`/news/${item.id}`)"
          >
            <div class="news-cover" v-if="item.coverImg" :style="{ backgroundImage: `url(${item.coverImg})` }"></div>
            <div class="news-cover placeholder-cover" v-else>
              <span>{{ item.type === 1 ? '📢' : '📰' }}</span>
            </div>
            <div class="news-body">
              <div class="news-meta-top">
                <span class="news-type-tag" :class="item.type === 1 ? 'announce' : 'info'">
                  {{ item.type === 1 ? '公告' : '资讯' }}
                </span>
                <span class="news-time">{{ item.createTime || item.publishTime || '' }}</span>
              </div>
              <h3 class="news-title">{{ item.title }}</h3>
              <p class="news-summary">{{ extractSummary(item.content) }}</p>
              <div class="news-footer">
                <span class="read-more">阅读全文 →</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="total > pageSize">
          <button class="page-btn" :disabled="page <= 1" @click="page--; loadList()">上一页</button>
          <span class="page-info">{{ page }} / {{ Math.ceil(total / pageSize) }}</span>
          <button class="page-btn" :disabled="page >= Math.ceil(total / pageSize)" @click="page++; loadList()">下一页</button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAnnouncementList } from '@/api/announcement'

const list = ref<any[]>([])
const loading = ref(false)
const filterType = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)

async function loadList() {
  loading.value = true
  try {
    const res: any = await getAnnouncementList({
      pageNum: page.value,
      pageSize,
      type: filterType.value || undefined
    })
    list.value = res?.records || res?.list || (Array.isArray(res) ? res : [])
    total.value = res?.total || 0
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function extractSummary(content: string) {
  if (!content) return ''
  const text = content.replace(/<[^>]+>/g, '').replace(/&nbsp;/g, ' ')
  return text.length > 120 ? text.slice(0, 120) + '...' : text
}

onMounted(() => loadList())
</script>

<style scoped>
.news-page { min-height: 60vh; }

/* Hero */
.page-hero { text-align: center; padding: 60px 40px 30px; }
.page-title { font-size: 36px; font-weight: 800; margin-bottom: 12px; }
.page-subtitle { color: var(--text-secondary); font-size: 16px; }

/* Filter */
.filter-section { padding: 10px 0 20px; }
.section-inner { max-width: 1100px; margin: 0 auto; padding: 0 40px; }
.filter-tags { display: flex; gap: 8px; }
.tag-btn {
  padding: 8px 20px; border-radius: 20px; border: 1px solid var(--border-color);
  background: transparent; color: var(--text-secondary); font-size: 13px; cursor: pointer;
  transition: all var(--transition-fast); font-family: var(--font-family);
}
.tag-btn:hover { border-color: var(--border-hover); color: var(--text-primary); }
.tag-btn.active { background: var(--gradient-primary); border-color: transparent; color: #fff; }

/* Content */
.content-section { padding: 20px 0 60px; }

.news-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.news-card {
  display: flex;
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.news-card:hover {
  transform: translateY(-4px);
  border-color: var(--border-glow);
  box-shadow: 0 8px 32px rgba(0, 210, 255, 0.1);
}

.news-cover {
  width: 240px;
  min-height: 180px;
  background-size: cover;
  background-position: center;
  flex-shrink: 0;
}

.placeholder-cover {
  background: linear-gradient(135deg, rgba(0, 210, 255, 0.08), rgba(123, 47, 247, 0.08));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
}

.news-body {
  flex: 1;
  padding: 22px 28px;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.news-meta-top {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.news-type-tag {
  padding: 3px 12px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
}

.news-type-tag.announce {
  background: rgba(0, 210, 255, 0.1);
  color: var(--color-accent-cyan);
}

.news-type-tag.info {
  background: rgba(123, 47, 247, 0.1);
  color: var(--color-accent-purple);
}

.news-time {
  color: var(--text-muted);
  font-size: 12px;
}

.news-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.news-summary {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.7;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-footer {
  margin-top: 12px;
}

.read-more {
  color: var(--color-accent-cyan);
  font-size: 13px;
  font-weight: 600;
  transition: color var(--transition-fast);
}

/* Pagination */
.pagination { display: flex; align-items: center; justify-content: center; gap: 20px; margin-top: 40px; }
.page-btn {
  padding: 8px 22px; border-radius: var(--radius-sm); border: 1px solid var(--border-color);
  background: transparent; color: var(--text-secondary); cursor: pointer; font-family: var(--font-family);
  transition: all var(--transition-fast); font-size: 13px;
}
.page-btn:hover:not(:disabled) { border-color: var(--color-accent-cyan); color: var(--color-accent-cyan); }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.page-info { color: var(--text-secondary); font-size: 14px; }

/* Empty & Loading */
.empty-state, .loading-state { text-align: center; padding: 80px 0; color: var(--text-muted); font-size: 16px; }
.empty-icon { font-size: 48px; margin-bottom: 16px; }

@media (max-width: 768px) {
  .news-card { flex-direction: column; }
  .news-cover { width: 100%; min-height: 160px; }
}
</style>
