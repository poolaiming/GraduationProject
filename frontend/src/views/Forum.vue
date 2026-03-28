<template>
  <div class="forum-page">
    <div class="app-card" style="margin-bottom: 16px;" v-if="isLoggedIn">
      <div class="head-row">
        <div>
          <div class="app-page-title">论坛社区</div>
          <div class="app-page-subtitle">发布、编辑、互动讨论，并浏览全部帖子广场</div>
        </div>
        <el-button type="primary" @click="openCreateDialog">发布帖子</el-button>
      </div>
    </div>

    <div class="app-card">
      <div class="toolbar">
        <el-input
            v-model="allKeyword"
            clearable
            placeholder="按标题搜索"
            style="width: 220px"
            @keyup.enter="onSearchAll"
        />
        <el-date-picker
            v-model="allTimeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
        />
        <el-button type="primary" @click="onSearchAll">搜索</el-button>
        <el-button @click="onResetAll">重置</el-button>
      </div>

      <el-skeleton v-if="loadingAll" :rows="8" animated style="margin-bottom: 10px" />
      <el-table v-else :data="allTableData" size="small">
        <el-table-column prop="title" label="标题" min-width="300" show-overflow-tooltip />
        <el-table-column prop="authorName" label="作者" width="160" show-overflow-tooltip />
        <el-table-column prop="likeCount" label="点赞" width="90">
          <template #default="{ row }">
            <span class="stat-badge">
              <el-icon><Star /></el-icon>
              {{ row.likeCount || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="commentCount" label="评论" width="90">
          <template #default="{ row }">
            <span class="stat-badge">
              <el-icon><ChatDotRound /></el-icon>
              {{ row.commentCount || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="互动" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="info" plain size="small" @click="openDetail(row)">查看内容</el-button>
            <el-button v-if="isLoggedIn" :type="row.__liked ? 'danger' : 'default'" :plain="!row.__liked" size="small" @click="onToggleLike(row)">
              <el-icon><component :is="row.__liked ? StarFilled : Star" /></el-icon>
              <span style="margin-left: 4px;">{{ row.__liked ? '已点赞' : '点赞' }}</span>
            </el-button>
            <el-button type="primary" plain size="small" @click="openComments(row)">
              <el-icon><ChatDotRound /></el-icon>
              <span style="margin-left: 4px;">评论</span>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="pager" background layout="prev, pager, next" :current-page="allPageNum" :page-size="pageSize" :total="allTotal" @current-change="onAllPageChange" />
    </div>

    <el-dialog v-model="createVisible" title="发布论坛帖子" width="700px">
      <el-form :model="form" label-width="70px">
        <el-form-item label="标题">
          <el-input v-model="form.title" maxlength="100" show-word-limit placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="5" maxlength="2000" show-word-limit placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmitPost">发布帖子</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="帖子详情" width="760px">
      <div v-if="detailPost" class="post-detail">
        <div class="post-detail-title">{{ detailPost.title }}</div>
        <div class="post-detail-meta">
          <span>作者：{{ detailPost.authorName || '未知' }}</span>
          <span>发布时间：{{ detailPost.createTime || '--' }}</span>
        </div>
        <div class="post-detail-content">{{ detailPost.content || '暂无内容' }}</div>
      </div>
    </el-dialog>

    <el-dialog v-model="commentVisible" title="帖子评论" width="760px">
      <div class="comment-input-area" v-if="isLoggedIn">
        <el-input v-model="commentContent" type="textarea" :rows="3" placeholder="请输入评论内容..." />
        <div class="comment-actions">
          <el-button type="primary" @click="onSubmitComment">发表评论</el-button>
        </div>
      </div>
      <div class="comment-list">
        <el-empty v-if="commentList.length === 0" description="暂无评论，快来抢沙发吧" />
        <div v-else class="comment-item" v-for="item in commentList" :key="item.id">
          <div class="comment-avatar">
            <el-avatar :size="40" :src="item.avatar || undefined">{{ item.username?.charAt(0) || 'U' }}</el-avatar>
          </div>
          <div class="comment-body">
            <div class="comment-header">
              <span class="comment-username">{{ item.username }}</span>
              <span class="comment-time">{{ item.createTime }}</span>
            </div>
            <div class="comment-content">{{ item.content }}</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { ChatDotRound, Star, StarFilled } from '@element-plus/icons-vue';
import { useUserStore } from '../store/user';
import {
  createForumPost,
  fetchForumPage,
  likeForumPost,
  unlikeForumPost,
  fetchForumStatus,
  fetchForumComments,
  createForumComment
} from '../api/forum';

const userStore = useUserStore();
// 发布帖子表单
const form = reactive({ title: '', content: '' });
// 发布弹窗显隐
const createVisible = ref(false);
// 发布请求加载状态
const submitting = ref(false);
// 分页大小
const pageSize = ref(10);

// 搜索关键词
const allKeyword = ref('');
// 时间范围
const allTimeRange = ref([]);
// 列表加载状态
const loadingAll = ref(false);
// 列表数据
const allTableData = ref([]);
// 当前页码
const allPageNum = ref(1);
// 总条数
const allTotal = ref(0);

// 详情弹窗显隐
const detailVisible = ref(false);
// 详情数据
const detailPost = ref(null);

// 评论弹窗显隐
const commentVisible = ref(false);
// 当前评论的帖子ID
const commentPostId = ref(null);
// 评论列表
const commentList = ref([]);
// 评论输入内容
const commentContent = ref('');

// 是否登录
const isLoggedIn = computed(() => userStore.isLoggedIn);

/**
 * 校验登录状态
 * @returns {boolean} 登录状态
 */
const ensureLogin = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再操作');
    return false;
  }
  return true;
};

/**
 * 打开发布帖子弹窗
 */
const openCreateDialog = () => {
  if (!ensureLogin()) return;
  form.title = '';
  form.content = '';
  createVisible.value = true;
};

/**
 * 加载帖子点赞状态
 */
const loadLikeStatus = async () => {
  if (!isLoggedIn.value) return;
  await Promise.all((allTableData.value || []).map(async (row) => {
    try {
      const status = await fetchForumStatus(row.id, userStore.userId);
      row.__liked = !!status.liked;
    } catch {
      row.__liked = false;
    }
  }));
};

/**
 * 加载帖子列表数据
 */
const loadAllData = async () => {
  loadingAll.value = true;
  try {
    const [startTime, endTime] = allTimeRange.value || [];
    const res = await fetchForumPage({
      keyword: allKeyword.value || undefined,
      startTime: startTime || undefined,
      endTime: endTime || undefined,
      pageNum: allPageNum.value,
      pageSize: pageSize.value
    });
    allTableData.value = res.records || [];
    allTotal.value = res.total || 0;
    await loadLikeStatus();
  } catch (error) {
    void error;
  } finally {
    loadingAll.value = false;
  }
};

/**
 * 搜索帖子
 */
const onSearchAll = () => {
  allPageNum.value = 1;
  loadAllData();
};

/**
 * 重置搜索条件
 */
const onResetAll = () => {
  allKeyword.value = '';
  allTimeRange.value = [];
  onSearchAll();
};

/**
 * 提交发布帖子
 */
const onSubmitPost = async () => {
  if (!ensureLogin()) return;
  if (!form.title.trim()) return ElMessage.warning('请输入标题');
  if (!form.content.trim()) return ElMessage.warning('请输入内容');
  submitting.value = true;
  try {
    await createForumPost({ userId: userStore.userId, title: form.title, content: form.content });
    ElMessage.success('帖子发布成功');
    createVisible.value = false;
    form.title = '';
    form.content = '';
    allPageNum.value = 1;
    await loadAllData();
  } catch (error) {
    void error;
  } finally {
    submitting.value = false;
  }
};

/**
 * 切换帖子点赞/取消点赞
 * @param {Object} row 帖子行数据
 */
const onToggleLike = async (row) => {
  if (!ensureLogin()) return;
  try {
    if (row.__liked) {
      await unlikeForumPost(row.id, userStore.userId);
      row.__liked = false;
      row.likeCount = Math.max((row.likeCount || 0) - 1, 0);
    } else {
      await likeForumPost(row.id, userStore.userId);
      row.__liked = true;
      row.likeCount = (row.likeCount || 0) + 1;
    }
  } catch (error) {
    void error;
  }
};

/**
 * 打开帖子详情
 * @param {Object} row 帖子行数据
 */
const openDetail = (row) => {
  detailPost.value = row;
  detailVisible.value = true;
};

/**
 * 打开评论弹窗并加载评论
 * @param {Object} row 帖子行数据
 */
const openComments = async (row) => {
  commentPostId.value = row.id;
  commentVisible.value = true;
  commentList.value = await fetchForumComments(row.id);
};

/**
 * 提交评论
 */
const onSubmitComment = async () => {
  if (!ensureLogin()) return;
  const content = commentContent.value.trim();
  if (!content) return ElMessage.warning('请输入评论内容');
  try {
    await createForumComment(commentPostId.value, { userId: userStore.userId, content });
    commentContent.value = '';
    commentList.value = await fetchForumComments(commentPostId.value);
    // 更新列表中的评论数
    const row = allTableData.value.find((r) => r.id === commentPostId.value);
    if (row) row.commentCount = (row.commentCount || 0) + 1;
    ElMessage.success('评论发表成功');
  } catch (error) {
    void error;
  }
};

/**
 * 列表分页切换
 * @param {number} p 页码
 */
const onAllPageChange = (p) => {
  allPageNum.value = p;
  loadAllData();
};

// 页面挂载时加载列表数据
onMounted(async () => {
  await loadAllData();
});
</script>

<style scoped>
.forum-page { max-width: 1240px; margin: 0 auto; }
.head-row { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.toolbar { margin-bottom: 12px; display: flex; gap: 10px; flex-wrap: wrap; padding: 12px; border: 1px solid var(--app-panel-border); border-radius: 12px; background: var(--app-panel-bg-soft); }
.pager { margin-top: 16px; }

.stat-badge { display: inline-flex; align-items: center; gap: 4px; color: var(--app-panel-text-muted); font-size: 14px; }
.stat-badge .el-icon { font-size: 16px; }

.post-detail { padding: 6px 2px; }
.post-detail-title { font-size: 22px; font-weight: 700; color: var(--app-panel-text-main); margin-bottom: 12px; line-height: 1.4; }
.post-detail-meta { display: flex; flex-wrap: wrap; gap: 14px; color: var(--app-panel-text-muted); font-size: 13px; margin-bottom: 14px; }
.post-detail-content { white-space: pre-wrap; line-height: 1.8; color: var(--app-panel-text); font-size: 14px; background: var(--app-panel-bg-soft); border: 1px solid var(--app-panel-border); border-radius: 10px; padding: 14px; max-height: 420px; overflow: auto; }

.comment-input-area { margin-bottom: 20px; padding: 16px; background: var(--app-panel-bg-soft); border-radius: 10px; border: 1px solid var(--app-panel-border); }
.comment-actions { margin-top: 10px; text-align: right; }

.comment-list { max-height: 500px; overflow-y: auto; }
.comment-item { display: flex; gap: 12px; padding: 16px; border-bottom: 1px solid var(--app-panel-border); }
.comment-item:last-child { border-bottom: none; }
.comment-avatar { flex-shrink: 0; }
.comment-body { flex: 1; min-width: 0; }
.comment-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.comment-username { font-weight: 600; color: var(--app-panel-text-main); font-size: 14px; }
.comment-time { font-size: 12px; color: var(--app-panel-text-faint); }
.comment-content { color: var(--app-panel-text-sub); font-size: 14px; line-height: 1.6; word-wrap: break-word; }
</style>
