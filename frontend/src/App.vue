<template>
  <div class="app" :class="{ 'dark': isDark }">
    <Header v-if="!isAdminPage" @toggle-dark="toggleDark" />
    <div class="flex" :class="{ 'min-h-screen': isAdminPage, 'min-h-[calc(100vh-80px)]': !isAdminPage }">
      <Sidebar v-if="!isEditor && !isAuthPage && !isAdminPage" />
      <main class="flex-1">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores'
import Header from '@/components/common/Header.vue'
import Sidebar from '@/components/common/Sidebar.vue'

const isDark = ref(localStorage.getItem('theme') === 'dark')
const route = useRoute()
const userStore = useUserStore()

const isEditor = computed(() => route.name === 'ContestEditor')
const isAuthPage = computed(() => route.path.startsWith('/login'))
const isAdminPage = computed(() => route.path.startsWith('/admin'))

const toggleDark = () => {
  isDark.value = !isDark.value
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
  document.documentElement.classList.toggle('dark', isDark.value)
}

// Initialize dark mode
if (isDark.value) {
  document.documentElement.classList.add('dark')
}

onMounted(async () => {
  if (userStore.token) {
    await userStore.fetchUser()
  }
})
</script>

<style scoped>
.app {
  min-height: 100vh;
}
</style>
