import apiClient from './client'

/**
 * JIRATHON API INTEGRATION LAYER
 * All endpoints match backend microservices at /api/v1/*
 * Response format: ApiResponse<T> with {success, message, data, error}
 */

// ============================================================================
// AUTH APIS - /api/v1/auth (Authentication & Authorization)
// ============================================================================
export const authAPI = {
  // Authentication endpoints
  register: (data) => 
    apiClient.post('/auth/register', data),
  login: (email, password) => 
    apiClient.post('/auth/login', { email, password }),
  logout: () => 
    apiClient.post('/auth/logout'),
  refreshToken: (refreshToken) =>
    apiClient.post('/auth/refresh', { refreshToken }),
  verifyOtp: (email, otp) =>
    apiClient.post('/auth/verify-otp', { email, otp }),
  resendOtp: (email, purpose = 'EMAIL_VERIFICATION') =>
    apiClient.post('/auth/resend-otp', { email, purpose }),
  
  // Password management
  forgotPassword: (email) =>
    apiClient.post('/auth/forgot-password', { email }),
  resetPassword: (token, newPassword) =>
    apiClient.post('/auth/reset-password', { token, newPassword }),
  
  // OAuth endpoints
  getOAuthUrl: (provider) => 
    apiClient.get(`/auth/oauth/${provider}/url`),
  handleOAuthCallback: (provider, code) =>
    apiClient.post(`/auth/oauth/${provider}/callback`, { code }),
  
  // Portal & current user
  getPortals: () =>
    apiClient.get('/auth/portals'),
  getCurrentUser: () =>
    apiClient.get('/auth/me'),
}

// ============================================================================
// USER APIS - /api/v1/users (User Profile & Management)
// ============================================================================
export const userAPI = {
  // Own profile
  getMe: () => 
    apiClient.get('/users/me'),
  updateMe: (data) => 
    apiClient.patch('/users/me', data),
  
  // Public user lookup
  getById: (userId) => 
    apiClient.get(`/users/${userId}`),
  search: (query, params = {}) =>
    apiClient.get('/users/search', { params: { q: query, ...params } }),
  
  // Leaderboard
  getLeaderboard: (params = {}) =>
    apiClient.get('/users/leaderboard', { params }),
  getOrganizationUsers: (orgId, params = {}) =>
    apiClient.get(`/users/organization/${orgId}`, { params }),
}

// ============================================================================
// ADMIN USER APIS - /api/v1/admin/users (Admin User Management)
// ============================================================================
export const adminUserAPI = {
  list: (params = {}) =>
    apiClient.get('/admin/users', { params }),
  getByStatus: (status, params = {}) =>
    apiClient.get(`/admin/users/status/${status}`, { params }),
  updateRoles: (userId, roles) =>
    apiClient.put('/admin/users/roles', { userId, roles }),
  updateStatus: (userId, status) =>
    apiClient.put('/admin/users/status', { userId, status }),
  delete: (userId) =>
    apiClient.delete(`/admin/users/${userId}`),
}

// ============================================================================
// CONTEST APIS - /api/v1/contests (User Contest Management)
// ============================================================================
export const contestAPI = {
  // Public endpoints
  list: (params = {}) => 
    apiClient.get('/contests', { params }),
  getById: (contestId) => 
    apiClient.get(`/contests/${contestId}`),
  
  // User registration
  register: (contestId) => 
    apiClient.post(`/contests/${contestId}/register`),
  getMyRegistration: (contestId, config = {}) =>
    apiClient.get(`/contests/${contestId}/registration/me`, { ...config }),
  updateRegistrationDetails: (registrationId, data) =>
    apiClient.put(`/contests/registrations/${registrationId}/details`, data),
  confirmPayment: (registrationId, paymentTransactionId) =>
    apiClient.post(`/contests/registrations/${registrationId}/confirm-payment`, { paymentTransactionId }),
}

// ============================================================================
// ADMIN CONTEST APIS - /api/v1/admin/contests (Admin Contest Management)
// ============================================================================
export const adminContestAPI = {
  list: (params = {}) =>
    apiClient.get('/admin/contests', { params }),
  getById: (contestId) =>
    apiClient.get(`/admin/contests/${contestId}`),
  create: (data) =>
    apiClient.post('/admin/contests', data),
  update: (contestId, data) =>
    apiClient.put(`/admin/contests/${contestId}`, data),
  delete: (contestId) =>
    apiClient.delete(`/admin/contests/${contestId}`),
  uploadBanner: (contestId, formData) =>
    apiClient.post(`/admin/contests/${contestId}/banner`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }),
  getSupportedLanguages: () =>
    apiClient.get('/admin/contests/meta/supported-languages'),
  listRegistrations: (contestId, params = {}) =>
    apiClient.get(`/admin/contests/${contestId}/registrations`, { params }),
}

