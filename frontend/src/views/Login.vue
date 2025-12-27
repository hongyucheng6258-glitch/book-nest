<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="login-background">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>
    
    <!-- 登录卡片 -->
    <el-card class="login-card" shadow="hover">
      <!-- 卡片头部 -->
      <template #header>
        <div class="card-header">
          <div class="header-icon">
            <el-icon class="icon-large"><User /></el-icon>
          </div>
          <h2 class="header-title">欢迎回来</h2>
          <p class="header-subtitle">请登录您的账号</p>
        </div>
      </template>
      
      <!-- 登录表单 -->
      <el-form 
        :model="loginForm" 
        :rules="loginRules" 
        ref="loginFormRef" 
        label-position="top"
        class="login-form"
      >
        <!-- 用户名输入 -->
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        
        <!-- 身份选择 -->
        <el-form-item>
          <div class="identity-selector">
            <span class="identity-label">登录身份：</span>
            <el-radio-group v-model="loginForm.identity" size="large" class="identity-radio-group">
              <el-radio-button label="user">普通用户</el-radio-button>
              <el-radio-button label="admin">管理员</el-radio-button>
            </el-radio-group>
          </div>
        </el-form-item>
        
        <!-- 密码输入 -->
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <!-- 记住密码和忘记密码 -->
        <div class="form-options">
          <el-checkbox v-model="loginForm.remember" size="large">
            <span class="remember-text">记住密码</span>
          </el-checkbox>
          <el-link 
            type="primary" 
            href="#" 
            @click.prevent="handleForgotPassword"
            size="large"
          >
            忘记密码？
          </el-link>
        </div>
        
        <!-- 登录按钮 -->
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleLogin" 
            :loading="loading" 
            style="width: 100%"
            size="large"
            round
          >
            <template #icon>
              <el-icon v-if="!loading"><Right /></el-icon>
              <el-icon v-else class="is-loading"><Loading /></el-icon>
            </template>
            登录
          </el-button>
        </el-form-item>
        
        <!-- 注册链接 -->
        <div class="login-footer">
          <span class="footer-text">还没有账号？</span>
          <el-link 
            type="primary" 
            @click="goToRegister"
            size="large"
            class="register-link"
          >
            立即注册
          </el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/userStore'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'
