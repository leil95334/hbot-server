<template>
  <div class="chat-layout">
    <!-- 侧边栏 -->
    <div class="chat-sider">
      <!-- Logo -->
      <div class="logo">
        <img
          src="https://mdn.alipayobjects.com/huamei_iwk9zp/afts/img/A*eco6RrQhxbMAAAAAAAAAAAAADgCCAQ/original"
          alt="logo"
          width="24"
          height="24"
        />
        <span>Hbot SDK</span>
      </div>

      <!-- 新建会话按钮 -->
      <div class="new-conversation-btn">
        <a-button
          type="primary"
          block
          class="new-chat-btn"
          @click="handleNewConversation"
        >
          <div class="new-chat-btn-inner">
            <div class="new-chat-left">
              <span class="new-chat-icon">
                <PlusOutlined />
              </span>
              <span class="new-chat-text">新会话</span>
            </div>
          </div>
        </a-button>
      </div>

      <!-- 会话列表 -->
      <div class="conversations">
        <a-list
          :data-source="conversations"
          class="conversation-list"
        >
          <template #renderItem="{ item }">
            <a-list-item
              :key="item.key"
              :class="{ 'active': item.key === activeConversationKey }"
              @click="handleConversationClick(item.key)"
            >
              <a-list-item-meta>
                <template #title>
                  <div class="conversation-title">
                    <span class="title-text">{{ item.label }}</span>
                    <span class="message-preview">{{ getLastMessagePreviewFn(item.messages) }}</span>
                  </div>
                </template>
              </a-list-item-meta>
              <template #actions>
                <a-dropdown :trigger="['click']">
                  <a-button type="text" size="small" @click.stop>
                    <template #icon>
                      <EllipsisOutlined />
                    </template>
                  </a-button>
                  <template #overlay>
                    <a-menu>
                      <a-menu-item key="rename" @click="handleRename(item)">
                        <template #icon><EditOutlined /></template>
                        重命名
                      </a-menu-item>
                      <a-menu-item key="delete" danger @click="handleDelete(item)">
                        <template #icon><DeleteOutlined /></template>
                        删除
                      </a-menu-item>
                    </a-menu>
                  </template>
                </a-dropdown>
              </template>
            </a-list-item>
          </template>
        </a-list>
      </div>

      <!-- 底部 -->
      <div class="side-footer">
        <a-avatar :size="24" />
        <a-button type="text" :icon="h(QuestionCircleOutlined)" />
      </div>
    </div>

    <!-- 主聊天区域 -->
    <div class="chat-main">
      <!-- 消息列表 -->
      <div class="chat-list" ref="chatListRef">
        <div v-if="messages.length === 0" class="welcome-container">
          <a-empty
            :description="false"
            :image="false"
          >
            <template #image>
              <img
                src="https://mdn.alipayobjects.com/huamei_iwk9zp/afts/img/A*s5sNRo5LjfQAAAAAAAAAAAAADgCCAQ/fmt.webp"
                alt="welcome"
                style="width: 200px; height: 200px;"
              />
            </template>
            <div class="welcome-content">
              <h2>你好，我是 Hbot 智能助手</h2>
              <p>基于 Hbot SDK 的智能对话系统，可以帮你解答问题、提供建议和协助工作。</p>
            </div>
          </a-empty>
        </div>

        <div v-else class="messages-container">
          <div
            v-for="message in messages"
            :key="message.id"
            :class="['message-item', `message-${message.role}`]"
          >
            <div class="message-avatar">
              <a-avatar v-if="message.role === 'assistant'">
                <template #icon><RobotOutlined /></template>
              </a-avatar>
              <a-avatar v-else>
                <template #icon><UserOutlined /></template>
              </a-avatar>
            </div>
            <div class="message-content">
              <div
                v-if="message.role === 'assistant'"
                class="message-bubble assistant-bubble"
                :class="{ 'updating': message.status === 'updating' || message.status === 'loading' }"
              >
                <div v-if="message.status === 'loading' || message.status === 'updating'" class="typing-indicator">
                  <span class="typing-dots">
                    <span class="dot"></span>
                    <span class="dot"></span>
                    <span class="dot"></span>
                  </span>
                </div>
                <div class="markdown-content" v-html="renderMarkdown(message.content)"></div>
              </div>
              <div v-else class="message-bubble user-bubble">
                <!-- 用户消息文本 -->
                <div v-if="message.content" class="message-text">{{ message.content }}</div>
                <!-- 用户消息中的图片 -->
                <div v-if="message.image" class="message-image">
                  <img :src="message.image" :alt="'用户图片'" />
                </div>
              </div>
              <div v-if="message.role === 'assistant' && message.status === 'done'" class="message-actions">
                <a-space>
                  <a-button type="text" size="small" @click="handleCopy(message.content)">
                    <template #icon><CopyOutlined /></template>
                    复制
                  </a-button>
                  <a-button 
                    type="text" 
                    size="small"
                    :disabled="!message.audioUrl"
                    @click="playAudio(message)"
                  >
                    <template #icon><SoundOutlined /></template>
                  </a-button>
                </a-space>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <div class="chat-input-container">
          <!-- 小工具栏：OCR / TTS -->
          <div class="tool-bar">
            <div class="tool-item" @click="handleOcrClick">
              <a-dropdown trigger="click">
                <div class="tool-icon" @click.stop>
                  <svg width="20" height="20" viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg">
                    <g fill="currentColor" fill-rule="evenodd">
                      <path d="M3,4 L29,4 C29.5522847,4 30,4.44771525 30,5 C30,5.55228475 29.5522847,6 29,6 L3,6 C2.44771525,6 2,5.55228475 2,5 C2,4.44771525 2.44771525,4 3,4 Z" />
                      <path d="M27.0035706,7.0022583 C27.5157162,7.0022583 27.9378187,7.38777894 27.995506,7.88444828 L28.0022247,8.00091245 L27.9999999,15.9999999 C27.9995274,18.7610895 25.7610896,20.9991442 23,20.9991442 L16.9999995,20.9989886 L17,22.5857864 L21.2426407,26.8284271 C21.633165,27.2189514 21.633165,27.8521164 21.2426407,28.2426407 C20.8521164,28.633165 20.2189514,28.633165 19.8284271,28.2426407 L15.914,24.328 L12,28.2426407 C11.639516,28.6031246 11.072285,28.6308542 10.6799938,28.3258293 L10.5857864,28.2426407 C10.1952621,27.8521164 10.1952621,27.2189514 10.5857864,26.8284271 L14.8284271,22.5857864 C14.8813457,22.5328679 14.9387196,22.4871202 14.9993414,22.4485432 L15,21 L9,21 C6.23857625,21 4,18.7614237 4,16 L4,9 C4,8.44771525 4.44771525,8 5,8 C5.55228475,8 6,8.44771525 6,9 L6,16 C6,17.6568542 7.34314575,19 9,19 L23.0047455,19 C24.6615997,19 26.0047455,17.6568542 26.0047455,16 L26.0047455,8.00108337 C26.0047455,7.44944752 26.4519347,7.0022583 27.0035706,7.0022583 Z" />
                    </g>
                  </svg>
                </div>
                <template #overlay>
                  <a-menu :selectedKeys="[ocrMode]" @click="handleOcrModeMenuClick">
                    <a-menu-item key="upload">上传图片</a-menu-item>
                    <a-menu-item key="screenshot">截图</a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
              <div class="tool-name">OCR</div>
            </div>
            <div class="tool-item">
              <div class="tool-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path fill="currentColor" d="M9 4v16l10-8z" />
                </svg>
              </div>
              <div class="tool-name">TTS</div>
            </div>
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
            ref="chatInputRef"
            v-model:value="inputValue"
            :placeholder="'向我提问吧...'"
            :disabled="isRequesting"
            @pressEnter="handleSend"
            size="large"
          >
            <template #suffix>
              <a-button
                type="primary"
                :loading="isRequesting"
                @click="handleSend"
              >
                发送
              </a-button>
            </template>
          </a-input>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted, onBeforeUnmount, h } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  EllipsisOutlined,
  QuestionCircleOutlined,
  RobotOutlined,
  UserOutlined,
  CopyOutlined,
  PictureOutlined,
  SoundOutlined,
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import { streamChat, type ChatMessage, type ChatRequestParams } from '@/api/chat'
import { marked } from 'marked'
import { 
  getAllConversations, 
  getConversation,
  saveConversation, 
  deleteConversation, 
  generateConversationTitle, 
  getLastMessagePreview,
  cleanupOldConversations,
  type ConversationStorage 
} from '@/utils/storage'

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
const chatInputRef = ref<any>(null)
const chatInputKey = ref(0)
const isRequesting = ref(false)
const chatListRef = ref<HTMLElement>()
const abortController = ref<(() => void) | null>(null)
const currentFile = ref<File | null>(null)
const filePreviewUrl = ref<string>('')
const requestStartTime = ref<number>(0)
const currentAudio = ref<HTMLAudioElement | null>(null)
const currentAudioMessageId = ref<string | null>(null)
const ocrFileInputRef = ref<HTMLInputElement | null>(null)
const isOcrFlow = ref(false)
const ocrMode = ref<'upload' | 'screenshot'>('upload')

