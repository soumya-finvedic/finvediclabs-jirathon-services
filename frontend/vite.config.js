import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

const buildProxyRule = (target, fromPrefix, toPrefix) => ({
  target,
  changeOrigin: true,
  rewrite: (requestPath) => requestPath.replace(new RegExp(`^${fromPrefix}`), toPrefix)
})

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  const authTarget         = env.VITE_AUTH_SERVICE_URL         || 'http://localhost:8081'
  const userTarget         = env.VITE_USER_SERVICE_URL         || 'http://localhost:8083'
  const organizationTarget = env.VITE_ORG_SERVICE_URL          || 'http://localhost:8084'
  const contestTarget      = env.VITE_CONTEST_SERVICE_URL      || 'http://localhost:8085'
  const executionTarget    = env.VITE_EXECUTION_SERVICE_URL    || 'http://localhost:8086'
  const leaderboardTarget  = env.VITE_LEADERBOARD_SERVICE_URL  || 'http://localhost:8087'
  const paymentTarget      = env.VITE_PAYMENT_SERVICE_URL      || 'http://localhost:8088'
  const notificationTarget = env.VITE_NOTIFICATION_SERVICE_URL || 'http://localhost:8091'

  const proxy = {
    '/api/auth':                buildProxyRule(authTarget,         '/api/auth',                '/api/v1/auth'),
    '/api/contests':            buildProxyRule(contestTarget,      '/api/contests',            '/api/v1/contests'),
    '/api/admin/contests':      buildProxyRule(contestTarget,      '/api/admin/contests',      '/api/v1/admin/contests'),
    '/api/execution':           buildProxyRule(executionTarget,    '/api/execution',           '/api/v1/executions'),
    '/api/executions':          buildProxyRule(executionTarget,    '/api/executions',          '/api/v1/executions'),
    '/api/leaderboards':        buildProxyRule(leaderboardTarget,  '/api/leaderboards',        '/api/v1/leaderboards'),
    '/api/admin/leaderboards':  buildProxyRule(leaderboardTarget,  '/api/admin/leaderboards',  '/api/v1/admin/leaderboards'),
    '/api/users':               buildProxyRule(userTarget,         '/api/users',               '/api/v1/users'),
    '/api/admin/users':         buildProxyRule(userTarget,         '/api/admin/users',         '/api/v1/admin/users'),
    '/api/organizations':       buildProxyRule(organizationTarget, '/api/organizations',       '/api/v1/organizations'),
    '/api/admin/organizations': buildProxyRule(organizationTarget, '/api/admin/organizations', '/api/v1/admin/organizations'),
    '/api/payments':            buildProxyRule(paymentTarget,      '/api/payments',            '/api/v1/payments'),
    '/api/coupons':             buildProxyRule(paymentTarget,      '/api/coupons',             '/api/v1/coupons'),
    '/api/notifications':       buildProxyRule(notificationTarget, '/api/notifications',       '/api/v1/notifications'),

    // PayU POSTs to these two routes on the backend after payment.
    // The backend processes the webhook then redirects to /payment/callback.
    // IMPORTANT: /payment/callback must NOT be here — it's a Vue route, not a backend route.
    '/payment/success': { target: paymentTarget, changeOrigin: true },
    '/payment/failure': { target: paymentTarget, changeOrigin: true },
  }

  if (env.VITE_API_GATEWAY_URL) {
    proxy['/api'] = {
      target: env.VITE_API_GATEWAY_URL,
      changeOrigin: true,
      rewrite: (requestPath) => requestPath.replace(/^\/api/, '/api/v1')
    }
  }

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': path.resolve(process.cwd(), './src'),
      },
    },
    server: {
      port: 5173,
      proxy,
    },
    build: {
      outDir: 'dist',
      sourcemap: false,
      minify: 'esbuild'
    }
  }
})