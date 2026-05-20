import request from '@/utils/request'

export function getOverview() { return request.get('/admin/statistics/overview') }
export function getMemberGrowth(params?: any) { return request.get('/admin/statistics/member-growth', { params }) }
export function getRevenue(params?: any) { return request.get('/admin/statistics/revenue', { params }) }
export function getCourseReserve() { return request.get('/admin/statistics/course-reserve') }
export function getCoursePlay() { return request.get('/admin/statistics/course-play') }
