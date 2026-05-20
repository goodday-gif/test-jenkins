<template>
  <div class="equipment-page">
    <section class="page-hero">
      <h1 class="page-title"><span class="gradient-text">健身器材</span></h1>
      <p class="page-subtitle">全进口专业健身设备，先进安全，为您的训练保驾护航</p>
    </section>

    <!-- 分类筛选 -->
    <section class="filter-section">
      <div class="section-inner">
        <div class="filter-tags">
          <button class="tag-btn" :class="{ active: !selectedCategory }" @click="selectedCategory = ''; loadList()">全部</button>
          <button
            v-for="c in categories" :key="c.id || c"
            class="tag-btn" :class="{ active: selectedCategory === (c.id || c) }"
            @click="selectedCategory = c.id || c; loadList()"
          >{{ c.name || c.categoryName || c }}</button>
        </div>
      </div>
    </section>

    <section class="content-section">
      <div class="section-inner">
        <div v-if="loading" class="loading-state">加载中...</div>
        <div v-else-if="!list.length" class="empty-state">
          <div class="empty-icon">🏋️</div>
          <p>暂无器材信息</p>
        </div>
        <div v-else class="card-grid cols-4">
          <div class="equip-card glass-card" v-for="e in list" :key="e.id" @click="showDetail(e)" style="cursor: pointer;">
            <div class="equip-cover" :style="coverStyle(e.image || e.imageUrl)"></div>
            <div class="equip-body">
              <h3 class="equip-name">{{ e.name || e.equipmentName }}</h3>
              <div class="equip-tags">
                <span class="equip-tag" v-if="e.category || e.categoryName">{{ e.category || e.categoryName }}</span>
                <span class="equip-status" :class="statusClass(e.status)">{{ e.status || '正常' }}</span>
              </div>
              <p class="equip-desc" v-if="e.description || e.function">{{ truncate(e.description || e.function, 60) }}</p>
              <div class="equip-meta" v-if="e.location || e.area">
                <span>📍 {{ e.location || e.area }}</span>
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

    <!-- 器材详情弹窗 -->
    <teleport to="body">
      <div class="detail-overlay" v-if="detailVisible" @click.self="detailVisible = false">
        <div class="detail-dialog glass-card">
          <button class="detail-close" @click="detailVisible = false">&times;</button>
          <div class="detail-cover" :style="coverStyle(currentEquip?.image || currentEquip?.imageUrl)"></div>
          <div class="detail-body">
            <h2 class="detail-title">{{ currentEquip?.name || currentEquip?.equipmentName }}</h2>
            <div class="detail-tags">
              <span class="equip-tag" v-if="currentEquip?.category || currentEquip?.categoryName">{{ currentEquip?.category || currentEquip?.categoryName }}</span>
              <span class="equip-status" :class="statusClass(currentEquip?.status)">{{ statusText(currentEquip?.status) }}</span>
            </div>
            <div class="detail-section" v-if="currentEquip?.functionDesc || currentEquip?.description || currentEquip?.function">
              <h4>功能说明</h4>
              <p>{{ currentEquip?.functionDesc || currentEquip?.description || currentEquip?.function }}</p>
            </div>
            <div class="detail-section" v-if="currentEquip?.usageMethod">
              <h4>使用方法</h4>
              <p>{{ currentEquip?.usageMethod }}</p>
            </div>
            <div class="detail-meta">
              <span v-if="currentEquip?.location || currentEquip?.area">📍 {{ currentEquip?.location || currentEquip?.area }}</span>
              <span v-if="currentEquip?.purchaseTime">📅 购入: {{ currentEquip?.purchaseTime }}</span>
              <span v-if="currentEquip?.maintainTime">🛠 上次维护: {{ currentEquip?.maintainTime }}</span>
            </div>

            <!-- 评价区域 -->
            <ReviewSection v-if="currentEquip?.id" :target-type="3" :target-id="currentEquip.id" />
          </div>
        </div>
      </div>
    </teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getEquipmentList, getEquipmentCategories } from '@/api/equipment'
import ReviewSection from '@/components/ReviewSection.vue'

const categories = ref<any[]>([])
const list = ref<any[]>([])
const loading = ref(false)
const selectedCategory = ref('')
const page = ref(1)
const pageSize = 12
const total = ref(0)
const detailVisible = ref(false)
const currentEquip = ref<any>(null)

function showDetail(e: any) {
  currentEquip.value = e
  detailVisible.value = true
}

async function loadList() {
  loading.value = true
  try {
    const res = await getEquipmentList({ pageNum: page.value, pageSize, category: selectedCategory.value || undefined })
    list.value = res?.records || res?.list || (Array.isArray(res) ? res : [])
    total.value = res?.total || 0
  } catch { /* handled */ } finally { loading.value = false }
}

function coverStyle(url?: string) {
  return url ? { backgroundImage: `url(${url})` } : { background: 'linear-gradient(135deg, rgba(0,210,255,0.1), rgba(123,47,247,0.1))' }
}

function statusClass(s?: any) {
  if (!s || s === '正常' || s === '可用' || s === 1) return 'ok'
  if (s === '维修中' || s === '维护' || s === 2) return 'warn'
  return 'off'
}

