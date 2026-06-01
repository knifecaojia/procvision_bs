import request from '@/utils/request'

// 查询系统异常日志列表
export function listErrlog(query) {
  return request({
    url: '/system/errlog/list',
    method: 'get',
    params: query
  })
}

// 查询系统异常日志详细
export function getErrlog(id) {
  return request({
    url: '/system/errlog/' + id,
    method: 'get'
  })
}

// 新增系统异常日志
export function addErrlog(data) {
  return request({
    url: '/system/errlog',
    method: 'post',
    data: data
  })
}

// 修改系统异常日志
export function updateErrlog(data) {
  return request({
    url: '/system/errlog',
    method: 'put',
    data: data
  })
}

// 删除系统异常日志
export function delErrlog(id) {
  return request({
    url: '/system/errlog/' + id,
    method: 'delete'
  })
}

export function cleanErrLog(){
    return request({
        url: '/system/errlog/clean',
        method: 'delete'
    })
}