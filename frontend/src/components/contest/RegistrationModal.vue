<template>
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50">
        <div class="card w-full max-w-md p-6 animate-slide-in-down">
            <div class="flex items-start justify-between mb-4">
                <div>
                    <h3 class="text-xl font-bold">Contest Registration</h3>
                    <p class="text-sm text-gray-600 dark:text-gray-400">{{ contestTitle }}</p>
                </div>
                <button class="btn-ghost" @click="$emit('close')">Close</button>
            </div>

            <form class="space-y-4" @submit.prevent="submit">
                <div>
                    <label class="block text-sm mb-1">{{ nameLabel }}</label>
                    <input v-model.trim="form.teamName" class="input" type="text" :placeholder="namePlaceholder" required />
                </div>

                <div>
                    <label class="block text-sm mb-1">Contact Number</label>
                    <input v-model.trim="form.contact" class="input" type="tel" placeholder="9876543210" required />
                </div>

                <div>
                    <label class="block text-sm mb-1">Coupon Code (optional)</label>
                    <input v-model.trim="form.coupon" class="input" type="text" placeholder="JIRA10" />
                </div>

                <button class="btn-primary w-full" :disabled="saving">
                    {{ saving ? 'Saving...' : submitLabel }}
                </button>
            </form>
        </div>
    </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
    contestTitle: {
        type: String,
        required: true
    },
    initialData: {
        type: Object,
        default: () => ({})
    },
    submitLabel: {
        type: String,
        default: 'Confirm Registration'
    },
    contestType: {
        type: String,
        default: 'INDIVIDUAL'
    }
})

const emit = defineEmits(['submit', 'close'])
const saving = ref(false)

const form = ref({
    teamName: '',
    contact: '',
    coupon: ''
})

const isTeamContest = computed(() => String(props.contestType || '').toUpperCase() === 'TEAM')
const nameLabel = computed(() => (isTeamContest.value ? 'Team Name' : 'Individual Name'))
const namePlaceholder = computed(() => (isTeamContest.value ? 'Code Titans' : 'John Doe'))

watch(
    () => props.initialData,
    (value) => {
        form.value = {
            teamName: value?.teamName || '',
            contact: value?.contact || '',
            coupon: value?.coupon || ''
        }
    },
    { immediate: true }
)

const submit = async () => {
    saving.value = true
    try {
        emit('submit', {
            teamName: form.value.teamName,
            contact: form.value.contact,
            coupon: form.value.coupon
        })
    } finally {
        saving.value = false
    }
}
</script>
