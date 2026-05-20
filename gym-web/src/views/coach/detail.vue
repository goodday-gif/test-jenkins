<template>
  <div class="detail-page" v-if="detail">
    <div class="section-inner">
      <!-- 教练信息头部 -->
      <div class="coach-header">
        <div class="coach-avatar-lg" :style="avatarStyle(detail.avatar)">
          <span v-if="!detail.avatar" class="avatar-text">{{ (detail.name || 'C').charAt(0) }}</span>
        </div>
        <div class="coach-info">
          <h1 class="detail-title">{{ detail.name }}</h1>
          <div class="coach-tags" v-if="detail.specialty || detail.specialties">
            <span class="coach-tag" v-for="(s, i) in getTags(detail.specialty || detail.specialties)" :key="i">{{ s }}</span>
          </div>
          <div class="info-meta">
            <span v-if="detail.experience">🏅 执教 {{ detail.experience }} 年</span>
            <span v-if="detail.phone">📞 {{ detail.phone }}</span>
          </div>
        </div>
      </div>

      <!-- 个人简介 -->
      <div class="content-block glass-card" v-if="detail.introduction || detail.description">
        <h3>个人简介</h3>
        <p>{{ detail.introduction || detail.description }}</p>
      </div>

      <!-- 资质证书 -->
      <div class="content-block glass-card" v-if="detail.certification || detail.certificates">
        <h3>资质证书</h3>
        <p>{{ detail.certification || detail.certificates }}</p>
      </div>

      <!-- 授课风格 -->
      <div class="content-block glass-card" v-if="detail.teachingStyle">
        <h3>授课风格</h3>
        <p>{{ detail.teachingStyle }}</p>
      </div>

      <!-- 该教练的课程 -->
      <div class="coach-classes" v-if="detail.classes && detail.classes.length">
        <h3 class="section-subtitle">{{ detail.name }} 的课程</h3>
        <div class="card-grid cols-3">
          <div class="content-card glass-card" v-for="c in detail.classes" :key="c.id" @click="$router.push(`/class/${c.id}`)">
            <div class="card-cover" :style="coverStyle(c.coverImg)">
              <span class="card-badge type">{{ c.classType || '团课' }}</span>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ c.className || c.name }}</h3>
              <div class="card-meta">
                <span v-if="c.startTime">🕐 {{ c.startTime?.substring(5, 16).replace('T', ' ') }}</span>
                <span v-if="c.price != null" class="price">¥{{ c.price }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 评价区域 -->
      <ReviewSection :target-type="2" :target-id="Number(route.params.id)" />
    </div>
  </div>
  <div v-else class="loading-page">加载中...</div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCoachDetail } from '@/api/coach'
import ReviewSection from '@/components/ReviewSection.vue'

const route = useRoute()
const router = useRouter()
const detail = ref<any>(null)

function avatarStyle(url?: string) {
  return url ? { backgroundImage: `url(${url})`, backgroundSize: 'cover', backgroundPosition: 'center' } : {}
}

function getTags(s: string | string[]) {
  if (!s) return []
  if (Array.isArray(s)) return s.slice(0, 5)
  return s.split(/[,，、]/).slice(0, 5)
}

function coverStyle(url?: string) {
  return url ? { backgroundImage: `url(${url})` } : { background: 'linear-gradient(135deg, rgba(123,47,247,0.12), rgba(0,210,255,0.12))' }
}

onMounted(async () => {
  const id = Number(route.params.id)
  if (!id) return
  try {
    const res = await getCoachDetail(id)
    // 后端返回 { coach: {...}, classList: [...] }，需要解包
    if (res?.coach) {
      detail.value = { ...res.coach, classes: res.classList || [] }
    } else {
      detail.value = res
    }
  } catch { router.push('/coach') }
})
</script>

<style scoped>
.detail-page { padding: 40px 0 60px; }
.loading-page { text-align: center; padding: 120px 0; color: var(--text-muted); }
.section-inner { max-width: 900px; margin: 0 auto; padding: 0 40px; }

.coach-header { display: flex; gap: 32px; align-items: center; margin-bottom: 36px; }
.coach-avatar-lg { width: 120px; height: 120px; border-radius: 50%; background: var(--gradient-primary); display: flex; align-items: center; justify-content: center; overflow: hidden; flex-shrink: 0; }
.avatar-text { font-size: 44px; font-weight: 800; color: #fff; }
.coach-info { flex: 1; }
.detail-title { font-size: 30px; font-weight: 800; margin-bottom: 12px; }
.coach-tags { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 12px; }
.coach-tag { padding: 4px 14px; border-radius: 20px; font-size: 12px; font-weight: 600; background: rgba(0,210,255,0.12); color: var(--color-accent-cyan); }
.info-meta { display: flex; gap: 20px; color: var(--text-muted); font-size: 14px; }

.content-block { padding: 28px; margin-bottom: 20px; }
.content-block h3 { font-size: 18px; font-weight: 700; margin-bottom: 16px; color: var(--text-primary); }
.content-block p { color: var(--text-secondary); font-size: 15px; line-height: 1.8; }

.coach-classes { margin-top: 36px; }
.section-subtitle { font-size: 20px; font-weight: 700; margin-bottom: 20px; }
.card-grid { display: grid; gap: 24px; }
.card-grid.cols-3 { grid-template-columns: repeat(3, 1fr); }
.content-card { cursor: pointer; overflow: hidden; transition: all var(--transition-normal); }
.content-card:hover { transform: translateY(-4px); border-color: var(--border-glow); box-shadow: 0 8px 32px rgba(0,210,255,0.1); }
.card-cover { height: 150px; background-size: cover; background-position: center; position: relative; }
.card-badge { position: absolute; top: 10px; right: 10px; padding: 3px 10px; border-radius: 20px; font-size: 11px; font-weight: 600; backdrop-filter: blur(8px); }
.card-badge.type { background: rgba(123,47,247,0.2); color: var(--color-accent-purple); }
.card-body { padding: 14px; }
.card-title { font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-meta { display: flex; gap: 14px; color: var(--text-muted); font-size: 12px; }
.price { color: var(--color-gold); font-weight: 700; }

@media (max-width: 768px) { .coach-header { flex-direction: column; text-align: center; } .card-grid.cols-3 { grid-template-columns: 1fr; } }
</style>
