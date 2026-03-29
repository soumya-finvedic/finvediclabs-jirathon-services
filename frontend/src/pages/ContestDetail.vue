<template>
    <div class="p-6 max-w-7xl mx-auto">
        <div v-if="loading" class="space-y-4">
            <div class="card h-40 skeleton"></div>
            <div class="card h-64 skeleton"></div>
        </div>

        <div v-else-if="contest">
            <!-- Contest Info Card -->
            <div class="card p-6 mb-6">
                <div class="flex items-start justify-between mb-4 gap-4">
                    <div>
                        <h1 class="text-3xl font-bold mb-2 text-gray-900 dark:text-gray-100">{{ contest.title }}</h1>
                        <p class="text-gray-600 dark:text-gray-400">{{ contest.description }}</p>
                    </div>
                    <span :class="['badge', statusBadgeClass]">{{ contest.status }}</span>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                        <p class="text-sm text-gray-600 dark:text-gray-400">Registration Fee</p>
                        <p class="text-lg font-semibold">{{ formatCurrency(contest.registrationFee || 0) }}</p>
                    </div>
                    <div>
                        <p class="text-sm text-gray-600 dark:text-gray-400">Registration Deadline</p>
                        <p class="text-lg font-semibold">{{ formatDate(contest.registrationDeadline) }}</p>
                    </div>
                    <div>
                        <p class="text-sm text-gray-600 dark:text-gray-400">Contest Window</p>
                        <p class="text-lg font-semibold">
                            {{ formatDate(contest.startTime) }} - {{ formatDate(contest.endTime) }}
                        </p>
                    </div>
                </div>
            </div>

            <!-- Registration & Payment Card -->
            <div class="card p-6 mb-6">
                <h2 class="text-xl font-semibold mb-4 inline-flex items-center gap-2">
                    <PaymentIcon class="w-5 h-5 text-primary-600" />
                    Registration & Payment
                </h2>

                <p v-if="!userStore.isAuthenticated" class="mb-4 text-gray-600 dark:text-gray-300">
                    Please <RouterLink to="/login" class="text-primary-600 underline">login/register</RouterLink> to
                    register for this contest.
                </p>

                <div v-else class="space-y-4">
                    <!-- Action Buttons -->
                    <div class="flex flex-wrap items-center gap-3">
                        <button v-if="userStore.isAdmin && !isContestPublished" class="btn-secondary"
                            @click="publishContest" :disabled="processing">
                            {{ processing ? 'Publishing...' : 'Publish Contest' }}
                        </button>

                        <button v-if="userStore.isAdmin" class="btn-secondary" @click="loadContestRegistrations"
                            :disabled="registrationsLoading">
                            {{ registrationsLoading ? 'Refreshing...' : 'Refresh Registrations' }}
                        </button>

                        <button v-if="!userStore.isAdmin" class="btn-primary" @click="registerContest"
                            :disabled="processing || !canRegister">
                            {{ registration ? 'Registered' : processing ? 'Registering...' : 'Register' }}
                        </button>

                        <button v-if="!userStore.isAdmin" class="btn-secondary" @click="openEditRegistration"
                            :disabled="processing || !canEditRegistration">
                            {{ localRegistration ? 'Edit Registration' : 'Add Registration Details' }}
                        </button>

                        <button
                            v-if="!userStore.isAdmin && registration && registration.status !== 'CONFIRMED'"
                            class="btn-secondary" @click="payNow" :disabled="processing">
                            <span class="inline-flex items-center gap-1">
                                <PaymentIcon class="w-4 h-4" />
                                {{ processing ? 'Processing payment...' : `Pay ${formatCurrency(registration.amount || 0)}` }}
                            </span>
                        </button>

                        <button
                            v-if="!userStore.isAdmin && paymentTransactionId && registration && registration.status !== 'CONFIRMED'"
                            class="btn-secondary" @click="confirmPayment" :disabled="processing">
                            Confirm Payment
                        </button>
                    </div>

                    <!-- Contest status notices -->
                    <p v-if="!isContestPublished" class="text-sm text-amber-600">
                        Contest is in {{ contest?.status }} status. Publish it to enable registration.
                    </p>
                    <p v-else-if="isRegistrationClosed" class="text-sm text-amber-600">
                        Registration deadline has passed for this contest.
                    </p>

                    <!-- ── CONTEST WINDOW ACCESS BLOCK ── -->
                    <div v-if="!userStore.isAdmin && contest.startTime">

                        <!-- Contest has ended -->
                        <div v-if="isContestEnded"
                            class="rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/40 p-4 text-center">
                            <p class="text-sm font-semibold text-gray-500 dark:text-gray-400">This contest has ended.</p>
                        </div>

                        <!-- Contest is LIVE -->
                        <div v-else-if="isContestLive" class="space-y-3">

                            <!-- Paid & confirmed → Enter Contest button -->
                            <div v-if="isPaidAndConfirmed">
                                <button
                                    class="btn-primary inline-flex items-center gap-2 px-6 py-2.5 font-semibold"
                                    @click="enterContest">
                                    <span class="relative flex h-2.5 w-2.5">
                                        <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
                                        <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-green-500"></span>
                                    </span>
                                    Enter Contest
                                </button>
                                <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">
                                    Contest is live · ends {{ formatDate(contest.endTime) }}
                                </p>
                            </div>

                            <!-- Not paid / not confirmed → Locked message -->
                            <div v-else
                                class="rounded-lg border border-amber-300 dark:border-amber-700 bg-amber-50 dark:bg-amber-900/20 p-4">
                                <div class="flex items-start gap-3">
                                    <svg class="w-5 h-5 text-amber-600 dark:text-amber-400 mt-0.5 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                            d="M12 15v2m0 0v2m0-2h2m-2 0H10m2-6V7a4 4 0 00-8 0v4H3a1 1 0 00-1 1v7a1 1 0 001 1h18a1 1 0 001-1v-7a1 1 0 00-1-1h-1V7a4 4 0 00-8 0z" />
                                    </svg>
                                    <div>
                                        <p class="text-sm font-semibold text-amber-700 dark:text-amber-400">
                                            Contest is live — access locked
                                        </p>
                                        <p class="text-xs text-amber-600 dark:text-amber-500 mt-1">
                                            Your payment is not confirmed. Complete payment and confirmation above to unlock the contest arena.
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Before contest starts → Countdown -->
                        <div v-else
                            class="rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/40 p-4">
                            <p class="text-xs text-gray-500 dark:text-gray-400 mb-1 text-center">Contest starts in</p>
                            <p class="text-2xl font-bold tabular-nums text-center tracking-widest text-gray-800 dark:text-gray-100">
                                {{ countdown }}
                            </p>
                            <p v-if="isPaidAndConfirmed" class="text-xs text-green-600 dark:text-green-400 text-center mt-2">
                                You're registered &amp; paid — you'll be able to enter when the window opens.
                            </p>
                            <p v-else class="text-xs text-amber-600 dark:text-amber-400 text-center mt-2">
                                Complete payment before the contest starts to gain access.
                            </p>
                        </div>
                    </div>
                    <!-- ── END CONTEST WINDOW ACCESS BLOCK ── -->

                    <!-- Admin Registrations Panel -->
                    <div v-if="userStore.isAdmin" class="space-y-3">
                        <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
                            <div class="card-sm p-3">
                                <p class="text-xs text-gray-500 dark:text-gray-400">Total Registrations</p>
                                <p class="text-xl font-semibold">{{ contestRegistrations.length }}</p>
                            </div>
                            <div class="card-sm p-3">
                                <p class="text-xs text-gray-500 dark:text-gray-400">Successful Payments</p>
                                <p class="text-xl font-semibold">{{ paidRegistrationsCount }}</p>
                            </div>
                            <div class="card-sm p-3">
                                <p class="text-xs text-gray-500 dark:text-gray-400">Pending Payments</p>
                                <p class="text-xl font-semibold">{{ contestRegistrations.length - paidRegistrationsCount }}</p>
                            </div>
                        </div>

                        <div class="card-sm p-4 overflow-auto">
                            <p class="text-sm font-semibold mb-2">Student Registrations & Payment Status</p>
                            <p v-if="registrationsLoading" class="text-sm text-gray-600 dark:text-gray-300">Loading registrations...</p>
                            <p v-else-if="contestRegistrations.length === 0" class="text-sm text-gray-600 dark:text-gray-300">No registrations yet.</p>
                            <table v-else class="table w-full text-sm">
                                <thead>
                                    <tr>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Team/Individual</th>
                                        <th>Contact</th>
                                        <th>Registration</th>
                                        <th>Payment</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item in contestRegistrations" :key="item.id">
                                        <td>{{ item.userDisplayName || '-' }}</td>
                                        <td>{{ item.userEmail || '-' }}</td>
                                        <td>{{ item.teamName || '-' }}</td>
                                        <td>{{ item.contactNumber || '-' }}</td>
                                        <td>{{ item.status || '-' }}</td>
                                        <td>{{ item.paymentStatus || 'PENDING' }}</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <!-- Registration info for user -->
                    <p v-if="!userStore.isAdmin && registration" class="text-sm text-gray-700 dark:text-gray-200">
                        Registration ID: {{ registration.id }}
                    </p>

                    <div v-if="!userStore.isAdmin && paymentDetails" class="card-sm p-4">
                        <p class="text-sm font-semibold mb-2">Payment Details</p>
                        <p class="text-sm text-gray-700 dark:text-gray-200">Transaction ID: {{ paymentDetails.transactionId }}</p>
                        <p class="text-sm text-gray-700 dark:text-gray-200">Gateway Txn ID: {{ paymentDetails.payuTxnId }}</p>
                        <p class="text-sm text-gray-700 dark:text-gray-200">Amount: {{ formatCurrency(paymentDetails.payableAmount) }}</p>
                    </div>

                    <p v-if="!userStore.isAdmin && registration" class="text-sm text-gray-700 dark:text-gray-200">
                        Registration Status:
                        <span class="font-semibold">{{ registration.status }}</span>
                        <span v-if="registration.paymentStatus"> | Payment: <span class="font-semibold">{{
                            registration.paymentStatus }}</span></span>
                    </p>

                    <div v-if="!userStore.isAdmin && localRegistration" class="card-sm p-4">
                        <p class="text-sm font-semibold mb-2">Saved Registration Details</p>
                        <p class="text-sm text-gray-700 dark:text-gray-200">{{ registrationNameLabel }}: {{ localRegistration.teamName }}</p>
                        <p class="text-sm text-gray-700 dark:text-gray-200">Contact: {{ localRegistration.contact }}</p>
                        <p v-if="localRegistration.coupon" class="text-sm text-gray-700 dark:text-gray-200">Coupon: {{
                            localRegistration.coupon }}</p>
                        <p class="text-xs text-gray-500 dark:text-gray-400 mt-2">
                            Updated: {{ formatDate(localRegistration.registeredAt) }}
                        </p>
                    </div>

                    <p v-if="registrationMessage" class="text-sm text-green-600">{{ registrationMessage }}</p>
                    <p v-if="registrationError" class="text-sm text-red-600">{{ registrationError }}</p>
                </div>
            </div>

            <!-- Rules & Languages Card -->
            <div class="card p-6">
                <h2 class="text-xl font-semibold mb-3">Rules</h2>
                <ul class="list-disc pl-6 space-y-1 text-gray-700 dark:text-gray-300">
                    <li v-for="rule in contest.rules || []" :key="rule">{{ rule }}</li>
                </ul>

                <h2 class="text-xl font-semibold mt-6 mb-3">Supported Languages</h2>
                <div class="flex flex-wrap gap-2">
                    <span v-for="lang in contest.supportedLanguages || []" :key="lang" class="badge badge-info">{{ lang }}</span>
                </div>
            </div>
        </div>

        <div v-else class="card p-6 text-center">
            <p class="text-gray-500 dark:text-gray-400">Contest not found.</p>
        </div>

        <RegistrationModal v-if="showRegistrationModal && contest" :contest-title="contest.title"
            :contest-type="contest.contestType"
            :initial-data="localRegistration || {}" submit-label="Save Registration Details"
            @close="showRegistrationModal = false" @submit="saveRegistrationDetails" />
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useContest } from '@/composables/useContests'
import { adminContestAPI, contestAPI, paymentAPI } from '@/api'
import { useUserStore } from '@/stores'
import RegistrationModal from '@/components/contest/RegistrationModal.vue'
import PaymentIcon from '@/components/common/icons/PaymentIcon.vue'
import { getContestRegistration, saveContestRegistration } from '@/utils/localRegistrations'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { contest, loading, fetchContest } = useContest(route.params.id)

