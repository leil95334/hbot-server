<template>
  <div class="chat-layout">
    <ChatSidebar
      :conversations="conversations"
      :active-conversation-key="activeConversationKey"
      @new-conversation="handleNewConversation"
      @conversation-click="handleConversationClick"
      @rename="handleRename"
      @delete="handleDelete"
    />

    <!-- 主聊天区域 -->
    <div class="chat-main">
      <ChatMessageList
        ref="chatMessageListRef"
        :messages="messages"
      />

      <!-- 输入区域 -->
      <div class="chat-input">
        <div class="chat-input-container">
          <!-- 小工具栏：OCR / TTS -->
          <div class="tool-bar">
            <button class="tool-item" type="button" @click="openOcrPanel" @keyup.enter="openOcrPanel">
              <div class="tool-icon">
                <svg width="20" height="20" viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg">
                  <g fill="currentColor" fill-rule="evenodd">
                    <path d="M3,4 L29,4 C29.5522847,4 30,4.44771525 30,5 C30,5.55228475 29.5522847,6 29,6 L3,6 C2.44771525,6 2,5.55228475 2,5 C2,4.44771525 2.44771525,4 3,4 Z" />
                    <path d="M27.0035706,7.0022583 C27.5157162,7.0022583 27.9378187,7.38777894 27.995506,7.88444828 L28.0022247,8.00091245 L27.9999999,15.9999999 C27.9995274,18.7610895 25.7610896,20.9991442 23,20.9991442 L16.9999995,20.9989886 L17,22.5857864 L21.2426407,26.8284271 C21.633165,27.2189514 21.633165,27.8521164 21.2426407,28.2426407 C20.8521164,28.633165 20.2189514,28.633165 19.8284271,28.2426407 L15.914,24.328 L12,28.2426407 C11.639516,28.6031246 11.072285,28.6308542 10.6799938,28.3258293 L10.5857864,28.2426407 C10.1952621,27.8521164 10.1952621,27.2189514 10.5857864,26.8284271 L14.8284271,22.5857864 C14.8813457,22.5328679 14.9387196,22.4871202 14.9993414,22.4485432 L15,21 L9,21 C6.23857625,21 4,18.7614237 4,16 L4,9 C4,8.44771525 4.44771525,8 5,8 C5.55228475,8 6,8.44771525 6,9 L6,16 C6,17.6568542 7.34314575,19 9,19 L23.0047455,19 C24.6615997,19 26.0047455,17.6568542 26.0047455,16 L26.0047455,8.00108337 C26.0047455,7.44944752 26.4519347,7.0022583 27.0035706,7.0022583 Z" />
                  </g>
                </svg>
              </div>
              <div class="tool-name">OCR</div>
            </button>
          </div>

          <!-- 图片预览（在聊天框中） -->
          <div v-if="currentFile && filePreviewUrl" class="input-image-preview">
            <div class="preview-image-wrapper">
              <img :src="filePreviewUrl" :alt="currentFile.name" class="preview-image-inline" />
              <a-button 
                type="text" 
                size="small" 
                class="preview-close-inline"
                @click="clearFilePreview"
              >
                <template #icon>
                  <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M10.6075 4.40278L8.00981 6.99973L10.6075 9.59739L9.59742 10.6074L6.99976 8.00978L4.40281 10.6074L3.39276 9.59739L5.98972 6.99973L3.39276 4.40278L4.40281 3.39273L6.99976 5.98969L9.59742 3.39273L10.6075 4.40278Z" fill="currentColor"></path>
                  </svg>
                </template>
              </a-button>
            </div>
          </div>
          
          <!-- 隐藏的 OCR 专用文件选择器 -->
          <input
            ref="ocrFileInputRef"
            type="file"
            accept="image/*"
            style="display: none;"
            @change="handleOcrFileChange"
          />

          <a-input
            :key="chatInputKey"
            v-model:value="inputValue"
            :placeholder="'向我提问吧...'"
            :disabled="isRequesting"
            @pressEnter="handleSend"
            size="large"
          >
            <template #suffix>
              <div 
                class="ds-icon-button send-button"
                :class="{ 'ds-icon-button--disabled': isRequesting }"
                :tabindex="isRequesting ? '-1' : '0'"
                role="button"
                :aria-disabled="isRequesting"
                @click="handleSend"
              >
                <div class="ds-icon-button__hover-bg"></div>
                <div class="ds-icon">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M8.3125 0.981648C8.66767 1.05456 8.97902 1.20565 9.2627 1.4338C9.48724 1.61444 9.73029 1.85939 9.97949 2.1086L14.707 6.83614L13.293 8.2502L9 3.95723V15.0432H7V3.95723L2.70703 8.2502L1.29297 6.83614L6.02051 2.1086C6.26971 1.85939 6.51277 1.61444 6.7373 1.4338C6.97662 1.24132 7.28445 1.04548 7.6875 0.981648C7.8973 0.948471 8.1031 0.956625 8.3125 0.981648Z" fill="currentColor"></path>
                  </svg>
                </div>
              </div>
            </template>
          </a-input>
        </div>
      </div>
    </div>

    <teleport to="body">
      <transition name="ocr-modal-fade">
        <div v-if="showOcrModal" class="ocr-modal-overlay">
          <button class="ocr-modal-fullscreen" type="button" @click="goToOcrPage" aria-label="全屏打开 OCR 页面">
            <svg
              class="ocr-fullscreen-icon"
              viewBox="0 0 1024 1024"
              xmlns="http://www.w3.org/2000/svg"
              aria-hidden="true"
            >
              <path
                d="M193.349046 210.094207v130.995519c0.084979 23.530755-6.042025 43.024996-29.555785 43.105726h-0.195452c-23.649726-0.025494-33.940714-19.53673-34.004448-43.207701v-169.916017a41.580349 41.580349 0 0 1 41.784299-41.809792h170.328166c23.641228 0.029743 42.795552 10.707386 42.825294 34.36561 0.029743 23.535004-19.009859 29.445311-42.523618 29.475054H210.344896a16.995851 16.995851 0 0 0-16.99585 16.99585z m620.306058-16.99585H681.992498c-23.513759-0.025494-42.549112-5.935801-42.523618-29.470805 0.029743-23.662473 19.184066-34.335867 42.825294-34.369859H852.78805a41.580349 41.580349 0 0 1 41.618589 41.809792v169.920266c-0.063734 23.666722-10.354722 43.182207-34.000199 43.21195h-0.199701c-23.513759-0.084979-29.636515-19.57922-29.555785-43.109975v-130.995519a16.995851 16.995851 0 0 0-16.99585-16.99585zM210.344896 830.09434H342.007502c23.513759 0.025494 42.553361 5.94005 42.523618 29.470805-0.029743 23.662473-19.184066 34.335867-42.825294 34.369859H171.21195a41.580349 41.580349 0 0 1-41.618589-41.809792v-169.916017c0.063734-23.670971 10.354722-43.186456 34.004448-43.21195h0.195452c23.513759 0.084979 29.636515 19.574971 29.555785 43.105726v130.995519a16.995851 16.995851 0 0 0 16.99585 16.99585z m620.306058-16.859884v-130.991269c-0.084979-23.535004 6.042025-43.024996 29.555785-43.109975h0.199701c23.645477 0.029743 33.936465 19.545228 34.000199 43.21195v169.916016a41.580349 41.580349 0 0 1-41.784299 41.809793h-170.328166c-23.641228-0.029743-42.795552-10.707386-42.825294-34.36561-0.025494-23.535004 19.009859-29.445311 42.523618-29.475054H813.655104a16.995851 16.995851 0 0 0 16.99585-16.995851z"
                fill="#333333"
              />
            </svg>
          </button>
          <button class="ocr-modal-close" type="button" @click="closeOcrPanel" aria-label="关闭 OCR 面板">
            <svg
              class="ocr-close-icon"
              viewBox="0 0 1024 1024"
              xmlns="http://www.w3.org/2000/svg"
              aria-hidden="true"
            >
              <path
                d="M512 466.944l233.472-233.472a31.744 31.744 0 0 1 45.056 45.056L557.056 512l233.472 233.472a31.744 31.744 0 0 1-45.056 45.056L512 557.056l-233.472 233.472a31.744 31.744 0 0 1-45.056-45.056L466.944 512 233.472 278.528a31.744 31.744 0 0 1 45.056-45.056z"
                fill="#5A5A68"
              />
            </svg>
          </button>
          <OcrPanel
            @close-panel="closeOcrPanel"
            @open-panel="openOcrPanel"
          />
        </div>
      </transition>
    </teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { streamChat, type ChatMessage, type ChatRequestParams } from '@/api/chat'
