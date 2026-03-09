import request from '@/utils/request'

// 查询工艺信息列表
export function listCraft(query) {
  return request({
    url: '/craft/info/list',
    method: 'get',
    params: query
  })
}

// 查询工艺信息详细
export function getCraft(id) {
  return request({
    url: '/craft/info/' + id,
    method: 'get'
  })
}

// 新增工艺信息
export function addCraft(data) {
  return request({
    url: '/craft/info',
    method: 'post',
    data: data
  })
}

// 修改工艺信息
export function updateCraft(data) {
  return request({
    url: '/craft/info',
    method: 'put',
    data: data
  })
}

// 删除工艺信息
export function delCraft(id) {
  return request({
    url: '/craft/info/' + id,
    method: 'delete'
  })
}

export function changeStatus(id) {
  return request({
    url: '/craft/info/checkStatus/' + id,
    method: 'get'
  })
}

export function getCraftSelector(){
    return request({
        url: '/craft/info/craftSelector',
        method: 'get'
    })
}

export function checkCamera(){
    return request({
        url: '/camera/check',
        method: 'get',
    })
}

export function importFromMOM(formData){
    return request({
        url: '/craft/info/getCraftFromMMO',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}