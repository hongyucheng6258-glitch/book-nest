<template>
  <div class="admin-container">
    <!-- 顶部导航栏 -->
    <div class="admin-header">
      <div class="header-left">
        <h1 class="admin-title">书城后台管理系统</h1>
      </div>
      <div class="header-right">
        <div class="user-info">
          <span>欢迎，{{ userInfo.username }}</span>
          <el-button type="text" @click="handleLogout" class="logout-btn">退出登录</el-button>
        </div>
      </div>
    </div>
    
    <!-- 主体内容区 -->
    <div class="admin-main">
      <!-- 左侧菜单栏 -->
      <div class="admin-sidebar">
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          background-color="#fff"
          text-color="#333"
          active-text-color="#409eff"
          @select="handleMenuSelect"
        >
          <el-menu-item index="dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="books">
            <el-icon><Document /></el-icon>
            <span>书籍管理</span>
          </el-menu-item>
          <el-menu-item index="categories">
            <el-icon><Menu /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="orders">
            <el-icon><ShoppingCart /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="reviews">
            <el-icon><Star /></el-icon>
            <span>评价管理</span>
          </el-menu-item>
          <el-menu-item index="friends">
            <el-icon><ChatDotRound /></el-icon>
            <span>好友管理</span>
          </el-menu-item>
        </el-menu>
      </div>
      
      <!-- 右侧内容区 -->
      <div class="admin-content">
        <div class="content-header">
          <h2 class="content-title">{{ pageTitle }}</h2>
          <div class="content-actions">
            <!-- 根据不同页面显示不同操作按钮 -->
            <template v-if="activeMenu === 'dashboard'">
              <el-button type="primary" @click="refreshData">
                <el-icon><Refresh /></el-icon>
                刷新数据
              </el-button>
            </template>

          </div>
        </div>
        
        <!-- 内容主体 -->
        <div class="content-body">
          <!-- 仪表盘内容 -->
          <template v-if="activeMenu === 'dashboard'">
            <div class="dashboard-stats">
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-info">
                    <p class="stat-label">书籍总数</p>
                    <h3 class="stat-value">{{ stats.bookCount }}</h3>
                    <p class="stat-change">+12.5% 较上周</p>
                  </div>
                  <div class="stat-icon book-icon">
                    <el-icon><Document /></el-icon>
                  </div>
                </div>
              </el-card>
              
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-info">
                    <p class="stat-label">用户总数</p>
                    <h3 class="stat-value">{{ stats.userCount }}</h3>
                    <p class="stat-change">+8.3% 较上周</p>
                  </div>
                  <div class="stat-icon user-icon">
                    <el-icon><User /></el-icon>
                  </div>
                </div>
              </el-card>
              
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-info">
                    <p class="stat-label">今日订单</p>
                    <h3 class="stat-value">{{ stats.todayOrderCount }}</h3>
                    <p class="stat-change">+15.2% 较昨日</p>
                  </div>
                  <div class="stat-icon order-icon">
                    <el-icon><ShoppingCart /></el-icon>
                  </div>
                </div>
              </el-card>
              
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-info">
                    <p class="stat-label">本月销售额</p>
                    <h3 class="stat-value">¥{{ stats.monthSales }}</h3>
                    <p class="stat-change">+22.1% 较上月</p>
                  </div>
                  <div class="stat-icon sales-icon">
                    <el-icon><Money /></el-icon>
                  </div>
                </div>
              </el-card>
            </div>
            
            <!-- 图表区域 -->
            <div class="dashboard-charts">
              <el-card class="chart-card" shadow="hover">
                <template #header>
                  <div class="card-header-title">
                    <span>最近7天销售额</span>
                  </div>
                </template>
                <div class="chart-content">
                  <!-- 销售额图表 -->
                  <div ref="salesChart" class="chart-container"></div>
                </div>
              </el-card>
              
              <el-card class="chart-card" shadow="hover">
                <template #header>
                  <div class="card-header-title">
                    <span>书籍分类占比</span>
                  </div>
                </template>
                <div class="chart-content">
                  <!-- 分类占比图表 -->
                  <div ref="categoryChart" class="chart-container"></div>
                </div>
              </el-card>
            </div>
          </template>
          
          <!-- 书籍管理内容 -->
          <template v-else-if="activeMenu === 'books'">
            <div class="books-content">
              <!-- 书籍管理组件 -->
              <AdminBooks />
            </div>
          </template>
          
          <!-- 分类管理内容 -->
          <template v-else-if="activeMenu === 'categories'">
            <div class="categories-content">
              <!-- 分类管理组件 -->
              <AdminCategories />
            </div>
          </template>
          
          <!-- 用户管理内容 -->
          <template v-else-if="activeMenu === 'users'">
            <div class="users-content">
              <!-- 用户管理组件 -->
              <AdminUsers />
            </div>
          </template>
          
          <!-- 订单管理内容 -->
          <template v-else-if="activeMenu === 'orders'">
            <div class="orders-content">
              <!-- 订单管理组件 -->
              <AdminOrders />
            </div>
          </template>
          
          <!-- 评价管理内容 -->
          <template v-else-if="activeMenu === 'reviews'">
            <div class="reviews-content">
              <!-- 评价管理组件 -->
              <AdminReviews />
            </div>
          </template>
          <!-- 好友管理内容 -->
          <template v-else-if="activeMenu === 'friends'">
            <div class="friends-content">
              <!-- 好友管理组件 -->
              <AdminFriends />
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/userStore'
import axios from '../../utils/axios'
import { ElMessage } from 'element-plus'
import AdminBooks from './AdminBooks.vue'
import AdminCategories from './AdminCategories.vue'
import AdminOrders from './AdminOrders.vue'
import AdminUsers from './AdminUsers.vue'
import AdminReviews from './AdminReviews.vue'
import AdminFriends from './AdminFriends.vue'
// 导入ECharts核心模块和必要组件
import * as echarts from 'echarts/core'
import {
  BarChart,
  PieChart
} from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent
} from 'echarts/components'
import {
  CanvasRenderer
} from 'echarts/renderers'