import { 
  getAllConversations, 
  getConversation,
  saveConversation, 
  deleteConversation, 
  generateConversationTitle, 
  cleanupOldConversations,
  type ConversationStorage 
} from '@/utils/storage'
import ChatSidebar from '@/components/chat/ChatSidebar.vue'
import ChatMessageList from '@/components/chat/ChatMessageList.vue'
import OcrPanel from '@/components/OcrPanel.vue'

const router = useRouter()
const route = useRoute()

// 配置
// OCR 智能体使用环境中的 VITE_APP_ID（原有配置）
const OCR_APP_ID = import.meta.env.VITE_APP_ID as string
// 猴塞雷聊天智能体 APP_ID，从环境变量 VITE_MONKEY_AI_APP_ID 读取
const CHAT_APP_ID = import.meta.env.VITE_MONKEY_AI_APP_ID as string
const USER_ID = import.meta.env.VITE_USER_ID || 'default-user'

// 状态
const conversations = ref<ConversationStorage[]>([])
const activeConversationKey = ref('')
const messages = ref<ChatMessage[]>([])
const inputValue = ref('')
const chatInputKey = ref(0)
const isRequesting = ref(false)
type ChatMessageListInstance = InstanceType<typeof ChatMessageList> & {
  scrollToBottom: () => void
}
const chatMessageListRef = ref<ChatMessageListInstance | null>(null)
const abortController = ref<(() => void) | null>(null)
const currentFile = ref<File | null>(null)
const filePreviewUrl = ref<string>('')
const requestStartTime = ref<number>(0)
const ocrFileInputRef = ref<HTMLInputElement | null>(null)
const isOcrFlow = ref(false)
const ocrMode = ref<'upload' | 'screenshot'>('upload')
const showOcrModal = ref(false)
const goToOcrPage = () => {
  router.push('/ocr')
}
const openOcrPanel = () => {
  showOcrModal.value = true
}

