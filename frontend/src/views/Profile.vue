<template>
  <div class="profile">
    <h2>个人中心</h2>
    
    <el-row :gutter="20">
      <!-- 左侧菜单 -->
      <el-col :span="6">
        <el-menu :default-active="activeMenu" class="profile-menu">
  <el-menu-item index="1" @click="activeMenu = '1'">
    <el-icon><user /></el-icon>
    <template #title>个人信息</template>
  </el-menu-item>
  <el-menu-item index="2" @click="activeMenu = '2'">
    <el-icon><document /></el-icon>
    <template #title>我的订单</template>
  </el-menu-item>
  <el-menu-item index="7" @click="goToHistory">
    <el-icon><Clock /></el-icon>
    <template #title>阅读历史</template>
  </el-menu-item>
  <el-menu-item index="8" @click="goToNotifications">
    <el-icon><Bell /></el-icon>
    <template #title>我的通知</template>
  </el-menu-item>
  <el-menu-item index="4" @click="goToFavorite">
    <el-icon><star /></el-icon>
    <template #title>我的收藏</template>
  </el-menu-item>
  <el-menu-item index="9" @click="activeMenu = '9'">
    <el-icon><User /></el-icon>
    <template #title>我的好友</template>
  </el-menu-item>
  <el-menu-item index="5" @click="activeMenu = '5'">
    <el-icon><location /></el-icon>
    <template #title>地址管理</template>
  </el-menu-item>
  <el-menu-item index="6" @click="activeMenu = '6'">
    <el-icon><credit-card /></el-icon>
    <template #title>支付方式</template>
  </el-menu-item>
  <el-menu-item index="3" @click="activeMenu = '3'">
    <el-icon><setting /></el-icon>
    <template #title>账户设置</template>
  </el-menu-item>
