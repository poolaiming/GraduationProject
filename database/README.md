# 数据库初始化说明

## 文件说明

- **init_database.sql** - 完整的数据库初始化脚本（1534行）
  - 包含所有表结构定义（19张表）
  - 包含基础种子数据（用户、分类、新闻等）
  - 包含补充数据（公告、论坛帖子、评论等）
  - 包含数据修正逻辑

## 快速开始

### 方式一：命令行导入

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS news_platform DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 导入数据
mysql -u root -p news_platform < init_database.sql
```

### 方式二：MySQL客户端

```sql
-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS news_platform DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 选择数据库
USE news_platform;

-- 3. 执行脚本
source /path/to/init_database.sql;
```

## 默认账号

| 类型 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 后台管理员 |
| 用户 | zhangsan | 123456 | 普通用户 |
| 用户 | lisi | 123456 | 普通用户 |
| 用户 | wangwu | 123456 | 普通用户 |

*注：密码使用BCrypt加密存储*

## 数据库配置

修改 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/news_platform?useUnicode=true&characterEncoding=utf8mb4
    username: root
    password: 你的密码
```

## 原始文件（已整合）

以下文件已整合到 `init_database.sql`，可以删除或归档：

- ~~create_tables.sql~~ - 表结构定义
- ~~seed_data.sql~~ - 基础种子数据
- ~~seed_data_extra.sql~~ - 补充数据
- ~~db_migration_20260303.sql~~ - 增量变更
