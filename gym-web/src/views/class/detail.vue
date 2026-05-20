<template>
  <div class="detail-page" v-if="detail">
    <div class="section-inner">
      <!-- 课程头部 -->
      <div class="detail-header">
        <div class="cover-area" :style="coverStyle(detail.coverImg)">
          <span class="card-badge type">{{ detail.classType || '团课' }}</span>
        </div>
        <div class="info-area">
          <h1 class="detail-title">{{ detail.className || detail.name }}</h1>
          <div class="info-tags">
            <span class="info-tag" v-if="detail.classType">{{ detail.classType }}</span>
            <span class="info-tag accent" v-if="detail.status">{{ detail.status }}</span>
          </div>
          <div class="info-list">
            <div class="info-row" v-if="detail.coachName"><span class="label">授课教练</span><span>{{ detail.coachName }}</span></div>
            <div class="info-row" v-if="detail.startTime"><span class="label">上课时间</span><span>{{ detail.startTime?.substring(0, 16).replace('T', ' ') }}</span></div>
            <div class="info-row" v-if="detail.endTime"><span class="label">结束时间</span><span>{{ detail.endTime?.substring(0, 16).replace('T', ' ') }}</span></div>
            <div class="info-row" v-if="detail.location || detail.address"><span class="label">上课地点</span><span>{{ detail.location || detail.address }}</span></div>
            <div class="info-row" v-if="detail.price != null"><span class="label">课程价格</span><span class="price">¥{{ detail.price }}</span></div>
          </div>

          <!-- 名额进度 -->
          <div class="capacity-bar" v-if="detail.maxMembers">
            <div class="capacity-track">
              <div class="capacity-fill" :style="{ width: capacityPct + '%' }"></div>
            </div>
            <span class="capacity-text">{{ detail.currentMembers || 0 }} / {{ detail.maxMembers }} 人已预约</span>
          </div>

          <!-- 预约按钮 -->
          <div class="action-area">
            <button v-if="myReserveId" class="btn-cancel" @click="handleCancel">取消预约</button>
            <button v-else-if="isFull" class="btn-disabled" disabled>名额已满</button>
            <button v-else class="btn-gradient" @click="handleReserve">立即预约</button>
          </div>
        </div>
      </div>

      <!-- 课程描述 -->
      <div class="content-block glass-card" v-if="detail.description || detail.content">
        <h3>课程介绍</h3>
        <div class="rich-content" v-html="detail.description || detail.content"></div>
      </div>

      <!-- 评价区域 -->
      <ReviewSection :target-type="1" :target-id="Number(route.params.id)" />
    </div>
  </div>
  <div v-else class="loading-page">加载中...</div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getClassDetail, reserveClass, cancelReserve, getMyReserves } from '@/api/gymClass'
import ReviewSection from '@/components/ReviewSection.vue'

const route = useRoute()
const router = useRouter()
const detail = ref<any>(null)
const myReserveId = ref<number | null>(null)

const capacityPct = computed(() => {
  if (!detail.value?.maxMembers) return 0
  return Math.min(100, Math.round(((detail.value.currentMembers || 0) / detail.value.maxMembers) * 100))
})

const isFull = computed(() => {
  if (!detail.value?.maxMembers) return false
  return (detail.value.currentMembers || 0) >= detail.value.maxMembers
})

function coverStyle(url?: string) {
  return url ? { backgroundImage: `url(${url})` } : { background: 'linear-gradient(135deg, rgba(123,47,247,0.15), rgba(0,210,255,0.15))' }
}

async function handleReserve() {
  if (!localStorage.getItem('member_token')) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await reserveClass(Number(route.params.id))
    ElMessage.success('预约成功')
    loadDetail()
    checkMyReserve()
  } catch { /* handled */ }
}

