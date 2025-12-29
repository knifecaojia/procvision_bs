import request from '@/utils/request'

// 查询算法列表
export function listAlgorithm(query) {
    return request({
        url: '/algorithm/list',
        method: 'get',
        params: query
    })
}

// 查询算法详细
export function getAlgorithm(id) {
    return request({
        url: '/algorithm/' + id,
        method: 'get'
    })
}

// 新增算法
export function addAlgorithm(data) {
    return request({
        url: '/algorithm',
        method: 'post',
        data: data
    })
}

// 修改算法
export function updateAlgorithm(data) {
    return request({
        url: '/algorithm',
        method: 'put',
        data: data,
    })
}

// 删除算法
export function delAlgorithm(id) {
    return request({
        url: '/algorithm/' + id,
        method: 'delete'
    })
}

export function getUploadUrl(){
    return request({
        url: '/algorithm/getUrl',
        method: 'get'
    })
}

export function removeUploadFile(objectName){
    return request({
        url: '/algorithm/remove',
        method: 'post',
        params:{
            'objectName':objectName
        }
    })
}
