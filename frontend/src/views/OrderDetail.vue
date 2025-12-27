<template>
  <div class="order-detail">
    <h2>订单详情</h2>
    
    <el-card shadow="hover">
      <!-- 订单信息 -->
      <div class="order-header">
        <div class="order-info">
          <p class="order-id">订单号：{{ order.userOrderNum }}</p>
          <p class="order-time">下单时间：{{ order.createdAt }}</p>
          <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
        </div>
        <div class="order-total">
          <span class="total-label">总计：</span>
          <span class="total-price">¥{{ order.totalPrice }}</span>
        </div>
      </div>
      
      <!-- 订单商品 -->
      <div class="order-items">
        <h3>商品信息</h3>
        <el-table :data="order.orderItems" border style="width: 100%">
          <el-table-column prop="book.name" label="书籍名称" width="300" />
          <el-table-column prop="book.author" label="作者" width="150" />
          <el-table-column prop="book.price" label="单价" width="100">
            <template #default="scope">¥{{ scope.row.book.price }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100" />
          <el-table-column label="小计" width="100">
            <template #default="scope">¥{{ (scope.row.book.price * scope.row.quantity).toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 订单操作 -->
      <div class="order-actions">
        <el-button v-if="order.status === 0" type="primary" @click="handlePay">立即付款</el-button>
        <el-button v-if="order.status === 0" type="danger" @click="handleCancel">取消订单</el-button>
        <el-button v-if="order.status === 2" type="success" @click="handleConfirm">确认收货</el-button>
        <el-button v-if="order.status === 3" type="info" @click="handleEvaluate">评价</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'

const route = useRoute()

// 订单信息
const order = ref({
  id: 0,
  userId: 0,
  totalPrice: 0,
  status: 0,
  createdAt: '',
  orderItems: []
})

// 获取订单详情
const getOrderDetail = async () => {
  try {
    const id = route.params.id
    const response = await axios.get(`/api/order/${id}`)
    order.value = response.data
  } catch (error) {
    console.error('获取订单详情失败:', error)
  }
}

// 获取订单状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待付款',
    1: '待发货',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 获取订单状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'info',
    2: 'primary',
    3: 'success',
    4: 'danger'
  }
  return typeMap[status] || 'default'
}

// 立即付款
const handlePay = async () => {
  try {
    const result = await axios.put(`/api/order/update/${order.value.id}`, null, {
      params: {
        status: 1 // 待发货
      }
    })
    if (result && result.success) {
      order.value.status = 1
      ElMessage.success('付款成功，订单已转为待发货状态')
    }
  } catch (error) {
    console.error('付款失败:', error)
    ElMessage.error('付款失败: ' + (error.response?.data?.message || error.message))
  }
}

// 取消订单
const handleCancel = async () => {
  try {
    const result = await axios.put(`/api/order/update/${order.value.id}`, null, {
      params: {
        status: 4 // 已取消
      }
    })
    if (result && result.success) {
      order.value.status = 4
      ElMessage.success('订单已取消')
    }
  } catch (error) {
    console.error('取消订单失败:', error)
    ElMessage.error('取消订单失败: ' + (error.response?.data?.message || error.message))
  }
}

// 确认收货
const handleConfirm = async () => {
  try {
    const result = await axios.put(`/api/order/update/${order.value.id}`, null, {
      params: {
        status: 3 // 已完成
      }
    })
    if (result && result.success) {
      order.value.status = 3
      ElMessage.success('确认收货成功，订单已完成')
    }
  } catch (error) {
    console.error('确认收货失败:', error)
    ElMessage.error('确认收货失败: ' + (error.response?.data?.message || error.message))
  }
}

// 评价
const handleEvaluate = () => {
  ElMessage.info('评价功能正在开发中')
}

// 组件挂载时获取订单详情
onMounted(() => {
  getOrderDetail()
})
</script>

<style scoped>
.order-detail {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 24px;
  min-height: 100vh;
  animation: fadeIn 0.8s ease-out;
}

h2 {
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 40px;
  text-align: center;
  letter-spacing: -1px;
}

:deep(.el-card) {
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-md);
  padding: 24px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-light);
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-id {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
}

.order-time {
  font-size: 0.95rem;
  color: var(--text-secondary);
}

.order-total {
  text-align: right;
}

.total-label {
  font-size: 1.1rem;
  color: var(--text-secondary);
}

.total-price {
  font-size: 2.25rem;
  font-weight: 800;
  color: var(--primary-color);
  display: block;
}

.order-items {
  margin-bottom: 40px;
}

h3 {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 24px;
  color: var(--text-primary);
}

:deep(.el-table) {
  border-radius: var(--radius-lg);
  overflow: hidden;
  border: 1px solid var(--border-light);
}

:deep(.el-table th.el-table__cell) {
  background: var(--bg-secondary);
  font-weight: 700;
  color: var(--text-primary);
}

.order-actions {
  display: flex;
  gap: 16px;
  justify-content: flex-end;
}

:deep(.el-button) {
  border-radius: var(--radius-md);
  padding: 12px 28px;
  font-weight: 600;
  transition: all 0.3s ease;
}

:deep(.el-button--primary) {
  background: var(--primary-gradient);
  border: none;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 24px;
  }
  
  .order-total {
    text-align: left;
  }
}
</style>