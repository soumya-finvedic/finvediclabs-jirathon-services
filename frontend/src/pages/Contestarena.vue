<template>
    <div class="min-h-screen bg-gray-50 dark:bg-gray-950">

        <!-- Access denied / loading guard -->
        <div v-if="loading" class="flex items-center justify-center min-h-screen">
            <div class="text-center space-y-3">
                <div class="w-8 h-8 border-2 border-primary-500 border-t-transparent rounded-full animate-spin mx-auto"></div>
                <p class="text-sm text-gray-500 dark:text-gray-400">Loading contest arena...</p>
            </div>
        </div>

        <div v-else-if="accessError"
            class="flex items-center justify-center min-h-screen">
            <div class="card p-10 max-w-md text-center space-y-4">
                <svg class="w-12 h-12 text-red-400 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                        d="M12 15v2m0 0v2m0-2h2m-2 0H10m2-6V7a4 4 0 00-8 0v4H3a1 1 0 00-1 1v7a1 1 0 001 1h18a1 1 0 001-1v-7a1 1 0 00-1-1h-1V7a4 4 0 00-8 0z" />
                </svg>
                <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100">Access Denied</h2>
                <p class="text-sm text-gray-500 dark:text-gray-400">{{ accessError }}</p>
                <button class="btn-primary w-full" @click="goBack">Back to Contest</button>
            </div>
        </div>

        <!-- Arena layout -->
        <div v-else class="flex flex-col h-screen overflow-hidden">

            <!-- Top bar -->
            <header class="bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-800 px-4 py-3 flex items-center justify-between shrink-0 z-10">
                <div class="flex items-center gap-3">
                    <button
                        class="p-1.5 rounded hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
                        @click="goBack"
                        title="Back to contest">
                        <svg class="w-5 h-5 text-gray-600 dark:text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
                        </svg>
                    </button>
                    <div>
                        <h1 class="text-base font-semibold text-gray-900 dark:text-gray-100 leading-tight">
                            {{ contest?.title }}
                        </h1>
                        <p class="text-xs text-gray-500 dark:text-gray-400">Arena</p>
                    </div>
                </div>

                <div class="flex items-center gap-4">
                    <!-- Live indicator -->
                    <div class="hidden sm:flex items-center gap-1.5">
                        <span class="relative flex h-2 w-2">
                            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
                            <span class="relative inline-flex rounded-full h-2 w-2 bg-green-500"></span>
                        </span>
                        <span class="text-xs font-medium text-green-600 dark:text-green-400">LIVE</span>
                    </div>

                    <!-- Countdown to end -->
                    <div class="text-center">
                        <p class="text-xs text-gray-400 dark:text-gray-500 leading-none mb-0.5">Time left</p>
                        <p :class="['text-lg font-bold tabular-nums leading-none', timeWarning ? 'text-red-500 dark:text-red-400' : 'text-gray-900 dark:text-gray-100']">
                            {{ countdown }}
                        </p>
                    </div>

                    <!-- Submit button -->
                    <button
                        class="btn-primary px-5 py-2 text-sm font-semibold"
                        :disabled="submitting || submitted"
                        @click="submitSolution">
                        {{ submitted ? 'Submitted' : submitting ? 'Submitting...' : 'Submit' }}
                    </button>
                </div>
            </header>

            <!-- Main content: problem list + editor -->
            <div class="flex flex-1 overflow-hidden">

                <!-- Left: Problem list sidebar -->
                <aside class="w-64 shrink-0 border-r border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-900 flex flex-col overflow-hidden">
                    <div class="px-4 py-3 border-b border-gray-200 dark:border-gray-800">
                        <p class="text-xs font-semibold uppercase tracking-wide text-gray-500 dark:text-gray-400">Problems</p>
                    </div>
                    <div class="flex-1 overflow-y-auto">
                        <div v-if="problemsLoading" class="p-4 space-y-2">
                            <div v-for="i in 4" :key="i" class="h-10 rounded skeleton"></div>
                        </div>
                        <div v-else-if="problems.length === 0" class="p-4">
                            <p class="text-sm text-gray-400 dark:text-gray-500">No problems found.</p>
                        </div>
                        <button
                            v-for="(problem, idx) in problems"
                            :key="problem.id"
                            :class="[
                                'w-full text-left px-4 py-3 border-b border-gray-100 dark:border-gray-800 transition-colors',
                                selectedProblem?.id === problem.id
                                    ? 'bg-primary-50 dark:bg-primary-900/20 border-l-2 border-l-primary-500'
                                    : 'hover:bg-gray-50 dark:hover:bg-gray-800/60'
                            ]"
                            @click="selectProblem(problem)">
                            <div class="flex items-center justify-between">
                                <span class="text-sm font-medium text-gray-800 dark:text-gray-200">
                                    {{ idx + 1 }}. {{ problem.title }}
                                </span>
                                <span :class="['text-xs px-1.5 py-0.5 rounded font-medium', difficultyClass(problem.difficulty)]">
                                    {{ problem.difficulty }}
                                </span>
                            </div>
                            <div v-if="submissionsMap[problem.id]" class="mt-1 flex items-center gap-1">
                                <span :class="['w-1.5 h-1.5 rounded-full', submissionsMap[problem.id] === 'ACCEPTED' ? 'bg-green-500' : 'bg-red-400']"></span>
                                <span class="text-xs text-gray-400 dark:text-gray-500">{{ submissionsMap[problem.id] }}</span>
                            </div>
                        </button>
                    </div>
                </aside>

                <!-- Center: Problem statement -->
                <main class="flex-1 overflow-y-auto bg-white dark:bg-gray-900 border-r border-gray-200 dark:border-gray-800">
                    <div v-if="!selectedProblem" class="flex items-center justify-center h-full">
                        <div class="text-center space-y-2">
                            <svg class="w-10 h-10 text-gray-300 dark:text-gray-700 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                            </svg>
                            <p class="text-sm text-gray-400 dark:text-gray-500">Select a problem to begin</p>
                        </div>
                    </div>

                    <div v-else class="p-6 max-w-3xl">
                        <div class="flex items-start justify-between mb-4 gap-4">
                            <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100">{{ selectedProblem.title }}</h2>
                            <span :class="['badge text-xs shrink-0', difficultyClass(selectedProblem.difficulty)]">
                                {{ selectedProblem.difficulty }}
                            </span>
                        </div>

                        <div class="prose prose-sm dark:prose-invert max-w-none mb-6"
                            v-html="selectedProblem.description || selectedProblem.statement || ''" />

                        <!-- Sample IO -->
                        <div v-if="selectedProblem.sampleInput || selectedProblem.sampleOutput" class="space-y-3 mb-6">
                            <div v-if="selectedProblem.sampleInput" class="rounded-lg bg-gray-50 dark:bg-gray-800 p-4">
                                <p class="text-xs font-semibold uppercase tracking-wide text-gray-500 dark:text-gray-400 mb-2">Sample Input</p>
                                <pre class="text-sm font-mono text-gray-800 dark:text-gray-200 whitespace-pre-wrap">{{ selectedProblem.sampleInput }}</pre>
                            </div>
                            <div v-if="selectedProblem.sampleOutput" class="rounded-lg bg-gray-50 dark:bg-gray-800 p-4">
                                <p class="text-xs font-semibold uppercase tracking-wide text-gray-500 dark:text-gray-400 mb-2">Sample Output</p>
                                <pre class="text-sm font-mono text-gray-800 dark:text-gray-200 whitespace-pre-wrap">{{ selectedProblem.sampleOutput }}</pre>
                            </div>
                        </div>

                        <!-- Constraints -->
                        <div v-if="selectedProblem.constraints" class="rounded-lg bg-amber-50 dark:bg-amber-900/20 border border-amber-200 dark:border-amber-800 p-4">
                            <p class="text-xs font-semibold uppercase tracking-wide text-amber-700 dark:text-amber-400 mb-1">Constraints</p>
                            <p class="text-sm text-amber-800 dark:text-amber-300 font-mono">{{ selectedProblem.constraints }}</p>
                        </div>
                    </div>
                </main>

                <!-- Right: Code editor panel -->
                <section class="w-96 shrink-0 flex flex-col bg-gray-950 dark:bg-gray-950 overflow-hidden">
                    <!-- Editor toolbar -->
                    <div class="flex items-center justify-between px-3 py-2 border-b border-gray-800 shrink-0">
                        <select
                            v-model="selectedLanguage"
                            class="text-xs bg-gray-800 text-gray-200 border border-gray-700 rounded px-2 py-1 focus:outline-none">
                            <option v-for="lang in supportedLanguages" :key="lang" :value="lang">{{ lang }}</option>
                        </select>
                        <button
                            class="text-xs text-gray-400 hover:text-gray-200 transition-colors px-2 py-1 rounded hover:bg-gray-800"
                            @click="resetCode">
                            Reset
                        </button>
                    </div>

                    <!-- Textarea editor (replace with Monaco if available) -->
                    <textarea
                        v-model="code"
                        class="flex-1 bg-gray-950 text-gray-100 text-sm font-mono p-4 resize-none focus:outline-none leading-relaxed"
                        placeholder="// Write your solution here..."
                        spellcheck="false"
                    />

                    <!-- Run / Submit row -->
                    <div class="px-3 py-2 border-t border-gray-800 flex items-center gap-2 shrink-0">
                        <button
                            class="flex-1 text-xs font-medium py-1.5 rounded border border-gray-700 text-gray-300 hover:bg-gray-800 transition-colors"
                            :disabled="running"
                            @click="runCode">
                            {{ running ? 'Running...' : 'Run' }}
                        </button>
                        <button
                            class="flex-1 text-xs font-medium py-1.5 rounded bg-primary-600 hover:bg-primary-700 text-white transition-colors"
                            :disabled="submitting || !selectedProblem"
                            @click="submitSolution">
                            {{ submitting ? 'Submitting...' : 'Submit' }}
                        </button>
                    </div>

                    <!-- Output panel -->
                    <div v-if="runOutput !== null"
                        class="border-t border-gray-800 bg-gray-900 p-3 max-h-40 overflow-y-auto shrink-0">
                        <p class="text-xs font-semibold uppercase tracking-wide text-gray-500 mb-1">Output</p>
                        <pre class="text-xs font-mono text-gray-300 whitespace-pre-wrap">{{ runOutput }}</pre>
                    </div>

                    <!-- Submission result -->
                    <div v-if="submissionResult"
                        :class="[
                            'border-t px-3 py-2 shrink-0',
                            submissionResult.verdict === 'ACCEPTED'
                                ? 'border-green-800 bg-green-900/30'
                                : 'border-red-800 bg-red-900/30'
                        ]">
                        <p :class="['text-xs font-semibold', submissionResult.verdict === 'ACCEPTED' ? 'text-green-400' : 'text-red-400']">
                            {{ submissionResult.verdict }}
                        </p>
                        <p v-if="submissionResult.message" class="text-xs text-gray-400 mt-0.5">{{ submissionResult.message }}</p>
                    </div>
                </section>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { contestAPI } from '@/api'
