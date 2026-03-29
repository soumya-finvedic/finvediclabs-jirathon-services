<template>
  <div class="overflow-x-auto">
    <table class="table w-full">
      <thead>
        <tr>
          <th>Rank</th>
          <th>User</th>
          <th>Score</th>
          <th>Time</th>
          <th>Solved</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(entry, index) in leaderboard" :key="entry.userId">
          <td>
            <span v-if="index < 3" class="text-lg">
              {{ ['🥇', '🥈', '🥉'][index] }}
            </span>
            {{ index + 1 }}
          </td>
          <td>
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 rounded-full bg-gradient-to-br from-primary-400 to-primary-600 flex items-center justify-center text-white text-sm font-bold">
                {{ entry.userName[0] }}
              </div>
              {{ entry.userName }}
            </div>
          </td>
          <td class="font-semibold">{{ entry.score }}</td>
          <td>{{ formatTime(entry.createdAt) }}</td>
          <td>{{ entry.solvedCount }}/{{ totalQuestions }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  leaderboard: {
    type: Array,
    default: () => []
  },
  totalQuestions: {
    type: Number,
    default: 0
  }
})

const formatTime = (date) => {
  return new Date(date).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