import {
  User,
  Lock,
  Right,
  Loading
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 登录表单
const loginForm = reactive({
  username: '',
  password: '',
  remember: false,
  identity: 'user' // 默认普通用户
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 表单引用
const loginFormRef = ref(null)

// 加载状态
const loading = ref(false)

// 组件挂载时，从localStorage恢复记住的密码
onMounted(() => {
  const savedUsername = localStorage.getItem('rememberedUsername')
  const savedPassword = localStorage.getItem('rememberedPassword')
  if (savedUsername && savedPassword) {
    loginForm.username = savedUsername
    loginForm.password = savedPassword
    loginForm.remember = true
  }
})

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    // 表单验证
    await loginFormRef.value.validate()
    
    // 设置加载状态
    loading.value = true
    
    // 调用登录API
    const response = await axios.post('/api/auth/login', loginForm)
    
    // 登录成功
    if (response.success) {
      // 保存用户信息到store
      const basicUserInfo = {
        id: response.data.id,
        username: response.data.username,
        privilege: response.data.privilege
      }
      
      // 清除旧的localStorage数据，确保使用最新的用户ID
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
      
      userStore.login(response.data.token, basicUserInfo)
      
      // 记住密码
      if (loginForm.remember) {
        localStorage.setItem('rememberedUsername', loginForm.username)
        localStorage.setItem('rememberedPassword', loginForm.password)
      } else {
        localStorage.removeItem('rememberedUsername')
        localStorage.removeItem('rememberedPassword')
      }
      
      // 获取完整用户信息，包括头像、昵称等
      try {
        const userInfoResponse = await axios.get('/api/user/info', {
          params: { userId: response.data.id }
        })
        
        if (userInfoResponse.success && userInfoResponse.data) {
          // 更新用户信息，包括头像、昵称等完整信息
          userStore.updateUserInfo({
            ...basicUserInfo,
            ...userInfoResponse.data
          })
        }
      } catch (error) {
        console.error('获取用户详情失败:', error)
      }
      
      // 显示成功消息
      ElMessage.success('登录成功，正在跳转...')
      
      // 根据用户选择的身份跳转到不同页面
      if (loginForm.identity === 'admin' && response.data.privilege === '1') {
        // 选择管理员身份且确实是管理员，跳转到后台管理页面
        router.push('/admin/dashboard')
      } else {
        // 选择普通用户身份或不是管理员，跳转到首页
        router.push('/')
      }
    } else {
      // 登录失败
      ElMessage.error(response.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.response?.data?.message || '登录失败，请检查网络或账号信息')
  } finally {
    // 重置加载状态
    loading.value = false
  }
}

// 跳转到注册页
const goToRegister = () => {
  router.push('/register')
}

// 忘记密码处理
const handleForgotPassword = () => {
  ElMessage.info('忘记密码功能正在开发中')
}
</script>

<style scoped>
/* 登录容器 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  position: relative;
  overflow: hidden;
  background: var(--bg-secondary);
}

/* 背景装饰 */
.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: float 10s ease-in-out infinite;
}

.shape-1 {
  width: 500px;
  height: 500px;
  background: var(--primary-light);
  top: -10%;
  left: -10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: #c7d2fe; 
  bottom: -10%;
  right: -10%;
  animation-delay: 3s;
}

.shape-3 {
  width: 300px;
  height: 300px;
  background: #e0e7ff;
  top: 40%;
  right: 20%;
  animation-delay: 5s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(20px, -20px); }
}

/* 登录卡片 */
.login-card {
  width: 100%;
  max-width: 440px;
  z-index: 1;
  border-radius: var(--radius-xl);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.login-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 35px 60px -15px rgba(0, 0, 0, 0.15);
}

/* 卡片头部 */
.card-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 10px 0;
}

.header-icon {
  margin-bottom: 16px;
  background: var(--primary-light);
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-large {
  font-size: 32px;
  color: var(--primary-color);
}

.header-title {
  margin: 0;
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.header-subtitle {
  margin: 0;
  font-size: 1rem;
  color: var(--text-secondary);
}

/* 登录表单 */
.login-form {
  padding: 10px 20px 20px;
}

/* 身份选择器 */
.identity-selector {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.identity-label {
  font-size: 14px;
  color: var(--text-regular);
  margin-right: 12px;
}

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.remember-text {
  font-size: 14px;
  color: var(--text-regular);
}

/* 登录按钮 */
:deep(.el-button--primary) {
  background: var(--primary-gradient);
  border: none;
  border-radius: 99px;
  font-weight: 600;
  height: 48px;
  font-size: 1rem;
  box-shadow: 0 4px 6px -1px rgba(79, 70, 229, 0.3);
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-1px);
  box-shadow: 0 10px 15px -3px rgba(79, 70, 229, 0.4);
  filter: brightness(1.1);
}

:deep(.el-button--primary:active) {
  transform: translateY(0);
}

/* 登录页脚 */
.login-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 16px;
}

.footer-text {
  font-size: 14px;
  color: var(--text-secondary);
}

.register-link {
  font-weight: 600;
}

/* 输入框样式 */
:deep(.el-input__wrapper) {
  border-radius: var(--radius-md);
  box-shadow: 0 0 0 1px var(--border-light) inset !important;
  background: rgba(255, 255, 255, 0.5);
  height: 44px;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--primary-color) inset !important;
  background: white;
}

/* 响应式设计 */
@media (max-width: 576px) {
  .login-card {
    margin: 20px;
    width: auto;
  }
}
</style>