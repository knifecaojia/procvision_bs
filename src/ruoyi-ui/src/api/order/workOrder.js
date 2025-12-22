import request from '@/utils/request'

// 查询工单列表
export function listWorkOrder(query) {
  return request({
    url: '/workOrder/list',
    method: 'get',
    params: query
  })
}

// 查询工单详细
export function getWorkOrder(id) {
  return request({
    url: '/workOrder/' + id,
    method: 'get'
  })
}

// 新增工单
export function addWorkOrder(data) {
  return request({
    url: '/workOrder',
    method: 'post',
    data: data
  })
}

// 修改工单
export function updateWorkOrder(data) {
  return request({
    url: '/workOrder',
    method: 'put',
    data: data
  })
}

// 删除工单
export function delWorkOrder(id) {
  return request({
    url: '/workOrder/' + id,
    method: 'delete'
  })
}
