import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import { 
  authAPI, contestAPI, userAPI, leaderboardAPI, 
  organizationAPI, paymentAPI, notificationAPI, adminContestAPI
} from '@/api'
import { clearContestRegistrations } from '@/utils/localRegistrations'

// ============================================================================
// USER STORE - Authentication & User Profile
// ============================================================================
export const useUserStore = defineStore('user', () => {
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const token = ref(localStorage.getItem('accessToken'))
  const refreshToken = ref(localStorage.getItem('refreshToken'))
  const tenantId = ref(localStorage.getItem('tenantId') || 'jirathon')
  
  if (token.value) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
  }
  
  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => {
    const roles = user.value?.roles || []
    return roles.includes('ADMIN') || roles.includes('SUPER_ADMIN') || 
           roles.includes('ROLE_ADMIN') || roles.includes('ROLE_SUPER_ADMIN')
  })

  const setSession = (authPayload) => {
    token.value = authPayload.accessToken
    refreshToken.value = authPayload.refreshToken
    user.value = authPayload.user
    localStorage.setItem('accessToken', authPayload.accessToken)
    localStorage.setItem('refreshToken', authPayload.refreshToken || '')
    localStorage.setItem('user', JSON.stringify(authPayload.user || null))
    localStorage.setItem('tenantId', authPayload.user?.tenantId || 'jirathon')
    axios.defaults.headers.common['Authorization'] = `Bearer ${authPayload.accessToken}`
  }

  const login = async (email, password) => {
    try {
      const response = await authAPI.login(email, password)
      setSession(response.data.data)
      return { ok: true, data: response.data.data }
    } catch (error) {
      const message = error.response?.data?.error || error.response?.data?.message || error.message
      console.error('Login failed:', error)
      return { ok: false, message }
    }
  }

  const register = async (payload) => {
    try {
      const response = await authAPI.register(payload)
      setSession(response.data.data)
      return { ok: true, data: response.data.data }
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
      // Ignore logout failures and clear local state regardless
    }
    token.value = null
    user.value = null
    refreshToken.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
    clearContestRegistrations()
    delete axios.defaults.headers.common['Authorization']
  }

  const updateProfile = async (data) => {
    try {
      const response = await userAPI.updateMe(data)
      user.value = response.data.data
      localStorage.setItem('user', JSON.stringify(user.value))
      return { ok: true, data: response.data.data }
    } catch (error) {
      return {
        ok: false,
        message: error.response?.data?.error || error.response?.data?.message || error.message
      }
    }
  }

  const fetchUser = async () => {
    try {
      const response = await userAPI.getMe()
      user.value = response.data.data
      localStorage.setItem('user', JSON.stringify(user.value))
      return { ok: true }
    } catch (error) {
      console.error('Failed to fetch user:', error)
      if (error.response?.status === 401) {
        await logout()
      }
      return { ok: false }
    }
  }

  return {
    user,
    token,
    refreshToken,
    tenantId,
    isAuthenticated,
    isAdmin,
    login,
    register,
    logout,
    updateProfile,
    fetchUser,
    setSession
  }
})