function statusText(s?: any) {
  if (s === 1 || s === '正常' || s === '可用' || !s) return '正常'
  if (s === 2 || s === '维修中' || s === '维护') return '维修中'
  if (s === 3 || s === '报废') return '报废'
  return s
}

function truncate(s: string, len: number) {
  return s && s.length > len ? s.substring(0, len) + '...' : s
}

onMounted(async () => {
  try {
    const res = await getEquipmentCategories()
    categories.value = Array.isArray(res) ? res : (res?.data || [])
  } catch { /* ignore */ }
  loadList()
})
</script>

<style scoped>
.equipment-page { min-height: 60vh; }
.page-hero { text-align: center; padding: 60px 40px 30px; }
.page-title { font-size: 36px; font-weight: 800; margin-bottom: 12px; }
.page-subtitle { color: var(--text-secondary); font-size: 16px; }

.filter-section { padding: 10px 0 20px; }
.section-inner { max-width: 1200px; margin: 0 auto; padding: 0 40px; }
.filter-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.tag-btn { padding: 6px 18px; border-radius: 20px; border: 1px solid var(--border-color); background: transparent; color: var(--text-secondary); font-size: 13px; cursor: pointer; transition: all var(--transition-fast); font-family: var(--font-family); }
.tag-btn:hover { border-color: var(--border-hover); color: var(--text-primary); }
.tag-btn.active { background: var(--gradient-primary); border-color: transparent; color: #fff; }

.content-section { padding: 20px 0 60px; }
.card-grid { display: grid; gap: 24px; }
.card-grid.cols-4 { grid-template-columns: repeat(4, 1fr); }

.equip-card { overflow: hidden; transition: all var(--transition-normal); }
.equip-card:hover { transform: translateY(-4px); border-color: var(--border-glow); box-shadow: 0 8px 32px rgba(0,210,255,0.1); }
.equip-cover { height: 160px; background-size: cover; background-position: center; }
.equip-body { padding: 16px; }
.equip-name { font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.equip-tags { display: flex; gap: 8px; margin-bottom: 8px; }
.equip-tag { padding: 2px 10px; border-radius: 12px; font-size: 11px; font-weight: 600; background: rgba(123,47,247,0.12); color: var(--color-accent-purple); }
.equip-status { padding: 2px 10px; border-radius: 12px; font-size: 11px; font-weight: 600; }
.equip-status.ok { background: rgba(0,210,255,0.12); color: var(--color-accent-cyan); }
.equip-status.warn { background: rgba(243,156,18,0.15); color: var(--color-gold); }
.equip-status.off { background: rgba(255,71,87,0.12); color: #ff4757; }
.equip-desc { color: var(--text-secondary); font-size: 13px; line-height: 1.6; margin-bottom: 8px; }
.equip-meta { color: var(--text-muted); font-size: 12px; }

.pagination { display: flex; align-items: center; justify-content: center; gap: 20px; margin-top: 40px; }
.page-btn { padding: 8px 22px; border-radius: var(--radius-sm); border: 1px solid var(--border-color); background: transparent; color: var(--text-secondary); cursor: pointer; font-family: var(--font-family); transition: all var(--transition-fast); font-size: 13px; }
.page-btn:hover:not(:disabled) { border-color: var(--color-accent-cyan); color: var(--color-accent-cyan); }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.page-info { color: var(--text-secondary); font-size: 14px; }

.empty-state, .loading-state { text-align: center; padding: 80px 0; color: var(--text-muted); font-size: 16px; }
.empty-icon { font-size: 48px; margin-bottom: 16px; }

@media (max-width: 1024px) { .card-grid.cols-4 { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) { .card-grid.cols-4 { grid-template-columns: repeat(2, 1fr); } .detail-dialog { max-width: 95vw; } }

/* Detail Dialog */
.detail-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.7); backdrop-filter: blur(4px); z-index: 1000; display: flex; align-items: center; justify-content: center; padding: 20px; }
.detail-dialog { position: relative; max-width: 600px; width: 100%; max-height: 85vh; overflow-y: auto; border-radius: var(--radius-lg); }
.detail-close { position: absolute; top: 12px; right: 16px; background: rgba(0,0,0,0.5); border: none; color: #fff; font-size: 24px; width: 36px; height: 36px; border-radius: 50%; cursor: pointer; z-index: 10; display: flex; align-items: center; justify-content: center; transition: all var(--transition-fast); }
.detail-close:hover { background: rgba(255,71,87,0.7); }
.detail-cover { height: 240px; background-size: cover; background-position: center; border-radius: var(--radius-lg) var(--radius-lg) 0 0; }
.detail-body { padding: 24px; }
.detail-title { font-size: 22px; font-weight: 800; margin-bottom: 12px; color: var(--text-primary); }
.detail-tags { display: flex; gap: 8px; margin-bottom: 20px; }
.detail-section { margin-bottom: 18px; }
.detail-section h4 { font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 8px; }
.detail-section p { color: var(--text-secondary); font-size: 14px; line-height: 1.8; }
.detail-meta { display: flex; flex-wrap: wrap; gap: 16px; color: var(--text-muted); font-size: 13px; padding-top: 12px; border-top: 1px solid var(--border-color); }
</style>
