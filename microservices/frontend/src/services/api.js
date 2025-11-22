import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

export const userService = {
  getAll: () => api.get('/users'),
  getById: (id) => api.get(`/users/${id}`),
  create: (user) => api.post('/users', user)
}

export const hospitalService = {
  getAll: () => api.get('/hospitals'),
  getById: (id) => api.get(`/hospitals/${id}`)
}

export const paymentService = {
  processPayment: (payment) => api.post('/payments', payment),
  getByUser: (userId) => api.get(`/payments/user/${userId}`)
}

export const notificationService = {
  send: (notification) => api.post('/notifications', notification),
  getByUser: (userId) => api.get(`/notifications/user/${userId}`)
}

export default api

