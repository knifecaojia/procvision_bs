import request from '@/utils/request'

// 查询过程记录列表
export function listRecord(query) {
  return request({
    url: '/process/record/list',
    method: 'get',
    params: query
  })
}

// 查询过程记录详细
export function getRecord(id) {
  return request({
    url: '/process/record/' + id,
    method: 'get'
  })
}

// 新增过程记录
export function addRecord(data) {
  return request({
    url: '/process/record',
    method: 'post',
    data: data
  })
}

// 修改过程记录
export function updateRecord(data) {
  return request({
    url: '/process/record',
    method: 'put',
    data: data
  })
}

// 删除过程记录
export function delRecord(id) {
  return request({
    url: '/process/record/' + id,
    method: 'delete'
  })
}
