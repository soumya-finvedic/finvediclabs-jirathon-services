const now = Date.now()

export const sampleContests = [
  {
    id: 'sample-contest-1',
    title: 'Jirathon Starter Sprint',
    description: 'Quick 90-minute challenge focused on arrays, strings, and debugging fundamentals.',
    status: 'PUBLISHED',
    contestType: 'PUBLIC',
    registrationFee: 99,
    registrationDeadline: new Date(now + 2 * 24 * 60 * 60 * 1000).toISOString(),
    startTime: new Date(now + 3 * 24 * 60 * 60 * 1000).toISOString(),
    endTime: new Date(now + 3 * 24 * 60 * 60 * 1000 + 90 * 60 * 1000).toISOString()
  },
  {
    id: 'sample-contest-2',
    title: 'Full Stack API Gauntlet',
    description: 'Build and optimize secure APIs, then pass hidden test cases under strict time limits.',
    status: 'DRAFT',
    contestType: 'TEAM',
    registrationFee: 249,
    registrationDeadline: new Date(now + 5 * 24 * 60 * 60 * 1000).toISOString(),
    startTime: new Date(now + 6 * 24 * 60 * 60 * 1000).toISOString(),
    endTime: new Date(now + 6 * 24 * 60 * 60 * 1000 + 2 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 'sample-contest-3',
    title: 'System Design Duel',
    description: 'Design scalable services, evaluate tradeoffs, and present architecture decisions.',
    status: 'ARCHIVED',
    contestType: 'INVITE',
    registrationFee: 399,
    registrationDeadline: new Date(now - 9 * 24 * 60 * 60 * 1000).toISOString(),
    startTime: new Date(now - 8 * 24 * 60 * 60 * 1000).toISOString(),
    endTime: new Date(now - 8 * 24 * 60 * 60 * 1000 + 2 * 60 * 60 * 1000).toISOString()
  }
]