// 注册必要的组件
echarts.use([
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  BarChart,
  PieChart,
  CanvasRenderer
])
import {
  DataAnalysis,
  Document,
  Menu,
  User,
  ShoppingCart,
  Refresh,
  Plus,
  Money,
  Search,
  Edit,
  Delete,
  Star,
  ChatDotRound
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 活跃菜单
const activeMenu = ref('dashboard')

// 页面标题
const pageTitle = computed(() => {
  const titleMap = {
    dashboard: '仪表盘',
    books: '书籍管理',
    categories: '分类管理',
    users: '用户管理',
    orders: '订单管理',
    reviews: '评价管理',
    friends: '好友管理'
  }
  return titleMap[activeMenu.value] || '仪表盘'
})

// 监听活跃菜单变化，当切换回仪表盘时重新初始化图表
watch(activeMenu, async (newMenu) => {
  if (newMenu === 'dashboard') {
    // 延迟一下，确保DOM已渲染
    await nextTick()
    // 重新初始化图表
    initSalesChart()
    initCategoryChart()
  }
})

// 用户信息
const userInfo = computed(() => userStore.userInfo || { username: '管理员' })

// 统计数据
const stats = ref({
  bookCount: 0,
  userCount: 0,
  todayOrderCount: 0,
  monthSales: 0
})

// 图表数据
const last7DaysSales = ref([])
const categorySales = ref([])

// 图表容器引用
const salesChart = ref(null)
const categoryChart = ref(null)

// 图表实例管理
const charts = {
  sales: null,
  category: null
}

// 销毁指定图表实例
const destroyChart = (chartName) => {
  if (charts[chartName]) {
    charts[chartName].dispose()
    charts[chartName] = null
  }
}

// 销毁所有图表实例
const destroyAllCharts = () => {
  destroyChart('sales')
  destroyChart('category')
}

// 更新销售额图表
const updateSalesChart = () => {
  if (!charts.sales || !salesChart.value) return
  
  let dates = []
  let sales = []
  
  if (last7DaysSales.value && last7DaysSales.value.length > 0) {
    dates = last7DaysSales.value.map(item => item.date)
    sales = last7DaysSales.value.map(item => item.sales)
  }
  
  console.log('销售额数据:', { dates, sales })
  
  // ECharts配置
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>销售额: ¥{c}'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates || [],
      axisLabel: {
        color: '#666',
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#666',
        fontSize: 12,
        formatter: '¥{value}'
      }
    },
    series: [
      {
        type: 'bar',
        data: sales || [],
        barWidth: '60%',
        itemStyle: {
          color: '#409eff',
          borderRadius: [4, 4, 0, 0]
        }
      }
    ]
  }
  
  try {
    charts.sales.setOption(option, true)
    console.log('销售额图表更新成功')
  } catch (error) {
    console.error('销售额图表更新失败:', error)
    // 失败时销毁实例，下次重新创建
    destroyChart('sales')
  }
}