import { useUserStore } from '@/stores'
import { useContest } from '@/composables/useContests'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { contest, fetchContest } = useContest(route.params.id)

// ── State ──────────────────────────────────────────────────────────────────
const loading = ref(true)
const accessError = ref('')
const problems = ref([])
const problemsLoading = ref(false)
const selectedProblem = ref(null)
const selectedLanguage = ref('')
const code = ref('')
const running = ref(false)
const runOutput = ref(null)
const submitting = ref(false)
const submitted = ref(false)
const submissionResult = ref(null)
const submissionsMap = ref({})   // problemId → verdict string
const registration = ref(null)

// ── Countdown ──────────────────────────────────────────────────────────────
const countdown = ref('--:--:--')
let countdownTimer = null

const pad = (n) => String(n).padStart(2, '0')

const tickCountdown = () => {
    if (!contest.value) return
    const now = Date.now()
    const end = new Date(contest.value.endTime).getTime()
    const diff = end - now
    if (diff <= 0) {
        countdown.value = '00:00:00'
        clearInterval(countdownTimer)
        // Redirect out when time is up
        setTimeout(() => router.push(`/contest/${route.params.id}`), 1500)
        return
    }
    const h = Math.floor(diff / 3_600_000)
    const m = Math.floor((diff % 3_600_000) / 60_000)
    const s = Math.floor((diff % 60_000) / 1_000)
    countdown.value = `${pad(h)}:${pad(m)}:${pad(s)}`
}

