<template>
  <div class="ocr-page" ref="ocrPageRef">
    <div class="ocr-content">
      <section class="panel upload-panel">
        <header class="ocr-header">
          <div class="header-left">
            <div class="header-tabs">
              <button class="tab is-active">OCR 文字识别</button>
            </div>
          </div>
        </header>
        <div class="dropzone" :class="{ 'has-preview': !!capturedImage }">
          <template v-if="capturedImage">
            <img :src="capturedImage" alt="截图预览" class="dropzone-preview" />
          </template>
          <template v-else>
            <p class="dropzone-hint">拖放图片 / 粘贴图像</p>
            <p class="dropzone-sub">支持 JPG、PNG、WebP 等常见格式</p>
          </template>
        </div>

        <input
          ref="fileInputRef"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleFileChange"
        />

        <footer class="upload-footer">
          <div class="upload-footer-left">
            <button class="secondary-btn" @click="triggerFileSelect">
              选择图片
            </button>
            <button
              class="secondary-btn"
              :disabled="isCapturing"
              @click="handleScreenshotClick"
            >
              {{ screenshotButtonText }}
            </button>
          </div>
          <button class="secondary-btn clear-btn" @click="clearImage" :disabled="!capturedImage">
            清空
          </button>
        </footer>
      </section>

      <section class="panel result-panel">
        <header class="result-header">
          <div class="chip-group">
            <button class="chip is-active">文字识别</button>
            <button class="chip">表格识别</button>
            <button class="chip">公式识别</button>
          </div>
          <button class="language-select" type="button">
            <span>中文</span>
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M7 10l5 5 5-5H7z" fill="currentColor" />
            </svg>
          </button>
        </header>

        <div class="result-body">
          <div v-if="isProcessing" class="result-loading">
            <div class="loading-spinner"></div>
            <p>识别中，请稍候...</p>
          </div>
          <div v-else-if="displayResult" class="result-text-wrapper">
            <pre class="result-text">{{ displayResult }}</pre>
          </div>
          <div v-else-if="ocrError" class="result-error">
            <p>{{ ocrError }}</p>
          </div>
          <div v-else class="result-placeholder">
            <div class="placeholder-icon">Tt</div>
            <p>OCR 文字识别结果</p>
          </div>
        </div>

        <footer class="result-footer">
          <button
            class="secondary-btn audio-footer-btn"
            type="button"
            :disabled="!audioUrl"
            @click="handlePlayAudio"
          >
            <svg
              t="1763990440613"
              class="icon"
              viewBox="0 0 1024 1024"
              xmlns="http://www.w3.org/2000/svg"
              aria-hidden="true"
            >
              <path
                d="M872.802928 755.99406 872.864326 755.99406 872.864326 755.624646Z"
                fill="currentColor"
              ></path>
              <path
                d="M744.055658 192.799074c-4.814656-2.889817-9.601682-5.251607-15.442714-5.251607-14.262842 0-25.758664 11.559267-25.758664 25.805736 0 10.285251 6.088672 18.519796 14.6957 23.195282 94.679359 55.247278 158.344355 157.787676 158.344355 275.30416 0 117.424386-63.605643 219.931015-158.159136 275.18034-8.29492 4.538363-15.442714 13.050224-15.442714 23.583115 0 14.261818 11.559267 25.820062 25.791409 25.820062 5.716188 0 10.252505-2.202155 15.22475-5.063319 109.871363-64.133669 183.764304-183.143157 183.764304-319.520197C927.074995 375.785665 853.495186 257.010515 744.055658 192.799074z"
                fill="currentColor"
              ></path>
              <path
                d="M773.946432 511.867994c0-79.96524-43.344181-149.739373-107.821681-187.289594-2.920516-1.52268-9.785877-4.520967-14.603603-4.520967-14.325263 0-25.914206 11.589966-25.914206 25.89988 0 9.616008 5.096065 18.176988 12.865006 22.666232 49.839105 28.307719 83.45983 81.829703 83.45983 143.244448 0 62.472843-34.801621 116.817566-86.070284 144.750755-7.457856 4.538363-12.397355 12.803607-12.397355 22.188348 0 14.325263 11.588943 25.943882 25.882484 25.943882 6.090718 0.031722 13.33061-3.542686 13.33061-3.542686C729.048873 664.171772 773.946432 593.294514 773.946432 511.867994z"
                fill="currentColor"
              ></path>
              <path
                d="M541.3694 124.672464c-10.846022-5.219885-23.740704-3.790326-33.215496 3.712555-0.435928 0.358157-46.423309 36.914748-97.195669 74.296123-88.308255 65.081251-114.036219 75.925227-119.257128 77.649498l-110.6194 0c-0.63752 0-1.243317 0.062422-1.879813 0.093121l-56.504922 0c-14.231119 0-25.775037 11.543917-25.775037 25.775037l0 411.697573c0 14.261818 11.512195 25.761734 25.775037 25.761734l189.511191 0.027629c5.096065 1.865487 29.395494 13.0799 107.761306 76.999698 45.613874 37.162388 86.505189 73.485665 86.940095 73.829496 5.841032 5.218862 13.298887 7.92039 20.820188 7.92039 4.349051 0 8.729825-0.930185 12.862959-2.764973 11.277858-5.064342 18.517749-16.252149 18.517749-28.619828 0 0 0.031722-97.257068 0.031722-132.212184 0.808412-2.484587 1.213641-5.127787 1.213641-7.863085 0-2.792603-1.245364-578.026786-1.245364-578.026786C559.110459 140.892891 552.214399 129.924071 541.3694 124.672464zM508.308423 726.470653c0 1.494027-0.467651 94.617961-0.467651 94.617961-13.889335-11.745509-29.332049-24.64019-45.240367-37.507242-104.59008-84.702124-130.505309-91.816149-148.030451-91.816149-0.372483 0-0.683569 0.091074-1.025353 0.091074s-0.652869-0.091074-1.025353-0.091074L170.394297 691.765223c-18.037818 0-22.248723-5.128811-22.248723-23.246447L148.145573 352.559685c0-12.32163 1.461281-20.057825 16.298198-20.057825l128.065747 0c17.090237 0 43.315528-6.991228 157.787676-90.839915 20.383236-14.914688 40.330544-29.938869 57.544601-43.113937 0 0 0.373507 445.207781 0.467651 521.368368C507.779374 722.028481 508.308423 724.234729 508.308423 726.470653z"
                fill="currentColor"
              ></path>
            </svg>
            朗读
          </button>

          <div class="footer-actions">
            <button class="secondary-btn" :disabled="!displayResult" @click="copyResult">
              复制结果
            </button>
            <button class="primary-btn" :disabled="!displayResult" @click="openTranslate">
              前往翻译
            </button>
          </div>
        </footer>
      </section>
    </div>
    <div
      v-if="showSelectionOverlay"
      class="selection-overlay"
      @pointerdown="handlePointerDown"
      @pointermove="handlePointerMove"
      @pointerup="handlePointerUp"
      @pointercancel="handlePointerUp"
      @contextmenu.prevent="cancelSelection"
    >
      <div class="selection-tip">拖动选择截图区域，按 Esc 取消</div>
      <div
        v-show="selectionRect.width && selectionRect.height"
        class="selection-box"
        :style="selectionBoxStyle"
      ></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import html2canvas from 'html2canvas'
