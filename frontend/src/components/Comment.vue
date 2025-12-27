<template>
  <div class="comment-component">
    <!-- 评论表单 -->
    <div v-if="userId" class="comment-form">
      <h3 class="comment-title">发表评论</h3>
      <el-input
        v-model="commentContent"
        type="textarea"
        :rows="4"
        placeholder="请输入您的评论..."
        maxlength="500"
        show-word-limit
      ></el-input>
      <div class="form-actions">
        <el-button type="primary" @click="submitComment">发表评论</el-button>
      </div>
    </div>
    
    <!-- 用户信息对话框 -->
    <el-dialog
      v-model="userInfoDialogVisible"
      title="用户信息"
      width="500px"
      @close="closeUserInfoDialog"
    >
      <div v-if="selectedUser" class="user-info-content">
        <div class="user-avatar">
          <el-avatar :size="100" :src="selectedUser.avatar">
            {{ selectedUser.username ? selectedUser.username.charAt(0) : '用' }}
          </el-avatar>
        </div>
        <div class="user-details">
          <div class="user-info-item">
            <span class="label">用户名：</span>
            <span class="value">{{ selectedUser.username }}</span>
          </div>
          <div class="user-info-item">
            <span class="label">昵称：</span>
            <span class="value">{{ selectedUser.nickname || '暂无昵称' }}</span>
          </div>
          <div class="user-info-item">
            <span class="label">性别：</span>
            <span class="value">{{ getUserGenderText(selectedUser.gender) }}</span>
          </div>
          <div class="user-info-item">
            <span class="label">生日：</span>
            <span class="value">{{ selectedUser.birthday || '暂无生日' }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeUserInfoDialog">关闭</el-button>
          <el-button 
            type="primary" 
            @click="sendFriendRequest"
            v-if="selectedUser && selectedUser.id !== userId"
            :loading="sendingFriendRequest"
          >
            {{ isFriend ? '已成为好友' : '添加好友' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 评论列表 -->
    <div class="comment-list">
      <h3 class="comment-title">评论列表</h3>
      
      <!-- 评论数量和排序 -->
      <div class="comment-header-bar">
        <div class="comment-count">共 {{ commentCount }} 条评论</div>
        <div class="comment-sort">
          <el-select v-model="sortConfig" size="small" @change="handleSortChange">
            <el-option label="最新评论" value="createdAt:desc" />
            <el-option label="最早评论" value="createdAt:asc" />
            <el-option label="点赞最多" value="likeCount:desc" />
          </el-select>
        </div>
      </div>
      
      <!-- 评论项 -->
      <div v-if="comments.length > 0" class="comments">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <span class="comment-user" @click="showUserInfo(comment)">{{ comment.userName || comment.user?.username || '匿名用户' }}</span>
            <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
          <div class="comment-actions">
            <el-button type="text" @click="toggleLike(comment)" :icon="comment.isLiked ? 'StarFilled' : 'Star'" :class="{ 'liked': comment.isLiked }">
              {{ comment.likeCount || 0 }}
            </el-button>
            <el-button type="text" @click="replyToComment(comment)">回复</el-button>
            <el-button v-if="comment.userId === userId" type="text" @click="deleteComment(comment.id)">删除</el-button>
          </div>
          
          <!-- 回复表单 -->
          <div v-if="replyConfig.commentId === comment.id" class="reply-form">
            <el-input
              v-model="replyConfig.content"
              type="textarea"
              :rows="2"
              placeholder="请输入您的回复..."
              maxlength="300"
              show-word-limit
            ></el-input>
            <div class="reply-actions">
              <el-button size="small" @click="cancelReply">取消</el-button>
              <el-button size="small" type="primary" @click="submitReply">发表回复</el-button>
            </div>
          </div>
          
          <!-- 子评论列表 -->
          <div v-if="comment.replies && comment.replies.length > 0" class="replies">
            <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
              <div class="reply-header">
                <span class="reply-user" @click="showUserInfo(reply)">{{ reply.userName || reply.user?.username || '匿名用户' }}</span>
                <span class="reply-time">{{ formatDate(reply.createdAt) }}</span>
              </div>
              <div class="reply-content">{{ reply.content }}</div>
              <div class="reply-actions">
            <el-button type="text" @click="toggleLike(reply)" :icon="reply.isLiked ? 'StarFilled' : 'Star'" :class="{ 'liked': reply.isLiked }">
              {{ reply.likeCount || 0 }}
            </el-button>
            <el-button type="text" @click="replyToComment(reply)">回复</el-button>
            <el-button v-if="reply.userId === userId" type="text" @click="deleteComment(reply.id)">删除</el-button>
          </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-else class="empty-comments">
        <el-empty description="暂无评论" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'

// 组件属性
const props = defineProps({
  // 书籍ID
  bookId: {
    type: Number,
    required: true
  },
  // 当前用户ID
  userId: {
    type: Number,
    default: 0
  }
})

// 组件事件
const emit = defineEmits(['comment-added', 'comment-deleted'])

// 响应式数据
const comments = ref([]) // 评论列表
const commentContent = ref('') // 评论内容
const commentCount = ref(0) // 评论数量
const isLoading = ref(false) // 是否正在加载评论
const isSubmitting = ref(false) // 是否正在提交评论
const sortConfig = ref('createdAt:desc') // 排序配置
const replyConfig = ref({ // 回复配置
  commentId: null,
  content: ''
})
// 用户信息相关
const userInfoDialogVisible = ref(false) // 用户信息对话框显示状态
const selectedUser = ref(null) // 选中的用户信息
const sendingFriendRequest = ref(false) // 发送好友请求加载状态
const isFriend = ref(false) // 是否已经是好友

// 解析排序配置
const parsedSortConfig = computed(() => {
  const [sortBy, order] = sortConfig.value.split(':')
  return { sortBy, order }
})

// 获取评论列表
const fetchComments = async () => {
  if (!props.bookId) return
  
  isLoading.value = true
  try {
    const { sortBy, order } = parsedSortConfig.value
    // 使用排序API获取评论列表
    const result = await axios.get(`/api/comment/sort/${props.bookId}`, {
      params: { sortBy, order }
    })
    if (result && result.success) {
      const fetchedComments = result.data || []
      // 为每个评论获取子评论和点赞状态
      await Promise.all(fetchedComments.map(async comment => {
        // 获取子评论
        await fetchReplies(comment)
        // 获取点赞状态
        await checkLikeStatus(comment)
      }))
      comments.value = fetchedComments
      commentCount.value = fetchedComments.length
    }
  } catch (error) {
    console.error('获取评论列表失败:', error)
    ElMessage.error('获取评论列表失败')
  } finally {
    isLoading.value = false
  }
}

// 获取子评论
const fetchReplies = async (comment) => {
  try {
    // 获取子评论
    const result = await axios.get(`/api/comment/replies/${comment.id}`)
    if (result && result.success) {
      const replies = result.data || []
      // 为每个回复获取点赞状态
      await Promise.all(replies.map(async reply => {
        await checkLikeStatus(reply)
      }))
      comment.replies = replies
    } else {
      comment.replies = []
    }
  } catch (error) {
    console.error('获取子评论失败:', error)
    comment.replies = []
  }
}

// 检查点赞状态
const checkLikeStatus = async (comment) => {
  if (!props.userId) {
    comment.isLiked = false
    return
  }
  
  try {
    // 获取点赞状态
    const result = await axios.get(`/api/comment/isLiked`, {
      params: { commentId: comment.id, userId: props.userId }
    })
    if (result && result.success) {
      comment.isLiked = result.data
    } else {
      comment.isLiked = false
    }
  } catch (error) {
    console.error('检查点赞状态失败:', error)
    comment.isLiked = false
  }
}

// 切换点赞状态
const toggleLike = async (comment) => {
  if (!props.userId) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    let result
    if (comment.isLiked) {
      // 取消点赞
      result = await axios.post(`/api/comment/unlike`, null, {
        params: {
          commentId: comment.id,
          userId: props.userId
        }
      })
    } else {
      // 点赞
      result = await axios.post(`/api/comment/like`, null, {
        params: {
          commentId: comment.id,
          userId: props.userId
        }
      })
    }
    
    if (result && result.success) {
      // 更新本地点赞状态和数量
      comment.isLiked = !comment.isLiked
      comment.likeCount = comment.isLiked ? (comment.likeCount || 0) + 1 : Math.max(0, (comment.likeCount || 0) - 1)
      ElMessage.success(comment.isLiked ? '点赞成功' : '取消点赞成功')
      // 重新获取评论列表以确保数据一致性
      await fetchComments()
    }
  } catch (error) {
    console.error('切换点赞状态失败:', error)
    ElMessage.error('操作失败')
  }
}

// 提交评论
const submitComment = async () => {
  if (!props.userId) {
    ElMessage.warning('请先登录')
    return
  }
  
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  isSubmitting.value = true
  try {
    const comment = {
      userId: props.userId,
      bookId: props.bookId,
      content: commentContent.value.trim(),
      parentId: null
    }
    
    const result = await axios.post('/api/comment/add', comment)
    if (result && result.success) {
      ElMessage.success('评论发表成功')
      commentContent.value = ''
      // 重新获取评论列表
      await fetchComments()
      // 触发评论添加事件
      emit('comment-added')
    } else {
      ElMessage.error('评论发表失败')
    }
  } catch (error) {
    console.error('提交评论失败:', error)
    ElMessage.error('评论发表失败')
  } finally {
    isSubmitting.value = false
  }
}

// 删除评论
const deleteComment = async (commentId) => {
  try {
    const result = await axios.delete(`/api/comment/${commentId}`, {
      params: { userId: props.userId }
    })
    if (result && result.success) {
      ElMessage.success('评论删除成功')
      // 重新获取评论列表
      await fetchComments()
      // 触发评论删除事件
      emit('comment-deleted')
    } else {
      ElMessage.error('评论删除失败')
    }
  } catch (error) {
    console.error('删除评论失败:', error)
    ElMessage.error('评论删除失败')
  }
}

// 回复评论
const replyToComment = (comment) => {
  replyConfig.value = {
    commentId: comment.id,
    content: ''
  }
}

// 取消回复
const cancelReply = () => {
  replyConfig.value = {
    commentId: null,
    content: ''
  }
}

// 提交回复
const submitReply = async () => {
  if (!props.userId) {
    ElMessage.warning('请先登录')
    return
  }
  
  if (!replyConfig.value.content.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  try {
    // 提交回复
    const comment = {
      userId: props.userId,
      bookId: props.bookId,
      content: replyConfig.value.content.trim(),
      parentId: replyConfig.value.commentId
    }
    
    const result = await axios.post('/api/comment/add', comment)
    if (result && result.success) {
      ElMessage.success('回复发表成功')
      cancelReply()
      // 重新获取评论列表
      await fetchComments()
      // 触发评论添加事件
      emit('comment-added')
    } else {
      ElMessage.error('回复发表失败')
    }
  } catch (error) {
    console.error('提交回复失败:', error)
    ElMessage.error('回复发表失败')
  }
}

// 处理排序变化
const handleSortChange = () => {
  fetchComments()
}

// 显示用户信息
const showUserInfo = async (commentOrReply) => {
  try {
    const userId = commentOrReply.userId
    if (!userId) return
    
    // 获取用户信息
    const response = await axios.get('/api/user/info', {
      params: { userId }
    })
    
    if (response && response.success) {
      selectedUser.value = response.data
      // 检查是否已经是好友
      await checkFriendship(userId)
      userInfoDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 关闭用户信息对话框
const closeUserInfoDialog = () => {
  userInfoDialogVisible.value = false
  selectedUser.value = null
  isFriend.value = false
}

// 获取性别文本
const getUserGenderText = (gender) => {
  switch (gender) {
    case 0:
      return '未知'
    case 1:
      return '男'
    case 2:
      return '女'
    default:
      return '未知'
  }
}

// 检查是否是好友
const checkFriendship = async (targetUserId) => {
  try {
    const response = await axios.get('/api/friend/check', {
      params: {
        userId1: props.userId,
        userId2: targetUserId
      }
    })
    if (response && response.success) {
      isFriend.value = response.data
    }
  } catch (error) {
    console.error('检查好友关系失败:', error)
    isFriend.value = false
  }
}

// 发送好友请求
const sendFriendRequest = async () => {
  if (!selectedUser.value) return
  await sendFriendRequestDirectly(selectedUser.value.userId)
}

// 直接发送好友请求
const sendFriendRequestDirectly = async (targetUserId) => {
  if (!props.userId) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    sendingFriendRequest.value = true
    const response = await axios.post('/api/friend/request/send', {
      fromUserId: props.userId,
      toUserId: targetUserId
    })
    
    if (response && response.success) {
      ElMessage.success('好友请求发送成功')
      // 检查是否已经是好友
      await checkFriendship(targetUserId)
    } else {
      ElMessage.error(response?.message || '好友请求发送失败')
    }
  } catch (error) {
    console.error('发送好友请求失败:', error)
    ElMessage.error('好友请求发送失败')
  } finally {
    sendingFriendRequest.value = false
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString()
}

// 组件挂载时获取评论列表
onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.comment-component {
  margin: 20px 0;
  background-color: #fafafa;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.comment-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #303133;
}

/* 评论表单 */
.comment-form {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #ebeef5;
}

.form-actions {
  margin-top: 16px;
  text-align: right;
}

/* 评论列表 */
.comment-list {
  margin-top: 32px;
}

.comment-header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.comment-count {
  font-size: 14px;
  color: #909399;
}

.comment-sort {
  font-size: 14px;
}

/* 评论项 */
.comments {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  background-color: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-user {
  font-weight: 600;
  color: #303133;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-content {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 8px;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  text-align: right;
  margin-top: 8px;
}

.comment-actions .liked {
  color: #f56c6c;
}

/* 回复表单 */
.reply-form {
  margin-top: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.reply-actions {
  margin-top: 8px;
  text-align: right;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 子评论列表 */
.replies {
  margin-top: 16px;
  margin-left: 40px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reply-item {
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.reply-user {
  font-weight: 500;
  color: #303133;
  font-size: 14px;
}

.reply-time {
  font-size: 11px;
  color: #909399;
}

.reply-content {
  color: #606266;
  line-height: 1.5;
  font-size: 14px;
  margin-bottom: 6px;
}

.reply-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  text-align: right;
  margin-top: 6px;
}

.reply-actions .liked {
  color: #f56c6c;
}

/* 空状态 */
.empty-comments {
  margin: 40px 0;
  text-align: center;
}

/* 用户信息对话框样式 */
.user-info-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.user-avatar {
  margin-bottom: 20px;
}

.user-details {
  width: 100%;
}

.user-info-item {
  display: flex;
  margin-bottom: 12px;
}

.user-info-item .label {
  width: 80px;
  font-weight: 500;
  color: #303133;
  text-align: right;
  margin-right: 16px;
}

.user-info-item .value {
  flex: 1;
  color: #606266;
}

/* 评论用户名称样式 */
.comment-user,
.reply-user {
  cursor: pointer;
  transition: color 0.2s;
}

.comment-user:hover,
.reply-user:hover {
  color: #409eff;
  text-decoration: underline;
}
</style>
