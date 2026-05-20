import request from '@/utils/request'

export function login(data: { username: string; password: string }) {
  return request.post('/admin/login', data)
}

export function getAdminInfo() {
  return request.get('/admin/info')
}
