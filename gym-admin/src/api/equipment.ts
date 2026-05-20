import request from '@/utils/request'

export function getEquipmentList(params: any) { return request.get('/admin/equipment/list', { params }) }
export function getEquipmentDetail(id: number) { return request.get(`/admin/equipment/${id}`) }
export function addEquipment(data: any) { return request.post('/admin/equipment', data) }
export function updateEquipment(data: any) { return request.put('/admin/equipment', data) }
export function updateEquipmentStatus(id: number, status: number) { return request.put(`/admin/equipment/status/${id}`, null, { params: { status } }) }
export function deleteEquipment(id: number) { return request.delete(`/admin/equipment/${id}`) }
