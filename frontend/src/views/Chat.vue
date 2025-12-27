<template>
  <div class="chat-page">
    <div class="chat-container">
      <!-- 好友列表 -->
      <div class="friend-list-section">
        <FriendList 
          ref="friendListRef"
          @select-friend="handleSelectFriend"
        />
      </div>
      
      <!-- 聊天窗口 -->
      <div class="chat-window-section">
        <ChatWindow 
          :currentUserId="currentUserId"
          :selectedFriend="selectedFriend"
          @messages-read="handleMessagesRead"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import FriendList from '../components/FriendList.vue'
import ChatWindow from '../components/ChatWindow.vue'
import { useUserStore } from '../store/userStore'
import { getCurrentUserId } from '../utils/userUtil'

// 从状态管理中获取当前用户ID
const userStore = useUserStore()
// 使用ref来存储当前用户ID，以便响应式更新
const currentUserId = ref(null)
// 选中的好友信息
const selectedFriend = ref(null)
// 好友列表组件引用
const friendListRef = ref(null)

// 处理选择好友
const handleSelectFriend = (friend) => {
  selectedFriend.value = friend
}

// 处理消息已读事件，清空对应好友的未读消息数
const handleMessagesRead = (friendId) => {
  console.log('处理消息已读事件，好友ID:', friendId)
  // 调用好友列表组件的clearUnreadCount方法
  if (friendListRef.value && typeof friendListRef.value.clearUnreadCount === 'function') {
    friendListRef.value.clearUnreadCount(friendId)
  }
}

// 组件挂载时检查当前用户ID并获取用户信息
onMounted(() => {
  if (!userStore.isLogin) {
    // 如果未登录，跳转到登录页
    window.location.href = '/login'
  } else {
    // 登录成功后，获取当前用户ID
    currentUserId.value = getCurrentUserId()
  }
})
</script>

<style scoped>
.chat-page {
  min-height: 100vh;
  padding: 40px 24px;
  animation: fadeIn 0.8s ease-out;
}

.chat-container {
  display: flex;
  gap: 24px;
  max-width: 1400px;
  margin: 0 auto;
  height: calc(100vh - 150px); /* Adjust based on header/footer */
  background: var(--glass-bg);
  backdrop-filter: blur(16px);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-xl);
  overflow: hidden;
}

.friend-list-section {
  width: 320px;
  flex-shrink: 0;
  height: 100%;
  border-right: 1px solid var(--border-light);
  background: rgba(255, 255, 255, 0.5);
}

.chat-window-section {
  flex: 1;
  height: 100%;
  min-width: 0;
  background: rgba(255, 255, 255, 0.3);
}

/* 动画 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .friend-list-section {
    width: 260px;
  }
}

@media (max-width: 768px) {
  .chat-page {
    padding: 12px;
  }
  
  .chat-container {
    flex-direction: column;
    height: calc(100vh - 100px);
    gap: 0;
  }
  
  .friend-list-section {
    width: 100%;
    height: 35%;
    border-right: none;
    border-bottom: 1px solid var(--border-light);
  }
  
  .chat-window-section {
    height: 65%;
  }
}
</style>