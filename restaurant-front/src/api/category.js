// api/category.js
import api from './index'

export const categoryApi = {
  getList() {
    return api.get('/api/category')
  },
  add(data) {
    return api.post('/api/category', data)
  },
  update(data) {
    return api.put('/api/category', data)
  },
  delete(id) {
    return api.delete(`/api/category/${id}`)
  }
}