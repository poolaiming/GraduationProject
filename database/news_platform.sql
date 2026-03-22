/*
 Navicat Premium Dump SQL

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : news_platform

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 06/03/2026 17:09:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_about
-- ----------------------------
DROP TABLE IF EXISTS `t_about`;
CREATE TABLE `t_about`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关于我们内容',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '关于我们表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_about
-- ----------------------------
INSERT INTO `t_about` VALUES (1, '欢迎加入我们团队', '2026-03-03 20:24:32');

-- ----------------------------
-- Table structure for t_admin
-- ----------------------------
DROP TABLE IF EXISTS `t_admin`;
CREATE TABLE `t_admin`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员账号（唯一）',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码（加密存储）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名/昵称',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ADMIN' COMMENT '角色标识',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_admin_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '后台管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_admin
-- ----------------------------
INSERT INTO `t_admin` VALUES (1, 'admin', '$2a$10$CtQvDODp8T8fgYC4vKYCQuqFE2MiZ1qV9VyYXtEj3BT6h4doPlBty', '系统管理员', 'ADMIN', 1, '2026-03-03 14:09:16', '2026-03-03 14:09:16');

-- ----------------------------
-- Table structure for t_announcement
-- ----------------------------
DROP TABLE IF EXISTS `t_announcement`;
CREATE TABLE `t_announcement`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '分类ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
  `view_count` bigint UNSIGNED NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` bigint UNSIGNED NULL DEFAULT 0 COMMENT '点赞数',
  `is_top` tinyint NULL DEFAULT 0 COMMENT '是否置顶：0否 1是',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1发布 0下线',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_announcement_category`(`category_id` ASC) USING BTREE,
  CONSTRAINT `fk_announcement_category` FOREIGN KEY (`category_id`) REFERENCES `t_announcement_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告资讯表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_announcement
-- ----------------------------
INSERT INTO `t_announcement` VALUES (3, NULL, 'test', 'test', 0, 0, 1, 1, '2026-03-04 23:29:54', '2026-03-04 23:29:54', '2026-03-04 23:29:54');
INSERT INTO `t_announcement` VALUES (4, NULL, '1', '1', 0, 0, 0, 1, '2026-03-04 23:30:10', '2026-03-04 23:30:10', '2026-03-04 23:30:10');
INSERT INTO `t_announcement` VALUES (5, NULL, '2', '2', 0, 0, 0, 1, '2026-03-04 23:30:14', '2026-03-04 23:30:13', '2026-03-04 23:30:13');
INSERT INTO `t_announcement` VALUES (6, 1, '平台正式上线公告', '经过团队的持续努力，新闻与论坛互动平台现已正式上线。平台提供新闻浏览、论坛讨论、问卷调查等功能，欢迎广大用户注册体验，并积极反馈意见，帮助我们持续改进。', 1024, 256, 1, 1, '2026-01-04 22:01:34', '2026-03-05 22:01:34', '2026-03-05 22:01:34');
INSERT INTO `t_announcement` VALUES (7, 1, '用户实名认证与记者申请说明', '为保障内容质量，平台支持用户申请记者身份。申请通过后可发布新闻内容，审核周期为 1-3 个工作日。请在个人中心填写真实信息并提交申请，管理员将尽快处理。', 678, 134, 1, 1, '2026-01-19 22:01:34', '2026-03-05 22:01:34', '2026-03-05 22:01:34');
INSERT INTO `t_announcement` VALUES (8, 2, '五一假期内容征集活动开启', '平台即日起开启五一假期内容征集活动，欢迎用户投稿旅行见闻、生活感悟或热点评论。优质内容将在首页推荐展示，活动截止至五一假期结束。', 512, 98, 0, 1, '2026-02-03 22:01:34', '2026-03-05 22:01:34', '2026-03-05 22:01:34');
INSERT INTO `t_announcement` VALUES (9, 3, '系统升级维护通知（已完成）', '平台已于近日完成系统升级，本次升级优化了新闻加载速度、评论区交互体验及后台管理功能。如遇异常请清除浏览器缓存后重试，如仍有问题请通过留言板反馈。', 389, 67, 0, 1, '2026-02-08 22:01:34', '2026-03-05 22:01:34', '2026-03-05 22:01:34');
INSERT INTO `t_announcement` VALUES (10, 1, '关于规范发帖行为的通知', '为维护良好的社区氛围，请用户在发帖和评论时遵守平台规范：不发布违法违规内容，不进行人身攻击，不传播未经核实的谣言。违规内容将被删除，情节严重者将封禁账号。', 445, 89, 0, 1, '2026-02-13 22:01:34', '2026-03-05 22:01:34', '2026-03-05 22:01:34');
INSERT INTO `t_announcement` VALUES (11, 2, '新功能上线：问卷调查模块', '平台新增问卷调查功能，用户可参与平台发布的各类调查问卷，帮助我们了解用户需求与使用体验。欢迎积极参与，您的每一条反馈都将推动平台持续优化。', 567, 112, 0, 1, '2026-02-18 22:01:34', '2026-03-05 22:01:34', '2026-03-05 22:01:34');
INSERT INTO `t_announcement` VALUES (12, 1, '内容审核机制说明', '平台对所有用户发布的新闻内容实行审核制度，审核通过后方可公开展示。审核标准包括：内容真实性、语言规范性及版权合规性。审核结果将通过站内消息通知。', 334, 72, 0, 1, '2026-02-21 22:01:34', '2026-03-05 22:01:34', '2026-03-05 22:01:34');
INSERT INTO `t_announcement` VALUES (13, 2, '论坛话题征集：你最关注的科技趋势', '本期论坛话题征集围绕\"你最关注的科技趋势\"展开，欢迎在论坛发帖分享你的观点。话题涵盖人工智能、新能源、芯片、量子计算等方向，期待与大家深入交流。', 423, 95, 0, 1, '2026-02-25 22:01:34', '2026-03-05 22:01:34', '2026-03-05 22:01:34');
INSERT INTO `t_announcement` VALUES (14, 3, '移动端适配优化完成通知', '平台已完成移动端页面适配优化，在手机浏览器访问时体验更流畅。后续将持续优化加载性能与交互细节，感谢用户的耐心等待与支持。', 298, 54, 0, 1, '2026-02-28 22:01:34', '2026-03-05 22:01:34', '2026-03-05 22:01:34');
INSERT INTO `t_announcement` VALUES (15, 1, '平台数据安全与隐私保护说明', '平台严格遵守相关法律法规，对用户个人信息进行加密存储，不向第三方共享用户数据。如您对数据安全有任何疑问，可通过留言板联系管理员获取详细说明。', 356, 81, 0, 1, '2026-03-03 22:01:34', '2026-03-05 22:01:34', '2026-03-05 22:01:34');

-- ----------------------------
-- Table structure for t_announcement_category
-- ----------------------------
DROP TABLE IF EXISTS `t_announcement_category`;
CREATE TABLE `t_announcement_category`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `sort` int NULL DEFAULT 0 COMMENT '排序值',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告资讯分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_announcement_category
-- ----------------------------
INSERT INTO `t_announcement_category` VALUES (1, '平台公告', 100, 1, '2026-03-05 22:01:34', '2026-03-05 22:01:34');
INSERT INTO `t_announcement_category` VALUES (2, '活动通知', 90, 1, '2026-03-05 22:01:34', '2026-03-05 22:01:34');
INSERT INTO `t_announcement_category` VALUES (3, '系统维护', 80, 1, '2026-03-05 22:01:34', '2026-03-05 22:01:34');

-- ----------------------------
-- Table structure for t_announcement_like
-- ----------------------------
DROP TABLE IF EXISTS `t_announcement_like`;
CREATE TABLE `t_announcement_like`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `announcement_id` bigint UNSIGNED NOT NULL COMMENT '公告ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_announcement_like`(`announcement_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_announcement_like_announcement`(`announcement_id` ASC) USING BTREE,
  INDEX `idx_announcement_like_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_announcement_like_announcement` FOREIGN KEY (`announcement_id`) REFERENCES `t_announcement` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_announcement_like_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_announcement_like
-- ----------------------------

-- ----------------------------
-- Table structure for t_browse_history
-- ----------------------------
DROP TABLE IF EXISTS `t_browse_history`;
CREATE TABLE `t_browse_history`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `news_id` bigint UNSIGNED NOT NULL COMMENT '新闻ID',
  `browse_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_browse_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_browse_news`(`news_id` ASC) USING BTREE,
  CONSTRAINT `fk_browse_news` FOREIGN KEY (`news_id`) REFERENCES `t_news` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_browse_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 93 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '浏览历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_browse_history
-- ----------------------------
INSERT INTO `t_browse_history` VALUES (1, 1, 19, '2026-03-03 12:09:16');
INSERT INTO `t_browse_history` VALUES (3, 3, 19, '2026-03-03 10:09:16');
INSERT INTO `t_browse_history` VALUES (7, 1, 47, '2026-03-03 06:09:16');
INSERT INTO `t_browse_history` VALUES (10, 1, 56, '2026-03-03 03:09:16');
INSERT INTO `t_browse_history` VALUES (12, 3, 44, '2026-03-03 01:09:16');
INSERT INTO `t_browse_history` VALUES (13, 1, 28, '2026-03-03 00:09:16');
INSERT INTO `t_browse_history` VALUES (15, 3, 30, '2026-03-02 22:09:16');
INSERT INTO `t_browse_history` VALUES (16, 1, 31, '2026-03-02 21:09:16');
INSERT INTO `t_browse_history` VALUES (21, 3, 38, '2026-03-02 16:09:16');
INSERT INTO `t_browse_history` VALUES (22, 1, 39, '2026-03-02 15:09:16');
INSERT INTO `t_browse_history` VALUES (27, 3, 46, '2026-03-02 10:09:16');
INSERT INTO `t_browse_history` VALUES (28, 1, 48, '2026-03-02 09:09:16');
INSERT INTO `t_browse_history` VALUES (31, 1, 53, '2026-03-02 06:09:16');
INSERT INTO `t_browse_history` VALUES (33, 3, 55, '2026-03-02 04:09:16');
INSERT INTO `t_browse_history` VALUES (37, 4, 5, '2026-03-03 14:34:27');
INSERT INTO `t_browse_history` VALUES (38, 4, 2, '2026-03-03 14:34:31');
INSERT INTO `t_browse_history` VALUES (39, 4, 5, '2026-03-03 14:34:36');
INSERT INTO `t_browse_history` VALUES (40, 4, 5, '2026-03-03 15:58:55');
INSERT INTO `t_browse_history` VALUES (41, 4, 6, '2026-03-03 16:18:55');
INSERT INTO `t_browse_history` VALUES (42, 4, 5, '2026-03-03 17:30:38');
INSERT INTO `t_browse_history` VALUES (43, 6, 5, '2026-03-03 19:16:39');
INSERT INTO `t_browse_history` VALUES (44, 6, 19, '2026-03-03 20:17:13');
INSERT INTO `t_browse_history` VALUES (45, 6, 5, '2026-03-03 20:17:24');
INSERT INTO `t_browse_history` VALUES (46, 6, 19, '2026-03-03 20:17:32');
INSERT INTO `t_browse_history` VALUES (47, 6, 5, '2026-03-03 20:28:41');
INSERT INTO `t_browse_history` VALUES (48, 6, 5, '2026-03-03 20:29:02');
INSERT INTO `t_browse_history` VALUES (49, 6, 62, '2026-03-04 16:43:00');
INSERT INTO `t_browse_history` VALUES (50, 6, 64, '2026-03-04 16:56:02');
INSERT INTO `t_browse_history` VALUES (51, 6, 64, '2026-03-04 16:56:09');
INSERT INTO `t_browse_history` VALUES (52, 6, 19, '2026-03-04 18:07:57');
INSERT INTO `t_browse_history` VALUES (53, 4, 65, '2026-03-04 18:13:32');
INSERT INTO `t_browse_history` VALUES (54, 4, 65, '2026-03-04 19:09:46');
INSERT INTO `t_browse_history` VALUES (55, 4, 65, '2026-03-04 19:13:51');
INSERT INTO `t_browse_history` VALUES (56, 4, 65, '2026-03-04 19:38:00');
INSERT INTO `t_browse_history` VALUES (57, 4, 65, '2026-03-04 20:27:18');
INSERT INTO `t_browse_history` VALUES (58, 4, 65, '2026-03-04 20:27:28');
INSERT INTO `t_browse_history` VALUES (59, 4, 65, '2026-03-04 21:52:07');
INSERT INTO `t_browse_history` VALUES (60, 4, 65, '2026-03-04 23:28:40');
INSERT INTO `t_browse_history` VALUES (61, 4, 5, '2026-03-04 23:32:18');
INSERT INTO `t_browse_history` VALUES (62, 4, 5, '2026-03-04 23:40:39');
INSERT INTO `t_browse_history` VALUES (63, 4, 5, '2026-03-04 23:40:50');
INSERT INTO `t_browse_history` VALUES (64, 4, 65, '2026-03-04 23:40:59');
INSERT INTO `t_browse_history` VALUES (65, 7, 5, '2026-03-04 23:43:52');
INSERT INTO `t_browse_history` VALUES (66, 6, 5, '2026-03-04 23:47:36');
INSERT INTO `t_browse_history` VALUES (67, 6, 5, '2026-03-04 23:48:06');
INSERT INTO `t_browse_history` VALUES (68, 6, 67, '2026-03-05 13:41:41');
INSERT INTO `t_browse_history` VALUES (69, 6, 67, '2026-03-05 13:41:46');
INSERT INTO `t_browse_history` VALUES (70, 6, 67, '2026-03-05 13:45:41');
INSERT INTO `t_browse_history` VALUES (71, 6, 67, '2026-03-05 13:45:44');
INSERT INTO `t_browse_history` VALUES (72, 6, 67, '2026-03-05 13:45:47');
INSERT INTO `t_browse_history` VALUES (73, 6, 67, '2026-03-05 13:54:14');
INSERT INTO `t_browse_history` VALUES (74, 6, 67, '2026-03-05 13:54:21');
INSERT INTO `t_browse_history` VALUES (75, 6, 67, '2026-03-05 13:54:31');
INSERT INTO `t_browse_history` VALUES (76, 6, 67, '2026-03-05 13:58:12');
INSERT INTO `t_browse_history` VALUES (77, 6, 67, '2026-03-05 13:58:26');
INSERT INTO `t_browse_history` VALUES (78, 6, 67, '2026-03-05 13:58:52');
INSERT INTO `t_browse_history` VALUES (79, 6, 67, '2026-03-05 13:58:58');
INSERT INTO `t_browse_history` VALUES (80, 6, 68, '2026-03-05 14:40:09');
INSERT INTO `t_browse_history` VALUES (81, 6, 68, '2026-03-05 16:55:33');
INSERT INTO `t_browse_history` VALUES (82, 6, 68, '2026-03-05 16:55:38');
INSERT INTO `t_browse_history` VALUES (83, 6, 68, '2026-03-05 16:55:56');
INSERT INTO `t_browse_history` VALUES (84, 6, 68, '2026-03-05 21:19:31');
INSERT INTO `t_browse_history` VALUES (85, 6, 68, '2026-03-05 21:19:35');
INSERT INTO `t_browse_history` VALUES (86, 6, 68, '2026-03-05 21:32:21');
INSERT INTO `t_browse_history` VALUES (87, 6, 63, '2026-03-05 21:32:25');
INSERT INTO `t_browse_history` VALUES (88, 6, 62, '2026-03-05 21:32:26');
INSERT INTO `t_browse_history` VALUES (89, 6, 64, '2026-03-05 21:32:28');
INSERT INTO `t_browse_history` VALUES (90, 6, 68, '2026-03-05 21:32:45');
INSERT INTO `t_browse_history` VALUES (91, 6, 68, '2026-03-05 21:32:47');
INSERT INTO `t_browse_history` VALUES (92, 6, 7, '2026-03-05 21:32:52');

-- ----------------------------
-- Table structure for t_carousel
-- ----------------------------
DROP TABLE IF EXISTS `t_carousel`;
CREATE TABLE `t_carousel`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '轮播标题',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片地址',
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转链接',
  `sort` int NULL DEFAULT 0 COMMENT '排序值',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `start_time` datetime NULL DEFAULT NULL COMMENT '展示开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '展示结束时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '轮播图表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_carousel
-- ----------------------------

-- ----------------------------
-- Table structure for t_forum_comment
-- ----------------------------
DROP TABLE IF EXISTS `t_forum_comment`;
CREATE TABLE `t_forum_comment`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint UNSIGNED NOT NULL COMMENT '帖子ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '评论用户ID',
  `parent_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '父评论ID（回复）',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `like_count` bigint UNSIGNED NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_forum_comment_post`(`post_id` ASC) USING BTREE,
  INDEX `idx_forum_comment_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_forum_comment_parent`(`parent_id` ASC) USING BTREE,
  CONSTRAINT `fk_forum_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `t_forum_comment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_forum_comment_post` FOREIGN KEY (`post_id`) REFERENCES `t_forum_post` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_forum_comment_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '论坛评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_forum_comment
-- ----------------------------
INSERT INTO `t_forum_comment` VALUES (1, 1, 4, NULL, '1', 0, '2026-03-03 14:13:56');
INSERT INTO `t_forum_comment` VALUES (2, 1, 5, NULL, '2', 0, '2026-03-03 14:17:09');
INSERT INTO `t_forum_comment` VALUES (3, 1, 6, NULL, '3', 0, '2026-03-03 20:42:40');
INSERT INTO `t_forum_comment` VALUES (4, 7, 4, NULL, '1', 0, '2026-03-04 21:29:25');
INSERT INTO `t_forum_comment` VALUES (5, 7, 6, NULL, '可以啊', 0, '2026-03-04 21:30:21');
INSERT INTO `t_forum_comment` VALUES (6, 10, 4, NULL, '1', 0, '2026-03-04 21:47:58');

-- ----------------------------
-- Table structure for t_forum_like
-- ----------------------------
DROP TABLE IF EXISTS `t_forum_like`;
CREATE TABLE `t_forum_like`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint UNSIGNED NOT NULL COMMENT '帖子ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_forum_like`(`post_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_forum_like_post`(`post_id` ASC) USING BTREE,
  INDEX `idx_forum_like_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_forum_like_post` FOREIGN KEY (`post_id`) REFERENCES `t_forum_post` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_forum_like_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '论坛点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_forum_like
-- ----------------------------
INSERT INTO `t_forum_like` VALUES (2, 1, 5, '2026-03-03 14:17:14');
INSERT INTO `t_forum_like` VALUES (3, 1, 4, '2026-03-03 14:17:40');
INSERT INTO `t_forum_like` VALUES (5, 7, 4, '2026-03-03 17:30:48');
INSERT INTO `t_forum_like` VALUES (6, 8, 6, '2026-03-03 19:16:30');
INSERT INTO `t_forum_like` VALUES (7, 7, 6, '2026-03-03 19:16:30');
INSERT INTO `t_forum_like` VALUES (8, 10, 6, '2026-03-04 15:56:55');
INSERT INTO `t_forum_like` VALUES (9, 3, 6, '2026-03-04 15:57:07');
INSERT INTO `t_forum_like` VALUES (10, 10, 4, '2026-03-04 21:47:52');

-- ----------------------------
-- Table structure for t_forum_post
-- ----------------------------
DROP TABLE IF EXISTS `t_forum_post`;
CREATE TABLE `t_forum_post`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '发帖用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子内容',
  `view_count` bigint UNSIGNED NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` bigint UNSIGNED NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` bigint UNSIGNED NULL DEFAULT 0 COMMENT '评论数',
  `is_top` tinyint NULL DEFAULT 0 COMMENT '是否置顶：0否 1是',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1待审核 2已发布 3驳回',
  `review_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见/驳回原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_forum_post_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_forum_post_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '论坛帖子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_forum_post
-- ----------------------------
INSERT INTO `t_forum_post` VALUES (1, 1, '大家平时都用什么方式读新闻？', '想了解一下大家获取新闻的渠道，是 APP、网页还是公众号？有什么好用的推荐吗？', 156, 30, 8, 1, 2, '审核通过', '2026-03-03 14:09:16', '2026-03-03 20:42:39');
INSERT INTO `t_forum_post` VALUES (3, 3, '推荐几个靠谱的科技资讯源', '本人常看的有少数派、机器之心、量子位，大家还有补充的吗？', 234, 46, 8, 0, 2, '审核通过', '2026-03-03 14:09:16', '2026-03-04 15:57:07');
INSERT INTO `t_forum_post` VALUES (4, 1, '新人报到，请多关照', '刚注册不久，希望在这里能和大家多交流新闻与观点～', 45, 6, 2, 0, 2, '审核通过', '2026-03-03 14:09:16', '2026-03-03 15:59:20');
INSERT INTO `t_forum_post` VALUES (7, 4, '1', '1', 0, 2, 2, 0, 2, '审核通过', '2026-03-03 16:00:08', '2026-03-04 21:30:20');
INSERT INTO `t_forum_post` VALUES (8, 4, '2', '222', 0, 1, 0, 0, 2, '审核通过', '2026-03-03 16:16:48', '2026-03-03 19:16:29');
INSERT INTO `t_forum_post` VALUES (10, 6, 'test', '1', 0, 2, 1, 0, 2, '审核通过', '2026-03-04 15:56:42', '2026-03-04 21:47:57');
INSERT INTO `t_forum_post` VALUES (11, 4, 'hh', 'test', 0, 0, 0, 0, 3, 'hh', '2026-03-04 19:11:55', '2026-03-04 19:12:06');

-- ----------------------------
-- Table structure for t_friend_link
-- ----------------------------
DROP TABLE IF EXISTS `t_friend_link`;
CREATE TABLE `t_friend_link`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '网站名称',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '网站地址',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Logo 地址',
  `sort` int NULL DEFAULT 0 COMMENT '排序值',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '友情链接表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_friend_link
-- ----------------------------
INSERT INTO `t_friend_link` VALUES (1, 'test', 'http://localhost:3000', NULL, 2, 1, '2026-03-03 14:36:35', '2026-03-03 14:36:35');
INSERT INTO `t_friend_link` VALUES (2, '新闻头条', 'https://www.toutiao.com/', NULL, 3, 1, '2026-03-03 19:04:25', '2026-03-03 19:04:25');

-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '留言用户ID（游客可为空）',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '留言内容',
  `reply_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员回复内容',
  `reply_admin` bigint UNSIGNED NULL DEFAULT NULL COMMENT '回复管理员ID',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `status` tinyint NULL DEFAULT 0 COMMENT '处理状态：0未处理 1已回复 2已关闭',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_message_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_message_admin`(`reply_admin` ASC) USING BTREE,
  CONSTRAINT `fk_message_admin` FOREIGN KEY (`reply_admin`) REFERENCES `t_admin` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_message_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '在线留言表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_message
-- ----------------------------
INSERT INTO `t_message` VALUES (1, 1, '请问如何修改个人头像？在设置里没找到入口。', '已上线', NULL, '2026-03-03 14:39:02', 1, '2026-03-03 14:09:16');
INSERT INTO `t_message` VALUES (2, NULL, '希望增加夜间模式，晚上看新闻比较护眼。', '感谢建议，我们已列入后续版本规划。', 1, '2026-03-02 14:09:16', 1, '2026-03-03 14:09:16');
INSERT INTO `t_message` VALUES (4, NULL, '网站打开速度有时较慢，能否优化？', NULL, NULL, NULL, 0, '2026-03-03 14:09:16');
INSERT INTO `t_message` VALUES (5, 3, '建议新闻详情页增加字体大小调节。', NULL, NULL, NULL, 0, '2026-03-03 14:09:16');
INSERT INTO `t_message` VALUES (6, 1, '收藏的新闻能否按分类筛选？', '该功能已在开发中，预计下月上线。', 1, '2026-03-03 14:09:16', 1, '2026-03-03 14:09:16');
INSERT INTO `t_message` VALUES (7, 4, 'ceshi', '1', NULL, '2026-03-03 14:12:04', 1, '2026-03-03 14:11:52');
INSERT INTO `t_message` VALUES (8, 5, 'ww', NULL, NULL, NULL, 0, '2026-03-03 14:39:43');
INSERT INTO `t_message` VALUES (9, 4, 'test', NULL, NULL, NULL, 0, '2026-03-04 22:57:20');

-- ----------------------------
-- Table structure for t_news
-- ----------------------------
DROP TABLE IF EXISTS `t_news`;
CREATE TABLE `t_news`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '新闻标题',
  `category_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '分类ID',
  `author_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '作者用户ID',
  `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '摘要',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '新闻内容',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图地址',
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '视频地址',
  `view_count` bigint UNSIGNED NULL DEFAULT 0 COMMENT '浏览量',
  `collect_count` bigint UNSIGNED NULL DEFAULT 0 COMMENT '收藏数',
  `like_count` bigint UNSIGNED NULL DEFAULT 0 COMMENT '点赞数',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0草稿 1待审核 2已发布 3驳回',
  `review_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见/驳回原因',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_news_category`(`category_id` ASC) USING BTREE,
  INDEX `idx_news_author`(`author_id` ASC) USING BTREE,
  CONSTRAINT `fk_news_author` FOREIGN KEY (`author_id`) REFERENCES `t_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_news_category` FOREIGN KEY (`category_id`) REFERENCES `t_news_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 69 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '新闻信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_news
-- ----------------------------
INSERT INTO `t_news` VALUES (1, '人工智能在生活中的应用越来越广泛', 1, 1, 'AI 技术正在改变我们的日常生活。', '人工智能已广泛应用于智能家居、语音助手、推荐系统等领域，为人们带来便利。未来将有更多场景与 AI 结合。', NULL, NULL, 128, 25, 42, 2, NULL, '2026-03-01 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (2, '国产新能源汽车销量创新高', 1, 1, '新能源车市持续火热。', '据行业数据，国产新能源汽车销量再创新高，多款车型供不应求。政策与技术进步共同推动绿色出行。', NULL, NULL, 257, 58, 89, 2, NULL, '2026-03-02 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:34:30');
INSERT INTO `t_news` VALUES (5, '互联网大厂发布最新财报', 1, 3, '多家互联网公司公布季度业绩。', '多家互联网巨头发布最新季度财报，营收与用户数据总体稳健。行业竞争格局持续演变。', NULL, NULL, 193, 34, 56, 2, NULL, '2026-03-03 14:09:16', '2026-03-03 14:09:16', '2026-03-04 23:48:05');
INSERT INTO `t_news` VALUES (6, '周末影院：本周值得一看的电影', 2, 3, '本周上映与热映影片推荐。', '本周末有多部新片上映，类型涵盖剧情、喜剧与动画，适合不同年龄层观众选择观看。', NULL, NULL, 68, 8, 23, 2, NULL, '2026-02-28 14:09:16', '2026-03-03 14:09:16', '2026-03-03 16:18:57');
INSERT INTO `t_news` VALUES (7, '多地推进智慧交通项目建设提速', 1, 1, '智慧交通进入规模化落地阶段。', '多地发布智慧交通建设方案，围绕信号协同、车路协同和公共出行优化展开，预计将显著提升通行效率。', NULL, NULL, 144, 19, 36, 2, NULL, '2026-02-27 14:09:16', '2026-03-03 14:09:16', '2026-03-05 21:32:52');
INSERT INTO `t_news` VALUES (9, '高校推出跨学科课程，培养复合能力', 3, 3, '课程体系改革持续推进。', '多所高校推出“AI+X”“工程+管理”等跨学科课程，强调项目实践与真实问题导向，提升学生综合能力。', NULL, NULL, 165, 24, 47, 2, NULL, '2026-02-25 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (11, '城市夜间经济升温，文娱消费活力增强', 2, 1, '夜间消费场景更加丰富。', '夜市、演出、沉浸式体验等夜间消费场景不断扩容，带动餐饮、零售与文旅等行业协同增长。', NULL, NULL, 152, 20, 44, 2, NULL, '2026-02-23 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (13, '云计算服务价格下调，中小企业受益', 1, 3, '数字化转型成本进一步降低。', '多家云厂商宣布核心产品降价，覆盖计算、存储与数据库服务，助力中小企业降低数字化门槛。', NULL, NULL, 220, 39, 81, 2, NULL, '2026-02-21 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (15, '中小学劳动课程实践基地建设提速', 3, 1, '劳动教育更加注重实践。', '各地学校联合社区和企业建设劳动教育实践基地，课程覆盖农艺、工艺和公益服务等内容。', NULL, NULL, 141, 16, 38, 2, NULL, '2026-02-19 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (17, '春季档电影票房稳步增长', 2, 3, '多类型影片带动观影热情。', '春季档影片类型丰富，家庭向和现实题材表现亮眼，影院上座率与二刷率均有提升。', NULL, NULL, 118, 14, 35, 2, NULL, '2026-02-17 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (19, '大模型应用走向垂直行业深耕', 1, 1, '从通用能力转向业务价值。', '企业开始将大模型能力嵌入客服、营销、研发与运营流程，关注准确率、稳定性和成本控制。', NULL, NULL, 292, 53, 104, 2, NULL, '2026-02-15 14:09:16', '2026-03-03 14:09:16', '2026-03-04 18:07:56');
INSERT INTO `t_news` VALUES (20, '音乐节经济带动周边文旅消费', 2, 1, '演出市场释放综合效应。', '热门音乐节吸引跨城观众，带动住宿、交通和本地文旅消费，城市品牌传播效应明显。', NULL, NULL, 130, 18, 40, 2, NULL, '2026-02-14 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (21, '高校心理健康教育课程覆盖面扩大', 3, 3, '学生心理支持体系更完善。', '多所高校将心理健康课程纳入通识教育体系，并强化咨询服务与危机干预机制。', NULL, NULL, 174, 22, 46, 2, NULL, '2026-02-13 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:53');
INSERT INTO `t_news` VALUES (24, '基础教育数字教材应用场景增加', 3, 1, '课堂互动方式更加多元。', '数字教材在课堂互动、分层作业与学习反馈中应用更广，教师教学效率与学生参与度同步提升。', NULL, NULL, 148, 21, 43, 2, NULL, '2026-02-10 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (25, '智能穿戴设备新品密集发布', 1, 3, '健康监测功能持续升级。', '新一代智能手表和手环在睡眠、心率和运动分析等健康场景中功能增强，市场关注度提升。', NULL, NULL, 182, 27, 58, 2, NULL, '2026-02-09 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (26, '热门话剧巡演开启，文化消费持续升温', 2, 3, '线下演出市场回暖明显。', '多部口碑话剧开启全国巡演，票务平台数据显示一线与新一线城市购票热度持续走高。', NULL, NULL, 109, 13, 29, 2, NULL, '2026-02-08 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (28, '低空经济试点城市公布新一批应用场景', 1, 1, '低空应用从展示走向常态化运营。', '多地围绕物流配送、应急巡检和文旅观光发布低空经济应用清单，推动产业链协同发展。', NULL, NULL, 194, 26, 51, 2, NULL, '2026-02-06 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (30, '多地博物馆推出夜场活动激发消费', 2, 3, '夜游文化场景持续升温。', '夜场导览、沉浸式演出和主题文创活动带动博物馆客流增长，文旅融合效应进一步显现。', NULL, NULL, 122, 16, 34, 2, NULL, '2026-02-04 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (31, '高校毕业设计展线上线下联动开展', 3, 1, '毕业成果展示形式更丰富。', '多所高校通过展馆与云展厅联动展示毕业作品，增强社会参与度并拓展学生就业对接渠道。', NULL, NULL, 151, 20, 39, 2, NULL, '2026-02-03 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (33, '多部现实题材剧集引发观众共鸣', 2, 1, '现实题材内容热度走高。', '聚焦职场、家庭与社会议题的剧集在口碑与播放量上表现突出，长尾讨论热度持续。', NULL, NULL, 140, 18, 41, 2, NULL, '2026-02-01 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (35, 'AI 辅助编程工具在开发团队中渗透率提升', 1, 3, '研发效率工具加速普及。', '企业在代码补全、测试生成与文档整理等环节引入 AI 工具，并强化代码审查与安全治理。', NULL, NULL, 268, 43, 92, 2, NULL, '2026-01-30 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (37, '多地中小学推进科学实验课程常态化', 3, 3, '科学教育实践占比提升。', '学校通过实验室开放与项目式学习强化科学素养培养，学生动手能力和探索兴趣明显提高。', NULL, NULL, 162, 21, 42, 2, NULL, '2026-01-28 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (38, '车企发布新一代智能座舱系统', 1, 1, '车载交互体验持续升级。', '新系统在语音理解、多屏协同和导航推荐方面进行优化，重点提升行车安全与交互流畅性。', NULL, NULL, 207, 30, 67, 2, NULL, '2026-01-27 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (39, '演唱会带动城市周末文旅订单增长', 2, 3, '演出经济效应持续释放。', '票务、酒店与餐饮订单在演出周期内显著增长，城市在文旅传播和消费活力方面实现双提升。', NULL, NULL, 133, 17, 36, 2, NULL, '2026-01-26 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (40, '高校开放实验平台支持学生科研训练', 3, 1, '本科生科研参与度持续提高。', '多所高校建设共享实验平台并完善导师机制，为学生参与科研项目提供更稳定资源支持。', NULL, NULL, 149, 19, 40, 2, NULL, '2026-01-25 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (44, '机器人产业园启动新一轮企业入驻计划', 1, 3, '产业集聚效应持续增强。', '多个机器人产业园发布扶持政策，覆盖研发补贴、测试场地与产线协同，吸引上下游企业集中布局。', NULL, NULL, 223, 38, 80, 2, NULL, '2026-01-21 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (45, '舞台剧数字票务与会员体系全面升级', 2, 1, '观演服务流程更顺畅。', '多家票务平台上线智能选座、场馆导览与会员权益联动功能，提升用户观演全流程体验。', NULL, NULL, 111, 12, 26, 2, NULL, '2026-01-20 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (46, '职业院校新增人工智能应用技术方向', 3, 3, '专业设置更加贴近产业需求。', '多所职业院校围绕 AI 应用开发、数据标注与模型运维设置课程，强化实践与就业对接。', NULL, NULL, 186, 27, 56, 2, NULL, '2026-01-19 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (47, '芯片封测企业加大先进封装产线投入', 1, 1, '半导体后道工艺竞争加剧。', '企业围绕先进封装技术扩建产线并提升良率，助力高性能计算与通信产品落地。', NULL, NULL, 245, 40, 88, 2, NULL, '2026-01-18 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (48, '纪录片导演分享会带动线下观影热潮', 2, 3, '内容交流活动提升观众黏性。', '多城举办纪录片主创分享会，观众在映后讨论中对社会议题与创作过程展开深度交流。', NULL, NULL, 104, 10, 24, 2, NULL, '2026-01-17 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (49, '中学综合实践课程引入真实项目挑战', 3, 1, '课堂与社会需求连接更紧密。', '学校联合社区与企业发布实践课题，学生通过团队协作完成调研、设计与成果汇报。', NULL, NULL, 158, 22, 44, 2, NULL, '2026-01-16 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (53, '工业互联网平台接入设备数量持续增长', 1, 3, '产业数字化覆盖面扩大。', '平台在设备接入、工艺优化与能耗分析方面能力提升，制造企业数字化改造进度加快。', NULL, NULL, 219, 34, 73, 2, NULL, '2026-01-12 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (54, '国风舞台节目热播带动传统文化关注', 2, 1, '传统文化传播方式创新。', '融合戏曲、舞蹈与数字舞美的节目受到欢迎，带动年轻群体对传统艺术的关注与参与。', NULL, NULL, 127, 16, 35, 2, NULL, '2026-01-11 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (55, '多地推进高校就业指导课程体系改革', 3, 3, '就业服务从“求职季”延伸到全过程。', '高校通过生涯规划、实训项目和企业导师机制，帮助学生更早建立职业认知与能力储备。', NULL, NULL, 174, 25, 50, 2, NULL, '2026-01-10 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (56, 'AI 语音交互在车载与家居场景融合加快', 1, 1, '跨终端语音体验趋于一致。', '厂商推动语音引擎在多终端打通，用户在车内与家庭设备之间可实现连续语义交互。', NULL, NULL, 201, 29, 64, 2, NULL, '2026-01-09 14:09:16', '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news` VALUES (58, 'test', 3, 4, '1', '1', NULL, NULL, 0, 0, 0, 3, '', NULL, '2026-03-03 14:40:17', '2026-03-04 22:59:34');
INSERT INTO `t_news` VALUES (62, '1', NULL, 6, '1', '1', NULL, NULL, 2, 0, 0, 2, '所属分类已被删除', '2026-03-05 21:32:37', '2026-03-03 20:58:29', '2026-03-05 21:32:36');
INSERT INTO `t_news` VALUES (63, '2', NULL, 6, '2', '2', NULL, NULL, 1, 0, 0, 2, '所属分类已被删除', '2026-03-05 21:32:36', '2026-03-04 15:54:39', '2026-03-05 21:32:35');
INSERT INTO `t_news` VALUES (64, '2', 3, 6, '2', '2', NULL, NULL, 3, 0, 0, 2, NULL, '2026-03-04 16:55:33', '2026-03-04 16:55:25', '2026-03-05 21:32:27');
INSERT INTO `t_news` VALUES (65, 'lll', NULL, 4, 'lll', 'lll', NULL, NULL, 9, 0, 1, 3, '所属分类已被删除', '2026-03-04 18:13:21', '2026-03-04 18:13:15', '2026-03-05 21:31:23');
INSERT INTO `t_news` VALUES (66, 'h\'h', NULL, 4, 'h\'h', 'h\'h', NULL, NULL, 0, 0, 0, 3, '所属分类已被删除', NULL, '2026-03-04 19:09:18', '2026-03-05 21:31:23');
INSERT INTO `t_news` VALUES (67, 'ok', 1, 6, 'ok', 'ok1', NULL, NULL, 12, 0, 0, 2, 'ok', '2026-03-05 13:58:45', '2026-03-04 22:58:01', '2026-03-05 13:58:57');
INSERT INTO `t_news` VALUES (68, '测试1', NULL, 6, 'test', '\n<img src=\"/uploads/fcb3a43fe1414ea7804951ac37088a91.png\" alt=\"image\" style=\"max-width:100%;border-radius:8px;\" />\n很好', NULL, NULL, 9, 0, 0, 2, '所属分类已被删除', '2026-03-05 21:32:35', '2026-03-05 14:39:54', '2026-03-05 21:32:47');

-- ----------------------------
-- Table structure for t_news_category
-- ----------------------------
DROP TABLE IF EXISTS `t_news_category`;
CREATE TABLE `t_news_category`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `sort` int NULL DEFAULT 0 COMMENT '排序值，越大越靠前',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '新闻分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_news_category
-- ----------------------------
INSERT INTO `t_news_category` VALUES (1, '科技', '科技与互联网资讯', 100, 1, '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news_category` VALUES (2, '娱乐', '娱乐与生活', 90, 1, '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_news_category` VALUES (3, '教育', '教育与学习', 80, 1, '2026-03-03 14:09:16', '2026-03-03 21:16:35');
INSERT INTO `t_news_category` VALUES (5, '金融', '', 10, 1, '2026-03-05 21:31:58', '2026-03-05 21:31:58');

-- ----------------------------
-- Table structure for t_news_collect
-- ----------------------------
DROP TABLE IF EXISTS `t_news_collect`;
CREATE TABLE `t_news_collect`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `news_id` bigint UNSIGNED NOT NULL COMMENT '新闻ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_news_collect`(`news_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_news_collect_news`(`news_id` ASC) USING BTREE,
  INDEX `idx_news_collect_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_news_collect_news` FOREIGN KEY (`news_id`) REFERENCES `t_news` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_news_collect_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '新闻收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_news_collect
-- ----------------------------
INSERT INTO `t_news_collect` VALUES (1, 19, 1, '2026-03-03 14:09:16');
INSERT INTO `t_news_collect` VALUES (3, 28, 3, '2026-03-03 14:09:16');
INSERT INTO `t_news_collect` VALUES (6, 35, 3, '2026-03-03 14:09:16');
INSERT INTO `t_news_collect` VALUES (10, 47, 1, '2026-03-03 14:09:16');
INSERT INTO `t_news_collect` VALUES (13, 53, 1, '2026-03-03 14:09:16');
INSERT INTO `t_news_collect` VALUES (14, 53, 3, '2026-03-03 14:09:16');
INSERT INTO `t_news_collect` VALUES (15, 56, 1, '2026-03-03 14:09:16');
INSERT INTO `t_news_collect` VALUES (18, 5, 4, '2026-03-03 14:34:48');
INSERT INTO `t_news_collect` VALUES (19, 19, 6, '2026-03-03 20:17:35');

-- ----------------------------
-- Table structure for t_news_comment
-- ----------------------------
DROP TABLE IF EXISTS `t_news_comment`;
CREATE TABLE `t_news_comment`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `news_id` bigint UNSIGNED NOT NULL COMMENT '新闻ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '评论用户ID',
  `parent_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '父评论ID（回复）',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `like_count` bigint UNSIGNED NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_news_comment_news`(`news_id` ASC) USING BTREE,
  INDEX `idx_news_comment_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_news_comment_parent`(`parent_id` ASC) USING BTREE,
  CONSTRAINT `fk_news_comment_news` FOREIGN KEY (`news_id`) REFERENCES `t_news` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_news_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `t_news_comment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_news_comment_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '新闻评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_news_comment
-- ----------------------------
INSERT INTO `t_news_comment` VALUES (4, 35, 1, NULL, 'AI 辅助编程确实提高了效率，但代码审查更重要。', 10, '2026-03-03 14:09:16');
INSERT INTO `t_news_comment` VALUES (11, 5, 4, NULL, '好', 0, '2026-03-03 14:34:44');
INSERT INTO `t_news_comment` VALUES (12, 65, 4, NULL, '很好', 0, '2026-03-04 21:52:12');

-- ----------------------------
-- Table structure for t_news_like
-- ----------------------------
DROP TABLE IF EXISTS `t_news_like`;
CREATE TABLE `t_news_like`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `news_id` bigint UNSIGNED NOT NULL COMMENT '新闻ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_news_like`(`news_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_news_like_news`(`news_id` ASC) USING BTREE,
  INDEX `idx_news_like_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_news_like_news` FOREIGN KEY (`news_id`) REFERENCES `t_news` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_news_like_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '新闻点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_news_like
-- ----------------------------
INSERT INTO `t_news_like` VALUES (1, 19, 1, '2026-03-03 14:09:16');
INSERT INTO `t_news_like` VALUES (3, 19, 3, '2026-03-03 14:09:16');
INSERT INTO `t_news_like` VALUES (6, 35, 1, '2026-03-03 14:09:16');
INSERT INTO `t_news_like` VALUES (8, 35, 3, '2026-03-03 14:09:16');
INSERT INTO `t_news_like` VALUES (11, 47, 1, '2026-03-03 14:09:16');
INSERT INTO `t_news_like` VALUES (18, 56, 1, '2026-03-03 14:09:16');
INSERT INTO `t_news_like` VALUES (19, 56, 3, '2026-03-03 14:09:16');
INSERT INTO `t_news_like` VALUES (22, 5, 4, '2026-03-03 14:34:47');
INSERT INTO `t_news_like` VALUES (23, 6, 4, '2026-03-03 16:18:57');
INSERT INTO `t_news_like` VALUES (24, 19, 6, '2026-03-03 20:17:33');
INSERT INTO `t_news_like` VALUES (26, 65, 4, '2026-03-04 21:52:13');

-- ----------------------------
-- Table structure for t_news_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_news_tag`;
CREATE TABLE `t_news_tag`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `news_id` bigint UNSIGNED NOT NULL COMMENT '新闻ID',
  `tag_id` bigint UNSIGNED NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_news_tag`(`news_id` ASC, `tag_id` ASC) USING BTREE,
  INDEX `idx_news_tag_news`(`news_id` ASC) USING BTREE,
  INDEX `idx_news_tag_tag`(`tag_id` ASC) USING BTREE,
  CONSTRAINT `fk_news_tag_news` FOREIGN KEY (`news_id`) REFERENCES `t_news` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_news_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `t_tag` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 138 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '新闻-标签 关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_news_tag
-- ----------------------------
INSERT INTO `t_news_tag` VALUES (1, 1, 1);
INSERT INTO `t_news_tag` VALUES (2, 1, 2);
INSERT INTO `t_news_tag` VALUES (3, 2, 1);
INSERT INTO `t_news_tag` VALUES (4, 2, 3);
INSERT INTO `t_news_tag` VALUES (7, 5, 1);
INSERT INTO `t_news_tag` VALUES (8, 5, 3);
INSERT INTO `t_news_tag` VALUES (9, 6, 2);
INSERT INTO `t_news_tag` VALUES (10, 7, 1);
INSERT INTO `t_news_tag` VALUES (11, 7, 3);
INSERT INTO `t_news_tag` VALUES (14, 9, 2);
INSERT INTO `t_news_tag` VALUES (15, 9, 3);
INSERT INTO `t_news_tag` VALUES (18, 11, 2);
INSERT INTO `t_news_tag` VALUES (20, 13, 1);
INSERT INTO `t_news_tag` VALUES (21, 13, 2);
INSERT INTO `t_news_tag` VALUES (24, 15, 3);
INSERT INTO `t_news_tag` VALUES (27, 17, 2);
INSERT INTO `t_news_tag` VALUES (30, 19, 1);
INSERT INTO `t_news_tag` VALUES (31, 19, 2);
INSERT INTO `t_news_tag` VALUES (32, 20, 2);
INSERT INTO `t_news_tag` VALUES (33, 20, 3);
INSERT INTO `t_news_tag` VALUES (35, 21, 2);
INSERT INTO `t_news_tag` VALUES (34, 21, 3);
INSERT INTO `t_news_tag` VALUES (39, 24, 3);
INSERT INTO `t_news_tag` VALUES (40, 25, 1);
INSERT INTO `t_news_tag` VALUES (41, 25, 2);
INSERT INTO `t_news_tag` VALUES (42, 26, 2);
INSERT INTO `t_news_tag` VALUES (43, 26, 3);
INSERT INTO `t_news_tag` VALUES (46, 28, 1);
INSERT INTO `t_news_tag` VALUES (47, 28, 3);
INSERT INTO `t_news_tag` VALUES (50, 30, 2);
INSERT INTO `t_news_tag` VALUES (51, 30, 3);
INSERT INTO `t_news_tag` VALUES (52, 31, 2);
INSERT INTO `t_news_tag` VALUES (53, 31, 3);
INSERT INTO `t_news_tag` VALUES (56, 33, 2);
INSERT INTO `t_news_tag` VALUES (57, 33, 4);
INSERT INTO `t_news_tag` VALUES (60, 35, 1);
INSERT INTO `t_news_tag` VALUES (61, 35, 2);
INSERT INTO `t_news_tag` VALUES (65, 37, 2);
INSERT INTO `t_news_tag` VALUES (64, 37, 3);
INSERT INTO `t_news_tag` VALUES (66, 38, 1);
INSERT INTO `t_news_tag` VALUES (67, 38, 4);
INSERT INTO `t_news_tag` VALUES (68, 39, 2);
INSERT INTO `t_news_tag` VALUES (69, 39, 3);
INSERT INTO `t_news_tag` VALUES (71, 40, 2);
INSERT INTO `t_news_tag` VALUES (70, 40, 3);
INSERT INTO `t_news_tag` VALUES (78, 44, 1);
INSERT INTO `t_news_tag` VALUES (79, 44, 3);
INSERT INTO `t_news_tag` VALUES (80, 45, 2);
INSERT INTO `t_news_tag` VALUES (81, 45, 4);
INSERT INTO `t_news_tag` VALUES (83, 46, 1);
INSERT INTO `t_news_tag` VALUES (82, 46, 3);
INSERT INTO `t_news_tag` VALUES (84, 47, 1);
INSERT INTO `t_news_tag` VALUES (85, 47, 4);
INSERT INTO `t_news_tag` VALUES (86, 48, 2);
INSERT INTO `t_news_tag` VALUES (87, 48, 3);
INSERT INTO `t_news_tag` VALUES (89, 49, 2);
INSERT INTO `t_news_tag` VALUES (88, 49, 3);
INSERT INTO `t_news_tag` VALUES (96, 53, 1);
INSERT INTO `t_news_tag` VALUES (97, 53, 3);
INSERT INTO `t_news_tag` VALUES (98, 54, 2);
INSERT INTO `t_news_tag` VALUES (99, 54, 3);
INSERT INTO `t_news_tag` VALUES (101, 55, 2);
INSERT INTO `t_news_tag` VALUES (100, 55, 3);
INSERT INTO `t_news_tag` VALUES (102, 56, 1);
INSERT INTO `t_news_tag` VALUES (103, 56, 2);
INSERT INTO `t_news_tag` VALUES (120, 58, 1);
INSERT INTO `t_news_tag` VALUES (121, 58, 2);
INSERT INTO `t_news_tag` VALUES (122, 58, 3);
INSERT INTO `t_news_tag` VALUES (137, 62, 3);
INSERT INTO `t_news_tag` VALUES (136, 63, 3);
INSERT INTO `t_news_tag` VALUES (114, 64, 1);
INSERT INTO `t_news_tag` VALUES (115, 65, 3);
INSERT INTO `t_news_tag` VALUES (116, 66, 4);
INSERT INTO `t_news_tag` VALUES (123, 67, 4);
INSERT INTO `t_news_tag` VALUES (133, 68, 1);
INSERT INTO `t_news_tag` VALUES (134, 68, 2);
INSERT INTO `t_news_tag` VALUES (135, 68, 3);

-- ----------------------------
-- Table structure for t_survey
-- ----------------------------
DROP TABLE IF EXISTS `t_survey`;
CREATE TABLE `t_survey`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问卷标题',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '问卷说明',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0草稿 1发布中 2已结束',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `create_admin` bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建管理员ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_survey_admin`(`create_admin` ASC) USING BTREE,
  CONSTRAINT `fk_survey_admin` FOREIGN KEY (`create_admin`) REFERENCES `t_admin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问卷调查表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_survey
-- ----------------------------
INSERT INTO `t_survey` VALUES (1, 'test', '1', 2, '2026-03-03 00:00:00', '2026-03-05 00:00:00', NULL, '2026-03-03 14:36:57', '2026-03-05 21:34:14');
INSERT INTO `t_survey` VALUES (2, '2', '2', 2, '2026-03-03 00:00:00', '2026-03-04 00:00:00', NULL, '2026-03-03 21:01:02', '2026-03-04 15:53:04');
INSERT INTO `t_survey` VALUES (3, '3.5', '3.5', 1, '2026-03-05 00:00:00', '2026-03-07 00:00:00', NULL, '2026-03-05 21:33:59', '2026-03-05 21:34:55');

-- ----------------------------
-- Table structure for t_survey_answer
-- ----------------------------
DROP TABLE IF EXISTS `t_survey_answer`;
CREATE TABLE `t_survey_answer`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `survey_id` bigint UNSIGNED NOT NULL COMMENT '问卷ID',
  `user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '答卷用户ID（匿名时可为空）',
  `submit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_survey_answer_survey`(`survey_id` ASC) USING BTREE,
  INDEX `idx_survey_answer_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_survey_answer_survey` FOREIGN KEY (`survey_id`) REFERENCES `t_survey` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_survey_answer_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问卷答卷表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_survey_answer
-- ----------------------------
INSERT INTO `t_survey_answer` VALUES (1, 1, 4, '2026-03-03 14:37:47');
INSERT INTO `t_survey_answer` VALUES (2, 1, 5, '2026-03-03 14:38:41');
INSERT INTO `t_survey_answer` VALUES (3, 2, 6, '2026-03-03 21:02:05');
INSERT INTO `t_survey_answer` VALUES (4, 1, 6, '2026-03-04 15:54:19');

-- ----------------------------
-- Table structure for t_survey_answer_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_survey_answer_detail`;
CREATE TABLE `t_survey_answer_detail`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `answer_id` bigint UNSIGNED NOT NULL COMMENT '答卷ID',
  `question_id` bigint UNSIGNED NOT NULL COMMENT '题目ID',
  `option_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '选中的选项ID（多选题一题多行）',
  `answer_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '填空/判断等文本答案',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_answer_detail_answer`(`answer_id` ASC) USING BTREE,
  INDEX `idx_answer_detail_question`(`question_id` ASC) USING BTREE,
  INDEX `fk_answer_detail_option`(`option_id` ASC) USING BTREE,
  CONSTRAINT `fk_answer_detail_answer` FOREIGN KEY (`answer_id`) REFERENCES `t_survey_answer` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_answer_detail_option` FOREIGN KEY (`option_id`) REFERENCES `t_survey_option` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_answer_detail_question` FOREIGN KEY (`question_id`) REFERENCES `t_survey_question` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问卷答题明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_survey_answer_detail
-- ----------------------------
INSERT INTO `t_survey_answer_detail` VALUES (1, 1, 1, 1, NULL);
INSERT INTO `t_survey_answer_detail` VALUES (2, 2, 1, 2, NULL);
INSERT INTO `t_survey_answer_detail` VALUES (3, 3, 2, 3, NULL);
INSERT INTO `t_survey_answer_detail` VALUES (4, 3, 2, 4, NULL);
INSERT INTO `t_survey_answer_detail` VALUES (5, 4, 1, 1, NULL);

-- ----------------------------
-- Table structure for t_survey_option
-- ----------------------------
DROP TABLE IF EXISTS `t_survey_option`;
CREATE TABLE `t_survey_option`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `question_id` bigint UNSIGNED NOT NULL COMMENT '题目ID',
  `option_label` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '选项标识（如 A/B/C）',
  `option_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选项内容',
  `order_no` int NULL DEFAULT 0 COMMENT '排序号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_survey_option_question`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_survey_option_question` FOREIGN KEY (`question_id`) REFERENCES `t_survey_question` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问卷选项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_survey_option
-- ----------------------------
INSERT INTO `t_survey_option` VALUES (1, 1, 'a', '1', 0);
INSERT INTO `t_survey_option` VALUES (2, 1, 'b', '1', 1);
INSERT INTO `t_survey_option` VALUES (3, 2, 'a', '2', 0);
INSERT INTO `t_survey_option` VALUES (4, 2, 'b', '2', 1);
INSERT INTO `t_survey_option` VALUES (5, 2, 'c', '2', 2);
INSERT INTO `t_survey_option` VALUES (6, 3, 'a', '3.5', 0);
INSERT INTO `t_survey_option` VALUES (7, 3, 'b', '3.5', 1);

-- ----------------------------
-- Table structure for t_survey_question
-- ----------------------------
DROP TABLE IF EXISTS `t_survey_question`;
CREATE TABLE `t_survey_question`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `survey_id` bigint UNSIGNED NOT NULL COMMENT '问卷ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目标题',
  `question_type` tinyint NOT NULL COMMENT '题目类型：1单选 2多选 3判断',
  `required` tinyint NULL DEFAULT 1 COMMENT '是否必答：1是 0否',
  `score` int NULL DEFAULT 0 COMMENT '分值（可选）',
  `order_no` int NULL DEFAULT 0 COMMENT '排序号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_survey_question_survey`(`survey_id` ASC) USING BTREE,
  CONSTRAINT `fk_survey_question_survey` FOREIGN KEY (`survey_id`) REFERENCES `t_survey` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问卷题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_survey_question
-- ----------------------------
INSERT INTO `t_survey_question` VALUES (1, 1, '1', 1, 1, 0, 0);
INSERT INTO `t_survey_question` VALUES (2, 2, '2', 2, 1, 0, 0);
INSERT INTO `t_survey_question` VALUES (3, 3, '3.5', 3, 1, 0, 0);

-- ----------------------------
-- Table structure for t_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tag_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_tag
-- ----------------------------
INSERT INTO `t_tag` VALUES (1, '热点', '2026-03-03 14:09:16');
INSERT INTO `t_tag` VALUES (2, '推荐', '2026-03-03 14:09:16');
INSERT INTO `t_tag` VALUES (3, '国内', '2026-03-03 14:09:16');
INSERT INTO `t_tag` VALUES (4, '国际', '2026-03-03 14:09:16');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名（唯一）',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码（加密存储）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像地址',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别：0未知 1男 2女',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1正常 0禁用 2待注销',
  `is_journalist` tinyint NULL DEFAULT 0 COMMENT '新闻工作者状态：0未申请 1已通过 2待审核 3已拒绝',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '前台用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, 'zhangsan', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '张三', NULL, NULL, 'zhangsan@example.com', 0, 1, 0, '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_user` VALUES (3, 'wangwu', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '王五', NULL, NULL, 'wangwu@example.com', 0, 1, 0, '2026-03-03 14:09:16', '2026-03-03 14:09:16');
INSERT INTO `t_user` VALUES (4, 'lll', '$2a$10$GJYz7tXdiIbgDPGdQIQV3.UjfgA5ThHJ62AC6LRz5..L8h0awrXRy', 'hh', '/uploads/9941bc01fda04e3ea6a500252433363e.png', NULL, NULL, 0, 1, 1, '2026-03-03 14:11:11', '2026-03-03 19:02:13');
INSERT INTO `t_user` VALUES (5, 'lisi', '$2a$10$9/vSe6vKYJ5iyKUMVWWhuut63wPh.YYBYcbV0p2eI/v5hZy1uF/w.', 'lisi', NULL, NULL, NULL, 0, 1, 0, '2026-03-03 14:16:23', '2026-03-03 14:16:23');
INSERT INTO `t_user` VALUES (6, 'test', '$2a$10$1DgKrzBZXI5lsjXTivvJoeIUL5zIQNk6rlQJPwZqtTQ9YxDqxhnKS', 'test', '/uploads/66552391ae9141179d381b057473e526.png', NULL, NULL, 0, 1, 1, '2026-03-03 19:06:15', '2026-03-03 20:26:28');
INSERT INTO `t_user` VALUES (7, 'opus', '$2a$10$xRHcFgFoqJxyeIPL8.c./eMl9eL9Rq8RdRp3RywZhaKQCn0vNk/12', 'opus', NULL, NULL, NULL, 0, 1, 0, '2026-03-04 23:20:30', '2026-03-04 23:20:30');

SET FOREIGN_KEY_CHECKS = 1;
