<template>
  <div class="p-6 max-w-7xl mx-auto">
    <h1 class="text-3xl font-bold mb-6 text-gray-900 dark:text-gray-100">Global Leaderboard</h1>

    <!-- Filters -->
    <div class="card p-4 mb-6 flex gap-4">
      <input 
        v-model="searchUsername"
        type="text" 
        placeholder="Search by username..." 
        class="input flex-1"
      />
      <select v-model="sortBy" class="select w-40">
        <option value="score">Sort by Score</option>
        <option value="contests">Sort by Contests</option>
        <option value="recent">Sort by Recent</option>
      </select>
    </div>

    <!-- Leaderboard Table -->
    <div v-if="loading" class="card h-96 skeleton"></div>
    <div v-else class="card overflow-hidden">
      <table class="table w-full">
        <thead>
          <tr>
            <th>Rank</th>
            <th>User</th>
            <th>Score</th>
            <th>Contests</th>
            <th>Problems Solved</th>
            <th>Accuracy</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(user, index) in filteredLeaderboard" :key="user.userId">
            <td>
              <span v-if="index < 3" class="text-lg mr-2">
                {{ ['🥇', '🥈', '🥉'][index] }}
              </span>
              {{ pageOffset + index + 1 }}
            </td>
            <td>
              <div class="flex items-center gap-2">
                <div class="w-8 h-8 rounded-full bg-gradient-to-br from-primary-400 to-primary-600 flex items-center justify-center text-white text-sm font-bold">
                  {{ user.userName[0] }}
                </div>
                {{ user.userName }}
              </div>
            </td>
            <td class="font-semibold text-primary-600">{{ user.score }}</td>
            <td>{{ user.contestCount }}</td>
            <td>{{ user.solvedCount }}</td>
            <td>{{ (user.accuracy * 100).toFixed(1) }}%</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="flex justify-center gap-2 mt-6">
      <button 
        @click="prevPage"
        :disabled="currentPage === 1"
        class="btn-secondary"
      >
        ← Previous
      </button>
      <span class="px-4 py-2 text-gray-600 dark:text-gray-400">
        Page {{ currentPage }}
      </span>
      <button 
        @click="nextPage"
        class="btn-secondary"
      >
        Next →
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useLeaderboard } from '@/composables/useLeaderboard'

const { leaderboard, loading, fetchLeaderboard, currentPage } = useLeaderboard()
const searchUsername = ref('')
const sortBy = ref('score')

onMounted(() => {
  fetchLeaderboard()
})

const filteredLeaderboard = computed(() => {
  let filtered = leaderboard.value

  if (searchUsername.value) {
    filtered = filtered.filter(user =>
      user.userName.toLowerCase().includes(searchUsername.value.toLowerCase())
    )
  }

  return filtered
})

const pageOffset = computed(() => (currentPage.value - 1) * 50)

const nextPage = () => {
  currentPage.value++
  fetchLeaderboard()
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchLeaderboard()
  }
}
</script>
