<template>
  <div class="member-page">
    <div class="member-container">
      <!-- 左侧菜单 -->
      <aside class="member-sidebar">
        <div class="sidebar-card glass-card">
          <!-- 用户信息 -->
          <div class="user-profile">
            <div class="avatar-wrapper">
              <img v-if="userStore.userInfo?.avatar" :src="userStore.userInfo.avatar" class="avatar-img" />
              <div v-else class="avatar-placeholder">
                {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
              </div>
            </div>
            <h3 class="user-nickname">{{ userStore.userInfo?.nickname || '会员用户' }}</h3>
            <span class="member-badge">
              {{ levelLabel }}
            </span>
          </div>
          <!-- 菜单列表 -->
          <nav class="sidebar-menu">
            <router-link
              v-for="item in menuItems"
              :key="item.path"
              :to="item.path"
              class="menu-item"
              :class="{ active: $route.path === item.path }"
            >
              <span class="menu-icon">{{ item.icon }}</span>
              <span class="menu-label">{{ item.label }}</span>
            </router-link>
          </nav>
        </div>
      </aside>

      <!-- 右侧内容区 -->
      <main class="member-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const menuItems = [
  { path: '/member/profile', label: '个人信息', icon: '👤' },
  { path: '/member/tutorials', label: '我的教程', icon: '📚' },
  { path: '/member/classes', label: '我的课程', icon: '🏋️' },
  { path: '/member/orders', label: '我的订单', icon: '📋' },
  { path: '/member/password', label: '修改密码', icon: '🔒' }
]

const levelLabel = computed(() => {
  const level = userStore.userInfo?.level
  if (level === 2) return '⭐ 黄金会员'
  if (level === 3) return '💎 钻石会员'
  return '🎫 普通会员'
})

onMounted(() => {
  if (!userStore.userInfo) {
    userStore.getInfo()
  }
})
</script>

<style scoped>
.member-page {
  min-height: 60vh;
  padding: 40px 0 60px;
}

.member-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 40px;
  display: flex;
  gap: 32px;
  align-items: flex-start;
}

/* 左侧菜单 */
.member-sidebar {
  width: 260px;
  flex-shrink: 0;
  position: sticky;
  top: calc(var(--nav-height) + 40px);
}

.sidebar-card {
  padding: 28px 20px;
}

.sidebar-card:hover {
  transform: none;
}

.user-profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 16px;
}

.avatar-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  padding: 3px;
  background: var(--gradient-primary);
  margin-bottom: 14px;
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  display: block;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: var(--bg-elevated);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
}

.user-nickname {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.member-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  background: rgba(0, 210, 255, 0.1);
  color: var(--color-accent-cyan);
  border: 1px solid rgba(0, 210, 255, 0.15);
}

/* 菜单 */
.sidebar-menu {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: all var(--transition-fast);
  position: relative;
  border-left: 3px solid transparent;
}

.menu-item:hover {
  color: var(--text-primary);
  background: rgba(255, 255, 255, 0.04);
}

.menu-item.active {
  color: var(--text-primary);
  background: rgba(0, 210, 255, 0.06);
  border-left-color: var(--color-accent-cyan);
}

.menu-item.active .menu-icon {
  transform: scale(1.1);
}

.menu-icon {
  font-size: 18px;
  transition: transform var(--transition-fast);
}

/* 右侧主体 */
.member-main {
  flex: 1;
  min-width: 0;
}

@media (max-width: 768px) {
  .member-container {
    flex-direction: column;
    padding: 0 20px;
  }
  .member-sidebar {
    width: 100%;
    position: static;
  }
  .sidebar-card {
    padding: 20px 16px;
  }
  .user-profile {
    flex-direction: row;
    gap: 16px;
    padding-bottom: 16px;
  }
  .avatar-wrapper {
    width: 50px;
    height: 50px;
    margin-bottom: 0;
  }
  .sidebar-menu {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 6px;
  }
  .menu-item {
    padding: 8px 12px;
    border-left: none;
    border-bottom: 2px solid transparent;
    font-size: 13px;
  }
  .menu-item.active {
    border-bottom-color: var(--color-accent-cyan);
    border-left-color: transparent;
  }
}
</style>