</el-menu>
      </el-col>
      
      <!-- 右侧内容 -->
      <el-col :span="18">
        <el-card shadow="hover">
          <!-- 个人信息 -->
          <div v-if="activeMenu === '1'" class="profile-info">
            <h3>个人信息</h3>
            <el-form :model="userInfo" label-width="80px">
              <el-form-item label="用户名">
                <el-input v-model="userInfo.username" disabled />
              </el-form-item>
              <el-form-item label="昵称">
                <el-input v-model="userInfo.nickname" />
              </el-form-item>
              <el-form-item label="性别">
                <el-radio-group v-model="userInfo.gender">
                  <el-radio :label="0">未知</el-radio>
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="生日">
                <el-date-picker v-model="userInfo.birthday" type="date" placeholder="选择生日" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userInfo.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userInfo.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="updateUserInfo">保存修改</el-button>
              </el-form-item>
            </el-form>
          </div>
          
          <!-- 我的订单 -->
          <div v-else-if="activeMenu === '2'" class="profile-order">
            <h3>我的订单</h3>
            <el-button type="primary" @click="goToOrder">查看全部订单</el-button>
          </div>
          
          <!-- 账户设置 -->
          <div v-else-if="activeMenu === '3'" class="profile-setting">
            <h3>账户设置</h3>
            <el-tabs v-model="activeTab">
              <el-tab-pane label="修改密码">
                <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
                  <el-form-item label="旧密码" prop="oldPassword">
                    <el-input v-model="passwordForm.oldPassword" type="password" show-password />
                  </el-form-item>
                  <el-form-item label="新密码" prop="newPassword">
                    <el-input v-model="passwordForm.newPassword" type="password" show-password />
                  </el-form-item>
                  <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="updatePassword">修改密码</el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>
              <el-tab-pane label="头像设置">
                <div class="avatar-setting">
                  <el-upload
                    class="avatar-uploader"
                    :action="uploadAction"
                    :show-file-list="false"
                    :on-success="handleAvatarSuccess"
                    :before-upload="beforeAvatarUpload"
                    :data="uploadData"
                  >
                    <img v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon"><plus /></el-icon>
                  </el-upload>
                  <div class="avatar-tip">支持 JPG、PNG 格式，不超过 2MB</div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
          
          <!-- 地址管理 -->
          <div v-else-if="activeMenu === '5'" class="profile-address">
            <h3>地址管理</h3>
            <el-button type="primary" @click="showAddAddressDialog">添加地址</el-button>
            <div class="address-list">
              <el-card v-for="address in addresses" :key="address.id" shadow="hover" class="address-item">
                <div class="address-header">
                  <div class="address-name">{{ address.name }} {{ address.phone }}</div>
                  <el-button v-if="address.isDefault !== 1" type="text" @click="setDefaultAddress(address.id)">设为默认</el-button>
                </div>
                <div class="address-detail">
                  {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detail }}
                </div>
                <div class="address-actions">
                  <el-button type="primary" @click="showEditAddressDialog(address)">编辑</el-button>
                  <el-button type="danger" @click="deleteAddress(address.id)">删除</el-button>
                </div>
              </el-card>
              <div v-if="addresses.length === 0" class="empty-state">
                <el-empty description="暂无地址" />
              </div>
            </div>
            
            <!-- 添加地址对话框 -->
            <el-dialog
              v-model="addAddressDialogVisible"
              title="添加地址"
              width="500px"
              @close="closeDialog"
            >
              <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="100px">
                <el-form-item label="收货人" prop="name">
                  <el-input v-model="addressForm.name" placeholder="请输入收货人姓名" />
                </el-form-item>
                <el-form-item label="手机号码" prop="phone">
                  <el-input v-model="addressForm.phone" placeholder="请输入手机号码" maxlength="11" />
                </el-form-item>
                <el-form-item label="省份" prop="province">
                  <el-input v-model="addressForm.province" placeholder="请输入省份" />
                </el-form-item>
                <el-form-item label="城市" prop="city">
                  <el-input v-model="addressForm.city" placeholder="请输入城市" />
                </el-form-item>
                <el-form-item label="区县" prop="district">
                  <el-input v-model="addressForm.district" placeholder="请输入区县" />
                </el-form-item>
                <el-form-item label="详细地址" prop="detail">
                  <el-input v-model="addressForm.detail" placeholder="请输入详细地址" type="textarea" :rows="2" />
                </el-form-item>
                <el-form-item>
                  <el-checkbox v-model="addressForm.isDefault" true-label="1" false-label="0">设为默认地址</el-checkbox>
                </el-form-item>
              </el-form>
              <template #footer>
                <span class="dialog-footer">
                  <el-button @click="closeDialog">取消</el-button>
                  <el-button type="primary" @click="saveAddress">保存</el-button>
                </span>
              </template>
            </el-dialog>
            
            <!-- 编辑地址对话框 -->
            <el-dialog
              v-model="editAddressDialogVisible"
              title="编辑地址"
              width="500px"
              @close="closeDialog"
            >
              <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="100px">
                <el-form-item label="收货人" prop="name">
                  <el-input v-model="addressForm.name" placeholder="请输入收货人姓名" />
                </el-form-item>
                <el-form-item label="手机号码" prop="phone">
                  <el-input v-model="addressForm.phone" placeholder="请输入手机号码" maxlength="11" />
                </el-form-item>
                <el-form-item label="省份" prop="province">
                  <el-input v-model="addressForm.province" placeholder="请输入省份" />
                </el-form-item>
                <el-form-item label="城市" prop="city">
                  <el-input v-model="addressForm.city" placeholder="请输入城市" />
                </el-form-item>
                <el-form-item label="区县" prop="district">
                  <el-input v-model="addressForm.district" placeholder="请输入区县" />
                </el-form-item>
                <el-form-item label="详细地址" prop="detail">
                  <el-input v-model="addressForm.detail" placeholder="请输入详细地址" type="textarea" :rows="2" />
                </el-form-item>
                <el-form-item>
                  <el-checkbox v-model="addressForm.isDefault" true-label="1" false-label="0">设为默认地址</el-checkbox>
                </el-form-item>
              </el-form>
              <template #footer>
                <span class="dialog-footer">
                  <el-button @click="closeDialog">取消</el-button>
                  <el-button type="primary" @click="saveAddress">保存</el-button>
                </span>
              </template>
            </el-dialog>
          </div>
          
          <!-- 支付方式管理 -->
          <div v-else-if="activeMenu === '6'" class="profile-payment">
            <h3>支付方式管理</h3>
            <el-button type="primary" @click="showAddPaymentDialog">添加支付方式</el-button>
            <div class="payment-list">
              <el-card v-for="payment in payments" :key="payment.id" shadow="hover" class="payment-item">
                <div class="payment-header">
                  <div class="payment-name">{{ payment.name }}</div>
                  <el-tag v-if="payment.isDefault === 1" type="success">默认</el-tag>
                </div>
                <div class="payment-detail">
                  {{ payment.cardType === 1 ? '信用卡' : '借记卡' }} **** **** **** {{ payment.cardNumber.slice(-4) }}
                </div>
                <div class="payment-actions">
                  <el-button v-if="payment.isDefault !== 1" type="primary" @click="setDefaultPayment(payment.id)">设为默认</el-button>
                  <el-button type="danger" @click="deletePayment(payment.id)">删除</el-button>
                </div>
              </el-card>
              <div v-if="payments.length === 0" class="empty-state">
                <el-empty description="暂无支付方式" />
              </div>
            </div>
          </div>
          
          <!-- 好友列表 -->
          <div v-else-if="activeMenu === '9'" class="profile-friends">
            <h3>我的好友</h3>
            <el-button type="primary" @click="goToChat">去聊天</el-button>
            <div class="friends-list">
              <el-card v-for="friend in friends" :key="friend.id" shadow="hover" class="friend-item">
                <div class="friend-header">
                  <div class="friend-info">
                    <div class="friend-avatar">
                      <el-avatar size="large" :src="friend.friendAvatar">{{ friend.friendUsername?.charAt(0) || '友' }}</el-avatar>
                    </div>
                    <div class="friend-name">{{ friend.friendUsername || '未知用户' }}</div>
                  </div>
                  <el-tag v-if="friend.isOnline" type="success">在线</el-tag>
                  <el-tag v-else type="info">离线</el-tag>
                </div>
                <div class="friend-actions">
                  <el-button type="primary" @click="goToChatWithFriend(friend)">开始聊天</el-button>
                  <el-button type="danger" @click="deleteFriend(friend.id)">删除好友</el-button>
                </div>
              </el-card>
              <div v-if="friends.length === 0" class="empty-state">
                <el-empty description="暂无好友" />
                <el-button type="primary" @click="goToChat">去添加好友</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/userStore'
