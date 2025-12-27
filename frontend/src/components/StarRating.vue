<template>
  <div class="star-rating">
    <div class="star-container">
      <div class="stars">
        <span
          v-for="star in maxStars"
          :key="star"
          class="star"
          :class="{'star-active': star <= Math.round(displayRating), 'star-hover': star <= hoverRating }"
          @mouseenter="handleMouseEnter(star)"
          @mouseleave="handleMouseLeave"
          @click="handleStarClick(star)"
        >
          <el-icon><StarFilled /></el-icon>
        </span>
      </div>
      <div class="rating-text">{{ displayRating }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { StarFilled } from '@element-plus/icons-vue'
import axios from '../utils/axios'

// 组件属性
const props = defineProps({
  // 书籍ID
  bookId: {
    type: Number,
    required: true
  },
  // 当前用户ID
  userId: {
    type: Number,
    required: true
  },
  // 最大评分（默认5星）
  maxRating: {
    type: Number,
    default: 5
  },
  // 只读模式
  readonly: {
    type: Boolean,
    default: false
  }
})

// 组件事件
const emit = defineEmits(['rating-change'])

// 响应式数据
const userRating = ref(0) // 用户当前评分
const hoverRating = ref(0) // 悬停时显示的评分
const displayRating = ref(0) // 显示的评分
const isRating = ref(false) // 是否正在评分中

// 计算属性
const maxStars = computed(() => props.maxRating)

// 处理鼠标进入事件
const handleMouseEnter = (star) => {
  if (props.readonly) return
  hoverRating.value = star
}

// 处理鼠标离开事件
const handleMouseLeave = () => {
  if (props.readonly) return
  hoverRating.value = 0
}

// 处理星星点击事件
const handleStarClick = (star) => {
  if (props.readonly) return
  userRating.value = star
  displayRating.value = star // 更新显示评分，确保星星显示和文字显示同步
  emit('rating-change', star)
}

// 获取书籍平均评分
const fetchAverageRating = async () => {
  try {
    // 调用后端API获取书籍平均评分
    const response = await axios.get(`/api/rating/${props.bookId}`)
    if (response && response.success) {
      // 设置显示评分
      displayRating.value = response.data || 0
    }
  } catch (error) {
    console.error('获取书籍平均评分失败:', error)
    // 失败时设置默认值
    displayRating.value = 0
  }
}

// 获取用户对书籍的评分
const fetchUserRating = async () => {
  if (props.userId === 0) return // 未登录用户不获取评分
  
  try {
    // 调用后端API获取用户对该书籍的评分
    const response = await axios.get(`/api/rating/user/${props.bookId}`, {
      params: {
        userId: props.userId
      }
    })
    if (response && response.success) {
      // 设置用户评分
      userRating.value = response.data?.rating || 0
    }
  } catch (error) {
    console.error('获取用户评分失败:', error)
  }
}

// 监听书籍评分变化
const updateDisplayRating = async () => {
  await fetchAverageRating()
  await fetchUserRating()
}

// 监听书籍ID变化，重新获取评分
watch(() => props.bookId, () => {
  updateDisplayRating()
})

// 监听用户ID变化，重新获取用户评分
watch(() => props.userId, () => {
  fetchUserRating()
})

// 组件挂载时初始化评分
onMounted(() => {
  updateDisplayRating()
})
</script>

<style scoped>
.star-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 20px 0;
}

.star-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.stars {
  display: flex;
  gap: 5px;
  cursor: pointer;
}

.star {
  font-size: 24px;
  color: #e6e6e6;
  transition: all 0.3s ease;
}

.star-active {
  color: #f56c6c;
}

.star-hover {
  color: #f56c6c;
}

.rating-text {
  font-size: 16px;
  color: #606266;
  margin-top: 10px;
}
</style>
