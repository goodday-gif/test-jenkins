<template>
  <div class="ai-chat-wrapper">
    <!-- 浮动按钮 -->
    <div class="ai-fab" :class="{ active: isOpen }" @click="toggleChat">
      <span class="ai-fab-icon" v-if="!isOpen">🤖</span>
      <span class="ai-fab-icon close" v-else>✕</span>
      <div class="ai-fab-pulse"></div>
    </div>

    <!-- 聊天窗口 -->
    <transition name="chat-popup">
      <div class="ai-chat-window" v-if="isOpen">
        <!-- 标题栏 -->
        <div class="chat-header">
          <div class="chat-header-info">
            <span class="chat-avatar">🤖</span>
            <div>
              <h4>AI健身助手</h4>
              <span class="chat-status">FitBot · 在线</span>
            </div>
          </div>
          <button class="chat-close-btn" @click="isOpen = false">✕</button>
        </div>

        <!-- 消息列表 -->
        <div class="chat-messages" ref="messagesRef">
          <!-- 欢迎消息 -->
          <div class="message-item assistant" v-if="messages.length === 0">
            <div class="message-avatar">🤖</div>
            <div class="message-bubble">
              <p>Hi！我是 FitBot 🏋️ 你的智能健身助手~</p>
              <p>你可以问我关于健身训练、课程推荐、器材使用等问题！</p>
            </div>
          </div>

          <div
            v-for="(msg, index) in messages"
            :key="index"
            class="message-item"
            :class="msg.role"
          >
            <div class="message-avatar" v-if="msg.role === 'assistant'">🤖</div>
            <div class="message-bubble">
              <p v-for="(line, i) in msg.content.split('\n')" :key="i">{{ line }}</p>
            </div>
            <div class="message-avatar user-avatar" v-if="msg.role === 'user'">Me</div>
          </div>

          <!-- Loading -->
          <div class="message-item assistant" v-if="loading">
            <div class="message-avatar">🤖</div>
            <div class="message-bubble loading-bubble">
              <div class="typing-indicator">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区 -->
        <div class="chat-input-area">
          <div class="chat-input-wrapper">
            <input
              v-model="inputText"
              type="text"
              placeholder="输入你的问题..."
              @keyup.enter="sendMessage"
              :disabled="loading"
            />
            <button class="send-btn" @click="sendMessage" :disabled="loading || !inputText.trim()">
              <span>➤</span>
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { sendChatMessage, type ChatMessage } from '@/api/ai'
import { ElMessage } from 'element-plus'

const isOpen = ref(false)
const inputText = ref('')
const loading = ref(false)
const messages = ref<ChatMessage[]>([])
const messagesRef = ref<HTMLElement | null>(null)

function toggleChat() {
  isOpen.value = !isOpen.value
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  // 添加用户消息
  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  loading.value = true
  scrollToBottom()

  try {
    // 发送最近10条历史
    const history = messages.value.slice(-10, -1)
    const res: any = await sendChatMessage(text, history)
    messages.value.push({ role: 'assistant', content: res.reply })
  } catch (e: any) {
    messages.value.push({ role: 'assistant', content: '抱歉，我暂时无法回答，请稍后再试 😅' })
    ElMessage.error('AI服务请求失败')
  } finally {
    loading.value = false
    scrollToBottom()
  }
}
</script>

<style scoped>
.ai-chat-wrapper {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 9999;
}

/* 浮动按钮 */
.ai-fab {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #00d2ff, #7a2cff);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(0, 210, 255, 0.4);
  transition: all 0.3s ease;
  position: relative;
  z-index: 10;
}

.ai-fab:hover {
  transform: scale(1.08);
  box-shadow: 0 6px 28px rgba(0, 210, 255, 0.5);
}

.ai-fab.active {
  background: linear-gradient(135deg, #ff4757, #ff6b81);
  box-shadow: 0 4px 20px rgba(255, 71, 87, 0.4);
}

.ai-fab-icon {
  font-size: 24px;
  line-height: 1;
}

.ai-fab-icon.close {
  font-size: 20px;
  color: #fff;
  font-style: normal;
}

.ai-fab-pulse {
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  background: linear-gradient(135deg, #00d2ff, #7a2cff);
  opacity: 0;
  z-index: -1;
  animation: pulse 2s infinite;
}

.ai-fab.active .ai-fab-pulse {
  display: none;
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 0.5; }
  100% { transform: scale(1.4); opacity: 0; }
}

/* 聊天窗口 */
.ai-chat-window {
  position: absolute;
  bottom: 72px;
  right: 0;
  width: 380px;
  height: 520px;
  background: rgba(20, 20, 30, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5), 0 0 40px rgba(0, 210, 255, 0.08);
}

/* 标题栏 */
.chat-header {
  padding: 16px 20px;
  background: linear-gradient(135deg, rgba(0, 210, 255, 0.15), rgba(122, 44, 255, 0.15));
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chat-header-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-avatar {
  font-size: 28px;
}

.chat-header-info h4 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: #fff;
}

.chat-status {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.chat-close-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-close-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}

/* 消息列表 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.chat-messages::-webkit-scrollbar {
  width: 4px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.message-avatar.user-avatar {
  background: linear-gradient(135deg, #00d2ff, #7a2cff);
  font-size: 11px;
  color: #fff;
  font-weight: 700;
}

.message-bubble {
  max-width: 75%;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 13.5px;
  line-height: 1.6;
  word-break: break-word;
}

.message-bubble p {
  margin: 0;
}

.message-bubble p + p {
  margin-top: 6px;
}

.message-item.assistant .message-bubble {
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.88);
  border-bottom-left-radius: 4px;
}

.message-item.user .message-bubble {
  background: linear-gradient(135deg, #00d2ff, #7a2cff);
  color: #fff;
  border-bottom-right-radius: 4px;
}

/* 打字指示器 */
.loading-bubble {
  padding: 14px 18px;
}

.typing-indicator {
  display: flex;
  gap: 5px;
  align-items: center;
}

.typing-indicator span {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
  30% { transform: translateY(-6px); opacity: 1; }
}

/* 输入区 */
.chat-input-area {
  padding: 14px 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  background: rgba(15, 15, 25, 0.6);
}

.chat-input-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  padding: 4px 4px 4px 14px;
  transition: border-color 0.3s;
}

.chat-input-wrapper:focus-within {
  border-color: rgba(0, 210, 255, 0.5);
  box-shadow: 0 0 12px rgba(0, 210, 255, 0.1);
}

.chat-input-wrapper input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  color: #fff;
  font-size: 13.5px;
  line-height: 1.4;
}

.chat-input-wrapper input::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.send-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #00d2ff, #7a2cff);
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}

.send-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 2px 12px rgba(0, 210, 255, 0.4);
}

.send-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* 弹出动画 */
.chat-popup-enter-active {
  animation: chatIn 0.3s ease;
}

.chat-popup-leave-active {
  animation: chatOut 0.25s ease;
}

@keyframes chatIn {
  from { opacity: 0; transform: translateY(20px) scale(0.9); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

@keyframes chatOut {
  from { opacity: 1; transform: translateY(0) scale(1); }
  to { opacity: 0; transform: translateY(20px) scale(0.9); }
}
</style>
