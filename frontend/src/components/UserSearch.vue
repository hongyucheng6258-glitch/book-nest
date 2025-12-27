<template>
  <div class="user-search-component">
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户名..."
        clearable
        @input="handleSearch"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>
    
    <!-- 搜索结果 -->
    <div v-if="showResults" class="search-results">
      <div v-if="searchResults.length > 0" class="results-list">
        <div v-for="user in searchResults" :key="user.id" class="user-item">
          <div class="user-info">
            <div class="user-avatar">
              <el-avatar size="large">{{ user.username.charAt(0) }}</el-avatar>
            </div>
            <div class="user-name">{{ user.username }}</div>
          </div>
          <div class="user-actions">
            <el-button 
              type="primary" 
              size="small" 
              @click="handleSendRequest(user)"
              :disabled="user.isRequested"
            >
              {{ user.isRequested ? '已发送' : '添加好友' }}
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 搜索结果为空 -->
      <div v-else-if="searchKeyword" class="empty-results">
        <el-empty description="未找到用户" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'

// 组件属性
const props = defineProps({
  // 当前用户ID
  currentUserId: {
    type: [Number, null],
    default: null
  }
})

// 组件事件
const emit = defineEmits(['send-request'])

// 响应式数据
const searchKeyword = ref('') // 搜索关键字
const searchResults = ref([]) // 搜索结果
const showResults = ref(false) // 是否显示搜索结果
const isLoading = ref(false) // 是否正在搜索

// 搜索用户
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    searchResults.value = []
    showResults.value = false
    return
  }
  
  isLoading.value = true
  try {
    // 调用真实的API搜索用户
    const result = await axios.get('/api/auth/user/search', { params: { keyword: searchKeyword.value } })
    
    // 过滤掉当前用户自己，添加空值检查
    searchResults.value = (result.data || []).filter(user => 
      user && props.currentUserId !== null && user.id !== props.currentUserId
    )
    
    showResults.value = true
  } catch (error) {
    console.error('搜索用户失败:', error)
    ElMessage.error('搜索用户失败')
    searchResults.value = []
    showResults.value = true
  } finally {
    isLoading.value = false
  }
}

// 发送好友请求
const handleSendRequest = async (user) => {
  try {
    // 验证当前用户ID是否有效
    if (props.currentUserId === null) {
      console.error('发送好友请求失败: 当前用户ID无效')
      ElMessage.error('发送好友请求失败: 当前用户未登录')
      return
    }
    
    console.log('发送好友请求，当前用户ID:', props.currentUserId, '目标用户ID:', user.id)
    // 调用真实的API发送好友请求
    const response = await axios.post('/api/friend/request/send', {
      fromUserId: props.currentUserId,
      toUserId: user.id
    })
    
    console.log('好友请求发送响应:', response)
    
    if (response && response.success) {
      // 更新用户状态
      user.isRequested = true
      emit('send-request', {
        fromUserId: props.currentUserId,
        toUserId: user.id
      })
      ElMessage.success('好友请求已发送')
    } else {
      console.error('发送好友请求失败:', response.message)
      ElMessage.error(response.message || '发送好友请求失败')
    }
  } catch (error) {
    console.error('发送好友请求失败:', error)
    console.error('错误详情:', error.response)
    ElMessage.error('发送好友请求失败')
  }
}
</script>

<style scoped>
.user-search-component {
  padding: 20px 0;
}

.search-form {
  margin-bottom: 20px;
}

.search-results {
  max-height: 300px;
  overflow-y: auto;
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.user-item:hover {
  background-color: #ecf5ff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  flex-shrink: 0;
}

.user-name {
  font-weight: 500;
  color: #303133;
}

.empty-results {
  margin-top: 20px;
}
</style>