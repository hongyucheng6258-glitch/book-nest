<template>
  <div class="friend-list-component">
    <h3 class="title">好友列表</h3>
    
    <!-- 搜索框和添加好友按钮 -->
    <div class="header-actions">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索好友..."
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <el-button type="primary" size="small" @click="showAddFriendDialog = true">
        <el-icon><Plus /></el-icon>
        添加好友
      </el-button>
    </div>
    
    <!-- 好友请求 -->
    <div v-if="pendingRequests.length > 0" class="friend-requests">
      <h4 class="section-title">待处理请求</h4>
      <div class="requests-list">
        <div v-for="request in pendingRequests" :key="request.id" class="request-item">
          <div class="request-info">
            <div class="request-avatar">
              <el-avatar size="medium">
                {{ request.fromUser?.username?.charAt(0) || '用' }}
              </el-avatar>
            </div>
            <div class="request-name">{{ request.fromUser?.username || '未知用户' }}</div>
          </div>
          <div class="request-actions">
            <el-button type="success" size="small" @click="acceptFriendRequest(request.id)">
              接受
            </el-button>
            <el-button type="danger" size="small" @click="rejectFriendRequest(request.id)">
              拒绝
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 好友列表 -->
    <div class="friend-list">
      <h4 class="section-title">好友列表</h4>
      <div v-if="friends.length > 0" class="friends">
        <div
          v-for="friend in filteredFriends"
          :key="friend.id"
          class="friend-item"
          :class="{ 'active': selectedFriendId === friend.id }"
          @click="selectFriend(friend)"
        >
          <div class="friend-avatar">
            <el-avatar size="large" :src="friend.friendAvatar">
              {{ friend.friendUsername?.charAt(0) || '友' }}
            </el-avatar>
            <span class="online-indicator" :class="{ 'online': friend.isOnline }"></span>
          </div>
          <div class="friend-info">
            <div class="friend-name">{{ friend.friendNickname || '未知用户' }}</div>
            <div v-if="friend.lastMessage" class="last-message">{{ friend.lastMessage }}</div>
          </div>
          <div v-if="friend.unreadCount > 0" class="unread-count">{{ friend.unreadCount }}</div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-else class="empty-friends">
        <el-empty description="暂无好友" />
        <el-button type="primary" @click="showAddFriendDialog = true">添加好友</el-button>
      </div>
    </div>
    
    <!-- 添加好友对话框 -->
    <el-dialog
      v-model="showAddFriendDialog"
      title="添加好友"
      width="400px"
    >
      <UserSearch 
        :currentUserId="getCurrentUserId()" 
        @send-request="handleSendFriendRequest" 
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import UserSearch from './UserSearch.vue'
import { useUserStore } from '../store/userStore'
import { getCurrentUserId } from '../utils/userUtil'

// 组件事件
const emit = defineEmits(['select-friend', 'clear-unread'])

// 用户状态管理
const userStore = useUserStore()



// 响应式数据
const friends = ref([]) // 好友列表
const selectedFriendId = ref(null) // 选中的好友ID
const searchKeyword = ref('') // 搜索关键字
const showAddFriendDialog = ref(false) // 是否显示添加好友对话框
const pendingRequests = ref([]) // 待处理的好友请求

// 过滤后的好友列表
const filteredFriends = computed(() => {
  if (!searchKeyword.value) {
    return friends.value
  }
  const keyword = searchKeyword.value.toLowerCase()
  return friends.value.filter(friend => 
    friend.friendUsername?.toLowerCase().includes(keyword)
  )
})

