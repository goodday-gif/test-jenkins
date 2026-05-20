<template>
  <div class="coach-page">
    <section class="page-hero">
      <h1 class="page-title"><span class="gradient-text">专业教练团队</span></h1>
      <p class="page-subtitle">国际认证教练，丰富执教经验，为您量身定制训练方案</p>
    </section>

    <section class="content-section">
      <div class="section-inner">
        <div v-if="loading" class="loading-state">加载中...</div>
        <div v-else-if="!list.length" class="empty-state">
          <div class="empty-icon">👨‍🏫</div>
          <p>暂无教练信息</p>
        </div>
        <div v-else class="card-grid cols-4">
          <div class="coach-card glass-card" v-for="c in list" :key="c.id" @click="$router.push(`/coach/${c.id}`)">
            <div class="coach-avatar" :style="avatarStyle(c.avatar)">
              <span v-if="!c.avatar" class="avatar-text">{{ (c.name || 'C').charAt(0) }}</span>
            </div>
            <h3 class="coach-name">{{ c.name }}</h3>
            <div class="coach-tags" v-if="c.specialty || c.specialties">
              <span class="coach-tag" v-for="(s, i) in getTags(c.specialty || c.specialties)" :key="i">{{ s }}</span>
            </div>
            <div class="coach-meta" v-if="c.experience">
              <span>🏅 执教 {{ c.experience }} 年</span>
            </div>
            <p class="coach-intro" v-if="c.introduction || c.description">{{ truncate(c.introduction || c.description, 50) }}</p>
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
import { getCoachList } from '@/api/coach'

const list = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = 12
const total = ref(0)

async function loadList() {
  loading.value = true
  try {
    const res = await getCoachList({ pageNum: page.value, pageSize })
    list.value = res?.records || res?.list || (Array.isArray(res) ? res : [])
    total.value = res?.total || 0
  } catch { /* handled */ } finally { loading.value = false }
}

function avatarStyle(url?: string) {
  return url ? { backgroundImage: `url(${url})`, backgroundSize: 'cover', backgroundPosition: 'center' } : {}
}

function getTags(s: string | string[]) {
  if (!s) return []
  if (Array.isArray(s)) return s.slice(0, 3)
  return s.split(/[,，、]/).slice(0, 3)
}

function truncate(s: string, len: number) {
  return s && s.length > len ? s.substring(0, len) + '...' : s
}

onMounted(() => loadList())
</script>

<style scoped>
.coach-page { min-height: 60vh; }
.page-hero { text-align: center; padding: 60px 40px 30px; }
.page-title { font-size: 36px; font-weight: 800; margin-bottom: 12px; }
.page-subtitle { color: var(--text-secondary); font-size: 16px; }

.content-section { padding: 30px 0 60px; }
.section-inner { max-width: 1200px; margin: 0 auto; padding: 0 40px; }
.card-grid { display: grid; gap: 24px; }
.card-grid.cols-4 { grid-template-columns: repeat(4, 1fr); }

.coach-card { padding: 32px 24px; text-align: center; cursor: pointer; transition: all var(--transition-normal); }
.coach-card:hover { transform: translateY(-4px); border-color: var(--border-glow); box-shadow: 0 8px 32px rgba(0,210,255,0.1); }

.coach-avatar { width: 88px; height: 88px; border-radius: 50%; margin: 0 auto 16px; background: var(--gradient-primary); display: flex; align-items: center; justify-content: center; overflow: hidden; }
.avatar-text { font-size: 32px; font-weight: 800; color: #fff; }
.coach-name { font-size: 18px; font-weight: 700; color: var(--text-primary); margin-bottom: 10px; }
.coach-tags { display: flex; flex-wrap: wrap; justify-content: center; gap: 6px; margin-bottom: 10px; }
.coach-tag { padding: 3px 10px; border-radius: 12px; font-size: 11px; font-weight: 600; background: rgba(0,210,255,0.12); color: var(--color-accent-cyan); }
.coach-meta { color: var(--text-muted); font-size: 13px; margin-bottom: 8px; }
.coach-intro { color: var(--text-secondary); font-size: 13px; line-height: 1.6; }

.pagination { display: flex; align-items: center; justify-content: center; gap: 20px; margin-top: 40px; }
.page-btn { padding: 8px 22px; border-radius: var(--radius-sm); border: 1px solid var(--border-color); background: transparent; color: var(--text-secondary); cursor: pointer; font-family: var(--font-family); transition: all var(--transition-fast); font-size: 13px; }
.page-btn:hover:not(:disabled) { border-color: var(--color-accent-cyan); color: var(--color-accent-cyan); }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.page-info { color: var(--text-secondary); font-size: 14px; }

.empty-state, .loading-state { text-align: center; padding: 80px 0; color: var(--text-muted); font-size: 16px; }
.empty-icon { font-size: 48px; margin-bottom: 16px; }

@media (max-width: 1024px) { .card-grid.cols-4 { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) { .card-grid.cols-4 { grid-template-columns: repeat(2, 1fr); } }
</style>
