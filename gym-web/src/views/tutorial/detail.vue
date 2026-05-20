<template>
  <div class="detail-page" v-if="detail">
    <!-- 视频/封面区 -->
    <section class="video-section">
      <div class="section-inner">
        <div class="video-wrapper" v-if="detail.videoUrl">
          <video :src="detail.videoUrl" controls class="video-player"></video>
        </div>
        <div class="video-wrapper placeholder-cover" v-else :style="coverStyle(detail.coverImg)">
          <div class="play-icon">▶</div>
        </div>
      </div>
    </section>

    <!-- 教程信息 -->
    <section class="info-section">
      <div class="section-inner">
        <div class="info-header">
          <h1 class="detail-title">{{ detail.title }}</h1>
          <div class="info-tags">
            <span class="info-tag" v-if="detail.categoryName">{{ detail.categoryName }}</span>
            <span class="info-tag accent" v-if="detail.difficulty">{{ difficultyText(detail.difficulty) }}</span>
          </div>
          <div class="info-meta">
            <span>▶ {{ detail.viewCount || 0 }} 播放</span>
            <span v-if="detail.duration">⏱ {{ detail.duration }} 分钟</span>
            <span v-if="detail.createTime">📅 {{ detail.createTime?.substring(0, 10) }}</span>
          </div>
        </div>

        <!-- 互动按钮 -->
        <div class="interact-bar">
          <button class="interact-btn" :class="{ active: detail.liked }" @click="handleLike">
            <span>👍</span> 点赞 {{ detail.likeCount || 0 }}
          </button>
          <button class="interact-btn" :class="{ active: detail.collected }" @click="handleCollect">
            <span>⭐</span> 收藏 {{ detail.collectCount || 0 }}
          </button>
        </div>

        <!-- 教程内容 -->
        <div class="content-block glass-card" v-if="detail.content">
          <h3>教程内容</h3>
          <div class="rich-content" v-html="detail.content"></div>
        </div>

        <!-- 适合人群 -->
        <div class="content-block glass-card" v-if="detail.suitablePeople">
          <h3>适合人群</h3>
          <p>{{ detail.suitablePeople }}</p>
        </div>

        <!-- 教练建议 -->
        <div class="content-block glass-card" v-if="detail.coachAdvice || detail.tips">
          <h3>教练建议</h3>
          <p>{{ detail.coachAdvice || detail.tips }}</p>
        </div>

        <!-- 评论区 -->
        <div class="comments-section">
          <h3 class="section-subtitle">评论区</h3>

          <!-- 评论输入 -->
          <div class="comment-input glass-card" v-if="isLoggedIn">
            <textarea v-model="commentContent" class="dark-input" placeholder="写下你的评论..." rows="3"></textarea>
            <button class="btn-gradient comment-submit" @click="submitComment" :disabled="!commentContent.trim()">发布评论</button>
          </div>
          <div class="login-tip glass-card" v-else>
            <p>请 <router-link to="/login">登录</router-link> 后发表评论</p>
          </div>

          <!-- 评论列表 -->
          <div class="comment-list">
            <div class="comment-item glass-card" v-for="c in comments" :key="c.id">
              <div class="comment-avatar">{{ (c.nickname || c.memberName || 'U').charAt(0) }}</div>
              <div class="comment-body">
                <div class="comment-header">
                  <span class="comment-name">{{ c.nickname || c.memberName || '匿名用户' }}</span>
                  <span class="comment-time">{{ c.createTime?.substring(0, 16) }}</span>
                </div>
                <p class="comment-text">{{ c.content }}</p>
              </div>
            </div>
            <div v-if="!comments.length" class="empty-comments">暂无评论，快来抢沙发～</div>
          </div>

          <div class="pagination" v-if="commentTotal > commentPageSize">
            <button class="page-btn" :disabled="commentPage <= 1" @click="commentPage--; loadComments()">上一页</button>
            <span class="page-info">{{ commentPage }} / {{ Math.ceil(commentTotal / commentPageSize) }}</span>
            <button class="page-btn" :disabled="commentPage >= Math.ceil(commentTotal / commentPageSize)" @click="commentPage++; loadComments()">下一页</button>
          </div>
        </div>
      </div>
    </section>
  </div>
  <div v-else class="loading-page">加载中...</div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTutorialDetail, likeTutorial, collectTutorial, commentTutorial, getTutorialComments } from '@/api/tutorial'

