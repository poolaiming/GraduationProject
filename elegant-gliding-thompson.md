# 前后端项目改进规范计划

## Context

基于 `FRONTEND_IMPROVEMENTS.md` 文档的分析建议，对新闻论坛互动平台的两个前端（`frontend` 用户端、`frontend-admin` 管理端）进行工程化改进。当前项目存在以下问题：

- HTTP 请求层缺少全局错误提示、无请求拦截器
- 状态管理使用简易 `ref + localStorage`，不利于维护和调试
- 路由守卫采用硬编码路径判断，扩展性差
- 开发配置硬编码代理地址，缺少代码规范工具

**重要前提**：后端当前**没有 JWT/Token 认证机制**，登录接口直接返回 User/Admin 对象。因此不会伪造 Token 注入，而是为未来扩展预留接口。

---

## Phase 1：重构 http.js — 统一错误处理

### 目标
两个前端的 `http.js` 增加请求拦截器、全局错误提示、认证失效兜底。

### 已完成的改动

#### 1. `frontend/src/utils/auth.js`（已新建 ✅）
封装 `getUser()`、`clearUser()`、`getLoginPath()` 辅助方法，解耦 http.js 与业务逻辑。

#### 2. `frontend-admin/src/utils/auth.js`（已新建 ✅）
同上，针对 admin 登录态（`STORAGE_KEY = 'admin'`）。

#### 3. `frontend/src/api/http.js`（已重写 ✅）
- 新增请求拦截器，预留 Token 注入扩展点
- 响应拦截器增加 `ElMessage.error` 全局业务错误提示
- 支持 `config.meta.silentError` 静默模式
- HTTP 401 自动清除登录态并跳转 `/login`
- 网络异常统一提示

#### 4. `frontend-admin/src/api/http.js`（已重写 ✅）
与用户端保持同构逻辑。

#### 5. `frontend/src/views/Login.vue`（已修改 ✅）
catch 块移除手动 `ElMessage.error`，改为仅做页面收尾（清空验证码、刷新验证码图片）。

#### 6. `frontend-admin/src/views/AdminLogin.vue`（已修改 ✅）
catch 块移除手动 `ElMessage.error`，由全局拦截器统一处理。

### 待完成的改动

#### 7. 其他页面错误处理去重（待做）
以下页面中存在手动 `ElMessage.error(e.response?.data?.message || e.message || '...')` 的模式，会与全局拦截器重复弹窗，需要逐步清理：
- `frontend/src/views/Forum.vue`
- `frontend/src/views/Profile.vue`
- `frontend/src/views/NewsDetail.vue`
- `frontend/src/views/NewsList.vue`
- `frontend/src/views/Message.vue`
- `frontend/src/views/Survey.vue`
- `frontend/src/views/Announcement.vue`
- `frontend/src/views/Home.vue`
- `frontend-admin/src/views/` 下各管理页面

**处理原则**：
- 移除 catch 中与全局拦截器重复的 `ElMessage.error` 调用
- 保留页面级业务收尾逻辑（如重置表单、刷新列表等）
- **`el-upload` 的成功/失败回调不受影响**（它绕过 axios 拦截器）

### 无需新增依赖

### 验证方式
1. 登录失败 → 只弹一次错误提示
2. 后端关闭 → 弹出网络异常提示
3. 正常业务接口报错 → 统一 ElMessage 提示
4. 上传功能不受影响

---

## Phase 2：引入 Pinia — 统一状态管理（待做）

### 目标
将两个前端的登录态管理从手写 `ref + localStorage` 迁移到 Pinia。

### 新增依赖
```bash
# frontend
cd frontend && npm install pinia

# frontend-admin
cd frontend-admin && npm install pinia
```

### 修改文件

#### 1. `frontend/src/main.js`
```js
import { createPinia } from 'pinia'
app.use(createPinia())  // 在 app.use(router) 之前
```

