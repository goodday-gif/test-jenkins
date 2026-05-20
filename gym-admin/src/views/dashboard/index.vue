<template>
  <div class="dashboard-container">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="item in statCards" :key="item.label">
        <div class="stat-card" :style="{ background: item.bg }">
          <div class="stat-info">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
          <el-icon :size="48" class="stat-icon"><component :is="item.icon" /></el-icon>
        </div>
      </el-col>
    </el-row>

    <!-- 会员增长 + 营收统计 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span class="card-title">会员增长趋势</span></template>
          <div ref="memberChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span class="card-title">营收统计</span></template>
          <div ref="revenueChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 课程预约排行 + 热门教程Top10 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span class="card-title">课程预约排行</span></template>
          <div ref="reserveChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span class="card-title">热门教程 Top10</span></template>
          <div ref="playChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, markRaw } from 'vue'
import { User, TrendCharts, Reading, Wallet } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getOverview, getMemberGrowth, getRevenue, getCourseReserve, getCoursePlay } from '@/api/statistics'

const memberChartRef = ref<HTMLElement>()
const revenueChartRef = ref<HTMLElement>()
const reserveChartRef = ref<HTMLElement>()
const playChartRef = ref<HTMLElement>()

let memberChart: echarts.ECharts | null = null
let revenueChart: echarts.ECharts | null = null
let reserveChart: echarts.ECharts | null = null
let playChart: echarts.ECharts | null = null

const statCards = reactive([
  { label: '总会员数', value: 0, icon: markRaw(User), bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { label: '活跃会员数', value: 0, icon: markRaw(TrendCharts), bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { label: '总课程数', value: 0, icon: markRaw(Reading), bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { label: '总营收(元)', value: 0, icon: markRaw(Wallet), bg: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
])

async function loadOverview() {
  try {
    const data: any = await getOverview()
    statCards[0].value = data.totalMembers ?? 0
    statCards[1].value = data.activeMembers ?? 0
    statCards[2].value = data.totalCourses ?? 0
    statCards[3].value = data.totalRevenue ?? 0
  } catch {}
}

async function loadMemberGrowth() {
  try {
    const data: any = await getMemberGrowth()
    const dates = (data || []).map((i: any) => i.date)
    const counts = (data || []).map((i: any) => i.count)
    memberChart?.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: dates, boundaryGap: false },
      yAxis: { type: 'value' },
      grid: { left: 50, right: 20, top: 20, bottom: 30 },
      series: [{ type: 'line', data: counts, smooth: true, areaStyle: { opacity: 0.3 }, itemStyle: { color: '#667eea' } }]
    })
  } catch {}
}

async function loadRevenue() {
  try {
    const data: any = await getRevenue()
    const dates = (data || []).map((i: any) => i.date)
    const amounts = (data || []).map((i: any) => i.amount)
    revenueChart?.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: dates },
      yAxis: { type: 'value' },
      grid: { left: 60, right: 20, top: 20, bottom: 30 },
      series: [{ type: 'bar', data: amounts, itemStyle: { color: '#43e97b', borderRadius: [4, 4, 0, 0] } }]
    })
  } catch {}
}

async function loadCourseReserve() {
  try {
    const data: any = await getCourseReserve()
    const names = (data || []).map((i: any) => i.name)
    const counts = (data || []).map((i: any) => i.count)
    reserveChart?.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: names, inverse: true },
      grid: { left: 100, right: 30, top: 10, bottom: 20 },
      series: [{ type: 'bar', data: counts, itemStyle: { color: '#4facfe', borderRadius: [0, 4, 4, 0] } }]
    })
  } catch {}
}

async function loadCoursePlay() {
  try {
    const data: any = await getCoursePlay()
    const names = (data || []).map((i: any) => i.title)
    const counts = (data || []).map((i: any) => i.playCount)
    playChart?.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: names, inverse: true },
      grid: { left: 100, right: 30, top: 10, bottom: 20 },
      series: [{ type: 'bar', data: counts, itemStyle: { color: '#f5576c', borderRadius: [0, 4, 4, 0] } }]
    })
  } catch {}
}

function handleResize() {
  memberChart?.resize()
  revenueChart?.resize()
  reserveChart?.resize()
  playChart?.resize()
}

onMounted(() => {
  if (memberChartRef.value) memberChart = echarts.init(memberChartRef.value)
  if (revenueChartRef.value) revenueChart = echarts.init(revenueChartRef.value)
  if (reserveChartRef.value) reserveChart = echarts.init(reserveChartRef.value)
  if (playChartRef.value) playChart = echarts.init(playChartRef.value)

  loadOverview()
  loadMemberGrowth()
  loadRevenue()
  loadCourseReserve()
  loadCoursePlay()

  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  memberChart?.dispose()
  revenueChart?.dispose()
  reserveChart?.dispose()
  playChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.dashboard-container { padding: 0; }
.stat-row { margin-bottom: 20px; }
.stat-card {
  border-radius: 12px;
  padding: 24px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 100px;
}
.stat-value { font-size: 28px; font-weight: 700; }
.stat-label { font-size: 14px; margin-top: 6px; opacity: 0.9; }
.stat-icon { opacity: 0.6; }
.chart-row { margin-bottom: 20px; }
.chart-box { height: 320px; }
.card-title { font-weight: 600; font-size: 15px; }
</style>