const closeOcrPanel = () => {
  showOcrModal.value = false
}

watch(showOcrModal, (visible) => {
  document.body.style.overflow = visible ? 'hidden' : ''
})

// 计算属性
const currentConversation = computed(() => {
  return conversations.value.find(c => c.key === activeConversationKey.value)
})


// 加载会话历史
const loadConversations = () => {
  cleanupOldConversations() // 清理30天前的会话
  const savedConversations = getAllConversations()
  
  if (savedConversations.length === 0) {
    // 如果没有历史会话，创建一个默认会话
    const now = Date.now().toString()
    const defaultConversation: ConversationStorage = {
      key: now,
      label: '新会话 1',
      group: '今天',
      createdAt: Date.now(),
      updatedAt: Date.now(),
      messages: []
    }
    conversations.value = [defaultConversation]
    activeConversationKey.value = now
    saveConversation(defaultConversation)
  } else {
    conversations.value = savedConversations
    // 按创建时间排序，最新的在前
    conversations.value.sort((a, b) => b.createdAt - a.createdAt)
    const latestConversation = conversations.value[0]
    if (latestConversation) {
      activeConversationKey.value = latestConversation.key
    }
  }
  
  // 加载当前会话的消息
  loadCurrentConversationMessages()
}

// 加载当前会话的消息
const loadCurrentConversationMessages = () => {
  const current = getConversation(activeConversationKey.value)
  if (current) {
    messages.value = [...current.messages]
  } else {
    messages.value = []
  }
  scrollToBottom()
}

