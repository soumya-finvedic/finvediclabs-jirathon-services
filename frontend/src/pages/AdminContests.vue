<template>
  <div class="flex min-h-screen bg-slate-900">
    <!-- Sidebar Navigation -->
    <aside class="w-64 flex-shrink-0 bg-slate-950 border-r border-slate-800 flex flex-col">
      <!-- Logo -->
      <div class="flex items-center gap-3 px-6 py-6 border-b border-slate-800">
        <div class="w-9 h-9 bg-gradient-to-br from-cyan-400 to-blue-600 rounded-lg flex items-center justify-center text-white font-bold text-sm">
          ⚡
        </div>
        <span class="text-lg font-bold text-white tracking-wide">SCALEGRAD</span>
      </div>

      <!-- Nav -->
      <div class="flex-1 px-4 py-6 overflow-y-auto">
        <p class="text-xs uppercase font-semibold text-slate-500 px-3 mb-3">MAIN</p>
        <nav class="space-y-1 mb-6">
          <RouterLink
            to="/admin"
            class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition text-sm"
            :class="{ 'bg-slate-800 text-white': $route.name === 'AdminPanel' }"
          >
            <span class="text-base">⊞</span>
            <span>Dashboard</span>
          </RouterLink>

          <RouterLink
            to="/admin/contests"
            class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition text-sm"
            :class="{ 'bg-slate-800 text-white': $route.path === '/admin/contests' }"
          >
            <span class="text-base">🏆</span>
            <span>Contests</span>
          </RouterLink>

          <RouterLink
            to="/leaderboard"
            class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition text-sm"
          >
            <span class="text-base">📊</span>
            <span>Leaderboard</span>
          </RouterLink>
        </nav>

        <p class="text-xs uppercase font-semibold text-slate-500 px-3 mb-3">ADMIN</p>
        <nav class="space-y-1">
          <RouterLink
            to="/admin/contests"
            class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition text-sm"
            :class="{ 'bg-slate-800 text-white': $route.path === '/admin/contests' }"
          >
            <span class="text-base">📋</span>
            <span>Manage Contests</span>
          </RouterLink>

          <RouterLink
            to="/admin/users"
            class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition text-sm"
          >
            <span class="text-base">👥</span>
            <span>Manage Users</span>
          </RouterLink>

          <RouterLink
            to="/admin/reports"
            class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition text-sm"
          >
            <span class="text-base">📄</span>
            <span>View Reports</span>
          </RouterLink>

          <RouterLink
            to="/admin/settings"
            class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition text-sm"
          >
            <span class="text-base">⚙️</span>
            <span>System Settings</span>
          </RouterLink>
        </nav>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 flex flex-col overflow-auto min-w-0">
      <!-- Sticky Top Bar -->
      <div class="sticky top-0 z-30 bg-slate-900/95 backdrop-blur border-b border-slate-800 px-8 py-3 flex items-center justify-between">
        <span class="text-white font-semibold text-sm">Manage Contests</span>
        <div class="flex items-center gap-3">
          <button
            class="flex items-center gap-2 px-4 py-2 bg-slate-800 hover:bg-slate-700 text-white rounded-lg text-sm font-medium transition border border-slate-700"
            @click="loadContests"
          >
            <span class="text-base">↻</span>
            <span>Refresh</span>
          </button>
          <button
            class="flex items-center gap-2 px-4 py-2 bg-cyan-500 hover:bg-cyan-400 text-slate-900 rounded-lg text-sm font-semibold transition"
            @click="showCreateModal = true"
          >
            <span>+</span>
            <span>Create Contest</span>
          </button>
          <button class="relative p-2 text-slate-300 hover:text-white transition">
            🔔
            <span class="absolute top-0.5 right-0.5 w-2 h-2 bg-yellow-400 rounded-full"></span>
          </button>
        </div>
      </div>

      <!-- Page Content -->
      <div class="p-8 max-w-7xl mx-auto w-full">
        <!-- Page Header -->
        <div class="flex items-start justify-between mb-8">
          <div>
            <h1 class="text-3xl font-bold text-white mb-1">Manage Contests</h1>
            <p class="text-slate-400 text-sm">Oversee all contests, review upcoming events and manage their lifecycle.</p>
          </div>
          <div class="flex gap-3 flex-shrink-0">
            <button
              class="flex items-center gap-2 px-4 py-2.5 bg-purple-600 hover:bg-purple-700 text-white rounded-lg font-medium transition text-sm"
              @click="showRegenerateModal = true"
            >
              <span>✦</span>
              <span>Regenerate</span>
            </button>
            <button
              class="flex items-center gap-2 px-4 py-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-900 rounded-lg font-semibold transition text-sm"
              @click="showCreateModal = true"
            >
              <span>+</span>
              <span>Create Contest</span>
            </button>
          </div>
        </div>

        <!-- Status Tabs -->
        <div class="flex gap-1 mb-6 border-b border-slate-800 overflow-x-auto">
          <button
            v-for="tab in statusTabs"
            :key="tab.value"
            :class="[
              'flex items-center gap-2 px-4 py-3 font-medium transition whitespace-nowrap text-sm border-b-2 -mb-px',
              activeTab === tab.value
                ? 'border-cyan-400 text-white'
                : 'border-transparent text-slate-400 hover:text-slate-200'
            ]"
            @click="activeTab = tab.value"
          >
            {{ tab.label }}
            <span :class="[
              'inline-flex items-center justify-center min-w-[22px] h-5 px-1.5 rounded text-xs font-semibold',
              activeTab === tab.value ? 'bg-cyan-500/20 text-cyan-300' : 'bg-slate-700 text-slate-300'
            ]">{{ getContestCountByStatus(tab.value) }}</span>
          </button>
        </div>

        <!-- Search + Filter Row -->
        <div class="flex gap-3 mb-6">
          <div class="flex-1 relative">
            <span class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-sm">🔍</span>
            <input
              v-model="searchQuery"
              class="w-full pl-9 pr-4 py-2.5 bg-slate-800 border border-slate-700 rounded-lg text-white placeholder-slate-500 text-sm focus:outline-none focus:border-cyan-500 transition"
              placeholder="Search contests by name, category..."
            />
          </div>
          <select
            v-model="categoryFilter"
            class="px-4 py-2.5 bg-slate-800 border border-slate-700 rounded-lg text-white text-sm focus:outline-none focus:border-cyan-500 transition min-w-[160px]"
          >
            <option value="">All Categories</option>
            <option value="rl">Reinforcement Learning</option>
            <option value="vision">Computer Vision</option>
            <option value="nlp">NLP</option>
            <option value="audio">Audio &amp; Speech</option>
            <option value="finance">Finance</option>
            <option value="tabular">Tabular Data</option>
            <option value="timeseries">Time Series</option>
          </select>
          <select
            v-model="sortOrder"
            class="px-4 py-2.5 bg-slate-800 border border-slate-700 rounded-lg text-white text-sm focus:outline-none focus:border-cyan-500 transition min-w-[140px]"
          >
            <option value="newest">Sort: Newest</option>
            <option value="oldest">Sort: Oldest</option>
            <option value="prize_desc">Prize: High→Low</option>
            <option value="prize_asc">Prize: Low→High</option>
          </select>
        </div>

        <!-- Error/Success Messages -->
        <div v-if="error" class="mb-6 p-4 bg-red-900/20 border border-red-800 rounded-lg text-red-300 text-sm">
          {{ error }}
        </div>
        <div v-if="success" class="mb-6 p-4 bg-green-900/20 border border-green-800 rounded-lg text-green-300 text-sm">
          {{ success }}
        </div>

        <!-- AI Generating Banner -->
        <div class="mb-8 p-5 bg-slate-800/60 border border-slate-700 rounded-xl flex items-center gap-4">
          <span class="text-2xl flex-shrink-0">✦</span>
          <div>
            <p class="text-cyan-400 font-medium text-sm">AI is generating upcoming event cards...</p>
            <p class="text-slate-400 text-xs mt-0.5">Analysing platform trends, category gaps, prize distribution patterns, and historical participation data.</p>
          </div>
        </div>

        <!-- Upcoming Events Section -->
        <div class="flex items-center gap-3 mb-5">
          <h2 class="text-lg font-bold text-white">Upcoming Events</h2>
          <span class="px-2.5 py-1 bg-purple-600/30 border border-purple-500/40 text-purple-300 text-xs font-semibold rounded-full">+ AI SUGGESTED</span>
        </div>

        <!-- Loading skeleton -->
        <div v-if="loading && contests.length === 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          <div v-for="i in 6" :key="i" class="h-64 rounded-xl bg-slate-800 animate-pulse"></div>
        </div>

        <!-- Contest Cards Grid -->
        <div v-else-if="displayContests.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5 mb-10">
          <div
            v-for="contest in displayContests"
            :key="contest.id"
            :class="[
              'rounded-xl p-5 text-white relative overflow-hidden cursor-pointer transition-all hover:scale-[1.02] hover:shadow-2xl',
              getGradientClass(contest.category || 'general')
            ]"
            @click="editContest(contest)"
          >
            <!-- Icon top-right -->
            <div class="absolute top-4 right-4 text-3xl opacity-50 select-none">
              {{ getContestIcon(contest.category) }}
            </div>

            <!-- Starts-in badge -->
            <div class="inline-flex items-center gap-1.5 mb-3 px-3 py-1 bg-black/60 rounded text-xs font-semibold text-white">
              <span>⏱</span>
              <span>Starts in {{ getStartsInDays(contest.startTime) }} days</span>
            </div>

            <!-- Category label -->
            <p :class="['text-xs font-bold uppercase tracking-widest mb-1.5', getCategoryColor(contest.category)]">
              {{ getCategoryLabel(contest.category) }}
            </p>

            <!-- Title -->
            <h3 class="text-lg font-bold text-white mb-2 leading-tight">{{ contest.title }}</h3>

            <!-- Description -->
            <p class="text-xs text-slate-200/80 mb-4 line-clamp-2 leading-relaxed">{{ contest.description }}</p>

            <!-- Tags -->
            <div class="flex flex-wrap gap-1.5 mb-5">
              <span
                v-for="tag in getContestTags(contest)"
                :key="tag"
                class="px-2 py-0.5 bg-white/15 border border-white/10 rounded text-xs font-medium"
              >
                {{ tag }}
              </span>
            </div>

            <!-- Stats -->
            <div class="grid grid-cols-3 gap-2 mb-5 pt-4 border-t border-white/20">
              <div>
                <p class="text-xs text-slate-300 uppercase font-semibold mb-0.5">PRIZE</p>
                <p :class="['text-base font-bold', getPrizeColor(contest.category)]">${{ (contest.prizePool || 0).toLocaleString() }}</p>
              </div>
              <div>
                <p class="text-xs text-slate-300 uppercase font-semibold mb-0.5">SLOTS</p>
                <p class="text-base font-bold text-white">{{ contest.maxParticipants || 0 }}</p>
              </div>
              <div>
                <p class="text-xs text-slate-300 uppercase font-semibold mb-0.5">DURATION</p>
                <p class="text-base font-bold text-white">{{ getDuration(contest.startTime, contest.endTime) }}</p>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="flex gap-2">
              <button
                v-if="contest.status !== 'PUBLISHED'"
                class="flex-1 flex items-center justify-center gap-1.5 px-3 py-2 bg-cyan-500 hover:bg-cyan-400 text-slate-900 rounded-lg text-sm font-semibold transition"
                @click.stop="publishContest(contest)"
              >
                🚀 Publish
              </button>
              <button
                v-else
                class="flex-1 flex items-center justify-center gap-1.5 px-3 py-2 bg-green-500 text-white rounded-lg text-sm font-semibold"
                disabled
              >
                ✓ Published
              </button>
              <button
                class="flex-1 flex items-center justify-center gap-1.5 px-3 py-2 bg-white/15 hover:bg-white/25 text-white rounded-lg text-sm font-medium transition border border-white/20"
                @click.stop="editContest(contest)"
              >
                ✏️ Edit
              </button>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else-if="!loading" class="text-center py-16 mb-10">
          <p class="text-slate-500 text-base">No contests found matching your filters.</p>
        </div>

        <!-- All Contests Table -->
        <div class="bg-slate-800/50 border border-slate-700 rounded-xl overflow-hidden">
          <div class="flex items-center justify-between px-6 py-4 border-b border-slate-700">
            <h3 class="text-white font-semibold text-base">All Contests</h3>
            <span class="text-slate-400 text-sm">{{ contests.length }} total</span>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b border-slate-700">
                  <th class="text-left text-xs font-semibold text-slate-400 uppercase tracking-wider px-6 py-3">CONTEST</th>
                  <th class="text-left text-xs font-semibold text-slate-400 uppercase tracking-wider px-4 py-3">STATUS</th>
                  <th class="text-left text-xs font-semibold text-slate-400 uppercase tracking-wider px-4 py-3">ENTRIES</th>
                  <th class="text-left text-xs font-semibold text-slate-400 uppercase tracking-wider px-4 py-3">PRIZE POOL</th>
                  <th class="text-left text-xs font-semibold text-slate-400 uppercase tracking-wider px-4 py-3">ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="loading && contests.length === 0">
                  <td colspan="5" class="text-center text-slate-500 py-8 text-sm">Loading...</td>
                </tr>
                <tr v-else-if="contests.length === 0">
                  <td colspan="5" class="text-center text-slate-500 py-8 text-sm">No contests yet.</td>
                </tr>
                <tr
                  v-for="contest in contests"
                  :key="'row-' + contest.id"
                  class="border-b border-slate-700/50 hover:bg-slate-800/40 transition"
                >
                  <td class="px-6 py-4">
                    <div>
                      <p class="text-white font-medium text-sm">{{ contest.title }}</p>
                      <p class="text-slate-400 text-xs mt-0.5">{{ getCategoryLabel(contest.category) }}</p>
                    </div>
                  </td>
                  <td class="px-4 py-4">
                    <span :class="['inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold', getTableStatusClass(contest)]">
                      {{ getTableStatus(contest) }}
                    </span>
                  </td>
                  <td class="px-4 py-4 text-slate-300 text-sm">{{ contest.participantsCount || 0 }}</td>
                  <td class="px-4 py-4 text-slate-300 text-sm font-medium">${{ (contest.prizePool || 0).toLocaleString() }}</td>
                  <td class="px-4 py-4">
                    <div class="flex gap-2">
                      <button
                        v-if="contest.status !== 'PUBLISHED'"
                        class="px-3 py-1.5 bg-cyan-500/20 hover:bg-cyan-500/30 text-cyan-300 border border-cyan-500/30 rounded text-xs font-medium transition"
                        @click="publishContest(contest)"
                      >
                        Publish
                      </button>
                      <button
                        class="px-3 py-1.5 bg-slate-700 hover:bg-slate-600 text-slate-200 rounded text-xs font-medium transition"
                        @click="editContest(contest)"
                      >
                        Edit
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </main>

    <!-- Create Contest Modal -->
    <ContestCreateModal
      v-if="showCreateModal"
      @close="showCreateModal = false"
      @success="handleContestCreated"
    />

    <!-- Regenerate Modal -->
    <div v-if="showRegenerateModal" class="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4">
      <div class="bg-slate-800 border border-slate-700 rounded-xl p-8 max-w-md w-full">
        <h2 class="text-xl font-bold text-white mb-3">Regenerate Event Cards</h2>
        <p class="text-slate-400 text-sm mb-6">
          AI will analyze your platform trends and generate new upcoming event suggestions. This may take a few moments.
        </p>
        <div class="flex gap-3">
          <button
            class="flex-1 px-4 py-2.5 bg-slate-700 hover:bg-slate-600 text-white rounded-lg font-medium transition text-sm"
            @click="showRegenerateModal = false"
          >
            Cancel
          </button>
          <button
            class="flex-1 px-4 py-2.5 bg-purple-600 hover:bg-purple-700 text-white rounded-lg font-medium transition text-sm"
            @click="regenerateContests"
          >
            Regenerate
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { adminContestAPI } from '@/api'
import ContestCreateModal from '@/components/admin/ContestCreateModal.vue'

