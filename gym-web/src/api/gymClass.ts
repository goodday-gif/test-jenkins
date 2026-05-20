import request from '@/utils/request'

export const getClassList = (params: any) => request.get('/front/class/list', { params })
export const getClassDetail = (id: number) => request.get(`/front/class/${id}`)
export const reserveClass = (id: number) => request.post(`/front/class/${id}/reserve`)
export const cancelReserve = (id: number) => request.delete(`/front/class/reserve/${id}`)
export const getMyReserves = (params: any) => request.get('/front/class/my-reserves', { params })