import axios from '../utils/axios'
import {
  User,
  Document,
  Setting,
  Plus,
  Star,
  Location,
  CreditCard,
  Clock,
  Bell
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getCurrentUserId } from '../utils/userUtil'

const router = useRouter()
const userStore = useUserStore()

// 活跃菜单
const activeMenu = ref('1')

// 活跃标签
const activeTab = ref(0)

// 获取正确的用户ID
const correctUserId = getCurrentUserId()

// 用户信息
const userInfo = reactive({
  id: correctUserId || '',
  userId: correctUserId || 0,
  username: userStore.userInfo?.username || '',
  privilege: userStore.userInfo?.privilege || '0',
  nickname: '',
  gender: 0,
  birthday: null,
  phone: '',
  email: '',
  avatar: ''
})

// 头像上传配置
const uploadAction = ref('/api/user/avatar')
const uploadData = computed(() => {
  return {
    userId: getCurrentUserId() || 0
  }
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码表单引用
const passwordFormRef = ref(null)

// 密码验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请确认新密码'))
        } else if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}



// 获取用户详情
const getUserInfo = async () => {
  try {
    console.log('开始获取用户详情')
    const currentUserId = getCurrentUserId()
    if (currentUserId) {
      console.log('当前用户ID:', currentUserId)
      
      const response = await axios.get('/api/user/info', {
        params: { userId: currentUserId }
      })
      
      console.log('getUserInfo响应:', response)
      console.log('response.data:', response.data)
      
      if (response && response.success) {
        console.log('获取用户详情成功')
        const userData = response.data
        
        // 明确更新每个字段，确保数据正确赋值
        
        // 更新昵称
        if (userData.nickname) {
          userInfo.nickname = userData.nickname
        }
        
        // 更新性别
        if (userData.gender !== undefined && userData.gender !== null) {
          userInfo.gender = parseInt(userData.gender)
        }
        
        // 处理并更新生日
        if (userData.birthday) {
          let birthday = userData.birthday
          console.log('原始生日数据:', birthday, '类型:', typeof birthday)
          
          // 如果是数组，只取第一个值
          if (Array.isArray(birthday)) {
            birthday = birthday[0]
          }
          
          // 如果是字符串且包含逗号，只取第一个值
          if (typeof birthday === 'string' && birthday.includes(',')) {
            birthday = birthday.split(',')[0]
          }
          
          // 确保是YYYY-MM-DD格式的字符串
          if (typeof birthday === 'string' && birthday.length === 10) {
            // 已经是YYYY-MM-DD格式，直接使用
            userInfo.birthday = birthday
            console.log('使用原始生日字符串:', birthday)
          } else {
            // 转换为日期对象，再转换为YYYY-MM-DD格式
            const dateObj = new Date(birthday)
            if (!isNaN(dateObj.getTime())) {
              userInfo.birthday = dateObj.toISOString().split('T')[0]
              console.log('转换后的生日:', userInfo.birthday)
            } else {
              console.error('无效的生日数据:', birthday)
            }
          }
        } else {
          // 如果没有生日，设置为null
          userInfo.birthday = null
        }
        
        // 更新手机号
        if (userData.phone) {
          userInfo.phone = userData.phone
        }
        
        // 更新邮箱
        if (userData.email) {
          userInfo.email = userData.email
        }
        
        // 更新头像
        if (userData.avatar) {
          userInfo.avatar = userData.avatar
        }
        
        console.log('更新后的userInfo:', userInfo)
        // 先更新store，再更新本地状态
        userStore.updateUserInfo(userData)
        console.log('更新后的userStore.userInfo:', userStore.userInfo)
        // 确保本地userInfo与store保持同步
        Object.assign(userInfo, userData)
      } else {
        console.error('获取用户详情失败:', response?.message || '未知错误')
      }
    } else {
      console.error('获取用户详情失败: 无效的用户ID')
    }
  } catch (error) {
    console.error('获取用户详情异常:', error)
    console.error('error.response:', error.response)
  }
}