const router = useRouter()
const contests = ref([])
const loading = ref(false)
const error = ref('')
const success = ref('')
const activeTab = ref('all')
const showCreateModal = ref(false)
const showRegenerateModal = ref(false)
const searchQuery = ref('')
const categoryFilter = ref('')
const sortOrder = ref('newest')

const statusTabs = [
  { label: 'All Contests', value: 'all' },
  { label: 'Live', value: 'live' },
  { label: 'Upcoming', value: 'upcoming' },
  { label: 'Drafts', value: 'drafts' },
  { label: 'Ended', value: 'ended' }
]

const filteredContests = computed(() => {
  const now = new Date()
  return contests.value.filter((contest) => {
    const startTime = new Date(contest.startTime)
    const endTime = new Date(contest.endTime)

    if (activeTab.value === 'live') {
      return now >= startTime && now <= endTime
    } else if (activeTab.value === 'upcoming') {
      return now < startTime
    } else if (activeTab.value === 'drafts') {
      return contest.status === 'DRAFT'
    } else if (activeTab.value === 'ended') {
      return now > endTime
    }
    return true
  })
})

const displayContests = computed(() => {
  let result = filteredContests.value

  if (searchQuery.value.trim()) {
    const q = searchQuery.value.trim().toLowerCase()
    result = result.filter(
      (c) =>
        c.title?.toLowerCase().includes(q) ||
        getCategoryLabel(c.category).toLowerCase().includes(q) ||
        c.description?.toLowerCase().includes(q)
    )
  }

  if (categoryFilter.value) {
    result = result.filter((c) => c.category?.toLowerCase() === categoryFilter.value)
  }

  result = [...result]
  if (sortOrder.value === 'newest') {
    result.sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
  } else if (sortOrder.value === 'oldest') {
    result.sort((a, b) => new Date(a.startTime) - new Date(b.startTime))
  } else if (sortOrder.value === 'prize_desc') {
    result.sort((a, b) => (b.prizePool || 0) - (a.prizePool || 0))
  } else if (sortOrder.value === 'prize_asc') {
    result.sort((a, b) => (a.prizePool || 0) - (b.prizePool || 0))
  }
  return result
})

