# 新闻与论坛互动平台 API 接口文档

**基础信息:**
- 后端地址: `http://localhost:8080`
- 统一响应格式: `ApiResponse<T> { code: 0, message: "...", data: T }`
- 成功响应: `code: 0`
- 错误响应: `code: -1` 或其他非0值

---

## 一、用户端接口 (`/api/*`)

### 1. 认证模块 (`/api/auth`)

#### 1.1 获取验证码
- **接口**: `GET /api/auth/captcha`
- **响应**: `{ captchaKey: string, captchaUrl: string }`

#### 1.2 用户注册
- **接口**: `POST /api/auth/register`
- **请求体**:
```json
{
  "username": "string",
  "password": "string",
  "nickname": "string",
  "captchaKey": "string",
  "captchaCode": "string"
}
```

#### 1.3 用户登录
- **接口**: `POST /api/auth/login`
- **请求体**:
```json
{
  "username": "string",
  "password": "string",
  "captchaKey": "string",
  "captchaCode": "string"
}
```

---

### 2. 用户模块 (`/api/user`)

#### 2.1 更新个人资料
- **接口**: `PUT /api/user/profile`
- **请求体**: `{ userId: number, nickname: string, avatar: string }`

#### 2.2 修改密码
- **接口**: `PUT /api/user/password`
- **请求体**: `{ userId: number, oldPassword: string, newPassword: string }`

#### 2.3 申请注销账号
- **接口**: `PUT /api/user/cancel-request`
- **请求体**: `{ userId: number }`

#### 2.4 申请成为新闻工作者
- **接口**: `POST /api/user/journalist/register`
- **请求体**: `{ userId: number }`
- **说明**: 状态变为2(待审核)

---

### 3. 新闻模块 (`/api/news`)

#### 3.1 分页查询新闻
- **接口**: `GET /api/news/page`
- **参数**:
  - `categoryId` (可选): 分类ID
  - `authorId` (可选): 作者ID
  - `keyword` (可选): 关键词
  - `authorName` (可选): 作者名
  - `tagId` (可选): 标签ID
  - `sortBy` (可选): 排序方式
  - `pageNum` (默认1): 页码
  - `pageSize` (默认10): 每页数量

#### 3.2 获取新闻详情
- **接口**: `GET /api/news/{id}`
- **参数**: `userId` (可选,用于记录浏览历史)

#### 3.3 获取推荐新闻
- **接口**: `GET /api/news/recommend`
- **参数**: `userId`, `size` (默认6)

#### 3.4 创建新闻
- **接口**: `POST /api/news`
- **请求体**:
```json
{
  "title": "string",
  "categoryId": "number",
  "authorId": "number",
  "summary": "string",
  "content": "string",
  "coverImage": "string",
  "videoUrl": "string",
  "status": "number",
  "tagIds": ["number"]
}
```

#### 3.5 更新新闻
- **接口**: `PUT /api/news/{id}`
- **请求体**:
```json
{
  "operatorId": "number",
  "title": "string",
  "categoryId": "number",
  "summary": "string",
  "content": "string",
  "coverImage": "string",
  "videoUrl": "string",
  "tagIds": ["number"]
}
```

#### 3.6 删除新闻
- **接口**: `DELETE /api/news/{id}`

#### 3.7 点赞新闻
- **接口**: `POST /api/news/{id}/like?userId={userId}`

#### 3.8 取消点赞
- **接口**: `DELETE /api/news/{id}/like?userId={userId}`

#### 3.9 收藏新闻
- **接口**: `POST /api/news/{id}/collect?userId={userId}`

#### 3.10 取消收藏
- **接口**: `DELETE /api/news/{id}/collect?userId={userId}`

#### 3.11 获取新闻互动状态
- **接口**: `GET /api/news/{id}/status?userId={userId}`
- **响应**: `{ liked: boolean, collected: boolean }`

#### 3.12 发表评论
- **接口**: `POST /api/news/{id}/comment`
- **请求体**: `{ userId: number, parentId: number, content: string }`

#### 3.13 获取评论列表
- **接口**: `GET /api/news/{id}/comments`

#### 3.14 获取分类列表
- **接口**: `GET /api/news/categories`

#### 3.15 获取标签列表
- **接口**: `GET /api/news/tags`

#### 3.16 获取新闻的标签
- **接口**: `GET /api/news/{id}/tags`

#### 3.17 我的点赞
- **接口**: `GET /api/news/my-likes?userId={userId}&size={size}`

#### 3.18 我的收藏
- **接口**: `GET /api/news/my-collects?userId={userId}&size={size}`

