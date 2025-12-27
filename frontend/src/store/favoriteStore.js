import { defineStore } from 'pinia'
import axios from '../utils/axios'

export const useFavoriteStore = defineStore('favorite', {
  state: () => ({
    favoriteBooks: [], // 收藏的书籍列表
    loading: false, // 加载状态
    error: null // 错误信息
  }),

  getters: {
    // 检查书籍是否已收藏
    isFavorited: (state) => (bookId) => {
      return state.favoriteBooks.some(book => book.id === bookId)
    }
  },

  actions: {
    // 获取用户收藏列表
    async fetchFavoriteBooks(userId) {
      if (!userId) return
      
      this.loading = true
      this.error = null
      
      try {
        const response = await axios.get('/api/favorite/list', {
          params: {
            userId
          }
        })
        
        if (response && response.success) {
          this.favoriteBooks = response.data || []
        } else {
          this.error = response?.message || '获取收藏列表失败'
        }
      } catch (error) {
        this.error = '网络错误，获取收藏列表失败'
        console.error('获取收藏列表失败:', error)
      } finally {
        this.loading = false
      }
    },

    // 添加收藏
    async addToFavorite(userId, bookId) {
      if (!userId || !bookId) return
      
      this.loading = true
      this.error = null
      
      try {
        const response = await axios.post('/api/favorite/add', null, {
          params: {
            userId,
            bookId
          }
        })
        
        if (response && response.success) {
          // 更新收藏列表
          await this.fetchFavoriteBooks(userId)
          return { success: true, message: response.message || '收藏成功' }
        } else {
          return { success: false, message: response?.message || '收藏失败' }
        }
      } catch (error) {
        console.error('添加收藏失败:', error)
        return { success: false, message: '网络错误，收藏失败' }
      } finally {
        this.loading = false
      }
    },

    // 取消收藏
    async removeFromFavorite(userId, bookId) {
      if (!userId || !bookId) return
      
      this.loading = true
      this.error = null
      
      try {
        const response = await axios.delete('/api/favorite/remove', {
          params: {
            userId,
            bookId
          }
        })
        
        if (response && response.success) {
          // 更新收藏列表
          await this.fetchFavoriteBooks(userId)
          return { success: true, message: response.message || '取消收藏成功' }
        } else {
          return { success: false, message: response?.message || '取消收藏失败' }
        }
      } catch (error) {
        console.error('取消收藏失败:', error)
        return { success: false, message: '网络错误，取消收藏失败' }
      } finally {
        this.loading = false
      }
    },

    // 检查书籍是否已收藏
    async checkFavoriteStatus(userId, bookId) {
      if (!userId || !bookId) return false
      
      try {
        const response = await axios.get('/api/favorite/check', {
          params: {
            userId,
            bookId
          }
        })
        
        return response && response.success && response.data
      } catch (error) {
        console.error('检查收藏状态失败:', error)
        return false
      }
    },

    // 清除收藏数据（退出登录时调用）
    clearFavorites() {
      this.favoriteBooks = []
      this.loading = false
      this.error = null
    }
  }
})