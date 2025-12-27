<template>
  <div class="order-confirm">
    <h2 class="page-title">订单确认</h2>
    
    <!-- 订单确认内容 -->
    <div class="confirm-content">
      <!-- 订单商品列表 -->
      <el-card shadow="hover" class="order-info">
        <template #header>
          <div class="card-header">
            <h3 class="card-title">订单商品</h3>
            <span class="item-count">(共 {{ cartItems.length }} 件商品)</span>
          </div>
        </template>
        
        <!-- 空购物车状态 -->
        <el-empty 
          v-if="cartItems.length === 0" 
          description="购物车为空" 
          :image-size="200"
        >
          <el-button type="primary" @click="router.push('/cart')">去购物车</el-button>
        </el-empty>
        
        <!-- 商品列表 -->
        <div v-else>
          <el-table 
            :data="cartItems" 
            border 
            style="width: 100%"
            :show-header="true"
            stripe
            size="medium"
          >
            <el-table-column label="书籍信息" min-width="300">
              <template #default="scope">
                <div class="book-item">
                  <el-image 
                    v-if="scope.row.book.imageUrl" 
                    :src="scope.row.book.imageUrl" 
                    class="book-cover-small"
                    fit="cover"
                  >
                    <template #error>
                      <div class="image-error-small">{{ scope.row.book.name }}</div>
                    </template>
                  </el-image>
                  <div v-else class="book-cover-small book-cover-placeholder-small">{{ scope.row.book.name }}</div>
                  
                  <div class="book-item-info">
                    <h4 class="book-item-name">{{ scope.row.book.name }}</h4>
                    <p class="book-item-author">作者：{{ scope.row.book.author }}</p>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="单价" width="100" align="center">
              <template #default="scope">
                <span class="price">¥{{ scope.row.book.price.toFixed(2) }}</span>
              </template>
            </el-table-column>
            
            <el-table-column label="数量" width="100" align="center">
              <template #default="scope">
                <el-tag type="info" size="medium">{{ scope.row.quantity }}</el-tag>
              </template>
            </el-table-column>
            
            <el-table-column label="小计" width="120" align="center">
              <template #default="scope">
                <span class="subtotal">¥{{ (scope.row.book.price * scope.row.quantity).toFixed(2) }}</span>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="order-summary">
            <div class="total">
              <span class="total-label">总计：</span>
              <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
            </div>
            <el-button 
              type="primary" 
              size="large" 
              @click="submitOrder"
              :loading="submitting"
              class="submit-button"
            >
              提交订单
            </el-button>
          </div>
        </div>
      </el-card>
      
      <!-- 订单备注 -->
      <el-card shadow="hover" class="order-note">
        <template #header>
          <div class="card-header">
            <h3 class="card-title">订单备注</h3>
          </div>
        </template>
        <el-input
          v-model="orderNote"
          type="textarea"
          placeholder="请输入订单备注（选填）"
          :rows="3"
          maxlength="200"
          show-word-limit
        ></el-input>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/userStore'
import { useCartStore } from '../store/cartStore'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

// 从购物车中获取商品列表
const cartItems = ref([])

// 订单备注
const orderNote = ref('')

// 提交状态
const submitting = ref(false)

// 计算总价格
const totalPrice = computed(() => {
  return cartItems.value.reduce((total, item) => {
    return total + (item.book.price * item.quantity)
  }, 0)
})

// 初始化购物车数据
const initCartItems = () => {
  if (!userStore.isLogin) {
    ElMessage.info('请先登录')
    router.push('/login')
    return
  }
  
  if (cartStore.cartItems.length === 0) {
    ElMessage.warning('购物车为空，无法提交订单')
    // 可以选择跳转到购物车页面
    // router.push('/cart')
  } else {
    cartItems.value = [...cartStore.cartItems]
  }
}

