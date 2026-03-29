<template>
  <div class="p-6 max-w-7xl mx-auto space-y-6">
    <div>
      <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100">Reports & Analytics</h1>
      <p class="text-gray-600 dark:text-gray-400 mt-1">View detailed reports and analytics</p>
    </div>

    <!-- Date Range Filter -->
    <div class="card p-4 flex gap-4 items-end">
      <div class="flex-1">
        <label class="text-sm font-medium">Date Range</label>
        <div class="flex gap-2 mt-2">
          <input v-model="dateRange.start" type="date" class="input" />
          <input v-model="dateRange.end" type="date" class="input" />
        </div>
      </div>
      <button @click="applyFilter" class="btn-primary">Apply Filter</button>
    </div>

    <!-- Key Metrics -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div class="card p-6">
        <p class="text-gray-600 dark:text-gray-400 text-sm font-medium">Total Users</p>
        <p class="text-3xl font-bold text-primary-600 mt-2">{{ metrics.totalUsers }}</p>
        <p class="text-xs text-gray-500 mt-2">↑ 12% from last month</p>
      </div>

      <div class="card p-6">
        <p class="text-gray-600 dark:text-gray-400 text-sm font-medium">Active Contests</p>
        <p class="text-3xl font-bold text-primary-600 mt-2">{{ metrics.activeContests }}</p>
        <p class="text-xs text-gray-500 mt-2">{{ metrics.totalSubmissions }} total submissions</p>
      </div>

      <div class="card p-6">
        <p class="text-gray-600 dark:text-gray-400 text-sm font-medium">Success Rate</p>
        <p class="text-3xl font-bold text-green-600 mt-2">{{ metrics.successRate }}%</p>
        <p class="text-xs text-gray-500 mt-2">Submission success ratio</p>
      </div>

      <div class="card p-6">
        <p class="text-gray-600 dark:text-gray-400 text-sm font-medium">Platform Revenue</p>
        <p class="text-3xl font-bold text-blue-600 mt-2">Rs. {{ metrics.totalRevenue }}</p>
        <p class="text-xs text-gray-500 mt-2">From contest registrations</p>
      </div>
    </div>

    <!-- Tabs -->
    <div class="flex gap-2 border-b border-gray-200 dark:border-dark-700">
      <button
        @click="activeTab = 'users'"
        :class="activeTab === 'users' ? 'border-b-2 border-primary-600 text-primary-600 font-semibold' : 'text-gray-600'"
        class="px-4 py-3 transition"
      >
        👥 User Reports
      </button>
      <button
        @click="activeTab = 'contests'"
        :class="activeTab === 'contests' ? 'border-b-2 border-primary-600 text-primary-600 font-semibold' : 'text-gray-600'"
        class="px-4 py-3 transition"
      >
        🎯 Contest Reports
      </button>
      <button
        @click="activeTab = 'submissions'"
        :class="activeTab === 'submissions' ? 'border-b-2 border-primary-600 text-primary-600 font-semibold' : 'text-gray-600'"
        class="px-4 py-3 transition"
      >
        📝 Submission Reports
      </button>
    </div>

    <!-- User Reports Tab -->
    <div v-if="activeTab === 'users'" class="space-y-4">
      <div class="card overflow-hidden">
        <div class="p-6">
          <h3 class="text-lg font-semibold mb-4">Top Performing Users</h3>
          <div class="table-responsive">
            <table class="table w-full">
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>User Name</th>
                  <th>Email</th>
                  <th>Contests Joined</th>
                  <th>Total Submissions</th>
                  <th>Success Rate</th>
                  <th>Points</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(user, idx) in userReports" :key="idx">
                  <td class="font-bold">{{ idx + 1 }}</td>
                  <td class="font-medium">{{ user.name }}</td>
                  <td class="text-sm">{{ user.email }}</td>
                  <td>{{ user.contestsJoined }}</td>
                  <td>{{ user.totalSubmissions }}</td>
                  <td>
                    <span class="badge" :class="user.successRate >= 80 ? 'badge-success' : 'badge-warning'">
                      {{ user.successRate }}%
                    </span>
                  </td>
                  <td class="font-bold text-primary-600">{{ user.points }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- Contest Reports Tab -->
    <div v-if="activeTab === 'contests'" class="space-y-4">
      <div class="card overflow-hidden">
        <div class="p-6">
          <h3 class="text-lg font-semibold mb-4">Contest Performance</h3>
          <div class="table-responsive">
            <table class="table w-full">
              <thead>
                <tr>
                  <th>Contest Name</th>
                  <th>Status</th>
                  <th>Registrations</th>
                  <th>Submissions</th>
                  <th>Avg Runtime</th>
                  <th>Success Rate</th>
                  <th>Revenue</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(contest, idx) in contestReports" :key="idx">
                  <td class="font-medium">{{ contest.name }}</td>
                  <td>
                    <span class="badge" :class="statusBadgeClass(contest.status)">{{ contest.status }}</span>
                  </td>
                  <td>{{ contest.registrations }}</td>
                  <td>{{ contest.submissions }}</td>
                  <td>{{ contest.avgRuntime }}ms</td>
                  <td>
                    <span class="badge badge-success">{{ contest.successRate }}%</span>
                  </td>
                  <td class="font-bold text-blue-600">Rs. {{ contest.revenue }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- Submission Reports Tab -->
    <div v-if="activeTab === 'submissions'" class="space-y-4">
      <div class="card overflow-hidden">
        <div class="p-6">
          <h3 class="text-lg font-semibold mb-4">Recent Submissions</h3>
          <div class="table-responsive">
            <table class="table w-full">
              <thead>
                <tr>
                  <th>User</th>
                  <th>Contest</th>
                  <th>Problem</th>
                  <th>Language</th>
                  <th>Status</th>
                  <th>Runtime</th>
                  <th>Memory</th>
                  <th>Submitted</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(sub, idx) in submissionReports" :key="idx">
                  <td class="text-sm">{{ sub.user }}</td>
                  <td class="text-sm font-medium">{{ sub.contest }}</td>
                  <td class="text-sm">{{ sub.problem }}</td>
                  <td class="text-sm">{{ sub.language }}</td>
                  <td>
                    <span class="badge" :class="sub.status === 'ACCEPTED' ? 'badge-success' : 'badge-danger'">
                      {{ sub.status }}
                    </span>
                  </td>
                  <td class="text-sm">{{ sub.runtime }}ms</td>
                  <td class="text-sm">{{ sub.memory }}MB</td>
                  <td class="text-sm">{{ sub.timestamp }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const activeTab = ref('users')
const dateRange = ref({
  start: '2026-03-01',
  end: '2026-03-27'
})

const metrics = ref({
  totalUsers: 1248,
  activeContests: 12,
  totalSubmissions: 8547,
  successRate: 76,
  totalRevenue: '15,240.00'
})

// Mock User Reports
const userReports = ref([
  {
    name: 'Sowmya Kumar',
    email: 'sowmya@example.com',
    contestsJoined: 8,
    totalSubmissions: 245,
    successRate: 92,
    points: 8500
  },
  {
    name: 'Raj Patel',
    email: 'raj.patel@example.com',
    contestsJoined: 6,
    totalSubmissions: 189,
    successRate: 85,
    points: 7200
  },
  {
    name: 'Priya Sharma',
    email: 'priya.sharma@example.com',
    contestsJoined: 7,
    totalSubmissions: 212,
    successRate: 88,
    points: 7950
  },
  {
    name: 'Arun Singh',
    email: 'arun.singh@example.com',
    contestsJoined: 5,
    totalSubmissions: 156,
    successRate: 79,
    points: 6800
  },
  {
    name: 'Neha Gupta',
    email: 'neha.gupta@example.com',
    contestsJoined: 9,
    totalSubmissions: 267,
    successRate: 81,
    points: 8100
  },
  {
    name: 'Vikram Reddy',
    email: 'vikram@example.com',
    contestsJoined: 4,
    totalSubmissions: 128,
    successRate: 74,
    points: 6200
  }
])

// Mock Contest Reports
const contestReports = ref([
  {
    name: 'JiraLite Clone',
    status: 'PUBLISHED',
    registrations: 234,
    submissions: 567,
    avgRuntime: 145,
    successRate: 82,
    revenue: 2340
  },
  {
    name: 'CodeLudge Arena',
    status: 'PUBLISHED',
    registrations: 189,
    submissions: 423,
    avgRuntime: 128,
    successRate: 78,
    revenue: 1890
  },
  {
    name: 'PayFlow Contest Checkout',
    status: 'PUBLISHED',
    registrations: 156,
    submissions: 298,
    avgRuntime: 167,
    successRate: 71,
    revenue: 1560
  },
  {
    name: 'Realtime Rankboard',
    status: 'PUBLISHED',
    registrations: 212,
    submissions: 489,
    avgRuntime: 156,
    successRate: 75,
    revenue: 2120
  },
  {
    name: 'Sample Contest',
    status: 'DRAFT',
    registrations: 45,
    submissions: 98,
    avgRuntime: 134,
    successRate: 69,
    revenue: 450
  }
])

// Mock Submission Reports
const submissionReports = ref([
  {
    user: 'Sowmya Kumar',
    contest: 'JiraLite Clone',
    problem: 'Issue Tracker',
    language: 'Java',
    status: 'ACCEPTED',
    runtime: 145,
    memory: 64,
    timestamp: '2026-03-27 14:32'
  },
  {
    user: 'Raj Patel',
    contest: 'CodeLudge Arena',
    problem: 'Binary Search',
    language: 'Python',
    status: 'ACCEPTED',
    runtime: 89,
    memory: 32,
    timestamp: '2026-03-27 14:28'
  },
  {
    user: 'Priya Sharma',
    contest: 'PayFlow Checkout',
    problem: 'Payment Gateway',
    language: 'JavaScript',
    status: 'REJECTED',
    runtime: 267,
    memory: 128,
    timestamp: '2026-03-27 14:25'
  },
  {
    user: 'Arun Singh',
    contest: 'Realtime Rankboard',
    problem: 'Leaderboard Sync',
    language: 'Go',
    status: 'ACCEPTED',
    runtime: 156,
    memory: 48,
    timestamp: '2026-03-27 14:20'
  },
  {
    user: 'Neha Gupta',
    contest: 'JiraLite Clone',
    problem: 'Dashboard UI',
    language: 'Vue',
    status: 'ACCEPTED',
    runtime: 134,
    memory: 72,
    timestamp: '2026-03-27 14:15'
  },
  {
    user: 'Vikram Reddy',
    contest: 'CodeLudge Arena',
    problem: 'Sorting Algorithm',
    language: 'C++',
    status: 'ACCEPTED',
    runtime: 112,
    memory: 24,
    timestamp: '2026-03-27 14:10'
  }
])

const statusBadgeClass = (status) => {
  return {
    DRAFT: 'badge-warning',
    PUBLISHED: 'badge-success',
    ARCHIVED: 'badge-danger'
  }[status] || 'badge-info'
}

const applyFilter = () => {
  console.log('Applying filter:', dateRange.value)
  // TODO: Implement actual filtering logic
  alert(`Filtering reports from ${dateRange.value.start} to ${dateRange.value.end}`)
}
</script>
