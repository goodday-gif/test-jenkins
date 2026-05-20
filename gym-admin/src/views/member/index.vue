<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="用户名/昵称/手机号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
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
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="会员等级" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.memberLevel === 1" type="warning">VIP</el-tag>
            <el-tag v-else>普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="余额" width="100" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="到期时间" width="170" />
        <el-table-column label="操作" min-width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleDetail(row)">详情</el-button>
            <el-button type="success" text size="small" @click="handleRecharge(row)">充值</el-button>
            <el-button type="warning" text size="small" @click="handleRenew(row)">续费</el-button>
            <el-button :type="row.status === 1 ? 'danger' : 'success'" text size="small" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '解封' }}
            </el-button>
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
    <el-dialog v-model="detailVisible" title="会员详情" width="500px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detailData.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ detailData.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ genderText(detailData.gender) }}</el-descriptions-item>
        <el-descriptions-item label="会员等级">{{ detailData.memberLevel === 1 ? 'VIP' : '普通' }}</el-descriptions-item>
        <el-descriptions-item label="余额">{{ detailData.balance }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailData.status === 1 ? '正常' : '禁用' }}</el-descriptions-item>
        <el-descriptions-item label="到期时间" :span="2">{{ detailData.expireTime }}</el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">{{ detailData.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 充值弹窗 -->
    <el-dialog v-model="rechargeVisible" title="会员充值" width="400px">
      <el-form :model="rechargeForm" label-width="80px">
        <el-form-item label="会员">{{ rechargeForm.nickname || rechargeForm.username }}</el-form-item>
        <el-form-item label="充值金额">
          <el-input-number v-model="rechargeForm.amount" :min="1" :max="100000" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rechargeVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitRecharge">确认充值</el-button>
      </template>
    </el-dialog>

    <!-- 续费弹窗 -->
    <el-dialog v-model="renewVisible" title="会员续费" width="400px">
      <el-form :model="renewForm" label-width="80px">
        <el-form-item label="会员">{{ renewForm.nickname || renewForm.username }}</el-form-item>
        <el-form-item label="续费月数">
          <el-select v-model="renewForm.months" style="width: 100%">
            <el-option :label="'1个月'" :value="1" />
            <el-option :label="'3个月'" :value="3" />
            <el-option :label="'6个月'" :value="6" />
            <el-option :label="'12个月'" :value="12" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renewVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitRenew">确认续费</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMemberList, getMemberDetail, rechargeMember, renewMember, toggleMemberStatus } from '@/api/member'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ keyword: '', status: undefined as number | undefined, page: 1, pageSize: 10 })

const detailVisible = ref(false)
const detailData = ref<any>({})

const rechargeVisible = ref(false)
const rechargeForm = reactive<any>({ id: 0, username: '', nickname: '', amount: 100 })

const renewVisible = ref(false)
const renewForm = reactive<any>({ id: 0, username: '', nickname: '', months: 1 })

function genderText(g: number) {
  return g === 1 ? '男' : g === 2 ? '女' : '未知'
}

async function loadData() {
  loading.value = true
  try {
    const data: any = await getMemberList(queryParams)
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadData() }
function handleReset() {
  queryParams.keyword = ''; queryParams.status = undefined; queryParams.page = 1
  loadData()
}

async function handleDetail(row: any) {
  try {
    const data: any = await getMemberDetail(row.id)
    detailData.value = data
    detailVisible.value = true
  } catch {}
}

function handleRecharge(row: any) {
  rechargeForm.id = row.id; rechargeForm.username = row.username; rechargeForm.nickname = row.nickname; rechargeForm.amount = 100
  rechargeVisible.value = true
}

async function submitRecharge() {
  submitLoading.value = true
  try {
    await rechargeMember({ memberId: rechargeForm.id, amount: rechargeForm.amount })
    ElMessage.success('充值成功')
    rechargeVisible.value = false
    loadData()
  } catch {} finally { submitLoading.value = false }
}

function handleRenew(row: any) {
  renewForm.id = row.id; renewForm.username = row.username; renewForm.nickname = row.nickname; renewForm.months = 1
  renewVisible.value = true
}

async function submitRenew() {
  submitLoading.value = true
  try {
    await renewMember({ memberId: renewForm.id, months: renewForm.months })
    ElMessage.success('续费成功')
    renewVisible.value = false
    loadData()
  } catch {} finally { submitLoading.value = false }
}

async function handleToggleStatus(row: any) {
  const action = row.status === 1 ? '禁用' : '解封'
  try {
    await ElMessageBox.confirm(`确定要${action}该会员吗？`, '提示', { type: 'warning' })
    await toggleMemberStatus(row.id)
    ElMessage.success(`${action}成功`)
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
