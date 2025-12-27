<template>
  <div class="book-detail">
    <el-card shadow="hover" class="detail-card">
      <!-- 加载状态 -->
      <el-skeleton :loading="loading" animated>
        <div class="book-content">
          <!-- 书籍封面 -->
          <div class="cover-container">
            <el-image 
              v-if="book.imageUrl" 
              :src="book.imageUrl" 
              class="book-cover" 
              :alt="book.name"
              fit="cover"
              placeholder="加载中..."
              error="加载失败"
            >
              <template #error>
                <div class="image-error">{{ book.name }}</div>
              </template>
            </el-image>
            <div v-else class="book-cover book-cover-placeholder">{{ book.name }}</div>
          </div>
          
          <!-- 书籍信息 -->
          <div class="book-info">
            <h1 class="book-name">{{ book.name }}</h1>
            
            <div class="book-meta">
              <p class="book-author">作者：{{ book.author }}</p>
              <p class="book-publisher">出版社：{{ book.publisher }}</p>
              <p class="book-stock" :class="{'stock-out': book.stock <= 0}">
                库存：{{ book.stock <= 0 ? '已售罄' : book.stock }}
              </p>
              <p class="book-sold-count">
                已卖：{{ book.soldCount || 0 }}
              </p>
            </div>
            
            <!-- 书籍评分 -->
            <div class="book-rating-section">
              <star-rating
                :book-id="book.id"
                :user-id="getCurrentUserId() || 0"
                :readonly="!userStore.isLogin"
                @rating-change="handleRatingChange"
              />
            </div>
            
            <div class="book-price-section">
              <span class="price-label">售价：</span>
              <span class="book-price">¥{{ book.price.toFixed(2) }}</span>
            </div>
            
            <!-- 加入购物车 -->
            <div class="book-actions">
              <el-input-number 
                v-model="quantity" 
                :min="1" 
                :max="99" 
                class="quantity-input"
                :disabled="!userStore.isLogin"
              />
              <el-button 
                type="primary" 
                size="large" 
                @click="addToCart"
                :disabled="!userStore.isLogin"
                class="action-button"
              >
                <el-icon><ShoppingCart /></el-icon>
                加入购物车
              </el-button>
              <el-button 
                type="success" 
                size="large" 
                @click="buyNow"
                :disabled="!userStore.isLogin || book.stock <= 0"
                class="action-button"
              >
                <el-icon><Ticket /></el-icon>
                {{ book.stock <= 0 ? '已售罄' : '立即购买' }}
              </el-button>
              <el-button 
                :type="isFavorited ? 'danger' : 'info'" 
                size="large" 
                @click="toggleFavorite"
                :disabled="!userStore.isLogin"
                class="action-button"
              >
                <el-icon v-if="isFavorited"><StarFilled /></el-icon>
                <el-icon v-else><Star /></el-icon>
                {{ isFavorited ? '取消收藏' : '收藏' }}
              </el-button>
            </div>
            
            <!-- 用户提示 -->
            <el-alert 
              v-if="!userStore.isLogin" 
              title="提示" 
              type="info" 
              :closable="false"
              class="login-tip"
            >
              请先登录以享受完整功能
            </el-alert>
          </div>
        </div>
        
        <!-- 书籍详情 -->
        <div class="book-description">
          <h3 class="section-title">书籍详情</h3>
          <div class="description-content">
            {{ book.description || '暂无详细描述' }}
          </div>
        </div>
        
        <!-- 书籍评论 -->
        <div class="book-comments">
          <comment
            :book-id="book.id"
            :user-id="getCurrentUserId() || 0"
          />
        </div>
      </el-skeleton>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/userStore'
import { useCartStore } from '../store/cartStore'
import axios from '../utils/axios'
import { ShoppingCart, Ticket, Star, StarFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getCurrentUserId } from '../utils/userUtil'
// 导入评分和评论组件
import StarRating from '../components/StarRating.vue'
import Comment from '../components/Comment.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

// 书籍信息
const book = ref({
  id: 0,
  name: '',
  author: '',
  publisher: '',
  price: 0,
  stock: 0,
  imageUrl: '',
  description: ''
})

// 购买数量
const quantity = ref(1)

// 是否已收藏
const isFavorited = ref(false)

