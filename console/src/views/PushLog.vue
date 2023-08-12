<script setup lang="ts">
import axios from "axios";
import type {PushLogs, PushLogsList} from "../types";
import {computed, onMounted, ref} from "vue";

const http = axios.create({
  baseURL: "/",
  timeout: 1000,
});

const pushLogsList = ref<PushLogsList>({
  page: 1,
  size: 20,
  total: 0,
  items: [],
  first: true,
  last: false,
  hasNext: false,
  hasPrevious: false,
  totalPages: 0,
});

const tabs = [
  {
    label: "全部",
  },
  {
    label: "Active",
  },
  {
    label: "Completed",
  },
];

const activeTab = ref("All");

/**
 * 列表展示的数据
 */
const pushLogs = computed(() => {
  return pushLogsList.value.items;
});

onMounted(handleFetchLogs);

// 查看 http://localhost:8090/swagger-ui.html
function handleFetchLogs() {
  http
      .get<PushLogsList>("/apis/sitepush.halo.run/v1alpha1/pushLogs")
      .then((response) => {
        pushLogsList.value = response.data;
      });
}
function dateFormat (timestamp: number|string|Date, format = 'YYYY-MM-DD HH:mm:ss'): string {
  var date = new Date(timestamp)
  function fixedTwo (value: number): string {
    return value < 10 ? '0' + value : String(value)
  }
  var showTime = format
  if (showTime.includes('SSS')) {
    const S = date.getMilliseconds()
    showTime = showTime.replace('SSS', '0'.repeat(3 - String(S).length) + S)
  }
  if (showTime.includes('YY')) {
    const Y = date.getFullYear()
    showTime = showTime.includes('YYYY') ? showTime.replace('YYYY', String(Y)) : showTime.replace('YY', String(Y).slice(2, 4))
  }
  if (showTime.includes('M')) {
    const M = date.getMonth() + 1
    showTime = showTime.includes('MM') ? showTime.replace('MM', fixedTwo(M)) : showTime.replace('M', String(M))
  }
  if (showTime.includes('D')) {
    const D = date.getDate()
    showTime = showTime.includes('DD') ? showTime.replace('DD', fixedTwo(D)) : showTime.replace('D', String(D))
  }
  if (showTime.includes('H')) {
    const H = date.getHours()
    showTime = showTime.includes('HH') ? showTime.replace('HH', fixedTwo(H)) : showTime.replace('H', String(H))
  }
  if (showTime.includes('m')) {
    var m = date.getMinutes()
    showTime = showTime.includes('mm') ? showTime.replace('mm', fixedTwo(m)) : showTime.replace('m', String(m))
  }
  if (showTime.includes('s')) {
    var s = date.getSeconds()
    showTime = showTime.includes('ss') ? showTime.replace('ss', fixedTwo(s)) : showTime.replace('s', String(s))
  }
  return showTime
}
</script>

<template>
  <div class="flex items-center justify-between bg-white p-4 h-14">
    <div class="min-w-0 flex-1 self-center">
      <h2 class="flex items-center truncate text-xl font-bold text-gray-800">
        <svg viewBox="0 0 20 20" width="1.2em" height="1.2em" class="mr-2 self-center"><g fill="none" fill-rule="evenodd" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"><path d="M17.5 14.5v-7l-5-5h-5a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2z"/><path d="M11.5 6.5v4h3m-9-6a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2"/></g></svg>
        <span>推送日志</span>
      </h2>
    </div>
  </div>
  <div class="m-0 md:m-4">
    <div class="card-wrapper">
      <div class="card-header">
        <div class="tabbar-outline tabbar-direction-row tabbar-wrapper w-full !rounded-none">
          <div class="tabbar-items">
            <div class="tabbar-item-active tabbar-item">
              <div class="tabbar-item-label">日志</div>
            </div>
          </div>
        </div>
      </div>
      <div class="!p-0 card-body">
        <div style="padding: 10px;">
          <table style="width: 100%; text-align: left">
            <tr>
              <th>时间</th>
              <th>推送类型</th>
              <th>推送地址</th>
              <th>结果</th>
            </tr>
            <tr v-for="(log, index) in pushLogs"
                class="todo"
                :key="index">
              <td>{{ dateFormat(log.createTime * 1000, 'YYYY-MM-DD HH:mm:ss') }}</td>
              <td>{{ log.pushType }}</td>
              <td>{{ log.pushUrl }}</td>
              <td>{{ log.pushStatus == 1 ? '成功' : log.pushStatus == 0 ? '失败' : '跳过' }}</td>
            </tr>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>

.wrapper {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  gap: 1.5rem;

}
</style>
