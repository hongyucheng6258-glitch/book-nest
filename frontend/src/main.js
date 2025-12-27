import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import pinia from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'
import { useUserStore } from './store/userStore'

const app = createApp(App)

// 使用插件
app.use(router)
app.use(pinia)
app.use(ElementPlus)

// 挂载后初始化userStore，避免在组件渲染过程中修改状态
app.mount('#app')
const userStore = useUserStore()
userStore.init()