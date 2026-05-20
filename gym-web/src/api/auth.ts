import request from '@/utils/request'

export function login(data: { username: string; password: string }) {
  return request.post('/front/login', data)
}

export function register(data: { username: string; password: string; nickname: string; phone: string }) {
  return request.post('/front/register', data)
}

export function getMemberInfo() {
  return request.get('/front/info')
}
