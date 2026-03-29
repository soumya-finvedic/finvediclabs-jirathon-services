<template>
  <div class="space-y-6">
    <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100">Admin Dashboard</h2>

    <div v-if="error" class="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-700">
      {{ error }}
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div class="card p-6">
        <p class="text-gray-600 dark:text-gray-400 text-sm font-medium">Total Users</p>
        <p class="text-3xl font-bold text-primary-600">{{ formatInteger(stats.totalUsers) }}</p>
      </div>

      <div class="card p-6">
        <p class="text-gray-600 dark:text-gray-400 text-sm font-medium">Active Contests</p>
        <p class="text-3xl font-bold text-primary-600">{{ formatInteger(stats.activeContests) }}</p>
      </div>

      <div class="card p-6">
        <p class="text-gray-600 dark:text-gray-400 text-sm font-medium">Total Submissions</p>
        <p class="text-3xl font-bold text-primary-600">{{ formatInteger(stats.totalSubmissions) }}</p>
      </div>

      <div class="card p-6">
        <p class="text-gray-600 dark:text-gray-400 text-sm font-medium">Platform Revenue</p>
        <p class="text-3xl font-bold text-primary-600">${{ formatCurrency(stats.totalRevenue) }}</p>
      </div>
    </div>

    <div class="flex justify-end">
      <button class="btn-secondary" :disabled="loading" @click="loadStats">
        {{ loading ? 'Refreshing...' : 'Refresh Stats' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { adminContestAPI, adminUserAPI, paymentAPI } from '@/api'

const stats = ref({
  totalUsers: 0,
  activeContests: 0,
  totalSubmissions: 0,
  totalRevenue: 0
})

const loading = ref(false)
const error = ref('')

const formatInteger = (value) => Number(value || 0).toLocaleString()
const formatCurrency = (value) => Number(value || 0).toLocaleString(undefined, {
  minimumFractionDigits: 2,
  maximumFractionDigits: 2
})

const isContestCountable = (contest) => String(contest?.status || '').toUpperCase() !== 'ARCHIVED'

const fetchAllUsers = async () => {
  try {
    const firstPage = await adminUserAPI.list({ page: 0, size: 1 })
    const pagePayload = firstPage?.data?.data || {}
    const totalUsers = Number(pagePayload.totalElements || 0)

    if (totalUsers === 0) {
      return { users: [], totalUsers: 0 }
    }

    const fullPage = await adminUserAPI.list({ page: 0, size: Math.max(totalUsers, 1) })
    const users = fullPage?.data?.data?.content || []
    return { users, totalUsers }
  } catch (err) {
    console.error('Error fetching users:', err)
    return { users: [], totalUsers: 0 }
  }
}

const fetchAllContests = async () => {
  try {
    const firstPage = await adminContestAPI.list({ page: 0, size: 1 })
    const pagePayload = firstPage?.data?.data || {}
    const totalContests = Number(pagePayload.totalElements || 0)

    if (totalContests === 0) {
      return []
    }

    const fullPage = await adminContestAPI.list({ page: 0, size: Math.max(totalContests, 1) })
    return fullPage?.data?.data?.content || []
  } catch (err) {
    console.error('Error fetching contests:', err)
    return []
  }
}

const loadStats = async () => {
  loading.value = true
  error.value = ''

  try {
    const [{ users, totalUsers }, contests] = await Promise.all([
      fetchAllUsers(),
      fetchAllContests()
    ])

    const totalSubmissions = (users || []).reduce(
      (sum, user) => sum + Number(user?.stats?.totalSubmissions || 0),
      0
    )

    const activeContests = (contests || []).filter(isContestCountable).length

    const paymentsByUser = await Promise.all(
      (users || []).map(async (user) => {
        try {
          const response = await paymentAPI.listByUser(user.id)
          return response?.data?.data || []
        } catch {
          return []
        }
      })
    )

    const totalRevenue = paymentsByUser
      .flat()
      .filter((payment) => String(payment?.status || '').toUpperCase() === 'SUCCESS')
      .reduce((sum, payment) => sum + Number(payment?.payableAmount || 0), 0)

    stats.value = {
      totalUsers,
      activeContests,
      totalSubmissions,
      totalRevenue
    }
  } catch (e) {
    error.value = e?.response?.data?.error || e?.message || 'Failed to load dashboard stats'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
})
</script>