const route = useRoute()
const router = useRouter()
const detail = ref<any>(null)
const comments = ref<any[]>([])
const commentContent = ref('')
const commentPage = ref(1)
const commentPageSize = 10
const commentTotal = ref(0)

const isLoggedIn = computed(() => !!localStorage.getItem('member_token'))

const difficultyMap: Record<number, string> = { 1: '入门', 2: '初级', 3: '中级', 4: '高级' }
function difficultyText(d: number) { return difficultyMap[d] || '' }

function coverStyle(url?: string) {
  return url
    ? { backgroundImage: `url(${url})` }
    : { background: 'linear-gradient(135deg, rgba(0,210,255,0.15), rgba(123,47,247,0.15))' }
}

async function loadDetail() {
  const id = Number(route.params.id)
  if (!id) return
  try {
    const res = await getTutorialDetail(id)
    // 后端返回 { course: {...}, liked, collected, likeInteractId, collectInteractId }
    const course = res?.course || res
    course.liked = res?.liked || false
    course.collected = res?.collected || false
    course.likeInteractId = res?.likeInteractId || null
    course.collectInteractId = res?.collectInteractId || null
    detail.value = course
  } catch { router.push('/tutorial') }
}

async function loadComments() {
  const id = Number(route.params.id)
  try {
    const res = await getTutorialComments(id, { pageNum: commentPage.value, pageSize: commentPageSize })
    comments.value = res?.records || res?.list || (Array.isArray(res) ? res : [])
    commentTotal.value = res?.total || 0
  } catch { /* ignore */ }
}

function checkLogin() {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return false
  }
  return true
}

async function handleLike() {
  if (!checkLogin()) return
  const id = Number(route.params.id)
  try {
    const res = await likeTutorial(id)
    if (res?.active) {
      detail.value.liked = true
      detail.value.likeCount = (detail.value.likeCount || 0) + 1
      detail.value.likeInteractId = res.interactId
    } else {
      detail.value.liked = false
      detail.value.likeCount = Math.max(0, (detail.value.likeCount || 0) - 1)
    }
  } catch { /* handled */ }
}

async function handleCollect() {
  if (!checkLogin()) return
  const id = Number(route.params.id)
  try {
    const res = await collectTutorial(id)
    if (res?.active) {
      detail.value.collected = true
      detail.value.collectCount = (detail.value.collectCount || 0) + 1
      detail.value.collectInteractId = res.interactId
    } else {
      detail.value.collected = false
      detail.value.collectCount = Math.max(0, (detail.value.collectCount || 0) - 1)
    }
  } catch { /* handled */ }
}

async function submitComment() {
  if (!checkLogin() || !commentContent.value.trim()) return
  const id = Number(route.params.id)
  try {
    await commentTutorial(id, commentContent.value.trim())
    ElMessage.success('评论成功')
    commentContent.value = ''
    commentPage.value = 1
    loadComments()
  } catch { /* handled */ }
}

onMounted(() => {
  loadDetail()
  loadComments()
})
</script>

<style scoped>
.detail-page { padding-bottom: 60px; }
.loading-page { text-align: center; padding: 120px 0; color: var(--text-muted); font-size: 16px; }
.section-inner { max-width: 900px; margin: 0 auto; padding: 0 40px; }

