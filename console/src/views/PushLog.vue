<script setup lang="ts">
import {getLogListApi} from '@/api/pushlog/pushlog'
import type {PushLogs, PushLogsList} from "../types";
import {nextTick, onMounted, reactive, ref, toRefs, watch} from "vue";
import {IconArrowLeft, IconArrowRight, VPagination,} from "@halo-dev/components";

const pushLogsList = reactive<PushLogsList<PushLogs>>({
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

const {page, size, total, items, first, last, hasNext, hasPrevious, totalPages} = toRefs(pushLogsList)


const loadData = async (logList: PushLogsList<PushLogs>) => {
  // 清空表格数据
  logList.items = []
  // 封装参数
  const params = {
    page: logList.page,
    size: logList.size
  }

  const {data} = await getLogListApi(params)

  items.value = data.items;
  total.value = data.total;
  first.value = data.first;
  last.value = data.last;
  hasNext.value = data.hasNext;
  hasPrevious.value = data.hasPrevious;
  totalPages.value = data.totalPages;

}

onMounted(() => loadData(pushLogsList))

const refresh = () => {
  page.value = 1;
  loadData(pushLogsList)
}

const refetch = async () => {
  await loadData(pushLogsList)
}

const changeSize = (pageSize: number) => {
  if (pageSize > total.value) {
    return;
  }
  size.value = pageSize;
  loadData(pushLogsList);
}

const changePage = (pageIndex: number) => {
  page.value = pageIndex
  loadData(pushLogsList)
}

watch(size, () => changeSize(size.value))
watch(page, () => changePage(page.value))

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

// 查看 http://localhost:8090/swagger-ui.html

function dateFormat(timestamp: number | string | Date, format = 'YYYY-MM-DD HH:mm:ss'): string {
  var date = new Date(timestamp)

  function fixedTwo(value: number): string {
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
        <svg viewBox="0 0 20 20" width="1.2em" height="1.2em" class="mr-2 self-center">
          <g fill="none" fill-rule="evenodd" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17.5 14.5v-7l-5-5h-5a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2z"/>
            <path d="M11.5 6.5v4h3m-9-6a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2"/>
          </g>
        </svg>
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
            <tr v-for="(item, index) in pushLogsList.items"
                class="todo"
                :key="index">
              <td>{{ dateFormat(item.createTime * 1000, 'YYYY-MM-DD HH:mm:ss') }}</td>
              <td>{{ item.pushType }}</td>
              <td>{{ item.pushUrl }}</td>
              <td>{{ item.pushStatus == 1 ? '成功' : item.pushStatus == 0 ? '失败' : '跳过' }}</td>
            </tr>
          </table>
        </div>
        <VPagination
          v-model:page="page"
          v-model:size="size"
          :page-label="$t('core.components.pagination.page_label')"
          :size-label="$t('core.components.pagination.size_label')"
          :total-label="$t('core.components.pagination.total_label', { total: total })"
          :total="total"
          :size-options="[10, 20, 30, 50, 100]"
        />
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
