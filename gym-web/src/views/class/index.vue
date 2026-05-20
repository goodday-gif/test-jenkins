<template>
  <div class="class-page">
    <!-- 页头 -->
    <section class="page-hero">
      <h1 class="page-title"><span class="gradient-text">健身课程</span></h1>
      <p class="page-subtitle">丰富的课程选择，专业教练指导，科学系统的训练计划</p>
    </section>

    <!-- 筛选 -->
    <section class="filter-section">
      <div class="section-inner">
        <div class="filter-bar">
          <div class="filter-tags">
            <button class="tag-btn" :class="{ active: !classType }" @click="classType = ''; loadList()">全部</button>
            <button class="tag-btn" :class="{ active: classType === '团课' }" @click="classType = '团课'; loadList()">团课</button>
            <button class="tag-btn" :class="{ active: classType === '私教课' }" @click="classType = '私教课'; loadList()">私教课</button>
            <button class="tag-btn" :class="{ active: classType === '小班课' }" @click="classType = '小班课'; loadList()">小班课</button>
          </div>
          <div class="search-box">
            <input v-model="keyword" @keyup.enter="loadList()" class="dark-input" placeholder="搜索课程..." style="width:200px;padding:8px 14px;font-size:13px;" />
          </div>
        </div>
      </div>
    </section>

    <!-- 课程列表 -->
    <section class="content-section">
      <div class="section-inner">
        <div v-if="loading" class="loading-state">加载中...</div>
        <div v-else-if="!list.length" class="empty-state">
          <div class="empty-icon">📋</div>
          <p>暂无相关课程</p>
        </div>
        <div v-else class="card-grid cols-3">
          <div class="content-card glass-card" v-for="c in list" :key="c.id" @click="$router.push(`/class/${c.id}`)">
            <div class="card-cover" :style="coverStyle(c.coverImg)">
              <span class="card-badge type">{{ c.classType || '团课' }}</span>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ c.className || c.name }}</h3>
              <div class="card-meta">
                <span v-if="c.coachName">👤 {{ c.coachName }}</span>
                <span v-if="c.location || c.address">📍 {{ c.location || c.address }}</span>
              </div>
              <div class="card-meta">
                <span v-if="c.startTime">🕐 {{ formatTime(c.startTime) }}</span>
                <span class="price" v-if="c.price != null">¥{{ c.price }}</span>
              </div>
              <!-- 名额进度 -->
              <div class="capacity-bar" v-if="c.maxMembers">
                <div class="capacity-track">
                  <div class="capacity-fill" :style="{ width: capacityPct(c) + '%' }"></div>
                </div>
                <span class="capacity-text">{{ c.currentMembers || 0 }}/{{ c.maxMembers }} 人</span>
              </div>
            </div>
          </div>
        </div>

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
import { getClassList } from '@/api/gymClass'

const list = ref<any[]>([])
const loading = ref(false)
const classType = ref('')
const keyword = ref('')
const page = ref(1)
const pageSize = 12
const total = ref(0)

async function loadList() {
  loading.value = true
  try {
    const res = await getClassList({ pageNum: page.value, pageSize, classType: classType.value || undefined, keyword: keyword.value || undefined })
    list.value = res?.records || res?.list || (Array.isArray(res) ? res : [])
    total.value = res?.total || 0
  } catch { /* handled */ } finally { loading.value = false }
}

function coverStyle(url?: string) {
  return url ? { backgroundImage: `url(${url})` } : { background: 'linear-gradient(135deg, rgba(123,47,247,0.12), rgba(0,210,255,0.12))' }
}

function formatTime(t: string) { return t ? t.substring(5, 16).replace('T', ' ') : '' }
function capacityPct(c: any) { return Math.min(100, Math.round(((c.currentMembers || 0) / c.maxMembers) * 100)) }

onMounted(() => loadList())
</script>

<style scoped>
.class-page { min-height: 60vh; }
.page-hero { text-align: center; padding: 60px 40px 30px; }
.page-title { font-size: 36px; font-weight: 800; margin-bottom: 12px; }
.page-subtitle { color: var(--text-secondary); font-size: 16px; }

.filter-section { padding: 10px 0 20px; }
.section-inner { max-width: 1200px; margin: 0 auto; padding: 0 40px; }
.filter-bar { display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 16px; }
.filter-tags { display: flex; gap: 8px; }
.tag-btn { padding: 6px 18px; border-radius: 20px; border: 1px solid var(--border-color); background: transparent; color: var(--text-secondary); font-size: 13px; cursor: pointer; transition: all var(--transition-fast); font-family: var(--font-family); }
.tag-btn:hover { border-color: var(--border-hover); color: var(--text-primary); }
.tag-btn.active { background: var(--gradient-primary); border-color: transparent; color: #fff; }

.content-section { padding: 20px 0 60px; }
.card-grid { display: grid; gap: 24px; }
.card-grid.cols-3 { grid-template-columns: repeat(3, 1fr); }
.content-card { cursor: pointer; overflow: hidden; transition: all var(--transition-normal); }
.content-card:hover { transform: translateY(-4px); border-color: var(--border-glow); box-shadow: 0 8px 32px rgba(0,210,255,0.1); }
.card-cover { height: 170px; background-size: cover; background-position: center; position: relative; }
.card-badge { position: absolute; top: 10px; right: 10px; padding: 3px 10px; border-radius: 20px; font-size: 11px; font-weight: 600; backdrop-filter: blur(8px); }
.card-badge.type { background: rgba(123,47,247,0.2); color: var(--color-accent-purple); }
.card-body { padding: 16px; }
.card-title { font-size: 16px; font-weight: 700; color: var(--text-primary); margin-bottom: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-meta { display: flex; gap: 14px; color: var(--text-muted); font-size: 12px; margin-top: 6px; }
.price { color: var(--color-gold); font-weight: 700; }

.capacity-bar { margin-top: 12px; display: flex; align-items: center; gap: 10px; }
.capacity-track { flex: 1; height: 6px; background: rgba(255,255,255,0.06); border-radius: 3px; overflow: hidden; }
.capacity-fill { height: 100%; background: var(--gradient-primary); border-radius: 3px; transition: width 0.5s ease; }
.capacity-text { font-size: 11px; color: var(--text-muted); flex-shrink: 0; }

.pagination { display: flex; align-items: center; justify-content: center; gap: 20px; margin-top: 40px; }
.page-btn { padding: 8px 22px; border-radius: var(--radius-sm); border: 1px solid var(--border-color); background: transparent; color: var(--text-secondary); cursor: pointer; font-family: var(--font-family); transition: all var(--transition-fast); font-size: 13px; }
.page-btn:hover:not(:disabled) { border-color: var(--color-accent-cyan); color: var(--color-accent-cyan); }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.page-info { color: var(--text-secondary); font-size: 14px; }

.empty-state, .loading-state { text-align: center; padding: 80px 0; color: var(--text-muted); font-size: 16px; }
.empty-icon { font-size: 48px; margin-bottom: 16px; }

@media (max-width: 768px) { .card-grid.cols-3 { grid-template-columns: 1fr; } }
</style>
