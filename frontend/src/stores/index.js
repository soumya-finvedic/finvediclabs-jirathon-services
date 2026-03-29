import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import { authAPI } from '@/api'
import { clearContestRegistrations } from '@/utils/localRegistrations'

export const useUserStore = defineStore('user', () => {
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const token = ref(localStorage.getItem('accessToken'))
  if (token.value) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
  }
  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => {
    const roles = user.value?.roles || []
    return roles.includes('ADMIN') || roles.includes('SUPER_ADMIN') || roles.includes('ROLE_ADMIN') || roles.includes('ROLE_SUPER_ADMIN')
  })

  const setSession = (authPayload) => {
    token.value = authPayload.accessToken
    user.value = authPayload.user
    localStorage.setItem('accessToken', authPayload.accessToken)
    localStorage.setItem('refreshToken', authPayload.refreshToken || '')
    localStorage.setItem('user', JSON.stringify(authPayload.user || null))
    axios.defaults.headers.common['Authorization'] = `Bearer ${authPayload.accessToken}`
  }

  const login = async (email, password) => {
    try {
      const response = await authAPI.login(email, password)
      setSession(response.data.data)
      return true
    } catch (error) {
      console.error('Login failed:', error)
      return false
    }
  }

  const register = async (payload) => {
    try {
      const response = await authAPI.register(payload)
      setSession(response.data.data)
      return { ok: true }
    } catch (error) {
      return {
        ok: false,
        message: error.response?.data?.error || error.response?.data?.message || error.message
      }
    }
  }

  const logout = async () => {
    try {
      await authAPI.logout()
    } catch (_) {
      // Ignore logout failures and clear local state regardless.
    }
    token.value = null
    user.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
    clearContestRegistrations()
    delete axios.defaults.headers.common['Authorization']
  }

  const fetchUser = async () => {
    try {
      const response = await axios.get('/api/auth/me')
      user.value = response.data.data
      localStorage.setItem('user', JSON.stringify(user.value))
    } catch (error) {
      console.error('Failed to fetch user:', error)
      logout()
    }
  }

  return {
    user,
    token,
    isAuthenticated,
    isAdmin,
    login,
    register,
    logout,
    fetchUser
  }
})

export const useContestStore = defineStore('contest', () => {
  const contests = ref([])
  const currentContest = ref(null)
  const loading = ref(false)
  const error = ref(null)

  const fetchContests = async () => {
    loading.value = true
    try {
      const response = await axios.get('/api/contests')
      contests.value = response.data.data
      error.value = null
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  const fetchContest = async (id) => {
    loading.value = true
    try {
      const response = await axios.get(`/api/contests/${id}`)
      currentContest.value = response.data.data
      error.value = null
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  const createContest = async (data) => {
    try {
      const response = await axios.post('/api/contests', data)
      contests.value.push(response.data.data)
      return response.data.data
    } catch (err) {
      error.value = err.message
      throw err
    }
  }

  const updateContest = async (id, data) => {
    try {
      const response = await axios.put(`/api/contests/${id}`, data)
      const index = contests.value.findIndex(c => c.id === id)
      if (index !== -1) {
        contests.value[index] = response.data.data
      }
      if (currentContest.value?.id === id) {
        currentContest.value = response.data.data
      }
      return response.data.data
    } catch (err) {
      error.value = err.message
      throw err
    }
  }

  return {
    contests,
    currentContest,
    loading,
    error,
    fetchContests,
    fetchContest,
    createContest,
    updateContest
  }
})

export const useLeaderboardStore = defineStore('leaderboard', () => {
  const globalLeaderboard = ref([])
  const contestLeaderboard = ref([])
  const userRank = ref(null)
  const loading = ref(false)

  const fetchGlobalLeaderboard = async () => {
    loading.value = true
    try {
      const response = await axios.get('/api/leaderboards/global')
      globalLeaderboard.value = response.data.data
    } catch (error) {
      console.error('Failed to fetch leaderboard:', error)
    } finally {
      loading.value = false
    }
  }

  const fetchContestLeaderboard = async (contestId) => {
    loading.value = true
    try {
      const response = await axios.get(`/api/leaderboards/contests/${contestId}`)
      contestLeaderboard.value = response.data.data
    } catch (error) {
      console.error('Failed to fetch contest leaderboard:', error)
    } finally {
      loading.value = false
    }
  }

  const fetchUserRank = async (contestId) => {
    try {
      const response = await axios.get(`/api/leaderboards/contests/${contestId}/user-rank`)
      userRank.value = response.data.data
    } catch (error) {
      console.error('Failed to fetch user rank:', error)
    }
  }

  return {
    globalLeaderboard,
    contestLeaderboard,
    userRank,
    loading,
    fetchGlobalLeaderboard,
    fetchContestLeaderboard,
    fetchUserRank
  }
})
