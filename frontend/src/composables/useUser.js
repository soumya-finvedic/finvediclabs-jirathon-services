import { ref } from 'vue'
import { userAPI } from '@/api'

export const useUser = () => {
  const user = ref(null)
  const stats = ref(null)
  const loading = ref(false)
  const error = ref(null)

  const fetchProfile = async () => {
    loading.value = true
    try {
      const response = await userAPI.getProfile()
      console.log('Fetched user profile:', response.data.data)
      user.value = response.data.data
      error.value = null
    } catch (err) {
      console.error('Error fetching user profile:', err)
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  // const fetchStats = async () => {
  //   try {
  //     const response = await userAPI.getStats()
  //     stats.value = response.data.data
  //   } catch (err) {
  //     error.value = err.message
  //   }
  // }

  const updateProfile = async (data) => {
    try {
      const response = await userAPI.updateProfile(data)
      user.value = response.data.data
      return true
    } catch (err) {
      error.value = err.message
      return false
    }
  }

  return {
    user,
    stats,
    loading,
    error,
    fetchProfile,
    fetchStats,
    updateProfile
  }
}
