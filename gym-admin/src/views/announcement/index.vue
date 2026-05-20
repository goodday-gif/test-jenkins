<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="标题" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.type" placeholder="全部" clearable style="width: 120px">
            <el-option label="公告" :value="1" />
            <el-option label="资讯" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
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
        <el-button type="primary" @click="handleAdd">新增公告/资讯</el-button>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'warning' : 'primary'">{{ row.type === 1 ? '公告' : '资讯' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="封面" width="90">
          <template #default="{ row }">
            <el-image v-if="row.coverImg" :src="row.coverImg" style="width: 50px; height: 50px" fit="cover" :preview-src-list="[row.coverImg]" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="发布人" width="100" />
        <el-table-column label="置顶" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isTop === 1" type="danger">置顶</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已发布' : '草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" min-width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" type="success" text size="small" @click="handlePublish(row)">发布</el-button>
            <el-button type="warning" text size="small" @click="handleToggleTop(row)">{{ row.isTop === 1 ? '取消置顶' : '置顶' }}</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑' : '新增'" width="700px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="公告" :value="1" />
            <el-option label="资讯" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面图">
          <ImageUpload v-model="form.coverImg" />
        </el-form-item>
        <el-form-item label="发布人">
          <el-input v-model="form.author" placeholder="请输入发布人" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="15" placeholder="请输入内容" />
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
import { getAnnouncementList, addAnnouncement, updateAnnouncement, publishAnnouncement, toggleTopAnnouncement, deleteAnnouncement } from '@/api/announcement'
import ImageUpload from '@/components/ImageUpload.vue'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ keyword: '', type: undefined as number | undefined, status: undefined as number | undefined, page: 1, pageSize: 10 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive<any>({ id: undefined, title: '', type: 1, coverImg: '', author: '', content: '' })

async function loadData() {
  loading.value = true
  try {
    const data: any = await getAnnouncementList(queryParams)
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadData() }
function handleReset() {
  queryParams.keyword = ''; queryParams.type = undefined; queryParams.status = undefined; queryParams.page = 1
  loadData()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, { id: undefined, title: '', type: 1, coverImg: '', author: '', content: '' })
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
      await updateAnnouncement(form)
      ElMessage.success('编辑成功')
    } else {
      await addAnnouncement(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {} finally { submitLoading.value = false }
}

async function handlePublish(row: any) {
  try {
    await ElMessageBox.confirm('确定要发布该内容吗？', '提示', { type: 'warning' })
    await publishAnnouncement(row.id)
    ElMessage.success('发布成功')
    loadData()
  } catch {}
}

async function handleToggleTop(row: any) {
  try {
    await toggleTopAnnouncement(row.id)
    ElMessage.success(row.isTop === 1 ? '取消置顶成功' : '置顶成功')
    loadData()
  } catch {}
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除该内容吗？', '提示', { type: 'warning' })
    await deleteAnnouncement(row.id)
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
