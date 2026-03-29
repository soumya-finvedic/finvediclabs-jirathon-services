<template>
    <div class="auth-shell min-h-screen flex items-center justify-center p-6 md:p-10">
        <div class="w-full max-w-6xl grid grid-cols-1 lg:grid-cols-2 gap-10 xl:gap-16 items-center">
            <section class="order-2 lg:order-1 left-brand-panel">
                <h1 class="welcome-title">Welcome to</h1>
                <div class="logo-block">
                    <img src="/scalegrad-logo-full.png" alt="ScaleGrad" class="scalegrad-logo" loading="eager" />
                </div>
                <p class="brand-copy">
                    ScaleGrad is an interactive learning platform offering financial education through curated
                    resources, courses, and collaborative tools for students, faculty, and professionals.
                </p>
                <img src="/login-illustration.svg" alt="Learning platform illustration" class="brand-illustration" />
            </section>

            <section class="order-1 lg:order-2">
                <!-- Modern Segmented Control -->
                <div class="mb-8 flex justify-center">
                    <div class="bg-gray-100 rounded-full p-1 flex gap-2 w-full max-w-sm shadow-md">
                        <button
                            @click="setPortal('jirathon')"
                            :class="[
                                'flex-1 py-3 px-4 rounded-full font-semibold transition-all duration-300 flex items-center justify-center gap-2',
                                selectedPortal === 'jirathon'
                                    ? 'bg-blue-600 text-white shadow-lg scale-105'
                                    : 'text-gray-600 hover:text-gray-800'
                            ]"
                        >
                            <span class="text-lg">📚</span>
                            <span class="hidden sm:inline">Jirathon</span>
                        </button>
                        <button
                            @click="setPortal('hackathon')"
                            :class="[
                                'flex-1 py-3 px-4 rounded-full font-semibold transition-all duration-300 flex items-center justify-center gap-2',
                                selectedPortal === 'hackathon'
                                    ? 'bg-purple-600 text-white shadow-lg scale-105'
                                    : 'text-gray-600 hover:text-gray-800'
                            ]"
                        >
                            <span class="text-lg">🚀</span>
                            <span class="hidden sm:inline">Hackathon</span>
                        </button>
                    </div>
                </div>

                <!-- Login/Register Form Container -->
                <div class="rounded-3xl border-2 bg-white p-8 md:p-10 shadow-xl"
                    :class="selectedPortal === 'hackathon' ? 'border-purple-200' : 'border-blue-200'">

                    <div class="text-center mb-8">
                        <h2 class="text-4xl font-bold mb-2"
                            :class="selectedPortal === 'hackathon' ? 'text-purple-900' : 'text-blue-900'">
                            {{ pageTitle }}
                        </h2>
                        <p class="text-gray-600">
                            {{ selectedPortal === 'hackathon' 
                                ? 'Build, innovate, and compete' 
                                : 'Learn, grow, and excel' }}
                        </p>
                    </div>

                    <div class="h-px bg-gradient-to-r from-transparent via-gray-300 to-transparent mb-8"></div>

                    <!-- OTP Verification Step -->
                    <div v-if="activeTab === 'verify-otp'" class="space-y-4">
                        <div class="text-sm text-slate-600 bg-blue-50 border border-blue-200 rounded-lg p-3">
                            A 6-digit verification code has been sent to
                            <span class="font-semibold text-slate-800">{{ pendingEmail }}</span>.
                            Enter it below to activate your account.
                        </div>

                        <div>
                            <label class="sr-only">Verification Code</label>
                            <input v-model.trim="otpCode" class="auth-input tracking-widest text-center text-xl" type="text"
                                inputmode="numeric" maxlength="6" placeholder="000000" autocomplete="one-time-code" />
                        </div>

                        <p v-if="error" class="text-sm text-red-600">{{ error }}</p>
                        <p v-if="success"
                            class="text-sm text-green-700 bg-green-50 border border-green-200 rounded-md px-3 py-2">
                            {{ success }}
                        </p>

                        <button class="auth-submit w-full font-semibold rounded-lg transition-colors duration-200" 
                            :disabled="submitting || otpCode.length !== 6"
                            :class="selectedPortal === 'hackathon'
                                ? 'bg-purple-600 hover:bg-purple-700 disabled:bg-purple-300 text-white'
                                : 'bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white'"
                            @click="verifyOtpCode">
                            {{ submitting ? 'Verifying...' : 'Verify & Activate Account' }}
                        </button>

                        <div class="flex items-center justify-between text-sm">
                            <button type="button" class="font-medium"
                                :class="selectedPortal === 'hackathon' ? 'text-purple-600 hover:text-purple-800' : 'text-blue-600 hover:text-blue-800'"
                                :disabled="resending" @click="resendOtp">
                                {{ resending ? 'Sending...' : 'Resend OTP' }}
                            </button>
                            <button type="button" class="text-slate-500 hover:text-slate-700"
                                @click="activeTab = 'register'">
                                Back to Sign up
                            </button>
                    </div>
                </div>

                    <!-- Forgot Password Step 1: Request Reset -->
                    <div v-else-if="activeTab === 'forgot-password-request'" class="space-y-4">
                        <div class="text-sm text-slate-600 bg-yellow-50 border border-yellow-200 rounded-lg p-3">
                            Enter your email address and we'll send you a link to reset your password.
                        </div>

                        <div>
                            <label class="sr-only">Email</label>
                            <input v-model.trim="forgotPasswordEmail" class="w-full px-4 py-3 rounded-xl border-2 border-gray-200 focus:border-blue-500 focus:outline-none transition" type="email" placeholder="Email Address" />
                        </div>

                        <p v-if="error" class="text-sm text-red-600 bg-red-50 p-3 rounded-lg">{{ error }}</p>
                        <p v-if="success" class="text-sm text-green-700 bg-green-50 border border-green-200 rounded-md px-3 py-2">{{ success }}</p>

                        <button class="auth-submit w-full font-semibold rounded-lg transition-colors duration-200"
                            :disabled="submitting || !forgotPasswordEmail"
                            :class="selectedPortal === 'hackathon'
                                ? 'bg-purple-600 hover:bg-purple-700 disabled:bg-purple-300 text-white'
                                : 'bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white'"
                            @click="requestPasswordReset">
                            {{ submitting ? 'Sending...' : 'Send Reset Link' }}
                        </button>

                        <button type="button" class="text-slate-500 hover:text-slate-700 text-sm w-full py-2"
                            @click="cancelForgotPassword">
                            Back to Login
                        </button>
                    </div>

                    <!-- Forgot Password Step 2: Reset Password -->
                    <div v-else-if="activeTab === 'forgot-password-reset'" class="space-y-4">
                        <div class="text-sm text-slate-600 bg-blue-50 border border-blue-200 rounded-lg p-3">
                            Check your email for the password reset link. Enter the reset token and your new password below.
                        </div>

                        <div>
                            <label class="sr-only">Reset Token</label>
                            <input v-model.trim="resetToken" class="w-full px-4 py-3 rounded-xl border-2 border-gray-200 focus:border-blue-500 focus:outline-none transition text-center tracking-widest font-mono" type="text" placeholder="Enter reset token from email" />
                        </div>

                        <div class="relative">
                            <label class="sr-only">New Password</label>
                            <input v-model="newPassword" class="w-full px-4 py-3 rounded-xl border-2 border-gray-200 focus:border-blue-500 focus:outline-none transition pr-12"
                                :type="showNewPassword ? 'text' : 'password'" placeholder="New Password (min. 8 characters)" minlength="8" />
                            <button type="button" class="absolute right-4 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700" @click="showNewPassword = !showNewPassword"
                                :aria-label="showNewPassword ? 'Hide password' : 'Show password'">
                                <svg v-if="!showNewPassword" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                    stroke-width="2" class="h-5 w-5">
                                    <path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6-10-6-10-6Z" />
                                    <circle cx="12" cy="12" r="3" />
                                </svg>
                                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                                    class="h-5 w-5">
                                    <path d="M3 3l18 18" />
                                    <path d="M10.6 10.7A3 3 0 0 0 13.4 13.5" />
                                    <path d="M9.4 5.1A11 11 0 0 1 12 5c6.5 0 10 7 10 7a18.7 18.7 0 0 1-4.2 5.2" />
                                    <path d="M6.7 6.7C4.2 8.4 2.7 11 2 12c0 0 3.5 7 10 7 1.8 0 3.3-.4 4.6-1" />
                                </svg>
                            </button>
                        </div>

                        <div class="relative">
                            <label class="sr-only">Confirm Password</label>
                            <input v-model="confirmPassword" class="w-full px-4 py-3 rounded-xl border-2 border-gray-200 focus:border-blue-500 focus:outline-none transition pr-12"
                                :type="showConfirmPassword ? 'text' : 'password'" placeholder="Confirm Password" minlength="8" />
                            <button type="button" class="absolute right-4 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700" @click="showConfirmPassword = !showConfirmPassword"
                                :aria-label="showConfirmPassword ? 'Hide password' : 'Show password'">
                                <svg v-if="!showConfirmPassword" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                    stroke-width="2" class="h-5 w-5">
                                    <path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6-10-6-10-6Z" />
                                    <circle cx="12" cy="12" r="3" />
                                </svg>
                                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                                    class="h-5 w-5">
                                    <path d="M3 3l18 18" />
                                    <path d="M10.6 10.7A3 3 0 0 0 13.4 13.5" />
                                    <path d="M9.4 5.1A11 11 0 0 1 12 5c6.5 0 10 7 10 7a18.7 18.7 0 0 1-4.2 5.2" />
                                    <path d="M6.7 6.7C4.2 8.4 2.7 11 2 12c0 0 3.5 7 10 7 1.8 0 3.3-.4 4.6-1" />
                                </svg>
                            </button>
                        </div>

                        <p v-if="newPassword && confirmPassword && newPassword !== confirmPassword" class="text-sm text-yellow-600 bg-yellow-50 p-3 rounded-lg">⚠️ Passwords do not match</p>

                        <p v-if="error" class="text-sm text-red-600 bg-red-50 p-3 rounded-lg">{{ error }}</p>
                        <p v-if="success" class="text-sm text-green-700 bg-green-50 border border-green-200 rounded-md px-3 py-2">{{ success }}</p>

                        <button class="auth-submit w-full font-semibold rounded-lg transition-colors duration-200"
                            :disabled="submitting || !resetToken || !newPassword || !confirmPassword || newPassword.length < 8 || newPassword !== confirmPassword"
                            :class="selectedPortal === 'hackathon'
                                ? 'bg-purple-600 hover:bg-purple-700 disabled:bg-purple-300 text-white'
                                : 'bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white'"
                            @click="submitPasswordReset">
                            {{ submitting ? 'Resetting...' : 'Reset Password' }}
                        </button>

                        <button type="button" class="text-slate-500 hover:text-slate-700 text-sm w-full py-2"
                            @click="cancelForgotPassword">
                            Back to Login
                        </button>
                    </div>

                    <!-- Login / Register Form -->
                    <form v-else class="space-y-5" @submit.prevent="submitAuth">
                        <div v-if="activeTab === 'register'">
                            <label class="sr-only">Username</label>
                            <input v-model.trim="form.username" class="w-full px-4 py-3 rounded-xl border-2 border-gray-200 focus:border-blue-500 focus:outline-none transition" type="text"
                                placeholder="Username (e.g. john_doe)" required />
                        </div>

                        <div>
                            <label class="sr-only">Email</label>
                            <input v-model.trim="form.email" class="w-full px-4 py-3 rounded-xl border-2 border-gray-200 focus:border-blue-500 focus:outline-none transition" type="email" placeholder="Email Address"
                                required />
                        </div>

                        <div v-if="activeTab === 'register'">
                            <label class="sr-only">Phone Number</label>
                            <input v-model.trim="form.phoneNumber" class="w-full px-4 py-3 rounded-xl border-2 border-gray-200 focus:border-blue-500 focus:outline-none transition" type="tel"
                                placeholder="Phone Number (e.g. +919876543210)" />
                        </div>

                        <div class="relative">
                            <label class="sr-only">Password</label>
                            <input v-model="form.password" class="w-full px-4 py-3 rounded-xl border-2 border-gray-200 focus:border-blue-500 focus:outline-none transition pr-12"
                                :type="showPassword ? 'text' : 'password'" placeholder="Password (min. 8 characters)" minlength="8" required />
                            <button type="button" class="absolute right-4 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700" @click="showPassword = !showPassword"
                                :aria-label="showPassword ? 'Hide password' : 'Show password'">
                                <svg v-if="!showPassword" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                    stroke-width="2" class="h-5 w-5">
                                    <path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6-10-6-10-6Z" />
                                    <circle cx="12" cy="12" r="3" />
                                </svg>
                                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                                    class="h-5 w-5">
                                    <path d="M3 3l18 18" />
                                    <path d="M10.6 10.7A3 3 0 0 0 13.4 13.5" />
                                    <path d="M9.4 5.1A11 11 0 0 1 12 5c6.5 0 10 7 10 7a18.7 18.7 0 0 1-4.2 5.2" />
                                    <path d="M6.7 6.7C4.2 8.4 2.7 11 2 12c0 0 3.5 7 10 7 1.8 0 3.3-.4 4.6-1" />
                                </svg>
                            </button>
                        </div>

                        <div v-if="activeTab === 'login'" class="flex items-center justify-between text-sm">
                            <label class="inline-flex items-center gap-2 text-gray-700 cursor-pointer hover:text-gray-900">
                                <input type="checkbox"
                                    :class="selectedPortal === 'hackathon' 
                                        ? 'h-4 w-4 rounded border-gray-400 text-purple-600 focus:ring-purple-500' 
                                        : 'h-4 w-4 rounded border-gray-400 text-blue-600 focus:ring-blue-500'" />
                                Remember me
                            </label>
                            <button type="button" 
                                @click="startForgotPassword"
                                :class="selectedPortal === 'hackathon' 
                                    ? 'text-purple-600 hover:text-purple-800' 
                                    : 'text-blue-600 hover:text-blue-800'"
                                class="font-medium">
                                Forgot password?
                            </button>
                        </div>

                        <p v-if="error" class="text-sm text-red-600 bg-red-50 p-3 rounded-lg">{{ error }}</p>
                        <p v-if="success" class="text-sm text-green-700 bg-green-50 border border-green-200 rounded-md px-3 py-2">{{ success }}</p>

                        <button class="w-full py-3.5 rounded-xl font-bold text-white transition-all duration-300 hover:shadow-lg transform hover:scale-105" 
                            :disabled="submitting"
                            :class="selectedPortal === 'hackathon'
                                ? 'bg-purple-600 hover:bg-purple-700 disabled:bg-purple-300'
                                : 'bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300'">
                            {{ submitting ? 'Please wait...' : activeTab === 'login' ? 'Login' : 'Create Account' }}
                        </button>
                    </form>

                    <div class="mt-6 text-center text-sm text-gray-700">
                        <template v-if="activeTab !== 'verify-otp'">
                            <span>{{ activeTab === 'login' ? 'Need an account?' : 'Already have an account?' }}</span>
                            <button type="button" class="ml-2 font-bold"
                                :class="selectedPortal === 'hackathon' 
                                    ? 'text-purple-600 hover:text-purple-800' 
                                    : 'text-blue-600 hover:text-blue-800'"
                                @click="toggleAuthMode">
                                {{ activeTab === 'login' ? 'Sign up' : 'Login' }}
                            </button>
                        </template>
                    </div>
                </div>
            </section>
        </div>
    </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authAPI } from '@/api'
