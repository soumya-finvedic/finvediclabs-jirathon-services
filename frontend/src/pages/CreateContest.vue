<template>
  <div class="p-6 max-w-4xl mx-auto space-y-6">
    <div>
      <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100">Create Contest</h1>
      <p class="text-gray-600 dark:text-gray-400 mt-1">Fill in the details to create a new contest</p>
    </div>

    <div v-if="error" class="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-700">
      {{ error }}
    </div>
    <div v-if="success" class="rounded-lg border border-green-200 bg-green-50 px-4 py-3 text-green-700">
      {{ success }}
    </div>

    <form class="card p-6 space-y-6" @submit.prevent="submitContest">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div class="space-y-1 md:col-span-2">
          <label class="text-sm font-medium">Title</label>
          <input v-model.trim="form.title" class="input" required maxlength="120" />
        </div>

        <div class="space-y-1">
          <label class="text-sm font-medium">Slug</label>
          <input v-model.trim="form.slug" class="input" required maxlength="80" pattern="^[a-z0-9]+(?:-[a-z0-9]+)*$" />
          <p class="text-xs text-gray-500">lowercase and hyphen-separated</p>
        </div>

        <div class="space-y-1">
          <label class="text-sm font-medium">Contest Type</label>
          <select v-model="form.contestType" class="select" required>
            <option value="INDIVIDUAL">INDIVIDUAL</option>
            <option value="TEAM">TEAM</option>
          </select>
        </div>

        <div class="space-y-1 md:col-span-2">
          <label class="text-sm font-medium">Description</label>
          <textarea v-model.trim="form.description" class="textarea min-h-24" required maxlength="5000" placeholder="Describe the contest objectives and prizes..." />
        </div>

        <div class="space-y-1 md:col-span-2">
          <label class="text-sm font-medium">Rules (one per line)</label>
          <textarea v-model="form.rulesText" class="textarea min-h-24" required placeholder="Rule 1&#10;Rule 2&#10;Rule 3" />
        </div>

        <div class="space-y-1 md:col-span-2">
          <label class="text-sm font-medium">Supported Languages (comma-separated)</label>
          <input v-model.trim="form.supportedLanguagesText" class="input" required placeholder="java, python, cpp, javascript" />
          <p v-if="supportedLanguages.length" class="text-xs text-gray-500">
            Supported by backend: {{ supportedLanguages.join(', ') }}
          </p>
        </div>

        <div class="space-y-1">
          <label class="text-sm font-medium">Start Time</label>
          <input v-model="form.startTime" class="input" type="datetime-local" required />
        </div>

        <div class="space-y-1">
          <label class="text-sm font-medium">End Time</label>
          <input v-model="form.endTime" class="input" type="datetime-local" required />
        </div>

        <div class="space-y-1 md:col-span-2">
          <label class="text-sm font-medium">Registration Fee (Rs.)</label>
          <input v-model.number="form.registrationFee" class="input" type="number" min="0" step="0.01" required />
        </div>
      </div>

      <div class="flex justify-end gap-3">
        <RouterLink to="/admin/contests" class="btn-secondary">
          Cancel
        </RouterLink>
        <button class="btn-primary" :disabled="submitting">
          {{ submitting ? (isEditMode ? 'Updating...' : 'Creating...') : (isEditMode ? 'Update Contest' : 'Create Contest') }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { adminContestAPI } from '@/api'

const router = useRouter()
const supportedLanguages = ref([])
const submitting = ref(false)
const error = ref('')
const success = ref('')

const form = ref({
  title: '',
  slug: '',
  description: '',
  rulesText: '',
  contestType: 'INDIVIDUAL',
  supportedLanguagesText: 'java, python',
  startTime: '',
  endTime: '',
  registrationFee: 0
})

const parseDateTime = (value) => {
  if (!value) return null

  const trimmed = String(value).trim()
  if (!trimmed) return null

  const direct = new Date(trimmed)
  if (!Number.isNaN(direct.getTime())) {
    return direct
  }

  const match = trimmed.match(/^(\d{2})-(\d{2})-(\d{4})\s+(\d{2}):(\d{2})$/)
  if (match) {
    const [, dd, mm, yyyy, hh, min] = match
    const parsed = new Date(Number(yyyy), Number(mm) - 1, Number(dd), Number(hh), Number(min))
    if (!Number.isNaN(parsed.getTime())) {
      return parsed
    }
  }

  return null
}

const toIso = (value) => {
  const parsed = parseDateTime(value)
  return parsed ? parsed.toISOString() : null
}

const normalizeSlug = (value) => value
  .toLowerCase()
  .trim()
  .replace(/[^a-z0-9\s-]/g, '')
  .replace(/\s+/g, '-')
  .replace(/-+/g, '-')
  .replace(/^-|-$/g, '')

watch(() => form.value.title, (newTitle) => {
  form.value.slug = normalizeSlug(newTitle)
})

const parseRules = (text) => text
  .split('\n')
  .map((line) => line.trim())
  .filter(Boolean)

const parseLanguages = (text) => [...new Set(
  text
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)
)]

