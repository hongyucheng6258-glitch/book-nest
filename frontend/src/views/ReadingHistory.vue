<template>
  <div class="reading-history">
    <h2 class="page-title">阅读历史</h2>
    
    <el-card shadow="hover" class="history-card">
      <div class="card-header">
        <div class="header-left">
          <h3>我的阅读记录</h3>
          <p class="history-count">共 {{ readingHistory.length }} 条记录</p>
        </div>
        <div class="header-right">
          <el-button 
            type="danger" 
            @click="handleClearHistory" 
            :disabled="readingHistory.length === 0"
          >
            <el-icon><Delete /></el-icon>
            清空历史
          </el-button>
        </div>
      </div>
      
      <!-- 阅读历史列表 -->
      <div class="history-list">
        <!-- 空状态 -->
        <el-empty 
          v-if="readingHistory.length === 0" 
          description="暂无阅读记录" 
          :image-size="200"
        >
          <el-button type="primary" @click="getReadingHistory">重新加载</el-button>
        </el-empty>
        
        <!-- 历史记录列表 -->
        <el-row :gutter="20" v-else>
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in readingHistory" :key="item.id">
            <el-card shadow="hover" class="history-item-card">
              <div class="history-item">
                <!-- 书籍封面 -->
                <div class="book-cover-container">
                  <el-image 
                    v-if="item.book.imageUrl" 
                    :src="item.book.imageUrl" 
                    class="book-cover" 
                    :alt="item.book.name"
                    fit="cover"
                  >
                    <template #error>
                      <div class="image-error">{{ item.book.name }}</div>
                    </template>
                  </el-image>
                  <div v-else class="book-cover book-cover-placeholder">{{ item.book.name }}</div>
                </div>
                
                <!-- 书籍信息 -->
                <div class="book-info">
                  <h4 class="book-name">{{ item.book.name }}</h4>
                  <p class="book-author">{{ item.book.author }}</p>
                  <p class="read-time">阅读时间: {{ formatDate(item.readAt) }}</p>
                  <div class="item-actions">
                    <el-button 
                      type="primary" 
                      size="small" 
                      @click="goToBookDetail(item.book.id)"
                    >
                      <el-icon><Document /></el-icon>
                      查看详情
                    </el-button>
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="handleDeleteItem(item.id)"
                    >
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'
import { ElMessage, ElLoading } from 'element-plus'
import { Delete, Document } from '@element-plus/icons-vue'
import { useUserStore } from '../store/userStore'
import { getCurrentUserId } from '../utils/userUtil'

const router = useRouter()
const userStore = useUserStore()

// 阅读历史数据
const readingHistory = ref([])

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取阅读历史记录
const getReadingHistory = async () => {
  if (!userStore.isLogin) {
    router.push('/login')
    return
  }
  
  const loading = ElLoading.service({
    lock: true,
    text: '加载中...',
    background: 'rgba(255, 255, 255, 0.7)'
  })
  
  try {
    // 确保获取最新的用户信息
    await refreshUserInfo()
    
    // 直接获取最新的用户ID，确保使用更新后的值
    const currentUserId = getCurrentUserId()
    console.log('刷新后用户ID:', currentUserId)
    const response = await axios.get('/api/history/user', {
      params: { userId: currentUserId }
    })
    
    console.log('阅读历史API响应:', response)
    if (response && response.success) {
      readingHistory.value = response.data || []
      console.log('阅读历史数据:', readingHistory.value)
    } else {
      console.error('获取阅读历史失败，API响应:', response)
      ElMessage.error('获取阅读历史失败')
    }
  } catch (error) {
    console.error('获取阅读历史失败:', error)
    console.error('错误详情:', error.response?.data)
    ElMessage.error('获取阅读历史失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.close()
  }
}

// 跳转到书籍详情
const goToBookDetail = (bookId) => {
  router.push(`/book/${bookId}`)
}

// 删除单条阅读记录
const handleDeleteItem = async (id) => {
  try {
    const response = await axios.delete(`/api/history/${id}`)
    if (response && response.success) {
      ElMessage.success('删除成功')
      // 重新加载阅读历史
      getReadingHistory()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    console.error('删除阅读记录失败:', error)
    ElMessage.error('删除阅读记录失败: ' + (error.response?.data?.message || error.message))
  }
}

// 清空所有阅读记录
const handleClearHistory = async () => {
  try {
    const response = await axios.delete('/api/history/clear', {
      params: { userId: getCurrentUserId() }
    })
    if (response && response.success) {
      ElMessage.success('清空阅读历史成功')
      readingHistory.value = []
    } else {
      ElMessage.error('清空阅读历史失败')
    }
  } catch (error) {
    console.error('清空阅读历史失败:', error)
    ElMessage.error('清空阅读历史失败: ' + (error.response?.data?.message || error.message))
  }
}

// 组件挂载时获取阅读历史
onMounted(async () => {
  if (userStore.isLogin) {
    // 刷新用户信息，确保ID与数据库一致
    await refreshUserInfo()
    await getReadingHistory()
  } else {
    router.push('/login')
  }
})

// 刷新用户信息
const refreshUserInfo = async () => {
  try {
    // 直接使用统一的用户ID获取函数
    const correctUserId = getCurrentUserId()
    console.log('刷新用户信息，正确的用户ID:', correctUserId)
    console.log('当前用户名:', userStore.userInfo.username)
    
    // 更新userStore和localStorage
    const updatedUserInfo = {
      ...userStore.userInfo,
      id: correctUserId
    }
    localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo))
    userStore.init()
    console.log('刷新用户信息成功，更新后的用户ID:', userStore.userInfo.id)
  } catch (error) {
    console.error('刷新用户信息失败:', error)
  }
}
</script>

<style scoped>
.reading-history {
  max-width: 1440px;
  margin: 0 auto;
  padding: 40px 24px;
  min-height: 100vh;
  animation: fadeIn 0.8s ease-out;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 40px;
  text-align: center;
  letter-spacing: -1px;
}

/* 顶部卡片美化 */
.history-card {
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-md);
  background: white;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32px;
  border-bottom: 1px solid var(--border-light);
  background: var(--bg-secondary);
}

.header-left h3 {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.history-count {
  font-size: 0.95rem;
  color: var(--text-secondary);
  font-weight: 500;
}

/* 记录网格 */
.history-list {
  padding: 32px;
}

.history-item-card {
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 24px;
}

.history-item-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary-light);
}

.book-cover-container {
  aspect-ratio: 3/4;
  overflow: hidden;
  border-radius: var(--radius-md) var(--radius-md) 0 0;
  background: var(--bg-secondary);
}

.book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.history-item-card:hover .book-cover {
  transform: scale(1.05);
}

.book-info {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.book-name {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.4;
  height: 3rem;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.book-author {
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.read-time {
  font-size: 0.8rem;
  color: var(--text-placeholder);
  margin-top: 4px;
}

.item-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.item-actions .el-button {
  flex: 1;
  border-radius: var(--radius-md);
  font-weight: 600;
}

/* 统一按钮美化 */
:deep(.el-button--primary) {
  background: var(--primary-gradient);
  border: none;
}

:deep(.el-button--danger) {
  border-radius: var(--radius-md);
}

/* 动画 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 640px) {
  .history-list {
    padding: 16px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
}
</style>