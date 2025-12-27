<template>
  <div class="category">
    <!-- 分类导航 -->
    <div class="category-nav">
      <el-tabs v-model="activeCategory" @tab-change="handleCategoryChange">
        <el-tab-pane 
          v-for="category in categories" 
          :key="category.id" 
          :label="category.name" 
          :name="category.id.toString()"
        ></el-tab-pane>
      </el-tabs>
    </div>
    
    <!-- 页面标题 -->
    <h2 class="page-title">{{ currentCategoryName }}</h2>
    
    <!-- 搜索和筛选 -->
    <div class="filter-bar">
      <!-- 搜索框 -->
      <div class="search-section">
        <el-input 
          v-model="keyword" 
          placeholder="搜索书籍" 
          @keyup.enter="handleSearch"
          class="search-input"
        >
          <template #append>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
      
      <!-- 排序和筛选 -->
      <div class="sort-filter">
        <!-- 排序 -->
        <div class="sort-section">
          <span class="filter-label">排序：</span>
          <el-radio-group v-model="sortBy" @change="handleSortChange">
            <el-radio-button label="default">默认</el-radio-button>
            <el-radio-button label="price_asc">价格↑</el-radio-button>
            <el-radio-button label="price_desc">价格↓</el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <el-skeleton :loading="loading" animated :rows="6">
      <!-- 书籍列表 -->
      <div class="book-list">
        <!-- 空状态 -->
        <el-empty 
          v-if="!loading && books.length === 0" 
          description="暂无书籍" 
          :image-size="200"
        >
          <el-button type="primary" @click="handleSearch">重新搜索</el-button>
        </el-empty>
        
        <!-- 书籍卡片 -->
        <div 
          class="book-item" 
          v-for="book in books" 
          :key="book.id"
        >
          <!-- 书籍封面 -->
          <div class="book-cover-container" @click="goToBookDetail(book.id)">
            <el-image 
              v-if="book.imageUrl" 
              :src="book.imageUrl" 
              class="book-cover" 
              :alt="book.name"
              fit="cover"
              placeholder="加载中..."
            >
              <template #error>
                <div class="image-error">{{ book.name }}</div>
              </template>
            </el-image>
            <div v-else class="book-cover book-cover-placeholder">{{ book.name }}</div>
            
            <!-- 快速操作按钮 -->
            <div class="book-quick-actions">
              <el-button 
                type="success" 
                size="small" 
                @click.stop="addToCart(book.id)"
                class="cart-btn"
              >
                <el-icon><ShoppingCart /></el-icon>
              </el-button>
              <el-button 
                :type="isFavorited(book.id) ? 'danger' : 'info'" 
                size="small" 
                @click.stop="toggleFavorite(book.id)"
                class="favorite-btn"
              >
                <el-icon v-if="isFavorited(book.id)"><StarFilled /></el-icon>
                <el-icon v-else><Star /></el-icon>
              </el-button>
            </div>
          </div>
          
          <!-- 书籍信息 -->
          <div class="book-info" @click="goToBookDetail(book.id)">
            <h3 class="book-name">{{ book.name }}</h3>
            <p class="book-author">{{ book.author }}</p>
            <p class="book-price">¥{{ book.price.toFixed(2) }}</p>
          </div>
          
          <!-- 操作按钮 -->
          <div class="book-actions">
            <el-button 
              type="primary" 
              size="small" 
              @click.stop="addToCart(book.id)"
              class="add-to-cart-btn"
            >
              加入购物车
            </el-button>
          </div>
        </div>
      </div>
    </el-skeleton>
    
    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[8, 18, 24]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination-component"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '../utils/axios'
