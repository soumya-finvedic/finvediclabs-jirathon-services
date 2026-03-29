<template>
  <div class="p-6 max-w-7xl mx-auto space-y-6">
    <div class="flex items-center justify-between gap-3">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100">Manage Contests</h1>
      <div class="flex gap-3">
        <button class="btn-secondary" :disabled="loading" @click="loadContests">
          {{ loading ? 'Refreshing...' : 'Refresh' }}
        </button>
        <RouterLink to="/admin/contests/create" class="btn-primary">
          ➕ Create Contest
        </RouterLink>
      </div>
    </div>

    <div v-if="error" class="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-700">
      {{ error }}
    </div>
    <div v-if="success" class="rounded-lg border border-green-200 bg-green-50 px-4 py-3 text-green-700">
      {{ success }}
    </div>

    <div class="card overflow-hidden">
      <div class="table-responsive">
        <table class="table w-full">
          <thead>
            <tr>
              <th>Title</th>
              <th>Type</th>
              <th>Status</th>
              <th>Fee</th>
              <th>Start</th>
              <th>End</th>
              <th>Created</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="!loading && contests.length === 0">
              <td colspan="8" class="text-center text-gray-500 py-8">
                No contests found. <RouterLink to="/admin/contests/create" class="text-primary-600 hover:underline">Create one</RouterLink>
              </td>
            </tr>
            <tr v-for="contest in contests" :key="contest.id">
              <td class="font-medium">{{ contest.title }}</td>
              <td>{{ contest.contestType }}</td>
              <td>
                <span class="badge" :class="statusClass(contest.status)">{{ contest.status }}</span>
              </td>
              <td>Rs. {{ Number(contest.registrationFee || 0).toFixed(2) }}</td>
              <td class="text-sm">{{ formatDate(contest.startTime) }}</td>
              <td class="text-sm">{{ formatDate(contest.endTime) }}</td>
              <td class="text-sm">{{ formatDate(contest.createdAt) }}</td>
              <td class="whitespace-nowrap space-x-2">
                <button class="btn-ghost text-sm" @click="editContest(contest)">Edit</button>
                <button class="btn-ghost text-sm text-red-600" :disabled="submitting" @click="deleteContest(contest)">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { adminContestAPI } from '@/api'

const contests = ref([])
const loading = ref(false)
const submitting = ref(false)
const error = ref('')
const success = ref('')

const formatDate = (value) => {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}

const statusClass = (status) => {
  return {
    DRAFT: 'badge-warning',
    PUBLISHED: 'badge-success',
    ARCHIVED: 'badge-danger'
  }[status] || 'badge-info'
}

const extractErrorMessage = (err, fallback = 'Request failed') => {
  return err?.response?.data?.error || err?.response?.data?.message || err?.message || fallback
}

const loadContests = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await adminContestAPI.list({ page: 0, size: 50 })
    const payload = response?.data?.data
    if (Array.isArray(payload?.content)) {
      contests.value = payload.content
    } else if (Array.isArray(payload)) {
      contests.value = payload
    } else {
      contests.value = []
    }
  } catch (err) {
    error.value = extractErrorMessage(err, 'Failed to load contests')
  } finally {
    loading.value = false
  }
}

const deleteContest = async (contest) => {
  const confirmed = window.confirm(`Delete contest "${contest.title}"?`)
  if (!confirmed) {
    return
  }

  submitting.value = true
  error.value = ''
  success.value = ''
  try {
    await adminContestAPI.delete(contest.id)
    success.value = 'Contest deleted successfully'
    await loadContests()
  } catch (err) {
    error.value = extractErrorMessage(err, 'Failed to delete contest')
    submitting.value = false
  }
}

const editContest = (contest) => {
  // TODO: Implement edit in modal or separate page
  console.log('Edit contest:', contest)
  // For now, show a simple alert
  alert('Edit feature coming soon. Contest: ' + contest.title)
}

onMounted(async () => {
  await loadContests()
})
</script>
