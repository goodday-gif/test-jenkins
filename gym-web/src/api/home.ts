import request from '@/utils/request'

export const getHomeStats = () => request.get('/front/home/stats')
export const getHotCourses = () => request.get('/front/home/hot-courses')
export const getHotTutorials = () => request.get('/front/home/hot-tutorials')
export const getLatestAnnouncements = () => request.get('/front/home/latest-announcements')
export const getBanners = () => request.get('/front/home/banner')
