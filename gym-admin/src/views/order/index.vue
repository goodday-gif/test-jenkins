<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="会员ID">
          <el-input v-model="queryParams.memberId" placeholder="会员ID" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="订单类型">
          <el-select v-model="queryParams.orderType" placeholder="全部" clearable style="width: 140px">
            <el-option label="课程购买" :value="1" />
            <el-option label="会员充值" :value="2" />
            <el-option label="会员续费" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付状态">
          <el-select v-model="queryParams.payStatus" placeholder="全部" clearable style="width: 120px">
            <el-option label="未支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已退款" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="orderNo" label="订单编号" width="200" show-overflow-tooltip />
        <el-table-column prop="memberId" label="会员ID" width="90" />
        <el-table-column label="订单类型" width="110">
          <template #default="{ row }">
            <el-tag :type="orderTypeTag(row.orderType)">{{ orderTypeText(row.orderType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="金额" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付状态" width="100">
          <template #default="{ row }">
            <el-tag :type="payStatusType(row.payStatus)">{{ payStatusText(row.payStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column prop="payTime" label="支付时间" width="170" />
        <el-table-column label="操作" min-width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.payStatus === 1" type="danger" text size="small" @click="handleRefund(row)">退款</el-button>
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="500px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="订单编号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="会员ID">{{ detailData.memberId }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">{{ orderTypeText(detailData.orderType) }}</el-descriptions-item>
        <el-descriptions-item label="金额">¥{{ detailData.amount }}</el-descriptions-item>
        <el-descriptions-item label="支付状态">{{ payStatusText(detailData.payStatus) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="支付时间" :span="2">{{ detailData.payTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, getOrderDetail, refundOrder } from '@/api/order'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ memberId: '', orderType: undefined as number | undefined, payStatus: undefined as number | undefined, page: 1, pageSize: 10 })

const detailVisible = ref(false)
const detailData = ref<any>({})

function orderTypeText(t: number) { return t === 1 ? '课程购买' : t === 2 ? '会员充值' : t === 3 ? '会员续费' : '-' }
function orderTypeTag(t: number) { return t === 1 ? 'primary' : t === 2 ? 'success' : 'warning' }
function payStatusText(s: number) { return s === 0 ? '未支付' : s === 1 ? '已支付' : s === 2 ? '已退款' : '-' }
function payStatusType(s: number) { return s === 0 ? 'info' : s === 1 ? 'success' : 'warning' }

async function loadData() {
  loading.value = true
  try {
    const data: any = await getOrderList(queryParams)
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadData() }
function handleReset() {
  queryParams.memberId = ''; queryParams.orderType = undefined; queryParams.payStatus = undefined; queryParams.page = 1
  loadData()
}

async function handleDetail(row: any) {
  try {
    const data: any = await getOrderDetail(row.id)
    detailData.value = data
    detailVisible.value = true
  } catch {}
}

async function handleRefund(row: any) {
  try {
    await ElMessageBox.confirm('确定要对该订单进行退款吗？退款后不可撤销。', '退款确认', { type: 'warning' })
    await refundOrder(row.id)
    ElMessage.success('退款成功')
    loadData()
  } catch {}
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 0; }
.search-card { margin-bottom: 16px; }
.search-card :deep(.el-card__body) { padding-bottom: 2px; }
.table-card { }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
