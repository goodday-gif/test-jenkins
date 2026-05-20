import request from '@/utils/request'

export const getCoachList = (params: any) => request.get('/front/coach/list', { params })
export const getCoachDetail = (id: number) => request.get(`/front/coach/${id}`)
