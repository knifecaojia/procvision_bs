import request from '@/utils/request'

// 查询工序信息列表
export function listProcess(query) {
  return request({
    url: '/craft/process/list',
    method: 'get',
    params: query
  })
}

// 查询工序信息详细
export function getProcess(id) {
  return request({
    url: '/craft/process/' + id,
    method: 'get'
  })
}

// 新增工序信息
export function addProcess(data) {
  return request({
    url: '/craft/process',
    method: 'post',
    data: data
  })
}

// 修改工序信息
export function updateProcess(data) {
  return request({
    url: '/craft/process',
    method: 'put',
    data: data
  })
}

// 删除工序信息
export function delProcess(id) {
  return request({
    url: '/craft/process/' + id,
    method: 'delete'
  })
}

export function bindProcessAlgorithm(id, algId){
    return request({
        url: '/craft/process/bindAlg/'+id+'/' + algId,
        method: 'get'
    })
}

export function changeExceptionCheck(id, status){
    const data = {
        id: id,
        exceptionCheck: status
    }
    return request({
        url: '/craft/process/changeExceptionStatus',
        method: 'post',
        data: data
    })
}

export function changeFinalCheckCheck(id, status){
    const data = {
        id: id,
        finalCheck: status
    }
    return request({
        url: '/craft/process/changeFinalStatus',
        method: 'post',
        data: data
    })
}