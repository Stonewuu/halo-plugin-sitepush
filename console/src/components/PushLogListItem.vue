<script lang="ts" setup>
import { timeAgo, formatDatetime } from "@/utils/date";
import type { PushLogs } from "../types";
import { VEntity, VEntityField, VStatusDot } from "@halo-dev/components";
import { computed, type Component } from "vue";
import SimpleIconsBaidu from "~icons/simple-icons/baidu";
import LogosGoogleIcon from "~icons/logos/google-icon";
import LogosBing from "~icons/logos/bing";

const props = withDefaults(
  defineProps<{
    pushLog: PushLogs;
    isSelect: boolean;
  }>(),
  {}
);

interface SearchEngine {
  type: "baidu" | "google" | "bing";
  name: string;
  icon: Component;
  color: string;
  checkUrl: string;
}

const searchEngines: SearchEngine[] = [
  {
    type: "baidu",
    name: "百度",
    icon: SimpleIconsBaidu,
    color: "#3385ff",
    checkUrl: `https://www.baidu.com/s?wd=${props.pushLog.pushUrl}`,
  },
  {
    type: "google",
    name: "谷歌",
    icon: LogosGoogleIcon,
    color: "#4285f4",
    checkUrl: `https://www.google.com/search?q=${props.pushLog.pushUrl}`,
  },
  {
    type: "bing",
    name: "必应",
    icon: LogosBing,
    color: "#008373",
    checkUrl: `https://cn.bing.com/search?q=${props.pushLog.pushUrl}`,
  },
];

const getSearchEngine = computed(() => {
  return searchEngines.find((item) => item.type === props.pushLog.pushType);
});

const state = computed(() => {
  const { pushStatus } = props.pushLog;
  if (pushStatus === 1) return "success";
  if (pushStatus === 0) return "warning";
  return "default";
});

const stateText = computed(() => {
  const { pushStatus } = props.pushLog;
  if (pushStatus === 1) return "成功";
  if (pushStatus === 0) return "失败";
  return "未知";
});
</script>

<template>
  <VEntity :is-selected="isSelect">
    <template #start>
      <VEntityField width="20rem" :description="pushLog.pushType">
        <template #description>
          <div class="flex flex-col gap-1 w-full">
            <div class="inline-flex gap-1">
              <component
                :is="getSearchEngine?.icon"
                class="text-xs"
                :style="{ color: getSearchEngine?.color }"
              />
              <span class="text-xs text-gray-600">
                {{ getSearchEngine?.name }}
              </span>
            </div>
            <a
              v-tooltip="'去搜索引擎检查是否搜录'"
              :href="getSearchEngine?.checkUrl"
              class="text-xs text-gray-600 truncate"
              target="_blank"
            >
              {{ pushLog.pushUrl }}
            </a>
          </div>
        </template>
      </VEntityField>
      <VEntityField :description="pushLog.pushStatus">
        <template #description>
          <VStatusDot :state="state" :text="stateText" />
        </template>
      </VEntityField>
    </template>
    <template #end>
      <VEntityField v-if="pushLog.metadata.deletionTimestamp">
        <template #description>
          <VStatusDot v-tooltip="'删除中'" state="warning" animate />
        </template>
      </VEntityField>
      <VEntityField
        v-tooltip="formatDatetime(pushLog.createTime * 1000)"
        :description="timeAgo(pushLog.createTime * 1000)"
      ></VEntityField>
    </template>
  </VEntity>
</template>