// 更新用户信息
const updateUserInfo = async () => {
  try {
    console.log('=== 开始更新用户信息 ===')
    // 创建不包含username字段的用户信息对象
    const userInfoToUpdate = {
      userId: getCurrentUserId() || 0,
      nickname: userInfo.nickname,
      avatar: userInfo.avatar,
      gender: userInfo.gender,
      birthday: userInfo.birthday,
      phone: userInfo.phone,
      email: userInfo.email
    }
    
    console.log('原始用户信息:', JSON.stringify(userInfoToUpdate))
    
    // 确保性别是数字类型，并且在0-2之间
    let gender = userInfoToUpdate.gender
    if (typeof gender === 'string') {
      gender = parseInt(gender)
    }
    // 确保性别值在有效范围内
    gender = isNaN(gender) ? 0 : Math.max(0, Math.min(2, gender))
    userInfoToUpdate.gender = gender
    
    // 确保生日格式正确，转换为YYYY-MM-DD格式
    let birthday = userInfoToUpdate.birthday
    if (birthday) {
      if (Array.isArray(birthday)) {
        birthday = birthday[0]
      }
      if (typeof birthday === 'string' && birthday.includes(',')) {
        birthday = birthday.split(',')[0]
      }
      // 确保生日是字符串格式，并且格式正确
      if (birthday instanceof Date) {
        birthday = birthday.toISOString().split('T')[0]
      } else if (typeof birthday === 'object') {
        // 处理日期对象
        console.log('处理日期对象:', birthday)
        birthday = new Date(birthday).toISOString().split('T')[0]
      } else if (typeof birthday === 'string') {
        // 确保格式为YYYY-MM-DD
        const dateObj = new Date(birthday)
        if (!isNaN(dateObj.getTime())) {
          birthday = dateObj.toISOString().split('T')[0]
        } else {
          // 如果日期无效，设为null
          console.log('无效日期，设为null:', birthday)
          birthday = null
        }
      }
      userInfoToUpdate.birthday = birthday
    } else {
      userInfoToUpdate.birthday = null
    }
    
    console.log('处理后要发送的用户信息:', JSON.stringify(userInfoToUpdate))
    
    // 添加请求拦截器，查看完整的请求信息
    const originalRequest = axios.interceptors.request.use(config => {
      console.log('请求配置:', JSON.stringify(config))
      return config
    }, error => {
      console.error('请求错误:', error)
      return Promise.reject(error)
    })
    
    const result = await axios.put('/api/user/info', userInfoToUpdate)
    
    // 移除请求拦截器
    axios.interceptors.request.eject(originalRequest)
    
    console.log('更新请求结果:', JSON.stringify(result))
    if (result && result.success) {
      ElMessage.success('更新成功')
      // 重新获取用户信息，确保页面显示最新数据
      await getUserInfo()
      // getUserInfo()已经会更新userStore，无需再次调用
    } else {
      console.error('更新失败，响应数据:', JSON.stringify(result))
      ElMessage.error('更新失败: ' + (result?.message || '未知错误'))
    }
    console.log('=== 更新用户信息结束 ===')
  } catch (error) {
    console.error('更新用户信息失败，错误详情:', error)
    console.error('错误响应:', JSON.stringify(error.response))
    console.error('错误信息:', error.message)
    console.error('错误栈:', error.stack)
    // 显示详细的错误信息，帮助调试
    if (error.response?.data?.message) {
      ElMessage.error('更新失败: ' + error.response.data.message)
    } else if (error.message) {
      ElMessage.error('更新失败: ' + error.message)
    } else {
      ElMessage.error('更新失败: 网络错误')
    }
    console.log('=== 更新用户信息结束 ===')
  }
}



