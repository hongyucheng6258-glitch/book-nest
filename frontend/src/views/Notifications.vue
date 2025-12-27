<template>
  <div class="notifications">
    <div class="notifications-header">
      <h2>我的通知</h2>
      <el-button 
        v-if="notifications.length > 0" 
        type="danger" 
        @click="clearAllNotifications"
        size="small"
      >
        清空所有
      </el-button>
    </div>
    
    <el-card shadow="hover" class="notifications-card">
      <div v-if="notifications.length > 0" class="notifications-list">
        <el-card 
          v-for="notification in notifications" 
          :key="notification.id" 
          shadow="hover" 
          class="notification-item"
          :class="{ 'read': notification.isRead === 1 }"
        >
          <div class="notification-content">
            <div class="notification-title">{{ notification.title }}</div>
            <div class="notification-message">{{ notification.content }}</div>
            <div class="notification-footer">
              <span class="notification-time">{{ formatTime(notification.createdAt) }}</span>
              <div class="notification-actions">
                <el-button 
                  v-if="notification.isRead === 0" 
                  type="primary" 
                  size="small" 
                  @click="markAsRead(notification.id)"
                >
                  标记已读
                </el-button>
                <el-button type="danger" size="small" @click="deleteNotification(notification.id)">
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </div>
      
      <div v-else class="empty-state">
        <el-empty description="暂无通知" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../store/userStore'
import axios from '../utils/axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const notifications = ref([])

// 获取通知列表
const getNotifications = async () => {
  try {
    const result = await axios.get('/api/notification/user', {
      params: { userId: userStore.userInfo.id }
    })
    if (result && result.success) {
      notifications.value = result.data || []
    }
  } catch (error) {
    console.error('获取通知列表失败:', error)
    ElMessage.error('获取通知失败')
  }
}

// 标记通知为已读
const markAsRead = async (notificationId) => {
  try {
    const result = await axios.put(`/api/notification/read/${notificationId}`)
    if (result && result.success) {
      // 更新本地通知状态
      const notification = notifications.value.find(n => n.id === notificationId)
      if (notification) {
        notification.isRead = 1
      }
      ElMessage.success('已标记为已读')
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除通知
const deleteNotification = async (notificationId) => {
  try {
    const result = await axios.delete(`/api/notification/delete/${notificationId}`)
    if (result && result.success) {
      // 从本地列表中删除
      notifications.value = notifications.value.filter(n => n.id !== notificationId)
      ElMessage.success('删除成功')
    }
  } catch (error) {
    console.error('删除通知失败:', error)
    ElMessage.error('操作失败')
  }
}

// 清空所有通知
const clearAllNotifications = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有通知吗？此操作不可撤销。',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        roundButton: true
      }
    )
    
    const result = await axios.delete('/api/notification/clear', {
        params: { userId: userStore.userInfo.id }
      })
        if (result && result.success) {
          notifications.value = []
          ElMessage.success('清空成功')
        }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('清空通知失败:', error)
        ElMessage.error('操作失败')
      }
    }
}

// 格式化时间
const formatTime = (timeString) => {
  const date = new Date(timeString)
  const now = new Date()
  const diff = now - date
  
  // 计算时间差（分钟、小时、天）
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 30) {
    return `${days}天前`
  } else {
    // 超过30天显示具体日期
    return date.toLocaleDateString()
  }
}

// 组件挂载时获取通知列表
onMounted(() => {
  getNotifications()
})
</script>

<style scoped>
.notifications {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 24px;
  min-height: 100vh;
  animation: fadeIn 0.8s ease-out;
}

.notifications-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.notifications-header h2 {
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -1px;
}

.notifications-card {
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-md);
  background: white;
  overflow: hidden;
}

.notifications-list {
  padding: 32px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.notification-item {
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.notification-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: var(--primary-gradient);
}

.notification-item.read::before {
  background: var(--border-light);
}

.notification-item:hover {
  transform: translateX(4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary-light);
}

.notification-item.read {
  opacity: 0.8;
  background: var(--bg-secondary);
}

.notification-content {
  padding: 24px;
}

.notification-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.notification-message {
  font-size: 1rem;
  color: var(--text-regular);
  margin-bottom: 24px;
  line-height: 1.6;
}

.notification-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification-time {
  font-size: 0.85rem;
  color: var(--text-placeholder);
  font-weight: 500;
}

.notification-actions {
  display: flex;
  gap: 12px;
}

:deep(.el-button) {
  border-radius: var(--radius-md);
  font-weight: 600;
  padding: 8px 16px;
}

:deep(.el-button--primary) {
  background: var(--primary-gradient);
  border: none;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
  .notifications-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .notification-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .notification-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>