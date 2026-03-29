# Quick Start - Frontend-Backend Integration

> **Status**: ✅ API layer complete. 🔄 Component integration in progress.

## What's Done ✅

1. **API Integration Layer** (`src/api/index.js`)
   - All backend endpoints mapped (auth, contests, users, leaderboard, organizations, payments, notifications)
   - Proper request/response formatting
   - JWT token + Tenant ID headers

2. **State Management** (`src/stores/index.js`)
   - Pinia stores for all features (User, Contest, Leaderboard, Organization, Notification, Payment)
   - Proper error handling and loading states
   - Async actions with consistent response format

3. **Error Handling** (`src/api/errorHandler.js`)
   - Centralized error formatting
   - Network error handling
   - Validation error extraction
   - Auth error handling

4. **Async Helpers** (`src/composables/useAsync.js`)
   - `useAsync()` - For single API calls
   - `usePaginated()` - For paginated lists
   - `useForm()` - For form submission

5. **Axios Configuration** (`src/api/client.js`)
   - Interceptors for token injection
   - Tenant ID header injection
   - 401 error handling with logout

---

## What's Left 🔄

Connect UI components to backend APIs. Use templates below.

---

## 🚀 Getting Started

### 1. Start Backend & Infrastructure

```bash
# Option A: Using Docker Compose (Recommended)
cd jirathon
docker-compose up -d

# Wait for services to be healthy (1-2 minutes)
docker-compose ps
```

**OR**

```bash
# Option B: Local Development (if you prefer)

# Terminal 1: Bring up infrastructure
docker-compose up -d mongodb redis kafka

# Terminal 2: Start auth-service
cd backend/auth-service
mvn spring-boot:run

# Terminal 3: Start user-service, contest-service, etc.
cd backend/user-service
mvn spring-boot:run

# Each service will log its startup
```

### 2. Start Frontend

```bash
# Terminal N: Frontend development
cd frontend
npm install
npm run dev
```

Frontend will be available at `http://localhost:5173` (Vite dev server)

### 3. Access the Application

Navigate to frontend:
- If via Vite dev server: `http://localhost:5173`
- If via Docker: `http://localhost:8080`

Backend APIs accessible at:
- Direct: `http://localhost:8081/api/v1/...` (auth service)
- Via Nginx proxy: `http://localhost:8080/api/v1/...`

---

## 📝 Connecting a Component: Quick Template

### Minimal Example - Dashboard

```vue
<template>
  <div class="dashboard">
    <!-- Loading -->
    <div v-if="loading" class="space-y-4">
      <div v-for="i in 3" :key="i" class="skeleton" />
    </div>

    <!-- Error -->
    <div v-else-if="error" class="alert-error">
      {{ error }}
      <button @click="refetch">Retry</button>
    </div>

    <!-- Content -->
    <div v-else>
      <h1>Welcome {{ userStore.user?.displayName }}</h1>
      
      <div class="contests-grid">
        <ContestCard 
          v-for="c in contests" 
          :key="c.id" 
          :contest="c"
          @register="handleRegister"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore, useContestStore, useLeaderboardStore } from '@/stores'
import ContestCard from '@/components/ContestCard.vue'

const userStore = useUserStore()
const contestStore = useContestStore()
const leaderboardStore = useLeaderboardStore()

// Computed properties from stores
const loading = computed(() => contestStore.loading)
const error = computed(() => contestStore.error)
const contests = computed(() => contestStore.contests)

// Fetch data on component mount
onMounted(async () => {
  await contestStore.fetchContests()
  await leaderboardStore.fetchGlobalLeaderboard()
})

// Handle user action
const handleRegister = async (contestId) => {
  const result = await contestStore.registerForContest(contestId)
  if (result.ok) {
    // Show toast success
  }
}

const refetch = async () => {
  await contestStore.fetchContests()
}
</script>
```

---

## 🎯 Component Integration Steps

### For Each Component:

