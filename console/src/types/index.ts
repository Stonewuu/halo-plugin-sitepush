export interface Metadata {
    name: string;
    labels?: {
        [key: string]: string;
    } | null;
    annotations?: {
        [key: string]: string;
    } | null;
    version?: number | null;
    creationTimestamp?: string | null;
    deletionTimestamp?: string | null;
}

/**
 *
 */
export interface PushLogs {
    createTime: number;
    pushUrl: string;
    pushType: string;
    pushStatus: number;
    apiVersion: "todo.plugin.halo.run/v1alpha1"; // apiVersion=自定义模型的 group/version
    kind: "PushLog";
    metadata: Metadata;
}

/**
 *
 */
export interface PushLogsList {
    page: number;
    size: number;
    total: number;
    items: Array<PushLogs>;
    first: boolean;
    last: boolean;
    hasNext: boolean;
    hasPrevious: boolean;
    totalPages: number;
}