import { useUserStore } from '@/stores'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')
const submitting = ref(false)
const error = ref('')
const success = ref('')
const showPassword = ref(false)
const selectedPortal = ref(localStorage.getItem('tenantId') || 'jirathon')

// Portal branding configuration
const portalBranding = {
    jirathon: {
        name: 'Jirathon',
        logo: '/scalegrad-logo-full.png',
        description: 'ScaleGrad is an interactive learning platform offering financial education through curated resources, courses, and collaborative tools for students, faculty, and professionals.',
        color: 'blue',
        bgGradient: 'from-blue-50 to-indigo-50',
        accentColor: 'blue-600',
        hoverColor: 'blue-700',
        emoji: '📚'
    },
    hackathon: {
        name: 'Hackathon',
        logo: '/hackathon-logo-full.svg',
        description: 'Hackathon is a platform for developers and innovators to showcase their skills, collaborate on creative projects, and compete for exciting prizes and opportunities.',
        color: 'purple',
        bgGradient: 'from-purple-50 to-blue-50',
        accentColor: 'purple-600',
        hoverColor: 'purple-700',
        emoji: '🚀'
    }
}

const portals = ref([
    { id: 'hackathon', label: 'Hackathon login' },
    { id: 'jirathon', label: 'Jirathon login' }
])

