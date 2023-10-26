import request from '@/api/request'

export const getLogListApi = (params: object) => {
    return request({
        url: '/apis/sitepush.halo.run/v1alpha1/pushLogs',
        method: 'get',
        params
    })
}


export const clearAllLogApi = () => {
  return request({
    url: '/apis/api.plugin.halo.run/v1alpha1/plugins/PluginSitePush/pushLogs/clear',
    method: 'delete'
  })
}