// Warn when < 5 minutes remain
const timeWarning = computed(() => {
    const parts = countdown.value.split(':').map(Number)
    if (parts.length !== 3) return false
    const totalSeconds = parts[0] * 3600 + parts[1] * 60 + parts[2]
    return totalSeconds < 300
})

onUnmounted(() => clearInterval(countdownTimer))

// ── Supported languages from contest ──────────────────────────────────────
const supportedLanguages = computed(() => contest.value?.supportedLanguages || [])

// ── Access guard ───────────────────────────────────────────────────────────
const verifyAccess = async () => {
    try {
        const regRes = await contestAPI.getMyRegistration(route.params.id)
        const reg = regRes?.data?.data
        registration.value = reg

        const isConfirmed = reg?.status === 'CONFIRMED'
        const isPaid = String(reg?.paymentStatus || '').toUpperCase() === 'SUCCESS'

        if (!isConfirmed || !isPaid) {
            accessError.value = 'Your payment is not confirmed. Please complete payment to access the arena.'
            return false
        }

        const now = Date.now()
        const start = new Date(contest.value?.startTime).getTime()
        const end = new Date(contest.value?.endTime).getTime()

        if (now < start) {
            accessError.value = 'The contest has not started yet.'
            return false
        }
        if (now > end) {
            accessError.value = 'The contest window has ended.'
            return false
        }

        return true
    } catch {
        accessError.value = 'Unable to verify your registration. Please go back and try again.'
        return false
    }
}

