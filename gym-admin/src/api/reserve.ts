import request from '@/utils/request'

export function getReserveList(params: any) { return request.get('/admin/reserve/list', { params }) }
export function checkInReserve(id: number) { return request.put(`/admin/reserve/check-in/${id}`) }
export function cancelReserve(id: number) { return request.put(`/admin/reserve/cancel/${id}`) }
