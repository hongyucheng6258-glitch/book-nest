<template>
  <div class="chat-window-component" v-if="selectedFriend">
    <!-- 聊天头部 -->
    <div class="chat-header">
      <div class="friend-info">
        <div class="friend-avatar">
          <el-avatar size="large" :src="selectedFriend.friendAvatar">{{ (selectedFriend.friendUsername || '未知用户').charAt(0) }}</el-avatar>
          <span class="online-indicator" :class="{ 'online': selectedFriend.isOnline }"></span>
        </div>
        <div class="friend-name">{{ selectedFriend.friendUsername || '未知用户' }}</div>
      </div>
      <div class="chat-actions">
        <el-button type="text" @click="showDeleteConfirm = true">删除对话</el-button>
      </div>
    </div>
    
    <!-- 聊天内容 -->
    <div class="chat-content" ref="chatContentRef">
      <!-- 聊天消息列表 -->
      <div class="messages">
        <div 
          v-for="message in messages" 
          :key="message.id"
          class="message-item"
          :class="{ 'sent': message.fromUserId === currentUserId, 'received': message.fromUserId !== currentUserId }"
        >
          <div class="message-content">
            <div class="message-text">{{ message.content }}</div>
            <div class="message-time">{{ formatTime(message.createdAt) }}</div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 聊天输入框 -->
    <div class="chat-input">
      <el-input
        v-model="inputMessage"
        type="textarea"
        :rows="2"
        placeholder="请输入消息..."
        maxlength="500"
        show-word-limit
        @keyup.enter.ctrl="handleSendMessage"
      ></el-input>
      <div class="input-actions">
        <el-button type="primary" @click="handleSendMessage">发送</el-button>
      </div>
    </div>
    
    <!-- 删除对话确认对话框 -->
    <el-dialog
      v-model="showDeleteConfirm"
      title="确认删除"
      width="300px"
    >
      <span>确定要删除与 {{ selectedFriend.friendUsername }} 的对话吗？</span>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showDeleteConfirm = false">取消</el-button>
          <el-button type="primary" @click="handleDeleteChat">删除</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
  
  <!-- 未选择好友时的提示 -->
  <div v-else class="no-selection">
    <el-empty description="请选择一个好友开始聊天" />
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'

// 组件属性
const props = defineProps({
  // 当前用户ID
  currentUserId: {
    type: [Number, null],
    default: null
  },
  // 选中的好友信息
  selectedFriend: {
    type: Object,
    default: null
  }
})

// 组件事件
const emit = defineEmits(['messages-read'])

// 响应式数据
const messages = ref([]) // 聊天消息列表
const inputMessage = ref('') // 输入的消息
const chatContentRef = ref(null) // 聊天内容容器引用
const showDeleteConfirm = ref(false) // 是否显示删除确认对话框
let refreshTimer = null // 定时刷新定时器

// 停止定时刷新聊天历史的定时器
const stopRefreshTimer = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

// 启动定时刷新聊天历史的定时器
const startRefreshTimer = () => {
  // 清除已有定时器，避免重复
  stopRefreshTimer()
  // 设置新的定时器，每3秒刷新一次聊天历史
  refreshTimer = setInterval(() => {
    if (props.selectedFriend && props.currentUserId) {
      loadChatHistory()
    }
  }, 3000)
}

// 监听选中的好友变化
watch(() => props.selectedFriend, (newFriend) => {
  if (newFriend) {
    loadChatHistory()
    // 启动定时刷新
    startRefreshTimer()
  } else {
    messages.value = []
    // 停止定时刷新
    stopRefreshTimer()
  }
}, { immediate: true })

// 监听组件挂载和卸载
onMounted(() => {
  if (props.selectedFriend) {
    startRefreshTimer()
  }
})

onUnmounted(() => {
  stopRefreshTimer()
})

// 加载聊天历史
const loadChatHistory = async () => {
  if (!props.selectedFriend || !props.currentUserId) return
  
  try {
    // 调用真实的API获取聊天历史
    const result = await axios.get('/api/chat/history', { 
      params: { 
        userId1: props.currentUserId, 
        userId2: props.selectedFriend.id, 
        limit: 50 
      } 
    })
    
    if (result && result.success) {
      // 将消息按时间正序排列
      messages.value = result.data || []
      messages.value.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
    } else {
      messages.value = []
    }
    
    // 滚动到底部
    await nextTick()
    scrollToBottom()
    
    // 加载聊天历史后，立即标记所有消息为已读
    await markMessagesAsRead()
    
    // 通知父组件消息已读，清空未读计数
    emit('messages-read', props.selectedFriend.id)
  } catch (error) {
    console.error('加载聊天历史失败:', error)
    ElMessage.error('加载聊天历史失败')
    messages.value = []
  }
}