const registration = ref(null)
const paymentTransactionId = ref('')
const processing = ref(false)
const registrationMessage = ref('')
const registrationError = ref('')
const showRegistrationModal = ref(false)
const localRegistration = ref(null)
const paymentDetails = ref(null)
const contestRegistrations = ref([])
const registrationsLoading = ref(false)

// ── Countdown ──────────────────────────────────────────────────────────────
const countdown = ref('')
let countdownTimer = null

const pad = (n) => String(n).padStart(2, '0')

const tickCountdown = () => {
    if (!contest.value) return
    const now = Date.now()
    const start = new Date(contest.value.startTime).getTime()
    const end = new Date(contest.value.endTime).getTime()

    if (now >= end) {
        countdown.value = '00:00:00'
        clearInterval(countdownTimer)
        return
    }

    const target = now < start ? start : end
    const diff = target - now
    const h = Math.floor(diff / 3_600_000)
    const m = Math.floor((diff % 3_600_000) / 60_000)
    const s = Math.floor((diff % 60_000) / 1_000)
    countdown.value = `${pad(h)}:${pad(m)}:${pad(s)}`
}

const startCountdown = () => {
    tickCountdown()
    countdownTimer = setInterval(tickCountdown, 1000)
}

onUnmounted(() => clearInterval(countdownTimer))

