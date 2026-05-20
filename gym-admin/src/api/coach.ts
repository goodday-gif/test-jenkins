import request from '@/utils/request'

export function getCoachList(params: any) { return request.get('/admin/coach/list', { params }) }
export function getCoachDetail(id: number) { return request.get(`/admin/coach/${id}`) }
export function addCoach(data: any) { return request.post('/admin/coach', data) }
export function updateCoach(data: any) { return request.put('/admin/coach', data) }
export function deleteCoach(id: number) { return request.delete(`/admin/coach/${id}`) }
