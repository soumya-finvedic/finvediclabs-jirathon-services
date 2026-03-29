import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores'

const routes = [
  {
    path: '/profile',
    component: () => import('@/pages/ProfilePage.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/AuthPage.vue'),
    meta: { guestOnly: true }
  },
  {
  path: '/payment/callback',
  name: 'PaymentCallback',
  component: () => import('@/pages/PaymentCallbackView.vue'),
  meta: { requiresAuth: true }
},
  {
    path: '/login/student',
    name: 'StudentLogin',
    redirect: { name: 'Login' }
  },
  {
    path: '/login/admin',
    name: 'AdminLogin',
    redirect: { name: 'Login' }
  },
  {
    path: '/',
    name: 'Home',
    redirect: { name: 'Login' }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/pages/Dashboard.vue')
  },
  {
    path: '/contests',
    name: 'ContestsList',
    component: () => import('@/pages/ContestsList.vue')
  },
  {
    path: '/contests/:id',
    name: 'ContestDetail',
    component: () => import('@/pages/ContestDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/contests/:id/arena',
    name: 'ContestArena',
    component: () => import('@/pages/Contestarena.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/contests/:id/editor',
    name: 'ContestEditor',
    component: () => import('@/pages/ContestEditor.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/contests/:id/leaderboard',
    name: 'ContestLeaderboard',
    component: () => import('@/pages/ContestLeaderboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/leaderboard',
    name: 'GlobalLeaderboard',
    component: () => import('@/pages/GlobalLeaderboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'AdminPanel',
    component: () => import('@/pages/AdminPanel.vue'),
    meta: { requiresAdmin: true }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('@/pages/AdminUsers.vue'),
    meta: { requiresAdmin: true }
  },
  {
    path: '/admin/contests',
    name: 'AdminContests',
    component: () => import('@/pages/AdminContests.vue'),
    meta: { requiresAdmin: true }
  },
  {
    path: '/admin/contests/create/:id?',
    name: 'CreateContest',
    component: () => import('@/pages/CreateContest.vue'),
    meta: { requiresAdmin: true }
  },
  {
    path: '/admin/reports',
    name: 'AdminReports',
    component: () => import('@/pages/AdminReports.vue'),
    meta: { requiresAdmin: true }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('@/pages/AuthPage.vue'),
    meta: { guestOnly: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/pages/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// Navigation guards
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.guestOnly && userStore.isAuthenticated) {
    if (userStore.isAdmin) {
      next({ name: 'AdminPanel' })
      return
    }
    next({ name: 'ContestsList' })
    return
  }

  if (to.meta.requiresAuth && !userStore.isAuthenticated) {
    next({ name: 'StudentLogin', query: { redirect: to.fullPath } })
    return
  }

  if (to.meta.requiresAdmin) {
    if (!userStore.isAdmin) {
      next({ name: 'Dashboard' })
      return
    }
  }
  next()
})

export default router
