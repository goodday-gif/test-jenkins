<template>
  <div class="review-section">
    <h3 class="review-heading">用户评价</h3>

    <!-- 评价统计 -->
    <div class="review-stats" v-if="stats">
      <div class="stats-rating">
        <span class="stats-score">{{ stats.avgRating ? stats.avgRating.toFixed(1) : '0.0' }}</span>
        <el-rate :model-value="stats.avgRating || 0" disabled :colors="['#f7ba2a','#f7ba2a','#f7ba2a']" />
      </div>
      <span class="stats-count">{{ stats.count || 0 }} 条评价</span>
    </div>

    <!-- 发表评价 -->
    <div class="review-form glass-inner" v-if="!hasReviewed && isLoggedIn">
      <h4 class="form-title">发表评价</h4>
      <div class="form-rating">
        <span class="form-label">评分</span>
        <el-rate v-model="form.rating" :colors="['#f7ba2a','#f7ba2a','#f7ba2a']" />
      </div>
      <el-input
        v-model="form.content"
        type="textarea"
        :rows="3"
        placeholder="分享你的体验和感受..."
        maxlength="500"
        show-word-limit
        class="review-textarea"
      />
      <button class="btn-submit" :disabled="submitting || !form.rating" @click="submitReview">
        {{ submitting ? '提交中...' : '发表评价' }}
      </button>
    </div>
    <div class="review-form glass-inner review-tip" v-else-if="hasReviewed && isLoggedIn">
      <span>✅ 你已评价过，感谢你的反馈！</span>
    </div>
    <div class="review-form glass-inner review-tip" v-else-if="!isLoggedIn">
      <span>请先<router-link to="/login" class="login-link">登录</router-link>后发表评价</span>
    </div>

    <!-- 评价列表 -->
    <div class="review-list">
      <div v-if="!reviews.length" class="review-empty">暂无评价，快来发表第一条评价吧</div>
      <div v-for="item in reviews" :key="item.id" class="review-item">
        <div class="review-avatar" :style="avatarBg(item.memberAvatar)">
          <span v-if="!item.memberAvatar">{{ (item.memberName || '匿').charAt(0) }}</span>
        </div>
        <div class="review-content">
          <div class="review-top">
            <span class="review-name">{{ item.memberName || '匿名用户' }}</span>
            <el-rate :model-value="item.rating" disabled size="small" :colors="['#f7ba2a','#f7ba2a','#f7ba2a']" />
            <span class="review-time">{{ formatTime(item.createTime) }}</span>
            <button v-if="isOwnReview(item)" class="btn-delete" @click="handleDelete(item.id)">删除</button>
          </div>
          <p class="review-text">{{ item.content }}</p>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="review-pagination" v-if="total > pageSize">
      <button class="page-btn" :disabled="pageNum <= 1" @click="pageNum--; loadReviews()">上一页</button>
      <span class="page-info">{{ pageNum }} / {{ Math.ceil(total / pageSize) }}</span>
      <button class="page-btn" :disabled="pageNum >= Math.ceil(total / pageSize)" @click="pageNum++; loadReviews()">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addReview, getReviewList, getReviewStats, checkReview, deleteReview } from '@/api/review'

const props = defineProps<{
  targetType: number
  targetId: number
}>()

const isLoggedIn = ref(!!localStorage.getItem('member_token'))
const hasReviewed = ref(false)
const stats = ref<any>(null)
const reviews = ref<any[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10
const submitting = ref(false)
const form = ref({ rating: 5, content: '' })

function avatarBg(url?: string) {
  return url
    ? { backgroundImage: `url(${url})`, backgroundSize: 'cover', backgroundPosition: 'center' }
    : {}
}

function formatTime(t?: string) {
  if (!t) return ''
  return t.substring(0, 16).replace('T', ' ')
}

function isOwnReview(item: any) {
  // 简单判断：如果当前已登录且该评价的memberName匹配
  // 后端返回的check接口更可靠，这里用删除按钮需要后端校验
  return isLoggedIn.value && hasReviewed.value && reviews.value.indexOf(item) !== -1
}

async function loadStats() {
  if (!props.targetId) return
  try {
    stats.value = await getReviewStats({ targetType: props.targetType, targetId: props.targetId })
  } catch { /* ignore */ }
}

async function loadReviews() {
  if (!props.targetId) return
  try {
    const res: any = await getReviewList({
      targetType: props.targetType,
      targetId: props.targetId,
      pageNum: pageNum.value,
      pageSize
    })
    reviews.value = res?.records || []
    total.value = res?.total || 0
  } catch { /* ignore */ }
}

async function loadCheck() {
  if (!isLoggedIn.value || !props.targetId) return
  try {
    const res = await checkReview({ targetType: props.targetType, targetId: props.targetId })
    hasReviewed.value = !!res
  } catch { /* ignore */ }
}

async function submitReview() {
  if (!form.value.rating) {
    ElMessage.warning('请选择评分')
    return
  }
  submitting.value = true
  try {
    await addReview({
      targetType: props.targetType,
      targetId: props.targetId,
      rating: form.value.rating,
      content: form.value.content
    })
    ElMessage.success('评价成功')
    form.value = { rating: 5, content: '' }
    hasReviewed.value = true
    loadStats()
    loadReviews()
  } catch { /* handled */ } finally {
    submitting.value = false
  }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定删除这条评价吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteReview(id)
    ElMessage.success('删除成功')
    hasReviewed.value = false
    loadStats()
    loadReviews()
  } catch { /* cancelled */ }
}

