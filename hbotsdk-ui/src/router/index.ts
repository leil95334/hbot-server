import { createRouter, createWebHistory } from 'vue-router'
import Chat from '@/views/Chat.vue'
import Ocr from '@/views/Ocr.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/ocr',
    },
    {
      path: '/chat',
      name: 'Chat',
      component: Chat,
    },
    {
      path: '/ocr',
      name: 'Ocr',
      component: Ocr,
    },
  ],
})

export default router