#### 3.19 浏览历史
- **接口**: `GET /api/news/history?userId={userId}&size={size}`

---

### 4. 论坛模块 (`/api/forum`)

#### 4.1 创建帖子
- **接口**: `POST /api/forum`
- **请求体**: `{ userId: number, title: string, content: string }`

#### 4.2 分页查询帖子
- **接口**: `GET /api/forum/page`
- **参数**: `keyword`, `userId`, `status`, `startTime`, `endTime`, `pageNum`, `pageSize`

#### 4.3 我的帖子
- **接口**: `GET /api/forum/my?userId={userId}&pageNum={pageNum}&pageSize={pageSize}`

#### 4.4 我点赞的帖子
- **接口**: `GET /api/forum/my-likes?userId={userId}&size={size}`

#### 4.5 更新帖子
- **接口**: `PUT /api/forum/{id}`
- **请求体**: `{ userId: number, title: string, content: string }`

#### 4.6 删除帖子
- **接口**: `DELETE /api/forum/{id}?userId={userId}`

#### 4.7 点赞帖子
- **接口**: `POST /api/forum/{id}/like?userId={userId}`

#### 4.8 取消点赞
- **接口**: `DELETE /api/forum/{id}/like?userId={userId}`

#### 4.9 获取帖子状态
- **接口**: `GET /api/forum/{id}/status?userId={userId}`
- **响应**: `{ liked: boolean }`

#### 4.10 发表评论
- **接口**: `POST /api/forum/{id}/comment`
- **请求体**: `{ userId: number, parentId: number, content: string }`

#### 4.11 获取评论列表
- **接口**: `GET /api/forum/{id}/comments`

---

### 5. 公告模块 (`/api/announcement`)

#### 5.1 获取公告列表
- **接口**: `GET /api/announcement/list`

#### 5.2 获取公告详情
- **接口**: `GET /api/announcement/{id}`

#### 5.3 点赞公告
- **接口**: `POST /api/announcement/{id}/like?userId={userId}`

#### 5.4 取消点赞
- **接口**: `DELETE /api/announcement/{id}/like?userId={userId}`

#### 5.5 获取公告状态
- **接口**: `GET /api/announcement/{id}/status?userId={userId}`
- **响应**: `{ liked: boolean }`

---

### 6. 留言模块 (`/api/message`)

#### 6.1 创建留言
- **接口**: `POST /api/message`
- **请求体**: `{ userId: number, content: string }`

#### 6.2 我的留言
- **接口**: `GET /api/message/my?userId={userId}&pageNum={pageNum}&pageSize={pageSize}`

#### 6.3 修改留言
- **接口**: `PUT /api/message/{id}`
- **请求体**: `{ userId: number, content: string }`
- **说明**: 仅未处理状态(status=0)可修改

---

### 7. 问卷模块 (`/api/survey`)

#### 7.1 分页查询问卷
- **接口**: `GET /api/survey/page`
- **参数**: `keyword`, `pageNum`, `pageSize`

#### 7.2 获取问卷详情
- **接口**: `GET /api/survey/{id}/detail`

#### 7.3 检查是否已提交
- **接口**: `GET /api/survey/{id}/check?userId={userId}`

#### 7.4 提交问卷答案
- **接口**: `POST /api/survey/{id}/answer`
- **请求体**:
```json
{
  "userId": "number",
  "answers": [
    {
      "questionId": "number",
      "optionIds": ["number"],
      "answerText": "string"
    }
  ]
}
```

#### 7.5 获取问卷统计
- **接口**: `GET /api/survey/{id}/stats`

---

### 8. 系统模块 (`/api/system`)

#### 8.1 获取轮播图列表
- **接口**: `GET /api/system/carousel/list`

#### 8.2 获取友情链接列表
- **接口**: `GET /api/system/friend-link/list`

#### 8.3 获取关于我们
- **接口**: `GET /api/system/about`

---

### 9. 文件上传模块 (`/api/upload`)

#### 9.1 上传图片
- **接口**: `POST /api/upload/image`
- **请求**: `multipart/form-data`, 字段名 `file`
- **限制**: 5MB, 仅图片格式
- **响应**: `"/uploads/filename.ext"`

#### 9.2 上传视频
- **接口**: `POST /api/upload/video`
- **请求**: `multipart/form-data`, 字段名 `file`
- **限制**: 50MB, 仅 mp4/webm/ogg 格式
- **响应**: `"/uploads/filename.ext"`

---

## 二、管理端接口 (`/api/admin/*`)

### 1. 管理员认证 (`/api/admin/auth`)