// 更新分类占比图表
const updateCategoryChart = () => {
  if (!charts.category || !categoryChart.value) return
  
  let categoryData = []
  
  if (categorySales.value && categorySales.value.length > 0) {
    categoryData = categorySales.value
      .filter(item => item !== null)
      .map(item => ({
        name: item.categoryName || '未知分类',
        value: item.sales || 0
      }))
      .filter(item => item.value > 0)
  }
  
  console.log('分类数据:', { categoryData })
  
  // ECharts配置
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: '5%',
      top: 'center',
      formatter: '{name}',
      data: categoryData.map(item => item.name),
      textStyle: {
        color: '#666',
        fontSize: 12
      },
      itemWidth: 12,
      itemHeight: 12,
      itemGap: 15
    },
    series: [
      {
        name: '销售额',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['65%', '50%'],
        data: categoryData || [],
        emphasis: {
          itemStyle: {
            shadowBlur: 15,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.2)'
          }
        },
        label: {
          show: false
        },
        labelLine: {
          show: false
        }
      }
    ]
  }
  
  try {
    charts.category.setOption(option, true)
    console.log('分类图表更新成功')
  } catch (error) {
    console.error('分类图表更新失败:', error)
    // 失败时销毁实例，下次重新创建
    destroyChart('category')
  }
}

// 初始化销售额图表
const initSalesChart = () => {
  if (!salesChart.value) return
  
  try {
    destroyChart('sales')
    charts.sales = echarts.init(salesChart.value)
    updateSalesChart()
    return true
  } catch (error) {
    console.error('初始化销售额图表失败:', error)
    return false
  }
}

// 初始化分类占比图表
const initCategoryChart = () => {
  if (!categoryChart.value) return
  
  try {
    destroyChart('category')
    charts.category = echarts.init(categoryChart.value)
    updateCategoryChart()
    return true
  } catch (error) {
    console.error('初始化分类图表失败:', error)
    return false
  }
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  Object.values(charts).forEach(chart => {
    if (chart) {
      try {
        chart.resize()
      } catch (error) {
        console.error('图表重绘失败:', error)
      }
    }
  })
}

// 菜单选择处理
const handleMenuSelect = (index) => {
  activeMenu.value = index
}

// 退出登录
const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/login')
}

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await axios.get('/api/admin/dashboard/stats')
    // axios响应拦截器已经处理为只返回data，所以直接使用response
    if (response && response.success) {
      stats.value = response.data || {
        bookCount: 0,
        userCount: 0,
        todayOrderCount: 0,
        monthSales: 0
      }
      ElMessage.success('获取统计数据成功')
    } else {
      ElMessage.error('获取统计数据失败')
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  }
}

// 获取最近7天销售额数据
const fetchLast7DaysSales = async () => {
  try {
    console.log('开始获取最近7天销售额数据')
    const response = await axios.get('/api/order/last7days')
    console.log('最近7天销售额API响应:', response)
    
    if (response && response.success) {
      last7DaysSales.value = response.data
      console.log('最近7天销售额数据:', last7DaysSales.value)
      // 更新图表
      updateSalesChart()
      ElMessage.success('获取最近7天销售额数据成功')
    } else {
      console.error('最近7天销售额API返回失败:', response)
      ElMessage.error('获取最近7天销售额数据失败')
    }
  } catch (error) {
    console.error('获取最近7天销售额数据失败:', error)
    ElMessage.error('获取最近7天销售额数据失败')
  }
}

