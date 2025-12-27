import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),

  getters: {
    // 计算登录状态
    isLogin: (state) => {
      console.log('userStore.isLogin getter called')
      return !!state.token && !!state.userInfo
    }
  },

  actions: {
    // 初始化userStore
    init() {
      console.log('userStore.init called')
      try {
        const userInfoStr = localStorage.getItem('userInfo')
        if (userInfoStr) {
          const parsedUserInfo = JSON.parse(userInfoStr)

          // 修复旧的头像URL，将http://localhost:8088替换为相对路径/
          if (parsedUserInfo.avatar && parsedUserInfo.avatar.startsWith('http://localhost:8088')) {
            parsedUserInfo.avatar = parsedUserInfo.avatar.replace('http://localhost:8088', '')
            // 更新localStorage中的用户信息
            localStorage.setItem('userInfo', JSON.stringify(parsedUserInfo))
          }

          if (JSON.stringify(this.userInfo) !== JSON.stringify(parsedUserInfo)) {
            console.log('userStore.init: updating userInfo')
            this.userInfo = parsedUserInfo
          } else {
            console.log('userStore.init: userInfo already up to date')
          }
        }
      } catch (error) {
        console.error('初始化userStore失败:', error)
        this.userInfo = null
      }
    },

    // 登录
    login(token, userInfo) {
      this.token = token
      this.userInfo = userInfo

      // 保存到localStorage
      localStorage.setItem('token', token)
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },

    // 登出
    logout() {
      this.token = ''
      this.userInfo = null

      // 清除localStorage
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    },

    // 更新用户信息
    updateUserInfo(userInfo) {
      // 提取id以外的所有字段，防止userInfo中的pk(id)覆盖外部授权的id
      const { id, ...otherInfo } = userInfo

      this.userInfo = {
        ...this.userInfo, // 保留原有字段 (包含正确的登录ID)
        ...otherInfo       // 更新新字段
      }
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
    }
  }
})