<template>
  <aside class="hidden md:block w-64 bg-white dark:bg-dark-800 border-r border-gray-200 dark:border-dark-700 h-[calc(100vh-80px)] overflow-y-auto">
    <nav class="p-6 space-y-2">
      <!-- Dashboard -->
      <RouterLink
        :to="dashboardPath"
        class="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-100 dark:hover:bg-dark-700 transition"
        :class="{ 'bg-primary-50 dark:bg-primary-900/30 text-primary-600': isRoute(dashboardPath) }"
      >
        <span>📊</span>
        Dashboard
      </RouterLink>

      <!-- Contests Section -->
      <div class="space-y-1">
        <RouterLink 
          to="/contests"
          class="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-100 dark:hover:bg-dark-700 transition"
          :class="{ 'bg-primary-50 dark:bg-primary-900/30 text-primary-600': isRoute('/contests') }"
        >
          <span>🎯</span>
          Contests
        </RouterLink>

        <!-- Manage Contests (Sub-item) -->
        <RouterLink v-if="userStore.isAdmin"
          to="/admin/contests"
          class="flex items-center gap-3 px-8 py-2 rounded-lg text-sm hover:bg-gray-100 dark:hover:bg-dark-700 transition"
          :class="{ 'bg-primary-50 dark:bg-primary-900/30 text-primary-600': isRoute('/admin/contests') }"
        >
          <span>📋</span>
          Manage Contests
        </RouterLink>
      </div>

      <!-- Leaderboard -->
      <RouterLink 
        to="/leaderboard"
        class="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-100 dark:hover:bg-dark-700 transition"
        :class="{ 'bg-primary-50 dark:bg-primary-900/30 text-primary-600': isRoute('/leaderboard') }"
      >
        <span>🏆</span>
        Leaderboard
      </RouterLink>

      <!-- Quick Actions Dropdown (Admin Only) -->
      <div v-if="userStore.isAdmin" class="px-2 py-2">
        <button 
          @click="showQuickActions = !showQuickActions"
          class="w-full flex items-center justify-between px-4 py-3 rounded-lg hover:bg-gray-100 dark:hover:bg-dark-700 transition font-medium"
        >
          <span class="flex items-center gap-3">
            <span>⚡</span>
            Quick Actions
          </span>
          <span :class="{ 'rotate-180': showQuickActions }">▼</span>
        </button>
        <div v-if="showQuickActions" class="mt-1 space-y-1 ml-2">
          <RouterLink 
            to="/admin/contests/create"
            class="block px-4 py-2 text-sm rounded-lg hover:bg-primary-50 dark:hover:bg-primary-900/30 text-primary-600 transition"
          >
            ➕ Create Contest
          </RouterLink>
          <RouterLink 
            to="/admin/reports"
            class="block px-4 py-2 text-sm rounded-lg hover:bg-primary-50 dark:hover:bg-primary-900/30 text-primary-600 transition"
          >
            📊 View Reports
          </RouterLink>
          <RouterLink 
            to="/admin/users"
            class="block px-4 py-2 text-sm rounded-lg hover:bg-primary-50 dark:hover:bg-primary-900/30 text-primary-600 transition"
          >
            👥 Manage Users
          </RouterLink>
        </div>
      </div>
    </nav>
  </aside>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores'

const route = useRoute()
const userStore = useUserStore()
const showQuickActions = ref(false)
const dashboardPath = computed(() => (userStore.isAdmin ? '/admin' : '/dashboard'))

const isRoute = (path) => {
  if (path === '/') {
    return route.path === '/'
  }
  return route.path === path || route.path.startsWith(path + '/')
}
</script>
