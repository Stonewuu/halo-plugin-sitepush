# halo-plugin-sitepush

Halo 2.x 搜索引擎主动推送插件

## 功能简介
该插件可将站点内文章模块、页面模块的链接，推送至各大搜索引擎收录平台

### 功能特性
- 支持站点收录平台的验证
- 支持推送站点链接至各大搜索引擎收录平台

### 当前支持的收录平台：
- [x] [百度收录](https://ziyuan.baidu.com)
- [x] [必应(bing)收录](https://www.bing.com/webmasters)
- [x] [谷歌(google)收录](https://search.google.com/search-console/)
- [ ] 更多 (欢迎PR)

## 安装方式
- 在 [release页面](https://github.com/Stonewuu/halo-plugin-sitepush/releases) 下载最新的 JAR 文件。
- 在 Halo 后台的插件管理上传 JAR 文件进行安装。

## 使用说明
- 安装完成后，在 Halo 后台的 `插件` -> `站点推送插件` 配置页面，进行配置。
- 推送的时机是：插件启动时、 页面与文章的发布时
- 谷歌推送需要网络能访问谷歌，其他推送同理

![插件截图](https://github.com/Erzbir/halo-plugin-sitepush/assets/100007608/0f258f18-1e2d-4d6d-b7ca-7c8aee8ffc9f)

## 配置说明
- 如果是通过 dns 记录或者其他方式已经认证，则不需要网站验证码
- 此插件的推送支持代理（支持无认证和 basic auth），配置此项之后需确保代理可用
  


## 参与开发

插件开发的详细文档请查阅：<https://docs.halo.run/developer-guide/plugin/hello-world>

```bash
git clone https://github.com/Stonewuu/halo-plugin-sitepush.git

# 或者当你 fork 之后

git clone https://github.com/{your_github_id}/halo-plugin-sitepush.git
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
