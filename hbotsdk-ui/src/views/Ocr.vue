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
            <p class="dropzone-sub">截图已就绪，可直接进行识别</p>
          </template>
          <template v-else>
            <div class="dropzone-icon">
              <svg viewBox="0 0 48 48" aria-hidden="true">
                <path
                  d="M23.293 13.293a1 1 0 0 1 1.414 0l7 7a1 1 0 0 1-1.414 1.414L25 16.414V34a1 1 0 1 1-2 0V16.414l-5.293 5.293a1 1 0 0 1-1.414-1.414l7-7z"
                  fill="currentColor"
                />
                <path
                  d="M10 30a1 1 0 0 1 1 1v4h26v-4a1 1 0 1 1 2 0v5a1 1 0 0 1-1 1H9a1 1 0 0 1-1-1v-5a1 1 0 0 1 1-1z"
                  fill="currentColor"
                />
              </svg>
            </div>
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

        <div class="upload-actions">
          <button class="ghost-btn" @click="triggerFileSelect">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path
                d="M12 5a7 7 0 0 1 7 7v5h1a1 1 0 1 1 0 2H4a1 1 0 1 1 0-2h1v-5a7 7 0 0 1 7-7zm0 2a5 5 0 0 0-5 5v5h10v-5a5 5 0 0 0-5-5zm0 8a1 1 0 1 1 0 2 1 1 0 0 1 0-2z"
                fill="currentColor"
              />
            </svg>
            选择图片
          </button>
          <button class="ghost-btn clear-btn" @click="clearImage" :disabled="!capturedImage">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path
                d="M6.343 6.343a1 1 0 0 1 1.414 0L12 10.586l4.243-4.243a1 1 0 0 1 1.414 1.414L13.414 12l4.243 4.243a1 1 0 0 1-1.414 1.414L12 13.414l-4.243 4.243a1 1 0 0 1-1.414-1.414L10.586 12 6.343 7.757a1 1 0 0 1 0-1.414z"
                fill="currentColor"
              />
            </svg>
            清空
          </button>
          <button class="ghost-btn" :disabled="isCapturing" @click="handleScreenshotClick">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path
                d="M6 5h12a1 1 0 0 1 1 1v12.5a1 1 0 0 1-.553.894l-5.5 2.75a1 1 0 0 1-.894 0l-5.5-2.75A1 1 0 0 1 6 18.5V6a1 1 0 0 1 1-1zm1 2v10.764l4.5 2.25 4.5-2.25V7H7zm1.5 2h7v2h-7V9zm0 3h5v2h-5v-2z"
                fill="currentColor"
              />
            </svg>
            {{ screenshotButtonText }}
          </button>
        </div>
      </section>

      <section class="panel result-panel">
        <div class="result-toolbar">
          <div class="chip-group">
            <button class="chip is-active">文字识别</button>
            <button class="chip">表格识别</button>
            <button class="chip">公式识别</button>
          </div>
          <div class="language-select">
            <span>中文</span>
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M7 10l5 5 5-5H7z" fill="currentColor" />
            </svg>
          </div>
        </div>

        <div class="result-body">
          <div v-if="isProcessing" class="result-loading">
            <div class="loading-spinner"></div>
            <p>识别中，请稍候...</p>
          </div>
          <div v-else-if="displayResult">
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
          <label class="toggle">
            <input type="checkbox" v-model="removeLineBreaks" />
            <span class="track">
              <span class="thumb"></span>
            </span>
            <span class="label">去除换行符</span>
          </label>

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
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue';
import html2canvas from 'html2canvas';
import { streamChat } from '@/api/chat';

const OCR_APP_ID = import.meta.env.VITE_APP_ID as string | undefined;
const USER_ID = import.meta.env.VITE_USER_ID || 'default-user';
const OCR_PROMPT = '提取这张图片上的文字';

const isCapturing = ref(false);
const ocrPageRef = ref<HTMLElement | null>(null);
const isSelecting = ref(false);
const showSelectionOverlay = ref(false);
const isPointerActive = ref(false);
const capturedImage = ref<string | null>(null);
const fileInputRef = ref<HTMLInputElement | null>(null);
const isProcessing = ref(false);
const ocrError = ref<string | null>(null);
const rawResult = ref('');
const removeLineBreaks = ref(false);
const streamAbort = ref<(() => void) | null>(null);

const selectionRect = reactive({
  left: 0,
  top: 0,
  width: 0,
  height: 0,
});
const selectionStart = reactive({
  x: 0,
  y: 0,
});

const selectionBoxStyle = computed(() => ({
  left: `${selectionRect.left}px`,
  top: `${selectionRect.top}px`,
  width: `${selectionRect.width}px`,
  height: `${selectionRect.height}px`,
}));

