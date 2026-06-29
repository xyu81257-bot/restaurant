// api/desk.js
import api from './index'

export const deskApi = {
  getList() {
    return api.get('/api/desk')
  },
  add(data) {
    return api.post('/api/desk', data)
  },
  update(data) {
    return api.put('/api/desk', data)
  },
  delete(id) {
    return api.delete(`/api/desk/${id}`)
  }
}