// ── Load problems ──────────────────────────────────────────────────────────
const loadProblems = async () => {
    problemsLoading.value = true
    try {
        const res = await contestAPI.getProblems(route.params.id)
        problems.value = Array.isArray(res?.data?.data) ? res.data.data : []
        if (problems.value.length > 0) selectProblem(problems.value[0])
    } catch {
        problems.value = []
    } finally {
        problemsLoading.value = false
    }
}

// ── Problem selection ──────────────────────────────────────────────────────
const selectProblem = (problem) => {
    selectedProblem.value = problem
    runOutput.value = null
    submissionResult.value = null
    code.value = ''
}

// ── Difficulty badge class ─────────────────────────────────────────────────
const difficultyClass = (diff) => {
    const d = String(diff || '').toUpperCase()
    if (d === 'EASY') return 'badge-success text-green-700 dark:text-green-300 bg-green-100 dark:bg-green-900/30'
    if (d === 'MEDIUM') return 'badge-warning text-amber-700 dark:text-amber-300 bg-amber-100 dark:bg-amber-900/30'
    if (d === 'HARD') return 'badge-danger text-red-700 dark:text-red-300 bg-red-100 dark:bg-red-900/30'
    return 'text-gray-500'
}

// ── Code actions ───────────────────────────────────────────────────────────
const resetCode = () => {
    code.value = ''
    runOutput.value = null
    submissionResult.value = null
}

const runCode = async () => {
    if (!selectedProblem.value || !code.value.trim()) return
    running.value = true
    runOutput.value = null
    try {
        const res = await contestAPI.runCode({
            problemId: selectedProblem.value.id,
            contestId: route.params.id,
            language: selectedLanguage.value,
            code: code.value,
        })
        runOutput.value = res?.data?.data?.output || res?.data?.data?.result || 'No output'
    } catch (err) {
        runOutput.value = err.response?.data?.error || err.response?.data?.message || 'Run failed.'
    } finally {
        running.value = false
    }
}

const submitSolution = async () => {
    if (!selectedProblem.value || !code.value.trim()) return
    submitting.value = true
    submissionResult.value = null
    try {
        const res = await contestAPI.submitSolution({
            problemId: selectedProblem.value.id,
            contestId: route.params.id,
            registrationId: registration.value?.id,
            language: selectedLanguage.value,
            code: code.value,
        })
        const result = res?.data?.data || {}
        submissionResult.value = {
            verdict: result.verdict || result.status || 'SUBMITTED',
            message: result.message || '',
        }
        // Track per-problem verdict
        submissionsMap.value[selectedProblem.value.id] = submissionResult.value.verdict
        if (submissionResult.value.verdict === 'ACCEPTED') submitted.value = true
    } catch (err) {
        submissionResult.value = {
            verdict: 'ERROR',
            message: err.response?.data?.error || err.response?.data?.message || 'Submission failed.',
        }
    } finally {
        submitting.value = false
    }
}

// ── Navigation ─────────────────────────────────────────────────────────────
const goBack = () => router.push({ name: 'ContestDetail', params: { id: route.params.id } })

// ── Mount ──────────────────────────────────────────────────────────────────
onMounted(async () => {
    await fetchContest()

    const allowed = await verifyAccess()
    if (!allowed) {
        loading.value = false
        return
    }

    // Set default language
    if (contest.value?.supportedLanguages?.length) {
        selectedLanguage.value = contest.value.supportedLanguages[0]
    }

    await loadProblems()

    // Start countdown timer
    tickCountdown()
    countdownTimer = setInterval(tickCountdown, 1000)

    loading.value = false
})
</script>