const getStartsInDays = (startTime) => {
  if (!startTime) return '?'
  const days = Math.ceil((new Date(startTime) - new Date()) / (1000 * 60 * 60 * 24))
  return Math.max(days, 0)
}

const getDuration = (start, end) => {
  if (!start || !end) return 'N/A'
  const days = Math.ceil((new Date(end) - new Date(start)) / (1000 * 60 * 60 * 24))
  return `${days} days`
}

const getContestIcon = (category) => {
  const icons = {
    'rl': '🤖',
    'vision': '👁️',
    'nlp': '🗣️',
    'audio': '🎤',
    'finance': '💰',
    'tabular': '📊',
    'timeseries': '⚡'
  }
  return icons[category?.toLowerCase()] || '🎯'
}

const getGradientClass = (category) => {
  const classes = {
    'rl': 'bg-gradient-to-br from-blue-900 via-blue-800 to-blue-700',
    'vision': 'bg-gradient-to-br from-teal-900 via-teal-800 to-teal-700',
    'nlp': 'bg-gradient-to-br from-purple-900 via-purple-800 to-purple-700',
    'audio': 'bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-700',
    'finance': 'bg-gradient-to-br from-red-900 via-red-800 to-red-700',
    'tabular': 'bg-gradient-to-br from-orange-900 via-orange-800 to-orange-700',
    'timeseries': 'bg-gradient-to-br from-green-900 via-green-800 to-green-700'
  }
  return classes[category?.toLowerCase()] || 'bg-gradient-to-br from-slate-900 via-slate-800 to-slate-700'
}

