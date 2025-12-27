<template>
  <div class="cart">
    <h2 class="cart-title">购物车</h2>
    
    <!-- 购物车为空时的提示 -->
    <div v-if="cartStore.cartItems.length === 0" class="empty-cart">
      <el-empty description="购物车空空如也">
        <el-button type="primary" size="large" @click="goToHome">去逛逛</el-button>
      </el-empty>
    </div>
    
    <!-- 购物车列表 -->
    <div v-else class="cart-container">
      <el-card shadow="never" class="cart-table-wrapper">
        <el-table 
          :data="cartStore.cartItems" 
          style="width: 100%"
          :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: '600' }"
          :row-class-name="'cart-row'"
        >
          <el-table-column label="商品信息" min-width="340">
            <template #default="scope">
              <div class="book-info">
                <div class="book-cover" v-if="scope.row.book.imageUrl">
                  <img :src="scope.row.book.imageUrl" :alt="scope.row.book.name" />
                </div>
                <div class="book-cover placeholder" v-else>
                  <span>{{ scope.row.book.name.charAt(0) }}</span>
                </div>
                <div class="book-details">
                <div class="book-name" @click="goToBookDetail(scope.row.book.id)">{{ scope.row.book.name }}</div>
                <div class="book-author">{{ scope.row.book.author }}</div>
                <div class="book-stock" :class="{'stock-out': scope.row.book.stock <= 0}">
                  {{ scope.row.book.stock <= 0 ? '库存不足' : `库存: ${scope.row.book.stock}` }}
                </div>
              </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="140" align="center">
            <template #default="scope">
              <span class="book-price">¥{{ scope.row.book.price.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="200" align="center">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.quantity"
                :min="1"
                :max="99"
                @change="handleQuantityChange(scope.row.id, scope.row.quantity)"
                size="default"
                class="quantity-input"
              />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="140" align="center">
            <template #default="scope">
              <span class="subtotal-price">¥{{ (scope.row.book.price * scope.row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center">
            <template #default="scope">
              <el-button 
                type="danger" 
                link
                @click="handleRemove(scope.row.id)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      
      <!-- 结算信息 -->
      <div class="checkout-bar">
        <div class="total-info">
          <span class="total-count">已选 {{ cartStore.cartItems.length }} 件商品</span>
          <div class="total-price-wrapper">
             <span class="total-label">合计：</span>
             <span class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
          </div>
        </div>
        <div class="checkout-actions">
          <el-button @click="goToHome" class="continue-btn" size="large">继续购物</el-button>
          <el-button 
            type="primary" 
            size="large" 
            @click="checkout"
            :disabled="cartStore.cartItems.length === 0"
            class="checkout-btn"
          >
            去结算
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/userStore'
import { useCartStore } from '../store/cartStore'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

// 获取购物车列表
const fetchCartList = async () => {
  if (userStore.isLogin && userStore.userInfo && userStore.userInfo.id) {
    await cartStore.fetchCartList(userStore.userInfo.id)
  }
}

// 更新商品数量
const handleQuantityChange = async (id, quantity) => {
  await cartStore.updateQuantity(id, quantity)
}

// 删除商品
const handleRemove = async (id) => {
  await cartStore.removeCartItem(id)
}

// 去结算
const checkout = () => {
  if (cartStore.cartItems.length === 0) {
    return
  }
  
  // 跳转到订单确认页
  router.push('/order/confirm')
}

// 去首页购物
const goToHome = () => {
  router.push('/')
}

// 跳转到详情页
const goToBookDetail = (bookId) => {
  router.push(`/book/${bookId}`)
}

// 组件挂载时获取购物车列表
onMounted(() => {
  fetchCartList()
})
</script>

<style scoped>
.cart {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px;
  min-height: 100vh;
  animation: fadeIn 0.8s ease-out;
}

.cart-title {
  font-size: 2rem;
  font-weight: 800;
  margin-bottom: 32px;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 16px;
}

.cart-title::before {
  content: '';
  display: block;
  width: 6px;
  height: 32px;
  background: var(--primary-gradient);
  border-radius: 4px;
}

/* 购物车为空 */
.empty-cart {
  padding: 80px 0;
  background-color: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
}

/* 购物车容器 */
.cart-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 购物车表格 */
.cart-table-wrapper {
  border-radius: var(--radius-lg);
  overflow: hidden;
  border: 1px solid var(--border-light);
}

:deep(.el-table) {
  --el-table-border-color: var(--border-light);
  --el-table-header-bg-color: #f8fafc;
}

:deep(.el-table__row) {
  transition: background-color 0.2s;
}

:deep(.el-table__row:hover) {
  background-color: #f8fafc !important;
}

/* 书籍信息 */
.book-info {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 8px 0;
}

.book-cover {
  width: 60px;
  height: 90px;
  border-radius: var(--radius-md);
  overflow: hidden;
  background-color: var(--bg-secondary);
  box-shadow: var(--shadow-sm);
  flex-shrink: 0;
}

.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.book-cover.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary);
  color: var(--text-secondary);
  font-weight: 700;
  font-size: 1.5rem;
}

.book-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.book-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 1.05rem;
  cursor: pointer;
  transition: color 0.2s;
}

.book-name:hover {
  color: var(--primary-color);
}

.book-author {
  font-size: 0.9rem;
  color: var(--text-secondary);
}

/* 库存样式 */
.book-stock {
  font-size: 0.85rem;
  color: var(--success-color);
  margin-top: 4px;
}

.book-stock.stock-out {
  color: var(--danger-color);
}

/* 价格样式 */
.book-price {
  font-weight: 600;
  color: var(--text-primary);
  font-family: monospace;
  font-size: 1.1rem;
}

.subtotal-price {
  font-weight: 700;
  color: var(--primary-color);
  font-family: monospace;
  font-size: 1.2rem;
}

/* 数量输入框 */
.quantity-input {
  width: 130px;
}

/* 结算区域 */
.checkout-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  background-color: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--border-light);
  position: sticky;
  bottom: 24px;
  z-index: 100;
}

.total-info {
  display: flex;
  align-items: center;
  gap: 32px;
}

.total-count {
  font-size: 0.95rem;
  color: var(--text-secondary);
}

.total-price-wrapper {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.total-label {
  font-size: 1.1rem;
  color: var(--text-primary);
}

.total-price {
  font-size: 2rem;
  font-weight: 800;
  color: var(--danger-color);
  line-height: 1;
  letter-spacing: -1px;
}

.checkout-actions {
  display: flex;
  gap: 16px;
}

.continue-btn {
  border-radius: var(--radius-md);
  padding: 0 32px;
}

.checkout-btn {
  border-radius: var(--radius-md);
  padding: 0 40px;
  font-weight: 600;
  font-size: 1.1rem;
  box-shadow: var(--shadow-primary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .cart {
    padding: 20px 16px;
  }
  
  .book-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .checkout-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 24px;
    padding: 20px;
    bottom: 0;
    border-radius: 12px 12px 0 0;
  }
  
  .total-info {
    flex-direction: column;
    align-items: flex-end;
    gap: 12px;
  }
  
  .checkout-actions {
    display: flex;
  }
  
  .checkout-actions .el-button {
    flex: 1;
  }
}
</style>