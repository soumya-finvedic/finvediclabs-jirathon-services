/**
 * Composable for handling async API operations
 * Provides loading state, error handling, and data management
 * 
 * Usage in component:
 * const { data, loading, error, execute, reset } = useAsync(apiFunction)
 * 
 * Then call: 
 * await execute()
 */

import { ref, readonly, computed } from 'vue'
import { handleApiError, handleNetworkError } from '@/api/errorHandler'

export const useAsync = (asyncFunction, immediate = false) => {
  const data = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const isError = computed(() => !!error.value)

  const execute = async (...args) => {
    loading.value = true
    error.value = null
    data.value = null

    try {
      const result = await asyncFunction(...args)
      data.value = result
      return {
        ok: true,
        data: result
      }
    } catch (err) {
      // Determine error type and format message
      let errorMessage = 'An error occurred'

      if (err.response) {
        // API error
        const apiError = handleApiError(err)
        errorMessage = apiError.message
      } else {
        // Network error
        errorMessage = handleNetworkError(err)
      }

      error.value = errorMessage
      console.error('Async operation error:', { error: err, message: errorMessage })

      return {
        ok: false,
        message: errorMessage,
        error: err
      }
    } finally {
      loading.value = false
    }
  }

  const reset = () => {
    data.value = null
    error.value = null
    loading.value = false
  }

  return {
    data: readonly(data),
    loading: readonly(loading),
    error: readonly(error),
    isError,
    execute,
    reset
  }
}

/**
 * Composable for handling paginated API data
 * 
 * Usage:
 * const { items, page, pageSize, totalPages, loading, fetch, nextPage, prevPage } = 
 *   usePaginated(apiListFunction)
 * 
 * await fetch() // Load first page
 */
export const usePaginated = (fetchFunction, pageSize = 20) => {
  const items = ref([])
  const page = ref(0)
  const totalPages = ref(0)
  const totalElements = ref(0)
  const loading = ref(false)
  const error = ref(null)

  const fetch = async (pageNumber = 0, size = pageSize) => {
    loading.value = true
    error.value = null

    try {
      const result = await fetchFunction({
        page: pageNumber,
        size
      })

      if (result.ok) {
        // Handle different response formats
        const data = result.data
        items.value = data.content || data.items || data || []
        totalPages.value = data.totalPages || Math.ceil((data.total || 0) / size)
        totalElements.value = data.totalElements || data.total || items.value.length
        page.value = pageNumber

        return { ok: true }
      } else {
        error.value = result.message
        return { ok: false, message: result.message }
      }
    } catch (err) {
      const apiError = err.response ? handleApiError(err) : handleNetworkError(err)
      error.value = apiError.message || err.message
      return { ok: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  const nextPage = () => {
    if (page.value < totalPages.value - 1) {
      return fetch(page.value + 1)
    }
  }

  const prevPage = () => {
    if (page.value > 0) {
      return fetch(page.value - 1)
    }
  }

  const goToPage = (pageNumber) => {
    if (pageNumber >= 0 && pageNumber < totalPages.value) {
      return fetch(pageNumber)
    }
  }

  return {
    items,
    page,
    pageSize,
    totalPages,
    totalElements,
    loading,
    error,
    fetch,
    nextPage,
    prevPage,
    goToPage
  }
}

/**
 * Composable for form submission with validation
 * 
 * Usage:
 * const { formData, errors, loading, submit } = useForm(
 *   { email: '', password: '' },
 *   (data) => authAPI.login(data.email, data.password)
 * )
 * 
 * await submit()
 */
export const useForm = (initialData, submitFunction, onSuccess = null) => {
  const formData = ref({ ...initialData })
  const errors = ref({})
  const loading = ref(false)
  const submitted = ref(false)
  const fieldErrors = ref({})

  const reset = () => {
    formData.value = { ...initialData }
    errors.value = {}
    fieldErrors.value = {}
    submitted.value = false
  }

  const clearErrors = () => {
    errors.value = {}
    fieldErrors.value = {}
  }

  const setFieldError = (fieldName, message) => {
    fieldErrors.value[fieldName] = message
  }

  const submit = async () => {
    loading.value = true
    clearErrors()
    submitted.value = true

    try {
      const result = await submitFunction(formData.value)

      if (result.ok) {
        if (onSuccess) {
          onSuccess(result.data)
        }
        return { ok: true, data: result.data }
      } else {
        // Check if response contains field-level errors
        if (result.details?.errors) {
          fieldErrors.value = result.details.errors
        }
        errors.value = {
          global: result.message
        }
        return { ok: false, message: result.message }
      }
    } catch (err) {
      const apiError = err.response ? handleApiError(err) : handleNetworkError(err)

      // Extract field errors if available
      if (apiError.details?.errors) {
        fieldErrors.value = apiError.details.errors
      }

      errors.value = {
        global: apiError.message
      }

      return { ok: false, message: apiError.message, error: err }
    } finally {
      loading.value = false
    }
  }

  return {
    formData,
    errors,
    fieldErrors,
    loading,
    submitted,
    reset,
    clearErrors,
    setFieldError,
    submit
  }
}

export default {
  useAsync,
  usePaginated,
  useForm
}