// 头像上传成功处理
const handleAvatarSuccess = (response) => {
  if (response.success) {
    // 更新本地用户信息
    userInfo.avatar = response.data.avatar
    
    // 更新store中的用户信息，确保首页头像也能更新
    userStore.updateUserInfo({
      ...userStore.userInfo,
      avatar: response.data.avatar
    })
    
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error('头像上传失败')
  }
}

// 头像上传前验证
const beforeAvatarUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPGOrPNG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPGOrPNG && isLt2M
}

// 修改密码
const updatePassword = async () => {
  try {
    // 表单验证
    await passwordFormRef.value.validate()
    
    // 调用后端修改密码API
    const response = await axios.post('/api/auth/change-password', passwordForm)
    
    if (response.success) {
      // 密码修改成功，重置表单
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      // 提示用户
      ElMessage.success('密码修改成功')
    } else {
      // 密码修改失败
      ElMessage.error('密码修改失败：' + response.message)
    }
  } catch (error) {
    // 处理表单验证错误
    if (error instanceof Error && error.message.includes('Error in mounted hook')) {
      // 忽略mounted钩子中的错误
      return
    }
    // 处理API错误
    ElMessage.error('密码修改失败：' + (error.response?.data?.message || error.message))
  }
}

// 跳转到订单列表
const goToOrder = () => {
  router.push('/order')
}

// 跳转到收藏页面
const goToFavorite = () => {
  router.push('/favorite')
}

// 跳转到阅读历史页面
const goToHistory = () => {
  router.push('/history')
}

// 跳转到通知页面
const goToNotifications = () => {
  router.push('/notifications')
}

// 地址管理相关
const addresses = ref([])
const getAddresses = async () => {
  try {
    const result = await axios.get('/api/address/list', {
      params: { userId: userStore.userInfo.id }
    })
    if (result && result.success) {
      addresses.value = result.data
    }
  } catch (error) {
    console.error('获取地址列表失败:', error)
  }
}

// 地址表单
const addressForm = reactive({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: 0
})

// 表单验证规则
const addressRules = {
  name: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  province: [
    { required: true, message: '请选择省份', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请选择城市', trigger: 'blur' }
  ],
  district: [
    { required: true, message: '请选择区县', trigger: 'blur' }
  ],
  detail: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { min: 5, max: 100, message: '详细地址长度在 5 到 100 个字符', trigger: 'blur' }
  ]
}

// 对话框控制
const addAddressDialogVisible = ref(false)
const editAddressDialogVisible = ref(false)
const currentAddressId = ref(null)

// 表单引用
const addressFormRef = ref(null)