const getGradientColor = (category) => {
  const colors = {
    'rl': '#1e3a8a',
    'vision': '#134e4a',
    'nlp': '#3f0f5c',
    'audio': '#3f0f5c',
    'finance': '#7c2d12',
    'tabular': '#7c2d12',
    'timeseries': '#15803d'
  }
  return colors[category?.toLowerCase()] || '#1e293b'
}

const getCategoryLabel = (category) => {
  const labels = {
    'rl': 'Reinforcement Learning',
    'vision': 'Computer Vision',
    'nlp': 'NLP',
    'audio': 'Audio & Speech',
    'finance': 'Finance',
    'tabular': 'Tabular Data',
    'timeseries': 'Time Series'
  }
  return labels[category?.toLowerCase()] || 'General'
}

const getContestTags = (contest) => {
  const tags = []
  if (contest.contestType) tags.push(contest.contestType)
  if (contest.difficulty) tags.push(contest.difficulty)
  return tags.slice(0, 3)
}

const getStatusBadgeClass = (status) => {
  return {
    DRAFT: 'bg-yellow-500/30 text-yellow-200',
    PUBLISHED: 'bg-green-500/30 text-green-200',
    ARCHIVED: 'bg-red-500/30 text-red-200'
  }[status] || 'bg-gray-500/30 text-gray-200'
}

