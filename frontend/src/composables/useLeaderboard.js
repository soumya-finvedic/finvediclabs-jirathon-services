import { ref } from 'vue'
import { leaderboardAPI } from '@/api'

export const useLeaderboard = () => {
  const leaderboard = ref([])
  const loading = ref(false)
  const error = ref(null)
  const pageSize = ref(50)
  const currentPage = ref(0)

  const fetchLeaderboard = async (containerId = null) => {
    loading.value = true
    try {
      const params = {
        offset: currentPage.value * pageSize.value,
        limit: pageSize.value
      }

      const response = containerId 
        ? await leaderboardAPI.getContestLeaderboard(containerId, params)
        : await leaderboardAPI.getGlobal(params)

      leaderboard.value = response.data.data
      error.value = null
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  const nextPage = () => {
    currentPage.value++
  }

  const prevPage = () => {
    if (currentPage.value > 0) {
      currentPage.value--
    }
  }

  return {
    leaderboard,
    loading,
    error,
    pageSize,
    currentPage,
    fetchLeaderboard,
    nextPage,
    prevPage
  }
}
