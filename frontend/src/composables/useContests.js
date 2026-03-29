import { ref, computed } from 'vue'
import { contestAPI } from '@/api'

export const useContests = () => {
  const contests = ref([])
  const loading = ref(false)
  const error = ref(null)

  const fetchContests = async () => {
    console.log('[useContests] fetchContests called')
    loading.value = true
    try {
      const response = await contestAPI.list()
      contests.value = response.data.data?.content || []
      console.log('[useContests] fetchContests success:', contests.value.length, 'contests')
      error.value = null
    } catch (err) {
      console.error('[useContests] fetchContests error:', err.message, err)
      contests.value = []
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  const activeContests = computed(() => 
    contests.value.filter(c => c.status === 'PUBLISHED')
  )

  const upcomingContests = computed(() => 
    contests.value.filter(c => c.status === 'DRAFT')
  )

  const pastContests = computed(() => 
    contests.value.filter(c => c.status === 'ARCHIVED')
  )

  return {
    contests,
    loading,
    error,
    activeContests,
    upcomingContests,
    pastContests,
    fetchContests
  }
}

export const useContest = (id) => {
  const contest = ref(null)
  const questions = ref([])
  const loading = ref(false)
  const error = ref(null)

  const fetchContest = async () => {
    console.log('[useContest] fetchContest called, id:', id)
    loading.value = true
    try {
      const response = await contestAPI.getById(id)
      contest.value = response.data.data
      console.log('[useContest] fetchContest success:', contest.value)
      error.value = null
    } catch (err) {
      console.error('[useContest] fetchContest error:', err.message, err)
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  const fetchQuestions = async () => {
    console.log('[useContest] fetchQuestions called, id:', id)
    try {
      const response = await contestAPI.getQuestions(id)
      questions.value = response.data.data || []
      console.log('[useContest] fetchQuestions success:', questions.value.length, 'questions')
    } catch (err) {
      console.error('[useContest] fetchQuestions error:', err.message, err)
      questions.value = []
    }
  }

  const registerContest = async () => {
    console.log('[useContest] registerContest called, id:', id)
    try {
      await contestAPI.register(id)
      console.log('[useContest] registerContest success, id:', id)
      return true
    } catch (err) {
      console.error('[useContest] registerContest error:', err.message, err)
      error.value = err.message
      return false
    }
  }

  return {
    contest,
    questions,
    loading,
    error,
    fetchContest,
    fetchQuestions,
    registerContest
  }
}

export const useExecution = () => {
  const output = ref('')
  const loading = ref(false)
  const error = ref(null)
  const executionTime = ref(0)

  const executeCode = async (code, language) => {
    console.log('[useExecution] executeCode called, language:', language)
    loading.value = true
    error.value = null
    const startTime = performance.now()

    try {
      const response = await executionAPI.run(code, language)
      output.value = response.data.data.output
      executionTime.value = performance.now() - startTime
      console.log('[useExecution] executeCode success, time:', executionTime.value.toFixed(2), 'ms')
      return response.data.data
    } catch (err) {
      console.error('[useExecution] executeCode error:', err.message, err)
      error.value = err.response?.data?.message || 'Execution failed'
      output.value = ''
    } finally {
      loading.value = false
    }
  }

  return {
    output,
    loading,
    error,
    executionTime,
    executeCode
  }
}