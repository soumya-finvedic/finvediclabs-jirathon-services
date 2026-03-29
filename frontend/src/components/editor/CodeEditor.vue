<template>
  <div class="space-y-4">
    <!-- Language selector -->
    <div class="flex gap-2 flex-wrap">
      <button 
        v-for="lang in languages"
        :key="lang"
        @click="selectedLanguage = lang"
        :class="[
          'px-3 py-2 rounded-lg text-sm font-medium transition',
          selectedLanguage === lang
            ? 'bg-primary-600 text-white'
            : 'bg-gray-200 dark:bg-dark-700 text-gray-900 dark:text-gray-100 hover:bg-gray-300 dark:hover:bg-dark-600'
        ]"
      >
        {{ lang }}
      </button>
    </div>

    <!-- Code editor -->
    <div class="card border-0 overflow-hidden">
      <div ref="editorContainer" style="height: 400px;"></div>
    </div>

    <!-- Run button -->
    <button 
      @click="runCode"
      :disabled="isRunning"
      class="btn-primary w-full"
    >
      {{ isRunning ? '⏳ Running...' : '▶️ Run Code' }}
    </button>

    <!-- Output section -->
    <div v-if="output || error" class="card p-4">
      <h4 class="font-semibold mb-2">Output:</h4>
      <pre 
        :class="[
          'bg-gray-100 dark:bg-dark-700 p-3 rounded text-sm overflow-x-auto whitespace-pre-wrap break-words',
          error ? 'text-red-600 dark:text-red-400' : 'text-gray-900 dark:text-gray-100'
        ]"
      >{{ output || error }}</pre>
      <p v-if="executionTime" class="text-xs text-gray-500 dark:text-gray-400 mt-2">
        Execution time: {{ executionTime.toFixed(2) }}ms
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as monaco from 'monaco-editor'
import { useExecution } from '@/composables/useContests'

const props = defineProps({
  initialCode: {
    type: String,
    default: '// Write your code here\nfunction solve() {\n  \n}\n'
  }
})

const editorContainer = ref(null)
let editor = null
const selectedLanguage = ref('javascript')
const languages = ['javascript', 'python', 'java', 'cpp', 'c']
const { output, error, executionTime, isRunning, executeCode } = useExecution()

onMounted(() => {
  editor = monaco.editor.create(editorContainer.value, {
    value: props.initialCode,
    language: 'javascript',
    theme: document.documentElement.classList.contains('dark') ? 'vs-dark' : 'vs',
    fontSize: 14,
    fontFamily: 'Monaco, Consolas, monospace',
    minimap: { enabled: false },
    lineNumbers: 'on',
    scrollBeyondLastLine: false,
    automaticLayout: true
  })

  // Watch for dark mode changes
  const observer = new MutationObserver(() => {
    const isDark = document.documentElement.classList.contains('dark')
    monaco.editor.setTheme(isDark ? 'vs-dark' : 'vs')
  })

  observer.observe(document.documentElement, {
    attributes: true,
    attributeFilter: ['class']
  })
})

const runCode = async () => {
  const code = editor.getValue()
  await executeCode(code, selectedLanguage.value)
}

defineExpose({ editor })
</script>