const getCategoryColor = (category) => {
  const colors = {
    rl: 'text-blue-400',
    vision: 'text-cyan-400',
    nlp: 'text-blue-300',
    audio: 'text-pink-400',
    finance: 'text-red-400',
    tabular: 'text-orange-400',
    timeseries: 'text-green-400'
  }
  return colors[category?.toLowerCase()] || 'text-slate-300'
}

const getPrizeColor = (category) => {
  const colors = {
    rl: 'text-purple-300',
    vision: 'text-cyan-300',
    nlp: 'text-blue-300',
    audio: 'text-pink-300',
    finance: 'text-red-300',
    tabular: 'text-orange-300',
    timeseries: 'text-green-300'
  }
  return colors[category?.toLowerCase()] || 'text-white'
}

const getTableStatus = (contest) => {
  const now = new Date()
  const start = new Date(contest.startTime)
  const end = new Date(contest.endTime)
  if (contest.status === 'DRAFT') return 'Draft'
  if (now > end) return 'Ended'
  if (now >= start && now <= end) return 'Live'
  return 'Upcoming'
}

const getTableStatusClass = (contest) => {
  const status = getTableStatus(contest)
  return {
    Live: 'bg-green-500/20 text-green-300 border border-green-500/30',
    Upcoming: 'bg-blue-500/20 text-blue-300 border border-blue-500/30',
    Draft: 'bg-yellow-500/20 text-yellow-300 border border-yellow-500/30',
    Ended: 'bg-slate-500/20 text-slate-300 border border-slate-500/30'
  }[status] || 'bg-slate-500/20 text-slate-300'
}