#### 2. `frontend/src/store/user.js`（重写）
改为 Pinia `defineStore`：
- **state**: `user`（从 localStorage 初始化）
- **getters**: `isLoggedIn`、`userId`、`displayName`
- **actions**: `setUser(user)`、`logout()`
- localStorage key 保持 `'user'` 不变，确保迁移无感

#### 3. `frontend-admin/src/main.js`
同样接入 Pinia

#### 4. `frontend-admin/src/store/admin.js`（重写）
改为 Pinia `defineStore`：
- **state**: `admin`
- **getters**: `isLoggedIn`、`displayName`
- **actions**: `setAdmin(admin)`、`logout()`
- localStorage key 保持 `'admin'`

#### 5. 受影响的页面组件
**用户端**（将 `userStore.value` 改为 `userStore.user` 或 `userStore.isLoggedIn`）：
- `frontend/src/App.vue`
- `frontend/src/router/index.js`
- `frontend/src/views/Login.vue`
- `frontend/src/views/Profile.vue`
- `frontend/src/views/Forum.vue`
- `frontend/src/views/NewsList.vue`
- `frontend/src/views/NewsDetail.vue`
- `frontend/src/views/Home.vue`
- `frontend/src/views/Message.vue`
- `frontend/src/views/Survey.vue`

**管理端**（将 `localStorage.getItem('admin')` 改为 store 调用）：
- `frontend-admin/src/views/AdminLogin.vue`
- `frontend-admin/src/views/AdminLayout.vue`
- `frontend-admin/src/router/index.js`

#### 6. `frontend/src/utils/auth.js` 和 `frontend-admin/src/utils/auth.js`
Phase 1 创建的辅助文件改为从 Pinia store 读取状态

### 验证方式
1. 登录后刷新页面 → 登录态保持
2. 退出登录 → 顶部导航立即更新
3. 个人中心修改资料 → 头部昵称/头像同步更新
4. Vue DevTools 中可查看 Pinia store 状态

---

## Phase 3：重构路由守卫 — meta.requiresAuth（待做）

### 目标
用户端从硬编码路径判断升级为 `meta.requiresAuth`；管理端 guard 改为从 Pinia store 读取。

### 修改文件

#### 1. `frontend/src/router/index.js`
- 给受保护路由添加 `meta: { requiresAuth: true }`：
  - `/message`、`/profile`、`/survey`
- 重写 `beforeEach` 守卫：
  ```js
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  }
  ```
- 已登录用户访问 `/login`、`/register` → 自动重定向 `/home`
- 添加 404 兜底路由 `/:pathMatch(.*)*` → 重定向 `/home`

#### 2. `frontend/src/views/Login.vue`
- 登录成功后优先跳转 `route.query.redirect`，否则跳 `/home`

#### 3. `frontend-admin/src/router/index.js`
- guard 从 `JSON.parse(localStorage.getItem('admin'))` 改为 `adminStore.isLoggedIn`
- 已登录访问 `/login` → 自动重定向 `/dashboard`

#### 4. `frontend-admin/src/views/AdminLogin.vue`
- 登录成功后优先跳转 `route.query.redirect`，否则跳 `/dashboard`

### 验证方式
1. 未登录访问 `/profile` → 跳转 `/login?redirect=/profile`
2. 登录后自动回到 `/profile`
3. 已登录访问 `/login` → 自动跳 `/home`
4. 输入不存在的路由 → 回到首页
5. 管理端未登录访问 `/dashboard` → 跳 `/login`

---

## Phase 4：环境变量 + ESLint（待做）

### 目标
提取硬编码配置到 `.env`，增加基础代码规范工具。

### 新增依赖
```bash
# 两个前端都安装
npm install -D eslint eslint-plugin-vue @eslint/js globals
```

### 修改/新建文件

#### 1. 环境变量文件（两个前端各一套）

**`frontend/.env.development`**（新建）
```
VITE_API_BASE=/api
VITE_PROXY_TARGET=http://127.0.0.1:8080
```

**`frontend/.env.production`**（新建）
```
VITE_API_BASE=/api
```

