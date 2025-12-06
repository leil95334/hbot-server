# hbot-server

[![GitHub Repo](https://img.shields.io/badge/repo-leil95334/hbot--server-blue)](https://github.com/leil95334/hbot-server)

## 项目简介

**hbot-server** 是一个专注于 OCR（光学字符识别）服务的聊天机器人后端项目。前端采用 Vue + TypeScript 开发，具备强大的图片识别、用户交互和管理功能。后端基于 Java，实现高效、稳定的 OCR 处理和任务调度。项目为用户和开发者提供一站式图像文字识别解决方案，支持多场景、多设备接入。

## 技术栈

- **前端**
  - [Vue](https://vuejs.org/) (主要界面开发)
  - [TypeScript](https://www.typescriptlang.org/) (类型安全与代码质量)
  - [HTML](https://developer.mozilla.org/docs/Web/HTML)
- **后端**
  - [Java](https://www.java.com/) (服务端开发与 OCR 调度)

## 前端核心功能

- **图片上传与预处理**：支持拖拽或选择图片，预览和格式校验。
- **OCR识别**：一键发送图片到服务器进行文字识别，实时获取识别结果。
- **识别结果展示与编辑**：高亮展示原图与识别文本内容，可直接编辑或校正识别文本。
- **复制与导出**：支持一键复制识别结果，可导出为多种文本格式（如 TXT、Word、PDF）。
- **识别历史管理**：保存识别历史，支持筛选和搜索，便于归档和跟踪使用记录。
- **识别参数配置**：自定义 OCR 识别参数、切换不同语种或识别模型。
- **多语言支持**：前端界面和识别引擎均支持多语言选择。
- **状态与进度反馈**：识别处理中有实时进度和状态提示，异常有错误反馈。
- **权限管理与安全**：部分功能仅对登录用户开放，数据和历史私密存储。

## 项目结构

```
hbot-server/
├── frontend/        # 前端 Vue & TypeScript
│   ├── src/
│   └── public/
├── backend/         # 后端 Java OCR 服务
│   ├── src/
│   └── resources/
├── README.md
└── ...
```

## 快速开始

### 环境要求

- Node.js >= 18.x（前端）
- Java >= 8（后端）
- npm 或 yarn

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

### 后端启动

```bash
cd backend
mvn spring-boot:run
```

### 本地访问

- 前端访问：http://localhost:8080
- 后端服务：http://localhost:8000

## 部署流程

1. 构建前端

    ```bash
    cd frontend
    npm run build
    ```

2. 构建后端

    ```bash
    cd backend
    mvn package
    java -jar target/hbot-server.jar
    ```

3. 可将前端打包结果部署至后端静态目录或独立静态服务器。

## 贡献与反馈

欢迎提交 issue 或 PR，一起优化 OCR 服务！  
请参考 [CONTRIBUTING.md] 以保持代码风格一致。

## 许可证

MIT License，详见 [LICENSE](LICENSE)。

## 联系方式

- 项目主页：[https://github.com/leil95334/hbot-server](https://github.com/leil95334/hbot-server)
- 作者：[@leil95334](https://github.com/leil95334)

---

如果觉得 hbot-server 有用，欢迎 Star 或 Fork 支持我们！