const displayResult = computed(() => {
  if (!rawResult.value) return '';
  return removeLineBreaks.value ? rawResult.value.replace(/\s*\n+\s*/g, ' ').trim() : rawResult.value;
});

const screenshotButtonText = computed(() => {
  if (isCapturing.value) return '截图中...';
  if (isSelecting.value) return '退出截取';
  return '屏幕截图';
});

const resetSelectionRect = () => {
  selectionRect.left = 0;
  selectionRect.top = 0;
  selectionRect.width = 0;
  selectionRect.height = 0;
  selectionStart.x = 0;
  selectionStart.y = 0;
};

const cancelSelection = () => {
  isSelecting.value = false;
  showSelectionOverlay.value = false;
  isPointerActive.value = false;
  resetSelectionRect();
};

const clearImage = () => {
  capturedImage.value = null;
  rawResult.value = '';
  ocrError.value = null;
  removeLineBreaks.value = false;
  streamAbort.value?.();
  streamAbort.value = null;
  isProcessing.value = false;
};

const triggerFileSelect = () => {
  fileInputRef.value?.click();
};

const fileToDataUrl = (file: File) => {
  return new Promise<string>((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = () => reject(reader.error);
    reader.readAsDataURL(file);
  });
};

const dataUrlToFile = (dataUrl: string, fileName: string) => {
  const [meta, content] = dataUrl.split(',');
  const mimeMatch = meta?.match(/data:(.*?);/);
  const mime = mimeMatch ? mimeMatch[1] : 'image/png';
  const binary = atob(content || '');
  const len = binary.length;
  const u8arr = new Uint8Array(len);
  for (let i = 0; i < len; i += 1) {
    u8arr[i] = binary.charCodeAt(i);
  }
  return new File([u8arr], fileName, { type: mime });
};

const handleFileChange = async (event: Event) => {
  const target = event.target as HTMLInputElement | null;
  const file = target?.files?.[0];
  if (!file) return;
  try {
    capturedImage.value = await fileToDataUrl(file);
    await startRecognition(file);
  } catch (error) {
    console.error('读取图片失败', error);
    ocrError.value = '读取图片失败，请重试';
  } finally {
    if (target) {
      target.value = '';
    }
  }
};

const startRecognition = async (file: File) => {
  if (!OCR_APP_ID) {
    ocrError.value = '缺少 OCR 应用配置，请检查环境变量';
    return;
  }

  streamAbort.value?.();
  rawResult.value = '';
  ocrError.value = null;
  isProcessing.value = true;

  try {
    streamAbort.value = await streamChat(
      {
        appId: OCR_APP_ID,
        query: OCR_PROMPT,
        userId: USER_ID,
        file,
      },
      (event) => {
        const isChunkEvent = event.type === 'chunk' || event.data?.type === 'chunk';
        const text = event.data?.payload?.text;
        if (isChunkEvent && typeof text === 'string') {
          rawResult.value += text;
        }
      },
      (error) => {
        console.error('OCR 请求失败', error);
        ocrError.value = '识别过程中出现问题，请稍后重试';
        isProcessing.value = false;
        streamAbort.value = null;
      },
      () => {
        isProcessing.value = false;
        streamAbort.value = null;
        if (!rawResult.value && !ocrError.value) {
          ocrError.value = '未获取到识别结果';
        }
      },
      { endpoint: '/api/bailing/stream-chat-with-file' },
    );
  } catch (error) {
    console.error('OCR 请求启动失败', error);
    ocrError.value = '无法发起识别请求，请稍后再试';
    isProcessing.value = false;
    streamAbort.value = null;
  }
};

const handleScreenshotClick = () => {
  if (isCapturing.value) return;
  if (isSelecting.value) {
    cancelSelection();
    return;
  }
  isSelecting.value = true;
  showSelectionOverlay.value = true;
};

const handlePointerDown = (event: PointerEvent) => {
  if (event.button !== 0) return;
  event.preventDefault();
  isPointerActive.value = true;
  selectionStart.x = event.clientX;
  selectionStart.y = event.clientY;
  selectionRect.left = selectionStart.x;
  selectionRect.top = selectionStart.y;
  selectionRect.width = 0;
  selectionRect.height = 0;
};

const handlePointerMove = (event: PointerEvent) => {
  if (!isPointerActive.value) return;
  const currentX = event.clientX;
  const currentY = event.clientY;
  selectionRect.left = Math.min(selectionStart.x, currentX);
  selectionRect.top = Math.min(selectionStart.y, currentY);
  selectionRect.width = Math.abs(currentX - selectionStart.x);
  selectionRect.height = Math.abs(currentY - selectionStart.y);
};