const extractErrorMessage = (err, fallback = 'Request failed') => {
  return err?.response?.data?.error || err?.response?.data?.message || err?.message || fallback
}

const loadSupportedLanguages = async () => {
  try {
    const response = await adminContestAPI.getSupportedLanguages()
    const data = response?.data?.data
    if (Array.isArray(data)) {
      supportedLanguages.value = data
    }
  } catch (_) {
    supportedLanguages.value = []
  }
}

const loadContestData = async (contestId) => {
  try {
    const response = await adminContestAPI.getById(contestId)
    const contest = response?.data?.data
    
    form.value = {
      title: contest.title,
      slug: contest.slug,
      description: contest.description,
      rulesText: (contest.rules || []).join('\n'),
      contestType: contest.contestType,
      supportedLanguagesText: (contest.supportedLanguages || []).join(', '),
      startTime: contest.startTime,
      endTime: contest.endTime,
      registrationFee: contest.registrationFee
    }
  } catch (err) {
    error.value = extractErrorMessage(err, 'Failed to load contest')
  }
}

const buildPayload = () => {
  const rules = parseRules(form.value.rulesText)
  const languages = parseLanguages(form.value.supportedLanguagesText)
  const startIso = toIso(form.value.startTime)
  const endIso = toIso(form.value.endTime)

  if (!startIso || !endIso) {
    throw new Error('Please provide valid Start Time and End Time')
  }

  if (new Date(endIso).getTime() <= new Date(startIso).getTime()) {
    throw new Error('End Time must be after Start Time')
  }

  const startMs = new Date(startIso).getTime()
  const registrationDeadlineIso = new Date(startMs - 60 * 1000).toISOString()

  return {
    title: form.value.title,
    slug: normalizeSlug(form.value.slug),
    description: form.value.description,
    rules,
    contestType: form.value.contestType,
    supportedLanguages: languages,
    registrationDeadline: registrationDeadlineIso,
    startTime: startIso,
    endTime: endIso,
    registrationFee: Number(form.value.registrationFee || 0)
  }
}

const submitContest = async () => {
  submitting.value = true
  error.value = ''
  success.value = ''

  try {
    const payload = buildPayload()
    if (isEditMode.value) {
      // Edit mode
      await adminContestAPI.update(route.params.id, payload)
      success.value = 'Contest updated successfully! Redirecting...'
    } else {
      // Create mode
      await adminContestAPI.create(payload)
      success.value = 'Contest created successfully! Redirecting...'
    }
    setTimeout(() => {
      router.push('/admin/contests')
    }, 1500)
  } catch (err) {
    error.value = extractErrorMessage(err, isEditMode.value ? 'Failed to update contest' : 'Failed to create contest')
    submitting.value = false
  }
}

onMounted(async () => {
  await loadSupportedLanguages()
  
  // Check if we're in edit mode
  if (route.params.id) {
    isEditMode.value = true
    await loadContestData(route.params.id)
  }
})
</script>