async function handleCancel() {
  if (!myReserveId.value) return
  try {
    await ElMessageBox.confirm('确定取消该预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelReserve(myReserveId.value)
    ElMessage.success('取消预约成功')
    myReserveId.value = null
    loadDetail()
  } catch { /* cancelled or error */ }
}

async function checkMyReserve() {
  if (!localStorage.getItem('member_token')) return
  try {
    const classId = Number(route.params.id)
    const res: any = await getMyReserves({ pageNum: 1, pageSize: 100 })
    const list = res?.records || res?.list || (Array.isArray(res) ? res : [])
    const found = list.find((r: any) => r.classId === classId && r.status === 1)
    myReserveId.value = found ? found.id : null
  } catch { /* ignore */ }
}

async function loadDetail() {
  const id = Number(route.params.id)
  if (!id) return
  try {
    const res = await getClassDetail(id)
    detail.value = res
  } catch { router.push('/class') }
}

onMounted(() => {
  loadDetail()
  checkMyReserve()
})
</script>

<style scoped>
.detail-page { padding: 40px 0 60px; }
.loading-page { text-align: center; padding: 120px 0; color: var(--text-muted); }
.section-inner { max-width: 1000px; margin: 0 auto; padding: 0 40px; }

.detail-header { display: flex; gap: 36px; margin-bottom: 36px; }
.cover-area { width: 420px; min-height: 280px; border-radius: var(--radius-lg); background-size: cover; background-position: center; position: relative; flex-shrink: 0; }
.card-badge { position: absolute; top: 12px; right: 12px; padding: 4px 14px; border-radius: 20px; font-size: 12px; font-weight: 600; backdrop-filter: blur(8px); }
.card-badge.type { background: rgba(123,47,247,0.2); color: var(--color-accent-purple); }

.info-area { flex: 1; display: flex; flex-direction: column; }
.detail-title { font-size: 28px; font-weight: 800; margin-bottom: 14px; }
.info-tags { display: flex; gap: 8px; margin-bottom: 16px; }
.info-tag { padding: 4px 14px; border-radius: 20px; font-size: 12px; font-weight: 600; background: rgba(255,255,255,0.06); color: var(--text-secondary); }
.info-tag.accent { background: rgba(0,210,255,0.15); color: var(--color-accent-cyan); }

.info-list { display: flex; flex-direction: column; gap: 10px; margin-bottom: 20px; }
.info-row { display: flex; gap: 12px; font-size: 14px; }
.info-row .label { color: var(--text-muted); min-width: 80px; }
.info-row span:last-child { color: var(--text-primary); }
.price { color: var(--color-gold) !important; font-weight: 700; font-size: 18px; }

.capacity-bar { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }
.capacity-track { flex: 1; height: 8px; background: rgba(255,255,255,0.06); border-radius: 4px; overflow: hidden; }
.capacity-fill { height: 100%; background: var(--gradient-primary); border-radius: 4px; transition: width 0.5s ease; }
.capacity-text { font-size: 13px; color: var(--text-secondary); flex-shrink: 0; }

.action-area { margin-top: auto; }
.btn-disabled { padding: 12px 32px; border-radius: var(--radius-sm); background: rgba(255,255,255,0.06); color: var(--text-muted); border: none; font-size: 15px; cursor: not-allowed; }
.btn-cancel { padding: 12px 32px; border-radius: var(--radius-sm); border: 1px solid rgba(255,71,87,0.3); background: rgba(255,71,87,0.08); color: #ff4757; font-size: 15px; font-weight: 500; cursor: pointer; transition: all 0.2s; }
.btn-cancel:hover { background: rgba(255,71,87,0.15); border-color: rgba(255,71,87,0.5); }

.content-block { padding: 28px; margin-bottom: 20px; }
.content-block h3 { font-size: 18px; font-weight: 700; margin-bottom: 16px; color: var(--text-primary); }
.rich-content { color: var(--text-secondary); font-size: 15px; line-height: 1.8; }

@media (max-width: 768px) { .detail-header { flex-direction: column; } .cover-area { width: 100%; min-height: 200px; } }
</style>
