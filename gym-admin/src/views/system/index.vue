<template>
  <div class="page-container">
    <el-card shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="用户管理" name="users">
          <el-table :data="userData" stripe>
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="username" label="用户名" width="130" />
            <el-table-column prop="realName" label="真实姓名" width="120" />
            <el-table-column prop="phone" label="手机号" width="140" />
            <el-table-column label="角色" width="100">
              <template #default="{ row }">
                <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'">{{ row.role === 'admin' ? '管理员' : '教练' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="170" />
          </el-table>
          <el-empty v-if="userData.length === 0" description="暂无用户数据，此功能待完善" />
        </el-tab-pane>
        <el-tab-pane label="操作日志" name="logs">
          <el-empty description="操作日志功能开发中..." />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const activeTab = ref('users')

// 模拟数据
const userData = ref([
  { id: 1, username: 'admin', realName: '系统管理员', phone: '13800000001', role: 'admin', status: 1, createTime: '2024-01-01 00:00:00' },
  { id: 2, username: 'coach001', realName: '张教练', phone: '13800000002', role: 'coach', status: 1, createTime: '2024-03-15 10:30:00' }
])
</script>

<style scoped>
.page-container { padding: 0; }
</style>
