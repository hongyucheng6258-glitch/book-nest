<template>
  <div class="admin-users">
    <h2>用户管理</h2>
    
    <!-- 搜索和筛选区域 -->
    <div class="search-filter">
      <el-input
        v-model="searchQuery"
        placeholder="请输入用户名搜索"
        clearable
        style="width: 300px; margin-right: 10px;"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select
        v-model="privilegeFilter"
        placeholder="权限筛选"
        clearable
        style="width: 150px; margin-right: 10px;"
      >
        <el-option label="管理员" :value="1" />
        <el-option label="普通用户" :value="0" />
      </el-select>
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
      <el-button @click="resetSearch">
        <el-icon><Refresh /></el-icon>
        重置
      </el-button>
      <el-button type="success" @click="openAddUserDialog">
        <el-icon><Plus /></el-icon>
        添加用户
      </el-button>
    </div>

    <!-- 添加用户对话框 -->
    <el-dialog
      v-model="addUserDialogVisible"
      title="添加用户"
      width="500px"
      destroy-on-close
    >
      <el-form
        :model="addUserForm"
        :rules="addUserRules"
        ref="addUserFormRef"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="addUserForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="addUserForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="权限" prop="privilege">
          <el-select v-model="addUserForm.privilege" placeholder="请选择权限">
            <el-option label="管理员" :value="1" />
            <el-option label="普通用户" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addUserDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddUser">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 编辑用户对话框 -->
    <el-dialog
      v-model="editUserDialogVisible"
      title="编辑用户"
      width="500px"
      destroy-on-close
    >
      <el-form
        :model="editUserForm"
        :rules="editUserRules"
        ref="editUserFormRef"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editUserForm.username" placeholder="请输入用户名" disabled />
        </el-form-item>
        <el-form-item label="新密码" prop="password" required="false">
          <el-input v-model="editUserForm.password" type="password" placeholder="请输入新密码（不填则不修改）" />
        </el-form-item>
        <el-form-item label="权限" prop="privilege">
          <el-select v-model="editUserForm.privilege" placeholder="请选择权限">
            <el-option label="管理员" :value="1" />
            <el-option label="普通用户" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editUserDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleEditUser">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 用户列表表格 -->
    <el-table
      v-loading="loading"
      :data="users"
      style="width: 100%; margin-top: 20px;"
    >
      <el-table-column prop="id" label="用户ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="privilege" label="权限" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.privilege === 1 ? 'primary' : 'success'">
            {{ scope.row.privilege === 1 ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" @click="openEditUserDialog(scope.row)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button type="danger" size="small" @click="handleDeleteUser(scope.row)">
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../../utils/axios'

// 用户列表数据
const users = ref([])
const loading = ref(false)
const searchQuery = ref('')
const privilegeFilter = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 添加用户相关数据
const addUserDialogVisible = ref(false)
const addUserFormRef = ref(null)
const addUserForm = reactive({
  username: '',
  password: '',
  privilege: 0
})
const addUserRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  privilege: [
    { required: true, message: '请选择权限', trigger: 'change' }
  ]
})