// ── Computed flags ─────────────────────────────────────────────────────────
const isContestLive = computed(() => {
    if (!contest.value) return false
    const now = Date.now()
    return new Date(contest.value.startTime).getTime() <= now &&
        now <= new Date(contest.value.endTime).getTime()
})

const isContestEnded = computed(() => {
    if (!contest.value) return false
    return Date.now() > new Date(contest.value.endTime).getTime()
})

const isPaidAndConfirmed = computed(() => {
    return (
        registration.value?.status === 'CONFIRMED' &&
        String(registration.value?.paymentStatus || '').toUpperCase() === 'SUCCESS'
    )
})

const statusBadgeClass = computed(() => {
    const status = contest.value?.status
    return (
        {
            'badge-success': status === 'PUBLISHED',
            'badge-warning': status === 'DRAFT',
            'badge-info': status === 'ARCHIVED',
        }[status] || 'badge-info'
    )
})

const isContestPublished = computed(() =>
    String(contest.value?.status || '').toUpperCase() === 'PUBLISHED'
)

const isRegistrationClosed = computed(() => {
    const deadline = contest.value?.registrationDeadline
    if (!deadline) return false
    return new Date(deadline).getTime() <= Date.now()
})

const canRegister = computed(() => {
    return !!contest.value && isContestPublished.value && !isRegistrationClosed.value && !registration.value
})

