import request from '@/utils/request'

export function getCourseList(params: any) { return request.get('/admin/fitness-course/list', { params }) }
export function getCourseDetail(id: number) { return request.get(`/admin/fitness-course/${id}`) }
export function addCourse(data: any) { return request.post('/admin/fitness-course', data) }
export function updateCourse(data: any) { return request.put('/admin/fitness-course', data) }
export function publishCourse(id: number) { return request.put(`/admin/fitness-course/publish/${id}`) }
export function offlineCourse(id: number) { return request.put(`/admin/fitness-course/offline/${id}`) }
export function deleteCourse(id: number) { return request.delete(`/admin/fitness-course/${id}`) }
