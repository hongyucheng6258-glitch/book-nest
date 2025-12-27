import { createRouter, createWebHistory } from 'vue-router'

// 定义路由
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/category/:id',
    name: 'Category',
    component: () => import('../views/Category.vue')
  },
  {
    path: '/book/:id',
    name: 'BookDetail',
    component: () => import('../views/BookDetail.vue')
  },
  { path: '/cart', name: 'Cart', component: () => import('../views/Cart.vue') },
  { path: '/order/confirm', name: 'OrderConfirm', component: () => import('../views/OrderConfirm.vue') },
  { path: '/order', name: 'Order', component: () => import('../views/Order.vue') },
  { path: '/order/:id', name: 'OrderDetail', component: () => import('../views/OrderDetail.vue') },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue')
  },
  {
    path: '/favorite',
    name: 'Favorite',
    component: () => import('../views/Favorite.vue')
  },
  {
    path: '/history',
    name: 'ReadingHistory',
    component: () => import('../views/ReadingHistory.vue')
  },
  {
    path: '/notifications',
    name: 'Notifications',
    component: () => import('../views/Notifications.vue')
  },
  {
    path: '/search',
    name: 'SearchResult',
    component: () => import('../views/SearchResult.vue')
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('../views/Chat.vue')
  },
  // 管理员路由
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('../views/admin/AdminDashboard.vue')
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查是否需要登录的路由
  const requireAuthPaths = ['/cart', '/profile', '/favorite', '/chat']
  const requireAuthRegex = /^\/order(?:\/|$)/
  
  // 管理员路由正则
  const adminRouteRegex = /^\/admin\//
  
  // 打印调试信息
  console.log('路由守卫调试信息:')
  console.log('当前路由:', to.path)
  console.log('localStorage中的token:', localStorage.getItem('token'))
  console.log('localStorage中的userInfo:', localStorage.getItem('userInfo'))
  
  // 获取登录状态和用户信息
  const token = localStorage.getItem('token')
  const userInfoStr = localStorage.getItem('userInfo')
  const userInfo = userInfoStr ? JSON.parse(userInfoStr) : null
  const isLogin = !!token && !!userInfo
  const isAdmin = userInfo?.privilege === '1'
  
  console.log('计算出的isLogin:', isLogin)
  
  // 处理管理员路由
  if (adminRouteRegex.test(to.path)) {
    if (!isLogin) {
      console.log('管理员路由未登录，跳转到登录页')
      next('/login')
    } else if (!isAdmin) {
      console.log('管理员路由已登录但不是管理员，跳转到首页')
      next('/')
    } else {
      console.log('管理员路由已登录且是管理员，允许访问')
      next()
    }
    return
  }
  
  // 处理普通需要登录的路由
  const isAuthRequired = requireAuthPaths.includes(to.path) || requireAuthRegex.test(to.path)
  console.log('isAuthRequired:', isAuthRequired)
  
  if (isAuthRequired && !isLogin) {
    console.log('普通需要登录的路由未登录，跳转到登录页')
    next('/login')
  } else {
    console.log('允许访问')
    next()
  }
})

export default router