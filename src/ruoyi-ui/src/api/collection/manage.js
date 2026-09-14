import request from '@/utils/request'

export const listManagedData = params => request({ url: '/collection/manage/list', method: 'get', params, timeout: 60000 })
export const getManagedStorage = id => request({ url: `/collection/manage/${id}/storage`, method: 'get', timeout: 60000 })
export const convertManagedStorage = data => request({ url: '/collection/manage/storage', method: 'put', data, timeout: 300000 })

export const exportManagedImages = data => request({ url: '/collection/manage/export-images', method: 'post', data, responseType: 'blob', timeout: 600000 })