import { useUserStore } from '../store/userStore'
import { useCartStore } from '../store/cartStore'
import { useFavoriteStore } from '../store/favoriteStore'
import { Search, ShoppingCart, Star, StarFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const favoriteStore = useFavoriteStore()

// 分类列表
const categories = ref([])

// 获取分类列表
const getCategories = async () => {
  try {
    const result = await axios.get('/api/category/all')
    if (result && result.success) {
      categories.value = result.data || []
    } else {
      categories.value = []
      ElMessage.warning('获取分类列表失败')
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    categories.value = []
    ElMessage.error('获取分类列表失败: ' + (error.response?.data?.message || error.message))
  }
}

// 当前激活的分类
const activeCategory = ref(Number(route.params.id || 1).toString())

// 当前分类ID
const categoryId = computed(() => Number(activeCategory.value))

// 当前分类名称
const currentCategoryName = computed(() => {
  const category = categories.value.find(c => c.id === categoryId.value)
  return category ? category.name : '全部'
})

// 搜索关键字
const keyword = ref(route.query.keyword || '')

// 排序方式
const sortBy = ref(route.query.sortBy || 'default')

// 书籍列表
const books = ref([])
const currentPage = ref(1)
const pageSize = ref(18)
const total = ref(0)
const loading = ref(false)

// 收藏状态
const favoritedBooks = ref(new Set())

// 获取书籍列表
const getBookList = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/book/query', {
      params: {
        currentPage: currentPage.value,
        pageSize: pageSize.value,
        categoryId: categoryId.value,
        keyword: keyword.value,
        sortBy: sortBy.value
      }
    })
    books.value = response.data?.records || []
    total.value = response.data?.totalRow || 0
    
    // 更新收藏状态
    if (userStore.isLogin) {
      await loadFavoriteStatus()
    }
  } catch (error) {
    console.error('获取书籍列表失败:', error)
    ElMessage.error('获取书籍列表失败')
    books.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 加载收藏状态
const loadFavoriteStatus = async () => {
  try {
    if (!userStore.isLogin || !userStore.userInfo?.id) {
      favoritedBooks.value = new Set()
      return
    }
    
    // 从收藏store获取收藏列表
    await favoriteStore.fetchFavoriteBooks(userStore.userInfo.id)
    // 更新收藏状态集合
    favoritedBooks.value = new Set(favoriteStore.favoriteBooks.map(item => item.bookId))
  } catch (error) {
    console.error('加载收藏状态失败:', error)
    favoritedBooks.value = new Set()
  }
}

// 检查是否已收藏
const isFavorited = (bookId) => {
  return favoritedBooks.value.has(bookId)
}

// 切换收藏状态
const toggleFavorite = async (bookId) => {
  if (!userStore.isLogin) {
    ElMessage.info('请先登录')
    router.push('/login')
    return
  }
  
  try {
    if (isFavorited(bookId)) {
      // 取消收藏
      await favoriteStore.removeFromFavorite(userStore.userInfo.id, bookId)
      favoritedBooks.value.delete(bookId)
      ElMessage.success('取消收藏成功')
    } else {
      // 添加收藏
      await favoriteStore.addToFavorite(userStore.userInfo.id, bookId)
      favoritedBooks.value.add(bookId)
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 分页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getBookList()
}

// 当前页改变
const handleCurrentChange = (current) => {
  currentPage.value = current
  getBookList()
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  getBookList()
  // 更新URL参数
  updateUrl()
}

// 处理分类切换
const handleCategoryChange = (value) => {
  activeCategory.value = value
  currentPage.value = 1
  getBookList()
  // 更新URL参数
  updateUrl()
}

// 处理排序改变
const handleSortChange = (value) => {
  sortBy.value = value
  currentPage.value = 1
  getBookList()
  // 更新URL参数
  updateUrl()
}

// 更新URL参数
const updateUrl = () => {
  const query = {}
  if (keyword.value) {
    query.keyword = keyword.value
  }
  if (sortBy.value !== 'default') {
    query.sortBy = sortBy.value
  }
  
  router.push({
    path: `/category/${categoryId.value}`,
    query
  })
}

// 跳转到书籍详情页
const goToBookDetail = (bookId) => {
  router.push(`/book/${bookId}`)
}

// 加入购物车
const addToCart = async (bookId) => {
  if (!userStore.isLogin) {
    ElMessage.info('请先登录')
    router.push('/login')
    return
  }
  
  if (!userStore.userInfo || !userStore.userInfo.id) {
    ElMessage.info('请先登录')
    router.push('/login')
    return
  }
  
  try {
    // 调用购物车store的addToCart方法并等待完成
    await cartStore.addToCart(userStore.userInfo.id, bookId, 1)
    // 提示用户添加成功
    ElMessage.success('添加到购物车成功')
  } catch (error) {
    console.error('加入购物车失败:', error)
    ElMessage.error('添加到购物车失败')
  }
}

// 监听登录状态变化
watch(() => userStore.isLogin, (newValue) => {
  if (newValue) {
    loadFavoriteStatus()
  } else {
    favoritedBooks.value = new Set()
  }
})

// 组件挂载时获取分类列表、书籍列表和收藏状态
onMounted(async () => {
  await getCategories()
  getBookList()
  if (userStore.isLogin) {
    loadFavoriteStatus()
  }
})
</script>

<style scoped>
.category {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 分类导航 */
.category-nav {
  margin-bottom: 24px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

/* 页面标题 */
.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 24px;
  text-align: center;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  margin-bottom: 24px;
  align-items: center;
  background-color: #ffffff;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 搜索部分 */
.search-section {
  flex: 1;
  min-width: 300px;
}

.search-input {
  width: 100%;
}

/* 排序筛选部分 */
.sort-filter {
  display: flex;
  gap: 24px;
  align-items: center;
}

.sort-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 16px;
  color: #606266;
  font-weight: 500;
}

/* 书籍列表 */
.book-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 24px;
  margin: 24px 0;
}

/* 响应式设计 - 针对6列布局优化 */
@media (min-width: 1200px) {
  .book-list {
    grid-template-columns: repeat(6, 1fr);
  }
}

@media (max-width: 1199px) and (min-width: 960px) {
  .book-list {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 959px) and (min-width: 768px) {
  .book-list {
    grid-template-columns: repeat(3, 1fr);
  }
}

/* 书籍卡片 */
.book-item {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
}

.book-item:hover {
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-5px);
}

/* 书籍封面容器 */
.book-cover-container {
  position: relative;
  overflow: hidden;
  border-radius: 12px 12px 0 0;
  height: 280px;
  aspect-ratio: 2/3;
}

/* 书籍封面 */
.book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.book-item:hover .book-cover {
  transform: scale(1.05);
}

/* 封面占位符 */
.book-cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  color: #909399;
  font-size: 18px;
  text-align: center;
  padding: 20px;
}

/* 图片错误处理 */
.image-error {
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #909399;
  font-size: 18px;
  text-align: center;
  padding: 20px;
}

/* 快速操作按钮 */
.book-quick-actions {
  position: absolute;
  bottom: 16px;
  right: 16px;
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.book-item:hover .book-quick-actions {
  opacity: 1;
}

.cart-btn, .favorite-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.cart-btn:hover, .favorite-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 书籍信息 */
.book-info {
  padding: 16px;
  flex: 1;
}

.book-name {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #303133;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.book-author {
  font-size: 14px;
  color: #606266;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.book-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: 700;
  line-height: 1;
}

/* 操作按钮 */
.book-actions {
  padding: 0 16px 16px 16px;
}

.add-to-cart-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.add-to-cart-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 分页 */
.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  background-color: #ffffff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.pagination-component {
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .book-list {
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    gap: 20px;
  }
  
  .book-cover-container {
    height: 320px;
  }
}

@media (max-width: 768px) {
  .category {
    padding: 10px;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .search-section {
    min-width: auto;
  }
  
  .sort-filter {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .sort-section {
    justify-content: space-between;
  }
  
  .book-list {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
    margin: 16px 0;
  }
  
  .book-cover-container {
    height: 280px;
  }
  
  .page-title {
    font-size: 24px;
    margin-bottom: 16px;
  }
}

@media (max-width: 480px) {
  .book-list {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .book-cover-container {
    height: 300px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .book-name {
    font-size: 18px;
  }
  
  .book-price {
    font-size: 22px;
  }
}
</style>