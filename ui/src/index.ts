import { definePlugin } from "@halo-dev/console-shared";
import { IconUpload } from "@halo-dev/components";
import { markRaw } from "vue";
import PushLog from "@/views/PushLog.vue";

export default definePlugin({
  components: {},
  routes: [
    {
      parentName: "ToolsRoot",
      route: {
        path: "site-push-log",
        name: "SitePushLog",
        component: PushLog,
        meta: {
          title: "站点收录推送",
          searchable: true,
          description: "查阅站点收录推送日志",
          permissions: ["plugin:sitepush:view"],
          menu: {
            name: "站点收录推送",
            group: "tool",
            icon: markRaw(IconUpload),
            priority: 0,
          },
        },
      },
    },
  ],
  extensionPoints: {},
});
