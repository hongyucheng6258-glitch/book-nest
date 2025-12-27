<template>
  <div class="admin-categories">
    <h2>分类管理</h2>
    
    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>
        添加分类
      </el-button>
    </div>
    
    <!-- 分类列表 -->
    <el-table
      v-loading="loading"
      :data="categories"
      border
      style="width: 100%"
      class="categories-table"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" min-width="200">
        <template #default="scope">
          <div class="category-name">
            {{ scope.row.name }}
            <el-tag v-if="scope.row.id === 1" type="info" size="small" style="margin-left: 8px;">
              系统默认
            </el-tag>
          </div>
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
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑分类' : '添加分类'"
      width="500px"
      destroy-on-close
    >
      <el-form
        :model="formData"
        ref="formRef"
        label-width="120px"
        :rules="rules"
      >
        <el-form-item label="分类ID" prop="id">
          <el-input-number
            v-model="formData.id"
            :min="1"
            :precision="0"
            placeholder="请输入分类ID"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
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
import { ref, reactive, onMounted } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../../utils/axios'

// 响应式数据
const categories = ref([])
const loading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const originalId = ref(0) // 保存原分类ID，用于比较是否修改了ID

// 表单数据
const formData = reactive({
  id: 0,
  name: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 1, max: 50, message: '分类名称长度在 1 到 50 个字符', trigger: 'blur' }
  ]
}

// 获取分类列表
const getCategories = async () => {
  loading.value = true
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
    loading.value = false
  }
}

// 打开添加对话框
const openAddDialog = () => {
  isEdit.value = false
  // 重置表单
  formRef.value?.resetFields()
  // 重置表单数据
  Object.assign(formData, {
    id: 0,
    name: ''
  })
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (row) => {
  isEdit.value = true
  // 直接设置id为数字类型，不使用Object.assign
  formData.id = parseInt(row.id)
  formData.name = row.name
  originalId.value = parseInt(row.id) // 保存原分类ID
  dialogVisible.value = true
}

// 表单提交前检查
const beforeSubmit = () => {
  // 保护"全部"分类，不允许修改其ID
  if (isEdit.value && originalId.value === 1 && formData.id !== 1) {
    ElMessage.warning('"全部"分类为系统默认分类，不允许修改其ID')
    return false
  }
  return true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    // 表单提交前检查
    if (!beforeSubmit()) {
      return
    }
    
    await formRef.value.validate()
    
    let result
    if (isEdit.value) {
      // 编辑分类
      if (formData.id === originalId.value) {
        // 只修改了分类名称，调用update API
        result = await axios.put('/api/category/admin/update', formData)
      } else {
        // 修改了分类ID，调用updateId API
        result = await axios.put(`/api/category/admin/updateId/${originalId.value}`, formData)
      }
    } else {
      // 添加分类
      result = await axios.post('/api/category/admin/add', formData)
    }
    
    if (result && result.success) {
      ElMessage.success(isEdit.value ? '编辑分类成功' : '添加分类成功')
      dialogVisible.value = false
      getCategories() // 刷新列表
    } else {
      ElMessage.error(isEdit.value ? '编辑分类失败' : '添加分类失败')
    }
  } catch (error) {
    console.error('提交表单失败:', error)
    if (error.name === 'Error') {
      // 表单验证失败，不显示错误信息
      return
    }
    ElMessage.error(isEdit.value ? '编辑分类失败: ' + (error.response?.data?.message || error.message) : '添加分类失败: ' + (error.response?.data?.message || error.message))
  }
}

// 删除分类
const handleDelete = async (id) => {
  try {
    // 保护"全部"分类，不允许删除
    if (id === 1) {
      ElMessage.warning('"全部"分类为系统默认分类，不允许删除')
      return
    }
    
    await ElMessageBox.confirm(
      '确定要删除这个分类吗？删除后，该分类下的书籍将无法正常显示。',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const result = await axios.delete(`/api/category/admin/delete/${id}`)
    if (result && result.success) {
      ElMessage.success('删除分类成功')
      getCategories() // 刷新列表
    } else {
      ElMessage.error('删除分类失败')
    }
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消操作
      return
    }
    console.error('删除分类失败:', error)
    ElMessage.error('删除分类失败: ' + (error.response?.data?.message || error.message))
  }
}

// 初始化
onMounted(() => {
  getCategories()
})
</script>

<style scoped>
.admin-categories {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.action-bar {
  margin-bottom: 20px;
}

.categories-table {
  margin-bottom: 20px;
}

.btn-edit {
  margin-right: 10px;
}

.btn-delete {
  margin-right: 10px;
}
</style>