const form = ref({
    username: '',
    email: '',
    phoneNumber: '',
    password: ''
})
const otpCode = ref('')
const pendingEmail = ref('')
const resending = ref(false)
const forgotPasswordEmail = ref('')
const resetToken = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const pageTitle = computed(() => {
    if (activeTab.value === 'verify-otp') return 'Verify your email'
    if (activeTab.value === 'login') return 'Login and start learning'
    return 'Create your account'
})

const setPortal = (portal) => {
    selectedPortal.value = portal
    localStorage.setItem('tenantId', portal)
}

const toggleAuthMode = () => {
    activeTab.value = activeTab.value === 'login' ? 'register' : 'login'
    error.value = ''
    success.value = ''
}

const loginAcrossPortals = async (email, password) => {
    const portalIds = portals.value.map((portal) => portal.id).filter(Boolean)
    const loginOrder = [...new Set([selectedPortal.value, ...portalIds])]

    for (const portalId of loginOrder) {
        setPortal(portalId)
        const ok = await userStore.login(email, password)
        if (ok) {
            return true
        }
    }

    return false
}

const loadPortals = async () => {
    try {
        const response = await authAPI.getPortals()
        const portalItems = response.data?.data?.portals
        const defaultPortal = response.data?.data?.defaultPortal

        if (Array.isArray(portalItems) && portalItems.length > 0) {
            portals.value = portalItems.map((portal) => ({
                id: portal.id,
                label: portal.label
            }))
        }

        const validPortal = portals.value.some((portal) => portal.id === selectedPortal.value)
        if (!validPortal) {
            setPortal(defaultPortal || 'jirathon')
        }
    } catch (loadError) {
        console.error('Failed to load auth portals:', loadError)
    }
}

