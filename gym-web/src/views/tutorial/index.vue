<template>
  <div class="tutorial-page">
    <!-- 页头 -->
    <section class="page-hero">
      <h1 class="page-title"><span class="gradient-text">健身教程</span></h1>
      <p class="page-subtitle">专业教练精心录制，从入门到进阶，科学系统的健身指导</p>
    </section>

    <!-- 筛选栏 -->
    <section class="filter-section">
      <div class="section-inner">
        <div class="filter-bar">
          <div class="filter-tags">
            <button
              class="tag-btn" :class="{ active: !selectedCategory }"
              @click="selectedCategory = ''; loadList()"
            >全部</button>
            <button
              v-for="c in categories" :key="c.id"
              class="tag-btn" :class="{ active: selectedCategory === c.id }"
              @click="selectedCategory = c.id; loadList()"
            >{{ c.name || c.categoryName }}</button>
          </div>
          <div class="filter-right">
            <select v-model="difficulty" @change="loadList()" class="dark-select">
              <option value="">全部难度</option>
              <option value="1">入门</option>
              <option value="2">初级</option>
              <option value="3">中级</option>
              <option value="4">高级</option>
            </select>
            <div class="search-box">
              <input v-model="keyword" @keyup.enter="loadList()" class="dark-input" placeholder="搜索教程..." />
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 教程网格 -->
    <section class="content-section">
      <div class="section-inner">
        <div v-if="loading" class="loading-state">加载中...</div>
        <div v-else-if="!list.length" class="empty-state">
          <div class="empty-icon">🔍</div>
          <p>暂无相关教程</p>
        </div>
        <div v-else class="card-grid cols-4">
          <div
            class="content-card glass-card"
            v-for="t in list" :key="t.id"
            @click="$router.push(`/tutorial/${t.id}`)"
          >
            <div class="card-cover" :style="coverStyle(t.coverImg)">
              <span class="card-badge" v-if="t.difficulty">{{ difficultyText(t.difficulty) }}</span>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ t.title }}</h3>
              <div class="card-meta">
                <span>▶ {{ t.viewCount || 0 }}</span>
                <span>👍 {{ t.likeCount || 0 }}</span>
                <span v-if="t.duration">⏱ {{ t.duration }}min</span>
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
import { getTutorialList, getTutorialCategories } from '@/api/tutorial'

const categories = ref<any[]>([])
const list = ref<any[]>([])
const loading = ref(false)
const selectedCategory = ref('')
const difficulty = ref('')
const keyword = ref('')
const page = ref(1)
const pageSize = 12
const total = ref(0)

const difficultyMap: Record<number, string> = { 1: '入门', 2: '初级', 3: '中级', 4: '高级' }
function difficultyText(d: number) { return difficultyMap[d] || '' }

async function loadList() {
  loading.value = true
  try {
    const res = await getTutorialList({
      pageNum: page.value,
      pageSize,
      categoryId: selectedCategory.value || undefined,
      keyword: keyword.value || undefined,
      difficulty: difficulty.value || undefined
    })
    list.value = res?.records || res?.list || (Array.isArray(res) ? res : []) 
    total.value = res?.total || 0
  } catch { /* handled */ } finally { loading.value = false }
}

function coverStyle(url?: string) {
  return url
    ? { backgroundImage: `url(${url})` }
    : { background: 'linear-gradient(135deg, rgba(0,210,255,0.12), rgba(123,47,247,0.12))' }
}

onMounted(async () => {
  try {
    const res = await getTutorialCategories()
    categories.value = Array.isArray(res) ? res : (res?.data || [])
  } catch { /* ignore */ }
  loadList()
})
</script>

<style scoped>
.tutorial-page { min-height: 60vh; }

/* Page Hero */
.page-hero { text-align: center; padding: 60px 40px 30px; }
.page-title { font-size: 36px; font-weight: 800; margin-bottom: 12px; }
.page-subtitle { color: var(--text-secondary); font-size: 16px; }

/* Filter */
.filter-section { padding: 10px 0 20px; }
.section-inner { max-width: 1200px; margin: 0 auto; padding: 0 40px; }
.filter-bar { display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 16px; }
.filter-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.tag-btn {
  padding: 6px 18px; border-radius: 20px; border: 1px solid var(--border-color);
  background: transparent; color: var(--text-secondary); font-size: 13px; cursor: pointer;
  transition: all var(--transition-fast); font-family: var(--font-family);
}
.tag-btn:hover { border-color: var(--border-hover); color: var(--text-primary); }
.tag-btn.active { background: var(--gradient-primary); border-color: transparent; color: #fff; }
.filter-right { display: flex; gap: 12px; align-items: center; }
.dark-select {
  padding: 8px 14px; background: rgba(255,255,255,0.04); border: 1px solid var(--border-color);
  border-radius: var(--radius-sm); color: var(--text-primary); font-size: 13px; outline: none;
  font-family: var(--font-family); cursor: pointer;
}
.dark-select option { background: #1a1a2e; color: #fff; }
.search-box .dark-input { width: 200px; padding: 8px 14px; font-size: 13px; }

/* Content */
.content-section { padding: 20px 0 60px; }
.card-grid { display: grid; gap: 24px; }
.card-grid.cols-4 { grid-template-columns: repeat(4, 1fr); }
.content-card { cursor: pointer; overflow: hidden; transition: all var(--transition-normal); }
.content-card:hover { transform: translateY(-4px); border-color: var(--border-glow); box-shadow: 0 8px 32px rgba(0,210,255,0.1); }
.card-cover { height: 160px; background-size: cover; background-position: center; position: relative; }
.card-badge { position: absolute; top: 10px; right: 10px; padding: 3px 10px; border-radius: 20px; font-size: 11px; font-weight: 600; background: rgba(0,210,255,0.2); color: var(--color-accent-cyan); backdrop-filter: blur(8px); }
.card-body { padding: 16px; }
.card-title { font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-meta { display: flex; gap: 14px; color: var(--text-muted); font-size: 12px; }

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

@media (max-width: 1024px) { .card-grid.cols-4 { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) { .card-grid.cols-4 { grid-template-columns: repeat(2, 1fr); } .filter-bar { flex-direction: column; align-items: flex-start; } }
</style>
