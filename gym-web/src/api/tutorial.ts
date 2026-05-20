import request from '@/utils/request'

export const getTutorialList = (params: any) => request.get('/front/tutorial/list', { params })
export const getTutorialCategories = () => request.get('/front/tutorial/categories')
export const getTutorialDetail = (id: number) => request.get(`/front/tutorial/${id}`)
export const likeTutorial = (id: number) => request.post(`/front/tutorial/${id}/like`)
export const collectTutorial = (id: number) => request.post(`/front/tutorial/${id}/collect`)
export const commentTutorial = (id: number, content: string) => request.post(`/front/tutorial/${id}/comment`, { content })
export const getTutorialComments = (id: number, params: any) => request.get(`/front/tutorial/${id}/comments`, { params })
export const cancelInteract = (id: number) => request.delete(`/front/tutorial/interact/${id}`)
