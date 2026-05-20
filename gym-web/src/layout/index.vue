<template>
  <div class="app-layout">
    <!-- 顶部导航栏 -->
    <header class="navbar" :class="{ scrolled: isScrolled }">
      <div class="navbar-inner">
        <!-- Logo -->
        <router-link to="/home" class="logo">
          <span class="logo-icon">⚡</span>
          <span class="logo-text gradient-text">FitZone</span>
          <span class="logo-sub">健身中心</span>
        </router-link>

        <!-- 导航菜单 -->
        <nav class="nav-menu">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            class="nav-link"
            :class="{ active: isActive(item.path) }"
          >
            {{ item.label }}
          </router-link>
        </nav>

        <!-- 右侧操作 -->
        <div class="nav-right">
          <template v-if="!userStore.isLoggedIn">
            <router-link to="/login" class="btn-login">登录</router-link>
            <router-link to="/register" class="btn-register">注册</router-link>
          </template>
          <template v-else>
            <div class="user-dropdown" ref="dropdownRef" @click.stop="showDropdown = !showDropdown">
              <div class="user-avatar">
                <img v-if="userStore.userInfo?.avatar" :src="userStore.userInfo.avatar" class="user-avatar-img" />
                <span v-else>{{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}</span>
              </div>
              <span class="user-name">{{ userStore.userInfo?.nickname || '会员' }}</span>
              <transition name="dropdown">
                <div v-if="showDropdown" class="dropdown-menu" @click.stop>
                  <router-link to="/member/profile" class="dropdown-item" @click="showDropdown = false">
                    <span class="dropdown-icon">👤</span> 个人信息
                  </router-link>
                  <router-link to="/member/tutorials" class="dropdown-item" @click="showDropdown = false">
                    <span class="dropdown-icon">📚</span> 我的教程
                  </router-link>
                  <router-link to="/member/classes" class="dropdown-item" @click="showDropdown = false">
                    <span class="dropdown-icon">🏋️</span> 我的课程
                  </router-link>
                  <router-link to="/member/orders" class="dropdown-item" @click="showDropdown = false">
                    <span class="dropdown-icon">📋</span> 我的订单
                  </router-link>
                  <div class="dropdown-divider"></div>
                  <div class="dropdown-item logout" @click="showDropdown = false; userStore.logout()">
                    <span class="dropdown-icon">🚪</span> 退出登录
                  </div>
                </div>
              </transition>
            </div>
          </template>
        </div>
      </div>
    </header>

    <!-- 内容区 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 底部页脚 -->
    <footer class="app-footer">
      <div class="footer-inner">
        <div class="footer-grid">
          <!-- Logo & 简介 -->
          <div class="footer-brand">
            <div class="footer-logo">
              <span class="logo-icon">⚡</span>
              <span class="gradient-text" style="font-size: 22px; font-weight: 700;">FitZone</span>
            </div>
            <p class="footer-desc">
              专业的健身服务平台，为您提供优质的健身课程、<br>专业教练指导和先进的健身器材。
            </p>
          </div>

          <!-- 快速链接 -->
          <div class="footer-links">
            <h4>快速链接</h4>
            <router-link to="/tutorial">健身教程</router-link>
            <router-link to="/class">健身课程</router-link>
            <router-link to="/coach">教练团队</router-link>
            <router-link to="/equipment">器材展示</router-link>
          </div>

          <!-- 更多链接 -->
          <div class="footer-links">
            <h4>了解更多</h4>
            <router-link to="/news">健身资讯</router-link>
            <router-link to="/about">关于我们</router-link>
            <router-link to="/member">会员中心</router-link>
          </div>

          <!-- 联系信息 -->
          <div class="footer-contact">
            <h4>联系我们</h4>
            <p>📍 北京市朝阳区建国路88号</p>
            <p>📞 400-888-9999</p>
            <p>✉️ contact@fitzone.com</p>
            <p>🕐 周一至周日 06:00-23:00</p>
          </div>
        </div>

        <div class="footer-bottom">
          <p>© 2026 FitZone 健身中心. All rights reserved.</p>
        </div>
      </div>
    </footer>

    <!-- AI 智能助手 -->
    <AiChat />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import AiChat from '@/components/AiChat.vue'

const route = useRoute()
const userStore = useUserStore()
const isScrolled = ref(false)
const showDropdown = ref(false)

const navItems = [
  { path: '/home', label: '首页' },
  { path: '/tutorial', label: '健身教程' },
  { path: '/class', label: '健身课程' },
  { path: '/coach', label: '教练团队' },
  { path: '/equipment', label: '器材展示' },
  { path: '/news', label: '健身资讯' },
  { path: '/about', label: '关于我们' }
]

function isActive(path: string) {
  return route.path === path || route.path.startsWith(path + '/')
}

function handleScroll() {
  isScrolled.value = window.scrollY > 20
}

function closeDropdown() {
  showDropdown.value = false
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  document.addEventListener('click', closeDropdown)
  if (userStore.isLoggedIn && !userStore.userInfo) {
    userStore.getInfo()
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  document.removeEventListener('click', closeDropdown)
})
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* ===== 导航栏 ===== */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: var(--nav-height);
  background: rgba(10, 10, 10, 0.7);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid transparent;
  transition: all var(--transition-normal);
}

