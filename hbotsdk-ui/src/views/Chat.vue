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
                  <span>{{ item.label }}</span>
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

        <!-- 新建会话按钮 -->
        <div class="new-conversation">
          <a-button type="primary" block @click="handleNewConversation">
            <template #icon>
              <PlusOutlined />
            </template>
            新会话
          </a-button>
        </div>
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
            description="开始新的对话"
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
                  <a-spin size="small" />
                </div>
                <div class="markdown-content" v-html="renderMarkdown(message.content)"></div>
              </div>
              <div v-else class="message-bubble user-bubble">
                {{ message.content }}
              </div>
              <div v-if="message.role === 'assistant' && message.status === 'done'" class="message-actions">
                <a-space>
                  <a-button type="text" size="small" @click="handleRetry(message.id)">
                    <template #icon><SyncOutlined /></template>
                    重新生成
                  </a-button>
                  <a-button type="text" size="small" @click="handleCopy(message.content)">
                    <template #icon><CopyOutlined /></template>
                    复制
                  </a-button>
                </a-space>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <a-input
          v-model:value="inputValue"
          :placeholder="'向我提问吧...'"
          :disabled="isRequesting"
          @pressEnter="handleSend"
          size="large"
        >
          <template #prefix>
            <a-upload
              :show-upload-list="false"
              :before-upload="handleFileUpload"
              accept="image/*"
            >
              <a-button type="text" :icon="h(PictureOutlined)" />
            </a-upload>
          </template>
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
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted, h } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  EllipsisOutlined,
  QuestionCircleOutlined,
  RobotOutlined,
  UserOutlined,
  SyncOutlined,
  CopyOutlined,
  PictureOutlined,
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import { streamChat, type ChatMessage, type ChatRequestParams } from '@/api/chat'
import { marked } from 'marked'

// 配置
const APP_ID = import.meta.env.VITE_APP_ID || '202511APy9N900565208'
const USER_ID = import.meta.env.VITE_USER_ID || 'default-user'

// 状态
const conversations = ref<Array<{ key: string; label: string; group?: string }>>([
  { key: 'default-0', label: '新会话 1', group: '今天' },
])
const activeConversationKey = ref('default-0')
const messages = ref<ChatMessage[]>([])
const inputValue = ref('')
const isRequesting = ref(false)
const chatListRef = ref<HTMLElement>()
const abortController = ref<(() => void) | null>(null)
const currentFile = ref<File | null>(null)

// 计算属性
const currentConversation = computed(() => {
  return conversations.value.find(c => c.key === activeConversationKey.value)
})

// 方法
const handleConversationClick = (key: string) => {
  activeConversationKey.value = key
  // TODO: 加载该会话的消息历史
}

const handleNewConversation = () => {
  const now = dayjs().valueOf().toString()
  const newConversation = {
    key: now,
    label: `新会话 ${conversations.value.length + 1}`,
    group: '今天',
  }
  conversations.value.push(newConversation)
  activeConversationKey.value = now
  messages.value = []
}

const handleRename = (item: { key: string; label: string }) => {
  // TODO: 实现重命名功能
  message.info('重命名功能待实现')
}

const handleDelete = (item: { key: string; label: string }) => {
  const index = conversations.value.findIndex(c => c.key === item.key)
  if (index > -1) {
    conversations.value.splice(index, 1)
    if (item.key === activeConversationKey.value) {
      activeConversationKey.value = conversations.value[0]?.key || ''
      messages.value = []
    }
  }
}

const handleFileUpload = (file: File) => {
  currentFile.value = file
  message.success(`已选择文件: ${file.name}`)
  return false // 阻止自动上传
}

