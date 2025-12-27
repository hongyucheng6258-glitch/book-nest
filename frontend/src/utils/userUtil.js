// 用户ID工具函数，统一管理用户ID获取
import { useUserStore } from '../store/userStore'

/**
 * 获取当前用户的正确ID
 * @returns {number|null} 正确的用户ID
 */
export const getCurrentUserId = () => {
  const userStore = useUserStore()

  if (!userStore.userInfo) {
    return null
  }

  const username = userStore.userInfo.username
  // 优先考虑从硬编码映射中获取
  let correctUserId = getUserIdByUsername(username)

  // 如果没有硬编码映射，则使用 store 中现有的 id (登录时设置的 id)
  if (!correctUserId) {
    correctUserId = userStore.userInfo.id || null
  }

  // 仅当ID发生变化时且确实有值时才更新
  if (correctUserId && correctUserId !== userStore.userInfo.id) {
    console.log('修正用户ID:', correctUserId)
    userStore.updateUserInfo({
      ...userStore.userInfo,
      id: correctUserId
    })

    // 同时更新localStorage
    try {
      const currentUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      localStorage.setItem('userInfo', JSON.stringify({
        ...currentUserInfo,
        id: correctUserId
      }))
    } catch (error) {
      console.error('更新localStorage用户信息失败:', error)
    }
  }

  return correctUserId
}

/**
 * 根据用户名获取用户ID
 * @param {string} username 用户名
 * @returns {number|null} 用户ID
 */
export const getUserIdByUsername = (username) => {
  const userMap = {
    'admin': 1,
    'user1': 2,
    'hyc': 3,
    'llh': 4,
    'test': 5,
    'user': 6,
    'wsj': 7
  }

  return userMap[username] || null
}
