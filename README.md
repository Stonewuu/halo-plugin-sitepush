# halo-plugin-sitepush

Halo 2.x 搜索引擎主动推送插件

## 功能简介
该插件可将站点内文章模块、页面模块的链接，推送至各大搜索引擎收录平台
当前支持的收录平台：
- [x] [百度收录](https://ziyuan.baidu.com)
- [ ] 更多

## 使用方式
- 在 [release页面](https://github.com/Stonewuu/halo-plugin-sitepush/releases) 下载最新的 JAR 文件。
- 在 Halo 后台的插件管理上传 JAR 文件进行安装。

## 参与开发

插件开发的详细文档请查阅：<https://docs.halo.run/developer-guide/plugin/hello-world>

```bash
git clone https://github.com/Stonewuu/halo-plugin-sitepush.git

# 或者当你 fork 之后

git clone git@github.com:{your_github_id}/halo-plugin-sitepush.git
```

```bash
cd path/to/halo-plugin-sitepush
```

```bash
# macOS / Linux
./gradlew build

# Windows
./gradlew.bat build
```

修改 Halo 配置文件：

```yaml
halo:
  plugin:
    runtime-mode: development
    fixedPluginPath:
      - "/path/to/halo-plugin-sitepush"
```