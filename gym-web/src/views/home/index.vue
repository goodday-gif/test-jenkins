<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-bg">
        <div class="hero-orb hero-orb-1"></div>
        <div class="hero-orb hero-orb-2"></div>
        <div class="grid-overlay"></div>
      </div>
      <div class="hero-content">
        <h1 class="hero-title">
          <span class="title-line">释放你的</span>
          <span class="gradient-text title-accent">无限潜能</span>
        </h1>
        <p class="hero-desc">
          专业教练团队 · 丰富课程体系 · 科学训练方案
        </p>
        <div class="hero-actions">
          <router-link to="/tutorial" class="btn-gradient hero-btn">开始探索</router-link>
          <router-link to="/class" class="btn-outline hero-btn">浏览课程</router-link>
        </div>
      </div>
    </section>

    <!-- 统计数据 -->
    <section class="stats-section">
      <div class="section-inner">
        <div class="stats-grid">
          <div class="stat-card glass-card" v-for="item in statsCards" :key="item.label">
            <div class="stat-icon">{{ item.icon }}</div>
            <div class="stat-value gradient-text">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门教程 -->
    <section class="section" v-if="hotTutorials.length">
      <div class="section-inner">
        <div class="section-header">
          <h2 class="section-title"><span class="gradient-text">热门健身教程</span></h2>
          <router-link to="/tutorial" class="view-more">查看更多 →</router-link>
        </div>
        <div class="card-grid cols-3">
          <div class="content-card glass-card" v-for="t in hotTutorials" :key="t.id" @click="$router.push(`/tutorial/${t.id}`)">
            <div class="card-cover" :style="coverStyle(t.coverImg)">
              <span class="card-badge" v-if="t.difficulty">{{ t.difficulty }}</span>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ t.title }}</h3>
              <div class="card-meta">
                <span>▶ {{ t.viewCount || 0 }} 播放</span>
                <span v-if="t.duration">⏱ {{ t.duration }}分钟</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门课程 -->
    <section class="section" v-if="hotCourses.length">
      <div class="section-inner">
        <div class="section-header">
          <h2 class="section-title"><span class="gradient-text">热门健身课程</span></h2>
          <router-link to="/class" class="view-more">查看更多 →</router-link>
        </div>
        <div class="card-grid cols-3">
          <div class="content-card glass-card" v-for="c in hotCourses" :key="c.id" @click="$router.push(`/class/${c.id}`)">
            <div class="card-cover" :style="coverStyle(c.coverImg)">
              <span class="card-badge type">{{ c.classType || '团课' }}</span>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ c.className || c.name }}</h3>
              <div class="card-meta">
                <span v-if="c.coachName">👤 {{ c.coachName }}</span>
                <span v-if="c.price != null" class="price">¥{{ c.price }}</span>
              </div>
              <div class="card-meta">
                <span v-if="c.startTime">🕐 {{ formatTime(c.startTime) }}</span>
                <span v-if="c.maxMembers">👥 {{ c.currentMembers || 0 }}/{{ c.maxMembers }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 最新公告 -->
    <section class="section" v-if="announcements.length">
      <div class="section-inner">
        <div class="section-header">
          <h2 class="section-title"><span class="gradient-text">最新公告</span></h2>
        </div>
        <div class="announcement-list">
          <div class="announcement-item glass-card" v-for="a in announcements" :key="a.id">
            <div class="ann-left">
              <span class="ann-type-tag" v-if="a.type">{{ a.type }}</span>
              <h4 class="ann-title">{{ a.title }}</h4>
            </div>
            <span class="ann-time">{{ formatDate(a.createTime || a.publishTime) }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 特色卡片 -->
    <section class="features-section">
      <div class="section-inner">
        <div class="features-grid">
          <div class="feature-card glass-card" v-for="f in features" :key="f.title">
            <div class="feature-icon">{{ f.icon }}</div>
            <h3>{{ f.title }}</h3>
            <p>{{ f.desc }}</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getHomeStats, getHotCourses, getHotTutorials, getLatestAnnouncements } from '@/api/home'

const statsCards = ref([
  { icon: '👥', value: '0', label: '活跃会员' },
  { icon: '📚', value: '0', label: '精品课程' },
  { icon: '🏆', value: '0', label: '专业教练' },
  { icon: '🎯', value: '0', label: '健身教程' }
])

const hotTutorials = ref<any[]>([])
const hotCourses = ref<any[]>([])
const announcements = ref<any[]>([])

const features = [
  { icon: '🏋️', title: '专业课程', desc: '涵盖力量、有氧、瑜伽等多种课程类型' },
  { icon: '👨‍🏫', title: '精英教练', desc: '国际认证教练团队，个性化训练方案' },
  { icon: '🎯', title: '智能训练', desc: '科学训练体系，让每一次训练更高效' },
  { icon: '🏆', title: '顶级装备', desc: '全进口健身设备，安全可靠有保障' }
]

function coverStyle(url?: string) {
  return url
    ? { backgroundImage: `url(${url})` }
    : { background: 'linear-gradient(135deg, rgba(0,210,255,0.15), rgba(123,47,247,0.15))' }
}

function formatTime(t: string) {
  if (!t) return ''
  return t.substring(5, 16).replace('T', ' ')
}

function formatDate(d: string) {
  if (!d) return ''
  return d.substring(0, 10)
}

onMounted(async () => {
  try {
    const [statsRes, tutRes, courseRes, annRes] = await Promise.allSettled([
      getHomeStats(),
      getHotTutorials(),
      getHotCourses(),
      getLatestAnnouncements()
    ])
    if (statsRes.status === 'fulfilled' && statsRes.value) {
      const s = statsRes.value
      statsCards.value[0].value = String(s.memberCount ?? 0)
      statsCards.value[1].value = String(s.classCount ?? s.courseCount ?? 0)
      statsCards.value[2].value = String(s.coachCount ?? 0)
      statsCards.value[3].value = String(s.tutorialCount ?? 0)
    }
    if (tutRes.status === 'fulfilled') {
      const tutData = tutRes.value
      hotTutorials.value = (Array.isArray(tutData) ? tutData : tutData?.records || []).slice(0, 6)
    }
    if (courseRes.status === 'fulfilled') {
      const courseData = courseRes.value
      hotCourses.value = (Array.isArray(courseData) ? courseData : courseData?.records || []).slice(0, 6)
    }
    if (annRes.status === 'fulfilled') {
      const annData = annRes.value
      announcements.value = (Array.isArray(annData) ? annData : annData?.records || []).slice(0, 5)
    }
  } catch { /* ignore */ }
})
</script>

<style scoped>
.home-page { overflow: hidden; }

/* Hero */
.hero {
  position: relative;
  min-height: calc(100vh - var(--nav-height));
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 80px 40px;
}
.hero-bg { position: absolute; inset: 0; pointer-events: none; overflow: hidden; }
.hero-orb { position: absolute; border-radius: 50%; filter: blur(100px); opacity: 0.3; animation: heroFloat 10s ease-in-out infinite; }
.hero-orb-1 { width: 500px; height: 500px; background: radial-gradient(circle, rgba(0,210,255,0.4), transparent 70%); top: 10%; right: 15%; }
.hero-orb-2 { width: 400px; height: 400px; background: radial-gradient(circle, rgba(123,47,247,0.35), transparent 70%); bottom: 10%; left: 15%; animation-delay: -5s; }
@keyframes heroFloat { 0%,100% { transform: translate(0,0); } 50% { transform: translate(40px,-30px); } }
.grid-overlay { position: absolute; inset: 0; background-image: linear-gradient(rgba(255,255,255,0.02) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.02) 1px, transparent 1px); background-size: 60px 60px; }
.hero-content { position: relative; z-index: 1; max-width: 800px; }
.hero-title { font-size: 60px; font-weight: 800; line-height: 1.2; margin-bottom: 20px; letter-spacing: -1px; }
.title-line { display: block; color: var(--text-primary); }
.title-accent { display: block; font-size: 68px; }
.hero-desc { color: var(--text-secondary); font-size: 18px; margin-bottom: 40px; letter-spacing: 2px; }
.hero-actions { display: flex; gap: 16px; justify-content: center; }
.hero-btn { padding: 14px 36px; font-size: 16px; border-radius: var(--radius-sm); text-decoration: none; font-weight: 600; }
.btn-outline { background: transparent; border: 1px solid var(--border-color); color: var(--text-primary); transition: all var(--transition-normal); display: inline-flex; align-items: center; }
.btn-outline:hover { border-color: var(--color-accent-cyan); color: var(--color-accent-cyan); box-shadow: 0 0 20px rgba(0,210,255,0.1); }

