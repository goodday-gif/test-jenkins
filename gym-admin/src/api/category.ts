import request from '@/utils/request'

export function getCategoryList() { return request.get('/admin/category/list') }
export function addCategory(data: any) { return request.post('/admin/category', data) }
export function updateCategory(data: any) { return request.put('/admin/category', data) }
export function deleteCategory(id: number) { return request.delete(`/admin/category/${id}`) }
