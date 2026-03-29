<template>
  <div class="card-sm p-4">
    <div class="flex items-start justify-between mb-4">
      <div>
        <h3 class="font-semibold text-gray-900 dark:text-gray-100">{{ contest.title }}</h3>
        <p class="text-sm text-gray-600 dark:text-gray-400 mt-1">{{ contest.description }}</p>
      </div>
      <div class="flex items-center gap-2">
        <span v-if="isJoined" class="badge badge-success">Joined</span>
        <span :class="['badge', statusClass]">{{ contest.status }}</span>
      </div>
    </div>

    <div class="grid grid-cols-3 gap-4 text-sm mb-4">
      <div>
        <p class="text-gray-600 dark:text-gray-400">Fee</p>
        <p class="font-semibold inline-flex items-center gap-1">
          <PaymentIcon class="w-4 h-4 text-primary-600" />
          Rs. {{ Number(contest.registrationFee || 0).toFixed(2) }}
        </p>
      </div>
      <div>
        <p class="text-gray-600 dark:text-gray-400">Type</p>
        <p class="font-semibold">{{ contest.contestType }}</p>
      </div>
      <div>
        <p class="text-gray-600 dark:text-gray-400">Deadline</p>
        <p class="font-semibold">{{ formatDate(contest.registrationDeadline) }}</p>
      </div>
    </div>

    <div class="flex gap-2">
      <button @click="viewContest" class="btn-primary flex-1 text-center text-sm">
        View
      </button>
      <button v-if="contest.status === 'PUBLISHED'" @click="openRegistration" :disabled="isJoining || isJoined"
        class="btn-secondary flex-1 text-sm">
        {{ isJoined ? 'Registered' : isJoining ? 'Joining...' : 'Register' }}
      </button>
    </div>

    <p v-if="showLoginPrompt" class="text-xs text-amber-700 dark:text-amber-300 mt-3">
      Please
      <RouterLink to="/login" class="font-semibold underline text-primary-700 dark:text-primary-300">
        login
      </RouterLink>
      to view details or register for this contest.
    </p>

    <p v-if="registrationData" class="text-xs text-gray-600 dark:text-gray-400 mt-3">
      Team: {{ registrationData.teamName }} | Contact: {{ registrationData.contact }}
    </p>

    <RegistrationModal v-if="showModal" :contest-title="contest.title" @close="showModal = false"
      @submit="handleModalSubmit" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { contestAPI } from '@/api'
import PaymentIcon from '@/components/common/icons/PaymentIcon.vue'
import RegistrationModal from '@/components/contest/RegistrationModal.vue'
import { getContestRegistration, saveContestRegistration } from '@/utils/localRegistrations'
import { useUserStore } from '@/stores'

const props = defineProps({
  contest: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['joined'])
const router = useRouter()
const userStore = useUserStore()
const isJoining = ref(false)
const showModal = ref(false)
const showLoginPrompt = ref(false)
const registrationState = ref(getContestRegistration(props.contest.id))

const registrationData = computed(() => {
  if (!userStore.isAuthenticated) {
    return null
  }

  return registrationState.value
})

const isJoined = computed(() => !!registrationData.value)

const statusClass = computed(() => {
  const status = props.contest.status
  return {
    'badge-success': status === 'PUBLISHED',
    'badge-warning': status === 'DRAFT',
    'badge-info': status === 'ARCHIVED'
  }[status] || 'badge-info'
})

const formatDate = (date) => new Date(date).toLocaleDateString()

const viewContest = () => {
  if (!userStore.isAuthenticated) {
    showLoginPrompt.value = true
    return
  }

  showLoginPrompt.value = false
  router.push(`/contests/${props.contest.id}`)
}

const openRegistration = () => {
  if (!userStore.isAuthenticated) {
    showLoginPrompt.value = true
    return
  }

  showLoginPrompt.value = false
  showModal.value = true
}

const handleModalSubmit = async (details) => {
  await joinContest(details)
  showModal.value = false
}

const joinContest = async (details) => {
  isJoining.value = true
  try {
    let registrationId = null
    try {
      const response = await contestAPI.register(props.contest.id)
      registrationId = response.data?.data?.id || null

      if (registrationId) {
        await contestAPI.updateRegistrationDetails(registrationId, {
          teamName: details.teamName,
          contactNumber: details.contact,
          couponCode: details.coupon
        })
      }
    } catch (_) {
      // Frontend-only fallback when backend registration API is unavailable.
    }
    registrationState.value = saveContestRegistration(props.contest.id, details)
    emit('joined', props.contest.id)
  } catch (error) {
    console.error('Failed to join contest:', error)
  } finally {
    isJoining.value = false
  }
}
</script>