import { streamChat } from '@/api/chat'

const OCR_APP_ID = import.meta.env.VITE_APP_ID as string | undefined
const USER_ID = import.meta.env.VITE_USER_ID || 'default-user'
const OCR_PROMPT = '提取这张图片上的文字'

const isCapturing = ref(false)
const ocrPageRef = ref<HTMLElement | null>(null)
const isSelecting = ref(false)
const showSelectionOverlay = ref(false)
const isPointerActive = ref(false)
const capturedImage = ref<string | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)
const isProcessing = ref(false)
const ocrError = ref<string | null>(null)
const rawResult = ref('')
const audioUrl = ref('')
const isPlayingAudio = ref(false)
let audioInstance: HTMLAudioElement | null = null
// 去除换行功能已移除，保留结果原始格式
const streamAbort = ref<(() => void) | null>(null)

const selectionRect = reactive({
  left: 0,
  top: 0,
  width: 0,
  height: 0,
})
const selectionStart = reactive({
  x: 0,
  y: 0,
})

const selectionBoxStyle = computed(() => ({
  left: `${selectionRect.left}px`,
  top: `${selectionRect.top}px`,
  width: `${selectionRect.width}px`,
  height: `${selectionRect.height}px`,
}))

const displayResult = computed(() => {
  return rawResult.value
})

const screenshotButtonText = computed(() => {
  if (isCapturing.value) return '截图中...'
  if (isSelecting.value) return '退出截取'
  return '屏幕截图'
})

