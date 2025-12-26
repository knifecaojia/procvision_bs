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
    // const formData = new FormData();
    // formData.append('code', data.code);
    // formData.append('name', data.name);
    // formData.append('version', data.version);
    // formData.append('desc', data.desc);
    //
    // if (data.file) {
    //     formData.append('file', data.file);
    // }

    return request({
        url: '/algorithm',
        method: 'post',
        data: data,
        // headers: {
        //     'Content-Type': 'multipart/form-data'
        // }
    })
}

// 修改算法
export function updateAlgorithm(data) {
    const formData = new FormData();
    formData.append('code', data.code);
    formData.append('name', data.name);
    formData.append('version', data.version);
    formData.append('desc', data.desc);
    formData.append('id', data.id)

    if (data.file) {
        formData.append('file', data.file);
    }

    return request({
        url: '/algorithm',
        method: 'put',
        data: data,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

// 删除算法
export function delAlgorithm(id) {
    return request({
        url: '/algorithm/' + id,
        method: 'delete'
    })
}

export function handleUpload(url, data){
    return request({
        url: url,
        method: 'put',
        data: data,
        // headers: {
        //     'Content-Type': 'application/octet-stream'
        // },
    })
}
