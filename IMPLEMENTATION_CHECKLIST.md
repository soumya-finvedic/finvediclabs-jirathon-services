# Frontend-Backend Integration Checklist

This is a detailed checklist to ensure all UI components are properly connected to backend APIs.

## ✅ Completed
- [x] API endpoints defined in `src/api/index.js`
- [x] Axios interceptors configured in `src/api/client.js`
- [x] Pinia stores created in `src/stores/index.js`
- [x] Error handler utility created in `src/api/errorHandler.js`
- [x] Async composables created in `src/composables/useAsync.js`

---

## 🔄 In Progress - Component Integration

### Authentication Pages

- [ ] **LoginPage.vue** - Connect to `authAPI.login()`
  - [ ] Handle success → redirect to dashboard
  - [ ] Handle error → show error message
  - [ ] Show loading spinner during submit
  
- [ ] **RegisterPage.vue** - Connect to `authAPI.register()`
  - [ ] Validate email format
  - [ ] Handle success → redirect to email verification
  - [ ] Show password requirements
  
- [ ] **EmailVerificationPage.vue** - Connect to `authAPI.verifyOtp()`
  - [ ] Display OTP input
  - [ ] Handle resend OTP via `authAPI.resendOtp()`
  - [ ] Validate OTP before submit
  
- [ ] **ForgotPasswordPage.vue** - Connect to `authAPI.forgotPassword()`
  - [ ] Send reset email
  - [ ] Handle errors gracefully

### Dashboard & Main Pages

- [ ] **Dashboard.vue** - Fetch and display:
  - [ ] User stats (via `userAPI.getMe()`)
  - [ ] Active contests (via `contestAPI.list()`)
  - [ ] Upcoming contests
  - [ ] User rank from leaderboard (via `leaderboardAPI.getGlobalUserRank()`)
  - [ ] Loading skeletons while fetching
  - [ ] Error states with retry button

- [ ] **ContestsList.vue** - Display all contests:
  - [ ] Fetch contests via `contestAPI.list()`
  - [ ] Implement filtering (status, type)
  - [ ] Implement search functionality
  - [ ] Add pagination
  - [ ] Show "Join" button for each contest
  - [ ] Handle register action

- [ ] **ContestDetail.vue** - Display single contest:
  - [ ] Fetch via `contestAPI.getById(contestId)`
  - [ ] Show contest rules
  - [ ] Show registration deadline
  - [ ] Display "Register" or "Already Registered" button
  - [ ] If registered, show registration details
  - [ ] Link to payment if needed

### Leaderboards

- [ ] **GlobalLeaderboard.vue** - Display top users:
  - [ ] Fetch via `leaderboardAPI.getGlobal()`
  - [ ] Show rank, name, score
  - [ ] Implement pagination
  - [ ] Add "View Profile" link for each user
  - [ ] Highlight current user's rank

- [ ] **ContestLeaderboard.vue** - Contest-specific leaderboard:
  - [ ] Fetch via `leaderboardAPI.getOrganization()` or similar
  - [ ] Filter to contest participants
  - [ ] Show submission count and scores

### User Profile

- [ ] **ProfilePage.vue** - User profile management:
  - [ ] Display current user info (via `userAPI.getMe()`)
  - [ ] Edit profile form (email, name, bio, skills)
  - [ ] Save changes via `userAPI.updateMe()`
  - [ ] Show success/error messages
  - [ ] Handle validation errors

- [ ] **SettingsPage.vue** - User settings:
  - [ ] Notification preferences (via `notificationAPI.getPreferences()`)
  - [ ] Update preferences (via `notificationAPI.updatePreferences()`)
  - [ ] Logout button

### Contests Management (For Registered Users)

- [ ] **MyContests.vue** - Show registered contests:
  - [ ] List all contests user registered for
  - [ ] Show registration status for each
  - [ ] Show payment status
  - [ ] Link to contest arena

- [ ] **ContestArena.vue** - During contest:
  - [ ] Display problems/questions
  - [ ] Code editor for solutions
  - [ ] Submit code for execution/evaluation
  - [ ] Show leaderboard during contest
  - [ ] Show contest timer

### Admin Pages

- [ ] **AdminContests.vue** - Manage contests:
  - [ ] Fetch contests via `adminContestAPI.list()`
  - [ ] Create new contest via `adminContestAPI.create()`
  - [ ] Edit contest via `adminContestAPI.update()`
  - [ ] Delete contest via `adminContestAPI.delete()`
  - [ ] Get supported languages via `adminContestAPI.getSupportedLanguages()`
  - [ ] Upload banner image

- [ ] **AdminUsers.vue** - Manage users:
  - [ ] Fetch users via `adminUserAPI.list()`
  - [ ] Update user roles
  - [ ] Update user status
  - [ ] Delete users
  - [ ] Search/filter users

- [ ] **AdminPanel.vue** - Dashboard for admins:
  - [ ] Display system statistics
  - [ ] Quick actions for common tasks
  - [ ] Navigation to admin pages

---

## 🔧 Implementation Template

Use this template when converting a page to use the API:

