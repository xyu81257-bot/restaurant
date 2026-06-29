// src/api/user.js
import api from './index'

export const userApi = {
  // 登录
  login(data) {
    return api.post('/api/user/login', data)
  },
  // 注册
  register(data) {
    return api.post('/api/user/register', data)
  },
  // 获取当前用户信息
  getInfo() {
    return api.get('/api/user/info')
  },
  // 获取用户列表（管理员）
  getList() {
    return api.get('/api/user/list')
  },
  // 添加用户（管理员）
  add(data) {
    return api.post('/api/user/add', data)
  },
  // 更新用户（管理员）
  update(data) {
    return api.put('/api/user/update', data)
  },
  // 删除用户（管理员）
  delete(id) {
    return api.delete(`/api/user/delete/${id}`)
  }
}