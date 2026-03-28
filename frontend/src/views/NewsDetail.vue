<template>
  <div class="news-detail-page">
    <div class="top-back">
      <el-button text type="primary" @click="goBack">← 返回</el-button>
    </div>

    <div class="app-card" v-if="news">
      <div class="news-title">{{ news.title }}</div>
      <div class="news-meta">
        <span v-if="news.authorName" class="meta-item">
          <el-icon><User /></el-icon>
          {{ news.authorName }}
        </span>
        <span class="meta-item">
          <el-icon><View /></el-icon>
          {{ news.viewCount || 0 }}
        </span>
        <span class="meta-item">
          <el-icon><Collection /></el-icon>
          {{ news.collectCount || 0 }}
        </span>
        <span class="meta-item">
          <el-icon><Star /></el-icon>
          {{ news.likeCount || 0 }}
        </span>
        <span v-if="news.publishTime" class="meta-item">{{ news.publishTime }}</span>
      </div>

      <div class="tag-row" v-if="tags.length">
        <el-tag v-for="tag in tags" :key="tag.id" type="info" effect="plain">{{ tag.name }}</el-tag>
      </div>

      <div class="action-row" v-if="currentUser?.id">
        <el-button :type="liked ? 'danger' : 'default'" :plain="!liked" size="large" @click="toggleLike">
          <el-icon><component :is="liked ? StarFilled : Star" /></el-icon>
          <span style="margin-left: 6px;">{{ liked ? '已点赞' : '点赞' }}</span>
        </el-button>
        <el-button :type="collected ? 'warning' : 'default'" :plain="!collected" size="large" @click="toggleCollect">
          <el-icon><component :is="collected ? CollectionTag : Collection" /></el-icon>
          <span style="margin-left: 6px;">{{ collected ? '已收藏' : '收藏' }}</span>
        </el-button>
      </div>

      <div class="news-content" v-html="news.content"></div>

      <el-divider>评论区</el-divider>
      <div class="comment-input-area" v-if="currentUser?.id">
        <el-alert v-if="replyTo" type="info" :closable="false" show-icon style="margin-bottom:8px">
          <template #title>
            正在回复：{{ replyTo.username }}
            <el-button link type="primary" @click="clearReply">取消</el-button>
          </template>
        </el-alert>
        <el-input v-model="commentContent" type="textarea" :rows="3" placeholder="说点什么吧.." />
        <div class="comment-actions">
          <el-button type="primary" @click="submitComment">发表评论</el-button>
        </div>
      </div>
      <el-empty v-else description="登录后可评论" />

      <div class="comment-list">
        <el-empty v-if="rootComments.length === 0" description="暂无评论，快来抢沙发吧~" />
        <div v-else>
          <div class="comment-card" v-for="item in rootComments" :key="item.id">
            <div class="comment-avatar">
              <el-avatar :size="40" :src="item.avatar || undefined">{{ item.username?.charAt(0) || 'U' }}</el-avatar>
            </div>
            <div class="comment-body">
              <div class="comment-header">
                <span class="comment-username">{{ item.username }}</span>
                <span class="comment-time">{{ item.createTime || '' }}</span>
              </div>
              <div class="comment-content">{{ item.content }}</div>
              <div class="comment-op" v-if="currentUser?.id">
                <el-button link type="primary" size="small" @click="setReply(item)">
                  <el-icon><ChatDotRound /></el-icon>
                  回复
                </el-button>
              </div>

              <div class="comment-children" v-if="childrenMap[item.id]?.length">
                <div class="comment-card child" v-for="child in childrenMap[item.id]" :key="child.id">
                  <div class="comment-avatar">
                    <el-avatar :size="32" :src="child.avatar || undefined">{{ child.username?.charAt(0) || 'U' }}</el-avatar>
                  </div>
                  <div class="comment-body">
                    <div class="comment-header">
                      <span class="comment-username">{{ child.username }}</span>
                      <span class="comment-time">{{ child.createTime || '' }}</span>
                    </div>
                    <div class="comment-content">{{ child.content }}</div>
                    <div class="comment-op" v-if="currentUser?.id">
                      <el-button link type="primary" size="small" @click="setReply(child)">
                        <el-icon><ChatDotRound /></el-icon>
                        回复
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <el-empty v-else description="未找到该新闻" />
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Star, StarFilled, Collection, CollectionTag, View, User, ChatDotRound } from '@element-plus/icons-vue';
import { useUserStore } from '../store/user';
import {
  fetchNewsDetail,
  fetchNewsStatus,
  likeNews,
  unlikeNews,
  collectNews,
  uncollectNews,
  fetchNewsComments,
  createNewsComment,
  fetchTagsByNewsId
} from '../api/news';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const news = ref(null);
const comments = ref([]);
const tags = ref([]);
const liked = ref(false);
const collected = ref(false);
const commentContent = ref('');
const replyTo = ref(null);
const currentUser = computed(() => userStore.user);
const newsId = computed(() => route.params.id);

// 根评论（无父级的评论）
const rootComments = computed(() => comments.value.filter(c => !c.parentId));
// 子评论映射表（父ID -> 子评论列表）
const childrenMap = computed(() => {
  const map = {};
  comments.value.forEach(c => {
    if (c.parentId) {
      if (!map[c.parentId]) map[c.parentId] = [];
      map[c.parentId].push(c);
    }
  });
  return map;
});

// 返回上一页，无历史则跳转到新闻列表
const goBack = () => {
  if (window.history.length > 1) {
    router.back();
  } else {
    router.push('/news');
  }
};