1. **Import store or use composable**
   ```javascript
   import { useContestStore } from '@/stores'
   // OR
   import { useAsync } from '@/composables/useAsync'
   import { contestAPI } from '@/api'
   ```

2. **Initialize in setup()**
   ```javascript
   const store = useContestStore()
   // OR
   const { data, loading, error, execute } = useAsync(() => contestAPI.list())
   ```

3. **Fetch data on mount**
   ```javascript
   onMounted(() => store.fetchData())
   ```

4. **Bind to template**
   ```vue
   <div v-if="store.loading">Loading...</div>
   <div v-else-if="store.error">{{ store.error }}</div>
   <div v-else>{{ store.data }}</div>
   ```

5. **Handle user actions**
   ```javascript
   const handleAction = async () => {
     const result = await store.performAction()
     if (result.ok) {
       // Success
     } else {
       // Error: result.message
     }
   }
   ```

---

## 📚 Component-to-API Mapping

| Component | Store/API | Method | Purpose |
|-----------|-----------|--------|---------|
| Dashboard | contestStore | fetchContests() | Load contests |
| Dashboard | leaderboardStore | fetchGlobalLeaderboard() | Load rankings |
| ContestsList | contestStore | fetchContests() | List all contests |
| ContestDetail | contestStore | getContest(id) | Get single contest |
| GlobalLeaderboard | leaderboardStore | fetchGlobalLeaderboard() | Get global rankings |
| ProfilePage | userStore | updateProfile() | Update user info |
| LoginPage | userStore | login() | Authenticate user |
| RegisterPage | userStore | register() | Create account |
| AdminContests | adminContestStore | fetchContests() | Admin: list contests |
| AdminContests | adminContestAPI | create() | Admin: create contest |

---

## 🔧 Development Workflow

### Making an API Call

```javascript
// Step 1: Import API or Store
import { contestAPI } from '@/api'
// OR
import { useContestStore } from '@/stores'

// Step 2: Call the function
const result = await contestAPI.list()
// OR
const store = useContestStore()
const result = await store.fetchContests()

// Step 3: Always check result.ok
if (result.ok) {
  // result.data has the data
  console.log(result.data)
} else {
  // result.message has error
  console.error(result.message)
}
```

### Debugging

```bash
# 1. Check Network tab in DevTools
# Click on API request
# Verify: Status 200, Response has data field

# 2. Check Console
# Should show no errors (only warnings)
# console.log() your variables to verify

# 3. Check Vue DevTools
# Pinia tab → Select store → Check state

# 4. Check backend logs
docker logs jirathon-auth  # Auth service logs
docker logs jirathon-contest  # Contest service logs
```

---

## 🆘 Common Issues

### Problem: 404 errors on API calls
```
GET /api/contests 404 Not Found
```
**Solution**: Ensure nginx/reverse proxy is running and routing correctly
```bash
# Test API endpoint directly
curl http://localhost:8080/api/v1/auth/portals -v

# Verify nginx config
docker exec jirathon-reverse-proxy cat /etc/nginx/nginx.conf | grep location
```

### Problem: 401 Unauthorized
```
POST /api/contests/register 401 Unauthorized
```
**Solution**: Token not being sent or expired
```javascript
// Check token exists
console.log('Token:', localStorage.getItem('accessToken'))

// Check header is set
console.log('Auth header:', apiClient.defaults.headers.common.Authorization)

// If expired, login again
```

### Problem: CORS errors
```
Access to XMLHttpRequest blocked by CORS policy
```
**Solution**: Already configured in backend. If still happening:
```yaml
# File: backend/auth-service/src/main/resources/application.yml
spring:
  web:
    cors:
      allowed-origins: "*"
      allowed-methods: "*"
      allowed-headers: "*"
```

### Problem: State not persisting after refresh
**Solution**: Add localStorage persistence in stores:
```javascript
const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

// When updating
localStorage.setItem('user', JSON.stringify(newValue))
```

---

## 📋 Verification Checklist

Before considering a component "done":