// 提交订单
const submitOrder = async () => {
  try {
    // 检查购物车是否为空
    if (cartItems.value.length === 0) {
      ElMessage.warning('购物车为空，无法提交订单')
      return
    }
    
    // 检查用户是否登录
    if (!userStore.isLogin) {
      ElMessage.info('请先登录')
      router.push('/login')
      return
    }
    
    submitting.value = true
    
    // 创建订单数据
    const orderData = {
      userId: userStore.userInfo.id,
      totalPrice: totalPrice.value,
      status: 0, // 设置默认状态：待付款
      orderItems: cartItems.value.map(item => ({
        bookId: item.book.id,
        quantity: item.quantity,
        price: item.book.price
      }))
    }
    
    // 调用后端API创建订单
    const response = await axios.post('/api/order/create', orderData)
    
    if (response.success) {
      // 清空购物车
      await cartStore.clearCart(userStore.userInfo.id)
      
      // 跳转到订单列表页面
      ElMessage.success('订单提交成功')
      router.push('/order')
    } else {
      // 处理后端返回的错误信息
      let errorMessage = response.message || '未知错误'
      // 特别处理库存不足的情况
      if (errorMessage.includes('书籍库存不够：')) {
        // 提取书籍名称，处理两种情况：
        // 1. "书籍库存不够：三体"
        // 2. "创建失败: 书籍库存不够：三体"
        let bookName = errorMessage
        if (bookName.includes('创建失败: 书籍库存不够：')) {
          bookName = bookName.replace('创建失败: 书籍库存不够：', '')
        } else {
          bookName = bookName.replace('书籍库存不够：', '')
        }
        errorMessage = `书籍 "${bookName}" 库存不够`
      }
      ElMessage.error(errorMessage)
    }
  } catch (error) {
    console.error('提交订单失败:', error)
    // 处理异常情况，提取具体的错误信息
    let errorMessage = '未知错误'
    // 检查错误响应结构
    if (error.response) {
      // 有响应，可能是后端返回的错误
      const responseData = error.response.data
      if (responseData) {
        // 如果responseData是对象，直接获取message
        if (typeof responseData === 'object' && responseData.message) {
          errorMessage = responseData.message
        } 
        // 否则尝试解析为字符串
        else if (typeof responseData === 'string') {
          errorMessage = responseData
        } 
        // 或者检查responseData.data是否有message
        else if (responseData.data && responseData.data.message) {
          errorMessage = responseData.data.message
        }
      }
    } 
    // 如果没有response，使用error.message
    else if (error.message) {
      errorMessage = error.message
    }
    
    // 特别处理库存不足的情况
    if (errorMessage.includes('书籍库存不够：')) {
      // 提取书籍名称，处理两种情况：
      // 1. "书籍库存不够：三体"
      // 2. "创建失败: 书籍库存不够：三体"
      let bookName = errorMessage
      if (bookName.includes('创建失败: 书籍库存不够：')) {
        bookName = bookName.replace('创建失败: 书籍库存不够：', '')
      } else {
        bookName = bookName.replace('书籍库存不够：', '')
      }
      errorMessage = `书籍 "${bookName}" 库存不够`
    }
    
    ElMessage.error(errorMessage)
  } finally {
    submitting.value = false
  }
}

// 组件挂载时初始化数据
onMounted(() => {
  initCartItems()
})
</script>

<style scoped>
.order-confirm {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px;
  min-height: 100vh;
  animation: fadeIn 0.8s ease-out;
}

/* 页面标题 */
.page-title {
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 40px;
  text-align: center;
  letter-spacing: -1px;
}

/* 确认内容布局 */
.confirm-content {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* 卡片通用美化 */
:deep(.el-card) {
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.card-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-count {
  font-size: 0.9rem;
  color: var(--text-secondary);
  font-weight: 500;
}

/* 表格美化 */
:deep(.el-table) {
  --el-table-header-bg-color: var(--bg-secondary);
  --el-table-border-color: var(--border-light);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

:deep(.el-table th.el-table__cell) {
  font-weight: 700;
  color: var(--text-primary);
  text-transform: uppercase;
  font-size: 0.85rem;
  letter-spacing: 0.5px;
}

/* 书籍项布局 */
.book-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 0;
}

.book-cover-small {
  width: 50px;
  height: 70px;
  border-radius: var(--radius-sm);
  box-shadow: var(--shadow-sm);
  flex-shrink: 0;
}

.book-item-name {
  font-size: 1rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.book-item-author {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.price {
  font-weight: 600;
  color: var(--text-regular);
}

.subtotal {
  font-weight: 700;
  color: var(--primary-color);
  font-size: 1.1rem;
}

/* 订单总结区域 - 玻璃拟态 */
.order-summary {
  margin-top: 32px;
  background: var(--bg-secondary);
  padding: 32px;
  border-radius: var(--radius-lg);
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 40px;
  border: 1px solid var(--border-light);
}

.total {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.total-label {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-secondary);
}

.total-price {
  font-size: 2.25rem;
  font-weight: 800;
  color: var(--primary-color);
  letter-spacing: -1px;
}

.submit-button {
  min-width: 200px;
  height: 56px;
  font-size: 1.1rem;
  font-weight: 700;
  border-radius: 99px;
  background: var(--primary-gradient);
  border: none;
  box-shadow: var(--shadow-primary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.submit-button:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: var(--shadow-lg);
  filter: brightness(1.1);
}

/* 备注框美化 */
:deep(.el-textarea__inner) {
  border-radius: var(--radius-md);
  padding: 16px;
  border-color: var(--border-light);
  transition: all 0.2s;
}

:deep(.el-textarea__inner:focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px var(--primary-light);
}

/* 动画 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .order-summary {
    flex-direction: column;
    align-items: stretch;
    gap: 24px;
    padding: 24px;
  }
  
  .total {
    justify-content: center;
  }
  
  .submit-button {
    width: 100%;
  }
}
</style>