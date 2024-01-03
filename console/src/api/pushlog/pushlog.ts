import request from "@/api/request";
import type { PushLogs, PushLogsList } from "@/types";

export const getLogListApi = (params: object) => {
  return request<PushLogsList<PushLogs>>({
    url: "/apis/sitepush.halo.run/v1alpha1/pushLogs",
    method: "get",
    params,
  });
};

export const clearAllLogApi = () => {
  return request({
    url: "/apis/api.plugin.halo.run/v1alpha1/plugins/PluginSitePush/pushLogs/clear",
    method: "delete",
  });
};
