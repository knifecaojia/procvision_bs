import request from '@/utils/request'

export const getResultOverview = params => request({ url: '/dashboard/overview', method: 'get', params, timeout: 60000 })
export const getKpiStats = params => request({ url: '/dashboard/kpiStats', method: 'get', params })
export const getChartAnalysis = params => request({ url: '/dashboard/chartAnalysis', method: 'get', params })
export const getRecentResults = params => request({ url: '/dashboard/recentResults', method: 'get', params, timeout: 60000 })
export const getResultReport = params => request({ url: '/dashboard/reportData', method: 'get', params, timeout: 60000 })
export const getResultImage = id => request({ url: `/dashboard/records/${encodeURIComponent(id)}/image`, method: 'get', params: { report: true }, responseType: 'blob', timeout: 300000 })
