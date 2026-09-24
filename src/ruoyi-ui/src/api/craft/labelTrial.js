import request from '@/utils/request'

export const getTrialStep = id => request({url: `/craft/label-trial/${id}`, method: 'get'})
export const getTrialUpload = id => request({
    url: `/craft/label-trial/${id}/upload`,
    method: 'post',
    headers: {repeatSubmit: false}
})
export const saveTrialLabel = data => request({
    url: '/craft/label-trial/save',
    method: 'post',
    data,
    timeout: 600000,
    headers: {repeatSubmit: false}
})