- [ ] Component fetches data on mount (not loading mock data)
- [ ] Skeleton/spinner shows while loading
- [ ] Error message displays if API fails
- [ ] Data displays correctly when API succeeds
- [ ] User interactions trigger correct API calls
- [ ] No `undefined` errors in console
- [ ] No hardcoded mock data
- [ ] Pinia store shows correct state (use Vue DevTools)
- [ ] Network requests show correct status codes (200, 201, etc.)
- [ ] Response data structure matches what component expects

---

## 🎓 Architecture Diagram

```
┌─────────────────────────────────────────────────┐
│          Vue 3 Components (UI)                  │
├─────────────────────────────────────────────────┤
│  Dashboard  │  ContestsList  │  Leaderboard    │
└──────────────┬───────────────┬──────────────────┘
               │               │
         Uses  │               │  Uses
               ▼               ▼
┌──────────────────────────────────────────────────┐
│        Pinia Stores (State Management)           │
├──────────────────────────────────────────────────┤
│  userStore  │  contestStore  │  leaderboardStore│
└──────────────┬───────────────┬──────────────────┘
               │               │
         Calls │               │  Calls
               ▼               ▼
┌──────────────────────────────────────────────────┐
│    API Services (src/api/index.js)              │
├──────────────────────────────────────────────────┤
│ authAPI │ contestAPI │ userAPI │ leaderboardAPI │
└──────────────┬───────────────┬──────────────────┘
               │               │
         Uses  │               │  Uses
               ▼               ▼
┌──────────────────────────────────────────────────┐
│  Axios Client + Interceptors (src/api/client.js)│
├──────────────────────────────────────────────────┤
│ - Inject Bearer token                           │
│ - Inject X-Tenant-Id header                     │
│ - Handle 401 errors                             │
└──────────────┬───────────────────────────────────┘
               │
         HTTP  │ GET/POST/PUT/DELETE
               ▼
┌──────────────────────────────────────────────────┐
│    Nginx Reverse Proxy (port 8080)              │
├──────────────────────────────────────────────────┤
│ /api/auth/* → auth-service:8081                │
│ /api/contests/* → contest-service:8085         │
│ /api/users/* → user-service:8083               │
└──────────────┬──────────────┬────────────────────┘
               │              │
         HTTP  │              │
               ▼              ▼
    ┌──────────────────┬──────────────────┐
    │  Auth Service    │  Contest Service │
    │  (Spring Boot)   │  (Spring Boot)   │
    └──────────────────┴──────────────────┘
               │
               ▼
        ┌─────────────┐
        │  MongoDB    │
        │   & Redis   │
        └─────────────┘
```

---

## 📖 Documentation Files

| File | Content |
|------|---------|
| `FRONTEND_BACKEND_INTEGRATION_GUIDE.md` | Detailed integration guide with examples |
| `IMPLEMENTATION_CHECKLIST.md` | Component checklist and templates |
| `README.md` (this file) | Quick start guide |
| `src/api/client.js` | Axios configuration |
| `src/api/index.js` | All API endpoints |
| `src/api/errorHandler.js` | Error handling utilities |
| `src/composables/useAsync.js` | Helper composables |
| `src/stores/index.js` | Pinia state management |

---

## 🎯 Next Steps

1. **Immediate**: Start the full docker-compose stack
   ```bash
   docker-compose up -d
   docker-compose ps  # Verify all services green
   ```

2. **Connect Dashboard**: Use template above to connect Dashboard component

3. **Connect Login**: Update auth flow to use stores

4. **Connect More Pages**: Follow component mapping table

5. **Test End-to-End**: 
   - Login → Browse Contests → Register → Check Leaderboard

6. **Deploy**: Update environment variables for production

---

## 📞 Support

If stuck:
1. Check the detailed guide: `FRONTEND_BACKEND_INTEGRATION_GUIDE.md`
2. Check component examples in the guide
3. Check component checklist: `IMPLEMENTATION_CHECKLIST.md`
4. Debug using DevTools (Network + Console + Vue DevTools)
5. Check backend logs: `docker logs jirathon-auth`

