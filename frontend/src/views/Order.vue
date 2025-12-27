<template>
  <div class="order">
    <h2 class="page-title">我的订单</h2>
    
    <!-- 订单状态筛选 -->
    <div class="order-filter">
      <el-radio-group v-model="status" @change="handleStatusChange" class="filter-radio">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="0">待付款</el-radio-button>
        <el-radio-button label="1">待发货</el-radio-button>
        <el-radio-button label="2">已发货</el-radio-button>
        <el-radio-button label="3">已完成</el-radio-button>
        <el-radio-button label="4">已取消</el-radio-button>
      </el-radio-group>
    </div>
    
    <!-- 加载状态 -->
    <el-skeleton :loading="loading" animated>
      <!-- 订单列表 -->
      <div class="order-list">
        <!-- 空状态 -->
        <el-empty 
          v-if="!loading && orders.length === 0" 
          description="暂无订单" 
          :image-size="200"
        >
          <el-button type="primary" @click="router.push('/')">去购物</el-button>
        </el-empty>
        
        <!-- 订单卡片 -->
        <el-card 
          v-for="order in orders" 
          :key="order.id" 
          shadow="hover"
          class="order-card"
        >
          <!-- 订单头部 -->
          <div class="order-header">
            <div class="order-info">
              <div class="order-main-info">
            <span class="order-id">订单号：{{ order.userOrderNum }}</span>
            <span class="order-time">{{ formatDate(order.createdAt) }}</span>
          </div>
              <el-tag 
                :type="getStatusType(order.status)" 
                size="medium"
                effect="dark"
              >
                {{ getStatusText(order.status) }}
              </el-tag>
            </div>
            <el-button 
              type="primary" 
              size="small" 
              @click="goToOrderDetail(order.id)"
              class="detail-button"
            >
              查看详情
            </el-button>
          </div>
          
          <!-- 订单商品 -->
          <div class="order-items">
            <div class="order-item" v-for="item in order.orderItems" :key="item.id">
              <el-image 
                v-if="item.book && item.book.imageUrl" 
                :src="item.book.imageUrl" 
                class="item-cover"
                fit="cover"
                placeholder="加载中..."
            >
                <template #error>
                  <div class="image-error-small">{{ item.book ? item.book.name : '未知书籍' }}</div>
                </template>
              </el-image>
              <div v-else class="item-cover item-cover-placeholder">{{ item.book ? item.book.name : '未知书籍' }}</div>
              
              <div class="item-info">
                <h4 class="item-name">{{ item.book ? item.book.name : '未知书籍' }}</h4>
                <p class="item-author">作者：{{ item.book ? item.book.author : '未知作者' }}</p>
                <div class="item-meta">
                  <span class="item-quantity">数量：{{ item.quantity }}</span>
                  <span class="item-unit-price">¥{{ item.price.toFixed(2) }}</span>
                </div>
              </div>
              <div class="item-subtotal">
                ¥{{ (item.price * item.quantity).toFixed(2) }}
              </div>
            </div>
          </div>
          
          <!-- 订单总金额 -->
          <div class="order-footer">
            <div class="total">
              <span class="total-label">共 {{ getTotalItems(order) }} 件商品 总计：</span>
              <span class="total-price">¥{{ order.totalPrice.toFixed(2) }}</span>
            </div>
            <div class="order-actions">
                <el-button 
                  v-if="order.status === 0" 
                  type="primary" 
                  size="small" 
                  @click="handlePay(order.id)"
                  class="action-btn pay-btn"
                >
                  立即付款
                </el-button>
                <el-button 
                  v-if="order.status === 0 || getStatusText(order.status) === '未知状态'" 
                  type="danger" 
                  size="small" 
                  @click="handleCancel(order.id)"
                  class="action-btn cancel-btn"
                >
                  取消订单
                </el-button>
                <el-button 
                  v-if="order.status === 2" 
                  type="success" 
                  size="small" 
                  @click="handleConfirm(order.id)"
                  class="action-btn confirm-btn"
                >
                  确认收货
                </el-button>
                <el-button 
                  v-if="order.status === 3" 
                  type="info" 
                  size="small" 
                  @click="handleEvaluate(order)"
                  class="action-btn evaluate-btn"
                >
                  评价
                </el-button>
                <el-button 
                  v-if="order.status === 3 || order.status === 4" 
                  type="danger" 
                  size="small" 
                  @click="handleDeleteOrder(order.id)"
                  class="action-btn delete-btn"
                >
                  删除订单
                </el-button>
              </div>
          </div>
        </el-card>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-container" v-if="!loading && total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          class="order-pagination"
        />
      </div>
    </el-skeleton>
    
    <!-- 评价对话框 -->
    <el-dialog
      v-model="evaluateDialogVisible"
      title="评价商品"
      width="500px"
    >
      <div v-if="currentOrderItem" class="evaluate-dialog">
        <!-- 商品信息 -->
        <div class="evaluate-product">
          <el-image
            v-if="currentOrderItem.book && currentOrderItem.book.imageUrl"
            :src="currentOrderItem.book.imageUrl"
            class="product-cover"
            fit="cover"
          />
          <div v-else class="product-cover-placeholder">
            {{ currentOrderItem.book ? currentOrderItem.book.name : '未知书籍' }}
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ currentOrderItem.book ? currentOrderItem.book.name : '未知书籍' }}</h3>
            <p class="product-author">作者：{{ currentOrderItem.book ? currentOrderItem.book.author : '未知作者' }}</p>
          </div>
        </div>
        
        <!-- 星级评分 -->
        <div class="evaluate-rating">
          <div class="rating-label">评分：</div>
          <el-rate
            v-model="rating"
            :max="5"
            show-score
            text-color="#f56c6c"
            score-template="{value}分"
          />
        </div>
        
        <!-- 评论输入 -->
        <div class="evaluate-comment">
          <div class="comment-label">评论：</div>
          <el-input
            v-model="comment"
            type="textarea"
            :rows="4"
            placeholder="请输入您的评价（可选）"
            maxlength="200"
            show-word-limit
          />
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="evaluateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveEvaluate">保存评价</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'
import { useUserStore } from '../store/userStore'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 订单列表
const orders = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const status = ref('') // 订单状态筛选
const loading = ref(false) // 加载状态

