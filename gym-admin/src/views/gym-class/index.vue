<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="课程名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="queryParams.classType" placeholder="全部" clearable style="width: 120px">
            <el-option label="团课" :value="1" />
            <el-option label="私教课" :value="2" />
            <el-option label="小班课" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="已取消" :value="0" />
            <el-option label="未开始" :value="1" />
            <el-option label="进行中" :value="2" />
            <el-option label="已结束" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">新增课程</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="课程名" min-width="130" show-overflow-tooltip />
        <el-table-column prop="coachName" label="教练" width="90" />
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="classTypeTag(row.classType).type" size="small">{{ classTypeTag(row.classType).text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="classTime" label="上课时间" width="170" />
        <el-table-column prop="location" label="地点" width="100" />
        <el-table-column label="名额" width="100">
          <template #default="{ row }">{{ row.currentCount || 0 }}/{{ row.maxCapacity }}</template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status).type" size="small">{{ statusTag(row.status).text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 1" type="warning" text size="small" @click="handleCancel(row)">取消</el-button>
            <el-button type="danger" text size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '新增课程'" width="600px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-form-item label="课程名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入课程名" />
        </el-form-item>
        <el-form-item label="教练" prop="coachId">
          <el-select v-model="formData.coachId" placeholder="请选择教练" style="width: 100%" filterable>
            <el-option v-for="c in coachList" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程类型" prop="classType">
          <el-select v-model="formData.classType" placeholder="请选择类型" style="width: 100%">
            <el-option label="团课" :value="1" />
            <el-option label="私教课" :value="2" />
            <el-option label="小班课" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="上课时间" prop="classTime">
          <el-date-picker v-model="formData.classTime" type="datetime" placeholder="请选择时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="formData.location" placeholder="请输入上课地点" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="最大名额" prop="maxCapacity">
              <el-input-number v-model="formData.maxCapacity" :min="1" :max="500" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="formData.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入课程描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getClassList, addClass, updateClass, cancelClass, deleteClass } from '@/api/gymClass'
import { getCoachList } from '@/api/coach'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const coachList = ref<any[]>([])
const queryParams = reactive({ keyword: '', classType: undefined as number | undefined, status: undefined as number | undefined, page: 1, pageSize: 10 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive({
  id: undefined as number | undefined,
  name: '', coachId: undefined as number | undefined, classType: undefined as number | undefined,
  classTime: '', location: '', maxCapacity: 30, price: 0, description: ''
})
const formRules: FormRules = {
  name: [{ required: true, message: '请输入课程名', trigger: 'blur' }],
  coachId: [{ required: true, message: '请选择教练', trigger: 'change' }],
  classType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  classTime: [{ required: true, message: '请选择时间', trigger: 'change' }],
  maxCapacity: [{ required: true, message: '请输入最大名额', trigger: 'blur' }]
}

type TagType = 'primary' | 'success' | 'info' | 'warning' | 'danger'

function classTypeTag(t: number): { text: string; type: TagType } {
  const map: Record<number, { text: string; type: TagType }> = {
    1: { text: '团课', type: 'primary' }, 2: { text: '私教课', type: 'warning' }, 3: { text: '小班课', type: 'success' }
  }
  return map[t] || { text: '未知', type: 'info' }
}

function statusTag(s: number): { text: string; type: TagType } {
  const map: Record<number, { text: string; type: TagType }> = {
    0: { text: '已取消', type: 'info' }, 1: { text: '未开始', type: 'warning' },
    2: { text: '进行中', type: 'success' }, 3: { text: '已结束', type: 'primary' }
  }
  return map[s] || { text: '未知', type: 'info' }
}

async function loadCoaches() {
  try {
    const data: any = await getCoachList({ page: 1, pageSize: 200 })
    coachList.value = data.records || []
  } catch {}
}

async function loadData() {
  loading.value = true
  try {
    const data: any = await getClassList(queryParams)
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadData() }
function handleReset() {
  queryParams.keyword = ''; queryParams.classType = undefined; queryParams.status = undefined; queryParams.page = 1
  loadData()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(formData, { id: undefined, name: '', coachId: undefined, classType: undefined, classTime: '', location: '', maxCapacity: 30, price: 0, description: '' })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  Object.assign(formData, { id: row.id, name: row.name, coachId: row.coachId, classType: row.classType, classTime: row.classTime, location: row.location, maxCapacity: row.maxCapacity, price: row.price, description: row.description })
  dialogVisible.value = true
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateClass({ ...formData })
    } else {
      const { id, ...rest } = formData
      await addClass(rest)
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch {} finally { submitLoading.value = false }
}

async function handleCancel(row: any) {
  try {
    await ElMessageBox.confirm('确定要取消该课程吗？', '提示', { type: 'warning' })
    await cancelClass(row.id)
    ElMessage.success('取消成功')
    loadData()
  } catch {}
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除该课程吗？', '提示', { type: 'warning' })
    await deleteClass(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

onMounted(() => { loadCoaches(); loadData() })
</script>

<style scoped>
.page-container { padding: 0; }
.search-card { margin-bottom: 16px; }
.search-card :deep(.el-card__body) { padding-bottom: 2px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
