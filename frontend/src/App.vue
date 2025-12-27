<template>
  <div class="app-container">
    <!-- 顶部导航栏 (仅在非管理员页面显示) -->
    <FixedHeader v-if="!isAdminRoute" class="modern-header" />
    
    <!-- 主内容区域 -->
    <main :class="['main-content', { 'admin-layout': isAdminRoute }]">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    
    <!-- 底部导航 (移动端增强，桌面端隐藏或作为页脚) -->
    <footer v-if="!isAdminRoute" class="modern-footer">
      <div class="container footer-inner">
        <div class="copyright">© 2025 Virtual Art Gallery. All rights reserved.</div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import FixedHeader from './components/FixedHeader.vue'

const route = useRoute()

// 判断是否为管理员路由
const isAdminRoute = computed(() => {
  return /^\/admin\//.test(route.path)
})
</script>

<style>
/* 页面过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 布局容器 */
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-secondary);
}

/* 主内容区 */
.main-content {
  flex: 1;
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
  padding: 80px 24px 40px; /* 顶部留出Header高度 */
  box-sizing: border-box;
}

.main-content.admin-layout {
  padding: 0;
  max-width: none;
}

/* 现代化Header样式覆盖 (如果在FixedHeader组件内部不容易改，可以在这里覆盖) */
.modern-header {
  backdrop-filter: blur(12px);
  background: rgba(255, 255, 255, 0.9) !important;
  box-shadow: 0 1px 0 rgba(0,0,0,0.05) !important;
  border-bottom: 1px solid rgba(0,0,0,0.05);
}

/* 简约Footer */
.modern-footer {
  padding: 24px 0;
  border-top: 1px solid var(--border-light);
  background: var(--bg-primary);
  margin-top: auto;
}

.footer-inner {
  text-align: center;
  color: var(--text-secondary);
  font-size: 14px;
}
</style>