/**
 * 聊天 API 服务
 */

export interface ChatMessage {
  id: string
  role: 'user' | 'assistant'
  content: string
  timestamp: number
  status?: 'loading' | 'updating' | 'done' | 'error'
  duration?: number // 耗时（毫秒）
  image?: string // 图片URL或Data URL
  audioUrl?: string // 语音播放地址
}

export interface ChatRequestParams {
  appId: string
  query: string
  userId: string
  file?: File
}

export interface StreamEvent {
  type: string
  data: any
}

/**
 * 流式聊天请求
 */
interface StreamChatOptions {
  endpoint?: string
}

export async function streamChat(
  params: ChatRequestParams,
  onMessage: (event: StreamEvent) => void,
  onError: (error: Error) => void,
  onComplete: () => void,
  options?: StreamChatOptions
): Promise<() => void> {
  const formData = new FormData()
  formData.append('appId', params.appId)
  formData.append('query', params.query)
  formData.append('userId', params.userId)
  if (params.file) {
    formData.append('file', params.file)
  }

  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  const endpoint = options?.endpoint ?? '/api/bailing/stream-chat-with-file'
  const url = `${apiBaseUrl}${endpoint}`

  // 使用 fetch 来处理 POST 请求和流式响应（SSE）
  const controller = new AbortController()
  const signal = controller.signal

  // 启动异步请求
  const requestPromise = fetch(url, {
    method: 'POST',
    body: formData,
    signal,
  })
    .then(async (response) => {
      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`)
      }

      const reader = response.body?.getReader()
      const decoder = new TextDecoder()

      if (!reader) {
        throw new Error('Response body is not readable')
      }

      let buffer = ''
      let currentEvent = ''

      while (true) {
        const { done, value } = await reader.read()

        if (done) {
          // 处理剩余的 buffer
          if (buffer.trim()) {
            const lines = buffer.split('\n')
            for (const line of lines) {
              if (line.startsWith('data:')) {
                try {
                  const data = JSON.parse(line.slice(5).trim())
                  onMessage({
                    type: currentEvent || data.type || 'message',
                    data,
                  })
                } catch (e) {
                  console.error('Failed to parse SSE data:', e, line)
                }
              } else if (line.startsWith('event:')) {
                currentEvent = line.slice(6).trim()
              }
            }
          }
          onComplete()
          break
        }

        buffer += decoder.decode(value, { stream: true })
        const chunks = buffer.split('\n\n')
        buffer = chunks.pop() || ''

        for (const chunk of chunks) {
          if (!chunk.trim()) continue

          const lines = chunk.split('\n')
          let eventType = 'message'
          let dataStr = ''

          for (const line of lines) {
            if (line.startsWith('event:')) {
              eventType = line.slice(6).trim()
              currentEvent = eventType
            } else if (line.startsWith('data:')) {
              dataStr = line.slice(5).trim()
            }
          }

          if (dataStr) {
            try {
              const data = JSON.parse(dataStr)
              onMessage({
                type: eventType,
                data,
              })
            } catch (e) {
              console.error('Failed to parse SSE data:', e, dataStr)
            }
          }
        }
      }
    })
    .catch((error) => {
      if (error.name !== 'AbortError') {
        onError(error)
      }
    })

  // 返回取消函数
  return () => {
    controller.abort()
  }
}

