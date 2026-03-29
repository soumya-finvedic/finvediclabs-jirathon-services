import apiClient from './client'

// Auth APIs
export const authAPI = {
  login: (email, password) => 
    apiClient.post('/auth/login', { email, password }),
  logout: () => 
    apiClient.post('/auth/logout'),
  register: (data) => 
    apiClient.post('/auth/register', data),
  verifyOtp: (data) =>
    apiClient.post('/auth/verify-otp', data),
  resendOtp: (email, purpose = 'EMAIL_VERIFICATION') =>
    apiClient.post(`/auth/resend-otp?email=${encodeURIComponent(email)}&purpose=${encodeURIComponent(purpose)}`),
  forgotPassword: (email) =>
    apiClient.post('/auth/forgot-password', { email }),
  resetPassword: (token, newPassword) =>
    apiClient.post('/auth/reset-password', { token, newPassword }),
  getPortals: () =>
    apiClient.get('/auth/portals'),
}

// Contest APIs
export const contestAPI = {
  list: (params) => 
    apiClient.get('/contests', { params }),
  getById: (id) => 
    apiClient.get(`/contests/${id}`),
  create: (data) => 
    apiClient.post('/contests', data),
  update: (id, data) => 
    apiClient.put(`/contests/${id}`, data),
  delete: (id) => 
    apiClient.delete(`/contests/${id}`),
  register: (id) => 
    apiClient.post(`/contests/${id}/register`),
  getMyRegistration: (id, config = {}) =>
    apiClient.get(`/contests/${id}/registration/me`, config),
  confirmPayment: (registrationId, paymentTransactionId) =>
    apiClient.post(`/contests/registrations/${registrationId}/confirm-payment`, { paymentTransactionId }),
  updateRegistrationDetails: (registrationId, data) =>
    apiClient.put(`/contests/registrations/${registrationId}/details`, data),
  getQuestions: (id) => 
    apiClient.get(`/contests/${id}/questions`),
}

// Admin Contest APIs
export const adminContestAPI = {
  list: (params) =>
    apiClient.get('/admin/contests', { params }),
  getById: (id) =>
    apiClient.get(`/admin/contests/${id}`),
  listRegistrations: (contestId) =>
    apiClient.get(`/admin/contests/${contestId}/registrations`),
  create: (data) =>
    apiClient.post('/admin/contests', data),
  update: (id, data) =>
    apiClient.put(`/admin/contests/${id}`, data),
  delete: (id) =>
    apiClient.delete(`/admin/contests/${id}`),
  getSupportedLanguages: () =>
    apiClient.get('/admin/contests/meta/supported-languages'),
}

// Execution APIs
export const executionAPI = {
  run: (code, language) => 
    apiClient.post('/execution/run', { code, language }),
  getLanguages: () => 
    apiClient.get('/execution/languages'),
}

// Submission APIs
export const submissionAPI = {
  submit: (contestId, questionId, data) => 
    apiClient.post(`/contests/${contestId}/questions/${questionId}/submit`, data),
  getSubmissions: (contestId) => 
    apiClient.get(`/contests/${contestId}/submissions`),
  getSubmissionById: (id) => 
    apiClient.get(`/submissions/${id}`),
}

// Leaderboard APIs
export const leaderboardAPI = {
  getGlobal: (params) => 
    apiClient.get('/leaderboards/global', { params }),
  getContestLeaderboard: (contestId, params) => 
    apiClient.get(`/leaderboards/contests/${contestId}`, { params }),
  getUserRank: (contestId) => 
    apiClient.get(`/leaderboards/contests/${contestId}/users/me/rank`),
}

// User APIs
export const userAPI = {
  getProfile: () => 
    apiClient.get('/auth/me'),
  updateProfile: (data) => 
    apiClient.put('/users/me', data),
  getStats: () => 
    apiClient.get('/users/me/stats'),
}

// Admin User APIs
export const adminUserAPI = {
  list: (params) =>
    apiClient.get('/admin/users', { params }),
}

// Payment APIs
export const paymentAPI = {
  getByRegistration: (registrationId) =>
    apiClient.get(`/payments/registration/${registrationId}`),
  initiatePayment: (payload) =>
    apiClient.post('/payments/initiate', payload),
  getPaymentStatus: (txnId) =>
    apiClient.get(`/payments/${txnId}`),
  listByUser: (userId) =>
    apiClient.get('/payments', { params: { userId } }),
  simulateSuccess: (txnId) =>
    apiClient.post(`/payments/${txnId}/simulate-success`),
}

// Notification APIs
export const notificationAPI = {
  list: (params) => 
    apiClient.get('/notifications', { params }),
  markAsRead: (id) => 
    apiClient.put(`/notifications/${id}/read`),
  getPreferences: () => 
    apiClient.get('/notifications/preferences'),
  updatePreferences: (data) => 
    apiClient.put('/notifications/preferences', data),
}

export default apiClient
