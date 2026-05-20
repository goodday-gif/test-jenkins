<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="器材名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryParams.category" placeholder="全部" clearable style="width: 140px">
            <el-option label="有氧器械" value="有氧器械" />
            <el-option label="力量器械" value="力量器械" />
            <el-option label="自由器械" value="自由器械" />
            <el-option label="功能性训练" value="功能性训练" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="维修" :value="2" />
            <el-option label="报废" :value="3" />
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
      <template #header>
        <el-button type="primary" @click="handleAdd">新增器材</el-button>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="名称" width="140" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column label="图片" width="90">
          <template #default="{ row }">
            <el-image v-if="row.image" :src="row.image" style="width: 50px; height: 50px" fit="cover" :preview-src-list="[row.image]" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="摆放位置" width="120" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="purchaseTime" label="购买时间" width="120" />
        <el-table-column prop="maintainTime" label="维护时间" width="120" />
        <el-table-column label="操作" min-width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" text size="small" @click="handleStatusChange(row)">修改状态</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑器材' : '新增器材'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="请输入器材名称" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="有氧器械" value="有氧器械" />
            <el-option label="力量器械" value="力量器械" />
            <el-option label="自由器械" value="自由器械" />
            <el-option label="功能性训练" value="功能性训练" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片">
          <ImageUpload v-model="form.image" />
        </el-form-item>
        <el-form-item label="功能介绍">
          <el-input v-model="form.functionDesc" type="textarea" :rows="3" placeholder="请输入功能介绍" />
        </el-form-item>
        <el-form-item label="使用方法">
          <el-input v-model="form.usageMethod" type="textarea" :rows="3" placeholder="请输入使用方法" />
        </el-form-item>
        <el-form-item label="摆放位置">
          <el-input v-model="form.location" placeholder="请输入摆放位置" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="正常" :value="1" />
            <el-option label="维修" :value="2" />
            <el-option label="报废" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="购买时间">
          <el-date-picker v-model="form.purchaseTime" type="date" value-format="YYYY-MM-DD" placeholder="请选择购买时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="维护时间">
          <el-date-picker v-model="form.maintainTime" type="date" value-format="YYYY-MM-DD" placeholder="请选择维护时间" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 修改状态弹窗 -->
    <el-dialog v-model="statusDialogVisible" title="修改器材状态" width="400px">
      <el-form label-width="80px">
        <el-form-item label="器材">{{ statusForm.name }}</el-form-item>
        <el-form-item label="状态">
          <el-select v-model="statusForm.status" style="width: 100%">
            <el-option label="正常" :value="1" />
            <el-option label="维修" :value="2" />
            <el-option label="报废" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitStatus">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getEquipmentList, addEquipment, updateEquipment, updateEquipmentStatus, deleteEquipment } from '@/api/equipment'
import ImageUpload from '@/components/ImageUpload.vue'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ keyword: '', category: '', status: undefined as number | undefined, page: 1, pageSize: 10 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive<any>({ id: undefined, name: '', category: '', image: '', functionDesc: '', usageMethod: '', location: '', status: 1, purchaseTime: '', maintainTime: '' })

const statusDialogVisible = ref(false)
const statusForm = reactive<any>({ id: 0, name: '', status: 1 })

function statusType(s: number) { return s === 1 ? 'success' : s === 2 ? 'warning' : 'danger' }
function statusText(s: number) { return s === 1 ? '正常' : s === 2 ? '维修' : '报废' }

async function loadData() {
  loading.value = true
  try {
    const data: any = await getEquipmentList(queryParams)
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadData() }
function handleReset() {
  queryParams.keyword = ''; queryParams.category = ''; queryParams.status = undefined; queryParams.page = 1
  loadData()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, { id: undefined, name: '', category: '', image: '', functionDesc: '', usageMethod: '', location: '', status: 1, purchaseTime: '', maintainTime: '' })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function submitForm() {
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateEquipment(form)
      ElMessage.success('编辑成功')
    } else {
      await addEquipment(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {} finally { submitLoading.value = false }
}

function handleStatusChange(row: any) {
  statusForm.id = row.id; statusForm.name = row.name; statusForm.status = row.status
  statusDialogVisible.value = true
}

async function submitStatus() {
  submitLoading.value = true
  try {
    await updateEquipmentStatus(statusForm.id, statusForm.status)
    ElMessage.success('状态修改成功')
    statusDialogVisible.value = false
    loadData()
  } catch {} finally { submitLoading.value = false }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除该器材吗？', '提示', { type: 'warning' })
    await deleteEquipment(row.id)
    ElMessage.success('删除成功')
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
