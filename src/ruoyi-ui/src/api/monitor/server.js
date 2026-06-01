import request from '@/utils/request'

// 获取服务信息
export function getServer() {
  return request({
    url: '/monitor/server',
    method: 'get'
  })
}

export function backupDatabase() {
    return request({
        url: '/monitor/server/backupDb',
        method: 'post'
    })
}