import { defineStore } from 'pinia'
import axios from '../utils/axios'

export const useCartStore = defineStore('cart', {
  state: () => ({
    cartItems: [],
    totalPrice: 0
  }),
  
  getters: {
    // 计算购物车商品总数
    totalQuantity: (state) => {
      console.log('cartStore.totalQuantity getter called')
      return state.cartItems.reduce((total, item) => total + item.quantity, 0)
    }
  },
  
  actions: {
    // 获取购物车列表
  async fetchCartList(userId) {
    console.log('cartStore.fetchCartList called with userId:', userId)
    try {
      // 检查userId是否存在
      if (!userId) {
        console.error('获取购物车列表失败: userId is required')
        if (this.cartItems.length > 0) {
          console.log('cartStore.fetchCartList: setting cartItems to empty array')
          this.cartItems = []
          this.calculateTotalPrice()
        }
        return
      }
      
      const result = await axios.get('/api/cart/list', {
        params: { userId }
      })
      // 确保result存在且success为true
      if (result && result.success) {
        // 实际购物车数据在result.data中
        const newCartItems = result.data || []
        if (JSON.stringify(this.cartItems) !== JSON.stringify(newCartItems)) {
          console.log('cartStore.fetchCartList: updating cartItems')
          this.cartItems = newCartItems
          this.calculateTotalPrice()
          console.log('购物车列表获取成功:', this.cartItems)
        } else {
          console.log('cartStore.fetchCartList: cartItems already up to date')
        }
      } else {
        // 响应无效时使用空数组
        if (this.cartItems.length > 0) {
          console.log('cartStore.fetchCartList: setting cartItems to empty array due to invalid response')
          this.cartItems = []
          this.calculateTotalPrice()
        }
        console.error('无效的API响应:', result)
      }
    } catch (error) {
      console.error('获取购物车列表失败:', error)
      // 出错时也确保cartItems是数组
      if (this.cartItems.length > 0) {
        console.log('cartStore.fetchCartList: setting cartItems to empty array due to error')
        this.cartItems = []
        this.calculateTotalPrice()
      }
    }
  },
    
    // 添加商品到购物车
    async addToCart(userId, bookId, quantity = 1) {
      try {
        // 检查userId和bookId是否存在
        if (!userId) {
          console.error('添加到购物车失败: userId is required')
          return
        }
        
        if (!bookId) {
          console.error('添加到购物车失败: bookId is required')
          return
        }
        
        const result = await axios.post('/api/cart/add', null, {
          params: { userId, bookId, quantity }
        })
        // 检查结果是否成功
        if (result && result.success) {
          // 重新获取购物车列表
          await this.fetchCartList(userId)
          console.log('商品添加到购物车成功')
        } else {
          console.error('添加到购物车失败:', result.message || '未知错误')
        }
      } catch (error) {
        console.error('添加到购物车失败:', error)
      }
    },
    
    // 更新购物车商品数量
    async updateQuantity(id, quantity) {
      try {
        await axios.put('/api/cart/update', null, {
          params: { id, quantity }
        })
        // 重新获取购物车列表，确保本地状态和数据库状态一致
        if (this.cartItems.length > 0) {
          const userId = this.cartItems[0].userId
          await this.fetchCartList(userId)
        }
      } catch (error) {
        console.error('更新购物车数量失败:', error)
      }
    },
    
    // 移除购物车商品
    async removeCartItem(id) {
      try {
        await axios.delete(`/api/cart/remove/${id}`)
        // 从本地状态中移除
        this.cartItems = this.cartItems.filter(item => item.id !== id)
        this.calculateTotalPrice()
      } catch (error) {
        console.error('移除购物车商品失败:', error)
      }
    },
    
    // 计算购物车总价
    calculateTotalPrice() {
      // 确保cartItems是数组，防止reduce失败
      this.totalPrice = (this.cartItems || []).reduce((total, item) => {
        // 确保item和item.book存在，防止访问属性失败
        if (item && item.book) {
          return total + (item.book.price * item.quantity)
        }
        return total
      }, 0)
    },
    
    // 清空购物车
    async clearCart(userId) {
      try {
        this.cartItems = []
        this.calculateTotalPrice()
        console.log('购物车已清空')
      } catch (error) {
        console.error('清空购物车失败:', error)
      }
    }
  }
})