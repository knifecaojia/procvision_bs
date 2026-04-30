import request from '@/utils/request'

// 查询数据采集列表
export function listDataset(query) {
    return request({
        url: '/collection/dataset/list',
        method: 'get',
        params: query
    })
}

// 查询数据采集详细
export function getData(id) {
    return request({
        url: '/collection/dataset/' + id,
        method: 'get'
    })
}

// 新增数据采集
export function addData(data) {
    return request({
        url: '/collection/dataset',
        method: 'post',
        data: data
    })
}

// 修改数据采集
export function updateData(data) {
    return request({
        url: '/collection/dataset',
        method: 'put',
        data: data
    })
}

// 删除数据采集
export function delData(id) {
    return request({
        url: '/collection/dataset/' + id,
        method: 'delete'
    })
}

export function uploadData(formData) {
    return request({
        url: '/collection/dataset/process',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}
