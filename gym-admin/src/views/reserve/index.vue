<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="会员关键词">
          <el-input v-model="queryParams.memberKeyword" placeholder="会员名/手机号" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="课程关键词">
          <el-input v-model="queryParams.classKeyword" placeholder="课程名" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="已取消" :value="0" />
            <el-option label="已预约" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="预约ID" width="80" />
        <el-table-column prop="memberName" label="会员" width="100" />
        <el-table-column prop="memberPhone" label="手机号" width="130" />
        <el-table-column prop="className" label="课程" min-width="130" show-overflow-tooltip />
        <el-table-column prop="reserveTime" label="预约时间" width="170" />
        <el-table-column label="签到状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.checkIn === 1 ? 'success' : 'info'" size="small">{{ row.checkIn === 1 ? '已签到' : '未签到' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="reserveStatusTag(row.status).type" size="small">{{ reserveStatusTag(row.status).text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1 && row.checkIn !== 1" type="success" text size="small" @click="handleCheckIn(row)">签到核销</el-button>
            <el-button v-if="row.status === 1" type="danger" text size="small" @click="handleCancel(row)">取消预约</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReserveList, checkInReserve, cancelReserve } from '@/api/reserve'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ memberKeyword: '', classKeyword: '', status: undefined as number | undefined, page: 1, pageSize: 10 })

type TagType = 'primary' | 'success' | 'info' | 'warning' | 'danger'

function reserveStatusTag(s: number): { text: string; type: TagType } {
  const map: Record<number, { text: string; type: TagType }> = {
    0: { text: '已取消', type: 'info' }, 1: { text: '已预约', type: 'warning' }, 2: { text: '已完成', type: 'success' }
  }
  return map[s] || { text: '未知', type: 'info' }
}

async function loadData() {
  loading.value = true
  try {
    const data: any = await getReserveList(queryParams)
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadData() }
function handleReset() {
  queryParams.memberKeyword = ''; queryParams.classKeyword = ''; queryParams.status = undefined; queryParams.page = 1
  loadData()
}

async function handleCheckIn(row: any) {
  try {
    await ElMessageBox.confirm('确定要签到核销该预约吗？', '提示', { type: 'info' })
    await checkInReserve(row.id)
    ElMessage.success('签到成功')
    loadData()
  } catch {}
}

async function handleCancel(row: any) {
  try {
    await ElMessageBox.confirm('确定要取消该预约吗？', '提示', { type: 'warning' })
    await cancelReserve(row.id)
    ElMessage.success('取消成功')
    loadData()
  } catch {}
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 0; }
.search-card { margin-bottom: 16px; }
.search-card :deep(.el-card__body) { padding-bottom: 2px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
