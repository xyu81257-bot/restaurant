// api/inventory.js
import api from './index'

export const inventoryApi = {
  getList() {
    return api.get('/api/inventory')
  },
  add(data) {
    return api.post('/api/inventory', data)
  },
  update(data) {
    return api.put('/api/inventory', data)
  },
  delete(id) {
    return api.delete(`/api/inventory/${id}`)
  }
}