// 显示添加地址对话框
const showAddAddressDialog = () => {
  // 重置表单
  Object.assign(addressForm, {
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: 0
  })
  currentAddressId.value = null
  addAddressDialogVisible.value = true
}

// 显示编辑地址对话框
const showEditAddressDialog = (address) => {
  // 填充表单数据
  Object.assign(addressForm, {
    name: address.name,
    phone: address.phone,
    province: address.province,
    city: address.city,
    district: address.district,
    detail: address.detail,
    isDefault: address.isDefault
  })
  currentAddressId.value = address.id
  editAddressDialogVisible.value = true
}

// 关闭对话框
const closeDialog = () => {
  addAddressDialogVisible.value = false
  editAddressDialogVisible.value = false
  if (addressFormRef.value) {
    addressFormRef.value.resetFields()
  }
}

// 保存地址
const saveAddress = async () => {
  try {
    // 表单验证
    await addressFormRef.value.validate()
    
    let result
    if (currentAddressId.value) {
      // 更新地址
      result = await axios.put(`/api/address/update/${currentAddressId.value}`, {
        ...addressForm,
        userId: userStore.userInfo.id
      })
    } else {
      // 添加地址
      result = await axios.post('/api/address/add', {
        ...addressForm,
        userId: userStore.userInfo.id
      })
    }
    
    if (result && result.success) {
      // 刷新地址列表
      getAddresses()
      // 关闭对话框
      closeDialog()
      // 提示成功
      ElMessage.success(currentAddressId.value ? '更新地址成功' : '添加地址成功')
    } else {
      ElMessage.error(currentAddressId.value ? '更新地址失败' : '添加地址失败')
    }
  } catch (error) {
    console.error('保存地址失败:', error)
    if (error instanceof Error && error.message.includes('Validation failed')) {
      // 表单验证失败，已由Element Plus处理
      return
    }
    ElMessage.error('保存地址失败，请重试')
  }
}

const setDefaultAddress = async (addressId) => {
  try {
    const result = await axios.put('/api/address/set-default', null, {
      params: {
        userId: userStore.userInfo.id,
        addressId: addressId
      }
    })
    if (result && result.success) {
      getAddresses()
      ElMessage.success('设置默认地址成功')
    }
  } catch (error) {
    console.error('设置默认地址失败:', error)
    ElMessage.error('设置默认地址失败')
  }
}

const deleteAddress = async (addressId) => {
  if (confirm('确定要删除这个地址吗？')) {
    try {
      const result = await axios.delete(`/api/address/${addressId}`)
      if (result && result.success) {
        getAddresses()
        ElMessage.success('删除地址成功')
      }
    } catch (error) {
      console.error('删除地址失败:', error)
      ElMessage.error('删除地址失败')
    }
  }
}

// 支付方式管理相关
const payments = ref([])
const getPayments = async () => {
  try {
    const result = await axios.get('/api/payment-method/list', {
      params: { userId: userStore.userInfo.id }
    })
    if (result && result.success) {
      payments.value = result.data
    }
  } catch (error) {
    console.error('获取支付方式列表失败:', error)
  }
}

const showAddPaymentDialog = () => {
  ElMessage.info('添加支付方式功能开发中')
}

const setDefaultPayment = async (paymentId) => {
  try {
    const result = await axios.put('/api/payment-method/set-default', null, {
      params: {
        userId: userStore.userInfo.id,
        paymentMethodId: paymentId
      }
    })
    if (result && result.success) {
      getPayments()
      ElMessage.success('设置默认支付方式成功')
    }
  } catch (error) {
    console.error('设置默认支付方式失败:', error)
    ElMessage.error('设置默认支付方式失败')
  }
}

const deletePayment = async (paymentId) => {
  if (confirm('确定要删除这个支付方式吗？')) {
    try {
      const result = await axios.delete(`/api/payment-method/${paymentId}`)
      if (result && result.success) {
        getPayments()
        ElMessage.success('删除支付方式成功')
      }
    } catch (error) {
      console.error('删除支付方式失败:', error)
      ElMessage.error('删除支付方式失败')
    }
  }
}