// 获取好友列表
const fetchFriends = async () => {
  try {
    const currentUserId = getCurrentUserId()
    if (currentUserId === null) {
      console.log('当前用户ID无效，无法获取好友列表')
      friends.value = []
      return
    }
    
    const result = await axios.get(`/api/friend/list/${currentUserId}`)
    if (result && result.success) {
      const friendList = result.data || []
      // 日志：查看原始好友数据
      console.log('原始好友数据:', friendList)
      // 处理好友数据，添加显示字段
      friends.value = friendList.map(friend => {
          // 确定好友用户名
          const isUser1 = friend.user1Id === currentUserId
          let friendUsername = '未知用户'
          let friendAvatar = null
          let friendNickname = '未知用户'
          
          // 优先从关联对象获取用户名、昵称和头像
          if (isUser1) {
            if (friend.user2?.userInfo?.nickname) {
              friendNickname = friend.user2.userInfo.nickname
              friendUsername = friend.user2.username
            } else if (friend.user2?.username) {
              friendUsername = friend.user2.username
              friendNickname = friend.user2.username
            } 
            // 尝试从直接字段获取
            else if (friend.username2) {
              friendUsername = friend.username2
              friendNickname = friend.username2
            }
            // 从user2Id获取用户名
            else if (friend.user2Id) {
              friendUsername = `用户${friend.user2Id}`
              friendNickname = `用户${friend.user2Id}`
            }
            
            // 获取好友头像
            if (friend.user2?.userInfo?.avatar) {
              friendAvatar = friend.user2.userInfo.avatar
            } else if (friend.avatar2) {
              friendAvatar = friend.avatar2
            }
          } else {
            if (friend.user1?.userInfo?.nickname) {
              friendNickname = friend.user1.userInfo.nickname
              friendUsername = friend.user1.username
            } else if (friend.user1?.username) {
              friendUsername = friend.user1.username
              friendNickname = friend.user1.username
            } 
            // 尝试从直接字段获取
            else if (friend.username1) {
              friendUsername = friend.username1
              friendNickname = friend.username1
            }
            // 从user1Id获取用户名
            else if (friend.user1Id) {
              friendUsername = `用户${friend.user1Id}`
              friendNickname = `用户${friend.user1Id}`
            }
            
            // 获取好友头像
            if (friend.user1?.userInfo?.avatar) {
              friendAvatar = friend.user1.userInfo.avatar
            } else if (friend.avatar1) {
              friendAvatar = friend.avatar1
            }
          }
          
          return {
            ...friend,
            friendUsername,
            friendNickname,
            friendAvatar,
            isOnline: Math.random() > 0.5, // 模拟在线状态
            lastMessage: '最近一条消息...', // 模拟最近消息
            unreadCount: 0, // 初始未读消息数为0，后续通过API获取或事件更新
            id: isUser1 ? friend.user2Id : friend.user1Id // 修正：将id设为好友的用户id，而不是关系id
          }
      })
    }
  } catch (error) {
    console.error('获取好友列表失败:', error)
    // 只在控制台输出错误，不显示给用户
  }
}

// 获取待处理的好友请求
const fetchPendingRequests = async () => {
  try {
    const currentUserId = getCurrentUserId()
    if (currentUserId === null) {
      console.log('当前用户ID无效，无法获取好友请求')
      pendingRequests.value = []
      return
    }
    
    const result = await axios.get(`/api/friend/request/received/${currentUserId}`)
    if (result && result.success) {
      pendingRequests.value = result.data || []
    }
  } catch (error) {
    console.error('获取好友请求失败:', error)
    // 只在控制台输出错误，不显示给用户
  }
}

// 接受好友请求
const acceptFriendRequest = async (requestId) => {
  try {
    const result = await axios.post(`/api/friend/request/accept/${requestId}`)
    if (result && result.success) {
      ElMessage.success('已接受好友请求')
      // 刷新好友列表和请求列表
      fetchFriends()
      fetchPendingRequests()
    }
  } catch (error) {
    console.error('接受好友请求失败:', error)
    ElMessage.error('接受好友请求失败')
  }
}

// 清空好友的未读消息数
const clearUnreadCount = (friendId) => {
  console.log('清空好友未读消息数:', friendId)
  const friendIndex = friends.value.findIndex(friend => friend.id === friendId)
  if (friendIndex !== -1) {
    friends.value[friendIndex].unreadCount = 0
  }
}

