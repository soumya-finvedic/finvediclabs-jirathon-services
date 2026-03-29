<template>
  <div class="p-6 max-w-7xl mx-auto">
    <!-- Header with filters -->
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100">Contests</h1>
      <div class="flex gap-3">
        <input v-model="searchQuery" type="text" placeholder="Search contests..." class="input w-64" />
        <select v-model="statusFilter" class="select w-40">
          <option value="">All Status</option>
          <option value="PUBLISHED">Published</option>
          <option value="DRAFT">Draft</option>
          <option value="ARCHIVED">Archived</option>
        </select>
      </div>
    </div>

    <!-- Contests Grid -->
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div v-for="i in 6" :key="i" class="card h-40 skeleton"></div>
    </div>
    <div v-else-if="filteredContests.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <ContestCard v-for="contest in filteredContests" :key="contest.id" :contest="contest"
        @joined="handleContestJoined" />
    </div>
    <div v-else class="text-center py-12">
      <p class="text-gray-500 dark:text-gray-400 text-lg">{{ emptyStateMessage }}</p>
    </div>

    <div class="mt-10">
      <h2 class="text-2xl font-bold mb-4 text-gray-900 dark:text-gray-100">Sample Jirathon Projects</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div v-for="project in sampleProjects" :key="project.id" class="card-sm p-4">
          <p class="text-sm font-semibold mb-1">{{ project.name }}</p>
          <p class="text-xs text-gray-600 dark:text-gray-400 mb-2">{{ project.stack }}</p>
          <p class="text-xs text-gray-600 dark:text-gray-400 inline-flex items-center gap-1">
            <CalendarIcon class="w-3.5 h-3.5 text-primary-600" />
            Start: {{ formatDate(project.startDate) }}
          </p>
          <p class="text-xs text-gray-600 dark:text-gray-400 mb-2 inline-flex items-center gap-1">
            <CalendarIcon class="w-3.5 h-3.5 text-primary-600" />
            End: {{ formatDate(project.endDate) }}
          </p>
          <span class="badge badge-info">{{ project.difficulty }}</span>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex justify-center gap-2 mt-8">
      <button v-for="page in totalPages" :key="page" @click="currentPage = page" :class="[
        'px-3 py-2 rounded-lg transition',
        currentPage === page
          ? 'bg-primary-600 text-white'
          : 'bg-gray-200 dark:bg-dark-700 hover:bg-gray-300'
      ]">
        {{ page }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useContests } from '@/composables/useContests'
import ContestCard from '@/components/contest/ContestCard.vue'
import CalendarIcon from '@/components/common/icons/CalendarIcon.vue'
import { sampleProjects } from '@/utils/sampleProjects'
import { contestAPI } from '@/api'
import { useUserStore } from '@/stores'
import { getContestRegistrations } from '@/utils/localRegistrations'

const { contests, loading, fetchContests } = useContests()
const userStore = useUserStore()
const searchQuery = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const itemsPerPage = 9
const registeredContestIds = ref(new Set())

const normalizeContestId = (id) => String(id)

const refreshRegisteredContests = async () => {
  const ids = new Set(Object.keys(getContestRegistrations() || {}))

  if (userStore.isAuthenticated && contests.value.length > 0) {
    const checks = await Promise.allSettled(
      contests.value.map(async (contest) => {
        const response = await contestAPI.getMyRegistration(contest.id, { skipAuthRedirect: true })
        return response.data?.data?.id ? normalizeContestId(contest.id) : null
      })
    )

    checks.forEach((result) => {
      if (result.status === 'fulfilled' && result.value) {
        ids.add(result.value)
      }
    })
  }

  registeredContestIds.value = ids
}

const handleContestJoined = async () => {
  await fetchContests()
  await refreshRegisteredContests()
}

onMounted(async () => {
  await fetchContests()
  await refreshRegisteredContests()
})

watch(
  () => userStore.isAuthenticated,
  async () => {
    await refreshRegisteredContests()
  }
)

const filteredContests = computed(() => {
  let filtered = contests.value

  // Non-admin users should only see published contests.
  if (!userStore.isAdmin) {
    filtered = filtered.filter((contest) => contest.status === 'PUBLISHED')
  }

  if (searchQuery.value) {
    filtered = filtered.filter(c =>
      c.title.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  if (statusFilter.value) {
    filtered = filtered.filter(c => c.status === statusFilter.value)
  }

  return filtered
})

const totalPages = computed(() =>
  Math.ceil(filteredContests.value.length / itemsPerPage)
)

const emptyStateMessage = computed(() => {
  return 'No contests found for the selected filters.'
})

const formatDate = (date) => new Date(date).toLocaleDateString()
</script>