const canEditRegistration = computed(() => {
    return !!registration.value || canRegister.value
})

const registrationNameLabel = computed(() => {
    return String(contest.value?.contestType || '').toUpperCase() === 'TEAM' ? 'Team' : 'Individual'
})

const paidRegistrationsCount = computed(() => {
    return contestRegistrations.value.filter(
        (item) => String(item.paymentStatus || '').toUpperCase() === 'SUCCESS'
    ).length
})

// ── Navigate to arena ──────────────────────────────────────────────────────
const enterContest = () => router.push({ name: 'ContestArena', params: { id: route.params.id } })

// ── API calls ──────────────────────────────────────────────────────────────
const loadMyRegistration = async () => {
    if (!userStore.isAuthenticated) {
        registration.value = null
        return
    }
    try {
        const response = await contestAPI.getMyRegistration(route.params.id)
        const data = response.data.data
        registration.value = data
        paymentTransactionId.value = data?.paymentTransactionId || ''

        if (data?.paymentTransactionId) {
            try {
                const payRes = await paymentAPI.getByRegistration(data.id)
                const txn = payRes?.data?.data
                if (txn) {
                    paymentDetails.value = {
                        transactionId: txn.id,
                        payuTxnId: txn.payuTxnId,
                        payableAmount: txn.payableAmount,
                    }
                }
            } catch (_) { /* not critical */ }
        }

        if (data?.teamName || data?.contactNumber || data?.couponCode) {
            localRegistration.value = saveContestRegistration(route.params.id, {
                teamName: data.teamName || '',
                contact: data.contactNumber || '',
                coupon: data.couponCode || '',
            })
        }
    } catch (_) {
        registration.value = null
    }
}