// 保存当前会话（带错误处理）
const saveCurrentConversation = () => {
  try {
    const current = conversations.value.find(c => c.key === activeConversationKey.value)
    if (current) {
      current.messages = [...messages.value]
      current.updatedAt = Date.now()
      // 如果有用户消息，更新标题
      if (messages.value.some(m => m.role === 'user')) {
        current.label = generateConversationTitle(messages.value)
      }
      const success = saveConversation(current)
      if (!success) {
        message.warning('存储空间不足，已清理旧会话')
      }
    }
  } catch (error) {
    console.error('Failed to save conversation:', error)
    message.error('保存会话失败')
  }
}

// 流式保存防抖
let streamingSaveTimer: ReturnType<typeof setTimeout> | null = null
const debouncedSaveDuringStreaming = () => {
  if (streamingSaveTimer) {
    clearTimeout(streamingSaveTimer)
  }
  streamingSaveTimer = setTimeout(() => {
    saveCurrentConversation()
  }, 2000) // 2秒防抖
}

const handleConversationClick = (key: string) => {
  // 如果点击的是当前会话，不做任何操作
  if (key === activeConversationKey.value) return
  
  // 保存当前会话
  saveCurrentConversation()
  // 切换到新会话
  activeConversationKey.value = key
  loadCurrentConversationMessages()
}

const handleNewConversation = () => {
  // 检查当前会话是否为空，如果为空则不允许创建新会话
  if (messages.value.length === 0) {
    message.warning('请先在当前会话中发送消息')
    return
  }
  
  // 保存当前会话
  saveCurrentConversation()
  
  const now = Date.now().toString()
  const newConversation: ConversationStorage = {
    key: now,
    label: '新会话',
    group: '今天',
    createdAt: Date.now(),
    updatedAt: Date.now(),
    messages: []
  }
  
  // 添加到开头
  conversations.value.unshift(newConversation)
  activeConversationKey.value = now
  messages.value = []
  saveConversation(newConversation)
  
  // 按创建时间排序，最新的在前
  conversations.value.sort((a, b) => b.createdAt - a.createdAt)
  // 强制触发响应式更新
  conversations.value = [...conversations.value]
  
  // 清理流式保存定时器，以便下一次可以正常保存
  if (streamingSaveTimer) {
    clearTimeout(streamingSaveTimer)
    streamingSaveTimer = null
  }
}

const handleRename = (item: { key: string; label: string }) => {
  // TODO: 实现重命名功能
  message.info('重命名功能待实现')
}

const handleDelete = (item: ConversationStorage) => {
  const index = conversations.value.findIndex(c => c.key === item.key)
  if (index > -1) {
    conversations.value.splice(index, 1)
    deleteConversation(item.key)
    
    if (item.key === activeConversationKey.value) {
      // 如果删除的是当前会话，切换到第一个会话或创建新会话
      if (conversations.value.length > 0) {
        const nextConversation = conversations.value[0]
        if (nextConversation) {
          activeConversationKey.value = nextConversation.key
          loadCurrentConversationMessages()
        }
      } else {
        // 如果没有会话了，创建新会话
        handleNewConversation()
      }
    }
  }
}

const handleFileUpload = (file: File) => {
  // 检查文件大小，限制为 10MB
  const maxSize = 10 * 1024 * 1024 // 10MB
  if (file.size > maxSize) {
    message.error(`文件大小不能超过 10MB，当前文件大小: ${(file.size / 1024 / 1024).toFixed(2)}MB`)
    return false
  }
  
  // 检查是否是图片文件
  if (!file.type.startsWith('image/')) {
    message.error('请选择图片文件')
    return false
  }
  
  currentFile.value = file
  
  // 生成图片预览 URL
  const reader = new FileReader()
  reader.onload = (e) => {
    filePreviewUrl.value = e.target?.result as string
  }
  reader.readAsDataURL(file)
  
  message.success(`已选择图片: ${file.name} (${(file.size / 1024).toFixed(2)}KB)`)
  return false // 阻止自动上传
}

// 清除图片预览
const clearFilePreview = () => {
  currentFile.value = null
  filePreviewUrl.value = ''
}

