# hbotsdk-ui

基于 Vue 3 + Vite + Ant Design Vue 的聊天助手前端项目。

## 功能特性

- 💬 流式聊天对话
- 📁 文件上传支持（图片）
- 💾 会话管理（新建、删除、重命名）
- 📝 Markdown 渲染
- 🎨 现代化 UI 设计

## Recommended IDE Setup

[VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Recommended Browser Setup

- Chromium-based browsers (Chrome, Edge, Brave, etc.):
  - [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd) 
  - [Turn on Custom Object Formatter in Chrome DevTools](http://bit.ly/object-formatters)
- Firefox:
  - [Vue.js devtools](https://addons.mozilla.org/en-US/firefox/addon/vue-js-devtools/)
  - [Turn on Custom Object Formatter in Firefox DevTools](https://fxdx.dev/firefox-devtools-custom-object-formatters/)

## Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) to make the TypeScript language service aware of `.vue` types.

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## 项目设置

### 安装依赖

```sh
npm install
```

### 环境配置

在项目根目录创建 `.env` 文件（或参考 `.env.example`）：

```env
# API 基础地址
VITE_API_BASE_URL=http://localhost:8080

# 应用 ID（需要替换为实际的应用 ID）
VITE_APP_ID=your-app-id

# 用户 ID（可选，默认为 default-user）
VITE_USER_ID=default-user
```

### 开发模式

```sh
npm run dev
```

访问 http://localhost:5173/chat 查看聊天页面。

### 生产构建

```sh
npm run build
```

## 项目结构

```
src/
├── api/           # API 服务
│   └── chat.ts   # 聊天 API
├── views/         # 页面组件
│   └── Chat.vue   # 聊天助手页面
├── router/        # 路由配置
└── stores/        # 状态管理
```

## 使用说明

1. 确保后端服务已启动（默认端口 8080）
2. 配置 `.env` 文件中的 `VITE_APP_ID` 为实际的应用 ID
3. 启动前端开发服务器：`npm run dev`
4. 访问聊天页面开始对话

## 技术栈

- Vue 3
- TypeScript
- Vite
- Ant Design Vue 4.x
- Pinia
- Vue Router
- Marked (Markdown 渲染)
