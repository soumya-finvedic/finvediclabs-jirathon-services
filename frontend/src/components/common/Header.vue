<template>
  <header class="sticky top-0 z-40 bg-white dark:bg-dark-800 shadow border-b border-gray-200 dark:border-dark-700">
    <div class="px-4 sm:px-6 lg:px-8 py-4 flex items-center justify-between">
      <!-- Logo -->
      <RouterLink to="/" class="flex items-center" aria-label="Scalegrad">
        <img src="/scalegrad-logo-full.png" alt="Scalegrad" class="h-8 sm:h-9 w-auto shrink-0 object-contain" />
      </RouterLink>

      <!-- Nav Links - Desktop -->
      <nav class="hidden md:flex items-center gap-8">
        <RouterLink to="/contests"
          class="text-gray-700 dark:text-gray-200 hover:text-primary-600 dark:hover:text-primary-400 transition">
          Contests
        </RouterLink>
        <RouterLink to="/leaderboard"
          class="text-gray-700 dark:text-gray-200 hover:text-primary-600 dark:hover:text-primary-400 transition">
          Leaderboard
        </RouterLink>
        <div v-if="userStore.isAdmin">
          <RouterLink to="/admin"
            class="text-gray-700 dark:text-gray-200 hover:text-primary-600 dark:hover:text-primary-400 transition">
            Admin
          </RouterLink>
        </div>
      </nav>

      <!-- Right side -->
      <div class="flex items-center gap-4">
        <!-- Dark mode toggle -->
        <button @click="$emit('toggleDark')" class="btn-ghost p-2" title="Toggle dark mode">
          <span v-if="isDark">☀️</span>
          <span v-else>🌙</span>
        </button>

        <!-- Notifications -->
        <button class="btn-ghost p-2 relative">
          🔔
          <span v-if="unreadCount > 0"
            class="absolute top-0 right-0 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
            {{ unreadCount }}
          </span>
        </button>

        <!-- User menu -->
        <RouterLink v-if="!userStore.isAuthenticated" to="/login/student" class="btn-primary">
          Login / Register
        </RouterLink>

        <div v-else class="relative">
  <!-- Username Button -->
  <button @click="showUserMenu = !showUserMenu" class="btn-ghost p-2">
    {{ userStore.user?.displayName || '👤' }}
  </button>

  <!-- Dropdown -->
  <div
    v-if="showUserMenu"
    class="absolute right-0 mt-2 w-48 bg-white dark:bg-dark-800 rounded-lg shadow-lg border border-gray-200 dark:border-dark-700"
  >
    <!-- Profile -->
    <button
      @click="goToProfile"
      class="w-full text-left px-4 py-2 hover:bg-gray-100 dark:hover:bg-dark-700"
    >
      Profile
    </button>

    <!-- Logout -->
    <button
      @click="logout"
      class="w-full text-left px-4 py-2 hover:bg-gray-100 dark:hover:bg-dark-700 text-red-600"
    >
      Logout
    </button>
  </div>
</div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores'

const router = useRouter()
const userStore = useUserStore()
const showUserMenu = ref(false)
const isDark = computed(() => document.documentElement.classList.contains('dark'))
const unreadCount = ref(0)

const goToProfile = () => {
  router.push('/profile')
  showUserMenu.value = false
}

const logout = () => {
  userStore.logout()
  router.push('/')
  showUserMenu.value = false
}

defineEmits(['toggleDark'])
</script>