// 获取分类占比数据
const fetchCategorySales = async () => {
  try {
    console.log('开始获取分类占比数据')
    
    // 1. 先获取所有分类列表
    const categoriesResponse = await axios.get('/api/category/all')
    let allCategories = []
    
    if (categoriesResponse && categoriesResponse.success) {
      allCategories = categoriesResponse.data || []
    }
    
    // 2. 再获取各分类的销售额数据
    const salesResponse = await axios.get('/api/order/category-sales')
    const categorySalesData = salesResponse && salesResponse.success ? salesResponse.data || [] : []
    
    // 3. 将所有分类与销售额数据合并，确保所有分类都有销售额数据（0或实际值）
    // 创建销售额映射表，方便查找
    const salesMap = new Map()
    categorySalesData.forEach(item => {
      // 确保item不是null，并且有categoryName属性
      if (item && item.categoryName) {
        salesMap.set(item.categoryName, item.sales || 0)
      }
    })
    
    // 合并数据，排除"全部"分类
    const mergedData = allCategories
      .filter(category => category.id !== 1) // 排除"全部"分类
      .map(category => ({
        categoryName: category.name,
        sales: salesMap.get(category.name) || 0
      }))
      .filter(item => item.sales > 0) // 只显示有销售额的分类
    
    console.log('分类占比数据:', mergedData)
    
    categorySales.value = mergedData
    // 更新图表
    updateCategoryChart()
    ElMessage.success('获取分类占比数据成功')
  } catch (error) {
    console.error('获取分类占比数据失败:', error)
    ElMessage.error('获取分类占比数据失败')
  }
}

// 刷新数据
const refreshData = async () => {
  if (activeMenu.value === 'dashboard') {
    await Promise.all([
      fetchStats(),
      fetchLast7DaysSales(),
      fetchCategorySales()
    ])
  }
}

// 页面挂载
onMounted(async () => {
  // 检查登录状态
  if (!userStore.isLogin) {
    router.push('/login')
    return
  }
  
  // 从后端获取真实统计数据和图表数据
  await Promise.all([
    fetchStats(),
    fetchLast7DaysSales(),
    fetchCategorySales()
  ])
  
  // 使用nextTick确保DOM渲染完成后初始化图表
  await nextTick()
  
  // 调试信息
  console.log('图表容器是否存在:', {
    salesChart: !!salesChart.value,
    categoryChart: !!categoryChart.value
  })
  
  // 初始化图表
  initSalesChart()
  initCategoryChart()
  
  // 添加窗口大小变化监听
  window.addEventListener('resize', handleResize)
})

// 组件卸载
onUnmounted(() => {
  // 移除窗口大小变化监听
  window.removeEventListener('resize', handleResize)
  // 销毁所有图表实例
  destroyAllCharts()
})
</script>

<style scoped>
/* 管理员容器 */
.admin-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 顶部导航栏 */
.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
  background: #fff;
  border-bottom: 1px solid #e0e0e0;
  padding: 0 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  z-index: 100;
  transition: all 0.3s ease;
}

.admin-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #409eff;
  letter-spacing: 0.5px;
  background: linear-gradient(135deg, #409eff 0%, #667eea 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info span {
  color: #606266;
  font-weight: 500;
  font-size: 14px;
}

.logout-btn {
  color: #909399;
  transition: all 0.3s ease;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
}

.logout-btn:hover {
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.1);
  transform: translateY(-1px);
}

/* 主体内容区 */
.admin-main {
  display: flex;
  flex: 1;
  overflow: hidden;
  background: #f5f7fa;
}

/* 左侧菜单栏 */
.admin-sidebar {
  width: 240px;
  background: #fff;
  border-right: 1px solid #e0e0e0;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.06);
  overflow-y: auto;
  transition: all 0.3s ease;
}

.sidebar-menu {
  height: 100%;
  border-right: none;
  background: transparent;
}

.sidebar-menu .el-menu-item {
  border-radius: 8px;
  margin: 8px 12px;
  height: 50px;
  line-height: 50px;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
}

.sidebar-menu .el-menu-item:hover {
  background: rgba(64, 158, 255, 0.08);
  color: #409eff;
}