const resetSelectionRect = () => {
  selectionRect.left = 0
  selectionRect.top = 0
  selectionRect.width = 0
  selectionRect.height = 0
  selectionStart.x = 0
  selectionStart.y = 0
}

const cancelSelection = () => {
  isSelecting.value = false
  showSelectionOverlay.value = false
  isPointerActive.value = false
  resetSelectionRect()
}

const clearImage = () => {
  capturedImage.value = null
  rawResult.value = ''
  audioUrl.value = ''
  isPlayingAudio.value = false
  if (audioInstance) {
    audioInstance.pause()
    audioInstance.currentTime = 0
    audioInstance = null
  }
  ocrError.value = null
  streamAbort.value?.()
  streamAbort.value = null
  isProcessing.value = false
}

const triggerFileSelect = () => {
  fileInputRef.value?.click()
}

const fileToDataUrl = (file: File) => {
  return new Promise<string>((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result as string)
    reader.onerror = () => reject(reader.error)
    reader.readAsDataURL(file)
  })
}

const dataUrlToFile = (dataUrl: string, fileName: string) => {
  const [meta, content] = dataUrl.split(',')
  const mimeMatch = meta?.match(/data:(.*?);/)
  const mime = mimeMatch ? mimeMatch[1] : 'image/png'
  const binary = atob(content || '')
  const len = binary.length
  const u8arr = new Uint8Array(len)
  for (let i = 0; i < len; i += 1) {
    u8arr[i] = binary.charCodeAt(i)
  }
  return new File([u8arr], fileName, { type: mime })
}

const handleFileChange = async (event: Event) => {
  const target = event.target as HTMLInputElement | null
  const file = target?.files?.[0]
  if (!file) return
  try {
    capturedImage.value = await fileToDataUrl(file)
    await startRecognition(file)
  } catch (error) {
    console.error('读取图片失败', error)
    ocrError.value = '读取图片失败，请重试'
  } finally {
    if (target) {
      target.value = ''
    }
  }
}

const startRecognition = async (file: File) => {
  if (!OCR_APP_ID) {
    ocrError.value = '缺少 OCR 应用配置，请检查环境变量'
    return
  }

  streamAbort.value?.()
  rawResult.value = ''
  ocrError.value = null
  isProcessing.value = true

  try {
    streamAbort.value = await streamChat(
      {
        appId: OCR_APP_ID,
        query: OCR_PROMPT,
        userId: USER_ID,
        file,
      },
      (event) => {
        const isChunkEvent = event.type === 'chunk' || event.data?.type === 'chunk'
        const lane = event.data?.lane
        const payload = event.data?.payload ?? event.data ?? event
        const textCandidate =
          typeof payload === 'string'
            ? payload
            : payload?.text ?? payload?.content ?? payload?.result ?? payload?.message ?? ''

        if (!isChunkEvent || typeof textCandidate !== 'string' || !textCandidate) return

        if (lane === 'output_isn9lm_text_1') {
          rawResult.value += textCandidate
        } else if (lane === 'output_h0uzga_text_1') {
          audioUrl.value = textCandidate
          // 新的音频地址到来时，重置播放状态
          isPlayingAudio.value = false
          if (audioInstance) {
            audioInstance.pause()
            audioInstance.currentTime = 0
            audioInstance = null
          }
        }
      },
      (error) => {
        console.error('OCR 请求失败', error)
        ocrError.value = '识别过程中出现问题，请稍后重试'
        isProcessing.value = false
        streamAbort.value = null
      },
      () => {
        isProcessing.value = false
        streamAbort.value = null
        if (!rawResult.value && !ocrError.value) {
          ocrError.value = '未获取到识别结果'
        }
      },
      { endpoint: '/api/bailing/stream-chat-with-file' },
    )
  } catch (error) {
    console.error('OCR 请求启动失败', error)
    ocrError.value = '无法发起识别请求，请稍后再试'
    isProcessing.value = false
    streamAbort.value = null
  }
}

const handleScreenshotClick = () => {
  if (isCapturing.value) return
  if (isSelecting.value) {
    cancelSelection()
    return
  }
  isSelecting.value = true
  showSelectionOverlay.value = true
}

const handlePointerDown = (event: PointerEvent) => {
  if (event.button !== 0) return
  event.preventDefault()
  isPointerActive.value = true
  selectionStart.x = event.clientX
  selectionStart.y = event.clientY
  selectionRect.left = selectionStart.x
  selectionRect.top = selectionStart.y
  selectionRect.width = 0
  selectionRect.height = 0
}

