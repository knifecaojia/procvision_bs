import request from '@/utils/request'

// 查询工序步骤过滤规则列表
export function listFilterRule(query) {
    return request({
        url: '/filterRule/list',
        method: 'get',
        params: query
    })
}

// 查询工序步骤过滤规则详细
export function getFilterRule(id) {
    return request({
        url: '/filterRule/' + id,
        method: 'get'
    })
}

// 新增过滤规则
export function addFilterRule(data) {
    return request({
        url: '/filterRule',
        method: 'post',
        data: data
    })
}

// 修改过滤规则
export function updateFilterRule(data) {
    return request({
        url: '/filterRule',
        method: 'put',
        data: data
    })
}

// 删除过滤规则
export function delFilterRule(id) {
    return request({
        url: '/filterRule/' + id,
        method: 'delete'
    })
}

// 修改启用状态
export function changeRuleStatus(id, isEnabled) {
    const data = {
        id,
        isEnabled
    }
    return request({
        url: '/filterRule/changeStatus',
        method: 'put',
        data: data
    })
}

// 获取可选产品类型与工序联动数据
export function getProductProcessOptions() {
    return request({
        url: '/filterRule/cascadeOptions',
        method: 'get'
    })
}

export function refreshCache() {
    return request({
        url: '/filterRule/refreshCache',
        method: 'delete'
    })
}