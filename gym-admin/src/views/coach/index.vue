<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="姓名/手机号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="在职" :value="1" />
            <el-option label="离职" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">新增教练</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="36" :src="row.avatar" />
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="experienceYears" label="执教年限" width="90" />
        <el-table-column prop="specialty" label="擅长领域" min-width="140" show-overflow-tooltip />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '在职' : '离职' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleEdit(row)">编辑</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑教练' : '新增教练'" width="600px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="头像">
          <ImageUpload v-model="formData.avatar" />
        </el-form-item>
        <el-form-item label="执教年限">
          <el-input-number v-model="formData.experienceYears" :min="0" :max="50" style="width: 200px" />
        </el-form-item>
        <el-form-item label="擅长领域">
          <el-input v-model="formData.specialty" placeholder="如：瑜伽、力量训练" />
        </el-form-item>
        <el-form-item label="资质证书">
          <ImageUpload v-model="formData.certificate" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="formData.introduction" type="textarea" :rows="3" placeholder="请输入教练简介" />
        </el-form-item>
        <el-form-item label="授课风格">
          <el-input v-model="formData.teachingStyle" placeholder="请输入授课风格" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">在职</el-radio>
            <el-radio :value="0">离职</el-radio>
          </el-radio-group>
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
import { getCoachList, addCoach, updateCoach, deleteCoach } from '@/api/coach'
import ImageUpload from '@/components/ImageUpload.vue'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ keyword: '', status: undefined as number | undefined, page: 1, pageSize: 10 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive({
  id: undefined as number | undefined,
  name: '', phone: '', avatar: '', experienceYears: 0,
  specialty: '', certificate: '', introduction: '', teachingStyle: '', status: 1
})
const formRules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const data: any = await getCoachList(queryParams)
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadData() }
function handleReset() {
  queryParams.keyword = ''; queryParams.status = undefined; queryParams.page = 1
  loadData()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(formData, { id: undefined, name: '', phone: '', avatar: '', experienceYears: 0, specialty: '', certificate: '', introduction: '', teachingStyle: '', status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  Object.assign(formData, { id: row.id, name: row.name, phone: row.phone, avatar: row.avatar, experienceYears: row.experienceYears, specialty: row.specialty, certificate: row.certificate, introduction: row.introduction, teachingStyle: row.teachingStyle, status: row.status })
  dialogVisible.value = true
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateCoach({ ...formData })
    } else {
      const { id, ...rest } = formData
      await addCoach(rest)
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch {} finally { submitLoading.value = false }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除该教练吗？', '提示', { type: 'warning' })
    await deleteCoach(row.id)
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
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
