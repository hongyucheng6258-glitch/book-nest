<template>
  <div class="admin-orders">
    <h2>订单管理</h2>
    
    <!-- 操作栏 -->
    <div class="action-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索订单ID或用户名"
        clearable
        class="search-input"
        @keyup.enter="handleSearch"
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select
        v-model="status"
        placeholder="选择订单状态"
        clearable
        class="status-select"
        @change="handleSearch"
      >
        <el-option label="全部" :value="''" />
        <el-option label="待付款" :value="0" />
        <el-option label="待发货" :value="1" />
        <el-option label="已发货" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="4" />
      </el-select>
    </div>
    
    <!-- 订单列表 -->
    <el-table
      v-loading="loading"
      :data="orders"
      border
      style="width: 100%"
      class="orders-table"
    >
      <el-table-column prop="id" label="订单ID" width="120" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="totalPrice" label="订单金额" width="120">
        <template #default="scope">¥{{ scope.row.totalPrice.toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="订单状态" width="120">
        <template #default="scope">
          <el-tag
            :type="getOrderStatusType(scope.row.status)"
            size="small"
          >
            {{ getOrderStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="200" />
      <el-table-column prop="updatedAt" label="更新时间" width="200" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button
            type="primary"
            size="small"
            @click="handleView(scope.row)"
            class="btn-view"
          >
            <el-icon><View /></el-icon>
            查看
          </el-button>
          <el-button
            type="warning"
            size="small"
            @click="openEditDialog(scope.row)"
            class="btn-edit"
          >
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <!-- 发货按钮，仅对待发货（状态1）的订单显示 -->
          <el-button
            v-if="scope.row.status === 1"
            type="success"
            size="small"
            @click="handleShip(scope.row)"
            class="btn-ship"
          >
            <el-icon><Ship /></el-icon>
            发货
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click="handleDelete(scope.row.id)"
            class="btn-delete"
          >
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    
    <!-- 编辑订单状态对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="'编辑订单状态'"
      width="500px"
      destroy-on-close
    >
      <el-form
        :model="formData"
        ref="formRef"
        label-width="120px"
        :rules="rules"
      >
        <el-form-item label="订单ID" prop="id">
          <el-input v-model="formData.id" disabled />
        </el-form-item>
        <el-form-item label="订单金额" prop="totalPrice">
          <el-input v-model="formData.totalPrice" disabled />
        </el-form-item>
        <el-form-item label="订单状态" prop="status">
          <el-select v-model="formData.status" placeholder="选择订单状态">
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="已发货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      :title="'订单详情'"
      width="800px"
      destroy-on-close
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单ID">{{ selectedOrder.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ selectedOrder.username }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ selectedOrder.totalPrice.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag
            :type="getOrderStatusType(selectedOrder.status)"
            size="small"
          >
            {{ getOrderStatusText(selectedOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ selectedOrder.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ selectedOrder.updatedAt }}</el-descriptions-item>
      </el-descriptions>
      
      <!-- 订单商品列表 -->
      <div class="order-items" style="margin-top: 20px;">
        <h3>订单商品</h3>
        <el-table
          v-if="selectedOrder.orderItems && selectedOrder.orderItems.length > 0"
          :data="selectedOrder.orderItems"
          border
          style="width: 100%"
          size="small"
        >
          <el-table-column label="商品ID" width="100">
            <template #default="scope">
              {{ scope.row.book?.id || scope.row.bookId || scope.row.id }}
            </template>
          </el-table-column>
          <el-table-column label="商品名称" min-width="200">
            <template #default="scope">
                {{ (scope.row.book?.name || scope.row.bookName || '未知商品') }}
              </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" align="center" />
          <el-table-column prop="price" label="单价" width="100" align="right">
            <template #default="scope">
              ¥{{ scope.row.price.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120" align="right">
            <template #default="scope">
              ¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
        <div v-else class="empty-order-items">
          <el-empty description="暂无订单商品信息" />
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Edit, Delete, Search, View, Ship } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../../utils/axios'

// 响应式数据
const orders = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchQuery = ref('')
const status = ref('')

// 对话框相关
const dialogVisible = ref(false)
const detailVisible = ref(false)
const formRef = ref(null)
const selectedOrder = ref({})

// 表单数据
const formData = reactive({
  id: '',
  totalPrice: 0,
  status: 1
})

// 表单验证规则
const rules = {
  status: [
    { required: true, message: '请选择订单状态', trigger: 'blur' }
  ]
}

// 获取订单列表
const getOrders = async () => {
  loading.value = true
  try {
    const params = {
      currentPage: currentPage.value,
      pageSize: pageSize.value
    }
    if (searchQuery.value) {
      const searchValue = parseInt(searchQuery.value)
      if (!isNaN(searchValue)) {
        // 如果搜索值是数字，只作为订单ID搜索
        // 避免同时传递id和userId导致AND条件不匹配
        params.id = searchValue
      } else {
        // 如果搜索值是字符串，作为用户名搜索
        params.username = searchQuery.value
      }
    }
    if (status.value !== '') {
      params.status = parseInt(status.value)
    }
    console.log('请求参数:', params);
    const result = await axios.get('/api/order/list', { params })
    console.log('响应结果:', result);
    if (result && result.success) {
      orders.value = result.data.records || []
      total.value = result.data.total || 0
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

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getOrders()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getOrders()
}

// 当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current
  getOrders()
}

// 打开编辑对话框
const openEditDialog = (row) => {
  // 复制数据到表单
  Object.assign(formData, {
    id: row.id,
    totalPrice: row.totalPrice,
    status: row.status
  })
  dialogVisible.value = true
}

// 打开详情对话框
const handleView = (row) => {
  selectedOrder.value = row
  detailVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 更新订单状态
    const result = await axios.put(`/api/order/update/${formData.id}`, {}, {
      params: { status: formData.status }
    })
    
    if (result && result.success) {
      ElMessage.success('更新订单状态成功')
      dialogVisible.value = false
      getOrders() // 刷新列表
    } else {
      ElMessage.error('更新订单状态失败')
    }
  } catch (error) {
    console.error('提交表单失败:', error)
    if (error.name === 'Error') {
      // 表单验证失败，不显示错误信息
      return
    }
    ElMessage.error('更新订单状态失败: ' + (error.response?.data?.message || error.message))
  }
}

// 删除订单
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个订单吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const result = await axios.delete(`/api/order/${id}`)
    if (result && result.success) {
      ElMessage.success('删除订单成功')
      getOrders() // 刷新列表
    } else {
      ElMessage.error('删除订单失败')
    }
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消操作
      return
    }
    console.error('删除订单失败:', error)
    ElMessage.error('删除订单失败: ' + (error.response?.data?.message || error.message))
  }
}

// 发货操作
const handleShip = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要为订单ID ${order.id} 发货吗？`,
      '发货确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )
    
    // 调用后端API更新订单状态为已发货（2）
    const result = await axios.put(`/api/order/update/${order.id}`, {}, {
      params: { status: 2 }
    })
    
    if (result && result.success) {
      ElMessage.success('发货成功')
      getOrders() // 刷新列表
    } else {
      ElMessage.error('发货失败')
    }
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消操作
      return
    }
    console.error('发货失败:', error)
    ElMessage.error('发货失败: ' + (error.response?.data?.message || error.message))
  }
}

// 获取订单状态文本
const getOrderStatusText = (status) => {
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
const getOrderStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'info',
    2: 'primary',
    3: 'success',
    4: 'danger'
  }
  return typeMap[status] || 'info'
}

// 初始化
onMounted(() => {
  getOrders()
})
</script>

<style scoped>
.admin-orders {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.admin-orders h2 {
  margin-bottom: 24px;
  color: #303133;
  font-size: 20px;
  font-weight: 600;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 12px;
}

.action-bar {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.search-input {
  width: 300px;
}

.status-select {
  width: 150px;
}

.orders-table {
  margin-bottom: 20px;
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding: 16px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

/* 操作按钮样式优化 */
:deep(.el-table__body-wrapper .el-button) {
  margin: 0 4px;
  padding: 6px 12px;
  font-size: 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

:deep(.el-table__body-wrapper .el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 状态标签样式优化 */
:deep(.el-tag) {
  border-radius: 12px;
  padding: 2px 10px;
  font-size: 12px;
  font-weight: 500;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header-wrapper th) {
  background-color: #fafafa;
  font-weight: 600;
  color: #303133;
  height: 48px;
  padding: 0 16px;
}

:deep(.el-table__body-wrapper td) {
  padding: 12px 16px;
  height: 56px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-table__row) {
  transition: all 0.3s ease;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 对话框样式优化 */
:deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background-color: #fafafa;
  border-bottom: 1px solid #ebeef5;
  padding: 16px 20px;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  color: #303133;
}

:deep(.el-dialog__footer) {
  border-top: 1px solid #ebeef5;
  padding: 16px 20px;
  background-color: #fafafa;
}

/* 订单商品列表样式 */
.order-items {
  margin-top: 20px;
  padding: 16px;
  background-color: #fafafa;
  border-radius: 8px;
}

.order-items h3 {
  margin-bottom: 16px;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.empty-order-items {
  padding: 40px 0;
}

/* 描述列表样式 */
:deep(.el-descriptions) {
  margin-bottom: 20px;
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-descriptions__header) {
  background-color: #fafafa;
  border-bottom: 1px solid #ebeef5;
  padding: 16px 20px;
}

:deep(.el-descriptions__body) {
  padding: 16px 20px;
}

:deep(.el-descriptions-item) {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-descriptions-item__label) {
  font-weight: 600;
  color: #606266;
  width: 100px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-orders {
    padding: 12px;
  }
  
  .action-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input,
  .status-select {
    width: 100%;
  }
  
  .search-input {
    margin-bottom: 12px;
  }
}
</style>