// 监听来自父组件的清空未读消息事件
defineExpose({
  clearUnreadCount
})

// 拒绝好友请求
const rejectFriendRequest = async (requestId) => {
  try {
    const result = await axios.post(`/api/friend/request/reject/${requestId}`)
    if (result && result.success) {
      ElMessage.success('已拒绝好友请求')
      // 刷新请求列表
      fetchPendingRequests()
    }
  } catch (error) {
    console.error('拒绝好友请求失败:', error)
    ElMessage.error('拒绝好友请求失败')
  }
}

// 选择好友
const selectFriend = (friend) => {
  selectedFriendId.value = friend.id
  // 清空该好友的未读消息数
  clearUnreadCount(friend.id)
  // 通知父组件清空未读消息
  emit('clear-unread', friend.id)
  emit('select-friend', friend)
}

// 搜索好友
const handleSearch = () => {
  // 实时过滤好友列表
}

// 发送好友请求
const handleSendFriendRequest = (friendRequest) => {
  showAddFriendDialog.value = false
  ElMessage.success('好友请求已发送')
}

// 监听用户信息变化，当用户信息变化时，重新获取好友列表和好友请求
watch(() => userStore.userInfo, (newUserInfo) => {
  if (newUserInfo && newUserInfo.id) {
    console.log('userStore.userInfo变化，重新获取好友列表和请求，当前用户ID:', newUserInfo.id)
    fetchFriends()
    fetchPendingRequests()
  }
}, { immediate: true })

// 组件挂载时获取好友列表和请求
onMounted(async () => {
  // 延迟执行，确保userStore完全初始化
  await new Promise(resolve => setTimeout(resolve, 100))
  fetchFriends()
  fetchPendingRequests()
})
</script>

<style scoped>
.friend-list-component {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: transparent;
  padding: 24px;
}

.title {
  font-size: 1.5rem;
  font-weight: 800;
  margin-bottom: 24px;
  color: var(--text-primary);
  letter-spacing: -0.5px;
}

.section-title {
  font-size: 0.85rem;
  font-weight: 700;
  margin: 24px 0 12px;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* 搜索与添加好友 */
.header-actions {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

:deep(.el-input__wrapper) {
  border-radius: 99px;
  background: var(--bg-secondary);
  box-shadow: none;
  border: 1px solid var(--border-light);
  padding: 4px 16px;
}

.header-actions .el-button {
  width: 100%;
  border-radius: 99px;
  font-weight: 700;
  background: var(--primary-gradient);
  border: none;
  box-shadow: var(--shadow-sm);
}

/* 好友请求项 */
.request-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background-color: var(--primary-light);
  border-radius: var(--radius-md);
  margin-bottom: 12px;
}

.request-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.request-name {
  font-weight: 700;
  color: var(--primary-color);
}

/* 好友列表项 */
.friend-list {
  flex: 1;
  overflow-y: auto;
  margin: 0 -12px;
  padding: 0 12px;
}

.friend-item {
  display: flex;
  align-items: center;
  padding: 14px;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 4px;
  border: 1px solid transparent;
}

.friend-item:hover {
  background-color: var(--bg-secondary);
}

.friend-item.active {
  background-color: white;
  border-color: var(--primary-light);
  box-shadow: var(--shadow-md);
}

.friend-avatar {
  position: relative;
  margin-right: 16px;
  flex-shrink: 0;
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

.friend-info {
  flex: 1;
  overflow: hidden;
}

.friend-name {
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.last-message {
  font-size: 0.85rem;
  color: var(--text-placeholder);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.unread-count {
  background: var(--primary-color);
  color: white;
  font-size: 0.75rem;
  font-weight: 800;
  min-width: 20px;
  height: 20px;
  border-radius: 99px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
  box-shadow: var(--shadow-sm);
}

.empty-friends {
  padding-top: 40px;
}
</style>