// 编辑用户相关数据
const editUserDialogVisible = ref(false)
const editUserFormRef = ref(null)
const editUserForm = reactive({
  id: 0,
  username: '',
  password: '',
  privilege: 0
})
const editUserRules = reactive({
  password: [
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  privilege: [
    { required: true, message: '请选择权限', trigger: 'change' }
  ]
})

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    const result = await axios.get('/api/admin/users', {
      params: {
        currentPage: currentPage.value,
        pageSize: pageSize.value,
        searchQuery: searchQuery.value,
        privilege: privilegeFilter.value
      }
    })
    
    // 处理后端返回的分页数据
    if (result && result.success) {
      const pageData = result.data
      if (pageData && Array.isArray(pageData.records)) {
        users.value = pageData.records
        total.value = pageData.totalRow || users.value.length
      } else {
        // 兼容非分页格式
        users.value = Array.isArray(pageData) ? pageData : []
        total.value = users.value.length
      }
      ElMessage.success('获取用户列表成功')
    } else {
      users.value = []
      total.value = 0
      ElMessage.error('获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    users.value = []
    total.value = 0
    ElMessage.error('获取用户列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 打开添加用户对话框
const openAddUserDialog = () => {
  addUserDialogVisible.value = true
  // 重置表单
  addUserForm.username = ''
  addUserForm.password = ''
  addUserForm.privilege = 0
  // 重置表单验证
  if (addUserFormRef.value) {
    addUserFormRef.value.resetFields()
  }
}

// 添加用户
const handleAddUser = async () => {
  if (!addUserFormRef.value) return
  
  try {
    await addUserFormRef.value.validate()
    
    const response = await axios.post('/api/admin/users', addUserForm)
    if (response && response.success) {
      ElMessage.success('添加用户成功')
      addUserDialogVisible.value = false
      fetchUsers() // 刷新用户列表
    } else {
      ElMessage.error('添加用户失败')
    }
  } catch (error) {
    console.error('添加用户失败:', error)
    if (error.name === 'Error') {
      // 表单验证失败，不显示错误信息
      return
    }
    ElMessage.error('添加用户失败: ' + (error.response?.data?.message || error.message))
  }
}

// 打开编辑用户对话框
const openEditUserDialog = (row) => {
  editUserDialogVisible.value = true
  // 设置表单数据
  editUserForm.id = row.id
  editUserForm.username = row.username
  editUserForm.privilege = row.privilege
  editUserForm.password = '' // 清空密码，用户不填则不修改
  // 重置表单验证
  if (editUserFormRef.value) {
    editUserFormRef.value.resetFields()
  }
}

// 编辑用户
const handleEditUser = async () => {
  if (!editUserFormRef.value) return
  
  try {
    await editUserFormRef.value.validate()
    
    // 构建请求数据，只包含需要修改的字段
    const updateData = {
      id: editUserForm.id,
      privilege: editUserForm.privilege
    }
    
    // 如果用户输入了密码，则包含密码字段
    if (editUserForm.password) {
      updateData.password = editUserForm.password
    }
    
    const response = await axios.put('/api/admin/users', updateData)
    if (response && response.success) {
      ElMessage.success('编辑用户成功')
      editUserDialogVisible.value = false
      fetchUsers() // 刷新用户列表
    } else {
      ElMessage.error('编辑用户失败')
    }
  } catch (error) {
    console.error('编辑用户失败:', error)
    if (error.name === 'Error') {
      // 表单验证失败，不显示错误信息
      return
    }
    ElMessage.error('编辑用户失败: ' + (error.response?.data?.message || error.message))
  }
}

// 删除用户
const handleDeleteUser = async (row) => {
  try {
    // 保护admin用户，不允许删除
    if (row.id === 1 && row.username === 'admin') {
      ElMessage.warning('admin用户为系统默认管理员，不允许删除')
      return
    }
    
    await ElMessageBox.confirm(
      `确定要删除用户"${row.username}"吗？删除后不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await axios.delete(`/api/admin/users/${row.id}`)
    if (response && response.success) {
      ElMessage.success('删除用户成功')
      fetchUsers() // 刷新用户列表
    } else {
      ElMessage.error('删除用户失败')
    }
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消操作
      return
    }
    console.error('删除用户失败:', error)
    ElMessage.error('删除用户失败: ' + (error.response?.data?.message || error.message))
  }
}

// 搜索用户
const handleSearch = () => {
  currentPage.value = 1 // 搜索时重置到第一页
  fetchUsers()
}

// 重置搜索
const resetSearch = () => {
  searchQuery.value = ''
  privilegeFilter.value = null
  currentPage.value = 1 // 重置时回到第一页
  fetchUsers()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1 // 改变分页大小时回到第一页
  fetchUsers()
}

// 分页当前页变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchUsers()
}

// 初始化
onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.admin-users {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-filter {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>