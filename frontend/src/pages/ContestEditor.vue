<template>
  <div class="flex flex-col h-full">
    <!-- Top Bar -->
    <div class="bg-white dark:bg-dark-800 border-b border-gray-200 dark:border-dark-700 p-4 flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-900 dark:text-gray-100">{{ currentProblem?.title }}</h1>
        <p class="text-sm text-gray-600 dark:text-gray-400">Problem {{ currentProblemIndex + 1 }} of {{ problems.length }}</p>
      </div>

      <div class="flex gap-2">
        <button @click="submitSolution" class="btn-primary">
          🚀 Submit Solution
        </button>
        <RouterLink :to="`/contests/${contestId}`" class="btn-secondary">
          ← Back
        </RouterLink>
      </div>
    </div>

    <!-- Main Content -->
    <div class="flex flex-1 gap-4 overflow-hidden">
      <!-- Problem Statement (Left) -->
      <div class="w-1/2 overflow-y-auto p-4 border-r border-gray-200 dark:border-dark-700">
        <div v-if="currentProblem" class="space-y-4">
          <div>
            <h2 class="text-xl font-bold mb-2 text-gray-900 dark:text-gray-100">{{ currentProblem.title }}</h2>
            <div class="space-y-2 mb-4">
              <span class="badge badge-info">{{ currentProblem.difficulty }}</span>
              <span class="badge badge-success">{{ currentProblem.solvedCount }} solved</span>
            </div>
          </div>

          <div class="prose dark:prose-invert max-w-none">
            <h3 class="font-bold text-gray-900 dark:text-gray-100">Description</h3>
            <p class="text-gray-700 dark:text-gray-300">{{ currentProblem.description }}</p>

            <h3 class="font-bold mt-4 text-gray-900 dark:text-gray-100">Examples</h3>
            <div class="space-y-2 text-sm">
              <div v-for="(example, i) in currentProblem.examples" :key="i" class="bg-gray-100 dark:bg-dark-700 p-3 rounded">
                <p class="font-semibold">Example {{ i + 1 }}:</p>
                <p class="font-mono">Input: {{ example.input }}</p>
                <p class="font-mono">Output: {{ example.output }}</p>
              </div>
            </div>

            <h3 class="font-bold mt-4 text-gray-900 dark:text-gray-100">Constraints</h3>
            <ul class="list-disc list-inside text-gray-700 dark:text-gray-300">
              <li v-for="constraint in currentProblem.constraints" :key="constraint">
                {{ constraint }}
              </li>
            </ul>
          </div>

          <!-- Navigation -->
          <div class="flex gap-2 pt-4">
            <button 
              v-if="currentProblemIndex > 0"
              @click="currentProblemIndex--"
              class="btn-secondary flex-1"
            >
              ← Previous
            </button>
            <button 
              v-if="currentProblemIndex < problems.length - 1"
              @click="currentProblemIndex++"
              class="btn-secondary flex-1"
            >
              Next →
            </button>
          </div>
        </div>
      </div>

      <!-- Code Editor (Right) -->
      <div class="w-1/2">
        <CodeEditor 
          ref="editorRef"
          :initialCode="currentProblem?.boilerplate || defaultCode"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useContest } from '@/composables/useContests'
import CodeEditor from '@/components/editor/CodeEditor.vue'

const route = useRoute()
const { contest, questions: problems, loading, fetchContest, fetchQuestions } = useContest(route.params.id)

const editorRef = ref(null)
const contestId = route.params.id
const currentProblemIndex = ref(0)
const defaultCode = '// Write your solution here\n\nfunction solve() {\n  \n}\n'

const currentProblem = computed(() => problems.value[currentProblemIndex.value])

onMounted(async () => {
  await fetchContest()
  await fetchQuestions()
})

const submitSolution = async () => {
  if (!editorRef.value?.editor) return
  
  const code = editorRef.value.editor.getValue()
  // Submit to API
  console.log('Submitting:', code)
  // await submissionAPI.submit(contestId, currentProblem.value.id, { code })
}
</script>