const submitAuth = async () => {
    submitting.value = true
    error.value = ''
    success.value = ''
    const redirectPath = typeof route.query.redirect === 'string' ? route.query.redirect : '/contests'

    try {
        if (activeTab.value === 'login') {
            const ok = await loginAcrossPortals(form.value.email, form.value.password)
            if (!ok) {
                error.value = 'Login failed. Please check your credentials.'
                return
            }

            await router.push(userStore.isAdmin ? '/admin' : redirectPath)
            return
        }

        const response = await authAPI.register({
            username: form.value.username,
            displayName: form.value.username,
            email: form.value.email,
            phoneNumber: form.value.phoneNumber || undefined,
            password: form.value.password,
            tenantId: selectedPortal.value
        })

        if (response?.data?.success === false) {
            error.value = response?.data?.error || response?.data?.message || 'Registration failed.'
            return
        }

        pendingEmail.value = form.value.email
        activeTab.value = 'verify-otp'
        success.value = `A 6-digit OTP has been sent to ${form.value.email}. Please verify to activate your account.`
    } catch (authError) {
        if (authError?.code === 'ECONNABORTED') {
            error.value = 'Request timed out. Please ensure auth-service (8081) is running.'
        } else if (!authError?.response) {
            error.value = 'Cannot reach auth service. Please check backend services and try again.'
        } else {
            error.value = authError?.response?.data?.error || authError?.response?.data?.message || authError?.message || 'Authentication failed.'
        }
    } finally {
        submitting.value = false
    }
}

