import request from '@/utils/request'

export function getCommentList(params: any) { return request.get('/admin/interact/comments', { params }) }
export function deleteComment(id: number) { return request.delete(`/admin/interact/comment/${id}`) }
