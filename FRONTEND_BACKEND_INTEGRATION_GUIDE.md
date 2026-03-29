# Jirathon Frontend-Backend Integration Guide

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [API Integration Setup](#api-integration-setup)
3. [Service Architecture](#service-architecture)
4. [Step-by-Step Integration](#step-by-step-integration)
5. [Component Examples](#component-examples)
6. [Error Handling](#error-handling)
7. [Testing & Running](#testing--running)
8. [Troubleshooting](#troubleshooting)

---

## Architecture Overview

### Backend (Spring Boot Microservices)
- **Technology**: Java 17, Spring Boot 3.2.4
- **Architecture**: Microservices with MongoDB, Redis, Kafka
- **API Base URL**: `/api/v1/` (all endpoints)
- **Response Format**: Wrapped in `ApiResponse<T>`
- **Reverse Proxy**: Nginx on port 8080 routes to services

### Frontend (Vue 3)
- **Technology**: Vue 3, Pinia (state management), Axios (HTTP client)
- **API Client**: Centralized in `src/api/`
- **State Management**: Pinia stores for each feature
- **Port**: 80 (exposed)

### Development Flow
```
Frontend (Vue 3) 
  ↓
Axios (HTTP Client) 
  ↓
API Client Layer (src/api/index.js) 
  ↓
Pinia Stores (src/stores/) 
  ↓
Backend Microservices (/api/v1/*)
  ↓
Spring Boot Services (Auth, Contest, User, etc.)
  ↓
MongoDB & Redis (Data Storage)
```

---

## API Integration Setup

### 1. Base URL Configuration

Frontend uses `/api` as base URL with Nginx reverse proxy. The Nginx config routes requests to backend services:

```
/api/auth/* → auth-service:8081
/api/contests/* → contest-service:8085
/api/users/* → user-service:8083
/api/leaderboards/* → leaderboard-service:8087
...and so on
```

**File**: `frontend/src/api/client.js`

```javascript
import axios from 'axios'

const API_BASE = '/api'  // Relative path - works with Nginx proxy

const apiClient = axios.create({
  baseURL: API_BASE,
  timeout: 20000,
})

// Request interceptor - adds auth token and tenant ID
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  const tenantId = localStorage.getItem('tenantId') || 'jirathon'
  
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  config.headers['X-Tenant-Id'] = tenantId
  return config
})

// Response interceptor - handles 401/auth errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Clear auth and redirect to login
      localStorage.removeItem('accessToken')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default apiClient
```

### 2. API Endpoints Structure

**File**: `frontend/src/api/index.js`

All endpoints are organized by service, grouped logically:

```javascript
// Auth APIs
export const authAPI = {
  login: (email, password) => apiClient.post('/auth/login', ...),
  register: (data) => apiClient.post('/auth/register', data),
  refreshToken: (refreshToken) => apiClient.post('/auth/refresh', ...),
}

// Contest APIs
export const contestAPI = {
  list: (params) => apiClient.get('/contests', { params }),
  getById: (id) => apiClient.get(`/contests/${id}`),
  register: (id) => apiClient.post(`/contests/${id}/register`),
}

// User APIs
export const userAPI = {
  getMe: () => apiClient.get('/users/me'),
  updateMe: (data) => apiClient.patch('/users/me', data),
}

// Leaderboard APIs
export const leaderboardAPI = {
  getGlobal: (params) => apiClient.get('/leaderboards/global', { params }),
}
```

---

## Service Architecture

### Authentication Flow

```
1. User enters email/password
   ↓
2. authAPI.login() → Backend
   ↓
3. Backend returns {accessToken, refreshToken, user}
   ↓
4. Frontend stores in localStorage & Pinia store
   ↓
5. All subsequent requests include token in Authorization header
   ↓
6. If token expires (401), automatically redirect to login
```

### Pinia Stores

**File**: `frontend/src/stores/index.js`

#### User Store (Authentication & Profile)
```javascript
export const useUserStore = defineStore('user', () => {
  // State
  const user = ref(null)          // Current user object
  const token = ref(null)         // JWT access token
  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => roles include ADMIN)
  
  // Actions
  const login = async (email, password) => { ... }
  const register = async (data) => { ... }
  const logout = async () => { ... }
  const fetchUser = async () => { ... }
  const updateProfile = async (data) => { ... }
})
```

#### Contest Store
```javascript
export const useContestStore = defineStore('contest', () => {
  const contests = ref([])
  const currentContest = ref(null)
  const myRegistrations = ref(new Map())
  const loading = ref(false)
  const error = ref(null)
  
  // Actions
  const fetchContests = async (params) => { ... }
  const getContest = async (contestId) => { ... }
  const registerForContest = async (contestId) => { ... }
  const getMyRegistration = async (contestId) => { ... }
})
```

#### Leaderboard Store
```javascript
export const useLeaderboardStore = defineStore('leaderboard', () => {
  const globalLeaderboard = ref([])
  const loading = ref(false)
  
  // Actions
  const fetchGlobalLeaderboard = async (params) => { ... }
  const fetchUserRank = async (userId) => { ... }
})
```

Similar stores exist for:
- `useOrganizationStore` - Organization management
- `useNotificationStore` - Notifications
- `usePaymentStore` - Payments
- `useAdminContestStore` - Admin contest operations

---

## Step-by-Step Integration

### Step 1: Update API Endpoints

✅ **COMPLETED** in `src/api/index.js`
- All REST endpoints from backend documented
- Request/response formats aligned
- Headers (Authorization, X-Tenant-Id) configured

### Step 2: Setup Pinia Stores

✅ **COMPLETED** in `src/stores/index.js`
- User store with auth methods
- Contest store with fetch/register methods
- Leaderboard, Organization, Notification, Payment stores
- Proper error handling and loading states

### Step 3: Connect Components to APIs

Example Connection in Dashboard Component:

```vue
<template>
  <div class="dashboard">
    <!-- Loading state -->
    <div v-if="loading" class="skeleton">Loading...</div>
    
    <!-- Error state -->
    <div v-else-if="error" class="error-banner">{{ error }}</div>
    
    <!-- Content -->
    <div v-else class="content">
      <h1>Welcome {{ userStore.user?.displayName }}</h1>
      
      <!-- User stats -->
      <div class="stats">
        <div>Contests: {{ contests.length }}</div>
        <div>Rank: #{{ userRank }}</div>
      </div>
      
      <!-- Active contests -->
      <div class="contests-grid">
        <ContestCard 
          v-for="contest in activeContests" 
          :key="contest.id"
          :contest="contest"
          @register="handleRegister"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores'
import { useContestStore } from '@/stores'
import { useLeaderboardStore } from '@/stores'

// Get stores
const userStore = useUserStore()
const contestStore = useContestStore()
const leaderboardStore = useLeaderboardStore()

// Component state
const error = ref(null)
const activeContests = computed(() => 
  contestStore.contests.filter(c => c.status === 'PUBLISHED')
)

// On mount - fetch data
onMounted(async () => {
  // Fetch contests
  const result = await contestStore.fetchContests()
  if (!result.ok) {
    error.value = result.message
  }
  
  // Fetch leaderboard
  await leaderboardStore.fetchGlobalLeaderboard()
  
  // Fetch user rank
  if (userStore.user?.id) {
    await leaderboardStore.fetchUserRank(userStore.user.id)
  }
})

// Register for contest
const handleRegister = async (contestId) => {
  const result = await contestStore.registerForContest(contestId)
  if (result.ok) {
    // Show success message
  } else {
    error.value = result.message
  }
}

const loading = computed(() => contestStore.loading || leaderboardStore.loading)
const userRank = computed(() => leaderboardStore.userRank?.rank)
const contests = computed(() => contestStore.contests)
</script>
```

### Step 4: Configure Authentication

1. **Login Page** (`src/pages/AuthPage.vue`):

```vue
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores'

const router = useRouter()
const userStore = useUserStore()
const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

const handleLogin = async () => {
  loading.value = true
  error.value = ''
  
  const result = await userStore.login(email.value, password.value)
  
  if (result.ok) {
    // Redirect to dashboard
    router.push('/dashboard')
  } else {
    error.value = result.message
  }
  
  loading.value = false
}

const handleRegister = async () => {
  // Similar flow for registration
  const result = await userStore.register({
    email: email.value,
    password: password.value,
    displayName: 'User'
  })
  
  if (result.ok) {
    router.push('/verify-email')
  } else {
    error.value = result.message
  }
}
</script>
```

2. **Protected Routes** (in `src/router/index.js`):

```javascript
const routes = [
  {
    path: '/login',
    component: AuthPage,
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  // More routes...
]

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && userStore.isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
})
```

### Step 5: Handle Errors & Loading

```vue
<!-- Generic error display -->
<div v-if="error" class="alert-error">
  {{ error }}
  <button @click="dismissError">Dismiss</button>
</div>

<!-- Loading skeleton -->
<div v-if="loading" class="skeleton-grid">
  <div v-for="i in 6" :key="i" class="skeleton-card" />
</div>

<!-- Empty state -->
<div v-else-if="contests.length === 0" class="empty-state">
  <p>No contests available yet</p>
  <button @click="refreshContests">Refresh</button>
</div>

<!-- Content -->
<div v-else class="content-grid">
  <!-- Your content here -->
</div>
```

---

## Component Examples

### Example 1: Contest List Component

**File**: `src/pages/ContestsList.vue`

```vue
<template>
  <div class="contests-list">
    <!-- Filter section -->
    <div class="filter-bar">
      <input v-model="searchQuery" placeholder="Search contests..." />
      <select v-model="statusFilter">
        <option value="">All Status</option>
        <option value="PUBLISHED">Published</option>
        <option value="DRAFT">Draft</option>
      </select>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading">
      <div v-for="i in 6" :key="i" class="card skeleton" />
    </div>

    <!-- Error -->
    <div v-else-if="error" class="error-banner">
      {{ error }}
      <button @click="refetch">Retry</button>
    </div>

    <!-- Contests grid -->
    <div v-else-if="filteredContests.length > 0" class="grid">
      <ContestCard 
        v-for="contest in filteredContests" 
        :key="contest.id"
        :contest="contest"
        @joined="handleJoined"
      />
    </div>

    <!-- Empty state -->
    <div v-else class="empty">
      <p>No contests found</p>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="pagination">
      <button 
        v-for="p in totalPages" 
        :key="p"
        :class="{ active: currentPage === p }"
        @click="currentPage = p; fetchContests()"
      >
        {{ p }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useContestStore } from '@/stores'
import ContestCard from '@/components/ContestCard.vue'

const contestStore = useContestStore()
const searchQuery = ref('')
const statusFilter = ref('')
const currentPage = ref(0)

const loading = computed(() => contestStore.loading)
const error = computed(() => contestStore.error)
const contests = computed(() => contestStore.contests)
const totalPages = computed(() => contestStore.totalPages)

const filteredContests = computed(() => {
  let filtered = contests.value
  
  if (searchQuery.value) {
    filtered = filtered.filter(c => 
      c.title.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  
  if (statusFilter.value) {
    filtered = filtered.filter(c => c.status === statusFilter.value)
  }
  
  return filtered
})

const fetchContests = async () => {
  const result = await contestStore.fetchContests({
    page: currentPage.value,
    size: 20
  })
  
  if (!result.ok) {
    console.error('Failed to fetch contests:', result.message)
  }
}

const handleJoined = async (contestId) => {
  const result = await contestStore.registerForContest(contestId)
  if (result.ok) {
    // Show success toast
    console.log('Registered for contest')
  }
}

const refetch = () => fetchContests()

// Fetch on mount
onMounted(() => fetchContests())

// Re-fetch when filters change
watch([searchQuery, statusFilter], () => {
  currentPage.value = 0
})
</script>
```

### Example 2: User Profile Component

**File**: `src/pages/ProfilePage.vue`

```vue
<template>
  <div class="profile-page">
    <div v-if="loading" class="skeleton" />
    
    <div v-else class="profile-content">
      <div class="profile-header">
        <img :src="user?.avatarUrl" :alt="user?.displayName" />
        <h1>{{ user?.displayName }}</h1>
        <p>{{ user?.email }}</p>
      </div>

      <div class="profile-form">
        <form @submit.prevent="handleUpdate">
          <div class="form-group">
            <label>Email</label>
            <input v-model="formData.email" type="email" disabled />
          </div>

          <div class="form-group">
            <label>Display Name</label>
            <input v-model="formData.displayName" type="text" />
          </div>

          <div class="form-group">
            <label>Bio</label>
            <textarea v-model="formData.bio" />
          </div>

          <div class="form-group">
            <label>Skills (comma-separated)</label>
            <input v-model="formData.skills" type="text" />
          </div>

          <div v-if="error" class="alert-error">{{ error }}</div>
          <div v-if="success" class="alert-success">Profile updated successfully</div>

          <button type="submit" :disabled="loading">
            {{ loading ? 'Saving...' : 'Save Profile' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores'

const userStore = useUserStore()
const loading = ref(false)
const error = ref('')
const success = ref(false)

const formData = ref({
  displayName: '',
  email: '',
  bio: '',
  skills: ''
})

const user = computed(() => userStore.user)

onMounted(() => {
  if (user.value) {
    formData.value = {
      displayName: user.value.displayName or '',
      email: user.value.email,
      bio: user.value.profile?.bio || '',
      skills: (user.value.profile?.skills || []).join(', ')
    }
  }
})

const handleUpdate = async () => {
  loading.value = true
  error.value = ''
  success.value = false

  const result = await userStore.updateProfile({
    displayName: formData.value.displayName,
    bio: formData.value.bio,
    skills: formData.value.skills.split(',').map(s => s.trim())
  })

  if (result.ok) {
    success.value = true
    setTimeout(() => { success.value = false }, 3000)
  } else {
    error.value = result.message
  }

  loading.value = false
}
</script>
```

---

## Error Handling

### Global Error Handling Middleware

**File**: `src/api/errorHandler.js` (NEW)

```javascript
export const handleApiError = (error) => {
  const status = error.response?.status
  const data = error.response?.data
  
  // Default error message
  let message = 'An error occurred. Please try again.'
  
  if (data?.error) {
    message = data.error
  } else if (data?.message) {
    message = data.message
  }
  
  // Handle specific status codes
  switch (status) {
    case 400:
      message = data?.message || 'Invalid request'
      break
    case 401:
      message = 'Unauthorized. Please login again.'
      // Clear auth and redirect handled by interceptor
      break
    case 403:
      message = 'You do not have permission to perform this action'
      break
    case 404:
      message = 'Resource not found'
      break
    case 500:
      message = 'Server error. Please try again later.'
      break
    case 503:
      message = 'Service unavailable. Please try again later.'
      break
  }
  
  return message
}

export const handleNetworkError = (error) => {
  if (error.code === 'ECONNABORTED') {
    return 'Request timeout. Please check your connection.'
  }
  if (!error.response) {
    return 'Network error. Please check your connection.'
  }
  return 'An unexpected error occurred'
}
```

### Component Error Handling

```vue
<script setup>
import { ref } from 'vue'
import { contestAPI } from '@/api'
import { handleApiError, handleNetworkError } from '@/api/errorHandler'

const loading = ref(false)
const error = ref('')

const fetchData = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const response = await contestAPI.list()
    // Process response
  } catch (err) {
    if (err.response) {
      error.value = handleApiError(err)
    } else {
      error.value = handleNetworkError(err)
    }
    console.error('Detailed error:', err)
  } finally {
    loading.value = false
  }
}
</script>
```

---

## Testing & Running

### Prerequisites

- Docker & Docker Compose installed
- Java 17 JDK (for local backend development)
- Node.js 18+ (for local frontend development)
- Maven 3.8+ (for building backend)

### Running Development Environment

#### Option 1: Docker Compose (Recommended)

```bash
# Navigate to project root
cd jirathon

# Start all services (backend, frontend, databases)
docker-compose up -d

# Access the app
# Frontend: http://localhost:8080
# API: http://localhost:8080/api/v1/...

# View logs
docker-compose logs -f frontend
docker-compose logs -f auth-service

# Stop services
docker-compose down
```

#### Option 2: Local Development

```bash
# Terminal 1: Start backend services (use VS Code tasks)
# Run task: "Run auth-service Java17"
# This starts auth service on port 8081

# Terminal 2: Start frontend
cd frontend
npm install
npm run dev
# Frontend runs on http://localhost:5173

# Terminal 3: Start Nginx reverse proxy
# Or configure webpack devServer proxy to backend
```

### API Testing

#### Using Manual Testing

```
1. Open http://localhost:8080 in browser
2. Navigate to /login
3. Create account or login
4. Check Network tab in DevTools to see API calls
```

#### Using Postman/Insomnia

Import the postman collection at `testing/postman/Jirathon.postman_collection.json`

```
1. Set environment to `Jirathon.local.postman_environment.json`
2. Set base URL: http://localhost:8080/api/v1
3. Run authentication endpoint first to get token
4. Subsequent requests use token from environment
```

#### Using Newman (CLI)

```bash
cd testing/newman

# Run collection with environment
./run-newman.sh
```

---

## Troubleshooting

### Common Issues & Solutions

#### 1. **CORS Errors**
```
Error: Access to XMLHttpRequest blocked by CORS policy
```

**Solution**: Ensure Spring Boot CORS is enabled:

**File**: `backend/auth-service/src/main/resources/application.yml`

```yaml
spring:
  web:
    cors:
      allowed-origins: "*"
      allowed-methods: "*"
      allowed-headers: "*"
      allow-credentials: true
      max-age: 3600
```

#### 2. **401 Unauthorized Errors**
```
Error: 401 Unauthorized even after login
```

**Causes**:
- Token not sent in Authorization header
- Token expired
- Wrong tenant ID

**Solutions**:
```javascript
// Verify token is in localStorage
console.log(localStorage.getItem('accessToken'))

// Check Authorization header is set
const auth = apiClient.defaults.headers.common['Authorization']
console.log('Auth header:', auth)

// Verify X-Tenant-Id header
const tenantId = localStorage.getItem('tenantId')
console.log('Tenant ID:', tenantId)
```

#### 3. **API Base URL Issues**
```
Error: 404 Not Found when calling /api/contests
```

**Cause**: Frontend API calls not routing through Nginx reverse proxy

**Solutions**:
```javascript
// In api/client.js, ensure baseURL is correct
const apiClient = axios.create({
  baseURL: '/api',  // Relative path
  timeout: 20000,
})

// If running frontend separately (not through Nginx):
// Then use absolute URL:
baseURL: 'http://localhost:8080/api'
```

#### 4. **MongoDB Connection Errors**
```
Error: MongoDB connection refused
```

**Solution**:
```bash
# Start MongoDB container
docker-compose up -d mongodb redis kafka

# Verify connection
docker-compose logs mongodb
```

#### 5. **Frontend Not Connecting to Backend**
```
Network tab shows failed requests to /api/*
```

**Debugging**:
```bash
# Check Nginx is running
docker ps | grep jirathon-reverse-proxy

# Check backend services are running
docker ps | grep jirathon-auth

# Check Nginx config
docker exec jirathon-reverse-proxy cat /etc/nginx/nginx.conf

# View Nginx logs
docker logs jirathon-reverse-proxy

# Test API endpoint directly
curl http://localhost:8080/api/v1/auth/portals -v
```

#### 6. **Pinia Store State Lost on Refresh**
```
State is empty after page refresh
```

**Solution**: Persist store to localStorage:

```javascript
// In store
const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

// When updating
const setSession = (data) => {
  user.value = data
  localStorage.setItem('user', JSON.stringify(data))
}
```

---

## Key Files Summary

| File | Purpose |
|------|---------|
| `frontend/src/api/client.js` | Axios instance with interceptors |
| `frontend/src/api/index.js` | All API endpoint definitions |
| `frontend/src/stores/index.js` | Pinia stores for state management |
| `frontend/src/pages/AuthPage.vue` | Authentication UI |
| `frontend/src/pages/Dashboard.vue` | Main dashboard with contests |
| `deployment/nginx/nginx.conf` | Reverse proxy configuration |
| `docker-compose.yml` | Service orchestration |

---

## Next Steps

1. ✅ API Endpoints defined
2. ✅ Pinia Stores created with proper error handling
3. ✅ Axios interceptors configured
4. 🔄 **Connect all remaining UI components** (see component examples)
5. 🔄 **Add loading skeletons** to all pages
6. 🔄 **Implement toast notifications** for success/error messages
7. 🔄 **Test end-to-end flows** (login → browse contests → register)
8. 🔄 **Configure production environment** (environment variables)

---

## Support & Resources

- [backend API Docs](./backend/README.md)
- [Postman Collection](./testing/postman/Jirathon.postman_collection.json)
- [Vue 3 Docs](https://vuejs.org/)
- [Pinia Docs](https://pinia.vuejs.org/)
- [Axios Docs](https://axios-http.com/)