const verifyOtpCode = async () => {
    submitting.value = true
    error.value = ''
    success.value = ''
    try {
        const response = await authAPI.verifyOtp({ email: pendingEmail.value, otp: otpCode.value, purpose: 'EMAIL_VERIFICATION' })
        
        // The backend now returns the updated user data with emailVerified: true
        if (response?.data?.data) {
            console.log('✅ Email verification successful! User data:', response.data.data)
            // Store the updated user in localStorage if needed
            localStorage.setItem('verifiedUser', JSON.stringify(response.data.data))
            
            // Check if email verification actually succeeded
            if (response.data.data.emailVerified && response.data.data.status === 'ACTIVE') {
                console.log('✅ Database update confirmed: emailVerified=true, status=ACTIVE')
            } else {
                console.warn('⚠️ Warning: Database may not have persisted changes. emailVerified=', response.data.data.emailVerified, 'status=', response.data.data.status)
            }
        }
        
        success.value = 'Account verified! Email address confirmed. Please log in with your credentials.'
        otpCode.value = ''
        // Pre-fill email for login convenience
        form.value = { username: '', email: pendingEmail.value, phoneNumber: '', password: '' }
        activeTab.value = 'login'
    } catch (otpError) {
        console.error('OTP verification error:', otpError)
        const errorMsg = otpError?.response?.data?.error || otpError?.response?.data?.message || 'Invalid or expired OTP. Please try again.'
        error.value = errorMsg
    } finally {
        submitting.value = false
    }
}

