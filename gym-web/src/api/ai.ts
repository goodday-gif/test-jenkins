import request from '@/utils/request'

export interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
}

export function sendChatMessage(message: string, history: ChatMessage[]) {
  return request({
    url: '/front/ai/chat',
    method: 'post',
    data: { message, history },
    timeout: 60000
  })
}