/* Video */
.video-section { padding: 40px 0 0; }
.video-wrapper { width: 100%; aspect-ratio: 16/9; border-radius: var(--radius-lg); overflow: hidden; background: #111; }
.video-player { width: 100%; height: 100%; object-fit: contain; background: #000; }
.placeholder-cover { background-size: cover; background-position: center; display: flex; align-items: center; justify-content: center; }
.play-icon { width: 72px; height: 72px; border-radius: 50%; background: rgba(0,0,0,0.6); display: flex; align-items: center; justify-content: center; font-size: 28px; color: #fff; backdrop-filter: blur(8px); }

/* Info */
.info-section { padding: 30px 0; }
.info-header { margin-bottom: 24px; }
.detail-title { font-size: 28px; font-weight: 800; margin-bottom: 14px; line-height: 1.3; }
.info-tags { display: flex; gap: 8px; margin-bottom: 12px; }
.info-tag { padding: 4px 14px; border-radius: 20px; font-size: 12px; font-weight: 600; background: rgba(255,255,255,0.06); color: var(--text-secondary); }
.info-tag.accent { background: rgba(0,210,255,0.15); color: var(--color-accent-cyan); }
.info-meta { display: flex; gap: 20px; color: var(--text-muted); font-size: 13px; }

/* Interact */
.interact-bar { display: flex; gap: 12px; margin-bottom: 30px; }
.interact-btn {
  display: flex; align-items: center; gap: 8px; padding: 10px 22px; border-radius: var(--radius-sm);
  border: 1px solid var(--border-color); background: transparent; color: var(--text-secondary);
  cursor: pointer; font-size: 14px; transition: all var(--transition-fast); font-family: var(--font-family);
}
.interact-btn:hover { border-color: var(--border-hover); color: var(--text-primary); }
.interact-btn.active { border-color: var(--color-accent-cyan); color: var(--color-accent-cyan); background: rgba(0,210,255,0.08); }

/* Content Block */
.content-block { padding: 28px; margin-bottom: 20px; }
.content-block h3 { font-size: 18px; font-weight: 700; margin-bottom: 16px; color: var(--text-primary); }
.content-block p { color: var(--text-secondary); font-size: 15px; line-height: 1.8; }
.rich-content { color: var(--text-secondary); font-size: 15px; line-height: 1.8; }
.rich-content :deep(img) { max-width: 100%; border-radius: var(--radius-sm); }

/* Comments */
.comments-section { margin-top: 40px; }
.section-subtitle { font-size: 20px; font-weight: 700; margin-bottom: 20px; }
.comment-input { padding: 20px; margin-bottom: 20px; }
.comment-input .dark-input { width: 100%; resize: vertical; min-height: 80px; margin-bottom: 12px; }
.comment-submit { padding: 8px 24px; font-size: 14px; }
.comment-submit:disabled { opacity: 0.4; cursor: not-allowed; }
.login-tip { padding: 20px; text-align: center; color: var(--text-muted); font-size: 14px; margin-bottom: 20px; }
.login-tip a { color: var(--color-accent-cyan); }

.comment-list { display: flex; flex-direction: column; gap: 12px; }
.comment-item { display: flex; gap: 14px; padding: 18px; }
.comment-avatar { width: 40px; height: 40px; border-radius: 50%; background: var(--gradient-primary); display: flex; align-items: center; justify-content: center; font-size: 14px; font-weight: 700; color: #fff; flex-shrink: 0; }
.comment-body { flex: 1; min-width: 0; }
.comment-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.comment-name { font-size: 14px; font-weight: 600; color: var(--text-primary); }
.comment-time { font-size: 12px; color: var(--text-muted); }
.comment-text { color: var(--text-secondary); font-size: 14px; line-height: 1.7; }
.empty-comments { text-align: center; padding: 40px 0; color: var(--text-muted); font-size: 14px; }

.pagination { display: flex; align-items: center; justify-content: center; gap: 20px; margin-top: 24px; }
.page-btn { padding: 8px 22px; border-radius: var(--radius-sm); border: 1px solid var(--border-color); background: transparent; color: var(--text-secondary); cursor: pointer; font-family: var(--font-family); transition: all var(--transition-fast); font-size: 13px; }
.page-btn:hover:not(:disabled) { border-color: var(--color-accent-cyan); color: var(--color-accent-cyan); }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.page-info { color: var(--text-secondary); font-size: 14px; }
</style>
