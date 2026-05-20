import request from '@/utils/request'

export const getAnnouncementList = (params: any) => request.get('/front/announcement/list', { params })
export const getAnnouncementDetail = (id: number) => request.get(`/front/announcement/${id}`)
