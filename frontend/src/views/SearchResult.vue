<template>
  <div class="search-result">
    <h2>搜索结果</h2>
    
    <!-- 搜索条件 -->
    <div class="search-condition">
      <el-input
        v-model="keyword"
        placeholder="请输入搜索关键词"
        clearable
        @keyup.enter="handleSearch"
        prefix-icon="Search"
        class="search-input"
      />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>
    
    <!-- 搜索结果统计 -->
    <div class="result-count">
      找到 {{ total }} 本相关书籍
    </div>
    
    <!-- 书籍列表 -->
    <div class="book-list">
      <el-card v-for="book in books" :key="book.id" shadow="hover">
        <div class="book-item" @click="goToBookDetail(book.id)">
          <img v-if="book.imageUrl" :src="book.imageUrl" class="book-cover" :alt="book.name">
          <div v-else class="book-cover">{{ book.name }}</div>
          <div class="book-info">
            <h3 class="book-name">{{ book.name }}</h3>
            <p class="book-author">{{ book.author }}</p>
            <p class="book-price">¥{{ book.price }}</p>
          </div>
        </div>
      </el-card>
      
      <!-- 空状态 -->
      <div v-if="books.length === 0" class="empty-state">
        <el-empty description="暂无搜索结果" />
      </div>
    </div>
    
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
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 搜索关键词
const keyword = ref('')

// 书籍列表
const books = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 初始化
onMounted(() => {
  // 从URL参数获取搜索关键词
  const searchKeyword = route.query.keyword
  if (searchKeyword) {
    keyword.value = searchKeyword
    handleSearch()
  }
})

// 处理搜索
const handleSearch = async () => {
  if (!keyword.value.trim()) {
    return
  }
  
  // 重置页码
  currentPage.value = 1
  
  // 更新URL
  router.push({
    path: '/search',
    query: { keyword: keyword.value }
  })
  
  // 获取搜索结果
  await getSearchResult()
}

// 获取搜索结果
  const getSearchResult = async () => {
    try {
      const result = await axios.get('/api/book/query', {
        params: {
          currentPage: currentPage.value,
          pageSize: pageSize.value,
          categoryId: 0, // 全部分类
          keyword: keyword.value
        }
      })
      if (result && result.success) {
        books.value = result.data.records
        total.value = result.data.totalRow
      }
    } catch (error) {
      console.error('获取搜索结果失败:', error)
      ElMessage.error('获取搜索结果失败: ' + (error.response?.data?.message || error.message))
    }
  }

// 跳转到书籍详情
const goToBookDetail = (bookId) => {
  router.push(`/book/${bookId}`)
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getSearchResult()
}

// 当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current
  getSearchResult()
}
</script>

<style scoped>
.search-result {
  max-width: 1440px;
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

/* 搜索增强区域 */
.search-condition {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 40px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
}

:deep(.search-input .el-input__wrapper) {
  border-radius: 99px;
  padding: 8px 24px;
  background: var(--bg-secondary);
  box-shadow: none;
  border: 1px solid var(--border-light);
  height: 56px;
  transition: all 0.3s;
}

:deep(.search-input .el-input__wrapper:hover) {
  border-color: var(--primary-light);
}

:deep(.search-input .el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px var(--primary-light);
}

.search-condition .el-button {
  height: 56px;
  padding: 0 32px;
  border-radius: 99px;
  font-weight: 700;
  background: var(--primary-gradient);
  border: none;
  box-shadow: var(--shadow-primary);
}

/* 结果统计 */
.result-count {
  font-size: 1rem;
  color: var(--text-secondary);
  font-weight: 600;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-light);
}

/* 网格布局 */
.book-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 32px;
}

:deep(.el-card) {
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-card:hover) {
  transform: translateY(-12px);
  box-shadow: var(--shadow-xl);
  border-color: var(--primary-light);
}

.book-item {
  cursor: pointer;
}

.book-cover {
  width: 100%;
  aspect-ratio: 3/4;
  object-fit: cover;
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-placeholder);
  font-weight: 600;
  text-align: center;
  padding: 24px;
}

.book-info {
  padding: 24px;
}

.book-name {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  font-size: 1rem;
  color: var(--text-secondary);
  margin-bottom: 20px;
}

.book-price {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--primary-color);
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
  .book-list {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
  }
}
</style>