// 计算属性
const currentConversation = computed(() => {
  return conversations.value.find(c => c.key === activeConversationKey.value)
})

// 工具函数
const formatDuration = (ms: number): string => {
  if (ms < 1000) {
    return `${ms}ms`
  } else if (ms < 60000) {
    return `${(ms / 1000).toFixed(1)}s`
  } else {
    const minutes = Math.floor(ms / 60000)
    const seconds = ((ms % 60000) / 1000).toFixed(1)
    return `${minutes}m ${seconds}s`
  }
}

// 获取文件类型
const getFileType = (filename: string): string => {
  const ext = filename.split('.').pop()?.toUpperCase() || 'FILE'
  return ext
}

// 暴露storage函数到模板
const getLastMessagePreviewFn = getLastMessagePreview

// 方法
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
    activeConversationKey.value = conversations.value[0].key
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
let streamingSaveTimer: NodeJS.Timeout | null = null
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
        activeConversationKey.value = conversations.value[0].key
        loadCurrentConversationMessages()
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

// OCR 工具点击：根据模式执行上传图片或引导截图粘贴
const handleOcrClick = () => {
  if (isRequesting.value) {
    message.warning('请求正在进行中，请等待当前请求完成')
    return
  }
  if (ocrMode.value === 'upload') {
    if (ocrFileInputRef.value) {
      ocrFileInputRef.value.value = ''
      ocrFileInputRef.value.click()
    }
  } else {
    message.info('已进入截图模式，请使用系统截图工具截取区域后，回到此页面按 Ctrl+V 粘贴图片')
  }
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
    inputValue.value = '提取这个图片上的文字'
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
  if (chatInputRef.value && (chatInputRef.value as any).input) {
    ;(chatInputRef.value as any).input.value = ''
  }

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
      // 处理流式消息，只处理 chunk 事件
      const isChunkEvent = event.type === 'chunk' || event.data?.type === 'chunk'
      
      if (isChunkEvent && event.data?.payload?.text) {
        const lane = event.data.lane
        const text = event.data.payload.text as string

        const messageIndex = messages.value.findIndex(m => m.id === assistantMessageId)

        // 当为语音地址通道时，不累积到 content，而是写入 audioUrl
        if (lane === 'output_h0uzga_text_1') {
          if (messageIndex !== -1) {
            messages.value[messageIndex].audioUrl = text
            // 语音地址就绪时，如果之前还是 loading，则标记为 updating 以触发 UI 更新
            if (!messages.value[messageIndex].content) {
              messages.value[messageIndex].status = 'updating'
            }
          }
        } else {
          // 其它通道（包含文字内容）仍然累积到 content
          accumulatedContent += text
          
          if (messageIndex !== -1) {
            messages.value[messageIndex].content = accumulatedContent
            messages.value[messageIndex].status = 'updating'
          }
        }

        // 流式保存
        debouncedSaveDuringStreaming()
        scrollToBottom()
      }
    },
    (error) => {
      console.error('Chat error:', error)
      const messageIndex = messages.value.findIndex(m => m.id === assistantMessageId)
      if (messageIndex !== -1) {
        const duration = Date.now() - requestStartTime.value
        messages.value[messageIndex].status = 'error'
        
        // 针对413错误提供更友好的提示
        let errorMsg = error.message
        if (error.message.includes('413')) {
          errorMsg = '请求体过大，请检查是否上传了过大的文件（限制10MB）'
        }
        
        messages.value[messageIndex].content = `错误: ${errorMsg}`
        messages.value[messageIndex].duration = duration // 错误情况下也保存耗时
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
        const duration = Date.now() - requestStartTime.value
        messages.value[messageIndex].status = 'done'
        messages.value[messageIndex].duration = duration // 保存耗时到独立字段
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

const handleCopy = (content: string) => {
  navigator.clipboard.writeText(content).then(() => {
    message.success('已复制到剪贴板')
  })
}

const playAudio = (msg: ChatMessage) => {
  if (!msg.audioUrl) {
    return
  }

  // 如果当前已经在播放同一条消息，点击则暂停并重置
  if (currentAudio.value && currentAudioMessageId.value === msg.id) {
    currentAudio.value.pause()
    currentAudio.value = null
    currentAudioMessageId.value = null
    return
  }

  // 如果有其他正在播放的音频，先停止
  if (currentAudio.value) {
    currentAudio.value.pause()
    currentAudio.value = null
    currentAudioMessageId.value = null
  }

  const audio = new Audio(msg.audioUrl)
  currentAudio.value = audio
  currentAudioMessageId.value = msg.id

  audio.play().catch((err) => {
    console.error('audio play error', err)
    message.error('语音播放失败，请稍后重试')
    currentAudio.value = null
    currentAudioMessageId.value = null
  })

  audio.onended = () => {
    currentAudio.value = null
    currentAudioMessageId.value = null
  }
}


const renderMarkdown = (content: string) => {
  if (!content) return ''
  try {
    return marked.parse(content) as string
  } catch (e) {
    console.error('Markdown render error:', e)
    return content
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatListRef.value) {
      chatListRef.value.scrollTop = chatListRef.value.scrollHeight
    }
  })
}