const loadContestRegistrations = async () => {
    if (!userStore.isAdmin || !contest.value?.id) {
        contestRegistrations.value = []
        return
    }
    registrationsLoading.value = true
    try {
        const response = await adminContestAPI.listRegistrations(route.params.id)
        const data = response?.data?.data
        contestRegistrations.value = Array.isArray(data) ? data : []
    } catch (_) {
        contestRegistrations.value = []
    } finally {
        registrationsLoading.value = false
    }
}

onMounted(async () => {
    await fetchContest()
    if (userStore.isAdmin) {
        await loadContestRegistrations()
    } else {
        localRegistration.value = getContestRegistration(route.params.id)
        await loadMyRegistration()
    }
    if (contest.value) startCountdown()
})

// ── Actions ────────────────────────────────────────────────────────────────
const publishContest = async () => {
    if (!contest.value?.id || !userStore.isAdmin) return
    registrationMessage.value = ''
    registrationError.value = ''
    processing.value = true
    try {
        const payload = {
            title: contest.value.title,
            slug: contest.value.slug,
            description: contest.value.description,
            rules: contest.value.rules || [],
            contestType: contest.value.contestType,
            supportedLanguages: contest.value.supportedLanguages || [],
            registrationDeadline: contest.value.registrationDeadline,
            startTime: contest.value.startTime,
            endTime: contest.value.endTime,
            registrationFee: Number(contest.value.registrationFee || 0),
            status: 'PUBLISHED',
        }
        await adminContestAPI.update(contest.value.id, payload)
        await fetchContest()
        await loadContestRegistrations()
        registrationMessage.value = 'Contest published successfully. Registration is now enabled.'
    } catch (err) {
        registrationError.value = err.response?.data?.error || err.response?.data?.message || err.message
    } finally {
        processing.value = false
    }
}

const registerContest = async () => {
    registrationMessage.value = ''
    registrationError.value = ''
    if (!isContestPublished.value) {
        registrationError.value = 'Contest must be published before users can register.'
        return
    }
    if (isRegistrationClosed.value) {
        registrationError.value = 'Registration deadline has passed.'
        return
    }
    processing.value = true
    try {
        const response = await contestAPI.register(route.params.id)
        registration.value = response.data.data
        registrationMessage.value = 'Registration created. Complete payment to confirm your slot.'
    } catch (err) {
        registrationError.value = err.response?.data?.error || err.response?.data?.message || err.message
    } finally {
        processing.value = false
    }
}