/* Section Common */
.section, .stats-section, .features-section { padding: 60px 0; }
.section-inner { max-width: 1200px; margin: 0 auto; padding: 0 40px; }
.section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 36px; }
.section-title { font-size: 28px; font-weight: 800; }
.view-more { color: var(--text-secondary); font-size: 14px; font-weight: 500; text-decoration: none; transition: color var(--transition-fast); }
.view-more:hover { color: var(--color-accent-cyan); }

/* Stats */
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; }
.stat-card { padding: 32px 24px; text-align: center; }
.stat-icon { font-size: 32px; margin-bottom: 12px; }
.stat-value { font-size: 36px; font-weight: 800; margin-bottom: 8px; }
.stat-label { color: var(--text-secondary); font-size: 14px; }

/* Card Grid */
.card-grid { display: grid; gap: 24px; }
.card-grid.cols-3 { grid-template-columns: repeat(3, 1fr); }
.content-card { cursor: pointer; overflow: hidden; transition: all var(--transition-normal); }
.content-card:hover { transform: translateY(-4px); border-color: var(--border-glow); box-shadow: 0 8px 32px rgba(0,210,255,0.1); }
.card-cover { height: 180px; background-size: cover; background-position: center; position: relative; }
.card-badge { position: absolute; top: 12px; right: 12px; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; background: rgba(0,210,255,0.2); color: var(--color-accent-cyan); backdrop-filter: blur(8px); }
.card-badge.type { background: rgba(123,47,247,0.2); color: var(--color-accent-purple); }
.card-body { padding: 18px; }
.card-title { font-size: 16px; font-weight: 700; color: var(--text-primary); margin-bottom: 10px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-meta { display: flex; align-items: center; gap: 16px; color: var(--text-muted); font-size: 13px; margin-top: 6px; }
.price { color: var(--color-gold); font-weight: 700; }

/* Announcements */
.announcement-list { display: flex; flex-direction: column; gap: 12px; }
.announcement-item { display: flex; align-items: center; justify-content: space-between; padding: 18px 24px; cursor: default; }
.ann-left { display: flex; align-items: center; gap: 14px; min-width: 0; }
.ann-type-tag { flex-shrink: 0; padding: 3px 10px; border-radius: 4px; font-size: 12px; font-weight: 600; background: rgba(0,210,255,0.12); color: var(--color-accent-cyan); }
.ann-title { font-size: 15px; font-weight: 600; color: var(--text-primary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.ann-time { flex-shrink: 0; color: var(--text-muted); font-size: 13px; }

/* Features */
.features-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; }
.feature-card { padding: 36px 28px; text-align: center; }
.feature-icon { font-size: 40px; margin-bottom: 20px; }
.feature-card h3 { font-size: 18px; font-weight: 700; color: var(--text-primary); margin-bottom: 12px; }
.feature-card p { color: var(--text-secondary); font-size: 14px; line-height: 1.7; }

@media (max-width: 768px) {
  .hero-title { font-size: 36px; }
  .title-accent { font-size: 42px; }
  .stats-grid, .features-grid { grid-template-columns: repeat(2, 1fr); }
  .card-grid.cols-3 { grid-template-columns: 1fr; }
}
</style>
