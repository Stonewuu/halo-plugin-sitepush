import type { Metadata } from "@halo-dev/api-client";

/**
 *
 */
export interface PushLogs {
  createTime: number;
  pushUrl: string;
  pushType: "baidu" | "bing" | "google";
  pushStatus: number;
  apiVersion: "todo.plugin.halo.run/v1alpha1"; // apiVersion=自定义模型的 group/version
  kind: "PushLog";
  metadata: Metadata;
  remark: string;
}

/**
 *
 */
export interface PushLogsList<T> {
  page: number;
  size: number;
  total: number;
  items: Array<T>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  totalPages: number;
}
