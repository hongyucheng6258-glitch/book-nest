<template>
  <div class="friends-management">
    <h3>好友管理</h3>
    
    <!-- 搜索和筛选 -->
    <div class="friends-header">
      <div class="search-filter">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名..."
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>
    
    <!-- 好友关系列表 -->
    <div class="friends-list">
      <div class="section-header">
        <h3>好友关系管理</h3>
        <el-button type="primary" size="small" @click="fetchFriends" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
      <el-table
        :data="friendsList"
        border
        stripe
        style="width: 100%"
        :loading="loading"
        v-loading="loading"
      >
        <el-table-column prop="id" label="关系ID" width="80" align="center" />
        <el-table-column prop="user1Username" label="用户1" width="150">
          <template #default="scope">
            <el-tag type="info" size="small" effect="plain">{{ scope.row.user1?.username || scope.row.username1 || '未知用户' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="user2Username" label="用户2" width="150">
          <template #default="scope">
            <el-tag type="info" size="small" effect="plain">{{ scope.row.user2?.username || scope.row.username2 || '未知用户' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '正常' : '已删除' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" align="center">
          <template #default="scope">
            {{ scope.row.createdAt ? new Date(scope.row.createdAt).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="180" align="center">
          <template #default="scope">
            {{ scope.row.updatedAt ? new Date(scope.row.updatedAt).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteFriend(scope.row)"
              v-if="scope.row.status === 1"
              :icon="Delete"
              circle
            >
            </el-button>
            <el-button
              type="primary"
              size="small"
              @click="handleRestoreFriend(scope.row)"
              v-else
              :icon="Refresh"
              circle
            >
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    
    <!-- 好友请求列表 -->
    <div class="friend-requests" style="margin-top: 30px;">
      <div class="section-header">
        <h3>好友请求管理</h3>
        <div class="header-actions">
          <el-input
            v-model="requestSearchKeyword"
            placeholder="搜索发送者或接收者..."
            clearable
            @input="handleRequestSearch"
            size="small"
            style="width: 300px; margin-right: 10px;"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" size="small" @click="fetchFriendRequests" :loading="requestsLoading">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
      <el-table
        :data="friendRequests"
        border
        stripe
        style="width: 100%"
        :loading="requestsLoading"
        v-loading="requestsLoading"
      >
        <el-table-column prop="id" label="请求ID" width="80" align="center" />
        <el-table-column prop="fromUsername" label="发送者" width="150">
          <template #default="scope">
            <el-tag type="primary" size="small" effect="plain">{{ scope.row.fromUser?.username || '未知用户' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="toUsername" label="接收者" width="150">
          <template #default="scope">
            <el-tag type="primary" size="small" effect="plain">{{ scope.row.toUser?.username || '未知用户' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 1 ? 'warning' : scope.row.status === 2 ? 'success' : 'danger'"
              size="small"
            >
              {{ getRequestStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" align="center">
          <template #default="scope">
            {{ scope.row.createdAt ? new Date(scope.row.createdAt).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="180" align="center">
          <template #default="scope">
            {{ scope.row.updatedAt ? new Date(scope.row.updatedAt).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button
          type="success"
          size="small"
          @click="handleProcessRequest(scope.row, 2)"
          v-if="scope.row.status === 1"
        >
          接受
        </el-button>
        <el-button
          type="warning"
          size="small"
          @click="handleProcessRequest(scope.row, 3)"
          v-if="scope.row.status === 1"
        >
          拒绝
        </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteRequest(scope.row)"
              :icon="Delete"
              circle
            >
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../../utils/axios'
import { ElMessage } from 'element-plus'
import { Search, Delete, Refresh } from '@element-plus/icons-vue'

// 原始好友列表数据
const originalFriendsList = ref([])
// 过滤后的好友列表数据
const friendsList = ref([])
// 原始好友请求列表数据
const originalFriendRequests = ref([])
// 过滤后的好友请求列表数据
const friendRequests = ref([])
// 搜索关键字
const searchKeyword = ref('')
// 好友请求搜索关键字
const requestSearchKeyword = ref('')
// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
// 加载状态
const loading = ref(false)
const requestsLoading = ref(false)

// 获取好友请求状态文本
const getRequestStatusText = (status) => {
  switch (status) {
    case 1: return '待处理'
    case 2: return '已接受'
    case 3: return '已拒绝'
    default: return '未知状态'
  }
}

// 过滤好友列表
const filterFriends = () => {
  if (!searchKeyword.value) {
    friendsList.value = originalFriendsList.value
  } else {
    const keyword = searchKeyword.value.toLowerCase()
    friendsList.value = originalFriendsList.value.filter(friend => {
      // 获取用户1和用户2的用户名
      const user1Username = friend.user1?.username || friend.username1 || ''
      const user2Username = friend.user2?.username || friend.username2 || ''
      // 检查用户名是否包含搜索关键字
      return user1Username.toLowerCase().includes(keyword) || 
             user2Username.toLowerCase().includes(keyword)
    })
  }
  // 更新分页总数
  total.value = friendsList.value.length
  // 重置页码
  currentPage.value = 1
}

// 过滤好友请求列表
const filterFriendRequests = () => {
  if (!requestSearchKeyword.value) {
    friendRequests.value = originalFriendRequests.value
  } else {
    const keyword = requestSearchKeyword.value.toLowerCase()
    friendRequests.value = originalFriendRequests.value.filter(request => {
      // 获取发送者和接收者的用户名
      const fromUsername = request.fromUser?.username || ''
      const toUsername = request.toUser?.username || ''
      // 检查用户名是否包含搜索关键字
      return fromUsername.toLowerCase().includes(keyword) || 
             toUsername.toLowerCase().includes(keyword)
    })
  }
}

// 搜索好友
const handleSearch = () => {
  filterFriends()
}

// 搜索好友请求
const handleRequestSearch = () => {
  filterFriendRequests()
}

// 获取好友列表
const fetchFriends = async () => {
  try {
    loading.value = true
    const response = await axios.get('/api/friend/all')
    if (response && response.success) {
      originalFriendsList.value = response.data || []
      // 过滤好友列表
      filterFriends()
    }
  } catch (error) {
    console.error('获取好友列表失败:', error)
    ElMessage.error('获取好友列表失败')
  } finally {
    loading.value = false
  }
}

// 获取好友请求列表
const fetchFriendRequests = async () => {
  try {
    requestsLoading.value = true
    const response = await axios.get('/api/friend/request/all')
    if (response && response.success) {
      originalFriendRequests.value = response.data || []
      // 过滤好友请求列表
      filterFriendRequests()
    }
  } catch (error) {
    console.error('获取好友请求列表失败:', error)
    ElMessage.error('获取好友请求列表失败')
  } finally {
    requestsLoading.value = false
  }
}

// 删除好友关系
const handleDeleteFriend = async (friend) => {
  try {
    const response = await axios.delete('/api/friend/delete', {
      params: {
        userId: friend.user1Id,
        friendId: friend.user2Id
      }
    })
    if (response && response.success) {
      ElMessage.success('删除好友关系成功')
      fetchFriends()
    }
  } catch (error) {
    console.error('删除好友关系失败:', error)
    ElMessage.error('删除好友关系失败')
  }
}

// 恢复好友关系
const handleRestoreFriend = async (friend) => {
  try {
    // 恢复好友关系需要重新创建好友记录
    const response = await axios.post('/api/friend/add', {
      user1Id: friend.user1Id,
      user2Id: friend.user2Id,
      status: 1
    })
    if (response && response.success) {
      ElMessage.success('恢复好友关系成功')
      fetchFriends()
    }
  } catch (error) {
    console.error('恢复好友关系失败:', error)
    ElMessage.error('恢复好友关系失败')
  }
}

// 处理好友请求
const handleProcessRequest = async (request, status) => {
  try {
    let response
    if (status === 2) {
      // 接受好友请求
      response = await axios.post(`/api/friend/request/accept/${request.id}`)
      ElMessage.success('接受好友请求成功')
    } else {
      // 拒绝好友请求
      response = await axios.post(`/api/friend/request/reject/${request.id}`)
      ElMessage.success('拒绝好友请求成功')
    }
    if (response && response.success) {
      fetchFriendRequests()
      fetchFriends()
    }
  } catch (error) {
    console.error('处理好友请求失败:', error)
    ElMessage.error('处理好友请求失败')
  }
}

// 删除好友请求
const handleDeleteRequest = async (request) => {
  try {
    const response = await axios.delete(`/api/friend/request/delete/${request.id}`)
    if (response && response.success) {
      ElMessage.success('删除好友请求成功')
      fetchFriendRequests()
    }
  } catch (error) {
    console.error('删除好友请求失败:', error)
    ElMessage.error('删除好友请求失败')
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchFriends()
}

// 当前页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current
  fetchFriends()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchFriends()
  fetchFriendRequests()
})
</script>

<style scoped>
.friends-management {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 80px);
}

/* 页面标题 */
.friends-management h3 {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 20px;
  font-weight: 600;
}

/* 搜索栏 */
.friends-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-filter {
  flex: 1;
  max-width: 400px;
}

/* 部分标题区域 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

/* 标题操作区域 */
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 好友列表区域 */
.friends-list {
  margin-bottom: 32px;
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 好友请求区域 */
.friend-requests {
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 分页 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header-wrapper) {
  background-color: #fafafa;
}

:deep(.el-table__header th) {
  background-color: #fafafa;
  font-weight: 600;
  color: #303133;
}

:deep(.el-table__body tr:hover > td) {
  background-color: #ecf5ff !important;
}

/* 操作按钮样式 */
:deep(.el-button--small) {
  margin-right: 8px;
}

/* 加载状态样式 */
:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.8);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .friends-management {
    padding: 10px;
  }
  
  .friends-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .search-filter {
    max-width: 100%;
    width: 100%;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .header-actions {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .header-actions .el-input {
    width: 100% !important;
  }
}
</style>