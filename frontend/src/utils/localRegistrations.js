const REGISTRATIONS_KEY = 'contestRegistrations'

export const getContestRegistrations = () => {
  try {
    const raw = localStorage.getItem(REGISTRATIONS_KEY)
    const parsed = JSON.parse(raw || '{}')
    return parsed && typeof parsed === 'object' ? parsed : {}
  } catch (_) {
    return {}
  }
}

export const getContestRegistration = (contestId) => {
  const map = getContestRegistrations()
  return map[contestId] || null
}

export const saveContestRegistration = (contestId, payload) => {
  const map = getContestRegistrations()
  map[contestId] = {
    ...payload,
    registeredAt: new Date().toISOString()
  }
  localStorage.setItem(REGISTRATIONS_KEY, JSON.stringify(map))
  return map[contestId]
}

export const clearContestRegistrations = () => {
  localStorage.removeItem(REGISTRATIONS_KEY)
}
