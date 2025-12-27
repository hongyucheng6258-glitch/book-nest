<template>
  <div class="admin-reviews">
    <div class="reviews-header">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange" size="large">
        <el-tab-pane label="评论管理" name="comments"></el-tab-pane>
        <el-tab-pane label="评分管理" name="ratings"></el-tab-pane>
      </el-tabs>
    </div>

    <!-- 评论管理 -->
    <div v-if="activeTab === 'comments'" class="comments-content">
      <el-card shadow="hover">
        <div class="card-header">
          <div class="header-left">
            <h4>评论列表</h4>
          </div>
          <div class="header-right">
            <el-input
              v-model="commentSearchKey"
              placeholder="请输入评论内容或用户名搜索"
              clearable
              @input="handleCommentSearch"
              style="width: 300px"
              size="large"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
        <div class="table-container">
          <el-table
            :data="filteredComments"
            style="width: 100%"
            border
            stripe
            size="small"
          >
            <el-table-column label="评论ID" width="80">
              <template #default="scope">
                {{ scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column prop="userId" label="用户ID" width="80"></el-table-column>
            <el-table-column prop="userName" label="用户名" width="120"></el-table-column>
            <el-table-column prop="bookId" label="书籍ID" width="80"></el-table-column>
            <el-table-column prop="bookName" label="书籍名称" min-width="150"></el-table-column>
            <el-table-column prop="content" label="评论内容" min-width="200">
              <template #default="scope">
                <el-popover
                  placement="top"
                  :width="300"
                  trigger="hover"
                >
                  <template #reference>
                    <span class="comment-content">{{ scope.row.content }}</span>
                  </template>
                  <div>{{ scope.row.content }}</div>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column prop="likeCount" label="点赞数量" width="120">
              <template #default="scope">
                {{ scope.row.likeCount || 0 }}
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="评论时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button
                  type="danger"
                  size="small"
                  @click="handleDeleteComment(scope.row.id)"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="commentCurrentPage"
            v-model:page-size="commentPageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalComments"
            @size-change="handleCommentSizeChange"
            @current-change="handleCommentCurrentChange"
          ></el-pagination>
        </div>
      </el-card>
    </div>

    <!-- 评分管理 -->
    <div v-else-if="activeTab === 'ratings'" class="ratings-content">
      <el-card shadow="hover">
        <div class="card-header">
          <div class="header-left">
            <h4>评分列表</h4>
          </div>
          <div class="header-right">
            <el-input
              v-model="ratingSearchKey"
              placeholder="请输入书籍名称或用户ID搜索"
              clearable
              @input="handleRatingSearch"
              style="width: 300px"
              size="large"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
        <div class="table-container">
          <el-table
            :data="filteredRatings"
            style="width: 100%"
            border
            stripe
            size="small"
          >
            <el-table-column label="评分ID" width="80">
              <template #default="scope">
                {{ scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column prop="userId" label="用户ID" width="80"></el-table-column>
            <el-table-column prop="userName" label="用户名" width="120"></el-table-column>
            <el-table-column prop="bookId" label="书籍ID" width="80"></el-table-column>
            <el-table-column prop="bookName" label="书籍名称" min-width="150"></el-table-column>
            <el-table-column label="评分" width="120">
              <template #default="scope">
                <el-rate
                  v-model="scope.row.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}"
                ></el-rate>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="评分时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button
                  type="danger"
                  size="small"
                  @click="handleDeleteRating(scope.row.id)"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="ratingCurrentPage"
            v-model:page-size="ratingPageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalRatings"
            @size-change="handleRatingSizeChange"
            @current-change="handleRatingCurrentChange"
          ></el-pagination>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from '../../utils/axios'
import { ElMessage, ElLoading } from 'element-plus'
import { Search, Delete } from '@element-plus/icons-vue'

// 活跃标签
const activeTab = ref('comments')

// 评论数据
const comments = ref([])
const filteredComments = ref([])
const commentSearchKey = ref('')
const commentCurrentPage = ref(1)
const commentPageSize = ref(10)
const totalComments = ref(0)

// 评分数据
const ratings = ref([])
const filteredRatings = ref([])
const ratingSearchKey = ref('')
const ratingCurrentPage = ref(1)
const ratingPageSize = ref(10)
const totalRatings = ref(0)

// 处理标签切换
const handleTabChange = (tab) => {
  activeTab.value = tab
  // 如果是第一次切换到评分标签，需要加载评分数据
  if (tab === 'ratings' && ratings.value.length === 0) {
    fetchRatings()
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取所有评论
const fetchComments = async () => {
  const loading = ElLoading.service({
    lock: true,
    text: '加载中...',
    background: 'rgba(255, 255, 255, 0.7)'
  })
  try {
    const response = await axios.get('/api/admin/review/comments')
    if (response && response.success) {
      comments.value = response.data || []
      totalComments.value = comments.value.length
      filteredComments.value = [...comments.value]
    }
  } catch (error) {
    console.error('获取评论列表失败:', error)
    ElMessage.error('获取评论列表失败')
  } finally {
    loading.close()
  }
}

// 获取所有评分
const fetchRatings = async () => {
  const loading = ElLoading.service({
    lock: true,
    text: '加载中...',
    background: 'rgba(255, 255, 255, 0.7)'
  })
  try {
    const response = await axios.get('/api/admin/review/ratings')
    if (response && response.success) {
      ratings.value = response.data || []
      totalRatings.value = ratings.value.length
      filteredRatings.value = [...ratings.value]
    }
  } catch (error) {
    console.error('获取评分列表失败:', error)
    ElMessage.error('获取评分列表失败')
  } finally {
    loading.close()
  }
}

// 删除评论
const handleDeleteComment = async (id) => {
  try {
    const response = await axios.delete(`/api/admin/review/comment/${id}`)
    if (response && response.success) {
      ElMessage.success('删除评论成功')
      // 重新加载评论数据
      fetchComments()
    } else {
      ElMessage.error('删除评论失败')
    }
  } catch (error) {
    console.error('删除评论失败:', error)
    ElMessage.error('删除评论失败')
  }
}

// 删除评分
const handleDeleteRating = async (id) => {
  try {
    const response = await axios.delete(`/api/admin/review/rating/${id}`)
    if (response && response.success) {
      ElMessage.success('删除评分成功')
      // 重新加载评分数据
      fetchRatings()
    } else {
      ElMessage.error('删除评分失败')
    }
  } catch (error) {
    console.error('删除评分失败:', error)
    ElMessage.error('删除评分失败')
  }
}

// 评论搜索
const handleCommentSearch = () => {
  const searchKey = commentSearchKey.value.toLowerCase()
  filteredComments.value = comments.value.filter(comment => {
    return (
      comment.userName?.toLowerCase().includes(searchKey) ||
      comment.bookName?.toLowerCase().includes(searchKey) ||
      comment.content?.toLowerCase().includes(searchKey)
    )
  })
  totalComments.value = filteredComments.value.length
  commentCurrentPage.value = 1
}

// 评分搜索
const handleRatingSearch = () => {
  const searchKey = ratingSearchKey.value.toLowerCase()
  filteredRatings.value = ratings.value.filter(rating => {
    return (
      rating.userName?.toLowerCase().includes(searchKey) ||
      rating.bookName?.toLowerCase().includes(searchKey) ||
      rating.userId?.toString().includes(searchKey) ||
      rating.bookId?.toString().includes(searchKey)
    )
  })
  totalRatings.value = filteredRatings.value.length
  ratingCurrentPage.value = 1
}

// 评论分页大小变化
const handleCommentSizeChange = (size) => {
  commentPageSize.value = size
  commentCurrentPage.value = 1
}

// 评论当前页变化
const handleCommentCurrentChange = (currentPage) => {
  commentCurrentPage.value = currentPage
}

// 评分分页大小变化
const handleRatingSizeChange = (size) => {
  ratingPageSize.value = size
  ratingCurrentPage.value = 1
}

// 评分当前页变化
const handleRatingCurrentChange = (currentPage) => {
  ratingCurrentPage.value = currentPage
}

// 页面加载时获取评论数据
onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.admin-reviews {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 0;
}

.reviews-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e2e8f0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e2e8f0;
}

.header-left {
  flex: 1;
}

.header-right {
  flex-shrink: 0;
}

.card-header h4 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.search-box {
  display: flex;
  gap: 10px;
}

.table-container {
  overflow-x: auto;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.table-container :deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

.table-container :deep(.el-table__header-wrapper .el-table__header) {
  background-color: #f8fafc;
}

.table-container :deep(.el-table__header-wrapper th) {
  background-color: #f8fafc;
  font-weight: 600;
  color: #334155;
  border-bottom: 2px solid #e2e8f0;
}

.table-container :deep(.el-table__body-wrapper tr:hover) {
  background-color: #f0f9ff;
}

.table-container :deep(.el-table__body-wrapper tr.el-table__row--striped) {
  background-color: #fafafa;
}

.table-container :deep(.el-table__body-wrapper tr.el-table__row--striped:hover) {
  background-color: #f0f9ff;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #e2e8f0;
}

.comment-content {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  max-height: 40px;
  color: #475569;
  font-size: 14px;
}

.comment-content:hover {
  color: #1e40af;
}

:deep(.el-button--danger) {
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 6px;
}

:deep(.el-button--danger:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.3);
}

:deep(.el-input) {
  border-radius: 8px;
  border-color: #e2e8f0;
  transition: all 0.3s ease;
}

:deep(.el-input:focus-within) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

:deep(.el-tabs__item) {
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
}

:deep(.el-tabs__item.is-active) {
  color: #3b82f6;
}

:deep(.el-tabs__active-bar) {
  background-color: #3b82f6;
}

:deep(.el-card) {
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
}

:deep(.el-card:hover) {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  border-color: #cbd5e1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .header-right {
    width: 100%;
  }
  
  :deep(.el-input) {
    width: 100% !important;
  }
}
</style>