// 日期格式化
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

// 计算订单商品总数
const getTotalItems = (order) => {
  return order.orderItems.reduce((total, item) => {
    return total + item.quantity
  }, 0)
}

// 获取订单列表
const getOrderList = async () => {
  loading.value = true
  try {
    if (!userStore.isLogin || !userStore.userInfo) {
      ElMessage.info('请先登录')
      router.push('/login')
      return
    }
    
    const response = await axios.get('/api/order/list', {
      params: {
        userId: userStore.userInfo.id,
        status: status.value === '' ? undefined : parseInt(status.value, 10),
        currentPage: currentPage.value,
        pageSize: pageSize.value
      }
    })
    
    if (response && response.success) {
      // 从response.data.records获取订单列表，从response.data.total获取总条数
      orders.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      orders.value = []
      total.value = 0
      ElMessage.warning('获取订单列表失败')
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    orders.value = []
    total.value = 0
    ElMessage.error('获取订单列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 跳转到订单详情
const goToOrderDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

// 状态筛选变化
const handleStatusChange = () => {
  currentPage.value = 1
  getOrderList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getOrderList()
}

// 当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current
  getOrderList()
}

// 获取订单状态文本
const getStatusText = (status) => {
  // 处理status为null或undefined的情况
  if (status == null) {
    return '未知状态'
  }
  // 将status转换为数字，处理字符串类型的状态码
  const statusNum = parseInt(status, 10)
  // 检查转换结果是否为有效数字
  if (isNaN(statusNum)) {
    return '未知状态'
  }
  const statusMap = {
    0: '待付款',
    1: '待发货',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[statusNum] || '未知状态'
}

// 获取订单状态标签类型
const getStatusType = (status) => {
  // 处理status为null或undefined的情况
  if (status == null) {
    return 'default'
  }
  // 将status转换为数字，处理字符串类型的状态码
  const statusNum = parseInt(status, 10)
  // 检查转换结果是否为有效数字
  if (isNaN(statusNum)) {
    return 'default'
  }
  const typeMap = {
    0: 'warning',
    1: 'info',
    2: 'primary',
    3: 'success',
    4: 'danger'
  }
  return typeMap[statusNum] || 'default'
}

// 立即付款
const handlePay = async (orderId) => {
  try {
    const result = await axios.put(`/api/order/update/${orderId}`, null, {
      params: {
        status: 1 // 待发货
      }
    })
    if (result && result.success) {
      // 重新获取订单列表
      await getOrderList()
      ElMessage.success('付款成功，订单已转为待发货状态')
    } else {
      ElMessage.error('付款失败')
    }
  } catch (error) {
    console.error('付款失败:', error)
    ElMessage.error('付款失败: ' + (error.response?.data?.message || error.message))
  }
}

// 取消订单
const handleCancel = async (orderId) => {
  try {
    // 添加确认弹窗
    const confirmResult = await ElMessageBox.confirm(
      '确定要取消该订单吗？',
      '取消订单确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    if (confirmResult === 'confirm') {
      // 调用后端API取消订单
      const result = await axios.put(`/api/order/update/${orderId}`, null, {
        params: {
          status: 4 // 已取消
        }
      })
      
      if (result && result.success) {
        // 重新获取订单列表
        await getOrderList()
        ElMessage.success('订单已取消')
      } else {
        ElMessage.error('取消订单失败')
      }
    }
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消操作
      return
    }
    console.error('取消订单失败:', error)
    ElMessage.error('取消订单失败: ' + (error.response?.data?.message || error.message))
  }
}

// 确认收货
const handleConfirm = async (orderId) => {
  try {
    const result = await axios.put(`/api/order/update/${orderId}`, null, {
      params: {
        status: 3 // 已完成
      }
    })
    if (result && result.success) {
      // 重新获取订单列表
      await getOrderList()
      ElMessage.success('确认收货成功，订单已完成')
    } else {
      ElMessage.error('确认收货失败')
    }
  } catch (error) {
    console.error('确认收货失败:', error)
    ElMessage.error('确认收货失败: ' + (error.response?.data?.message || error.message))
  }
}

// 评价相关
const evaluateDialogVisible = ref(false)
const currentOrder = ref(null)
const currentOrderItem = ref(null)
const rating = ref(5)
const comment = ref('')

// 打开评价对话框
const handleEvaluate = (order) => {
  // 检查订单是否有商品
  if (!order.orderItems || order.orderItems.length === 0) {
    ElMessage.warning('该订单没有商品可以评价')
    return
  }
  
  // 默认选择第一个商品
  currentOrder.value = order
  currentOrderItem.value = order.orderItems[0]
  rating.value = 5
  comment.value = ''
  evaluateDialogVisible.value = true
}

// 保存评价
const saveEvaluate = async () => {
  if (!currentOrderItem.value || !currentOrderItem.value.book) {
    ElMessage.error('评价失败：无效的商品信息')
    return
  }
  
  try {
    // 保存评分
    const ratingData = {
      userId: userStore.userInfo.id,
      bookId: currentOrderItem.value.book.id,
      rating: rating.value,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    await axios.post('/api/rating/add', ratingData)
    
    // 保存评论（如果有评论内容）
    if (comment.value.trim()) {
      const commentData = {
        userId: userStore.userInfo.id,
        bookId: currentOrderItem.value.book.id,
        content: comment.value,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
      await axios.post('/api/comment/add', commentData)
    }
    
    ElMessage.success('评价成功')
    evaluateDialogVisible.value = false
    // 刷新订单列表
    await getOrderList()
  } catch (error) {
    console.error('保存评价失败:', error)
    ElMessage.error('评价失败：' + (error.response?.data?.message || error.message))
  }
}

// 删除订单
const handleDeleteOrder = async (orderId) => {
  try {
    // 确认删除
    const confirmResult = await ElMessageBox.confirm(
      '确定要删除该订单吗？此操作不可恢复。',
      '删除订单确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    if (confirmResult === 'confirm') {
      // 调用删除API
      const result = await axios.delete(`/api/order/${orderId}`)
      
      if (result && result.success) {
        // 重新获取订单列表
        await getOrderList()
        ElMessage.success('订单删除成功')
      } else {
        ElMessage.error('删除订单失败')
      }
    }
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消删除
      return
    }
    console.error('删除订单失败:', error)
    ElMessage.error('删除订单失败: ' + (error.response?.data?.message || error.message))
  }
}

// 监听登录状态变化
watch(() => userStore.isLogin, (newValue) => {
  if (newValue) {
    getOrderList()
  }
})

// 组件挂载时获取订单列表
onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
.order {
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

/* 筛选栏 - 玻璃拟态 */
.order-filter {
  margin-bottom: 32px;
  background: var(--glass-bg);
  backdrop-filter: blur(16px);
  padding: 8px;
  border-radius: 99px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: var(--shadow-md);
  display: flex;
  justify-content: center;
}

:deep(.el-radio-group) {
  display: flex;
  width: 100%;
}

:deep(.el-radio-button__inner) {
  border: none !important;
  background: transparent !important;
  color: var(--text-regular);
  font-weight: 600;
  padding: 12px 32px;
  border-radius: 99px !important;
  transition: all 0.3s ease;
  box-shadow: none !important;
}

:deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: var(--primary-gradient) !important;
  color: white !important;
  box-shadow: var(--shadow-primary) !important;
}

/* 订单卡片 */
.order-card {
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-light);
  margin-bottom: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.order-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
  border-color: var(--primary-light);
}

/* 订单头部 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-light);
}

.order-info {
  display: flex;
  align-items: center;
  gap: 24px;
}

.order-main-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-id {
  font-size: 0.95rem;
  font-weight: 700;
  color: var(--text-primary);
}

.order-time {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

/* 商品列表 */
.order-items {
  padding: 12px 24px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px 0;
  border-bottom: 1px solid var(--border-light);
}

.order-item:last-child {
  border-bottom: none;
}

.item-cover {
  width: 80px;
  height: 110px;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  flex-shrink: 0;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
  transition: color 0.3s;
}

.item-author {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.item-meta {
  display: flex;
  gap: 24px;
  font-size: 0.9rem;
  color: var(--text-regular);
}

.item-subtotal {
  font-size: 1.25rem;
  font-weight: 800;
  color: var(--text-primary);
}

/* 订单底部 */
.order-footer {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fafafa;
}

.total {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.total-label {
  font-size: 1rem;
  color: var(--text-secondary);
}

.total-price {
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--primary-color);
}

.order-actions {
  display: flex;
  gap: 12px;
  
  .order-id {
    font-size: 14px;
  }
  
  .order-time {
    font-size: 12px;
  }
  
  .order-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .item-cover {
    width: 60px;
    height: 80px;
  }
  
  .item-cover-placeholder, .image-error-small {
    width: 60px;
    height: 80px;
  }
  
  .item-subtotal {
    text-align: left;
    min-width: auto;
    font-size: 16px;
  }
  
  .order-footer {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .total {
    justify-content: center;
  }
  
  .order-actions {
    justify-content: stretch;
  }
  
  .action-btn {
    flex: 1;
    min-width: 100px;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 20px;
  }
  
  .order-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .total-label {
    font-size: 16px;
  }
  
  .total-price {
    font-size: 24px;
  }
  
  .action-btn {
    height: 32px;
    font-size: 13px;
  }
}

/* 评价对话框样式 */
.evaluate-dialog {
  padding: 20px 0;
}

.evaluate-product {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.product-cover {
  width: 80px;
  height: 100px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.product-cover-placeholder {
  width: 80px;
  height: 100px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #909399;
  text-align: center;
  padding: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-author {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.evaluate-rating {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.rating-label {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  width: 60px;
}

.evaluate-comment {
  margin-bottom: 20px;
}

.comment-label {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  display: block;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

</style>