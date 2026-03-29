<!-- src/pages/PaymentCallbackView.vue -->
<template>
  <div class="p-6 max-w-xl mx-auto text-center">
    <div v-if="processing" class="card p-8">
      <p class="text-lg font-semibold mb-2">Confirming your registration...</p>
      <p class="text-sm text-gray-500">Please wait, do not close this tab.</p>
    </div>

    <div v-else-if="confirmed" class="card p-8">
      <p class="text-2xl font-bold text-green-600 mb-2">Registration Confirmed!</p>
      <p class="text-gray-600 mb-4">Your payment was successful and your slot is confirmed.</p>
      <RouterLink
        :to="contestId ? `/contests/${contestId}` : '/contests'"
        class="btn-primary"
      >
        View Contest
      </RouterLink>
    </div>

    <div v-else-if="failed" class="card p-8">
      <p class="text-2xl font-bold text-red-600 mb-2">Payment Failed</p>
      <p class="text-gray-600 mb-4">{{ errorMessage }}</p>
      <RouterLink
        :to="contestId ? `/contests/${contestId}` : '/contests'"
        class="btn-secondary"
      >
        Try Again
      </RouterLink>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { contestAPI, paymentAPI } from '@/api'
import { useUserStore } from '@/stores'

const route     = useRoute()
const router    = useRouter()
const userStore = useUserStore()

const processing   = ref(true)
const confirmed    = ref(false)
const failed       = ref(false)
const errorMessage = ref('')
const contestId    = ref('')

onMounted(async () => {
  const status         = route.query.status
  const registrationId = route.query.udf1    // udf1 = registrationId
  contestId.value      = route.query.udf2 || '' // udf2 = contestId (set early for redirect link)

  console.log('[PaymentCallback] status:', status)
  console.log('[PaymentCallback] registrationId (udf1):', registrationId)
  console.log('[PaymentCallback] contestId (udf2):', contestId.value)

  if (!userStore.isAuthenticated) {
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }

  if (!status || !registrationId) {
    failed.value = true
    errorMessage.value = 'Missing payment information. Please contact support.'
    processing.value = false
    return
  }

  if (String(status).toLowerCase() !== 'success') {
    failed.value = true
    errorMessage.value = 'Payment was not successful. Please try again.'
    processing.value = false
    return
  }

  try {
    // Fetch the payment transaction using registrationId to get paymentTransactionId
    console.log('[PaymentCallback] Fetching payment for registrationId:', registrationId)
    const paymentRes = await paymentAPI.getByRegistration(registrationId)
    console.log('[PaymentCallback] paymentRes:', paymentRes?.data)

    const paymentTransactionId = paymentRes?.data?.data?.id
    console.log('[PaymentCallback] paymentTransactionId:', paymentTransactionId)

    if (!paymentTransactionId) throw new Error('Could not find payment record')

    // Confirm the registration with registrationId + paymentTransactionId
    console.log('[PaymentCallback] Calling confirmPayment...')
    const res  = await contestAPI.confirmPayment(registrationId, paymentTransactionId)
    console.log('[PaymentCallback] confirmPayment response:', res?.data)

    const data = res?.data?.data
    // Prefer contestId from response, fall back to udf2
    contestId.value = data?.contestId || contestId.value

    confirmed.value = true
    console.log('[PaymentCallback] confirmed! contestId:', contestId.value)
  } catch (err) {
    console.error('[PaymentCallback] FAILED:', err)
    console.error('[PaymentCallback] status:', err.response?.status)
    console.error('[PaymentCallback] response body:', err.response?.data)

    // If registration is already CONFIRMED (Kafka processed it before callback), treat as success
    if (err.response?.data?.data?.status === 'CONFIRMED') {
      confirmed.value = true
      return
    }

    failed.value = true
    errorMessage.value =
      err.response?.data?.error ||
      err.response?.data?.message ||
      err.message ||
      'Failed to confirm registration.'
  } finally {
    processing.value = false
  }
})
</script>