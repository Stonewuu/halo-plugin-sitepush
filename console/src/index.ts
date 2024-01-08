import { definePlugin } from "@halo-dev/console-shared";
import { IconUpload } from "@halo-dev/components";
import { markRaw } from "vue";
import PushLog from "@/views/PushLog.vue";

export default definePlugin({
  name: "PluginSitePush",
  components: {},
  routes: [
    {
      parentName: "Root",
      route: {
        path: "/pushLog",
        name: "站点收录推送",
        component: PushLog,
        meta: {
          title: "站点收录推送",
          searchable: true,
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
