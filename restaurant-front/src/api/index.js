import axios from 'axios'

const baseURL = 'http://localhost:8080'

const api = axios.create({
  baseURL,
  timeout: 10000
})

// 请求拦截器（和截图代码完全一致，无token时不携带Authorization头）
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器（原有代码完整保留，未做任何修改）
api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  error => {
    return Promise.reject(error)
  }
)

export default api