const handlePointerMove = (event: PointerEvent) => {
  if (!isPointerActive.value) return
  const currentX = event.clientX
  const currentY = event.clientY
  selectionRect.left = Math.min(selectionStart.x, currentX)
  selectionRect.top = Math.min(selectionStart.y, currentY)
  selectionRect.width = Math.abs(currentX - selectionStart.x)
  selectionRect.height = Math.abs(currentY - selectionStart.y)
}

const captureSelectedArea = async () => {
  if (typeof window === 'undefined') return
  if (selectionRect.width < 10 || selectionRect.height < 10) return
  isCapturing.value = true
  try {
    await nextTick()
    await new Promise((resolve) => requestAnimationFrame(resolve))

    const target = ocrPageRef.value || document.body
    const rect = target.getBoundingClientRect()

    const canvas = await html2canvas(target, {
      backgroundColor: '#ffffff',
      useCORS: true,
      scale: window.devicePixelRatio || 1,
      x: selectionRect.left - rect.left,
      y: selectionRect.top - rect.top,
      width: selectionRect.width,
      height: selectionRect.height,
    })

    const dataUrl = canvas.toDataURL('image/png')
    capturedImage.value = dataUrl
    const screenshotFile = dataUrlToFile(dataUrl, `screenshot-${Date.now()}.png`)
    await startRecognition(screenshotFile)
  } catch (error) {
    console.error('截图失败', error)
    ocrError.value = '截图失败，请重试'
  } finally {
    isCapturing.value = false
    cancelSelection()
  }
}

const handlePointerUp = () => {
  if (!isPointerActive.value) return
  isPointerActive.value = false
  if (selectionRect.width < 10 || selectionRect.height < 10) {
    cancelSelection()
    return
  }
  isSelecting.value = false
  showSelectionOverlay.value = false
  void captureSelectedArea()
}

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && isSelecting.value) {
    event.preventDefault()
    cancelSelection()
  }
}

const copyResult = async () => {
  if (!displayResult.value) return
  try {
    await navigator.clipboard.writeText(displayResult.value)
  } catch (error) {
    console.error('复制失败', error)
    ocrError.value = '复制失败，请手动选择文本'
  }
}

const openTranslate = () => {
  if (!displayResult.value) return
  const url = `https://fanyi.baidu.com/#zh/en/${encodeURIComponent(displayResult.value)}`
  window.open(url, '_blank', 'noopener')
}

const handlePlayAudio = () => {
  if (!audioUrl.value) return
  try {
    // 如果已有实例且在播放，则点击变为暂停
    if (audioInstance && isPlayingAudio.value) {
      audioInstance.pause()
      isPlayingAudio.value = false
      return
    }

    // 如果已有实例但未播放，只需复用
    if (!audioInstance) {
      audioInstance = new Audio(audioUrl.value)
      audioInstance.addEventListener('ended', () => {
        isPlayingAudio.value = false
      })
    }

    void audioInstance.play()
    isPlayingAudio.value = true
  } catch (error) {
    console.error('播放音频失败', error)
    isPlayingAudio.value = false
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleKeydown)
  streamAbort.value?.()
  streamAbort.value = null
  if (audioInstance) {
    audioInstance.pause()
    audioInstance = null
  }
})
</script>

<style scoped>
/* 直接复用 Ocr.vue 的样式 */
:global(body) {
  background-color: #ffffff;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  color: #0f172a;
}

.ocr-page {
  height: 100%;
  background: #f5f7fb;
  color: #0f172a;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.ocr-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 18px;
}

.logo-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 16px;
  background: linear-gradient(135deg, #69a5ff, #3c63ff);
  font-weight: 600;
}

.header-tabs {
  display: inline-flex;
  padding: 4px;
  border-radius: 999px;
  background-color: #e0e7ff;
  gap: 6px;
}

.tab {
  border: none;
  border-radius: 999px;
  padding: 8px 18px;
  background: transparent;
  color: #4b5563;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab:hover {
  color: #111827;
}

.tab.is-active {
  background: #ffffff;
  color: #111827;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
}

.mode-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  cursor: default;
}

.mode-chip svg {
  width: 20px;
  height: 20px;
}

.ocr-content {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  column-gap: 16px;
}

.panel {
  background: #ffffff;
  border-radius: 24px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  padding: 16px 18px;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
}

.upload-panel {
  gap: 12px;
  flex: 1;
}

