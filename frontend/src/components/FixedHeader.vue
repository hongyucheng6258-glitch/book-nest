<template>
  <div class="fixed-header">
    <div class="header-content">
      <div class="logo" @click="router.push('/')">书城App</div>
      <div class="search">
        <el-input v-model="keyword" placeholder="搜索书籍" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      <div class="user-menu">
        <el-button v-if="!isLogin" @click="goToLogin" size="default" plain>
          登录
        </el-button>
        <el-dropdown v-else-if="userInfo && userInfo.username" trigger="hover">
          <div class="user-info-dropdown">
            <el-avatar :src="userInfo.avatar" size="small" class="user-avatar">
              {{ (userInfo.nickname || userInfo.username).charAt(0) }}
            </el-avatar>
            <span class="user-name">{{ userInfo.nickname || userInfo.username }}</span>
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goToProfile">
                <el-icon><User /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item @click="goToOrder">
                <el-icon><Document /></el-icon>
                我的订单
              </el-dropdown-item>
              <el-dropdown-item @click="goToChat">
                <el-icon><ChatLineRound /></el-icon>
                好友列表
              </el-dropdown-item>
              <el-dropdown-item @click="goToFavorite">
                <el-icon><Star /></el-icon>
                我的收藏
              </el-dropdown-item>
              <el-dropdown-item divided @click="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-button v-else @click="goToLogin" size="default" plain>
          登录
        </el-button>
        <el-dropdown v-if="isLogin" trigger="hover">
          <el-button circle>
            <el-icon><shopping-cart /></el-icon>
            <span v-if="totalQuantity > 0" class="cart-badge">{{ totalQuantity }}</span>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goToCart">
                <el-icon><shopping-cart /></el-icon>
                查看购物车
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/userStore'
import { useCartStore } from '../store/cartStore'
import {
  ArrowDown,
  ShoppingCart,
  User,
  Document,
  SwitchButton,
  ChatLineRound,
  Star
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

// 搜索关键字
const keyword = ref('')

// 使用computed属性缓存依赖的状态，避免直接依赖store导致的递归更新
const isLogin = computed(() => {
  console.log('isLogin computed called')
  return userStore.isLogin
})

const userInfo = computed(() => {
  console.log('userInfo computed called')
  return userStore.userInfo
})

const totalQuantity = computed(() => {
  console.log('totalQuantity computed called')
  return cartStore.totalQuantity
})

// 处理搜索
const handleSearch = () => {
  if (keyword.value) {
    router.push({ path: '/category/0', query: { keyword: keyword.value } })
  }
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 跳转到购物车
const goToCart = () => {
  router.push('/cart')
}

// 跳转到个人中心
const goToProfile = () => {
  router.push('/profile')
}

// 跳转到订单列表
const goToOrder = () => {
  router.push('/order')
}

// 跳转到聊天页面（好友列表）
const goToChat = () => {
  router.push('/chat')
}

// 跳转到收藏页面
const goToFavorite = () => {
  router.push('/favorite')
}

// 退出登录
const logout = () => {
  userStore.logout()
  router.push('/')
}

// 检查是否有其他地方可能导致递归更新
console.log('FixedHeader component setup called')
</script>

<style scoped>
/* 固定定位导航栏 */
.fixed-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 72px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  /* 默认样式，会被父组件的 class 覆盖或增强 */
  background: var(--glass-bg);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: var(--shadow-sm);
}

/* 导航栏内容容器 */
.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 40px;
}

/* Logo样式 */
.logo {
  font-size: 26px;
  font-weight: 800;
  background: var(--primary-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  cursor: pointer;
  letter-spacing: -0.5px;
  transition: opacity 0.2s;
}

.logo:hover {
  opacity: 0.9;
}

/* 搜索区域样式 */
.search {
  flex: 1;
  max-width: 500px;
  margin: 0 40px;
  position: relative;
}

/* 搜索框深度定制 */
.search :deep(.el-input__wrapper) {
  border-radius: 9999px; /* 全圆角 */
  background-color: var(--bg-secondary);
  box-shadow: none !important;
  border: 1px solid transparent;
  padding-left: 20px;
  padding-right: 90px; /* 为按钮留出空间 */
  height: 48px;
}

.search :deep(.el-input__wrapper:hover) {
  background-color: #fff;
  border-color: var(--border-light);
  box-shadow: var(--shadow-sm) !important;
}

.search :deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px var(--primary-light) !important;
}

.search :deep(.el-button) {
  position: absolute;
  right: 6px;
  top: 6px;
  bottom: 6px;
  border-radius: 9999px;
  background: var(--primary-gradient);
  border: none;
  font-weight: 600;
  padding: 0 24px;
  height: auto;
}

.search :deep(.el-button:hover) {
  filter: brightness(1.1);
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(79, 70, 229, 0.3);
}

/* 用户菜单样式 */
.user-menu {
  display: flex;
  align-items: center;
  gap: 24px;
}

/* 用户信息区域 */
.user-info-dropdown {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 9999px;
  transition: all 0.2s;
}

.user-info-dropdown:hover {
  background-color: var(--bg-secondary);
}

.user-avatar {
  border: 2px solid #fff;
  box-shadow: 0 0 0 1px var(--border-light);
}

.user-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 15px;
}

/* 购物车按钮 */
.cart-btn-wrapper {
  position: relative;
}

.cart-badge {
  position: absolute;
  top: -6px;
  right: -6px;
  background: var(--danger-color);
  color: white;
  font-size: 11px;
  font-weight: 700;
  height: 18px;
  min-width: 18px;
  padding: 0 5px;
  border-radius: 99px;
  border: 2px solid #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .header-content {
    padding: 0 24px;
  }
}

@media (max-width: 768px) {
  .fixed-header {
    height: 64px;
  }
  
  .header-content {
    justify-content: space-between;
    gap: 16px;
  }
  
  .search {
    display: none; /* 移动端暂时隐藏搜索框，或改为图标触发 */
  }
  
  .logo {
    font-size: 20px;
  }
}
</style>