onMounted(() => {
  loadConversations()
  // 添加页面卸载前的保存处理
  const handleBeforeUnload = () => {
    saveCurrentConversation()
  }
  window.addEventListener('beforeunload', handleBeforeUnload)
  window.addEventListener('paste', handleOcrPaste)
  
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
</script>

<style scoped lang="less">
.chat-layout {
  width: 100%;
  height: 100vh;
  display: flex;
  background: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.chat-sider {
  width: 280px;
  height: 100%;
  background: #f9fafb;
  display: flex;
  flex-direction: column;
  padding: 0 12px;
  box-sizing: border-box;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: start;
  padding: 0 12px;
  gap: 8px;
  margin: 24px 0;

  span {
    font-weight: bold;
    color: #000;
    font-size: 16px;
  }
}

.new-conversation-btn {
  padding: 12px 0;
}

.new-chat-btn {
  :deep(.ant-btn) {
    all: unset;
  }

  height: 40px;
  border-radius: 12px;
  background: #ffffff;
  color: #1677ff;
  padding: 0;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 6px rgba(15, 23, 42, 0.06);
  cursor: pointer;
  display: block;

  &:hover {
    background: #f5f7ff;
    box-shadow: 0 4px 10px rgba(15, 23, 42, 0.1);
  }

  &:active {
    background: #e4edff;
    box-shadow: 0 2px 6px rgba(15, 23, 42, 0.08);
  }
}

.new-chat-btn-inner {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 14px;
  box-sizing: border-box;
}

.new-chat-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.new-chat-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.2);
}

.new-chat-text {
  font-size: 14px;
  font-weight: 500;
}

.conversations {
  flex: 1;
  overflow-y: auto;
  margin-top: 0;
}

.conversation-list {
  :deep(.ant-list-item) {
    padding: 12px;
    cursor: pointer;
    border-radius: 8px;
    margin-bottom: 4px;

    &:hover {
      background: #f5f5f5;
    }

    &.active {
      background: #e6f7ff;
    }
  }
  
  .conversation-title {
    display: flex;
    flex-direction: column;
    gap: 4px;
    
    .title-text {
      font-weight: 500;
      color: #262626;
      font-size: 14px;
      line-height: 1.4;
    }
    
    .message-preview {
      font-size: 12px;
      color: #8c8c8c;
      line-height: 1.3;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      max-height: 32px;
    }
  }
}

.new-conversation {
  padding: 12px;
  border-top: 1px solid #e8e8e8;
}

.side-footer {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
}

.chat-main {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.chat-list {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
}

.welcome-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
}

.welcome-content {
  margin-top: 24px;

  h2 {
    font-size: 24px;
    margin-bottom: 12px;
  }

  p {
    color: #666;
    font-size: 14px;
  }
}

.messages-container {
  max-width: 840px;
  width: 100%;
  margin: 0 auto;
}

.message-item {
  display: flex;
  margin-bottom: 24px;
  gap: 12px;

  &.message-user {
    flex-direction: row-reverse;

    .message-content {
      align-items: flex-end;
    }
  }
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  word-wrap: break-word;
  line-height: 1.6;

  &.assistant-bubble {
    background: transparent;
    color: #000;
  }

  &.user-bubble {
    background: #f5f5f5;
    color: #000;
  }
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;
}

.typing-dots {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  
  .dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background-color: #8c8c8c;
    animation: typing-dot 1.4s infinite ease-in-out;
    
    &:nth-child(1) {
      animation-delay: -0.32s;
    }
    
    &:nth-child(2) {
      animation-delay: -0.16s;
    }
    
    &:nth-child(3) {
      animation-delay: 0;
    }
  }
}