const captureSelectedArea = async () => {
  if (typeof window === 'undefined') return;
  if (selectionRect.width < 10 || selectionRect.height < 10) return;
  isCapturing.value = true;
  try {
    await nextTick();
    await new Promise((resolve) => requestAnimationFrame(resolve));
    const canvas = await html2canvas(document.body, {
      backgroundColor: '#ffffff',
      useCORS: true,
      scale: window.devicePixelRatio || 1,
      x: selectionRect.left + window.scrollX,
      y: selectionRect.top + window.scrollY,
      width: selectionRect.width,
      height: selectionRect.height,
    });
    const dataUrl = canvas.toDataURL('image/png');
    capturedImage.value = dataUrl;
    const screenshotFile = dataUrlToFile(dataUrl, `screenshot-${Date.now()}.png`);
    await startRecognition(screenshotFile);
  } catch (error) {
    console.error('截图失败', error);
    ocrError.value = '截图失败，请重试';
  } finally {
    isCapturing.value = false;
    cancelSelection();
  }
};

const handlePointerUp = () => {
  if (!isPointerActive.value) return;
  isPointerActive.value = false;
  if (selectionRect.width < 10 || selectionRect.height < 10) {
    cancelSelection();
    return;
  }
  isSelecting.value = false;
  showSelectionOverlay.value = false;
  void captureSelectedArea();
};

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && isSelecting.value) {
    event.preventDefault();
    cancelSelection();
  }
};

const copyResult = async () => {
  if (!displayResult.value) return;
  try {
    await navigator.clipboard.writeText(displayResult.value);
  } catch (error) {
    console.error('复制失败', error);
    ocrError.value = '复制失败，请手动选择文本';
  }
};

const openTranslate = () => {
  if (!displayResult.value) return;
  const url = `https://fanyi.baidu.com/#zh/en/${encodeURIComponent(displayResult.value)}`;
  window.open(url, '_blank', 'noopener');
};

onMounted(() => {
  window.addEventListener('keydown', handleKeydown);
});

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleKeydown);
  streamAbort.value?.();
  streamAbort.value = null;
});
</script>

<style scoped>
:global(body) {
  background-color: #ffffff;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  color: #0f172a;
}

.ocr-page {
  min-height: 100vh;
  background: #f5f7fb;
  color: #0f172a;
  padding: 32px 40px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.ocr-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
  grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));
  gap: 24px;
}

.panel {
  background: #ffffff;
  border-radius: 32px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  padding: 32px;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
}

.upload-panel {
  gap: 24px;
  flex: 1;
}

.dropzone {
  border: 1px dashed rgba(15, 23, 42, 0.15);
  border-radius: 24px;
  padding: 48px 24px;
  text-align: center;
  background: #f8fafc;
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 12px;
}

.dropzone.has-preview {
  padding: 16px;
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

.upload-actions {
  display: flex;
  gap: 16px;
  margin-top: 32px;
}

.ghost-btn {
  flex: 1;
  border-radius: 18px;
  border: 1px solid rgba(15, 23, 42, 0.12);
  background: #ffffff;
  color: #0f172a;
  padding: 14px 18px;
  font-size: 15px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  transition: border-color 0.2s ease, transform 0.2s ease, background 0.2s ease,
    color 0.2s ease;
}

.ghost-btn svg {
  width: 20px;
  height: 20px;
}

.ghost-btn:hover {
  border-color: rgba(37, 99, 235, 0.6);
  transform: translateY(-1px);
}

.ghost-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
  transform: none;
  border-color: rgba(148, 163, 184, 0.6);
  background: rgba(241, 245, 249, 0.8);
  color: #94a3b8;
}

.ghost-btn:disabled:hover {
  border-color: rgba(148, 163, 184, 0.6);
}

.clear-btn {
  border-color: rgba(239, 68, 68, 0.6);
  color: #b91c1c;
}

.clear-btn:hover {
  border-color: rgba(220, 38, 38, 0.8);
  background: #dc2626;
  color: #ffffff;
}

.result-panel {
  gap: 24px;
}

.result-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
}

.language-select svg {
  width: 18px;
  height: 18px;
}

.result-body {
  flex: 1;
  border-radius: 24px;
  background: #f8fafc;
  border: 1px dashed rgba(15, 23, 42, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 48px 24px;
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

.result-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
}

.toggle {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}

.toggle input {
  display: none;
}

.track {
  width: 46px;
  height: 24px;
  border-radius: 999px;
  background: linear-gradient(135deg, #4f78ff, #6ddcff);
  position: relative;
  padding: 3px;
}

.thumb {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #fff;
  position: absolute;
  top: 3px;
  right: 3px;
  transition: transform 0.2s ease;
}

.label {
  font-size: 14px;
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

