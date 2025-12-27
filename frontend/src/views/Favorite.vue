<template>
  <div class="favorite">
    <h2 class="page-title">我的收藏</h2>
    
    <!-- 统计信息 -->
    <div class="stats-bar" v-if="favorites.length > 0">
      <div class="stat-item">
        <span class="stat-label">全部收藏</span>
        <span class="stat-value">{{ total }}</span>
      </div>
    </div>

    <!-- 收藏列表 - 网格布局 -->
    <div class="favorite-grid" v-if="favorites.length > 0">
      <div v-for="item in favorites" :key="item.id" class="favorite-card">
        <template v-if="item.book">
          <div class="book-cover-wrapper" @click="goToBookDetail(item.book.id)">
            <img v-if="item.book.imageUrl" :src="item.book.imageUrl" class="book-cover" :alt="item.book.name">
            <div v-else class="book-cover-placeholder">{{ item.book.name }}</div>
            <div class="overlay">
              <el-button type="primary" circle @click.stop="goToBookDetail(item.book.id)">
                <el-icon><View /></el-icon>
              </el-button>
            </div>
          </div>
          
          <div class="book-info">
            <h3 class="book-name">{{ item.book.name }}</h3>
            <p class="book-author">{{ item.book.author }}</p>
            <div class="price-row">
              <span class="book-price">¥{{ item.book.price.toFixed(2) }}</span>
              <el-button link type="primary" @click="removeFavorite(item.book.id)" class="remove-btn">
                取消收藏
              </el-button>
            </div>
            <p class="favorite-time">{{ formatDate(item.createdAt) }}</p>
          </div>
        </template>
        
        <template v-else>
          <div class="book-cover-wrapper invalid">
            <div class="book-cover-placeholder">无效书籍</div>
          </div>
          <div class="book-info">
            <h3 class="book-name">书籍已下架</h3>
            <el-button type="danger" plain @click="removeFavorite(item.bookId)" size="small">
              删除无效收藏
            </el-button>
          </div>
        </template>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-else class="empty-state">
      <el-empty description="您的收藏夹还是空的哦" :image-size="200">
        <el-button type="primary" @click="router.push('/')" round>去书城逛逛</el-button>
      </el-empty>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-container" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/userStore'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 收藏列表
const favorites = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取收藏列表
const getFavoriteList = async () => {
  if (!userStore.isLogin || !userStore.userInfo?.id) {
    return
  }
  
  try {
    const result = await axios.get('/api/favorite/list', {
      params: {
        userId: userStore.userInfo.id
      }
    })
    if (result && result.success) {
      favorites.value = result.data
      total.value = result.data.length
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('获取收藏列表失败: ' + (error.response?.data?.message || error.message))
  }
}

// 跳转到书籍详情
const goToBookDetail = (bookId) => {
  router.push(`/book/${bookId}`)
}

// 取消收藏
const removeFavorite = async (bookId) => {
  try {
    if (!bookId) {
      ElMessage.warning('无效的书籍ID')
      return
    }
    
    const result = await axios.delete('/api/favorite/remove', {
      params: {
        userId: userStore.userInfo.id,
        bookId: bookId
      }
    })
    if (result && result.success) {
      // 重新获取收藏列表
      await getFavoriteList()
      ElMessage.success('取消收藏成功')
    }
  } catch (error) {
    console.error('取消收藏失败:', error)
    ElMessage.error('取消收藏失败: ' + (error.response?.data?.message || error.message))
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleString()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getFavoriteList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current
  getFavoriteList()
}

// 组件挂载时获取收藏列表
onMounted(() => {
  getFavoriteList()
})
</script>

<style scoped>
.favorite {
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

/* 统计条 */
.stats-bar {
  background: var(--bg-secondary);
  padding: 16px 32px;
  border-radius: var(--radius-lg);
  margin-bottom: 40px;
  display: flex;
  gap: 40px;
  border: 1px solid var(--border-light);
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 0.85rem;
  color: var(--text-secondary);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--primary-color);
}

/* 网格布局 */
.favorite-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 32px;
}

.favorite-card {
  background: white;
  border-radius: var(--radius-lg);
  overflow: hidden;
  border: 1px solid var(--border-light);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
}

.favorite-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-xl);
  border-color: var(--primary-light);
}

/* 封面区域 */
.book-cover-wrapper {
  position: relative;
  aspect-ratio: 3/4;
  overflow: hidden;
  cursor: pointer;
  background: var(--bg-secondary);
}

.book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.favorite-card:hover .book-cover {
  transform: scale(1.1);
}

.book-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  text-align: center;
  color: var(--text-placeholder);
  font-weight: 600;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.book-cover-wrapper:hover .overlay {
  opacity: 1;
}

/* 信息区域 */
.book-info {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.book-name {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.book-price {
  font-size: 1.25rem;
  font-weight: 800;
  color: var(--primary-color);
}

.remove-btn {
  font-size: 0.85rem;
  font-weight: 600;
}

.favorite-time {
  font-size: 0.75rem;
  color: var(--text-placeholder);
  margin-top: 12px;
}

/* 动画 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 分页 */
.pagination-container {
  margin-top: 60px;
  display: flex;
  justify-content: center;
}

@media (max-width: 640px) {
  .favorite-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 16px;
  }
}
</style>