// 发送消息
const handleSendMessage = async () => {
  if (!inputMessage.value.trim() || !props.selectedFriend || !props.currentUserId) return
  
  const content = inputMessage.value.trim()
  inputMessage.value = '' // 清空输入框
  
  // 创建临时消息对象
  const tempMessage = {
    id: Date.now(),
    fromUserId: props.currentUserId,
    toUserId: props.selectedFriend.id,
    content,
    createdAt: new Date().toISOString(),
    isRead: 0,
    isTemp: true
  }
  
  // 添加到消息列表
  messages.value.push(tempMessage)
  
  // 滚动到底部
  await nextTick()
  scrollToBottom()
  
  try {
    // 调用真实的API发送消息，不包含createdAt字段，后端会自动设置当前时间
    const messageToSend = {
      fromUserId: props.currentUserId,
      toUserId: props.selectedFriend.id,
      content,
      isRead: 0
    }
    
    const result = await axios.post('/api/chat/send', messageToSend)
    
    if (result && result.success) {
      // 发送成功，更新消息状态
      const messageIndex = messages.value.findIndex(msg => msg.id === tempMessage.id)
      if (messageIndex !== -1) {
        // 从API返回结果中获取真实的消息ID和创建时间
        // 由于当前API没有返回创建的消息，我们只需要标记消息为非临时即可
        messages.value[messageIndex].isTemp = false
        messages.value[messageIndex].isRead = 1
      }
      
      // 发送成功后不刷新聊天历史，避免重复加载
  // await loadChatHistory()
      
      // 标记所有消息为已读
      await markMessagesAsRead()
    } else {
      // 发送失败，移除临时消息
      messages.value = messages.value.filter(msg => msg.id !== tempMessage.id)
      ElMessage.error('发送消息失败')
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    console.error('错误详情:', error.response)
    // 发送失败，移除临时消息
    messages.value = messages.value.filter(msg => msg.id !== tempMessage.id)
    ElMessage.error('发送消息失败')
  }
}

// 标记消息为已读
const markMessagesAsRead = async () => {
  if (!props.currentUserId || !props.selectedFriend) return
  
  try {
    await axios.post('/api/chat/read', {
      fromUserId: props.selectedFriend.id,
      toUserId: props.currentUserId
    })
  } catch (error) {
    console.error('标记消息为已读失败:', error)
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (chatContentRef.value) {
    chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight
  }
}

// 格式化时间
const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleTimeString()
}

// 删除对话
const handleDeleteChat = async () => {
  showDeleteConfirm.value = false
  try {
    if (!props.currentUserId || !props.selectedFriend) return
    
    // 调用后端API删除两个用户之间的所有对话
    const result = await axios.delete('/api/chat/delete', {
      params: {
        userId1: props.currentUserId,
        userId2: props.selectedFriend.id
      }
    })
    
    if (result && result.success) {
      // 清空当前聊天记录
      messages.value = []
      ElMessage.success('对话已删除')
    } else {
      ElMessage.error('删除对话失败')
    }
  } catch (error) {
    console.error('删除对话失败:', error)
    ElMessage.error('删除对话失败')
  }
}
</script>

<style scoped>
.chat-window-component {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  background: rgba(255, 255, 255, 0.4);
  border-bottom: 1px solid var(--border-light);
}

.friend-info {
  display: flex;
  align-items: center;
}

.friend-avatar {
  position: relative;
  margin-right: 16px;
}

.online-indicator {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #94a3b8;
  border: 2px solid white;
}

.online-indicator.online {
  background-color: #10b981;
}

.friend-name {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-primary);
}

.chat-content {
  flex: 1;
  overflow-y: auto;
  padding: 32px;
  background: linear-gradient(to bottom, transparent, rgba(79, 70, 229, 0.05));
}

.messages {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.message-item {
  display: flex;
  width: 100%;
}

.message-item.sent {
  justify-content: flex-end;
}

.message-content {
  max-width: 65%;
  padding: 12px 20px;
  border-radius: 20px;
  box-shadow: var(--shadow-sm);
  position: relative;
}

.message-item.sent .message-content {
  background: var(--primary-gradient);
  color: white;
  border-bottom-right-radius: 4px;
}

.message-item.received .message-content {
  background: white;
  color: var(--text-primary);
  border-bottom-left-radius: 4px;
  border: 1px solid var(--border-light);
}

.message-text {
  font-size: 0.95rem;
  line-height: 1.6;
  word-break: break-all;
}

.message-time {
  font-size: 0.75rem;
  margin-top: 6px;
  opacity: 0.7;
}

.message-item.sent .message-time {
  text-align: right;
}

/* 输入区域 - 玻璃拟态 */
.chat-input {
  padding: 24px;
  background: rgba(255, 255, 255, 0.6);
  border-top: 1px solid var(--border-light);
}

:deep(.el-textarea__inner) {
  border-radius: var(--radius-lg);
  padding: 16px;
  border-color: var(--border-light);
  background: white;
  box-shadow: none !important;
  transition: all 0.3s;
}

:deep(.el-textarea__inner:focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px var(--primary-light) !important;
}

.input-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.input-actions .el-button {
  min-width: 100px;
  height: 44px;
  border-radius: 99px;
  font-weight: 700;
  background: var(--primary-gradient);
  border: none;
  box-shadow: var(--shadow-primary);
}

.no-selection {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--text-placeholder);
}
</style>