#### 1.1 管理员登录
- **接口**: `POST /api/admin/auth/login`
- **请求体**: `{ username: string, password: string }`

---

### 2. 用户管理 (`/api/admin/user`)

#### 2.1 分页查询用户
- **接口**: `GET /api/admin/user/page`
- **参数**: `id`, `username`, `keyword`, `pageNum`, `pageSize`

#### 2.2 更新用户状态
- **接口**: `PUT /api/admin/user/{id}/status?status={status}`

#### 2.3 审核新闻工作者
- **接口**: `PUT /api/admin/user/{id}/journalist-status?journalistStatus={journalistStatus}`
- **说明**: 0=非新闻工作者, 1=已通过, 2=待审核, 3=已拒绝

#### 2.4 删除用户
- **接口**: `DELETE /api/admin/user/{id}`
- **说明**: 删除用户及其所有业务数据

---

### 3. 新闻管理 (`/api/admin/news`)

#### 3.1 分页查询新闻
- **接口**: `GET /api/admin/news/page`
- **参数**: `categoryId`, `keyword`, `status`, `tagId`, `pageNum`, `pageSize`

#### 3.2 获取新闻详情
- **接口**: `GET /api/admin/news/{id}`

#### 3.3 创建新闻
- **接口**: `POST /api/admin/news`
- **请求体**:
```json
{
  "title": "string",
  "categoryId": "number",
  "authorId": "number",
  "summary": "string",
  "content": "string",
  "coverImage": "string",
  "videoUrl": "string",
  "status": "number",
  "reviewRemark": "string",
  "tagIds": ["number"]
}
```

#### 3.4 批量导入新闻
- **接口**: `POST /api/admin/news/import`
- **请求体**: `[CreateNewsRequest]` 数组

#### 3.5 更新新闻
- **接口**: `PUT /api/admin/news/{id}`
- **请求体**: 同创建新闻

#### 3.6 更新新闻状态(审核)
- **接口**: `PUT /api/admin/news/{id}/status?status={status}&reviewRemark={reviewRemark}`
- **说明**: status=2 发布, status=3 拒绝(需填写reviewRemark)

#### 3.7 删除新闻
- **接口**: `DELETE /api/admin/news/{id}`

---

### 4. 新闻分类管理 (`/api/admin/news-category`)

#### 4.1 获取分类列表
- **接口**: `GET /api/admin/news-category/list`

#### 4.2 创建分类
- **接口**: `POST /api/admin/news-category`
- **请求体**: `NewsCategory`

#### 4.3 更新分类
- **接口**: `PUT /api/admin/news-category/{id}`

#### 4.4 删除分类
- **接口**: `DELETE /api/admin/news-category/{id}`
- **说明**: 删除分类时会拒绝该分类下的所有新闻

---

### 5. 论坛管理 (`/api/admin/forum`)

#### 5.1 分页查询帖子
- **接口**: `GET /api/admin/forum/page`
- **参数**: `keyword`, `pageNum`, `pageSize`

#### 5.2 设置置顶
- **接口**: `PUT /api/admin/forum/{id}/top?isTop={isTop}`

#### 5.3 更新帖子状态(审核)
- **接口**: `PUT /api/admin/forum/{id}/status?status={status}&reviewRemark={reviewRemark}`

#### 5.4 删除帖子
- **接口**: `DELETE /api/admin/forum/{id}`

#### 5.5 获取帖子评论(分页)
- **接口**: `GET /api/admin/forum/{id}/comments?pageNum={pageNum}&pageSize={pageSize}`

#### 5.6 删除评论
- **接口**: `DELETE /api/admin/forum/comment/{id}`

---

### 6. 公告管理 (`/api/admin/announcement`)

#### 6.1 分页查询公告
- **接口**: `GET /api/admin/announcement/page`
- **参数**: `keyword`, `pageNum`, `pageSize`

#### 6.2 创建公告
- **接口**: `POST /api/admin/announcement`
- **请求体**: `Announcement`

#### 6.3 更新公告
- **接口**: `PUT /api/admin/announcement/{id}`

#### 6.4 设置置顶
- **接口**: `PUT /api/admin/announcement/{id}/top?isTop={isTop}`

#### 6.5 删除公告
- **接口**: `DELETE /api/admin/announcement/{id}`

---

### 7. 留言管理 (`/api/admin/message`)

#### 7.1 分页查询留言
- **接口**: `GET /api/admin/message/page?pageNum={pageNum}&pageSize={pageSize}`

#### 7.2 回复留言
- **接口**: `PUT /api/admin/message/{id}/reply`
- **请求体**: `{ replyContent: string }`

