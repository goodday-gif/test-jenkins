import request from '@/utils/request'

export function getClassList(params: any) { return request.get('/admin/gym-class/list', { params }) }
export function getClassDetail(id: number) { return request.get(`/admin/gym-class/${id}`) }
export function addClass(data: any) { return request.post('/admin/gym-class', data) }
export function updateClass(data: any) { return request.put('/admin/gym-class', data) }
export function cancelClass(id: number) { return request.put(`/admin/gym-class/cancel/${id}`) }
export function deleteClass(id: number) { return request.delete(`/admin/gym-class/${id}`) }