const resendOtp = async () => {
    resending.value = true
    error.value = ''
    success.value = ''
    try {
        await authAPI.resendOtp(pendingEmail.value, 'EMAIL_VERIFICATION')
        success.value = 'A new OTP has been sent to your email.'
    } catch (resendError) {
        error.value = resendError?.response?.data?.error || 'Failed to resend OTP. Please try again.'
    } finally {
        resending.value = false
    }
}

const startForgotPassword = () => {
    activeTab.value = 'forgot-password-request'
    error.value = ''
    success.value = ''
    forgotPasswordEmail.value = ''
    resetToken.value = ''
    newPassword.value = ''
}

const requestPasswordReset = async () => {
    submitting.value = true
    error.value = ''
    success.value = ''
    try {
        await authAPI.forgotPassword(forgotPasswordEmail.value)
        success.value = 'A password reset link has been sent to your email. Check your inbox for the reset token.'
        setTimeout(() => {
            activeTab.value = 'forgot-password-reset'
            success.value = ''
        }, 2000)
    } catch (resetError) {
        const errorMsg = resetError?.response?.data?.error || 
                        resetError?.response?.data?.message || 
                        'Failed to send password reset email. Please try again.'
        error.value = errorMsg
    } finally {
        submitting.value = false
    }
}

const submitPasswordReset = async () => {
    submitting.value = true
    error.value = ''
    success.value = ''
    
    // Validate passwords match
    if (newPassword.value !== confirmPassword.value) {
        error.value = 'Passwords do not match. Please try again.'
        submitting.value = false
        return
    }
    
    try {
        await authAPI.resetPassword(resetToken.value, newPassword.value)
        success.value = 'Password reset successfully! Redirecting to login...'
        setTimeout(() => {
            activeTab.value = 'login'
            form.value.email = forgotPasswordEmail.value
            form.value.password = ''
            resetToken.value = ''
            newPassword.value = ''
            confirmPassword.value = ''
            success.value = 'Password reset successful. Please login with your new password.'
        }, 2000)
    } catch (resetError) {
        const errorMsg = resetError?.response?.data?.error || 
                        resetError?.response?.data?.message || 
                        'Failed to reset password. Please check the reset token and try again.'
        error.value = errorMsg
    } finally {
        submitting.value = false
    }
}

const cancelForgotPassword = () => {
    activeTab.value = 'login'
    error.value = ''
    success.value = ''
    forgotPasswordEmail.value = ''
    resetToken.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
}

