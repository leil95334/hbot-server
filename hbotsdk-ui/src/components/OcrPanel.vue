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
        <div
          class="dropzone"
          :class="{ 'has-preview': !!capturedImage }"
          @paste.prevent="handleDropzonePaste"
          @dragover.prevent
          @dragenter.prevent
          @drop.prevent="handleDropzoneDrop"
        >
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
            <span class="audio-icon-wrapper">
              <svg
                v-if="!isPlayingAudio"
                class="icon"
                viewBox="0 0 24 24"
                xmlns="http://www.w3.org/2000/svg"
                aria-hidden="true"
              >
                <path
                  fill="currentColor"
                  fill-rule="evenodd"
                  clip-rule="evenodd"
                  d="M5.714 8.857h.8l.596-.532 4.366-3.899v15.148L7.11 15.675l-.596-.532H3.095V8.857zm0-2.095 5.295-4.728c1.027-.834 2.562-.103 2.562 1.22v17.492c0 1.324-1.535 2.054-2.562 1.22l-5.295-4.728H3.095A2.095 2.095 0 0 1 1 15.143V8.857c0-1.157.938-2.095 2.095-2.095zM18.03 4.274a1.05 1.05 0 0 1 1.48-.082A10.45 10.45 0 0 1 23 12c0 3.103-1.35 5.892-3.492 7.809a1.048 1.048 0 0 1-1.397-1.562A8.36 8.36 0 0 0 20.905 12a8.36 8.36 0 0 0-2.794-6.247 1.05 1.05 0 0 1-.082-1.48m-.5 3.924a1.048 1.048 0 0 0-1.63 1.318c.606.748.932 1.518.932 2.484 0 .967-.326 1.736-.931 2.484a1.048 1.048 0 0 0 1.629 1.318c.85-1.052 1.397-2.274 1.397-3.802s-.546-2.75-1.397-3.802"
                />
              </svg>
              <div v-else class="audio-wave">
                <span></span>
                <span></span>
                <span></span>
                <span></span>
              </div>
            </span>
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
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { streamChat } from '@/api/chat'

const OCR_APP_ID = import.meta.env.VITE_APP_ID as string | undefined
const USER_ID = import.meta.env.VITE_USER_ID || 'default-user'
const OCR_PROMPT = '提取这张图片上的文字'

const ocrPageRef = ref<HTMLElement | null>(null)
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

const displayResult = computed(() => {
  return rawResult.value
})

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

const handleDropzonePaste = async (event: ClipboardEvent) => {
  const items = event.clipboardData?.items
  if (!items || items.length === 0) return

  for (let i = 0; i < items.length; i += 1) {
    const item = items[i]
    if (!item) continue
    if (item.type.startsWith('image/')) {
      const file = item.getAsFile()
      if (!file) continue
      try {
        event.preventDefault()
        capturedImage.value = await fileToDataUrl(file)
        await startRecognition(file)
      } catch (error) {
        console.error('粘贴图片处理失败', error)
        ocrError.value = '粘贴图片处理失败，请重试'
      }
      break
    }
  }
}

const handleDropzoneDrop = async (event: DragEvent) => {
  const files = event.dataTransfer?.files
  if (!files || files.length === 0) return

  const file = files[0]
  if (!file.type.startsWith('image/')) {
    ocrError.value = '请拖入图片文件'
    return
  }

  try {
    capturedImage.value = await fileToDataUrl(file)
    await startRecognition(file)
  } catch (error) {
    console.error('拖放图片处理失败', error)
    ocrError.value = '拖放图片处理失败，请重试'
  }
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

const handleScreenshotGuide = () => {
  // 这里不做真实截图，只给出引导说明
  // 建议用户使用系统截图快捷键，然后通过「选择图片」或粘贴到聊天输入框进行 OCR
  alert('请使用系统截图快捷键（例如 Win+Shift+S），然后：\n1）粘贴到聊天输入框，或\n2）保存为图片后使用“选择图片”上传进行识别。')
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
})

onBeforeUnmount(() => {
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
  align-items: stretch;
}

.panel {
  background: #ffffff;
  border-radius: 24px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  padding: 16px 18px;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  height: 100%;
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
  height: auto;
  max-height: 260px;
  flex: 0 0 auto;
  border-radius: 18px;
  object-fit: contain;
  background: #f8fafc;
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

.result-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: rgba(15, 23, 42, 0.85);
}

.loading-spinner {
  display: none;
}

.result-loading p {
  margin: 0;
  font-size: 14px;
}

.result-loading p::after {
  content: '';
  display: inline-block;
  width: 1.2em;
  text-align: left;
  animation: loading-dots 1.2s steps(4, end) infinite;
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
  background: #f8fafc;
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
  background: transparent;
  box-shadow: none;
  padding-left: 0;
  padding-right: 0;
}

.audio-icon-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
}

.audio-footer-btn .icon {
  width: 18px;
  height: 18px;
}

.audio-wave {
  display: inline-flex;
  align-items: flex-end;
  justify-content: center;
  width: 18px;
  gap: 2px;
  height: 18px;
}

.audio-wave span {
  width: 1.5px;
  background-color: currentColor;
  border-radius: 999px;
  animation: audio-wave 1s infinite ease-in-out;
}

.audio-wave span:nth-child(1) {
  height: 6px;
  animation-delay: 0s;
}

.audio-wave span:nth-child(2) {
  height: 10px;
  animation-delay: 0.1s;
}

.audio-wave span:nth-child(3) {
  height: 14px;
  animation-delay: 0.2s;
}

.audio-wave span:nth-child(4) {
  height: 9px;
  animation-delay: 0.3s;
}

@keyframes audio-wave {
  0%, 100% {
    transform: scaleY(0.6);
    opacity: 0.6;
  }
  50% {
    transform: scaleY(1);
    opacity: 1;
  }
}

@keyframes ocr-spinner {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes loading-dots {
  0% {
    content: '';
  }
  25% {
    content: '.';
  }
  50% {
    content: '..';
  }
  75% {
    content: '...';
  }
  100% {
    content: '';
  }
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
