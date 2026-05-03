import request from '@/utils/request'

// 获取顶部 KPI 统计指标
export function getKpiStats() {
    return request({
        url: '/dashboard/kpiStats',
        method: 'get'
    })
}

// 获取 ECharts 分析数据 (包含趋势、状态分布、缺陷类型等)
export function getChartAnalysis(query) {
    return request({
        url: '/dashboard/chartAnalysis',
        method: 'get',
        params: query
    })
}

// 获取最新装配实拍结果流
export function getRecentResults() {
    return request({
        url: '/dashboard/recentResults',
        method: 'get'
    })
}