const getContestCountByStatus = (status) => {
  const now = new Date()
  if (status === 'all') return contests.value.length
  if (status === 'live') {
    return contests.value.filter((c) => {
      const start = new Date(c.startTime)
      const end = new Date(c.endTime)
      return now >= start && now <= end
    }).length
  }
  if (status === 'upcoming') {
    return contests.value.filter((c) => now < new Date(c.startTime)).length
  }
  if (status === 'drafts') {
    return contests.value.filter((c) => c.status === 'DRAFT').length
  }
  if (status === 'ended') {
    return contests.value.filter((c) => now > new Date(c.endTime)).length
  }
  return 0
}

const loadContests = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await adminContestAPI.list({ page: 0, size: 100 })
    const payload = response?.data?.data
    if (Array.isArray(payload?.content)) {
      contests.value = payload.content
    } else if (Array.isArray(payload)) {
      contests.value = payload
    } else {
      contests.value = []
    }
  } catch (err) {
    error.value = err?.response?.data?.error || err?.message || 'Failed to load contests'
  } finally {
    loading.value = false
  }
}

const publishContest = async (contest) => {
  if (!confirm(`Publish contest "${contest.title}"?`)) return

  try {
    await adminContestAPI.update(contest.id, { status: 'PUBLISHED' })
    success.value = 'Contest published successfully'
    await loadContests()
    setTimeout(() => (success.value = ''), 3000)
  } catch (err) {
    error.value = err?.response?.data?.error || 'Failed to publish contest'
  }
}

const editContest = (contest) => {
  router.push({ name: 'CreateContest', params: { id: contest.id } })
}

const regenerateContests = async () => {
  showRegenerateModal.value = false
  // TODO: Implement AI regeneration endpoint
  alert('AI regeneration feature coming soon')
}

const handleContestCreated = async () => {
  showCreateModal.value = false
  success.value = 'Contest created successfully'
  await loadContests()
  setTimeout(() => (success.value = ''), 3000)
}

onMounted(() => {
  loadContests()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
