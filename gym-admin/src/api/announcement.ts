import request from '@/utils/request'

export function getAnnouncementList(params: any) { return request.get('/admin/announcement/list', { params }) }
export function getAnnouncementDetail(id: number) { return request.get(`/admin/announcement/${id}`) }
export function addAnnouncement(data: any) { return request.post('/admin/announcement', data) }
export function updateAnnouncement(data: any) { return request.put('/admin/announcement', data) }
export function publishAnnouncement(id: number) { return request.put(`/admin/announcement/publish/${id}`) }
export function toggleTopAnnouncement(id: number) { return request.put(`/admin/announcement/top/${id}`) }
export function deleteAnnouncement(id: number) { return request.delete(`/admin/announcement/${id}`) }
