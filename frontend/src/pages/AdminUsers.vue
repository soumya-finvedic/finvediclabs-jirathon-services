<template>
  <div class="p-6 max-w-7xl mx-auto">
    <h1 class="text-3xl font-bold mb-6 text-gray-900 dark:text-gray-100">Manage Users</h1>

    <!-- Search and Filters -->
    <div class="card p-4 mb-6 flex gap-4">
      <input 
        v-model="searchQuery"
        type="text" 
        placeholder="Search users..." 
        class="input flex-1"
      />
      <select v-model="roleFilter" class="select w-40">
        <option value="">All Roles</option>
        <option value="USER">User</option>
        <option value="ADMIN">Admin</option>
      </select>
    </div>

    <!-- Users Table -->
    <div class="card overflow-hidden">
      <table class="table w-full">
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Status</th>
            <th>Joined</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in filteredUsers" :key="user.id">
            <td>{{ user.name }}</td>
            <td>{{ user.email }}</td>
            <td>
              <span class="badge" :class="user.role === 'ADMIN' ? 'badge-warning' : 'badge-info'">
                {{ user.role }}
              </span>
            </td>
            <td>
              <span class="badge" :class="user.status === 'ACTIVE' ? 'badge-success' : 'badge-danger'">
                {{ user.status }}
              </span>
            </td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td>
              <button class="btn-ghost text-sm">View</button>
              <button class="btn-ghost text-sm">Edit</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const searchQuery = ref('')
const roleFilter = ref('')
const users = ref([
  { id: 1, name: 'John Doe', email: 'john@example.com', role: 'USER', status: 'ACTIVE', createdAt: '2024-01-15' },
  { id: 2, name: 'Jane Admin', email: 'jane@example.com', role: 'ADMIN', status: 'ACTIVE', createdAt: '2024-01-01' }
])

const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchSearch = !searchQuery.value || 
      user.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.email.toLowerCase().includes(searchQuery.value.toLowerCase())
    
    const matchRole = !roleFilter.value || user.role === roleFilter.value
    
    return matchSearch && matchRole
  })
})

const formatDate = (date) => new Date(date).toLocaleDateString()
</script>
