import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import './assets/main.css'
import App from './App.vue'
import SummaryView from './components/SummaryView.vue'
import CompareUpload from './components/CompareUpload.vue'
import CompareResult from './components/CompareResult.vue'

// 라우트 정의
const routes = [
  {
    path: '/',
    name: 'Summary',
    component: SummaryView
  },
  {
    path: '/compare',
    name: 'CompareUpload',
    component: CompareUpload
  },
  {
    path: '/compare/result/:id',
    name: 'CompareResult',
    component: CompareResult,
    props: true
  }
]

// 라우터 생성
const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)
app.use(router)
app.mount('#app')
