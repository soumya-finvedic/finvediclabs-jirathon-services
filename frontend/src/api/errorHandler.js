/**
 * API Error Handler Utility
 * Centralized error handling for all API calls
 */

export class ApiError extends Error {
  constructor(status, message, details = null) {
    super(message)
    this.status = status
    this.details = details
    this.name = 'ApiError'
  }
}

/**
 * Handle API errors consistently across the application
 * @param {Error} error - The error from axios
 * @returns {Object} - { message, status, details }
 */
export const handleApiError = (error) => {
  const response = error.response
  const status = response?.status
  const data = response?.data

  // Extract error message
  let message = 'An error occurred. Please try again.'

  if (data?.error) {
    message = data.error
  } else if (data?.message) {
    message = data.message
  } else if (error.message) {
    message = error.message
  }

  // Handle specific HTTP status codes
  switch (status) {
    case 400:
      message = data?.message || 'Invalid request. Please check your input.'
      break

    case 401:
      message = 'Your session has expired. Please login again.'
      // Clear auth tokens
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      window.location.href = '/login'
      break

    case 403:
      message = 'You do not have permission to perform this action.'
      break

    case 404:
      message = 'The requested resource was not found.'
      break

    case 409:
      message = data?.message || 'Conflict: This resource already exists.'
      break

    case 422:
      message = 'Validation failed. Please check your input.'
      break

    case 500:
      message = 'Server error. Our team has been notified. Please try again later.'
      break

    case 503:
      message = 'Service temporarily unavailable. Please try again later.'
      break

    default:
      if (!response) {
        message = 'Network error. Please check your connection.'
      }
  }

  return {
    message,
    status,
    details: data,
    isAuthError: status === 401,
    isValidationError: status === 400 || status === 422,
    isNotFoundError: status === 404,
    isServerError: status >= 500
  }
}

/**
 * Handle network/connection errors
 * @param {Error} error - The error object
 * @returns {string} - User-friendly error message
 */
export const handleNetworkError = (error) => {
  if (error.code === 'ECONNABORTED') {
    return 'Request timeout. Please check your connection and try again.'
  }

  if (error.code === 'ERR_NETWORK') {
    return 'Network error. Please check your internet connection.'
  }

  if (!error.response && error.message === 'Network Error') {
    return 'Unable to connect to server. Please check your connection.'
  }

  return 'An unexpected error occurred. Please try again.'
}

/**
 * Extract validation errors from failed API response
 * Useful for form field validation
 * @param {Object} errorData - The error response data
 * @returns {Object} - Field-level errors { fieldName: 'error message' }
 */
export const extractValidationErrors = (errorData) => {
  const errors = {}

  // Handle different error response formats
  if (Array.isArray(errorData?.errors)) {
    errorData.errors.forEach(err => {
      if (err.field) {
        errors[err.field] = err.message
      }
    })
  } else if (typeof errorData?.errors === 'object') {
    Object.entries(errorData.errors).forEach(([field, message]) => {
      errors[field] = message
    })
  }

  return errors
}

/**
 * Format error message for user display
 * Can include retry advice based on error type
 * @param {ApiError} error - The API error
 * @returns {string} - Formatted error message
 */
export const formatErrorMessage = (error) => {
  let message = error.message

  if (error.isNetworkError) {
    message += '\n\nPlease check your internet connection and try again.'
  } else if (error.isServerError) {
    message += '\n\nOur server is experiencing issues. Please try again in a few moments.'
  } else if (error.isValidationError) {
    message += '\n\nPlease review your input and try again.'
  }

  return message
}

export default {
  handleApiError,
  handleNetworkError,
  extractValidationErrors,
  formatErrorMessage,
  ApiError
}
