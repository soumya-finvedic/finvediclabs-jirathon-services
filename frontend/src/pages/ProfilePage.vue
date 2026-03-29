<template>
  <div class="min-h-screen bg-gray-50 dark:bg-dark-900 p-6">
    <div class="max-w-4xl mx-auto">

      <!-- 🔷 Profile Card -->
      <div class="bg-white dark:bg-dark-800 rounded-2xl shadow p-6 mb-6">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-6">
            <!-- Avatar -->
            <img
              :src="user?.avatarUrl || defaultAvatar"
              alt="Avatar"
              class="w-20 h-20 rounded-full object-cover border"
            />

            <!-- User Info -->
            <div>
              <h2 class="text-2xl font-bold text-gray-800 dark:text-white">
                {{ user?.displayName }}
              </h2>

              <p class="text-gray-600 dark:text-gray-400">
                {{ user?.email }}
              </p>

              <p class="text-sm mt-1 text-green-600" v-if="user?.emailVerified">
                ✅ Email Verified
              </p>
              <p class="text-sm mt-1 text-red-500" v-else>
                ❌ Email Not Verified
              </p>
            </div>
          </div>

          <!-- Refresh Button -->
          <button
            @click="refreshUserData"
            :disabled="refreshing"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-400 transition-colors"
            :title="refreshing ? 'Refreshing...' : 'Refresh profile data'"
          >
            {{ refreshing ? '🔄 Refreshing...' : '🔄 Refresh' }}
          </button>
        </div>
      </div>

      <!-- 🔷 Profile Details -->
      <div class="bg-white dark:bg-dark-800 rounded-2xl shadow p-6 mb-6">
        <h3 class="text-lg font-semibold mb-4 text-gray-800 dark:text-white">
          Profile Details
        </h3>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">

          <div>
            <label class="text-sm text-gray-500">Email</label>
            <p class="font-medium">{{ user?.email || 'N/A' }}</p>
          </div>

          <div>
            <label class="text-sm text-gray-500">Mobile Number</label>
            <p class="font-medium">{{ user?.phoneNumber || 'N/A' }}</p>
          </div>

          <div>
            <label class="text-sm text-gray-500">Organization</label>
            <p class="font-medium">{{ user?.organizationId || 'N/A' }}</p>
          </div>

          <div>
            <label class="text-sm text-gray-500">Status</label>
            <p class="font-medium">
              <span class="badge" :class="statusBadgeClass(user?.status)">
                {{ user?.status || 'N/A' }}
              </span>
            </p>
          </div>

          <div>
            <label class="text-sm text-gray-500">Joined At</label>
            <p class="font-medium">{{ formatDate(user?.createdAt) }}</p>
          </div>

          <div>
            <label class="text-sm text-gray-500">Last Login</label>
            <p class="font-medium">{{ formatDate(user?.lastLoginAt) || 'Never' }}</p>
          </div>

        </div>
      </div>

      <!-- 🔷 Stats Section -->
      <div class="bg-white dark:bg-dark-800 rounded-2xl shadow p-6">
        <h3 class="text-lg font-semibold mb-4 text-gray-800 dark:text-white">
          Stats
        </h3>

        <div class="grid grid-cols-2 sm:grid-cols-3 gap-4">

          <div class="p-4 bg-gray-100 dark:bg-dark-700 rounded-xl text-center">
            <p class="text-sm text-gray-500">Contests</p>
            <p class="text-xl font-bold">{{ stats?.contests || 0 }}</p>
          </div>

          <div class="p-4 bg-gray-100 dark:bg-dark-700 rounded-xl text-center">
            <p class="text-sm text-gray-500">Problems Solved</p>
            <p class="text-xl font-bold">{{ stats?.problemsSolved || 0 }}</p>
          </div>

          <div class="p-4 bg-gray-100 dark:bg-dark-700 rounded-xl text-center">
            <p class="text-sm text-gray-500">Rank</p>
            <p class="text-xl font-bold">{{ stats?.rank || 'N/A' }}</p>
          </div>

        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userAPI } from '@/api'
import { useUserStore } from '@/stores'

const userStore = useUserStore()
const user = ref(null)
const stats = ref({
  contests: 0,
  problemsSolved: 0,
  rank: 'N/A'
})
const loading = ref(false)
const refreshing = ref(false)

const defaultAvatar = 'https://cdn-icons-png.flaticon.com/512/149/149071.png'

const fetchProfile = async () => {
  try {
    const res = await userAPI.getProfile()
    console.log('Fetched user profile:', res.data.data)
    user.value = res.data.data
  } catch (err) {
    console.error('Error fetching profile', err)
  }
}

const fetchStats = async () => {
  try {
    const res = await userAPI.getStats()
    if (res?.data?.data) {
      stats.value = res.data.data
    }
  } catch (err) {
    console.error('Error fetching stats', err)
    // Keep default stats if fetch fails
  }
}

const refreshUserData = async () => {
  refreshing.value = true
  try {
    console.log('Refreshing user data from server...')
    // Fetch fresh user data from auth service
    await userStore.fetchUser()
    // Also fetch from user service
    await fetchProfile()
    // Refresh stats
    await fetchStats()
    console.log('✅ User data refreshed successfully')
  } catch (err) {
    console.error('Error refreshing user data:', err)
  } finally {
    refreshing.value = false
  }
}

const formatDate = (date) => {
  if (!date) return 'N/A'
  return new Date(date).toLocaleString()
}

const statusBadgeClass = (status) => {
  return {
    ACTIVE: 'badge-success',
    PENDING_VERIFICATION: 'badge-warning',
    SUSPENDED: 'badge-danger',
    DELETED: 'badge-danger'
  }[status] || 'badge-info'
}

onMounted(() => {
  fetchProfile()
  fetchStats()
})
</script>