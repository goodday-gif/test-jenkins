import request from '@/utils/request'

export function getOrderList(params: any) { return request.get('/admin/order/list', { params }) }
export function getOrderDetail(id: number) { return request.get(`/admin/order/${id}`) }
export function refundOrder(id: number) { return request.put(`/admin/order/refund/${id}`) }
export function updateOrderStatus(id: number, payStatus: number) { return request.put(`/admin/order/status/${id}`, null, { params: { payStatus } }) }
