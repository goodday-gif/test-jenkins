<template>
  <div class="tutorials-page">
    <h2 class="section-heading"><span class="gradient-text">我的教程</span></h2>

    <!-- Tab 切换 -->
    <div class="custom-tabs">
      <button
        class="tab-btn" :class="{ active: activeTab === 1 }"
        @click="activeTab = 1; page = 1; loadList()"
      >👍 我的点赞</button>
      <button
        class="tab-btn" :class="{ active: activeTab === 2 }"
        @click="activeTab = 2; page = 1; loadList()"
      >⭐ 我的收藏</button>
    </div>

    <!-- 列表 -->
    <div v-if="loading" class="loading-state">加载中...</div>
    <div v-else-if="!list.length" class="empty-state">
      <div class="empty-icon">{{ activeTab === 1 ? '👍' : '⭐' }}</div>
      <p>{{ activeTab === 1 ? '暂无点赞的教程' : '暂无收藏的教程' }}</p>
      <router-link to="/tutorial" class="empty-link">去发现教程 →</router-link>
    </div>
    <div v-else class="card-grid">
      <div
        class="content-card glass-card"
        v-for="t in list" :key="t.id"
        @click="$router.push(`/tutorial/${t.tutorialId || t.id}`)"
      >
        <div class="card-cover" :style="coverStyle(t.coverImg)">
          <span class="card-badge" v-if="t.difficulty">{{ t.difficulty }}</span>
        </div>
        <div class="card-body">
          <h3 class="card-title">{{ t.title }}</h3>
          <div class="card-meta">
            <span v-if="t.categoryName">{{ t.categoryName }}</span>
            <span v-if="t.duration">⏱ {{ t.duration }}min</span>
            <span>▶ {{ t.viewCount || 0 }}</span>
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
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMyTutorials } from '@/api/member'

const activeTab = ref(1)
const list = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = 8
const total = ref(0)

async function loadList() {
  loading.value = true
  try {
    const res: any = await getMyTutorials({ pageNum: page.value, pageSize, type: activeTab.value })
    list.value = res?.records || res?.list || (Array.isArray(res) ? res : [])
    total.value = res?.total || 0
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function coverStyle(url?: string) {
  return url
    ? { backgroundImage: `url(${url})` }
    : { background: 'linear-gradient(135deg, rgba(0,210,255,0.12), rgba(123,47,247,0.12))' }
}

onMounted(() => loadList())
</script>

<style scoped>
.tutorials-page { padding: 0; }

.section-heading {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 24px;
}

/* Tabs */
.custom-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 28px;
}

.tab-btn {
  padding: 10px 24px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-color);
  background: transparent;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: var(--font-family);
  position: relative;
}

.tab-btn:hover {
  border-color: var(--border-hover);
  color: var(--text-primary);
}

.tab-btn.active {
  background: var(--gradient-primary);
  border-color: transparent;
  color: #fff;
}

/* Card Grid */
.card-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.content-card {
  cursor: pointer;
  overflow: hidden;
  transition: all var(--transition-normal);
}

.content-card:hover {
  transform: translateY(-4px);
  border-color: var(--border-glow);
  box-shadow: 0 8px 32px rgba(0, 210, 255, 0.1);
}

.card-cover {
  height: 140px;
  background-size: cover;
  background-position: center;
  position: relative;
}

.card-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
  background: rgba(0, 210, 255, 0.2);
  color: var(--color-accent-cyan);
  backdrop-filter: blur(8px);
}

.card-body { padding: 14px 16px; }

.card-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta {
  display: flex;
  gap: 14px;
  color: var(--text-muted);
  font-size: 12px;
}

/* Pagination */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-top: 32px;
}

.page-btn {
  padding: 8px 22px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-color);
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  font-family: var(--font-family);
  transition: all var(--transition-fast);
  font-size: 13px;
}

.page-btn:hover:not(:disabled) {
  border-color: var(--color-accent-cyan);
  color: var(--color-accent-cyan);
}

.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.page-info { color: var(--text-secondary); font-size: 14px; }

/* Empty & Loading */
.empty-state, .loading-state {
  text-align: center;
  padding: 60px 0;
  color: var(--text-muted);
  font-size: 15px;
}

.empty-icon { font-size: 48px; margin-bottom: 14px; }

.empty-link {
  display: inline-block;
  margin-top: 16px;
  color: var(--color-accent-cyan);
  font-size: 14px;
  font-weight: 500;
}

@media (max-width: 640px) {
  .card-grid { grid-template-columns: 1fr; }
}
</style>