// 加载状态
const loading = ref(true)

// 获取书籍详情
const getBookDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const result = await axios.get(`/api/book/${id}`)
    // 检查响应结构，确保数据正确解析
    console.log('书籍详情API响应:', result)
    // 确保result存在且success为true
    if (result && result.success) {
      // 实际书籍数据在result.data中
      const bookData = result.data || { id: 0, name: '', author: '', publisher: '', price: 0 }
      console.log('书籍数据:', bookData)
      // 更新书籍信息，确保响应式更新
      book.value = {
        id: bookData.id,
        name: bookData.name,
        author: bookData.author,
        publisher: bookData.publisher,
        price: bookData.price,
        stock: bookData.stock || 0,
        soldCount: bookData.soldCount || 0,
        categoryId: bookData.category_id,
        imageUrl: bookData.imageUrl,
        description: bookData.description
      }
      
      // 检查是否已收藏
      await checkFavoriteStatus()
    } else {
      // 响应无效时使用默认值
      book.value = {
        id: 0,
        name: '',
        author: '',
        publisher: '',
        price: 0,
        stock: 0,
        soldCount: 0,
        categoryId: 0,
        imageUrl: '',
        description: ''
      }
      console.error('无效的API响应:', result)
      ElMessage.error('获取书籍详情失败')
    }
  } catch (error) {
    console.error('获取书籍详情失败:', error)
    // 出错时确保book有默认值
    book.value = {
      id: 0,
      name: '',
      author: '',
      publisher: '',
      price: 0,
      stock: 0,
      soldCount: 0,
      categoryId: 0,
      imageUrl: '',
      description: ''
    }
    ElMessage.error('获取书籍详情失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 检查是否已收藏
const checkFavoriteStatus = async () => {
  if (!userStore.isLogin || !userStore.userInfo?.id) {
    isFavorited.value = false
    return
  }
  
  try {
    const result = await axios.get('/api/favorite/check', {
      params: {
        userId: userStore.userInfo.id,
        bookId: book.value.id
      }
    })
    if (result && result.success) {
      isFavorited.value = result.data
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
    isFavorited.value = false
  }
}

// 切换收藏状态
const toggleFavorite = async () => {
  if (!userStore.isLogin) {
    ElMessage.info('请先登录')
    router.push('/login')
    return
  }
  
  try {
    if (isFavorited.value) {
      // 取消收藏
      const result = await axios.delete('/api/favorite/remove', {
        params: {
          userId: getCurrentUserId(),
          bookId: book.value.id
        }
      })
      if (result && result.success) {
        isFavorited.value = false
        ElMessage.success('取消收藏成功')
      }
    } else {
      // 添加收藏
      const result = await axios.post('/api/favorite/add', null, {
        params: {
          userId: getCurrentUserId(),
          bookId: book.value.id
        }
      })
      if (result && result.success) {
        isFavorited.value = true
        ElMessage.success('收藏成功')
      }
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
  }
}

// 加入购物车
const addToCart = async () => {
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
  
  await cartStore.addToCart(getCurrentUserId(), book.value.id, quantity.value)
  
  // 提示添加成功
  ElMessage.success('添加到购物车成功')
}

// 立即购买
const buyNow = async () => {
  if (!userStore.isLogin) {
    // 未登录，跳转到登录页
    ElMessage.info('请先登录')
    router.push('/login')
    return
  }
  
  // 检查库存
  if (book.value.stock <= 0) {
    ElMessage.warning('该书籍已售罄，无法购买')
    return
  }
  
  try {
    // 创建订单数据
      const orderData = {
        userId: getCurrentUserId(),
      totalPrice: book.value.price * quantity.value,
      status: 1, // 待发货
      orderItems: [{
        bookId: book.value.id,
        quantity: quantity.value,
        price: book.value.price
      }]
    }
    
    // 调用后端API创建订单
    const result = await axios.post('/api/order/create', orderData)
    
    if (result && result.success) {
      ElMessage.success('订单创建成功，待发货')
      // 跳转到订单列表页
      router.push('/order')
    } else {
      ElMessage.error('订单创建失败')
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('订单创建失败: ' + (error.response?.data?.message || error.message))
  }
}

// 处理评分变化
const handleRatingChange = async (rating) => {
  if (!userStore.isLogin) {
    ElMessage.info('请先登录')
    router.push('/login')
    return
  }
  
  try {
    // 创建评分数据
    const ratingData = {
      userId: getCurrentUserId(),
      bookId: book.value.id,
      rating: rating
    }
    
    // 调用后端API保存评分
    const result = await axios.post('/api/rating/add', ratingData)
    
    if (result && result.success) {
      ElMessage.success('评分成功')
    } else {
      ElMessage.error('评分失败')
    }
  } catch (error) {
    console.error('评分失败:', error)
    ElMessage.error('评分失败: ' + (error.response?.data?.message || error.message))
  }
}

// 保存阅读记录
const saveReadingHistory = async () => {
  if (userStore.isLogin && book.value.id) {
    try {
    const currentUserId = getCurrentUserId();
    console.log('开始保存阅读记录，用户ID:', currentUserId, '书籍ID:', book.value.id)
    const now = new Date();
    // 格式化日期为yyyy-MM-dd HH:mm:ss格式，后端LocalDateTime可以正确解析
    const formattedDate = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`;
    const historyData = {
      userId: currentUserId,
        bookId: book.value.id,
        readAt: formattedDate,
        lastPage: 0,
        readingProgress: 0
      }
      console.log('保存阅读记录数据:', historyData)
      const response = await axios.post('/api/history/add', historyData)
      console.log('保存阅读记录API响应:', response)
      console.log('阅读记录保存成功')
    } catch (error) {
      console.error('保存阅读记录失败:', error)
      console.error('错误详情:', error.response?.data)
    }
  }
}

// 组件挂载时获取书籍详情
onMounted(() => {
  getBookDetail()
  // 延迟保存阅读记录，确保书籍详情已加载
  setTimeout(saveReadingHistory, 500)
})
</script>

<style scoped>
.book-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px;
  min-height: 100vh;
  animation: fadeIn 0.8s ease-out;
}

.detail-card {
  background: white;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--border-light);
  overflow: hidden;
}

.book-content {
  display: flex;
  gap: 48px;
  padding: 48px;
  align-items: flex-start;
}

/* 封面容器 */
.cover-container {
  flex-shrink: 0;
  width: 320px;
}

.book-cover {
  width: 100%;
  aspect-ratio: 2/3;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.16);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.book-cover:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 48px rgba(0, 0, 0, 0.2);
}

.book-cover-placeholder {
  width: 100%;
  height: 100%;
  aspect-ratio: 2/3;
  display: flex;
  justify-content: center;
  align-items: center;
  background: var(--bg-secondary);
  color: var(--text-secondary);
  font-size: 1.2rem;
  border-radius: var(--radius-lg);
}

/* 书籍信息 */
.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.book-name {
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 24px;
  line-height: 1.2;
}

.book-meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 32px;
  padding-bottom: 32px;
  border-bottom: 1px solid var(--border-light);
}

.book-author, .book-publisher {
  font-size: 1.1rem;
  color: var(--text-regular);
}

.book-stock {
  font-size: 0.9rem;
  color: var(--text-secondary);
  display: inline-flex;
  align-items: center;
}

.book-stock.stock-out {
  color: var(--danger-color);
  font-weight: 600;
}

/* 价格与操作 */
.book-price-section {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 32px;
}

.price-label {
  font-size: 1rem;
  color: var(--text-secondary);
}

.book-price {
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--primary-color);
  letter-spacing: -1px;
}

.book-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
  margin-bottom: 32px;
}

.quantity-input {
  width: 140px;
}

/* 书籍详情与评论 */
.book-description, .book-comments {
  padding: 48px;
  border-top: 1px solid var(--border-light);
}

.book-description {
  background: var(--bg-secondary);
}

.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-title::before {
  content: '';
  display: block;
  width: 4px;
  height: 24px;
  background: var(--primary-color);
  border-radius: 2px;
}

.description-content {
  color: var(--text-regular);
  line-height: 1.8;
  font-size: 1.05rem;
}

/* 响应式 */
@media (max-width: 900px) {
  .book-content {
    flex-direction: column;
    padding: 24px;
  }
  
  .cover-container {
    width: 200px;
    margin: 0 auto;
  }
  
  .book-name {
    font-size: 1.8rem;
    text-align: center;
  }
}
</style>