---

### 8. 问卷管理 (`/api/admin/survey`)

#### 8.1 分页查询问卷
- **接口**: `GET /api/admin/survey/page?pageNum={pageNum}&pageSize={pageSize}`

#### 8.2 创建问卷
- **接口**: `POST /api/admin/survey`
- **请求体**: `Survey`

#### 8.3 更新问卷
- **接口**: `PUT /api/admin/survey/{id}`

#### 8.4 删除问卷
- **接口**: `DELETE /api/admin/survey/{id}`

#### 8.5 获取问卷题目
- **接口**: `GET /api/admin/survey/{id}/questions`

#### 8.6 创建题目
- **接口**: `POST /api/admin/survey/{id}/questions`
- **请求体**:
```json
{
  "title": "string",
  "questionType": "number",
  "required": "number",
  "score": "number",
  "orderNo": "number",
  "options": [
    {
      "optionLabel": "string",
      "optionContent": "string",
      "orderNo": "number"
    }
  ]
}
```

#### 8.7 更新题目
- **接口**: `PUT /api/admin/survey/question/{questionId}`

#### 8.8 删除题目
- **接口**: `DELETE /api/admin/survey/question/{questionId}`

#### 8.9 创建选项
- **接口**: `POST /api/admin/survey/question/{questionId}/options`

#### 8.10 更新选项
- **接口**: `PUT /api/admin/survey/option/{optionId}`

#### 8.11 删除选项
- **接口**: `DELETE /api/admin/survey/option/{optionId}`

#### 8.12 获取问卷统计
- **接口**: `GET /api/admin/survey/{id}/stats`

---

### 9. 统计分析 (`/api/admin/statistics`)

#### 9.1 获取仪表盘数据
- **接口**: `GET /api/admin/statistics/dashboard`

#### 9.2 获取互动趋势
- **接口**: `GET /api/admin/statistics/interaction-trend?days={days}`

---

### 10. 系统配置 (`/api/admin/system`)

#### 10.1 获取关于我们
- **接口**: `GET /api/admin/system/about`

#### 10.2 保存关于我们
- **接口**: `PUT /api/admin/system/about`
- **请求体**: `About`

#### 10.3 轮播图列表
- **接口**: `GET /api/admin/system/carousel/list`

#### 10.4 创建轮播图
- **接口**: `POST /api/admin/system/carousel`

#### 10.5 更新轮播图
- **接口**: `PUT /api/admin/system/carousel/{id}`

#### 10.6 删除轮播图
- **接口**: `DELETE /api/admin/system/carousel/{id}`

#### 10.7 友情链接列表
- **接口**: `GET /api/admin/system/friend-link/list`

#### 10.8 创建友情链接
- **接口**: `POST /api/admin/system/friend-link`

#### 10.9 更新友情链接
- **接口**: `PUT /api/admin/system/friend-link/{id}`

#### 10.10 删除友情链接
- **接口**: `DELETE /api/admin/system/friend-link/{id}`

#### 10.11 公告分类列表
- **接口**: `GET /api/admin/system/announcement-category/list`

#### 10.12 创建公告分类
- **接口**: `POST /api/admin/system/announcement-category`

#### 10.13 更新公告分类
- **接口**: `PUT /api/admin/system/announcement-category/{id}`

#### 10.14 删除公告分类
- **接口**: `DELETE /api/admin/system/announcement-category/{id}`

---

## 三、数据字典

### 新闻状态 (status)
| 值 | 含义 |
|----|------|
| 0  | 草稿 |
| 1  | 待审核 |
| 2  | 已发布 |
| 3  | 已拒绝 |

### 论坛帖子状态 (status)
| 值 | 含义 |
|----|------|
| 1  | 待审核 |
| 2  | 已发布 |
| 3  | 已拒绝 |

### 用户状态 (status)
| 值 | 含义 |
|----|------|
| 0  | 禁用 |
| 1  | 正常 |

### 新闻工作者状态 (isJournalist)
| 值 | 含义 |
|----|------|
| 0  | 非新闻工作者 |
| 1  | 已通过 |
| 2  | 待审核 |
| 3  | 已拒绝 |

### 留言状态 (status)
| 值 | 含义 |
|----|------|
| 0  | 未处理 |
| 1  | 已回复 |

### 问卷状态 (status)
| 值 | 含义 |
|----|------|
| 0  | 草稿 |
| 1  | 进行中 |
| 2  | 已结束 |

### 题目类型 (questionType)
| 值 | 含义 |
|----|------|
| 1  | 单选 |
| 2  | 多选 |
| 3  | 填空 |
