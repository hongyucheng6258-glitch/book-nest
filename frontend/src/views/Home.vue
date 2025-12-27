<template>
  <div class="home">
    <!-- 轮播图 -->
    <el-carousel :interval="5000" height="400px" arrow="hover" indicator-position="outside" trigger="click" :autoplay="true" style="margin-bottom: 30px;">
      <el-carousel-item v-for="(banner, index) in banners" :key="index">
        <div class="carousel-item">
          <img :src="banner.imageUrl" alt="banner" class="carousel-image">
          <div class="carousel-content">
            <h2>{{ banner.title }}</h2>
            <p>{{ banner.description }}</p>
            <el-button type="primary" size="large" @click="goToCategory(banner.categoryId || 0)">
              {{ banner.buttonText || '立即探索' }}
            </el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
    
    <!-- 分类导航 -->
    <div class="section">
      <h2 class="section-title">书籍分类</h2>
      <div class="category-nav">
        <div 
          class="category-item"
          v-for="category in categories" 
          :key="category.id" 
          @click="goToCategory(category.id)"
        >
          <div class="category-icon">
            <component :is="iconComponents[categoryIcons[category.id] || 'Menu']" />
          </div>
          <span class="category-name">{{ category.name }}</span>
        </div>
      </div>
    </div>
    
    <!-- 书籍列表 -->
    <div class="section">
      <div class="section-header">
        <h2 class="section-title">热门书籍</h2>
        <el-button type="text" @click="goToCategory(0)">查看全部</el-button>
      </div>
      <!-- 加载状态 -->
      <el-skeleton :loading="loading" animated :rows="6">
        <div class="book-list">
          <!-- 空状态 -->
          <el-empty 
            v-if="!loading && books.length === 0" 
            description="暂无书籍" 
            :image-size="200"
          >
            <el-button type="primary" @click="getBookList">重新加载</el-button>
          </el-empty>
          
          <!-- 书籍卡片 -->
          <div 
            class="book-item" 
            v-for="book in books" 
            :key="book.id" 
            @click="goToBookDetail(book.id)"
          >
            <div class="book-cover-container">
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
              
              <!-- 销量标签 -->
              <div class="book-badge" v-if="book.saleCount > 0">
                销量 {{ book.saleCount }}
              </div>
              
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
            <div class="book-info">
              <h3 class="book-name">{{ book.name }}</h3>
              <p class="book-author">{{ book.author }}</p>
              <div class="book-rating" v-if="book.rating">
                <el-rate :model-value="book.rating" disabled show-score text-color="#ff9900" score-template="{value}" />
              </div>
              <div class="book-price-row">
                <span class="book-price">¥{{ book.price }}</span>
                <span class="book-original-price" v-if="book.originalPrice">¥{{ book.originalPrice }}</span>
              </div>
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
          :page-sizes="[8, 16, 18, 24, 32]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'
import { useUserStore } from '../store/userStore'
import { useCartStore } from '../store/cartStore'
import {
  ShoppingCart,
  Star,
  StarFilled,
  Menu,
  Document,
  User
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useFavoriteStore } from '../store/favoriteStore'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const favoriteStore = useFavoriteStore()

// 轮播图数据
const banners = ref([
  {
    id: 1,
    title: '精选书籍推荐',
    description: '发现你喜爱的图书，享受阅读的乐趣',
    buttonText: '立即探索',
    categoryId: 0,
    imageUrl: 'https://img0.baidu.com/it/u=2519693015,3483221246&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=280'
  },
  {
    id: 2,
    title: '新书上架',
    description: '最新出版的书籍，抢先阅读',
    buttonText: '查看新书',
    categoryId: 0,
    imageUrl: 'https://img0.baidu.com/it/u=2065951972,1364990407&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=280'
  },
  {
    id: 3,
    title: '畅销书籍',
    description: '最受欢迎的书籍，不容错过',
    buttonText: '查看畅销',
    categoryId: 0,
    imageUrl: 'https://img0.baidu.com/it/u=417484742,1938326362&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=280'
  }
])

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

// 书籍列表
const books = ref([])
const currentPage = ref(1)
const pageSize = ref(18) // 3行6列 = 18本书
const total = ref(0)
const loading = ref(false)