// ============================================================================
// CONTEST STORE - Contest Management
// ============================================================================
export const useContestStore = defineStore('contest', () => {
  const contests = ref([])
  const currentContest = ref(null)
  const myRegistrations = ref(new Map()) // contestId -> registration
  const loading = ref(false)
  const error = ref(null)
  const page = ref(0)
  const size = ref(20)
  const totalPages = ref(0)

  const fetchContests = async (params = {}) => {
    loading.value = true
    error.value = null
    try {
      const response = await contestAPI.list({
        page: params.page || page.value,
        size: params.size || size.value,
        ...params
      })
      contests.value = response.data.data?.content || response.data.data || []
      totalPages.value = response.data.data?.totalPages || 0
      return { ok: true }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      console.error('Failed to fetch contests:', err)
      return { ok: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  const getContest = async (contestId) => {
    try {
      const response = await contestAPI.getById(contestId)
      currentContest.value = response.data.data
      return { ok: true, data: response.data.data }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      console.error('Failed to fetch contest:', err)
      return { ok: false, message: error.value }
    }
  }

  const registerForContest = async (contestId) => {
    try {
      const response = await contestAPI.register(contestId)
      myRegistrations.value.set(contestId, response.data.data)
      return { ok: true, data: response.data.data }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    }
  }

  const getMyRegistration = async (contestId) => {
    try {
      const response = await contestAPI.getMyRegistration(contestId, { skipAuthRedirect: true })
      myRegistrations.value.set(contestId, response.data.data)
      return { ok: true, data: response.data.data }
    } catch (err) {
      // 401 is expected if not registered
      if (err.response?.status !== 401) {
        console.error('Failed to fetch registration:', err)
      }
      return { ok: false }
    }
  }

  const updateRegistrationDetails = async (registrationId, data) => {
    try {
      const response = await contestAPI.updateRegistrationDetails(registrationId, data)
      return { ok: true, data: response.data.data }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    }
  }

  return {
    contests,
    currentContest,
    myRegistrations,
    loading,
    error,
    page,
    size,
    totalPages,
    fetchContests,
    getContest,
    registerForContest,
    getMyRegistration,
    updateRegistrationDetails
  }
})

// ============================================================================
// ADMIN CONTEST STORE - Admin Contest Management
// ============================================================================
export const useAdminContestStore = defineStore('adminContest', () => {
  const contests = ref([])
  const currentContest = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const supportedLanguages = ref([])

  const fetchContests = async (params = {}) => {
    loading.value = true
    error.value = null
    try {
      const response = await adminContestAPI.list(params)
      contests.value = response.data.data?.content || response.data.data || []
      return { ok: true }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  const getContest = async (contestId) => {
    try {
      const response = await adminContestAPI.getById(contestId)
      currentContest.value = response.data.data
      return { ok: true, data: response.data.data }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    }
  }

  const createContest = async (data) => {
    try {
      const response = await adminContestAPI.create(data)
      contests.value.push(response.data.data)
      return { ok: true, data: response.data.data }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    }
  }

  const updateContest = async (contestId, data) => {
    try {
      const response = await adminContestAPI.update(contestId, data)
      const index = contests.value.findIndex(c => c.id === contestId)
      if (index !== -1) {
        contests.value[index] = response.data.data
      }
      if (currentContest.value?.id === contestId) {
        currentContest.value = response.data.data
      }
      return { ok: true, data: response.data.data }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    }
  }

  const deleteContest = async (contestId) => {
    try {
      await adminContestAPI.delete(contestId)
      contests.value = contests.value.filter(c => c.id !== contestId)
      return { ok: true }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    }
  }

  const getSupportedLanguages = async () => {
    try {
      const response = await adminContestAPI.getSupportedLanguages()
      supportedLanguages.value = response.data.data || []
      return { ok: true }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    }
  }

  return {
    contests,
    currentContest,
    loading,
    error,
    supportedLanguages,
    fetchContests,
    getContest,
    createContest,
    updateContest,
    deleteContest,
    getSupportedLanguages
  }
})

// ============================================================================
// LEADERBOARD STORE - Leaderboard Data
// ============================================================================
export const useLeaderboardStore = defineStore('leaderboard', () => {
  const globalLeaderboard = ref([])
  const organizationLeaderboard = ref([])
  const userRank = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const offset = ref(0)
  const limit = ref(20)

  const fetchGlobalLeaderboard = async (params = {}) => {
    loading.value = true
    error.value = null
    try {
      const response = await leaderboardAPI.getGlobal({
        offset: params.offset || offset.value,
        limit: params.limit || limit.value
      })
      globalLeaderboard.value = response.data.data?.entries || []
      return { ok: true }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      console.error('Failed to fetch global leaderboard:', err)
      return { ok: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  const fetchOrganizationLeaderboard = async (organizationId, params = {}) => {
    loading.value = true
    error.value = null
    try {
      const response = await leaderboardAPI.getOrganization(organizationId, {
        offset: params.offset || offset.value,
        limit: params.limit || limit.value
      })
      organizationLeaderboard.value = response.data.data?.entries || []
      return { ok: true }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  const fetchUserRank = async (userId) => {
    try {
      const response = await leaderboardAPI.getGlobalUserRank(userId)
      userRank.value = response.data.data
      return { ok: true }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      console.error('Failed to fetch user rank:', err)
      return { ok: false }
    }
  }

  return {
    globalLeaderboard,
    organizationLeaderboard,
    userRank,
    loading,
    error,
    offset,
    limit,
    fetchGlobalLeaderboard,
    fetchOrganizationLeaderboard,
    fetchUserRank
  }
})

// ============================================================================
// ORGANIZATION STORE - Organization Management
// ============================================================================
export const useOrganizationStore = defineStore('organization', () => {
  const organizations = ref([])
  const currentOrganization = ref(null)
  const myOrganizations = ref([])
  const members = ref([])
  const loading = ref(false)
  const error = ref(null)

  const fetchOrganizations = async (params = {}) => {
    loading.value = true
    error.value = null
    try {
      const response = await organizationAPI.list(params)
      organizations.value = response.data.data?.content || response.data.data || []
      return { ok: true }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  const getOrganization = async (orgId) => {
    try {
      const response = await organizationAPI.getById(orgId)
      currentOrganization.value = response.data.data
      return { ok: true, data: response.data.data }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    }
  }

  const getMyOrganizations = async () => {
    try {
      const response = await organizationAPI.getMy()
      myOrganizations.value = response.data.data || []
      return { ok: true }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    }
  }

  const createOrganization = async (data) => {
    try {
      const response = await organizationAPI.create(data)
      myOrganizations.value.push(response.data.data)
      return { ok: true, data: response.data.data }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    }
  }

  const joinOrganization = async (orgId) => {
    try {
      const response = await organizationAPI.join(orgId)
      return { ok: true, data: response.data.data }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    }
  }

  const fetchMembers = async (orgId, params = {}) => {
    try {
      const response = await organizationAPI.getMembers(orgId, params)
      members.value = response.data.data?.content || response.data.data || []
      return { ok: true }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    }
  }

  return {
    organizations,
    currentOrganization,
    myOrganizations,
    members,
    loading,
    error,
    fetchOrganizations,
    getOrganization,
    getMyOrganizations,
    createOrganization,
    joinOrganization,
    fetchMembers
  }
})

// ============================================================================
// NOTIFICATION STORE - Notifications
// ============================================================================
export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref([])
  const unreadCount = ref(0)
  const loading = ref(false)
  const error = ref(null)

  const fetchNotifications = async (params = {}) => {
    loading.value = true
    error.value = null
    try {
      const response = await notificationAPI.list(params)
      notifications.value = response.data.data || []
      return { ok: true }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      console.error('Failed to fetch notifications:', err)
      return { ok: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  const fetchUnreadCount = async () => {
    try {
      const response = await notificationAPI.getUnreadCount()
      unreadCount.value = response.data.data || 0
      return { ok: true }
    } catch (err) {
      console.error('Failed to fetch unread count:', err)
      return { ok: false }
    }
  }

  const markAsRead = async (notificationId) => {
    try {
      await notificationAPI.markAsRead(notificationId)
      const notification = notifications.value.find(n => n.id === notificationId)
      if (notification) {
        notification.status = 'READ'
      }
      return { ok: true }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    }
  }

  return {
    notifications,
    unreadCount,
    loading,
    error,
    fetchNotifications,
    fetchUnreadCount,
    markAsRead
  }
})

// ============================================================================
// PAYMENT STORE - Payments
// ============================================================================
export const usePaymentStore = defineStore('payment', () => {
  const payments = ref([])
  const currentPayment = ref(null)
  const loading = ref(false)
  const error = ref(null)

  const initiatePayment = async (data) => {
    loading.value = true
    error.value = null
    try {
      const response = await paymentAPI.initiatePayment(data)
      currentPayment.value = response.data.data
      return { ok: true, data: response.data.data }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      console.error('Failed to initiate payment:', err)
      return { ok: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  const getPaymentStatus = async (transactionId) => {
    try {
      const response = await paymentAPI.getByTransactionId(transactionId)
      currentPayment.value = response.data.data
      return { ok: true, data: response.data.data }
    } catch (err) {
      error.value = err.response?.data?.error || err.message
      return { ok: false, message: error.value }
    }
  }

  return {
    payments,
    currentPayment,
    loading,
    error,
    initiatePayment,
    getPaymentStatus
  }
})