// 好友列表相关
const friends = ref([])
const getFriends = async () => {
  try {
    const result = await axios.get(`/api/friend/list/${getCurrentUserId() || 0}`)
    if (result && result.success) {
      const friendList = result.data || []
      // 处理好友数据，添加显示字段
      friends.value = friendList.map(friend => {
        // 确定好友用户名和头像
        const isUser1 = friend.user1Id === getCurrentUserId()
        const friendUsername = isUser1 ? friend.user2?.username : friend.user1?.username
        let friendAvatar = null
        
        // 获取好友头像
        if (isUser1) {
          if (friend.user2?.userInfo?.avatar) {
            friendAvatar = friend.user2.userInfo.avatar
          } else if (friend.avatar2) {
            friendAvatar = friend.avatar2
          }
        } else {
          if (friend.user1?.userInfo?.avatar) {
            friendAvatar = friend.user1.userInfo.avatar
          } else if (friend.avatar1) {
            friendAvatar = friend.avatar1
          }
        }
        
        return {
          ...friend,
          friendUsername,
          friendAvatar,
          isOnline: Math.random() > 0.5, // 模拟在线状态
        }
      })
    }
  } catch (error) {
    console.error('获取好友列表失败:', error)
    ElMessage.error('获取好友列表失败')
  }
}

// 跳转到聊天页面
const goToChat = () => {
  router.push('/chat')
}

// 跳转到与特定好友聊天
const goToChatWithFriend = (friend) => {
  router.push({ path: '/chat', query: { friendId: friend.id } })
}

// 删除好友
const deleteFriend = async (friendId) => {
  if (confirm('确定要删除这个好友吗？')) {
    try {
      const result = await axios.delete('/api/friend/delete', {
        params: {
          userId: getCurrentUserId() || 0,
          friendId: friendId
        }
      })
      if (result && result.success) {
        getFriends()
        ElMessage.success('删除好友成功')
      }
    } catch (error) {
      console.error('删除好友失败:', error)
      ElMessage.error('删除好友失败')
    }
  }
}

// 组件挂载时获取数据
onMounted(() => {
  console.log('Profile组件挂载成功')
  console.log('当前用户信息:', userStore.userInfo)
  // 从userStore获取最新的用户信息
  updateUserInfoFromStore()
  // 使用setTimeout延迟获取数据，避免在组件渲染过程中修改状态
  setTimeout(() => {
    getUserInfo()
    getAddresses()
    getPayments()
    getFriends()
  }, 0)
})

// 从userStore更新userInfo
const updateUserInfoFromStore = () => {
  if (userStore.userInfo) {
    userInfo.id = userStore.userInfo.id || ''
    userInfo.userId = userStore.userInfo.id || 0
    userInfo.username = userStore.userInfo.username || ''
    userInfo.privilege = userStore.userInfo.privilege || '0'
    // 只更新非空的昵称、性别、生日、手机和邮箱
    if (userStore.userInfo.nickname) {
      userInfo.nickname = userStore.userInfo.nickname
    }
    if (userStore.userInfo.gender !== undefined) {
      userInfo.gender = userStore.userInfo.gender
    }
    if (userStore.userInfo.birthday) {
      userInfo.birthday = userStore.userInfo.birthday
    }
    if (userStore.userInfo.phone) {
      userInfo.phone = userStore.userInfo.phone
    }
    if (userStore.userInfo.email) {
      userInfo.email = userStore.userInfo.email
    }
    if (userStore.userInfo.avatar) {
      userInfo.avatar = userStore.userInfo.avatar
    }
    console.log('从userStore更新后的userInfo:', userInfo)
  }
}

// 监听userStore.userInfo的变化，确保userInfo对象始终与userStore中的userInfo保持同步
watch(() => userStore.userInfo, (newUserInfo, oldUserInfo) => {
  if (newUserInfo) {
    console.log('userStore.userInfo变化:', newUserInfo)
    // 只在userInfo真正变化时更新，避免无限循环
    if (JSON.stringify(newUserInfo) !== JSON.stringify(oldUserInfo)) {
      console.log('updateUserInfoFromStore called')
      updateUserInfoFromStore()
    } else {
      console.log('userInfo unchanged, skipping update')
    }
  }
}, { deep: true })

// 组件卸载时清理
onUnmounted(() => {
  console.log('Profile组件卸载成功')
})

