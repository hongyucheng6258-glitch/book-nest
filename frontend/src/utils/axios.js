import axios from 'axios'

// 创建axios实例
const instance = axios.create({
  baseURL: '', // 后端API地址，请求路径已经包含/api前缀
  timeout: 10000 // 请求超时时间
})

// 请求拦截器
instance.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    
    // 如果token存在，添加到请求头
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 设置请求头的字符编码为UTF-8
    config.headers['Content-Type'] = 'application/json;charset=UTF-8'
    
    // 修复URL，将http://localhost:8088替换为相对路径/
    if (config.url && config.url.startsWith('http://localhost:8088')) {
      config.url = config.url.replace('http://localhost:8088', '')
    }
    
    return config
  },
  error => {
    // 请求错误处理
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  response => {
    // 只返回数据部分
    return response.data
  },
  error => {
    // 响应错误处理
    console.error('响应错误:', error)
    
    // 处理401和403未授权/禁止访问错误
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      // 清除本地存储的token和用户信息
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      
      // 跳转到登录页
      window.location.href = '/login'
    }
    
    return Promise.reject(error)
  }
)

export default instance