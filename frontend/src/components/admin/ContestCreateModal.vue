<template>
  <div class="fixed inset-0 bg-black/85 flex items-center justify-center z-50 p-4">
    <div class="bg-slate-850 bg-slate-900 border border-slate-700 rounded-2xl w-full max-w-md max-h-[92vh] overflow-y-auto shadow-2xl">
      <div class="p-7">
        <!-- Header -->
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-xl font-bold text-white">Create New Contest</h2>
          <button
            class="w-8 h-8 flex items-center justify-center rounded-lg text-slate-400 hover:text-white hover:bg-slate-700 transition text-lg font-medium"
            @click="$emit('close')"
          >×</button>
        </div>

        <!-- Step Progress Bar -->
        <div class="flex gap-1.5 mb-8">
          <div
            v-for="i in 3"
            :key="i"
            :class="[
              'h-1 flex-1 rounded-full transition-all duration-300',
              i <= step ? 'bg-cyan-500' : 'bg-slate-700'
            ]"
          ></div>
        </div>

        <!-- Step 1: Basic Info -->
        <div v-if="step === 1" class="space-y-5">
          <div>
            <label class="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">CONTEST TITLE *</label>
            <input
              v-model="form.title"
              class="w-full px-4 py-3 bg-slate-800 border border-slate-600 rounded-lg text-white placeholder-slate-500 text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500/30 transition"
              placeholder="e.g., Multilingual Sentiment Olympiad"
              maxlength="120"
            />
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">CATEGORY</label>
            <div class="relative">
              <select
                v-model="form.category"
                class="w-full px-4 py-3 bg-slate-800 border border-slate-600 rounded-lg text-white text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500/30 transition appearance-none pr-10"
              >
                <option value="">Select a category</option>
                <option value="rl">Reinforcement Learning</option>
                <option value="vision">Computer Vision</option>
                <option value="nlp">NLP</option>
                <option value="audio">Audio &amp; Speech</option>
                <option value="finance">Finance</option>
                <option value="tabular">Tabular Data</option>
                <option value="timeseries">Time Series</option>
              </select>
              <span class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none">▾</span>
            </div>
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">DESCRIPTION</label>
            <textarea
              v-model="form.description"
              class="w-full px-4 py-3 bg-slate-800 border border-slate-600 rounded-lg text-white placeholder-slate-500 text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500/30 transition resize-none"
              rows="4"
              placeholder="Brief overview of the challenge..."
              maxlength="500"
            ></textarea>
          </div>
        </div>

        <!-- Step 2: Settings -->
        <div v-if="step === 2" class="space-y-5">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">START DATE *</label>
              <div class="relative">
                <input
                  v-model="form.startDate"
                  type="date"
                  class="w-full px-4 py-3 bg-slate-800 border border-slate-600 rounded-lg text-white text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500/30 transition"
                  :placeholder="'dd/mm/yyyy'"
                />
              </div>
            </div>
            <div>
              <label class="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">END DATE *</label>
              <div class="relative">
                <input
                  v-model="form.endDate"
                  type="date"
                  class="w-full px-4 py-3 bg-slate-800 border border-slate-600 rounded-lg text-white text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500/30 transition"
                />
              </div>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">PRIZE POOL ($)</label>
              <input
                v-model.number="form.prizePool"
                type="number"
                min="0"
                step="100"
                class="w-full px-4 py-3 bg-slate-800 border border-slate-600 rounded-lg text-white text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500/30 transition"
                placeholder="2500"
              />
            </div>
            <div>
              <label class="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">MAX PARTICIPANTS</label>
              <input
                v-model.number="form.maxParticipants"
                type="number"
                min="1"
                step="50"
                class="w-full px-4 py-3 bg-slate-800 border border-slate-600 rounded-lg text-white text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500/30 transition"
                placeholder="500"
              />
            </div>
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">EVALUATION METRIC</label>
            <div class="relative">
              <select
                v-model="form.evaluationMetric"
                class="w-full px-4 py-3 bg-slate-800 border border-slate-600 rounded-lg text-white text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500/30 transition appearance-none pr-10"
              >
                <option value="Accuracy">Accuracy</option>
                <option value="Precision">Precision</option>
                <option value="Recall">Recall</option>
                <option value="F1-Score">F1-Score</option>
                <option value="RMSE">RMSE</option>
                <option value="AUC">AUC</option>
              </select>
              <span class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none">▾</span>
            </div>
          </div>
        </div>

        <!-- Step 3: Rules & Languages -->
        <div v-if="step === 3" class="space-y-5">
          <div>
            <label class="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">RULES (one per line)</label>
            <textarea
              v-model="form.rules"
              class="w-full px-4 py-3 bg-slate-800 border border-slate-600 rounded-lg text-white placeholder-slate-500 text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500/30 transition resize-none font-mono"
              rows="4"
              placeholder="Rule 1&#10;Rule 2&#10;Rule 3"
            ></textarea>
          </div>

          <div>
            <label class="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">SUPPORTED LANGUAGES (comma-separated)</label>
            <input
              v-model="form.languages"
              class="w-full px-4 py-3 bg-slate-800 border border-slate-600 rounded-lg text-white placeholder-slate-500 text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500/30 transition"
              placeholder="java, python, cpp, javascript"
            />
          </div>
        </div>

        <!-- Error Message -->
        <div v-if="error" class="mt-5 p-3 bg-red-900/25 border border-red-700/50 rounded-lg text-red-300 text-sm">
          {{ error }}
        </div>

        <!-- Step label + Navigation Buttons -->
        <div class="mt-7 pt-5 border-t border-slate-700">
          <p class="text-xs text-slate-500 mb-4">
            Step {{ step }} of 3 —
            <span class="text-slate-400">{{ ['Basic Info', 'Settings', 'Final Details'][step - 1] }}</span>
          </p>
          <div class="flex gap-3">
            <button
              v-if="step > 1"
              class="flex-1 px-4 py-3 bg-slate-700 hover:bg-slate-600 text-white rounded-lg font-medium transition text-sm"
              @click="step--"
            >
              ← Back
            </button>
            <button
              v-if="step < 3"
              class="flex-1 px-4 py-3 bg-cyan-500 hover:bg-cyan-400 text-slate-900 rounded-lg font-semibold transition text-sm"
              @click="nextStep"
            >
              Continue →
            </button>
            <button
              v-if="step === 3"
              :disabled="submitting"
              class="flex-1 px-4 py-3 bg-cyan-500 hover:bg-cyan-400 disabled:bg-slate-600 disabled:text-slate-400 text-slate-900 rounded-lg font-semibold transition text-sm"
              @click="submitForm"
            >
              {{ submitting ? 'Creating...' : 'Create Contest' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { adminContestAPI } from '@/api'

const emit = defineEmits(['close', 'success'])

const step = ref(1)
const submitting = ref(false)
const error = ref('')

const form = ref({
  title: '',
  category: '',
  description: '',
  startDate: '',
  endDate: '',
  prizePool: 2500,
  maxParticipants: 500,
  evaluationMetric: 'Accuracy',
  rules: '',
  languages: 'java, python'
})

const nextStep = () => {
  error.value = ''
  if (step.value === 1 && !form.value.title) {
    error.value = 'Please enter a contest title'
    return
  }
  if (step.value === 2 && !form.value.startDate) {
    error.value = 'Please select start date'
    return
  }
  step.value++
}

const submitForm = async () => {
  error.value = ''

  if (!form.value.title) {
    error.value = 'Contest title is required'
    return
  }

  submitting.value = true
  try {
    const categoryMap = {
      rl: 'RL',
      vision: 'Vision',
      nlp: 'NLP',
      audio: 'Audio',
      finance: 'Finance',
      tabular: 'Tabular',
      timeseries: 'TimeSeries'
    }
    const payload = {
      title: form.value.title,
      category: categoryMap[form.value.category] || form.value.category || 'General',
      description: form.value.description,
      startTime: form.value.startDate ? new Date(form.value.startDate).toISOString() : new Date().toISOString(),
      endTime: form.value.endDate ? new Date(form.value.endDate).toISOString() : new Date().toISOString(),
      prizePool: form.value.prizePool || 0,
      maxParticipants: form.value.maxParticipants || 100,
      evaluationMetric: form.value.evaluationMetric,
      rules: form.value.rules.split('\n').filter((r) => r.trim()),
      supportedLanguages: form.value.languages.split(',').map((l) => l.trim()),
      status: 'DRAFT',
      contestType: 'INDIVIDUAL'
    }

    await adminContestAPI.create(payload)
    emit('success')
    emit('close')
  } catch (err) {
    error.value = err?.response?.data?.error || err?.message || 'Failed to create contest'
  } finally {
    submitting.value = false
  }
}
</script>