onMounted(() => {
    loadPortals()
    
    // Check if there's a reset token in the URL (from password reset email)
    const urlToken = route.query.token
    if (urlToken && typeof urlToken === 'string') {
        activeTab.value = 'forgot-password-reset'
        resetToken.value = urlToken
        success.value = 'Password reset email verified! Please enter your new password.'
    }
})
</script>

<style scoped>
.auth-shell {
    background:
        radial-gradient(circle at 15% 15%, #dbeafe 0%, transparent 32%),
        radial-gradient(circle at 80% 5%, #e0e7ff 0%, transparent 25%),
        #f3f4f6;
}

.left-brand-panel {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
}

.welcome-title {
    font-size: clamp(2.9rem, 4.6vw, 4.4rem);
    line-height: 1.03;
    font-weight: 900;
    color: #0f172a;
    margin: 0;
}

.logo-block {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    width: 100%;
    max-width: 24rem;
    margin-top: 0.9rem;
    margin-bottom: 1.3rem;
}

.scalegrad-logo {
    width: 100%;
    max-width: 24rem;
    height: clamp(2.75rem, 7vw, 4.25rem);
    object-fit: contain;
    object-position: left center;
    display: block;
}

.brand-copy {
    max-width: 42rem;
    color: #334155;
    font-size: 1.05rem;
    line-height: 1.45;
    margin-bottom: 1.8rem;
}

.brand-illustration {
    width: min(100%, 21rem);
    height: auto;
    margin-top: 0.5rem;
    display: block;
}

.password-field {
    position: relative;
}

.password-toggle {
    position: absolute;
    right: 0.95rem;
    top: 50%;
    transform: translateY(-50%);
    color: #64748b;
}

.access-tabs {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 0.4rem;
    padding: 0.35rem;
    border-radius: 0.9rem;
    border: 1px solid #cbd5e1;
    background: #f8fafc;
}

.access-tab {
    border-radius: 0.65rem;
    border: 1px solid transparent;
    padding: 0.68rem 0.85rem;
    font-weight: 700;
    transition: all 0.2s ease;
}

.access-tab-active {
    color: #ffffff;
    background: linear-gradient(145deg, #0f172a, #334155);
    border-color: #1e293b;
    box-shadow: 0 6px 14px rgba(15, 23, 42, 0.18);
}

.access-tab-idle {
    color: #1e293b;
    background: transparent;
}

.access-tab-idle:hover {
    background: #e2e8f0;
}

.portal-tabs {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 0.4rem;
    padding: 0.35rem;
    border-radius: 0.9rem;
    border: 1px solid #cbd5e1;
    background: #f8fafc;
}

.portal-tab {
    border-radius: 0.65rem;
    border: 1px solid transparent;
    padding: 0.68rem 0.85rem;
    font-weight: 700;
    transition: all 0.2s ease;
}

.portal-tab-active {
    color: #ffffff;
    background: linear-gradient(145deg, #2563eb, #4338ca);
    border-color: #4338ca;
    box-shadow: 0 6px 14px rgba(37, 99, 235, 0.2);
}

.portal-tab-idle {
    color: #1e293b;
    background: transparent;
}

.portal-tab-idle:hover {
    background: #eef2ff;
}

.portal-note {
    font-size: 0.9rem;
    color: #475569;
}

.portal-note-strong {
    color: #1e293b;
    font-weight: 700;
}

.auth-input {
    width: 100%;
    border: 1px solid #cbd5e1;
    border-radius: 0.7rem;
    padding: 0.72rem 0.9rem;
    color: #0f172a;
    background: #f8fafc;
}

.auth-input:focus {
    outline: none;
    border-color: #6366f1;
    box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
    background: #ffffff;
}

.auth-submit {
    width: 100%;
    border: none;
    border-radius: 0.7rem;
    padding: 0.85rem 1rem;
    color: #ffffff;
    font-weight: 700;
    background: linear-gradient(145deg, #1d4ed8, #4338ca);
}

.auth-submit:disabled {
    opacity: 0.65;
    cursor: not-allowed;
}
</style>