// OCR 文件通用处理逻辑
const processOcrFile = (file: File) => {
  // 复用现有上传校验逻辑
  const maxSize = 10 * 1024 * 1024 // 10MB
  if (file.size > maxSize) {
    message.error(`文件大小不能超过 10MB，当前文件大小: ${(file.size / 1024 / 1024).toFixed(2)}MB`)
    return
  }
  if (!file.type.startsWith('image/')) {
    message.error('请选择图片文件')
    return
  }

  currentFile.value = file

  const reader = new FileReader()
  reader.onload = (ev) => {
    filePreviewUrl.value = ev.target?.result as string
    // 图片预览就绪后，自动发起 OCR 请求
    inputValue.value = '提取图片上的文字'
    isOcrFlow.value = true
    handleSend()
  }
  reader.readAsDataURL(file)
}

// OCR 文件选择变化
const handleOcrFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement | null
  const file = target?.files?.[0]
  if (!file) return
  processOcrFile(file)
}

// OCR 模式切换菜单点击
const handleOcrModeMenuClick = (info: { key: string }) => {
  if (info.key === 'upload' || info.key === 'screenshot') {
    ocrMode.value = info.key
  }
}
// OCR 截图模式下的粘贴处理：从剪贴板读取图片并复用 OCR 流程
const handleOcrPaste = (e: ClipboardEvent) => {
  if (ocrMode.value !== 'screenshot') {
    return
  }

  const items = e.clipboardData?.items
  if (!items) return

  for (let i = 0; i < items.length; i++) {
    const item = items[i]
    if (!item) continue
    if (item.type.startsWith('image/')) {
      const file = item.getAsFile()
      if (file) {
        e.preventDefault()
        message.success('已获取截图，正在识别...')
        processOcrFile(file)
      }
      break
    }
  }
}

const handleSend = async () => {
  if (!inputValue.value.trim() && !currentFile.value) {
    return
  }

  if (isRequesting.value) {
    message.warning('请求正在进行中，请等待请求完成')
    return
  }
  
  // 再次检查文件大小（以不会发送过大的文件）
  if (currentFile.value) {
    const maxSize = 10 * 1024 * 1024 // 10MB
    if (currentFile.value.size > maxSize) {
      message.error(`文件大小不能超过 10MB`)
      currentFile.value = null
      return
    }
  }

  // 先缓存当前输入内容和图片，然后立刻清空输入框和预览，保证视觉上立即清空
  const query = inputValue.value
  const image = filePreviewUrl.value || undefined
  inputValue.value = ''
  chatInputKey.value += 1

  // 添加用户消息
  const userMessage: ChatMessage = {
    id: Date.now().toString(),
    role: 'user',
    content: query,
    timestamp: Date.now(),
    image: image, // 保存图片
  }
  messages.value.push(userMessage)

  // 创建助手消息
  const assistantMessage: ChatMessage = {
    id: `assistant-${Date.now()}`,
    role: 'assistant',
    content: '',
    timestamp: Date.now(),
    status: 'loading',
  }
  messages.value.push(assistantMessage)

  const file = currentFile.value
  currentFile.value = null
  filePreviewUrl.value = '' // 清除图片预览

  isRequesting.value = true
  requestStartTime.value = Date.now() // 记录请求开始时间

  const params: ChatRequestParams = {
    appId: isOcrFlow.value ? OCR_APP_ID : CHAT_APP_ID,
    query,
    userId: USER_ID,
    file: file || undefined,
  }

  let accumulatedContent = ''
  const assistantMessageId = assistantMessage.id

  abortController.value = await streamChat(
    params,
    (event) => {
      // 处理流式消息：兼容老格式 payload.text 和新格式 v 字段
      const data = event.data || {}
      const text: string | undefined =
        (data.payload && typeof data.payload.text === 'string' ? data.payload.text : undefined) ??
        (typeof data.v === 'string' ? data.v : undefined)

      if (!text) {
        return
      }

      const messageIndex = messages.value.findIndex(m => m.id === assistantMessageId)
      const targetMessage = messageIndex !== -1 ? messages.value[messageIndex] : null

      // 新协议下没有 lane/audio 通道，直接把文本累积到 content 即可
      accumulatedContent += text

      if (targetMessage) {
        targetMessage.content = accumulatedContent
        targetMessage.status = 'updating'
      }

      // 流式保存
      debouncedSaveDuringStreaming()
      scrollToBottom()
    },
    (error) => {
      console.error('Chat error:', error)
      const messageIndex = messages.value.findIndex(m => m.id === assistantMessageId)
      if (messageIndex !== -1) {
        const targetMessage = messages.value[messageIndex]
        if (!targetMessage) {
          return
        }
        const duration = Date.now() - requestStartTime.value
        targetMessage.status = 'error'
        
        // 针对413错误提供更友好的提示
        let errorMsg = error.message
        if (error.message.includes('413')) {
          errorMsg = '请求体过大，请检查是否上传了过大的文件（限制10MB）'
        }
        
        targetMessage.content = `错误: ${errorMsg}`
        targetMessage.duration = duration // 错误情况下也保存耗时
      }
      isRequesting.value = false
      isOcrFlow.value = false
      
      // 针对413错误的特殊提示
      if (error.message.includes('413')) {
        message.error('文件过大！请上传小于10MB的文件')
      } else {
        message.error('请求失败: ' + error.message)
      }
    },
    () => {
      const messageIndex = messages.value.findIndex(m => m.id === assistantMessageId)
      if (messageIndex !== -1) {
        const targetMessage = messages.value[messageIndex]
        if (!targetMessage) {
          return
        }
        const duration = Date.now() - requestStartTime.value
        targetMessage.status = 'done'
        targetMessage.duration = duration // 保存耗时到独立字段
      }
      isRequesting.value = false
      // 清理流式保存定时器并最终保存
      if (streamingSaveTimer) {
        clearTimeout(streamingSaveTimer)
        streamingSaveTimer = null
      }
      saveCurrentConversation()
      scrollToBottom()
    }
  )

  scrollToBottom()
}

