<template>
  <div class="ocr-view">
    <button
        class="ocr-collapse-button"
        type="button"
        aria-label="返回弹层模式"
        @click="backToModal"
    >
      <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
           stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
           class="lucide lucide-minimize2-icon lucide-minimize-2 size-4">
        <polyline points="4 14 10 14 10 20"></polyline>
        <polyline points="20 10 14 10 14 4"></polyline>
        <line x1="14" x2="21" y1="10" y2="3"></line>
        <line x1="3" x2="10" y1="21" y2="14"></line>
      </svg>
    </button>
    <OcrPanel/>
  </div>
</template>

<script setup lang="ts">
import {useRouter} from 'vue-router'
import OcrPanel from '@/components/OcrPanel.vue'

const router = useRouter()

const backToModal = () => {
  router.push({path: '/chat', query: {ocrModal: '1'}})
}
</script>

<style scoped>
.ocr-view {
  width: 100%;
  height: 100%;
  position: relative;
}

.ocr-collapse-button {
  position: fixed;
  top: 28px;
  right: 28px;
  width: 40px;
  height: 40px;
  border-radius: 999px;
  border: none;
  background: rgba(255, 255, 255, 0.9);
  color: #0f172a;
  font-size: 22px;
  font-weight: 600;
  cursor: pointer;
  z-index: 2100;
}
</style>