.navbar.scrolled {
  background: rgba(10, 10, 10, 0.92);
  border-bottom-color: var(--border-color);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.3);
}

.navbar-inner {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 40px;
  gap: 40px;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  flex-shrink: 0;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.logo-sub {
  color: var(--text-secondary);
  font-size: 13px;
  font-weight: 400;
  margin-left: 4px;
}

/* 导航菜单 */
.nav-menu {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.nav-link {
  padding: 8px 16px;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
  text-decoration: none;
  white-space: nowrap;
}

.nav-link:hover {
  color: var(--text-primary);
  background: rgba(255, 255, 255, 0.05);
}

.nav-link.active {
  color: var(--color-accent-cyan);
  background: rgba(0, 210, 255, 0.08);
}

/* 右侧 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.btn-login {
  padding: 8px 20px;
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 500;
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
  text-decoration: none;
}

.btn-login:hover {
  background: rgba(255, 255, 255, 0.06);
}

.btn-register {
  padding: 8px 24px;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  border-radius: var(--radius-sm);
  text-decoration: none;
  transition: all var(--transition-fast);
}

.btn-register:hover {
  box-shadow: 0 0 20px rgba(0, 210, 255, 0.3);
}

/* 用户下拉 */
.user-dropdown {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: var(--radius-sm);
  transition: background var(--transition-fast);
}

.user-dropdown:hover {
  background: rgba(255, 255, 255, 0.05);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  overflow: hidden;
}

.user-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.user-name {
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 500;
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 200px;
  background: rgba(26, 26, 46, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  color: var(--text-secondary);
  font-size: 14px;
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
  text-decoration: none;
  cursor: pointer;
}

.dropdown-item:hover {
  color: var(--text-primary);
  background: rgba(255, 255, 255, 0.06);
}

.dropdown-item.logout:hover {
  color: #ff4757;
}

.dropdown-icon {
  font-size: 16px;
}

.dropdown-divider {
  height: 1px;
  background: var(--border-color);
  margin: 6px 0;
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ===== 内容区 ===== */
.main-content {
  flex: 1;
  margin-top: var(--nav-height);
  min-height: calc(100vh - var(--nav-height));
}

/* ===== 页脚 ===== */
.app-footer {
  background: #0d0d1a;
  border-top: 1px solid var(--border-color);
  margin-top: auto;
}

.footer-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 60px 40px 30px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1fr 1.2fr;
  gap: 40px;
  margin-bottom: 40px;
}

.footer-brand .footer-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.footer-desc {
  color: var(--text-muted);
  font-size: 14px;
  line-height: 1.8;
}

.footer-links h4,
.footer-contact h4 {
  color: var(--text-primary);
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 16px;
}

.footer-links a {
  display: block;
  color: var(--text-muted);
  font-size: 14px;
  padding: 6px 0;
  transition: color var(--transition-fast);
  text-decoration: none;
}

.footer-links a:hover {
  color: var(--color-accent-cyan);
}

.footer-contact p {
  color: var(--text-muted);
  font-size: 14px;
  padding: 5px 0;
}

.footer-bottom {
  border-top: 1px solid var(--border-color);
  padding-top: 24px;
  text-align: center;
}

.footer-bottom p {
  color: var(--text-muted);
  font-size: 13px;
}
</style>
