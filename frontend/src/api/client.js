import axios from 'axios'

const API_BASE = '/api'

const apiClient = axios.create({
  baseURL: API_BASE,
  timeout: 20000,
})

// Request interceptor
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  const tenantId = localStorage.getItem('tenantId') || 'jirathon'
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  config.headers['X-Tenant-Id'] = tenantId
  return config
})

// Response interceptor
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const requestUrl = error.config?.url || ''
    const skipAuthRedirect = !!error.config?.skipAuthRedirect
    const isAuthEndpoint = requestUrl.includes('/auth/login') || 
                          requestUrl.includes('/auth/register') ||
                          requestUrl.includes('/auth/forgot-password') ||
                          requestUrl.includes('/auth/reset-password')
    const isRegistrationProbe = requestUrl.includes('/registration/me')

    if (error.response?.status === 401 && !skipAuthRedirect && !isAuthEndpoint && !isRegistrationProbe) {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default apiClient