@keyframes typing-dot {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.markdown-content {
  :deep(p) {
    margin: 0 0 8px 0;

    &:last-child {
      margin-bottom: 0;
    }
  }

  :deep(ul),
  :deep(ol) {
    list-style: none;
    padding-left: 0;
    margin: 8px 0;
  }

  :deep(li) {
    list-style: none;
    margin: 4px 0;
  }

  :deep(code) {
    background: rgba(0, 0, 0, 0.1);
    padding: 2px 4px;
    border-radius: 4px;
    font-size: 0.9em;
  }

  :deep(pre) {
    background: rgba(0, 0, 0, 0.05);
    padding: 12px;
    border-radius: 8px;
    overflow-x: auto;
    margin: 8px 0;

    code {
      background: none;
      padding: 0;
    }
  }
}

.message-actions {
  display: flex;
  gap: 8px;
}

.chat-input {
  padding: 81px 24px;
  background: #fff;
  width: 100%;
  box-sizing: border-box;
}

.chat-input-container {
  max-width: 840px;
  width: 100%;
  margin: 0 auto;

  :deep(.ant-input) {
    border-radius: 24px;
    padding-left: 16px;
  }
}

.tool-bar {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  margin-bottom: 6px;
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
  box-shadow: 0 2px 6px rgba(15, 23, 42, 0.08);
}

.tool-item:hover {
  background-color: #f3f4f6;
  color: #111827;
}

.tool-item:active {
  transform: scale(0.96);
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

.message-text {
  word-wrap: break-word;
  line-height: 1.6;
}

.message-image {
  margin-top: 8px;
  max-width: 300px;
  border-radius: 8px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: auto;
    display: block;
    border-radius: 8px;
  }
}
</style>

