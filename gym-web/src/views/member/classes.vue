<template>
  <div class="classes-page">
    <h2 class="section-heading"><span class="gradient-text">我的课程</span></h2>

    <div v-if="loading" class="loading-state">加载中...</div>
    <div v-else-if="!list.length" class="empty-state">
      <div class="empty-icon">🏋️</div>
      <p>暂无预约课程</p>
      <router-link to="/class" class="empty-link">去预约课程 →</router-link>
    </div>
    <div v-else class="reserve-list">
      <div class="reserve-card glass-card" v-for="item in list" :key="item.id">
        <div class="reserve-header">
          <h3 class="class-name">{{ item.className || item.courseName || '课程' }}</h3>
          <span class="status-tag" :class="statusClass(item.status)">{{ statusText(item.status) }}</span>
        </div>
        <div class="reserve-info">
          <div class="info-row">
            <span class="info-icon">👨‍🏫</span>
            <span>教练：{{ item.coachName || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-icon">📅</span>
            <span>时间：{{ item.classTime || item.startTime || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-icon">📍</span>
            <span>地点：{{ item.location || item.address || '-' }}</span>
          </div>
          <div class="info-row" v-if="item.checkInStatus !== undefined">
            <span class="info-icon">✅</span>
            <span>签到：<span class="checkin-tag" :class="item.checkInStatus ? 'done' : 'pending'">{{ item.checkInStatus ? '已签到' : '未签到' }}</span></span>
          </div>
        </div>
        <div class="reserve-actions" v-if="item.status === 1 || item.status === '已预约'">
          <button class="btn-cancel" @click="handleCancel(item)" :disabled="canceling === item.id">
            {{ canceling === item.id ? '取消中...' : '取消预约' }}
          </button>
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
import { getMyReserves, cancelReserve } from '@/api/gymClass'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const canceling = ref<number | null>(null)
const page = ref(1)
const pageSize = 10
const total = ref(0)

async function loadList() {
  loading.value = true
  try {
    const res: any = await getMyReserves({ pageNum: page.value, pageSize })
    list.value = res?.records || res?.list || (Array.isArray(res) ? res : [])
    total.value = res?.total || 0
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function statusClass(status: any) {
  if (status === 1 || status === '已预约') return 'blue'
  if (status === 2 || status === '已完成') return 'green'
  if (status === 0 || status === '已取消') return 'gray'
  return 'blue'
}

function statusText(status: any) {
  if (status === 1 || status === '已预约') return '已预约'
  if (status === 2 || status === '已完成') return '已完成'
  if (status === 0 || status === '已取消') return '已取消'
  return String(status)
}

async function handleCancel(item: any) {
  try {
    await ElMessageBox.confirm('确定取消该预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    canceling.value = item.id
    await cancelReserve(item.id)
    ElMessage.success('取消预约成功')
    loadList()
  } catch { /* cancelled or error */ } finally {
    canceling.value = null
  }
}

onMounted(() => loadList())
</script>

<style scoped>
.classes-page { padding: 0; }

.section-heading {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 24px;
}

.reserve-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.reserve-card {
  padding: 22px 24px;
}

.reserve-card:hover {
  transform: none;
  border-color: var(--border-hover);
}

.reserve-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.class-name {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-primary);
}

.status-tag {
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-tag.blue {
  background: rgba(0, 150, 255, 0.12);
  color: #4da6ff;
}

.status-tag.green {
  background: rgba(0, 210, 100, 0.12);
  color: #00d264;
}

.status-tag.gray {
  background: rgba(255, 255, 255, 0.06);
  color: var(--text-muted);
}

.reserve-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
  font-size: 14px;
}

.info-icon { font-size: 16px; }

.checkin-tag {
  font-weight: 600;
}

.checkin-tag.done { color: #00d264; }
.checkin-tag.pending { color: var(--color-gold); }

.reserve-actions {
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
}

.btn-cancel {
  padding: 8px 22px;
  border-radius: var(--radius-sm);
  border: 1px solid rgba(255, 71, 87, 0.3);
  background: rgba(255, 71, 87, 0.08);
  color: #ff4757;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: var(--font-family);
}

.btn-cancel:hover:not(:disabled) {
  background: rgba(255, 71, 87, 0.15);
  border-color: rgba(255, 71, 87, 0.5);
}

.btn-cancel:disabled { opacity: 0.5; cursor: not-allowed; }

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

.page-btn:hover:not(:disabled) { border-color: var(--color-accent-cyan); color: var(--color-accent-cyan); }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.page-info { color: var(--text-secondary); font-size: 14px; }

/* Empty & Loading */
.empty-state, .loading-state { text-align: center; padding: 60px 0; color: var(--text-muted); font-size: 15px; }
.empty-icon { font-size: 48px; margin-bottom: 14px; }
.empty-link { display: inline-block; margin-top: 16px; color: var(--color-accent-cyan); font-size: 14px; font-weight: 500; }

@media (max-width: 640px) {
  .reserve-info { grid-template-columns: 1fr; }
}
</style>
