<template>
  <div class="chat-list" ref="chatListRef">
    <div v-if="messages.length === 0" class="welcome-container">
      <a-empty :description="false" :image="false">
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
            :class="{ updating: message.status === 'updating' || message.status === 'loading' }"
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
            <div v-if="message.content" class="message-text">{{ message.content }}</div>
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
</template>

<script setup lang="ts">
import { nextTick, ref } from 'vue'
import { message as antMessage } from 'ant-design-vue'
import {
  CopyOutlined,
  RobotOutlined,
  SoundOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
import { marked } from 'marked'
import type { ChatMessage } from '@/api/chat'

defineProps<{
  messages: ChatMessage[]
}>()

const chatListRef = ref<HTMLDivElement | null>(null)
const currentAudio = ref<HTMLAudioElement | null>(null)
const currentAudioMessageId = ref<string | null>(null)

const handleCopy = (content: string) => {
  navigator.clipboard.writeText(content).then(() => {
    antMessage.success('已复制到剪贴板')
  })
}

const playAudio = (msg: ChatMessage) => {
  if (!msg.audioUrl) return

  if (currentAudio.value && currentAudioMessageId.value === msg.id) {
    currentAudio.value.pause()
    currentAudio.value = null
    currentAudioMessageId.value = null
    return
  }

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
    antMessage.error('语音播放失败，请稍后重试')
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

defineExpose({
  scrollToBottom,
})
</script>

<style scoped lang="less">
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
  0%,
  80%,
  100% {
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

.message-actions {
  display: flex;
  gap: 8px;
}
</style>

