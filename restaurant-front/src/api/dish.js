// api/dish.js
import api from './index'

export const dishApi = {
  // 获取菜品列表
  getList() {
    return api.get('/api/dish')
  },
  // 添加菜品
  add(data) {
    return api.post('/api/dish', data)
  },
  // 更新菜品
  update(data) {
    return api.put('/api/dish', data)
  },
  // 删除菜品
  delete(id) {
    return api.delete(`/api/dish/${id}`)
  }
}