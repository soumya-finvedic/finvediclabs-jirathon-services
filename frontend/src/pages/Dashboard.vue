<template>
  <div class="p-6 max-w-7xl mx-auto">
    <!-- Hero Section -->
    <div class="card p-8 mb-8 bg-gradient-to-r from-primary-500 to-primary-700 text-white">
      <h1 class="text-4xl font-bold mb-2">Welcome to Jirathon</h1>
      <p class="text-lg opacity-90">Challenge yourself with coding contests, compete with others, and rise in rankings.
      </p>
    </div>

    <!-- User Stats -->
    <!-- MOCK DATA: userStats is hardcoded. Replace with userAPI.getStats() call.
    <div v-if="userStore.isAuthenticated" class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
      <div class="card p-4">
        <p class="text-sm text-gray-600 dark:text-gray-400">Contests Joined</p>
        <p class="text-2xl font-bold text-primary-600">{{ userStats.contestsJoined }}</p>
      </div>
      <div class="card p-4">
        <p class="text-sm text-gray-600 dark:text-gray-400">Problems Solved</p>
        <p class="text-2xl font-bold text-primary-600">{{ userStats.problemsSolved }}</p>
      </div>
      <div class="card p-4">
        <p class="text-sm text-gray-600 dark:text-gray-400">Current Rank</p>
        <p class="text-2xl font-bold text-primary-600">#{{ userStats.currentRank }}</p>
      </div>
      <div class="card p-4">
        <p class="text-sm text-gray-600 dark:text-gray-400">Score</p>
        <p class="text-2xl font-bold text-primary-600">{{ userStats.totalScore }}</p>
      </div>
    </div>
    -->

    <!-- Active Contests -->
    <div class="mb-8">
      <h2 class="text-2xl font-bold mb-4 text-gray-900 dark:text-gray-100">Active Contests</h2>
      <div v-if="loading" class="space-y-4">
        <div v-for="i in 3" :key="i" class="card h-40 skeleton"></div>
      </div>
      <div v-else-if="activeContests.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <ContestCard v-for="contest in activeContests.slice(0, 3)" :key="contest.id" :contest="contest"
          @joined="onContestJoined" />
      </div>
      <p v-else class="text-gray-500 dark:text-gray-400">No active contests right now. Check back soon!</p>
    </div>

    <!-- Upcoming Contests -->
    <div>
      <h2 class="text-2xl font-bold mb-4 text-gray-900 dark:text-gray-100">Upcoming Contests</h2>
      <div v-if="upcomingContests.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <ContestCard v-for="contest in upcomingContests.slice(0, 3)" :key="contest.id" :contest="contest" />
      </div>
      <p v-else class="text-gray-500 dark:text-gray-400">No upcoming contests scheduled.</p>
    </div>

    <!-- Sample Jirathon Projects -->
    <!-- MOCK DATA: sampleProjects is imported from a static local file (@/utils/sampleProjects).
         Replace with a real API call when a projects endpoint is available.
    <div class="mt-10">
      <h2 class="text-2xl font-bold mb-4 text-gray-900 dark:text-gray-100">Sample Jirathon Projects</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div v-for="project in sampleProjects" :key="project.id" class="card p-5">
          <div class="flex items-center justify-between mb-2">
            <h3 class="text-lg font-semibold">{{ project.name }}</h3>
            <span class="badge badge-info">{{ project.difficulty }}</span>
          </div>
          <p class="text-sm text-gray-600 dark:text-gray-400 mb-3">{{ project.summary }}</p>
          <p class="text-xs text-gray-600 dark:text-gray-400 inline-flex items-center gap-1">
            <CalendarIcon class="w-3.5 h-3.5 text-primary-600" />
            Start: {{ formatDate(project.startDate) }}
          </p>
          <p class="text-xs text-gray-600 dark:text-gray-400 mb-2 inline-flex items-center gap-1">
            <CalendarIcon class="w-3.5 h-3.5 text-primary-600" />
            End: {{ formatDate(project.endDate) }}
          </p>
          <p class="text-xs uppercase tracking-wide text-primary-600 font-semibold">{{ project.stack }}</p>
        </div>
      </div>
    </div>
    -->

    <!-- Trending Product Ideas -->
    <!-- MOCK DATA: trendingIdeas is imported from a static local file (@/utils/trendingIdeas).
         Replace with a real API call when a trends/content endpoint is available.
    <div class="mt-10">
      <h2 class="text-2xl font-bold mb-4 text-gray-900 dark:text-gray-100">Trending Product Ideas (2026)</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div v-for="idea in trendingIdeas" :key="idea.id" class="card p-5">
          <div class="flex items-center justify-between mb-2 gap-3">
            <h3 class="text-lg font-semibold">{{ idea.title }}</h3>
            <span class="badge badge-success">High Potential</span>
          </div>
          <p class="text-sm text-gray-600 dark:text-gray-400 mb-3">{{ idea.summary }}</p>
          <p class="text-xs font-semibold text-primary-700 dark:text-primary-300 mb-2">Impact: {{ idea.impact }}</p>
          <p class="text-xs text-gray-600 dark:text-gray-400">Services: {{ idea.services.join(', ') }}</p>
        </div>
      </div>
    </div>
    -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useContests } from '@/composables/useContests'
import { useUserStore } from '@/stores'
import ContestCard from '@/components/contest/ContestCard.vue'
// import CalendarIcon from '@/components/common/icons/CalendarIcon.vue' // only needed by commented sections

// MOCK DATA: static imports — replace with real API calls when endpoints are available
// import { sampleProjects } from '@/utils/sampleProjects'
// import { trendingIdeas } from '@/utils/trendingIdeas'

const userStore = useUserStore()
const { activeContests, upcomingContests, loading, fetchContests } = useContests()

// MOCK DATA: hardcoded user stats — replace with userAPI.getStats() once integrated
// const userStats = ref({
//   contestsJoined: 5,
//   problemsSolved: 23,
//   currentRank: 142,
//   totalScore: 5240
// })

onMounted(async () => {
  await fetchContests()
})

const onContestJoined = (contestId) => {
  // Update UI or refetch
  fetchContests()
}

const formatDate = (date) => new Date(date).toLocaleDateString()
</script>