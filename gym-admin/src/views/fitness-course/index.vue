<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="教程标题" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryParams.categoryId" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="c in categoryList" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态">
          <el-select v-model="queryParams.publishStatus" placeholder="全部" clearable style="width: 120px">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">新增教程</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column label="难度" width="80">
          <template #default="{ row }">
            <el-tag :type="difficultyTag(row.difficulty).type" size="small">{{ difficultyTag(row.difficulty).text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分)" width="90" />
        <el-table-column prop="playCount" label="播放量" width="80" />
        <el-table-column prop="likeCount" label="点赞" width="70" />
        <el-table-column prop="collectCount" label="收藏" width="70" />
        <el-table-column label="发布状态" width="90">
          <template #default="{ row }">
            <el-tag :type="publishTag(row.publishStatus).type" size="small">{{ publishTag(row.publishStatus).text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.publishStatus !== 1" type="success" text size="small" @click="handlePublish(row)">发布</el-button>
            <el-button v-if="row.publishStatus === 1" type="warning" text size="small" @click="handleOffline(row)">下架</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑教程' : '新增教程'" width="700px" top="5vh">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入教程标题" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="c in categoryList" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="难度" prop="difficulty">
              <el-select v-model="formData.difficulty" placeholder="请选择难度" style="width: 100%">
                <el-option label="入门" :value="1" />
                <el-option label="初级" :value="2" />
                <el-option label="中级" :value="3" />
                <el-option label="高级" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="时长(分钟)" prop="duration">
          <el-input-number v-model="formData.duration" :min="1" :max="600" style="width: 200px" />
        </el-form-item>
        <el-form-item label="封面图">
          <ImageUpload v-model="formData.coverImg" />
        </el-form-item>
        <el-form-item label="视频">
          <VideoUpload v-model="formData.videoUrl" />
        </el-form-item>
        <el-form-item label="适合人群">
          <el-input v-model="formData.suitableFor" placeholder="请输入适合人群" />
        </el-form-item>
        <el-form-item label="教练建议">
          <el-input v-model="formData.coachTip" placeholder="请输入教练建议" />
        </el-form-item>
        <el-form-item label="图文内容">
          <el-input v-model="formData.content" type="textarea" :rows="6" placeholder="请输入图文内容" />
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
import { getCourseList, addCourse, updateCourse, publishCourse, offlineCourse, deleteCourse } from '@/api/course'
import { getCategoryList } from '@/api/category'
import ImageUpload from '@/components/ImageUpload.vue'
import VideoUpload from '@/components/VideoUpload.vue'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const categoryList = ref<any[]>([])
const queryParams = reactive({ keyword: '', categoryId: undefined as number | undefined, publishStatus: undefined as number | undefined, page: 1, pageSize: 10 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive({
  id: undefined as number | undefined,
  title: '', categoryId: undefined as number | undefined, difficulty: undefined as number | undefined,
  duration: 30, coverImg: '', videoUrl: '', suitableFor: '', coachTip: '', content: ''
})
const formRules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }]
}

type TagType = 'primary' | 'success' | 'info' | 'warning' | 'danger'

function difficultyTag(d: number): { text: string; type: TagType } {
  const map: Record<number, { text: string; type: TagType }> = {
    1: { text: '入门', type: 'success' }, 2: { text: '初级', type: 'primary' },
    3: { text: '中级', type: 'warning' }, 4: { text: '高级', type: 'danger' }
  }
  return map[d] || { text: '未知', type: 'info' }
}

function publishTag(s: number): { text: string; type: TagType } {
  const map: Record<number, { text: string; type: TagType }> = {
    0: { text: '草稿', type: 'info' }, 1: { text: '已发布', type: 'success' }, 2: { text: '已下架', type: 'warning' }
  }
  return map[s] || { text: '未知', type: 'info' }
}

async function loadCategories() {
  try {
    const data: any = await getCategoryList()
    categoryList.value = Array.isArray(data) ? data : (data.records || [])
  } catch {}
}

async function loadData() {
  loading.value = true
  try {
    const data: any = await getCourseList(queryParams)
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadData() }
function handleReset() {
  queryParams.keyword = ''; queryParams.categoryId = undefined; queryParams.publishStatus = undefined; queryParams.page = 1
  loadData()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(formData, { id: undefined, title: '', categoryId: undefined, difficulty: undefined, duration: 30, coverImg: '', videoUrl: '', suitableFor: '', coachTip: '', content: '' })
  dialogVisible.value = true
}

async function handleEdit(row: any) {
  isEdit.value = true
  Object.assign(formData, { id: row.id, title: row.title, categoryId: row.categoryId, difficulty: row.difficulty, duration: row.duration, coverImg: row.coverImg, videoUrl: row.videoUrl, suitableFor: row.suitableFor, coachTip: row.coachTip, content: row.content })
  dialogVisible.value = true
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateCourse({ ...formData })
    } else {
      const { id, ...rest } = formData
      await addCourse(rest)
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch {} finally { submitLoading.value = false }
}

async function handlePublish(row: any) {
  try {
    await ElMessageBox.confirm('确定要发布该教程吗？', '提示', { type: 'info' })
    await publishCourse(row.id)
    ElMessage.success('发布成功')
    loadData()
  } catch {}
}

async function handleOffline(row: any) {
  try {
    await ElMessageBox.confirm('确定要下架该教程吗？', '提示', { type: 'warning' })
    await offlineCourse(row.id)
    ElMessage.success('下架成功')
    loadData()
  } catch {}
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除该教程吗？', '提示', { type: 'warning' })
    await deleteCourse(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

onMounted(() => { loadCategories(); loadData() })
</script>

<style scoped>
.page-container { padding: 0; }
.search-card { margin-bottom: 16px; }
.search-card :deep(.el-card__body) { padding-bottom: 2px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