const handleSend = async () => {
  if (!inputValue.value.trim() && !currentFile.value) {
    return
  }

  if (isRequesting.value) {
    message.warning('请求正在进行中，请等待请求完成')
    return
  }

  // 添加用户消息
  const userMessage: ChatMessage = {
    id: Date.now().toString(),
    role: 'user',
    content: inputValue.value,
    timestamp: Date.now(),
  }
  messages.value.push(userMessage)

  // 添加助手占位消息
  const assistantMessage: ChatMessage = {
    id: (Date.now() + 1).toString(),
    role: 'assistant',
    content: '',
    timestamp: Date.now(),
    status: 'loading',
  }
  messages.value.push(assistantMessage)

  const query = inputValue.value
  inputValue.value = ''
  const file = currentFile.value
  currentFile.value = null

  isRequesting.value = true

  const params: ChatRequestParams = {
    appId: APP_ID,
    query,
    userId: USER_ID,
    file: file || undefined,
  }

  let accumulatedContent = ''
  const assistantMessageId = assistantMessage.id

  abortController.value = await streamChat(
    params,
    (event) => {
      // 调试日志
      console.log('Received event:', event)
      
      // 处理流式消息，只处理 chunk 事件
      // 检查 event.type 或 event.data.type 是否为 'chunk'
      const isChunkEvent = event.type === 'chunk' || event.data?.type === 'chunk'
      
      if (isChunkEvent && event.data?.payload?.text) {
        accumulatedContent += event.data.payload.text
        
        // 更新 messages 数组中的消息以触发响应式更新
        const messageIndex = messages.value.findIndex(m => m.id === assistantMessageId)
        const targetMessage = messageIndex !== -1 ? messages.value[messageIndex] : null
        if (targetMessage) {
          targetMessage.content = accumulatedContent
          targetMessage.status = 'updating'
        }
        scrollToBottom()
      } else {
        // 调试：记录未处理的事件
        console.log('Event not processed:', {
          eventType: event.type,
          dataType: event.data?.type,
          hasData: !!event.data,
          hasPayload: !!event.data?.payload,
          hasText: !!event.data?.payload?.text,
          payload: event.data?.payload
        })
      }
    },
    (error) => {
      console.error('Chat error:', error)
      const messageIndex = messages.value.findIndex(m => m.id === assistantMessageId)
      const targetMessage = messageIndex !== -1 ? messages.value[messageIndex] : null
      if (targetMessage) {
        targetMessage.status = 'error'
        targetMessage.content = `错误: ${error.message}`
      }
      isRequesting.value = false
      message.error('请求失败: ' + error.message)
    },
    () => {
      const messageIndex = messages.value.findIndex(m => m.id === assistantMessageId)
      const targetMessage = messageIndex !== -1 ? messages.value[messageIndex] : null
      if (targetMessage) {
        targetMessage.status = 'done'
      }
      isRequesting.value = false
      scrollToBottom()
    }
  )

  scrollToBottom()
}

const handleRetry = (messageId: string) => {
  // 找到用户消息并重新发送
  const messageIndex = messages.value.findIndex(m => m.id === messageId)
  if (messageIndex > 0) {
    const userMessage = messages.value[messageIndex - 1]
    if (userMessage && userMessage.role === 'user') {
      // 删除当前助手消息
      messages.value.splice(messageIndex, 1)
      // 重新发送
      inputValue.value = userMessage.content
      handleSend()
    }
  }
}

const handleCopy = (content: string) => {
  navigator.clipboard.writeText(content).then(() => {
    message.success('已复制到剪贴板')
  })
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
  scrollToBottom()
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
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  padding: 0 12px;
  box-sizing: border-box;
  border-right: 1px solid #e8e8e8;
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

.conversations {
  flex: 1;
  overflow-y: auto;
  margin-top: 12px;
}

.conversation-list {
  :deep(.ant-list-item) {
    padding: 8px 12px;
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
}

.new-conversation {
  padding: 12px;
  border-top: 1px solid #e8e8e8;
}

.side-footer {
  border-top: 1px solid #e8e8e8;
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
  max-width: 70%;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  word-wrap: break-word;
  line-height: 1.6;

  &.assistant-bubble {
    background: #f5f5f5;
    color: #000;

    &.updating {
      background-image: linear-gradient(90deg, #ff6b23 0%, #af3cb8 31%, #53b6ff 89%);
      background-size: 100% 2px;
      background-repeat: no-repeat;
      background-position: bottom;
    }
  }

  &.user-bubble {
    background: #1890ff;
    color: #fff;
  }
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
}

.markdown-content {
  :deep(p) {
    margin: 0 0 8px 0;

    &:last-child {
      margin-bottom: 0;
    }
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
  padding: 16px 24px;
  border-top: 1px solid #e8e8e8;
  background: #fff;

  :deep(.ant-input) {
    border-radius: 24px;
  }
}
</style>