// ============================================================================
// EXECUTION APIS - /api/v1/executions (Code Execution)
// ============================================================================
export const executionAPI = {
  run: (language, sourceCode, stdin = '', options = {}) => 
    apiClient.post('/executions/run', {
      language,
      sourceCode,
      stdin,
      timeLimitMs: options.timeLimitMs || 5000,
      memoryLimitMb: options.memoryLimitMb || 256,
    }),
  getLanguages: () => 
    apiClient.get('/executions/languages'),
}

// ============================================================================
// LEADERBOARD APIS - /api/v1/leaderboards (Leaderboards)
// ============================================================================
export const leaderboardAPI = {
  // Global leaderboard
  getGlobal: (params = {}) => 
    apiClient.get('/leaderboards/global', { params }),
  getGlobalUserRank: (userId) =>
    apiClient.get(`/leaderboards/global/users/${userId}/rank`),
  
  // Organization leaderboard
  getOrganization: (organizationId, params = {}) =>
    apiClient.get(`/leaderboards/organizations/${organizationId}`, { params }),
  getOrganizationUserRank: (organizationId, userId) =>
    apiClient.get(`/leaderboards/organizations/${organizationId}/users/${userId}/rank`),
}

// ============================================================================
// ORGANIZATION APIS - /api/v1/organizations (Organization Management)
// ============================================================================
export const organizationAPI = {
  // Public endpoints
  list: (params = {}) =>
    apiClient.get('/organizations', { params }),
  getById: (orgId) =>
    apiClient.get(`/organizations/${orgId}`),
  getBySlug: (slug) =>
    apiClient.get(`/organizations/slug/${slug}`),
  search: (query, params = {}) =>
    apiClient.get('/organizations/search', { params: { query, ...params } }),
  
  // User endpoints
  getMy: () =>
    apiClient.get('/organizations/my'),
  create: (data) =>
    apiClient.post('/organizations', data),
  join: (orgId) =>
    apiClient.post(`/organizations/${orgId}/join`),
  leave: (orgId) =>
    apiClient.post(`/organizations/${orgId}/leave`),
  
  // Membership management
  getMembers: (orgId, params = {}) =>
    apiClient.get(`/organizations/${orgId}/members`, { params }),
  approveMember: (orgId, userId) =>
    apiClient.post(`/organizations/${orgId}/members/${userId}/approve`),
  removeMember: (orgId, userId) =>
    apiClient.delete(`/organizations/${orgId}/members/${userId}`),
  updateMemberRole: (orgId, userId, role) =>
    apiClient.put(`/organizations/${orgId}/members/role`, { userId, role }),
}

// ============================================================================
// ADMIN ORGANIZATION APIS - /api/v1/admin/organizations
// ============================================================================
export const adminOrganizationAPI = {
  list: (params = {}) =>
    apiClient.get('/admin/organizations', { params }),
  updateStatus: (orgId, status) =>
    apiClient.put(`/admin/organizations/${orgId}/status`, { status }),
}

// ============================================================================
// PAYMENT APIS - /api/v1/payments (Payment Processing)
// ============================================================================
export const paymentAPI = {
  initiatePayment: (data) =>
    apiClient.post('/payments/initiate', data),
  getByTransactionId: (transactionId) =>
    apiClient.get(`/payments/${transactionId}`),
  getByRegistration: (registrationId) =>
    apiClient.get(`/payments/registration/${registrationId}`),
  listByUser: (userId, params = {}) =>
    apiClient.get('/payments', { params: { userId, ...params } }),
  refund: (transactionId, reason) =>
    apiClient.post(`/payments/${transactionId}/refund`, { reason }),
  simulateSuccess: (transactionId) =>
    apiClient.post(`/payments/${transactionId}/simulate-success`),
}

// ============================================================================
// COUPON APIS - /api/v1/coupons (Coupon Management)
// ============================================================================
export const couponAPI = {
  validate: (code, amount) =>
    apiClient.get(`/coupons/${code}/validate`, { params: { amount } }),
}

// ============================================================================
// NOTIFICATION APIS - /api/v1/notifications (Notifications)
// ============================================================================
export const notificationAPI = {
  listUnread: () => 
    apiClient.get('/notifications/unread'),
  list: (params = {}) => 
    apiClient.get('/notifications', { params }),
  getByType: (type, params = {}) =>
    apiClient.get(`/notifications/type/${type}`, { params }),
  getUnreadCount: () =>
    apiClient.get('/notifications/unread/count'),
  markAsRead: (notificationId) => 
    apiClient.put(`/notifications/${notificationId}/read`),
  archive: (notificationId) =>
    apiClient.put(`/notifications/${notificationId}/archive`),
  getPreferences: () => 
    apiClient.get('/notifications/preferences'),
  updatePreferences: (data) => 
    apiClient.put('/notifications/preferences', data),
}

export default apiClient
