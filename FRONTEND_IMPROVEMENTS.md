# 前端项目改进建议文档

基于对 `frontend` 目录核心代码（Vue 3 + Vite + Element Plus + Axios）的初步分析，本项目整体结构清晰，但在请求封装、状态管理和工程化配置方面存在一些可优化的空间。以下是详细的改进建议：

## 1. HTTP 请求与错误处理 (`src/api/http.js`)

当前使用 Axios 进行了基础封装，但缺乏实际业务中常见的高级特性，降低了开发效率和用户体验。

*   **缺少鉴权 Token 携带机制**：
    *   **现状**：请求拦截器尚未实现。
    *   **建议**：在 Axios 的 `interceptors.request` 中，从本地存储（或 Store）读取 Token，并统一注入到请求头（如 `Authorization: Bearer <token>`）中，避免在每次业务请求时手动传递。
*   **缺乏全局 UI 错误提示**：
    *   **现状**：当 `res.code !== 0` 时仅仅 `Promise.reject`，需要每个 API 调用处手动 `.catch` 并弹窗。
    *   **建议**：在响应拦截器中引入 Element Plus 的 `ElMessage`，当遇到业务错误时统一弹出错误信息，精简组件内的错误处理逻辑。
*   **未处理认证失效 (如 401 状态码)**：
    *   **现状**：没有对特定错误码（如 Token 过期、未登录）做特殊跳转逻辑。
    *   **建议**：在响应拦截器中判断如果返回未授权的业务码或 HTTP 401 状态码，应自动清理本地缓存的 User 信息，并强制重定向到 `/login` 页面。

## 2. 状态管理 (`src/store/user.js`)

*   **自定义响应式状态的局限性**：
    *   **现状**：使用 Vue 的 `ref` 和 `localStorage` 配合导出的纯函数封装了一个简易 Store。
    *   **建议**：随着项目复杂度增加，这种做法不利于状态追踪、模块化拆分和 Vue DevTools 调试。建议引入 Vue 3 官方推荐的状态管理库 **Pinia**。Pinia 提供了更直观的 API、完整的 TypeScript 类型推导以及良好的开发者体验。可以搭配 `pinia-plugin-persistedstate` 轻松实现本地持久化。

## 3. 工程化配置与环境变量

*   **硬编码的开发代理 (`vite.config.js`)**：
    *   **现状**：后端 API 地址 (`http://127.0.0.1:8080`) 被直接写死在 `vite.config.js` 中。
    *   **建议**：引入环境变量机制。在根目录创建 `.env.development` 和 `.env.production`，使用 `process.env.VITE_API_URL` 或 `loadEnv` 动态读取并配置代理目标地址，方便在不同环境（本地、测试、生产）之间切换而无需修改代码。
*   **缺少代码规范工具 (`package.json`)**：
    *   **现状**：项目中尚未配置 ESLint 和 Prettier。
    *   **建议**：在多人协作或长期维护中，代码风格的不一致容易引发冲突和隐藏的 Bug。建议配置 ESLint + Prettier，并在 `.vscode/settings.json` 中配置保存时自动格式化。

## 4. 路由拦截与权限控制

*   **全局路由守卫缺失**：
    *   **建议**：检查 `router/index.js`，对于需要登录才能访问的页面（如发布新闻、个人中心等），应配置全局前置守卫 (`router.beforeEach`)。在路由元信息 (`meta: { requiresAuth: true }`) 中标记权限，并在跳转前校验 Store 中的用户信息，无权限则拦截至登录页。

---

## 🎯 建议的行动计划 (Action Plan)

如果你决定实施这些改进，建议按以下顺序进行：
1.  **Phase 1**: 重构 `src/api/http.js`，加入 `ElMessage` 全局错误拦截和 Token 注入机制。
2.  **Phase 2**: 安装并配置 `Pinia`，将现在的 `user.js` 迁移为符合标准规范的 Pinia Store。
3.  **Phase 3**: 完善 `router/index.js` 中的权限拦截逻辑，确保受保护的页面安全。
4.  **Phase 4**: 将 `vite.config.js` 的硬编码地址提取到 `.env` 文件中，并配置基础的 ESLint。