.sidebar-menu .el-menu-item.is-active {
  background: rgba(64, 158, 255, 0.12);
  color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.sidebar-menu .el-menu-item .el-icon {
  margin-right: 14px;
  font-size: 18px;
  color: #909399;
  transition: all 0.3s ease;
}

.sidebar-menu .el-menu-item:hover .el-icon {
  color: #409eff;
}

.sidebar-menu .el-menu-item.is-active .el-icon {
  color: #409eff;
}

/* 右侧内容区 */
.admin-content {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
  background: #f8fafc;
}

/* 内容头部 */
.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.content-title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.5px;
}

.content-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

/* 内容主体 */
.content-body {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.content-body:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

/* 按钮样式优化 */
:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  letter-spacing: 0.3px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 10px 20px;
  font-size: 14px;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #409eff 0%, #667eea 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.2);
  background: linear-gradient(135deg, #667eea 0%, #409eff 100%);
}

:deep(.el-button--success) {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.15);
}

:deep(.el-button--success:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(103, 194, 58, 0.2);
}

:deep(.el-button--danger) {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.15);
}

:deep(.el-button--danger:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(245, 108, 108, 0.2);
}

:deep(.el-button--info) {
  background: linear-gradient(135deg, #909399 0%, #a6a9ad 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(144, 147, 153, 0.15);
}

:deep(.el-button--info:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(144, 147, 153, 0.2);
}

/* 仪表盘统计卡片 */
.dashboard-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #409eff 0%, #667eea 100%);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #c6e2ff;
}

.stat-card:hover::before {
  transform: scaleX(1);
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.stat-info {
  flex: 1;
}

.stat-label {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #909399;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.stat-value {
  margin: 0 0 16px 0;
  font-size: 36px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

.stat-change {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #67c23a;
  padding: 4px 8px;
  border-radius: 4px;
  background: rgba(103, 194, 58, 0.08);
}

.stat-change::before {
  content: '↑';
  font-size: 12px;
}

.stat-change.negative {
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.08);
}

.stat-change.negative::before {
  content: '↓';
}

/* 统计卡片图标 */
.stat-icon {
  width: 72px;
  height: 72px;
  border-radius: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 32px;
  background: linear-gradient(135deg, #ecf5ff 0%, #e0f2fe 100%);
  color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  transition: all 0.3s ease;
}

.stat-card:nth-child(2) .stat-icon {
  background: linear-gradient(135deg, #f0f9eb 0%, #dcfce7 100%);
  color: #67c23a;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.15);
}

.stat-card:nth-child(3) .stat-icon {
  background: linear-gradient(135deg, #fef0f0 0%, #fee2e2 100%);
  color: #f56c6c;
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.15);
}

.stat-card:nth-child(4) .stat-icon {
  background: linear-gradient(135deg, #fdf6ec 0%, #fef3c7 100%);
  color: #e6a23c;
  box-shadow: 0 4px 12px rgba(230, 162, 60, 0.15);
}

.stat-card:hover .stat-icon {
  transform: scale(1.1) rotate(5deg);
}

/* 图表区域 */
.dashboard-charts {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-top: 32px;
}

.chart-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.chart-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #c6e2ff;
}

.chart-card :deep(.el-card__header) {
  background: #fafafa;
  border-bottom: 1px solid #ebeef5;
  padding: 20px 24px;
  border-radius: 12px 12px 0 0;
}

.chart-card :deep(.el-card__body) {
  padding: 24px;
  border-radius: 0 0 12px 12px;
}

.card-header-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #303133;
  font-size: 18px;
  letter-spacing: 0.3px;
}

.chart-content {
  height: 360px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.chart-container {
  width: 100%;
  height: 100%;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .dashboard-charts {
    grid-template-columns: 1fr;
  }
  
  .dashboard-stats {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  }
}

@media (max-width: 768px) {
  .admin-sidebar {
    width: 200px;
  }
  
  .dashboard-stats {
    grid-template-columns: 1fr;
  }
  
  .admin-title {
    font-size: 18px;
  }
  
  .content-title {
    font-size: 20px;
  }
  
  .content-body {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .admin-sidebar {
    width: 100%;
    position: absolute;
    z-index: 1000;
    transform: translateX(-100%);
  }
  
  .admin-sidebar.open {
    transform: translateX(0);
  }
  
  .admin-content {
    padding: 16px;
  }
}
</style>