```vue
<template>
  <div class="page">
    <!-- Loading State -->
    <div v-if="loading" class="space-y-4">
      <div v-for="i in 3" :key="i" class="skeleton-card" />
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert-error">
      <p>{{ error }}</p>
      <button @click="refetch">Retry</button>
    </div>

    <!-- Content State -->
    <div v-else class="content">
      <!-- Your content here -->
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useStore } from '@/stores'
import { useAsync } from '@/composables/useAsync'

// Get store
const store = useStore()

// Or use useAsync composable for single API calls
const { data, loading, error, execute } = useAsync(
  () => apiFunction()
)

// Fetch data on mount
onMounted(async () => {
  await execute()
  // OR
  const result = await store.fetchData()
  if (!result.ok) {
    // Handle error
  }
})

// Computed properties
const loading = computed(() => store.loading)
const error = computed(() => store.error)
const items = computed(() => store.items)

// Methods
const refetch = () => execute()
</script>
```

---

## 🔌 Connection Examples

### Example 1: Using Pinia Store

```vue
<script setup>
import { useContestStore } from '@/stores'
import { onMounted } from 'vue'

const contestStore = useContestStore()

onMounted(async () => {
  const result = await contestStore.fetchContests({ page: 0, size: 20 })
  if (!result.ok) {
    console.error('Failed to fetch:', result.message)
  }
})
</script>

<template>
  <div v-if="contestStore.loading">Loading...</div>
  <div v-else-if="contestStore.error">{{ contestStore.error }}</div>
  <div v-else>
    <ContestCard 
      v-for="contest in contestStore.contests"
      :key="contest.id"
      :contest="contest"
    />
  </div>
</template>
```

### Example 2: Using useAsync Composable

```vue
<script setup>
import { contestAPI } from '@/api'
import { useAsync } from '@/composables/useAsync'

const { data: contests, loading, error, execute } = useAsync(
  () => contestAPI.list()
)

// Fetch on mount
onMounted(execute)
</script>

<template>
  <div v-if="loading">Loading...</div>
  <div v-else-if="error">{{ error }}</div>
  <div v-else>
    <ContestCard v-for="c in contests" :key="c.id" :contest="c" />
  </div>
</template>
```

### Example 3: Using useForm for User Input

```vue
<script setup>
import { authAPI } from '@/api'
import { useForm } from '@/composables/useAsync'
import { useRouter } from 'vue-router'

const router = useRouter()

const { formData, errors, loading, submit } = useForm(
  { email: '', password: '' },
  (data) => authAPI.login(data.email, data.password),
  (result) => {
    // On success
    router.push('/dashboard')
  }
)

const handleSubmit = async () => {
  const result = await submit()
  if (!result.ok) {
    console.error('Login failed:', result.message)
  }
}
</script>

<template>
  <form @submit.prevent="handleSubmit">
    <input v-model="formData.email" type="email" />
    <input v-model="formData.password" type="password" />
    
    <div v-if="errors.global" class="error">{{ errors.global }}</div>
    <div v-if="errors.fieldErrors.email" class="error">
      {{ errors.fieldErrors.email }}
    </div>
    
    <button type="submit" :disabled="loading">
      {{ loading ? 'Logging in...' : 'Login' }}
    </button>
  </form>
</template>
```

---

## 📋 Testing Checklist per Component

For each component you convert, verify:

- [ ] API call is made on component mount
- [ ] Loading state shows spinner/skeleton
- [ ] Data displays correctly when API succeeds
- [ ] Error message shows when API fails
- [ ] Retry button works
- [ ] User interactions trigger correct API calls
- [ ] Form validation works before submit
- [ ] Success/error messages appear after submit
- [ ] No hardcoded mock data left
- [ ] No console errors or warnings

---

## 🚀 Priority Order

Connect components in this order (highest to lowest priority):

1. **Auth Components** (Login, Register, Profile)
   - User must be able to login to use app

2. **Dashboard** (Home page with contests)
   - Main user entry point after login

3. **ContestsList** (Browse contests)
   - Core app feature

4. **GlobalLeaderboard** (View rankings)
   - User engagement feature

5. **ProfilePage** (User profile management)
   - User self-service

6. **AdminPages** (Contest/user management)
   - Admin functionality

7. **NotificationCenter** (Notifications)
   - Nice to have

---

## 🔍 Verification Commands

### Check All API Calls Work
```bash
# Open browser DevTools
# Network tab → filter by XHR
# Perform user actions (login, browse, register)
# Verify all requests return 2xx status
# Check response data is displayed correctly
```

### Check Console for Errors
```bash
# Console tab → filter by Error
# Should see no red errors
# Only warnings about unused variables etc.
```

### Check Pinia Store
```bash
# Install Vue DevTools extension
# Open Vue DevTools → Pinia tab
# Select store
# Verify state updates correctly
```

### Check Network Performance
```bash
# Network tab → Performance
# Verify API calls complete in < 2 seconds
# Check for any 401/403/500 errors
# Monitor for infinite loading loops
```

---

## 📝 Notes

- All API base paths assume they go through Nginx reverse proxy at `/api`
- Token is automatically added to requests via axios interceptor
- X-Tenant-Id header is automatically added via interceptor
- On 401 error, user is redirected to login
- All errors are formatted via `handleApiError()` for consistency
- Use `useAsync` composable for single function calls
- Use Pinia stores for shared state across components

