import request from '@/utils/request'

export const getEquipmentList = (params: any) => request.get('/front/equipment/list', { params })
export const getEquipmentCategories = () => request.get('/front/equipment/categories')