function loadAll() {
  if (!props.targetId) return
  isLoggedIn.value = !!localStorage.getItem('member_token')
  pageNum.value = 1
  loadStats()
  loadReviews()
  loadCheck()
}

watch(() => props.targetId, () => {
  loadAll()
})

onMounted(() => {
  loadAll()
})
</script>

<style scoped>
.review-section {
  margin-top: 36px;
  padding-top: 28px;
  border-top: 1px solid rgba(255,255,255,0.06);
}
.review-heading {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 20px;
  color: var(--text-primary);
}

/* 统计 */
.review-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: rgba(255,255,255,0.03);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.06);
}
.stats-rating {
  display: flex;
  align-items: center;
  gap: 10px;
}
.stats-score {
  font-size: 32px;
  font-weight: 800;
  background: linear-gradient(135deg, #00d2ff, #7b2ff7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.stats-count {
  color: var(--text-muted);
  font-size: 14px;
}

/* 表单 */
.glass-inner {
  background: rgba(255,255,255,0.03);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.06);
  padding: 20px;
  margin-bottom: 24px;
}
.form-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 14px;
}
.form-rating {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}
.form-label {
  font-size: 14px;
  color: var(--text-secondary);
}
.review-textarea :deep(.el-textarea__inner) {
  background: rgba(255,255,255,0.05) !important;
  border: 1px solid rgba(255,255,255,0.08) !important;
  color: rgba(255,255,255,0.9) !important;
  border-radius: 8px;
  resize: none;
}
.review-textarea :deep(.el-textarea__inner:focus) {
  border-color: rgba(0,210,255,0.4) !important;
}
.review-textarea :deep(.el-input__count) {
  background: transparent !important;
  color: var(--text-muted) !important;
}
.btn-submit {
  margin-top: 14px;
  padding: 10px 28px;
  border-radius: 8px;
  border: none;
  background: linear-gradient(135deg, #00d2ff, #7b2ff7);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  font-family: var(--font-family);
}
.btn-submit:hover:not(:disabled) {
  opacity: 0.9;
  transform: translateY(-1px);
}
.btn-submit:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
.review-tip {
  color: var(--text-secondary);
  font-size: 14px;
}
.login-link {
  background: linear-gradient(135deg, #00d2ff, #7b2ff7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 600;
  text-decoration: none;
}

/* 评价列表 */
.review-empty {
  text-align: center;
  padding: 40px 0;
  color: var(--text-muted);
  font-size: 14px;
}
.review-item {
  display: flex;
  gap: 14px;
  padding: 18px 0;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}
.review-item:last-child {
  border-bottom: none;
}
.review-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #00d2ff, #7b2ff7);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
  font-size: 16px;
  font-weight: 700;
  color: #fff;
}
.review-content {
  flex: 1;
  min-width: 0;
}
.review-top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}
.review-name {
  font-size: 14px;
  font-weight: 600;
  background: linear-gradient(135deg, #00d2ff, #7b2ff7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.review-time {
  font-size: 12px;
  color: var(--text-muted);
  margin-left: auto;
}
.review-text {
  color: rgba(255,255,255,0.8);
  font-size: 14px;
  line-height: 1.7;
  word-break: break-all;
}
.btn-delete {
  background: none;
  border: 1px solid rgba(255,71,87,0.3);
  color: #ff4757;
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 12px;
  cursor: pointer;
  font-family: var(--font-family);
  transition: all 0.2s;
}
.btn-delete:hover {
  background: rgba(255,71,87,0.12);
  border-color: rgba(255,71,87,0.5);
}

/* 分页 */
.review-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
  padding-top: 16px;
}
.review-pagination .page-btn {
  padding: 6px 18px;
  border-radius: 8px;
  border: 1px solid rgba(255,255,255,0.08);
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  font-family: var(--font-family);
  font-size: 13px;
  transition: all 0.2s;
}
.review-pagination .page-btn:hover:not(:disabled) {
  border-color: rgba(0,210,255,0.4);
  color: var(--color-accent-cyan);
}
.review-pagination .page-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}
.review-pagination .page-info {
  color: var(--text-secondary);
  font-size: 14px;
}

.review-section :deep(.el-rate) {
  --el-rate-fill-color: #f7ba2a;
}
.review-section :deep(.el-rate__icon) {
  font-size: 18px;
}
</style>
