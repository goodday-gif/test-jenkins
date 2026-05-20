import request from '@/utils/request'

// 发表评价
export function addReview(data: any) {
  return request.post('/front/review', data)
}

// 获取评价列表
export function getReviewList(params: any) {
  return request.get('/front/review/list', { params })
}

// 获取评价统计
export function getReviewStats(params: any) {
  return request.get('/front/review/stats', { params })
}

// 检查是否已评价
export function checkReview(params: any) {
  return request.get('/front/review/check', { params })
}

// 删除评价
export function deleteReview(id: number) {
  return request.delete(`/front/review/${id}`)
}