// 加载新闻详情、评论、标签、用户操作状态
const loadDetail = async () => {
  try {
    news.value = await fetchNewsDetail(newsId.value, currentUser.value?.id);
    comments.value = await fetchNewsComments(newsId.value);
    tags.value = await fetchTagsByNewsId(newsId.value);
    // 登录状态下加载用户对该新闻的点赞/收藏状态
    if (currentUser.value?.id) {
      const status = await fetchNewsStatus(newsId.value, currentUser.value.id);
      liked.value = !!status.liked;
      collected.value = !!status.collected;
    }
  } catch (error) {
    console.error('加载新闻详情失败：', error);
    news.value = null;
    tags.value = [];
  }
};

// 设置回复目标
const setReply = (item) => {
  replyTo.value = item;
  // 自动聚焦到评论输入框（体验优化）
  setTimeout(() => {
    const textarea = document.querySelector('.comment-input-area el-textarea textarea');
    textarea?.focus();
  }, 0);
};

// 清空回复目标
const clearReply = () => {
  replyTo.value = null;
};

// 切换点赞状态
const toggleLike = async () => {
  if (!currentUser.value?.id) {
    ElMessage.warning('请先登录');
    return;
  }
  try {
    if (liked.value) {
      await unlikeNews(newsId.value, currentUser.value.id);
      liked.value = false;
      news.value.likeCount = Math.max((news.value.likeCount || 0) - 1, 0);
    } else {
      await likeNews(newsId.value, currentUser.value.id);
      liked.value = true;
      news.value.likeCount = (news.value.likeCount || 0) + 1;
    }
  } catch (error) {
    void error;
  }
};

// 切换收藏状态
const toggleCollect = async () => {
  if (!currentUser.value?.id) {
    ElMessage.warning('请先登录');
    return;
  }
  try {
    if (collected.value) {
      await uncollectNews(newsId.value, currentUser.value.id);
      collected.value = false;
      news.value.collectCount = Math.max((news.value.collectCount || 0) - 1, 0);
    } else {
      await collectNews(newsId.value, currentUser.value.id);
      collected.value = true;
      news.value.collectCount = (news.value.collectCount || 0) + 1;
    }
  } catch (error) {
    void error;
  }
};

// 提交评论
const submitComment = async () => {
  if (!currentUser.value?.id) {
    ElMessage.warning('请先登录');
    return;
  }
  const content = commentContent.value.trim();
  if (!content) {
    return ElMessage.warning('请输入评论内容');
  }

  try {
    await createNewsComment(newsId.value, {
      userId: currentUser.value.id,
      content,
      parentId: replyTo.value?.id || null
    });
    // 清空输入框和回复状态
    commentContent.value = '';
    replyTo.value = null;
    // 重新加载评论列表
    comments.value = await fetchNewsComments(newsId.value);
    ElMessage.success('评论成功');
  } catch (error) {
    void error;
  }
};

// 页面挂载时加载数据
onMounted(loadDetail);
</script>

<style scoped>
.news-detail-page {
  max-width: 980px;
  margin: 0 auto;
  padding: 16px;
}
.top-back { margin-bottom: 12px; }
.news-title {
  font-size: 30px;
  font-weight: 700;
  margin-bottom: 10px;
  line-height: 1.35;
  color: var(--app-panel-text-main);
}
.news-meta {
  font-size: 13px;
  color: var(--app-text-sub);
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-bottom: 14px;
}
.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: var(--app-panel-bg-soft);
  border: 1px solid var(--app-panel-border);
  padding: 4px 10px;
  border-radius: 999px;
}
.meta-item .el-icon { font-size: 15px; }
.tag-row {
  display: flex;
  gap: 8px;
  margin-bottom: 14px;
  flex-wrap: wrap;
}
.action-row {
  margin-bottom: 18px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.action-row :deep(.el-button) { min-width: 104px; }
.news-content {
  line-height: 1.95;
  color: var(--app-panel-text-main);
  margin-bottom: 26px;
  font-size: 15px;
}
.news-content :deep(p) { margin: 0 0 14px; }
.news-content :deep(img),
.news-content :deep(video) {
  display: block;
  max-width: 100%;
  height: auto;
  border-radius: 10px;
  margin: 12px 0;
  object-fit: contain;
}

.comment-input-area {
  margin-bottom: 20px;
  padding: 16px;
  background: var(--app-panel-bg-soft);
  border: 1px solid var(--app-panel-border);
  border-radius: 10px;
}
.comment-actions {
  margin-top: 10px;
  text-align: right;
}

.comment-list { margin-top: 16px; }
.comment-card {
  display: flex;
  gap: 12px;
  padding: 14px;
  border: 1px solid var(--app-panel-border);
  border-radius: 10px;
  background: var(--app-panel-bg);
  margin-bottom: 10px;
}
.comment-card:last-child { margin-bottom: 0; }
.comment-card.child {
  padding: 10px;
  margin-top: 8px;
}
.comment-avatar { flex-shrink: 0; }
.comment-body {
  flex: 1;
  min-width: 0;
}
.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.comment-username {
  font-weight: 600;
  color: var(--app-panel-text-main);
  font-size: 14px;
}
.comment-time {
  font-size: 12px;
  color: var(--app-panel-text-faint);
}
.comment-content {
  color: var(--app-panel-text-sub);
  font-size: 14px;
  line-height: 1.7;
  word-wrap: break-word;
  margin-bottom: 6px;
}
.comment-op { margin-top: 4px; }
.comment-children {
  margin-top: 10px;
  padding-left: 10px;
  border-left: 2px solid #dbe7f7;
}

/* 响应式优化 */
@media (max-width: 992px) {
  .news-title { font-size: 24px; }
  .news-content { font-size: 14px; }
  .news-detail-page { padding: 12px; }
  .comment-card { padding: 10px; }
}
</style>
