<template>
  <div class="admin-books">
    <h2>书籍管理</h2>
    
    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>
        添加书籍
      </el-button>
      <el-input
        v-model="searchQuery"
        placeholder="搜索书名、作者或出版社"
        clearable
        class="search-input"
        @keyup.enter="handleSearch"
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>
    
    <!-- 书籍列表 -->
    <el-table
      v-loading="loading"
      :data="books"
      border
      style="width: 100%"
      class="books-table"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="书名" min-width="200" />
      <el-table-column prop="author" label="作者" width="150" />
      <el-table-column prop="publisher" label="出版社" width="200" />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="scope">¥{{ scope.row.price }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100">
        <template #default="scope">
          <span :class="{'stock-low': scope.row.stock <= 0}">{{ scope.row.stock }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="soldCount" label="已卖" width="100">
        <template #default="scope">{{ scope.row.soldCount || 0 }}</template>
      </el-table-column>
      <el-table-column label="分类" width="150">
        <template #default="scope">
          {{ categoryMap.get(scope.row.categoryId) || '未知分类' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button
            type="primary"
            size="small"
            @click="openEditDialog(scope.row)"
            class="btn-edit"
          >
            <el-icon><Edit /></el-icon>
            编辑
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
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑书籍' : '添加书籍'"
      width="600px"
      destroy-on-close
    >
      <el-form
        :model="formData"
        ref="formRef"
        label-width="120px"
        :rules="rules"
      >
        <el-form-item label="书名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="formData.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="formData.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="formData.price"
            :min="0.01"
            :step="0.01"
            :precision="2"
            placeholder="请输入价格"
          />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select
            v-model="formData.categoryId"
            placeholder="请选择分类"
            style="width: 100%"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="图片URL" prop="imageUrl">
          <el-input v-model="formData.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="书籍描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入书籍描述"
          />
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <el-input-number
            v-model="formData.stock"
            :min="0"
            :step="1"
            placeholder="请输入库存数量"
          />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { Plus, Edit, Delete, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../../utils/axios'

// 响应式数据
const books = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchQuery = ref('')
const categories = ref([]) // 分类列表
const loadingCategories = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

// 表单数据
const formData = reactive({
  id: '',
  name: '',
  author: '',
  publisher: '',
  price: 0,
  stock: 0,
  soldCount: 0,
  categoryId: 1,
  imageUrl: '',
  description: ''
})

// 分类名称映射
const categoryMap = computed(() => {
  const map = new Map()
  categories.value.forEach(category => {
    map.set(category.id, category.name)
  })
  return map
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入书名', trigger: 'blur' },
    { min: 1, max: 100, message: '书名长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入作者', trigger: 'blur' },
    { min: 1, max: 50, message: '作者长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  publisher: [
    { required: true, message: '请输入出版社', trigger: 'blur' },
    { min: 1, max: 100, message: '出版社长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '价格必须大于 0', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请输入分类ID', trigger: 'blur' },
    { type: 'number', min: 1, message: '分类ID必须大于 0', trigger: 'blur' }
  ],
  stock: [
    { type: 'number', min: 0, message: '库存数量不能小于 0', trigger: 'blur' }
  ]
}

// 获取分类列表
const getCategories = async () => {
  loadingCategories.value = true
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
  } finally {
    loadingCategories.value = false
  }
}

// 获取书籍列表
const getBooks = async () => {
  loading.value = true
  try {
    const params = {
      currentPage: currentPage.value,
      pageSize: pageSize.value
    }
    if (searchQuery.value) {
      params.keyword = searchQuery.value
    }
    const result = await axios.get('/api/book/query', { params })
    if (result && result.success) {
      // 确保每本书籍都有stock和soldCount字段
      const booksData = result.data.records || []
      books.value = booksData.map(book => ({
        ...book,
        stock: book.stock || 0, // 确保stock字段存在，默认为0
        soldCount: book.soldCount || 0 // 确保soldCount字段存在，默认为0
      }))
      total.value = result.data.totalRow || 0
    } else {
      books.value = []
      total.value = 0
      ElMessage.warning('获取书籍列表失败')
    }
  } catch (error) {
    console.error('获取书籍列表失败:', error)
    books.value = []
    total.value = 0
    ElMessage.error('获取书籍列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getBooks()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getBooks()
}

// 当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current
  getBooks()
}

// 打开添加对话框
const openAddDialog = () => {
  isEdit.value = false
  // 重置表单
  formRef.value?.resetFields()
  // 重置表单数据
  Object.assign(formData, {
    id: '',
    name: '',
    author: '',
    publisher: '',
    price: 0,
    stock: 0,
    soldCount: 0,
    categoryId: 1,
    imageUrl: '',
    description: ''
  })
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (row) => {
  isEdit.value = true
  // 复制数据到表单
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    let result
    if (isEdit.value) {
      // 编辑书籍
      result = await axios.put('/api/book/admin/update', formData)
    } else {
      // 添加书籍
      result = await axios.post('/api/book/admin/add', formData)
    }
    
    if (result && result.success) {
      ElMessage.success(isEdit.value ? '编辑书籍成功' : '添加书籍成功')
      dialogVisible.value = false
      getBooks() // 刷新列表
    } else {
      ElMessage.error(isEdit.value ? '编辑书籍失败' : '添加书籍失败')
    }
  } catch (error) {
    console.error('提交表单失败:', error)
    if (error.name === 'Error') {
      // 表单验证失败，不显示错误信息
      return
    }
    ElMessage.error(isEdit.value ? '编辑书籍失败: ' + (error.response?.data?.message || error.message) : '添加书籍失败: ' + (error.response?.data?.message || error.message))
  }
}

// 删除书籍
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这本书吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const result = await axios.delete(`/api/book/admin/delete/${id}`)
    if (result && result.success) {
      ElMessage.success('删除书籍成功')
      getBooks() // 刷新列表
    } else {
      ElMessage.error('删除书籍失败')
    }
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消操作
      return
    }
    console.error('删除书籍失败:', error)
    ElMessage.error('删除书籍失败: ' + (error.response?.data?.message || error.message))
  }
}

// 初始化
onMounted(async () => {
  // 先获取分类列表，再获取书籍列表
  await getCategories()
  await getBooks()
})
</script>

<style scoped>
.admin-books {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.books-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.btn-edit {
  margin-right: 10px;
}

.btn-delete {
  margin-right: 10px;
}

.stock-low {
  color: #f56c6c;
  font-weight: bold;
}
</style>