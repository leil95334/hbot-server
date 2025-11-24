<template>
  <div class="chat-sider">
    <div class="logo">
      <img
        src="https://mdn.alipayobjects.com/huamei_iwk9zp/afts/img/A*eco6RrQhxbMAAAAAAAAAAAAADgCCAQ/original"
        alt="logo"
        width="24"
        height="24"
      />
      <span>Hbot SDK</span>
    </div>

    <div class="new-conversation-btn">
      <a-button
        type="primary"
        block
        class="new-chat-btn"
        @click="emit('new-conversation')"
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

    <div class="conversations">
      <a-list
        :data-source="conversations"
        class="conversation-list"
      >
        <template #renderItem="{ item }">
          <a-list-item
            :key="item.key"
            :class="{ active: item.key === activeConversationKey }"
            @click="emit('conversation-click', item.key)"
          >
            <a-list-item-meta>
              <template #title>
                <div class="conversation-title">
                  <span class="title-text">{{ item.label }}</span>
                  <span class="message-preview">{{ getLastMessagePreview(item.messages) }}</span>
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
                    <a-menu-item key="rename" @click="emit('rename', item)">
                      <template #icon><EditOutlined /></template>
                      重命名
                    </a-menu-item>
                    <a-menu-item key="delete" danger @click="emit('delete', item)">
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

    <div class="side-footer">
      <a-avatar :size="24" />
      <a-button type="text">
        <template #icon><QuestionCircleOutlined /></template>
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  DeleteOutlined,
  EditOutlined,
  EllipsisOutlined,
  PlusOutlined,
  QuestionCircleOutlined,
} from '@ant-design/icons-vue'
import type { ConversationStorage } from '@/utils/storage'
import { getLastMessagePreview } from '@/utils/storage'

defineProps<{
  conversations: ConversationStorage[]
  activeConversationKey: string
}>()

const emit = defineEmits<{
  (e: 'new-conversation'): void
  (e: 'conversation-click', key: string): void
  (e: 'rename', item: ConversationStorage): void
  (e: 'delete', item: ConversationStorage): void
}>()
</script>

<style scoped lang="less">
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
  cursor: pointer;
  display: block;
  transition: background-color 0.2s ease, transform 0.1s ease;
  outline: none;

  &:focus-visible {
    outline: 2px solid #1677ff;
    outline-offset: 2px;
  }

  &:hover {
    background-color: rgba(22, 119, 255, 0.08);
  }

  &:active {
    transform: scale(0.95);
    background-color: rgba(22, 119, 255, 0.12);
  }
}

.new-chat-btn-inner {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 14px;
  box-sizing: border-box;
  color: #1677ff;
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
  color: #1677ff;
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

.side-footer {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
}
</style>