const scrollToBottom = () => {
  chatMessageListRef.value?.scrollToBottom()
}

onMounted(() => {
  loadConversations()
  // 添加页面卸载前的保存处理
  const handleBeforeUnload = () => {
    saveCurrentConversation()
  }
  window.addEventListener('beforeunload', handleBeforeUnload)
  window.addEventListener('paste', handleOcrPaste)

  // 如果从全屏 OCR 页面带着标记返回，则自动打开 OCR 弹层
  if (route.query.ocrModal === '1') {
    openOcrPanel()
  }
  
  // 组件卸载时清理事件监听器
  onBeforeUnmount(() => {
    window.removeEventListener('beforeunload', handleBeforeUnload)
    window.removeEventListener('paste', handleOcrPaste)
    if (streamingSaveTimer) {
      clearTimeout(streamingSaveTimer)
      streamingSaveTimer = null
    }
  })
})

onBeforeUnmount(() => {
  document.body.style.overflow = ''
})
</script>

<style scoped lang="less">
.chat-layout {
  width: 100%;
  height: 100vh;
  display: flex;
  background: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.chat-main {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.chat-input {
  padding: 10px 24px;
  background: #fff;
  width: 100%;
  box-sizing: border-box;
  margin-bottom: 40px;
}

.chat-input-container {
  max-width: 840px;
  width: 100%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  height: 100%;

  :deep(.ant-input) {
    border-radius: 24px;
    padding-left: 16px;
  }
}

.tool-bar {
  display: flex;
  gap: 12px;
  align-self: flex-start;
  margin-bottom: 12px;
}

.tool-item {
  display: inline-flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  padding: 4px 10px;
  height: 28px;
  border-radius: 999px;
  border: 1px solid #e5e7eb;
  color: #4b5563;
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s, transform 0.1s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.tool-item:hover {
  background-color: #f3f4f6;
  color: #111827;
}

.tool-item:active {
  transform: scale(0.96);
}

.tool-item--icon-only {
  cursor: default;
  border: none;
  box-shadow: none;
  padding: 0;
  height: auto;
}

.tool-item--icon-only:hover,
.tool-item--icon-only:active {
  background-color: transparent;
  color: inherit;
  transform: none;
}

.tool-icon {
  width: 14px;
  height: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tool-name {
  margin-left: 6px;
  font-size: 12px;
  line-height: 1;
  display: inline-flex;
  align-items: center;
}

:deep(.ant-dropdown-menu-item-selected) {
  background-color: #1677ff !important;
  color: #fff !important;
}

.input-image-preview {
  margin-bottom: 12px;
}

.preview-image-wrapper {
  position: relative;
  display: inline-block;
  width: 200px;
  height: 200px;
  
  &:hover .preview-close-inline {
    opacity: 1;
  }
}

.preview-image-inline {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  display: block;
}

.preview-close-inline {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #fff;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50% !important;
  width: 32px !important;
  height: 32px !important;
  min-width: 32px !important;
  min-height: 32px !important;
  padding: 0 !important;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  opacity: 0;
  transition: opacity 0.2s;
  
  &:hover {
    background: rgba(0, 0, 0, 0.8);
    color: #fff;
  }
  
  :deep(svg) {
    flex-shrink: 0;
  }
}

.ds-icon-button,
.send-button {
  --hover-size: 34px;
  width: 34px;
  height: 34px;
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease;
  color: #1677ff;
  flex-shrink: 0;
  border: none;
  background: transparent;
  outline: none;
  
  &:focus-visible {
    outline: 2px solid #1677ff;
    outline-offset: 2px;
  }
  
  &:hover:not(.ds-icon-button--disabled) {
    background-color: rgba(22, 119, 255, 0.08);
  }
  
  &:active:not(.ds-icon-button--disabled) {
    transform: scale(0.95);
    background-color: rgba(22, 119, 255, 0.12);
  }
  
  &.ds-icon-button--disabled {
    cursor: not-allowed;
    opacity: 0.4;
    color: #bfbfbf;
    pointer-events: none;
  }
}

.ds-icon-button__hover-bg {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: var(--hover-size);
  height: var(--hover-size);
  border-radius: 50%;
  background-color: transparent;
  transition: background-color 0.2s ease;
  pointer-events: none;
  z-index: 0;
}

.ds-icon-button:hover:not(.ds-icon-button--disabled) .ds-icon-button__hover-bg,
.send-button:hover:not(.ds-icon-button--disabled) .ds-icon-button__hover-bg {
  background-color: rgba(22, 119, 255, 0.08);
}

.ds-icon {
  font-size: 16px;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
  
  svg {
    width: 100%;
    height: 100%;
  }
}

.ocr-modal-overlay {
  position: fixed;
  inset: 0;
  background: #626773;
  backdrop-filter: blur(3px);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.ocr-modal-fullscreen {
  position: fixed;
  top: 28px;
  right: 80px;
  width: 40px;
  height: 40px;
  border-radius: 999px;
  border: none;
  background: rgba(255, 255, 255, 0.85);
  color: #0f172a;
  cursor: pointer;
  z-index: 2001;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);

  &:hover {
    background: #ffffff;
  }
}

.ocr-fullscreen-icon {
  width: 20px;
  height: 20px;
}

.ocr-modal-close {
  position: fixed;
  top: 28px;
  right: 28px;
  width: 40px;
  height: 40px;
  border-radius: 999px;
  border: none;
  background: rgba(255, 255, 255, 0.85);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2001;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);

  &:hover {
    background: #ffffff;
  }
}

.ocr-close-icon {
  width: 20px;
  height: 20px;
}

.ocr-modal-overlay :deep(.ocr-page) {
  width: 80vw;
  height: 80vh;
  max-width: 80vw;
  max-height: 80vh;
  overflow: hidden;
  background: #626773;
}

.ocr-modal-fade-enter-active,
.ocr-modal-fade-leave-active {
  transition: opacity 0.2s ease;
}

.ocr-modal-fade-enter-from,
.ocr-modal-fade-leave-to {
  opacity: 0;
}
</style>

