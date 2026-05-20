import request from '@/utils/request'

export const updateProfile = (data: any) => request.put('/front/member/profile', data)
export const updatePassword = (data: any) => request.put('/front/member/password', data)
export const getMyTutorials = (params: any) => request.get('/front/member/tutorials', { params })
export const getMyOrders = (params: any) => request.get('/front/member/orders', { params })
