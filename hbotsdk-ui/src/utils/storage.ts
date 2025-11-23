/**
 * 本地存储服务 - 管理会话历史
 */

import { type ChatMessage } from '@/api/chat'

export interface ConversationStorage {
  key: string
  label: string
  group: string
  createdAt: number
  updatedAt: number
  messages: ChatMessage[]
}

const STORAGE_KEY = 'hbot_conversations'

/**
 * 获取所有会话
 */
export function getAllConversations(): ConversationStorage[] {
  try {
    const data = localStorage.getItem(STORAGE_KEY)
    return data ? JSON.parse(data) : []
  } catch (error) {
    console.error('Failed to load conversations from storage:', error)
    return []
  }
}

/**
 * 保存所有会话
 */
export function saveAllConversations(conversations: ConversationStorage[]): void {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(conversations))
  } catch (error) {
    console.error('Failed to save conversations to storage:', error)
  }
}

/**
 * 获取单个会话
 */
export function getConversation(key: string): ConversationStorage | null {
  const conversations = getAllConversations()
  return conversations.find(c => c.key === key) || null
}

/**
 * 保存或更新会话（带存储空间检查）
 */
export function saveConversation(conversation: ConversationStorage): boolean {
  try {
    const conversations = getAllConversations()
    const existingIndex = conversations.findIndex(c => c.key === conversation.key)
    
    if (existingIndex > -1) {
      conversations[existingIndex] = conversation
    } else {
      conversations.push(conversation)
    }
    
    // 检查存储空间，如果满了则清理旧会话
    try {
      saveAllConversations(conversations)
      return true
    } catch (quotaError: any) {
      if (quotaError?.name === 'QuotaExceededError' || quotaError?.name === 'NS_ERROR_DOM_QUOTA_REACHED') {
        // 清理最旧的会话
        cleanupOldestConversations(conversations)
        saveAllConversations(conversations)
        return true
      }
      throw quotaError
    }
  } catch (error) {
    console.error('Failed to save conversation:', error)
    return false
  }
}

/**
 * 清理最旧的会话
 */
function cleanupOldestConversations(conversations: ConversationStorage[]): void {
  // 按更新时间排序，保留最新的50个会话
  conversations.sort((a, b) => b.updatedAt - a.updatedAt)
  const toKeep = conversations.slice(0, 50)
  conversations.length = 0
  conversations.push(...toKeep)
}

/**
 * 删除会话
 */
export function deleteConversation(key: string): void {
  const conversations = getAllConversations()
  const filtered = conversations.filter(c => c.key !== key)
  saveAllConversations(filtered)
}

/**
 * 生成会话标题（基于第一条用户消息）
 */
export function generateConversationTitle(messages: ChatMessage[]): string {
  const userMessage = messages.find(m => m.role === 'user')
  if (!userMessage || !userMessage.content.trim()) {
    return '新会话'
  }
  
  // 截取前20个字符作为标题
  const title = userMessage.content.trim()
  return title.length > 20 ? title.substring(0, 20) + '...' : title
}

/**
 * 获取会话最后一条消息预览
 */
export function getLastMessagePreview(messages: ChatMessage[]): string {
  if (messages.length === 0) return ''
  
  const lastMessage = messages[messages.length - 1]
  if (!lastMessage || !lastMessage.content) return ''
  
  const content = lastMessage.content.length > 30 ? lastMessage.content.substring(0, 30) + '...' : lastMessage.content
  
  if (lastMessage.role === 'user') {
    return `用户: ${content}`
  } else {
    return content
  }
}

/**
 * 清理30天前的会话
 */
export function cleanupOldConversations(): void {
  const conversations = getAllConversations()
  const thirtyDaysAgo = Date.now() - (30 * 24 * 60 * 60 * 1000)
  const filtered = conversations.filter(c => c.updatedAt > thirtyDaysAgo)
  saveAllConversations(filtered)
}