// 监听活跃菜单变化
watch(activeMenu, (newMenu) => {
  console.log('活跃菜单变化:', newMenu)
})
</script>

<style scoped>
.profile {
  max-width: 1440px;
  margin: 0 auto;
  padding: 40px 24px;
  min-height: 100vh;
  animation: fadeIn 0.8s ease-out;
}

/* 页面标题 */
h2 {
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 40px;
  letter-spacing: -1px;
}

/* 布局调整 */
:deep(.el-row) {
  display: flex;
  align-items: stretch;
}

/* 左侧菜单 */
.profile-menu {
  background: white;
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
  padding: 12px;
  height: auto;
}

:deep(.el-menu) {
  border-right: none !important;
}

:deep(.el-menu-item) {
  height: 56px;
  line-height: 56px;
  margin-bottom: 4px;
  border-radius: var(--radius-lg);
  font-size: 1rem;
  font-weight: 500;
  color: var(--text-regular);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-menu-item:hover) {
  background: var(--bg-secondary) !important;
  color: var(--primary-color) !important;
}

:deep(.el-menu-item.is-active) {
  background: var(--primary-light) !important;
  color: var(--primary-color) !important;
  font-weight: 700;
}

:deep(.el-menu-item .el-icon) {
  font-size: 1.25rem;
  margin-right: 12px;
  transition: transform 0.3s ease;
}

:deep(.el-menu-item.is-active .el-icon) {
  transform: scale(1.1);
}

/* 右侧内容卡片 */
:deep(.el-card) {
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-md);
  height: 100%;
}

/* 内容区域通用样式 */
.profile-info,
.profile-order,
.profile-setting,
.profile-address,
.profile-payment,
.profile-friends {
  padding: 12px;
}

/* 小标题 */
h3 {
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 32px;
  display: flex;
  align-items: center;
  gap: 12px;
}

h3::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--border-light);
  margin-left: 12px;
}

/* 表单定制 */
:deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--text-regular);
  padding-bottom: 8px;
}

:deep(.el-input__wrapper) {
  border-radius: var(--radius-md);
  box-shadow: 0 0 0 1px var(--border-light) inset !important;
  transition: all 0.2s ease;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--primary-color) inset !important;
}

/* 按钮美化 */
:deep(.el-button--primary) {
  background: var(--primary-gradient);
  border: none;
  font-weight: 600;
  padding: 12px 28px;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-primary);
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
  filter: brightness(1.1);
}

/* 头像上传美化 */
.avatar-setting {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  padding: 40px 0;
}

.avatar-uploader {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  border: 2px dashed var(--border-regular);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  overflow: hidden;
  background: var(--bg-secondary);
}

.avatar-uploader:hover {
  border-color: var(--primary-color);
  background: var(--primary-light);
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-uploader-icon {
  font-size: 2rem;
  color: var(--text-placeholder);
}

/* 列表网格系统 */
.address-list,
.payment-list,
.friends-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

/* 卡片项美化 */
.address-item,
.payment-item,
.friend-item {
  background: white;
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
  padding: 24px;
  transition: all 0.3s ease;
}

.address-item:hover,
.payment-item:hover,
.friend-item:hover {
  border-color: var(--primary-light);
  box-shadow: var(--shadow-lg);
  transform: translateY(-4px);
}

.address-header,
.payment-header,
.friend-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.address-name,
.payment-name,
.friend-name {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-primary);
}

.friend-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.address-detail {
  color: var(--text-regular);
  font-size: 0.95rem;
  line-height: 1.6;
  margin-bottom: 20px;
  min-height: 3.2em;
}

/* 好友列表细节 */
.friend-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

/* 响应式调整 */
@media (max-width: 992px) {
  :deep(.el-row) {
    flex-direction: column;
    gap: 24px;
  }
  
  :deep(.el-col) {
    width: 100% !important;
    max-width: 100% !important;
  }
}

@media (max-width: 768px) {
  .profile {
    padding: 20px 16px;
  }
  
  h2 {
    font-size: 1.75rem;
    margin-bottom: 24px;
  }
}

/* 其他 element-plus 覆盖 */
:deep(.el-tabs__item) {
  font-size: 1.1rem;
  font-weight: 600;
  padding: 0 24px;
}

:deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}
</style>