// 收藏状态
const favoritedBooks = ref(new Set())

// 获取随机颜色用于轮播图
const getRandomColor = () => {
  const colors = ['409eff', '67c23a', 'e6a23c', 'f56c6c', '909399', '722ed1', 'fa8c16']
  return colors[Math.floor(Math.random() * colors.length)]
}

// 获取分类图标映射
const categoryIcons = {
  1: 'Menu',
  2: 'Document',
  3: 'Document',
  4: 'Document',
  5: 'Document',
  6: 'Document'
}

// 图标组件映射
const iconComponents = {
  Menu,
  Document
}

// 获取书籍列表
const getBookList = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/book/query', {
      params: {
        currentPage: currentPage.value,
        pageSize: pageSize.value,
        categoryId: 1, // 全部分类
        keyword: ''
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
    books.value = []
    total.value = 0
    ElMessage.error('获取书籍列表失败')
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

// 分页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  getBookList()
}

// 当前页改变
const handleCurrentChange = (current) => {
  currentPage.value = current
  getBookList()
}

// 跳转到分类页
const goToCategory = (categoryId) => {
  router.push(`/category/${categoryId}`)
}

// 跳转到书籍详情页
const goToBookDetail = (bookId) => {
  router.push(`/book/${bookId}`)
}

// 加入购物车
const addToCart = async (bookId) => {
  if (!userStore.isLogin) {
    // 未登录，跳转到登录页
    router.push('/login')
    return
  }
  
  if (!userStore.userInfo || !userStore.userInfo.id) {
    // 用户信息不完整，跳转到登录页
    router.push('/login')
    return
  }
  
  try {
    // 调用购物车store的addToCart方法并等待完成
    await cartStore.addToCart(userStore.userInfo.id, bookId)
    // 提示用户添加成功
    ElMessage.success('添加到购物车成功')
  } catch (error) {
    ElMessage.error('添加到购物车失败')
    console.error('添加到购物车失败:', error)
  }
}

// 加入/取消收藏
const toggleFavorite = async (bookId) => {
  if (!userStore.isLogin) {
    // 未登录，跳转到登录页
    ElMessage.info('请先登录')
    router.push('/login')
    return
  }
  
  if (!userStore.userInfo || !userStore.userInfo.id) {
    // 用户信息不完整，跳转到登录页
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
    ElMessage.error('操作失败，请重试')
    console.error('收藏操作失败:', error)
  }
}

// 组件挂载时获取分类列表、书籍列表和收藏状态
onMounted(async () => {
  await getCategories()
  getBookList()
  
  // 如果用户已登录，获取收藏列表
  if (userStore.isLogin && userStore.userInfo && userStore.userInfo.id) {
    await favoriteStore.fetchFavoriteBooks(userStore.userInfo.id)
  }
})
</script>

<style scoped>
/* 主容器 */
.home {
  width: 100%;
  animation: fadeIn 0.8s ease-out;
}

/* 轮播图区域 */
:deep(.el-carousel) {
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-xl);
  margin-bottom: 40px;
}

.carousel-item {
  position: relative;
  width: 100%;
  height: 100%;
  background: var(--primary-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

/* 动态背景装饰 */
.carousel-item::after {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 80%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0) 70%);
  transform: rotate(-15deg);
  pointer-events: none;
}

.carousel-content {
  position: relative;
  z-index: 10;
  text-align: center;
  color: white;
  max-width: 700px;
  padding: 0 24px;
}

