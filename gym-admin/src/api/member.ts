import request from '@/utils/request'

export function getMemberList(params: any) { return request.get('/admin/member/list', { params }) }
export function getMemberDetail(id: number) { return request.get(`/admin/member/${id}`) }
export function rechargeMember(data: any) { return request.post('/admin/member/recharge', data) }
export function renewMember(data: any) { return request.post('/admin/member/renew', data) }
export function toggleMemberStatus(id: number) { return request.put(`/admin/member/status/${id}`) }
