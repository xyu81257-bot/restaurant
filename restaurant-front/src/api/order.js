// api/order.js
import api from './index'

export const orderApi = {
  // 创建订单
  create(data) {
    return api.post('/api/order/create', data)
  },
  // 获取订单列表
  getList(params) {
    return api.get('/api/order/list', { params })
  },
  // 获取订单详情
  getDetail(orderId) {
    return api.get(`/api/order/detail/${orderId}`)
  },
  // 更新订单状态
  updateStatus(orderId, orderStatus) {
    return api.put(`/api/order/status/${orderId}`, null, {
      params: { orderStatus }
    })
  },
  // 支付订单
  pay(orderId) {
    return api.post(`/api/order/pay/${orderId}`)
  },
  // 取消订单
  cancel(orderId) {
    return api.post(`/api/order/cancel/${orderId}`)
  }
}