.carousel-content h2 {
  font-size: 3.5rem;
  font-weight: 800;
  margin-bottom: 1.5rem;
  letter-spacing: -1px;
  text-shadow: 0 4px 12px rgba(0,0,0,0.2);
  background: linear-gradient(to right, #ffffff, #e0e7ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.carousel-content p {
  font-size: 1.25rem;
  margin-bottom: 2.5rem;
  opacity: 0.9;
  font-weight: 300;
  line-height: 1.6;
}

.carousel-content .el-button {
  height: 56px;
  padding: 0 48px;
  font-size: 1.125rem;
  border-radius: 9999px;
  background: white;
  color: var(--primary-color);
  border: none;
  font-weight: 600;
  box-shadow: 0 10px 25px -5px rgba(0,0,0,0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.carousel-content .el-button:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 20px 25px -5px rgba(0,0,0,0.2);
}

/* 章节通用样式 */
.section {
  margin-bottom: 48px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
  padding: 0 12px;
}

.section-title {
  font-size: 2rem;
  font-weight: 800;
  color: var(--text-primary);
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-title::after {
  content: '';
  display: block;
  flex: 1;
  height: 1px;
  background: var(--border-light);
  margin-left: 24px;
  width: 100px;
}

/* 分类导航 - 现代网格布局 */
.category-nav {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 20px;
  padding: 8px;
}

.category-item {
  background: white;
  border-radius: var(--radius-lg);
  padding: 32px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
}

.category-item:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-xl);
  border-color: var(--primary-light);
}

.category-icon {
  width: 64px;
  height: 64px;
  background: var(--bg-secondary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-color);
  font-size: 28px;
  transition: all 0.3s ease;
}

.category-item:hover .category-icon {
  background: var(--primary-color);
  color: white;
  transform: rotate(15deg) scale(1.1);
}

.category-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 1rem;
}

/* 书籍列表网格 */
.book-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 32px;
  padding: 12px;
}

/* 玻璃拟态书籍卡片 */
.book-item {
  background: white;
  border-radius: var(--radius-lg);
  overflow: hidden;
  position: relative;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border-light);
  display: flex;
  flex-direction: column;
}

.book-item:hover {
  transform: translateY(-12px);
  box-shadow: var(--shadow-xl);
  border-color: var(--primary-light);
}

.book-cover-container {
  aspect-ratio: 2/3;
  position: relative;
  overflow: hidden;
  background: var(--bg-secondary);
}

.book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.book-item:hover .book-cover {
  transform: scale(1.1);
}

/* 悬浮操作层 */
.book-quick-actions {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(to top, rgba(0,0,0,0.8), transparent);
  display: flex;
  justify-content: center;
  gap: 16px;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.3s ease;
}

.book-item:hover .book-quick-actions {
  opacity: 1;
  transform: translateY(0);
}

.cart-btn, .favorite-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: white;
  color: var(--text-primary);
  transition: all 0.2s;
  box-shadow: 0 4px 6px rgba(0,0,0,0.3);
}

.cart-btn:hover {
  background: var(--primary-color);
  color: white;
}

.favorite-btn:hover {
  background: var(--danger-color);
  color: white;
}

/* 书籍信息区 */
.book-info {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.book-name {
  font-size: 1.1rem;
  font-weight: 700;
  line-height: 1.4;
  color: var(--text-primary);
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-author {
  color: var(--text-secondary);
  font-size: 0.9rem;
  margin: 0;
}

.book-rating {
  margin-top: auto;
  padding-top: 8px;
}

.book-price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-top: 12px;
}

.book-price {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--primary-color);
  letter-spacing: -0.5px;
}

.book-price::before {
  content: '¥';
  font-size: 0.8em;
  margin-right: 2px;
}

.book-original-price {
  color: var(--text-placeholder);
  text-decoration: line-through;
  font-size: 0.9rem;
}

/* 按钮区域 */
.book-actions {
  padding: 0 20px 24px;
}

.add-to-cart-btn {
  width: 100%;
  height: 40px;
  border-radius: var(--radius-md);
  font-weight: 600;
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.3s ease;
}

.book-item:hover .add-to-cart-btn {
  opacity: 1;
  transform: translateY(0);
}

/* 分页容器美化 */
.pagination-container {
  margin-top: 60px;
  display: flex;
  justify-content: center;
}

/* 适配移动端 */
@media (max-width: 768px) {
  .carousel-item {
    height: 300px;
  }
  
  .carousel-content h2 {
    font-size: 2rem;
  }
  
  .book-list {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 16px;
  }
  
  .book-item:hover .add-to-cart-btn {
    opacity: 1;
    transform: translateY(0);
  }
  
  .add-to-cart-btn {
    opacity: 1;
    transform: none;
  }
}
</style>