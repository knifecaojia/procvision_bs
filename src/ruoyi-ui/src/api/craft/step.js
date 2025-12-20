import request from '@/utils/request'

// 查询工步信息列表
export function listStep(query) {
  return request({
    url: '/craft/step/list',
    method: 'get',
    params: query
  })
}

// 查询工步信息详细
export function getStep(id) {
  return request({
    url: '/craft/step/' + id,
    method: 'get'
  })
}

// 新增工步信息
export function addStep(data) {
  return request({
    url: '/craft/step',
    method: 'post',
    data: data
  })
}

// 修改工步信息
export function updateStep(data) {
  return request({
    url: '/craft/step',
    method: 'put',
    data: data
  })
}

// 删除工步信息
export function delStep(id) {
  return request({
    url: '/craft/step/' + id,
    method: 'delete'
  })
}