**`frontend-admin/.env.development`** 和 **`.env.production`**（新建，同上）

#### 2. `frontend/vite.config.js`
```js
import { defineConfig, loadEnv } from 'vite'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())
  return {
    server: {
      proxy: {
        '/api': { target: env.VITE_PROXY_TARGET, changeOrigin: true },
        '/uploads': { target: env.VITE_PROXY_TARGET, changeOrigin: true }
      }
    }
  }
})
```

#### 3. `frontend-admin/vite.config.js`
同上（无 `/uploads` 代理）

#### 4. `frontend/src/api/http.js` 和 `frontend-admin/src/api/http.js`
```js
baseURL: import.meta.env.VITE_API_BASE || '/api'
```

#### 5. ESLint 配置（两个前端各一份）

**`frontend/.eslintrc.cjs`**（新建）
```js
module.exports = {
  root: true,
  env: { browser: true, es2021: true },
  extends: ['eslint:recommended', 'plugin:vue/vue3-recommended'],
  rules: {
    'vue/multi-word-component-names': 'off',
    'no-console': 'warn',
    'no-unused-vars': ['warn', { argsIgnorePattern: '^_' }]
  }
}
```

#### 6. `package.json`（两个前端）
新增 scripts：
```json
"lint": "eslint src --ext .js,.vue",
"lint:fix": "eslint src --ext .js,.vue --fix"
```

### 验证方式
1. `npm run dev` → 代理正常工作
2. 修改 `.env.development` 中端口 → 代理跟随变化
3. `npm run lint` → 能执行，报告合理数量的 warning
4. `npm run build` → 构建成功

---

## 不在本次范围内

| 项目 | 原因 |
|------|------|
| JWT Token 注入 | 后端无 JWT 机制，伪造 Token 无意义 |
| Prettier | 会产生大量格式 diff，毕业设计阶段先不引入 |
| TypeScript 迁移 | 改动量过大，不适合当前阶段 |
| Spring Security | 后端架构变动过大 |

---

## 总进度

| 阶段 | 状态 | 说明 |
|------|------|------|
| Phase 1: http.js 重构 | 🟡 进行中 | 核心文件已完成，页面去重待做 |
| Phase 2: Pinia 迁移 | ⬜ 待做 | |
| Phase 3: 路由守卫重构 | ⬜ 待做 | |
| Phase 4: 环境变量 + ESLint | ⬜ 待做 | |

### Phase 1 已完成文件清单

| 文件 | 操作 | 状态 |
|------|------|------|
| `frontend/src/utils/auth.js` | 新建 | ✅ |
| `frontend-admin/src/utils/auth.js` | 新建 | ✅ |
| `frontend/src/api/http.js` | 重写 | ✅ |
| `frontend-admin/src/api/http.js` | 重写 | ✅ |
| `frontend/src/views/Login.vue` | 去重错误提示 | ✅ |
| `frontend-admin/src/views/AdminLogin.vue` | 去重错误提示 | ✅ |
| 其他页面 catch 块去重 | 批量修改 | ⬜ 待做 |

---

## 关键文件索引

| 文件 | 作用 |
|------|------|
| `frontend/src/api/http.js` | 用户端请求封装核心 |
| `frontend/src/utils/auth.js` | 用户端认证辅助工具 |
| `frontend/src/store/user.js` | 用户端登录态 Store |
| `frontend/src/router/index.js` | 用户端路由与守卫 |
| `frontend/src/main.js` | 用户端入口（接入 Pinia） |
| `frontend/vite.config.js` | 用户端开发配置 |
| `frontend-admin/src/api/http.js` | 管理端请求封装核心 |
| `frontend-admin/src/utils/auth.js` | 管理端认证辅助工具 |
| `frontend-admin/src/store/admin.js` | 管理端登录态 Store |
| `frontend-admin/src/router/index.js` | 管理端路由与守卫 |
| `frontend-admin/src/main.js` | 管理端入口（接入 Pinia） |
| `frontend-admin/vite.config.js` | 管理端开发配置 |
