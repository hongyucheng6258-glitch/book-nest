<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="login-background">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>
    
    <!-- 注册卡片 -->
    <el-card class="login-card" shadow="hover">
      <!-- 卡片头部 -->
      <template #header>
        <div class="card-header">
          <div class="header-icon">
            <el-icon class="icon-large"><User /></el-icon>
          </div>
          <h2 class="header-title">创建账号</h2>
          <p class="header-subtitle">加入我们，开始您的阅读之旅</p>
        </div>
      </template>
      
      <!-- 注册表单 -->
      <el-form 
        :model="registerForm" 
        :rules="registerRules" 
        ref="registerFormRef" 
        label-position="top"
        class="login-form"
      >
        <!-- 用户名输入 -->
        <el-form-item prop="username">
          <el-input 
            v-model="registerForm.username" 
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        
        <!-- 密码输入 -->
        <el-form-item prop="password">
          <el-input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <!-- 确认密码输入 -->
        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="请确认密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <!-- 注册按钮 -->
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleRegister" 
            :loading="loading" 
            style="width: 100%"
            size="large"
            round
          >
            <template #icon>
              <el-icon v-if="!loading"><Right /></el-icon>
              <el-icon v-else class="is-loading"><Loading /></el-icon>
            </template>
            注册
          </el-button>
        </el-form-item>
        
        <!-- 登录链接 -->
        <div class="login-footer">
          <span class="footer-text">已有账号？</span>
          <el-link 
            type="primary" 
            @click="goToLogin"
            size="large"
            class="register-link"
          >
            立即登录
          </el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'
import {
  User,
  Lock,
  Right,
  Loading
} from '@element-plus/icons-vue'

const router = useRouter()

// 注册表单
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请确认密码'))
        } else if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 表单引用
const registerFormRef = ref(null)

// 加载状态
const loading = ref(false)

// 注册处理
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    // 表单验证
    await registerFormRef.value.validate()
    
    // 设置加载状态
    loading.value = true
    
    // 调用注册API
    const response = await axios.post('/api/auth/register', {
      username: registerForm.username,
      password: registerForm.password
    })
    
    // 注册成功
    if (response.success) {
      ElMessage.success('注册成功，正在跳转到登录页...')
      // 跳转到登录页
      router.push('/login')
    } else {
      // 注册失败
      ElMessage.error(response.message || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error(error.response?.data?.message || '注册失败，请检查网络或账号信息')
  } finally {
    // 重置加载状态
    loading.value = false
  }
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
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

/* 注册按钮 */
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