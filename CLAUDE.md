# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a news and forum interactive platform graduation project with three main components:
- **backend**: Spring Boot REST API (port 8080)
- **frontend**: Vue 3 user-facing application (port 3000)
- **frontend-admin**: Vue 3 admin dashboard (port 3001)

## Development Commands

### Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run          # Start backend server
mvn clean package            # Build JAR
mvn test                     # Run tests
```

Backend runs on `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Frontend (User Portal)
```bash
cd frontend
npm install                  # Install dependencies
npm run dev                  # Start dev server (port 3000)
npm run build                # Build for production
```

### Frontend Admin (Admin Dashboard)
```bash
cd frontend-admin
npm install                  # Install dependencies
npm run dev                  # Start dev server (port 3001)
npm run build                # Build for production
```

## Architecture & Key Patterns

### Backend Response Structure
All API endpoints return a unified response format:
```java
ApiResponse<T> {
  code: 0,           // 0 = success, -1 or other = error
  message: "...",
  data: T
}
```

Success responses use `code: 0`, not `code: 200`.

### Frontend HTTP Interceptor Pattern
Both frontends use an axios interceptor ([frontend/src/api/http.js](frontend/src/api/http.js)) that:
1. Checks if `response.data.code === 0`
2. If success, returns `response.data.data` directly
3. If error, rejects with the error message

**Critical**: When calling backend APIs through the http.js instance, the response is already unwrapped to `data`. Do NOT check `res.code` or access `res.data` again.

**Exception**: `el-upload` component bypasses axios interceptors and receives raw responses. In upload success handlers, check `response.code === 0` and access `response.data`.

### Backend Layer Structure
```
controller/          # REST endpoints
  ├─ admin/         # Admin endpoints (/api/admin/*)
  └─ *Controller    # User endpoints (/api/*)
service/            # Business logic
  └─ impl/
mapper/             # MyBatis-Plus data access
entity/             # Database entities (camelCase mapping enabled)
common/             # ApiResponse, shared utilities
config/             # Spring configuration
exception/          # GlobalExceptionHandler
```

### Frontend Structure (both frontend & frontend-admin)
```
src/
  ├─ api/          # API calls organized by module (news.js, user.js, etc.)
  ├─ router/       # Vue Router with navigation guards
  ├─ store/        # Lightweight state (user.js for auth)
  ├─ views/        # Page components
  ├─ styles/       # Global theme styles
  └─ App.vue       # Root component with header/footer
```

### State Management
User authentication state is managed via a simple reactive store ([frontend/src/store/user.js](frontend/src/store/user.js)):
- `userStore.value` - current user object (null if logged out)
- `setUser(user)` - update user and persist to localStorage
- `logout()` - clear user state

Both frontends use the same pattern.

### File Upload
- Backend endpoint: `/api/upload/image` (POST multipart/form-data)
- Files saved to `uploads/` directory (configurable in application.yml)
- Returns: `ApiResponse<String>` with path like `/uploads/filename.ext`
- Frontend proxy: `/uploads` proxied to backend in vite.config.js
- Max size: 5MB for images (configurable in application.yml: 10MB)

### Database Configuration
MySQL connection configured in [backend/src/main/resources/application.yml](backend/src/main/resources/application.yml):
- Database: `news_platform`
- Default credentials: root/123456
- MyBatis-Plus auto-maps snake_case columns to camelCase fields

### Redis
Used for verification codes and caching. Configure connection in application.yml.

## Important Conventions

### API Routing
- User endpoints: `/api/*` (e.g., `/api/news`, `/api/forum`)
- Admin endpoints: `/api/admin/*` (e.g., `/api/admin/users`, `/api/admin/news`)
- File uploads: `/api/upload/*`
- System info: `/api/system/*` (about, friend links, carousels)

### Frontend Router Guards
Both frontends have route guards that redirect to `/login` if accessing protected routes without authentication. Check `userStore.value` to determine if user is logged in.

### Component Style
- Use Vue 3 Composition API with `<script setup>`
- Prefer `scoped` styles in components
- Element Plus is the UI framework for both frontends
- Admin dashboard uses ECharts for data visualization

### Error Handling
- Backend: GlobalExceptionHandler catches exceptions and returns ApiResponse with error code/message
- Frontend: Axios interceptor rejects promises on error, display with ElMessage

## Technology Stack

**Backend:**
- Java 21
- Spring Boot 3.3.3
- MyBatis-Plus 3.5.10.1 (with pagination)
- MySQL 8.x
- Redis 6.x/7.x
- springdoc-openapi (Swagger)

**Frontend:**
- Vue 3 (Composition API)
- Vite 5
- Vue Router 4
- Element Plus 2.8
- Axios 1.7
- ECharts 5.5 (admin only)

## Backend Patterns

### MyBatis-Plus Query Building
Use `LambdaQueryWrapper` for type-safe queries:
```java
new LambdaQueryWrapper<News>()
  .eq(News::getCategoryId, categoryId)    // WHERE category_id = ?
  .like(News::getTitle, keyword)          // AND title LIKE %keyword%
  .in(News::getId, ids)                   // AND id IN (?, ...)
  .orderByDesc(News::getPublishTime);     // ORDER BY publish_time DESC
```

### Pagination Pattern
MyBatis-Plus returns `Page<T>` with this structure:
```java
Page<News> page = service.page(Page.of(pageNum, pageSize), wrapper);
// Returns: { records: [...], total: N, pages: M, size: 10, current: 1 }
```

Frontend receives `records` array and `total` count for pagination components.

### Request Parameter Conventions
- **GET/DELETE**: Query parameters via `@RequestParam`
- **POST/PUT**: JSON body via `@RequestBody`
- **Mixed**: Path variables + query params (e.g., `POST /api/news/{id}/like?userId=123`)

### Service Layer Pattern
Services extend `ServiceImpl<Mapper, Entity>` from MyBatis-Plus:
- Inherit CRUD methods: `save()`, `getById()`, `page()`, `list()`
- Add custom business logic in service interface/implementation
- Mark transactional operations with `@Transactional(rollbackFor = Exception.class)`

## Database Schema

**Key tables** (19 total in `create_tables.sql`):
- `t_user` - Users with journalist status (0=none, 1=approved, 2=pending, 3=rejected)
- `t_news` - News articles with status workflow (0=draft, 1=pending, 2=published, 3=rejected)
- `t_news_category` - News categories
- `t_tag` + `t_news_tag` - Tags with many-to-many relationship
- `t_forum_post` + `t_forum_comment` - Forum with nested comments
- `t_news_like`, `t_news_collect`, `t_browse_history` - User interactions
- `t_announcement`, `t_message` - System content
- `t_carousel`, `t_friend_link`, `t_about` - Homepage content

**Setup**: Run `create_tables.sql` then `seed_data.sql` on database `news_platform`.

## Common Pitfalls

1. **Response handling**: Don't check `res.code` or access `res.data` when using the http.js axios instance - the interceptor already unwraps it
2. **Upload handlers**: el-upload bypasses interceptors, so DO check `response.code === 0` in success handlers
3. **Success code**: Backend uses `code: 0` for success, not `code: 200`
4. **File paths**: Use forward slashes in paths even on Windows (bash shell syntax)
5. **Static resources**: Uploaded files must be proxied in vite.config.js during development