.dropzone {
  border-radius: 16px;
  padding: 12px 16px;
  text-align: center;
  background: #f8fafc;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.dropzone.has-preview {
  padding: 8px 12px;
}

.dropzone-preview {
  width: 100%;
  height: 100%;
  flex: 1;
  border-radius: 18px;
  object-fit: contain;
  background: #ffffff;
  box-shadow: inset 0 0 0 1px rgba(15, 23, 42, 0.08);
}

.dropzone-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 24px;
  border-radius: 24px;
  background: rgba(99, 102, 241, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4f46e5;
}

.dropzone-icon svg {
  width: 38px;
  height: 38px;
}

.dropzone-hint {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #0f172a;
}

.dropzone-sub {
  font-size: 14px;
  color: rgba(15, 23, 42, 0.5);
}

.upload-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  height: 56px;
  margin-top: 8px;
}

.upload-footer-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.upload-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.upload-btn {
  flex: 1;
}

.upload-btn svg {
  width: 20px;
  height: 20px;
}

.upload-btn:disabled,
.clear-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.clear-btn {
  background: rgba(239, 68, 68, 0.08);
  color: #b91c1c;
}

.clear-btn:hover {
  background: rgba(239, 68, 68, 0.16);
}

.result-panel {
  gap: 12px;
  flex: 1;
}

.result-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
}

.chip-group {
  display: inline-flex;
  gap: 12px;
  background: rgba(59, 130, 246, 0.1);
  padding: 6px;
  border-radius: 999px;
}

.chip {
  border: none;
  border-radius: 999px;
  padding: 8px 18px;
  background: transparent;
  color: #4b5563;
  cursor: pointer;
  font-size: 14px;
}

.chip.is-active {
  background: #ffffff;
  color: #111827;
  font-weight: 600;
}

.language-select {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  border-radius: 12px;
  background: rgba(59, 130, 246, 0.08);
  font-size: 14px;
  cursor: pointer;
  border: none;
}

.language-select svg {
  width: 18px;
  height: 18px;
}

.result-body {
  flex: 1;
  border-radius: 16px;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 12px 16px;
}

.result-placeholder {
  color: rgba(15, 23, 42, 0.55);
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
}

.placeholder-icon {
  font-size: 52px;
  font-weight: 600;
  letter-spacing: 0.05em;
  color: rgba(15, 23, 42, 0.35);
}

.result-text-wrapper {
  position: relative;
  flex: 1;
  width: 100%;
  height: 100%;
  border-radius: 18px;
  background: #ffffff;
  box-shadow: inset 0 0 0 1px rgba(15, 23, 42, 0.08);
  padding: 16px 20px 48px;
  text-align: left;
  overflow: auto;
}

.result-text {
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-all;
  color: #0f172a;
}

.audio-footer-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.audio-footer-btn .icon {
  width: 18px;
  height: 18px;
}

.result-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 8px 0 0;
  height: 56px;
}

.footer-actions {
  display: flex;
  gap: 12px;
}

.secondary-btn,
.primary-btn {
  border: none;
  border-radius: 16px;
  padding: 12px 22px;
  font-size: 14px;
  cursor: pointer;
  font-weight: 600;
}

.secondary-btn {
  background: rgba(59, 130, 246, 0.1);
  color: #0f172a;
}

.secondary-btn:hover {
  background: rgba(59, 130, 246, 0.2);
}

.primary-btn {
  background: linear-gradient(135deg, #2563eb, #5de0ff);
  color: #020205;
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.25);
}

.primary-btn:hover {
  filter: brightness(1.05);
}

.selection-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.25);
  backdrop-filter: blur(2px);
  z-index: 999;
  cursor: crosshair;
  user-select: none;
}

.selection-tip {
  position: fixed;
  top: 24px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(15, 23, 42, 0.85);
  color: #ffffff;
  padding: 8px 18px;
  border-radius: 999px;
  font-size: 14px;
  pointer-events: none;
}

.selection-box {
  position: fixed;
  border: 2px solid #2563eb;
  background: rgba(37, 99, 235, 0.15);
  border-radius: 16px;
  pointer-events: none;
  box-shadow: 0 0 0 1px rgba(15, 23, 42, 0.1);
}

@media (max-width: 768px) {
  .ocr-page {
    padding: 24px;
  }

  .upload-actions {
    flex-direction: column;
  }

  .result-footer {
    flex-direction: column;
    gap: 18px;
    align-items: flex-start;
  }
}
</style>
