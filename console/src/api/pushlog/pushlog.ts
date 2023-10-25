import request from '@/api/request'

export const getLogListApi = (params: object) => {
    return request({
        url: '/apis/sitepush.halo.run/v1alpha1/pushLogs',
        method: 'get',
        params
    })
}
