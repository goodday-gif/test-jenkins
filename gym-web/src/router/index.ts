import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue')
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    children: [
      {
        path: '',
        redirect: '/home'
      },
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue')
      },
      {
        path: 'tutorial',
        name: 'TutorialList',
        component: () => import('@/views/tutorial/index.vue'),
        meta: { title: '健身教程' }
      },
      {
        path: 'tutorial/:id',
        name: 'TutorialDetail',
        component: () => import('@/views/tutorial/detail.vue'),
        meta: { title: '教程详情' }
      },
      {
        path: 'class',
        name: 'ClassList',
        component: () => import('@/views/class/index.vue'),
        meta: { title: '健身课程' }
      },
      {
        path: 'class/:id',
        name: 'ClassDetail',
        component: () => import('@/views/class/detail.vue'),
        meta: { title: '课程详情' }
      },
      {
        path: 'coach',
        name: 'CoachList',
        component: () => import('@/views/coach/index.vue'),
        meta: { title: '教练团队' }
      },
      {
        path: 'coach/:id',
        name: 'CoachDetail',
        component: () => import('@/views/coach/detail.vue'),
        meta: { title: '教练详情' }
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('@/views/equipment/index.vue'),
        meta: { title: '器材展示' }
      },
      {
        path: 'news',
        name: 'NewsList',
        component: () => import('@/views/news/index.vue'),
        meta: { title: '健身资讯' }
      },
      {
        path: 'news/:id',
        name: 'NewsDetail',
        component: () => import('@/views/news/detail.vue'),
        meta: { title: '资讯详情' }
      },
      {
        path: 'about',
        name: 'About',
        component: () => import('@/views/about/index.vue'),
        meta: { title: '关于我们' }
      },
      {
        path: 'member',
        name: 'MemberCenter',
        component: () => import('@/views/member/index.vue'),
        redirect: '/member/profile',
        meta: { requiresAuth: true },
        children: [
          {
            path: 'profile',
            name: 'MemberProfile',
            component: () => import('@/views/member/profile.vue'),
            meta: { title: '个人信息', requiresAuth: true }
          },
          {
            path: 'tutorials',
            name: 'MemberTutorials',
            component: () => import('@/views/member/tutorials.vue'),
            meta: { title: '我的教程', requiresAuth: true }
          },
          {
            path: 'classes',
            name: 'MemberClasses',
            component: () => import('@/views/member/classes.vue'),
            meta: { title: '我的课程', requiresAuth: true }
          },
          {
            path: 'orders',
            name: 'MemberOrders',
            component: () => import('@/views/member/orders.vue'),
            meta: { title: '我的订单', requiresAuth: true }
          },
          {
            path: 'password',
            name: 'MemberPassword',
            component: () => import('@/views/member/password.vue'),
            meta: { title: '修改密码', requiresAuth: true }
          }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('member_token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
