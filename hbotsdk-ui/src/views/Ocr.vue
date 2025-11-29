<template>
  <div class="ocr-view">
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

</style>
