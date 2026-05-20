<template>
  <div class="orders-page">
    <h2 class="section-heading"><span class="gradient-text">我的订单</span></h2>

    <div v-if="loading" class="loading-state">加载中...</div>
    <div v-else-if="!list.length" class="empty-state">
      <div class="empty-icon">📋</div>
      <p>暂无订单记录</p>
    </div>
    <div v-else class="order-list">
      <div class="order-card glass-card" v-for="item in list" :key="item.id">
        <div class="order-header">
          <div class="order-no">
            <span class="label">订单编号</span>
            <span class="value">{{ item.orderNo || item.id }}</span>
          </div>
          <span class="pay-tag" :class="payStatusClass(item.payStatus)">{{ payStatusText(item.payStatus) }}</span>
        </div>
        <div class="order-body">
          <div class="order-info-grid">
            <div class="order-info-item">
              <span class="label">订单类型</span>
              <span class="type-tag">{{ orderTypeText(item.orderType || item.type) }}</span>
            </div>
            <div class="order-info-item">
              <span class="label">订单金额</span>
              <span class="amount">¥{{ item.amount?.toFixed(2) || '0.00' }}</span>
            </div>
            <div class="order-info-item">
              <span class="label">创建时间</span>
              <span class="time">{{ item.createTime || '-' }}</span>
            </div>
            <div class="order-info-item" v-if="item.payTime">
              <span class="label">支付时间</span>
              <span class="time">{{ item.payTime }}</span>
            </div>
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
import { getMyOrders } from '@/api/member'

const list = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)

async function loadList() {
  loading.value = true
  try {
    const res: any = await getMyOrders({ pageNum: page.value, pageSize })
    list.value = res?.records || res?.list || (Array.isArray(res) ? res : [])
    total.value = res?.total || 0
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function payStatusClass(status: any) {
  if (status === 0 || status === '未支付') return 'orange'
  if (status === 1 || status === '已支付') return 'green'
  if (status === 2 || status === '已退款') return 'gray'
  return 'orange'
}

function payStatusText(status: any) {
  if (status === 0 || status === '未支付') return '未支付'
  if (status === 1 || status === '已支付') return '已支付'
  if (status === 2 || status === '已退款') return '已退款'
  return String(status)
}

function orderTypeText(type: any) {
  if (type === 1 || type === '课程购买') return '课程购买'
  if (type === 2 || type === '会员充值') return '会员充值'
  if (type === 3 || type === '会员续费') return '会员续费'
  return type || '其他'
}

onMounted(() => loadList())
</script>

<style scoped>
.orders-page { padding: 0; }

.section-heading {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 24px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  padding: 22px 24px;
}

.order-card:hover {
  transform: none;
  border-color: var(--border-hover);
}

.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--border-color);
}

.order-no .label {
  font-size: 12px;
  color: var(--text-muted);
  margin-right: 8px;
}

.order-no .value {
  font-size: 14px;
  color: var(--text-secondary);
  font-family: 'Courier New', monospace;
}

.pay-tag {
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.pay-tag.orange {
  background: rgba(243, 156, 18, 0.12);
  color: var(--color-gold);
}

.pay-tag.green {
  background: rgba(0, 210, 100, 0.12);
  color: #00d264;
}

.pay-tag.gray {
  background: rgba(255, 255, 255, 0.06);
  color: var(--text-muted);
}

.order-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.order-info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-info-item .label {
  font-size: 12px;
  color: var(--text-muted);
}

.type-tag {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 600;
}

.amount {
  font-size: 20px;
  font-weight: 800;
  color: var(--color-accent-cyan);
}

.time {
  font-size: 13px;
  color: var(--text-secondary);
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

.page-btn:hover:not(:disabled) { border-color: var(--color-accent-cyan); color: var(--color-accent-cyan); }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.page-info { color: var(--text-secondary); font-size: 14px; }

/* Empty & Loading */
.empty-state, .loading-state { text-align: center; padding: 60px 0; color: var(--text-muted); font-size: 15px; }
.empty-icon { font-size: 48px; margin-bottom: 14px; }

@media (max-width: 640px) {
  .order-info-grid { grid-template-columns: 1fr; }
}
</style>