const payNow = async () => {
    if (!registration.value || !contest.value) return
    registrationMessage.value = ''
    registrationError.value = ''
    processing.value = true
    try {
        const user = userStore.user || {}
        const name = user.displayName || user.name || user.fullName || user.email || 'Unknown'
        const email = user.email || user.emailAddress || ''
        const phone = user.phone || user.phoneNumber || user.mobile || '9999999999'

        if (!email) {
            registrationError.value = 'User email not found. Please log out and log in again.'
            processing.value = false
            return
        }

        const payload = {
            registrationId: registration.value.id,
            contestId: contest.value.id,
            userId: userStore.user?.id || user.userId || user._id || '',
            name,
            email,
            phone,
            amount: registration.value.amount || contest.value.registrationFee || 0,
        }

        const initResponse = await paymentAPI.initiatePayment(payload)
        const paymentData = initResponse?.data?.data || {}
        paymentTransactionId.value = paymentData.transactionId || ''
        paymentDetails.value = {
            transactionId: paymentData.transactionId || '',
            payuTxnId: paymentData.payuTxnId || '',
            payableAmount: paymentData.payableAmount || payload.amount,
        }

        if (paymentData.paymentUrl && paymentData.payuFormData) {
            submitPayUForm(paymentData.paymentUrl, paymentData.payuFormData)
            return
        }

        await paymentAPI.simulateSuccess(paymentTransactionId.value)
        registrationMessage.value = 'Payment simulated. Please click Confirm Payment.'
    } catch (err) {
        registrationError.value = err.response?.data?.error || err.response?.data?.message || err.message
        processing.value = false
    }
}

const submitPayUForm = (paymentUrl, formData) => {
    const form = document.createElement('form')
    form.method = 'POST'
    form.action = paymentUrl
    Object.entries(formData || {}).forEach(([key, value]) => {
        const input = document.createElement('input')
        input.type = 'hidden'
        input.name = key
        input.value = value == null ? '' : String(value)
        form.appendChild(input)
    })
    document.body.appendChild(form)
    form.submit()
    document.body.removeChild(form)
}

const confirmPayment = async () => {
    if (!registration.value || !paymentTransactionId.value) return
    registrationMessage.value = ''
    registrationError.value = ''
    processing.value = true
    try {
        const response = await contestAPI.confirmPayment(registration.value.id, paymentTransactionId.value)
        registration.value = response.data.data
        registrationMessage.value = 'Registration confirmed. You are fully enrolled in this contest.'
    } catch (err) {
        registrationError.value = err.response?.data?.error || err.response?.data?.message || err.message
    } finally {
        processing.value = false
    }
}

const openEditRegistration = () => {
    if (!canEditRegistration.value) {
        registrationError.value = 'Publish the contest first to add registration details.'
        return
    }
    showRegistrationModal.value = true
}

const saveRegistrationDetails = async (details) => {
    registrationMessage.value = ''
    registrationError.value = ''
    localRegistration.value = saveContestRegistration(route.params.id, details)
    if (registration.value?.id) {
        try {
            const response = await contestAPI.updateRegistrationDetails(registration.value.id, {
                teamName: details.teamName,
                contactNumber: details.contact,
                couponCode: details.coupon,
            })
            registration.value = response.data.data
            registrationMessage.value = 'Registration details saved to backend.'
        } catch (err) {
            registrationError.value =
                err.response?.data?.error || err.response?.data?.message || 'Saved locally only.'
        }
    } else {
        registrationMessage.value = 'Details saved locally. Register first to sync with backend.'
    }
    showRegistrationModal.value = false
}

const formatDate = (date) => new Date(date).toLocaleString()
const formatCurrency = (amount) => `Rs. ${Number(amount || 0).toFixed(2)}`
</script>