<template>
  <div class="profile-page">
    <div class="app-card">
      <div class="app-page-title">个人信息</div>
      <div class="app-page-subtitle">管理你的基础信息、账号安全和个人内容</div>

      <el-row :gutter="20" class="profile-grid">
        <el-col :xs="24" :lg="8" class="left-panel-col">
          <el-card shadow="never" class="panel-card panel-card--profile">
            <template #header><span class="card-title">基础信息</span></template>
            <el-form :model="profileForm" label-width="80px">
              <el-form-item label="用户名"><el-input v-model="profileForm.username" disabled /></el-form-item>
              <el-form-item label="昵称"><el-input v-model="profileForm.nickname" placeholder="请输入昵称" /></el-form-item>
              <el-form-item label="头像">
                <div class="avatar-upload-area">
                  <el-avatar :size="64" :src="profileForm.avatar || undefined" class="avatar-preview">
                    {{ profileForm.avatar ? '' : (profileForm.nickname || profileForm.username || '?')[0] }}
                  </el-avatar>
                  <el-upload
                    action="/api/upload/image"
                    :show-file-list="false"
                    accept="image/*"
                    :on-success="onAvatarSuccess"
                    :before-upload="beforeAvatarUpload"
                  >
                    <el-button size="small" type="primary" plain style="margin-left: 12px">上传头像</el-button>
                  </el-upload>
                </div>
              </el-form-item>
              <el-form-item><el-button type="primary" :loading="savingProfile" @click="onSaveProfile">保存</el-button></el-form-item>
            </el-form>
          </el-card>

          <el-card shadow="never" class="panel-card">
            <template #header><span class="card-title">新闻工作者资质</span></template>
            <div class="journalist-box">
              <el-tag v-if="journalistStatus === 1" type="success">新闻工作者（已通过）</el-tag>
              <el-tag v-else-if="journalistStatus === 2" type="warning">申请中（待审核）</el-tag>
              <el-tag v-else-if="journalistStatus === 3" type="danger">申请被拒绝</el-tag>
              <el-tag v-else type="info">未申请</el-tag>
              <el-button v-if="journalistStatus !== 1 && journalistStatus !== 2" type="primary" :loading="registeringJournalist" @click="onRegisterJournalist">申请成为新闻工作者</el-button>
            </div>
          </el-card>

          <el-card shadow="never" class="panel-card">
            <template #header><span class="card-title">账号安全</span></template>
            <div class="security-actions">
              <el-button type="primary" @click="openPasswordDialog">修改密码</el-button>
              <el-button type="danger" plain @click="onRequestCancelAccount">申请注销账号</el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :lg="16" class="right-panel-col">
          <el-card shadow="never" class="panel-card panel-card--content">
            <template #header><span class="card-title">个人中心</span></template>
            <el-tabs v-model="activeTopTab">
              <el-tab-pane label="我的内容" name="content">
                <el-tabs v-model="activeContentTab">
                  <el-tab-pane label="我的论坛帖子" name="forum">
                    <el-table :data="myForum.records" v-loading="loadingForum" size="small">
                      <el-table-column prop="title" label="标题" min-width="260" show-overflow-tooltip />
                      <el-table-column prop="likeCount" label="点赞" width="90" />
                      <el-table-column prop="commentCount" label="评论" width="90" />
                      <el-table-column prop="status" label="状态" width="100">
                        <template #default="{ row }">
                          <el-tag v-if="row.status === 1" type="warning">待审核</el-tag>
                          <el-tag v-else-if="row.status === 2" type="success">已发布</el-tag>
                          <el-tag v-else-if="row.status === 3" type="danger">驳回</el-tag>
                          <el-tag v-else>未知</el-tag>
                        </template>
                      </el-table-column>
                      <el-table-column prop="reviewRemark" label="审核意见" min-width="180" show-overflow-tooltip />
                      <el-table-column prop="createTime" label="发布时间" width="170" />
                      <el-table-column label="操作" width="240" fixed="right">
                        <template #default="{ row }">
                          <el-button type="primary" link @click="openForumComments(row)">查看评论</el-button>
                          <el-button type="primary" link @click="openEditForum(row)">编辑</el-button>
                          <el-button type="danger" link @click="onDeleteForum(row)">删除</el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-tab-pane>

                  <el-tab-pane label="我的留言" name="message">
                    <el-table :data="myMessages.records" v-loading="loadingMessage" size="small">
                      <el-table-column prop="content" label="留言内容" min-width="300" show-overflow-tooltip />
                      <el-table-column prop="status" label="状态" width="90" />
                      <el-table-column prop="createTime" label="留言时间" width="170" />
                    </el-table>
                  </el-tab-pane>

                  <el-tab-pane v-if="isJournalist" label="我发布的新闻" name="news">
                    <div class="news-list-wrap">
                      <div class="news-section-title">我的新闻列表</div>
                      <el-table :data="myNews.records" v-loading="loadingNews" size="small" style="width: 100%">
                        <el-table-column prop="title" label="标题" min-width="260" show-overflow-tooltip />
                        <el-table-column label="审核状态" width="120">
                          <template #default="{ row }">
                            <el-tag v-if="row.status === 1" type="warning">待审核</el-tag>
                            <el-tag v-else-if="row.status === 2" type="success">已发布</el-tag>
                            <el-tag v-else-if="row.status === 3" type="danger">驳回</el-tag>
                            <el-tag v-else type="info">草稿/未知</el-tag>
                          </template>
                        </el-table-column>
                        <el-table-column prop="reviewRemark" label="驳回原因/审核意见" min-width="220" show-overflow-tooltip>
                          <template #default="{ row }">
                            <span v-if="row.reviewRemark">{{ row.reviewRemark }}</span>
                            <span v-else style="color: #94a3b8">--</span>
                          </template>
                        </el-table-column>
                        <el-table-column prop="publishTime" label="发布时间" width="180" />
                        <el-table-column label="操作" width="180">
                          <template #default="{ row }">
                            <el-button type="primary" link @click="goNewsDetail(row.id)">查看</el-button>
                            <el-button type="primary" link @click="openEditNews(row)">编辑</el-button>
                          </template>
                        </el-table-column>
                      </el-table>
                    </div>
                  </el-tab-pane>
                </el-tabs>
              </el-tab-pane>

              <el-tab-pane label="浏览历史" name="historyMenu">
                <el-table :data="myHistory.records" v-loading="loadingHistory" size="small">
                  <el-table-column prop="title" label="新闻标题" min-width="320" show-overflow-tooltip>
                    <template #default="{ row }">
                      <a class="history-link" @click.prevent="goNewsDetail(row.id)">{{ row.title }}</a>
                    </template>
                  </el-table-column>
                  <el-table-column prop="publishTime" label="发布时间" width="170" />
                </el-table>
              </el-tab-pane>

              <el-tab-pane label="我的点赞" name="likes">
                <el-tabs v-model="activeLikeTab">
                  <el-tab-pane label="点赞的新闻" name="likedNews">
                    <el-table :data="myLikedNews.records" v-loading="loadingLikedNews" size="small">
                      <el-table-column prop="title" label="新闻标题" min-width="320" show-overflow-tooltip>
                        <template #default="{ row }">
                          <a class="history-link" @click.prevent="goNewsDetail(row.id)">{{ row.title }}</a>
                        </template>
                      </el-table-column>
                      <el-table-column prop="publishTime" label="发布时间" width="170" />
                    </el-table>
                  </el-tab-pane>
                  <el-tab-pane label="点赞的帖子" name="likedForum">
                    <el-table :data="myLikedForum.records" v-loading="loadingLikedForum" size="small">
                      <el-table-column prop="title" label="帖子标题" min-width="320" show-overflow-tooltip />
                      <el-table-column prop="authorName" label="作者" width="140" />
                      <el-table-column prop="createTime" label="发布时间" width="170" />
                    </el-table>
                  </el-tab-pane>
                </el-tabs>
              </el-tab-pane>

              <el-tab-pane label="我的收藏" name="collects">
                <el-table :data="myCollectedNews.records" v-loading="loadingCollects" size="small">
                  <el-table-column prop="title" label="新闻标题" min-width="320" show-overflow-tooltip>
                    <template #default="{ row }">
                      <a class="history-link" @click.prevent="goNewsDetail(row.id)">{{ row.title }}</a>
                    </template>
                  </el-table-column>
                  <el-table-column prop="publishTime" label="发布时间" width="170" />
                </el-table>
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-dialog v-model="passwordVisible" title="修改密码" width="520px">
      <el-form :model="passwordForm" label-width="92px">
        <el-form-item label="原密码"><el-input v-model="passwordForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="passwordForm.newPassword" type="password" show-password /></el-form-item>
        <el-form-item label="确认新密码"><el-input v-model="passwordForm.confirmPassword" type="password" show-password /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingPassword" @click="onChangePassword">确认修改</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editForumVisible" title="编辑帖子" width="700px">
      <el-form :model="editForumForm" label-width="70px">
        <el-form-item label="标题"><el-input v-model="editForumForm.title" maxlength="100" show-word-limit /></el-form-item>
        <el-form-item label="内容"><el-input v-model="editForumForm.content" type="textarea" :rows="5" maxlength="2000" show-word-limit /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editForumVisible = false">取消</el-button>
        <el-button type="primary" :loading="updatingForum" @click="onUpdateForum">保存修改</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="forumCommentsVisible" title="帖子评论" width="760px">
      <div class="comment-input-area" v-if="currentUser?.id">
        <el-input v-model="forumCommentContent" type="textarea" :rows="3" placeholder="请输入评论内容..." />
        <div class="comment-actions">
          <el-button type="primary" @click="onSubmitForumComment">发表评论</el-button>
        </div>
      </div>
      <div class="comment-list" v-loading="loadingForumComments">
        <el-empty v-if="!loadingForumComments && forumComments.length === 0" description="暂无评论，快来抢沙发吧~" />
        <div v-else class="comment-item" v-for="item in forumComments" :key="item.id">
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

    <el-dialog
      v-model="editNewsVisible"
      title="编辑新闻"
      width="760px"
      top="5vh"
      style="max-width: 95vw;"
      draggable
      overflow
    >
      <div class="news-edit-scroll-container">
        <el-form :model="editNewsForm" label-width="70px" class="news-edit-form">
          <el-form-item label="标题"><el-input v-model="editNewsForm.title" maxlength="100" show-word-limit /></el-form-item>
          <el-form-item label="摘要"><el-input v-model="editNewsForm.summary" type="textarea" :rows="2" maxlength="300" show-word-limit /></el-form-item>
          <el-form-item label="内容">
            <el-input
              v-model="editNewsForm.content"
              type="textarea"
              :rows="5"
              maxlength="20000"
              show-word-limit
              placeholder="可输入文本，或通过下方按钮插入图片/GIF/视频"
            />
            <div class="media-upload-row">
              <el-upload
                action="/api/upload/image"
                :show-file-list="false"
                accept="image/*"
                :before-upload="beforeEditNewsImageUpload"
                :on-success="onEditNewsImageUploadSuccess"
              >
                <el-button size="small" plain>插入图片/GIF</el-button>
              </el-upload>
              <el-upload
                action="/api/upload/video"
                :show-file-list="false"
                accept="video/mp4,video/webm,video/ogg"
                :before-upload="beforeEditNewsVideoUpload"
                :on-success="onEditNewsVideoUploadSuccess"
              >
                <el-button size="small" plain>插入视频</el-button>
              </el-upload>
            </div>
            <div class="content-preview" v-if="editNewsForm.content.trim()">
              <div class="content-preview-title">内容预览</div>
              <div class="content-preview-body" v-html="editNewsForm.content"></div>
            </div>
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="editNewsForm.categoryId" clearable style="width:100%" placeholder="选择分类">
              <el-option v-for="item in categoryOptions" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="标签">
            <el-select v-model="editNewsForm.tagIds" multiple collapse-tags collapse-tags-tooltip style="width:100%" placeholder="可多选标签">
              <el-option v-for="item in tagOptions" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="editNewsVisible = false">取消</el-button>
        <el-button type="primary" :loading="updatingNews" @click="onUpdateNews">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';
import { useUserStore } from '../store/user';
import { updateProfile, changePassword, requestCancelAccount, registerJournalist } from '../api/user';
import { fetchMyNews, fetchNewsDetail, fetchTagsByNewsId, updateNews, fetchNewsTags, fetchNewsCategories, fetchBrowseHistory, fetchMyLikedNews, fetchMyCollectedNews } from '../api/news';
import { fetchMyForumPosts, fetchMyLikedForumPosts, updateForumPost, deleteForumPost, fetchForumComments, createForumComment } from '../api/forum';
import { fetchMyMessages } from '../api/message';

const router = useRouter();
const userStore = useUserStore();
const currentUser = computed(() => userStore.user);
const journalistStatus = computed(() => currentUser.value?.isJournalist ?? 0);
const isJournalist = computed(() => journalistStatus.value === 1);

const profileForm = reactive({ username: '', nickname: '', avatar: '' });
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' });
const passwordVisible = ref(false);
const savingProfile = ref(false);
const savingPassword = ref(false);
const registeringJournalist = ref(false);

const activeTopTab = ref('content');
const activeContentTab = ref('forum');
const myNews = reactive({ records: [] });
const myForum = reactive({ records: [] });
const myMessages = reactive({ records: [] });
const myHistory = reactive({ records: [] });
const loadingNews = ref(false);
const loadingForum = ref(false);
const loadingMessage = ref(false);
const loadingHistory = ref(false);
const myLikedNews = reactive({ records: [] });
const myLikedForum = reactive({ records: [] });
const myCollectedNews = reactive({ records: [] });
const loadingLikedNews = ref(false);
const loadingLikedForum = ref(false);
const loadingCollects = ref(false);
const activeLikeTab = ref('likedNews');

const editForumVisible = ref(false);
const updatingForum = ref(false);
const editForumForm = reactive({ id: null, title: '', content: '' });
const forumCommentsVisible = ref(false);
const loadingForumComments = ref(false);
const forumComments = ref([]);
const forumCommentPostId = ref(null);
const forumCommentContent = ref('');

const editNewsVisible = ref(false);
const editNewsForm = reactive({ id: null, title: '', summary: '', content: '', categoryId: null, tagIds: [] });
const updatingNews = ref(false);
const tagOptions = ref([]);
const categoryOptions = ref([]);

const ensureLogin = () => {
  if (!currentUser.value) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return false;
  }
  return true;
};

const openPasswordDialog = () => {
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
  passwordVisible.value = true;
};

const initProfile = () => {
  if (!currentUser.value) return;
  profileForm.username = currentUser.value.username;
  profileForm.nickname = currentUser.value.nickname || '';
  profileForm.avatar = currentUser.value.avatar || '';
};

const onAvatarSuccess = (response) => {
  if (response.code === 0) {
    profileForm.avatar = response.data;
    ElMessage.success('头像上传成功，点击保存生效');
  } else {
    ElMessage.error(response.message || '上传失败');
  }
};

const beforeAvatarUpload = (file) => {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件');
    return false;
  }
  if (file.size / 1024 / 1024 > 5) {
    ElMessage.error('图片大小不能超过 5MB');
    return false;
  }
  return true;
};

const appendEditNewsMedia = (markup) => {
  editNewsForm.content = `${editNewsForm.content || ''}\n${markup}\n`;
};

const beforeEditNewsImageUpload = (file) => {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片/GIF 文件');
    return false;
  }
  if (file.size / 1024 / 1024 > 8) {
    ElMessage.error('图片/GIF 大小不能超过 8MB');
    return false;
  }
  return true;
};

const beforeEditNewsVideoUpload = (file) => {
  const allow = ['video/mp4', 'video/webm', 'video/ogg'];
  if (!allow.includes(file.type)) {
    ElMessage.error('仅支持mp4/webm/ogg 视频');
    return false;
  }
  if (file.size / 1024 / 1024 > 50) {
    ElMessage.error('视频大小不能超过 50MB');
    return false;
  }
  return true;
};

const onEditNewsImageUploadSuccess = (res) => {
  if (res.code !== 0 || !res.data) return ElMessage.error(res.message || '图片上传失败');
  appendEditNewsMedia(`<img src="${res.data}" alt="image" style="max-width:100%;border-radius:8px;" />`);
  ElMessage.success('图片已插入内容');
};

const onEditNewsVideoUploadSuccess = (res) => {
  if (res.code !== 0 || !res.data) return ElMessage.error(res.message || '视频上传失败');
  appendEditNewsMedia(`<video src="${res.data}" controls style="max-width:100%;border-radius:8px;"></video>`);
  ElMessage.success('视频已插入内容');
};

const onSaveProfile = async () => {
  if (!ensureLogin()) return;
  savingProfile.value = true;
  try {
    const updated = await updateProfile({ userId: currentUser.value.id, nickname: profileForm.nickname, avatar: profileForm.avatar });
    userStore.setUser(updated);
    ElMessage.success('个人信息已更新');
  } catch (error) {
    void error;
  } finally {
    savingProfile.value = false;
  }
};

const onRegisterJournalist = async () => {
  if (!ensureLogin()) return;
  registeringJournalist.value = true;
  try {
    await registerJournalist({ userId: currentUser.value.id });
    userStore.setUser({ ...currentUser.value, isJournalist: 2 });
    ElMessage.success('申请已提交，请等待管理员审核');
  } catch (error) {
    void error;
  } finally {
    registeringJournalist.value = false;
  }
};

const openEditNews = async (row) => {
  if (!ensureLogin()) return;
  try {
    const detail = await fetchNewsDetail(row.id, currentUser.value.id);
    const tags = await fetchTagsByNewsId(row.id);
    editNewsForm.id = detail.id;
    editNewsForm.title = detail.title || '';
    editNewsForm.summary = detail.summary || '';
    editNewsForm.content = detail.content || '';
    editNewsForm.categoryId = detail.categoryId ?? null;
    editNewsForm.tagIds = (tags || []).map((t) => t.id).filter(Boolean);
    editNewsVisible.value = true;
  } catch (error) {
    void error;
  }
};

const submitNewsUpdate = async (republish = false) => {
  if (!ensureLogin()) return;
  if (!editNewsForm.title.trim() || !editNewsForm.content.trim()) return ElMessage.warning('标题和内容不能为空');

  updatingNews.value = true;
  try {
    await updateNews(editNewsForm.id, {
      operatorId: currentUser.value.id,
      title: editNewsForm.title,
      summary: editNewsForm.summary,
      content: editNewsForm.content,
      categoryId: editNewsForm.categoryId,
      tagIds: editNewsForm.tagIds
    });
    ElMessage.success(republish ? '已提交重新发布，等待审核' : '新闻更新成功');
    editNewsVisible.value = false;
    await loadMyNews();
  } catch (error) {
    void error;
  } finally {
    updatingNews.value = false;
  }
};

const onUpdateNews = async () => {
  await submitNewsUpdate(false);
};

const onRepublishNews = async () => {
  await submitNewsUpdate(true);
};

const onChangePassword = async () => {
  if (!ensureLogin()) return;
  if (!passwordForm.oldPassword || !passwordForm.newPassword) return ElMessage.warning('请填写完整原密码和新密码');
  if (passwordForm.newPassword !== passwordForm.confirmPassword) return ElMessage.warning('两次输入的新密码不一致');
  savingPassword.value = true;
  try {
    await changePassword({ userId: currentUser.value.id, oldPassword: passwordForm.oldPassword, newPassword: passwordForm.newPassword });
    ElMessage.success('密码修改成功，即将退出并跳转登录');
    passwordVisible.value = false;
    userStore.logout();
    router.replace('/login');
  } catch (error) {
    void error;
  } finally {
    savingPassword.value = false;
  }
};

const onRequestCancelAccount = async () => {
  if (!ensureLogin()) return;
  try {
    await requestCancelAccount({ userId: currentUser.value.id });
    ElMessage.success('注销申请已提交，请等待管理员处理');
    userStore.logout();
    router.replace('/login');
  } catch (error) {
    void error;
  }
};

const openEditForum = (row) => {
  editForumForm.id = row.id;
  editForumForm.title = row.title || '';
  editForumForm.content = row.content || '';
  editForumVisible.value = true;
};

const onUpdateForum = async () => {
  if (!ensureLogin()) return;
  if (!editForumForm.id) return;
  if (!editForumForm.title.trim() || !editForumForm.content.trim()) return ElMessage.warning('标题和内容不能为空');

  updatingForum.value = true;
  try {
    await updateForumPost(editForumForm.id, {
      userId: currentUser.value.id,
      title: editForumForm.title,
      content: editForumForm.content
    });
    ElMessage.success('帖子更新成功');
    editForumVisible.value = false;
    await loadMyForum();
  } catch (error) {
    void error;
  } finally {
    updatingForum.value = false;
  }
};

const onDeleteForum = async (row) => {
  if (!ensureLogin()) return;
  try {
    await ElMessageBox.confirm('确认删除该帖子？', '提示', { type: 'warning' });
    await deleteForumPost(row.id, currentUser.value.id);
    ElMessage.success('删除成功');
    await loadMyForum();
  } catch (e) {
    if (e === 'cancel') return;
    void e;
  }
};

const openForumComments = async (row) => {
  forumCommentPostId.value = row.id;
  forumCommentContent.value = '';
  forumCommentsVisible.value = true;
  loadingForumComments.value = true;
  try {
    forumComments.value = await fetchForumComments(row.id);
  } catch (error) {
    void error;
    forumComments.value = [];
  } finally {
    loadingForumComments.value = false;
  }
};

const onSubmitForumComment = async () => {
  if (!ensureLogin()) return;
  const content = forumCommentContent.value.trim();
  if (!content) return ElMessage.warning('请输入评论内容');
  if (!forumCommentPostId.value) return;
  try {
    await createForumComment(forumCommentPostId.value, { userId: currentUser.value.id, content });
    forumCommentContent.value = '';
    forumComments.value = await fetchForumComments(forumCommentPostId.value);
    const row = myForum.records.find((r) => r.id === forumCommentPostId.value);
    if (row) row.commentCount = (row.commentCount || 0) + 1;
    ElMessage.success('评论成功');
  } catch (error) {
    void error;
  }
};

const goNewsDetail = (id) => {
  router.push(`/news/${id}`);
};

const loadMyNews = async () => {
  if (!ensureLogin() || !isJournalist.value) return (myNews.records = []);
  loadingNews.value = true;
  try {
    const page = await fetchMyNews({ authorId: currentUser.value.id, pageNum: 1, pageSize: 50 });
    myNews.records = page.records || [];
  } finally {
    loadingNews.value = false;
  }
};

const loadMyForum = async () => {
  if (!ensureLogin()) return;
  loadingForum.value = true;
  try {
    const page = await fetchMyForumPosts({ userId: currentUser.value.id, pageNum: 1, pageSize: 50 });
    myForum.records = page.records || [];
  } finally {
    loadingForum.value = false;
  }
};

const loadMyMessages = async () => {
  if (!ensureLogin()) return;
  loadingMessage.value = true;
  try {
    const page = await fetchMyMessages({ userId: currentUser.value.id, pageNum: 1, pageSize: 50 });
    myMessages.records = page.records || [];
  } finally {
    loadingMessage.value = false;
  }
};

const loadMyHistory = async () => {
  if (!ensureLogin()) return;
  loadingHistory.value = true;
  try {
    myHistory.records = await fetchBrowseHistory(currentUser.value.id, 50);
  } finally {
    loadingHistory.value = false;
  }
};

const loadMyLikedNews = async () => {
  if (!ensureLogin()) return;
  loadingLikedNews.value = true;
  try {
    myLikedNews.records = await fetchMyLikedNews(currentUser.value.id, 50);
  } finally {
    loadingLikedNews.value = false;
  }
};

const loadMyLikedForum = async () => {
  if (!ensureLogin()) return;
  loadingLikedForum.value = true;
  try {
    myLikedForum.records = await fetchMyLikedForumPosts(currentUser.value.id, 50);
  } finally {
    loadingLikedForum.value = false;
  }
};

const loadMyCollectedNews = async () => {
  if (!ensureLogin()) return;
  loadingCollects.value = true;
  try {
    myCollectedNews.records = await fetchMyCollectedNews(currentUser.value.id, 50);
  } finally {
    loadingCollects.value = false;
  }
};

const loadTags = async () => {
  try {
    tagOptions.value = await fetchNewsTags();
  } catch {
    tagOptions.value = [];
  }
};

const loadCategories = async () => {
  try {
    categoryOptions.value = await fetchNewsCategories();
  } catch {
    categoryOptions.value = [];
  }
};

onMounted(async () => {
  if (!ensureLogin()) return;
  initProfile();
  await Promise.all([loadMyForum(), loadMyMessages(), loadMyHistory(), loadTags(), loadCategories(), loadMyLikedNews(), loadMyLikedForum(), loadMyCollectedNews()]);
  if (isJournalist.value) {
    activeContentTab.value = 'news';
    await loadMyNews();
  }
});
</script>

<style scoped>
.profile-page {
  max-width: 1280px;
  margin: 0 auto;
  padding: 6px 4px 18px;
}

.profile-grid {
  margin-top: 10px;
}

.left-panel-col,
.right-panel-col {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.panel-card {
  border: 1px solid #e9eef6;
  border-radius: 14px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.04);
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
}

.panel-card :deep(.el-card__header) {
  border-bottom: 1px solid #eef3f9;
  padding: 14px 18px;
}

.panel-card :deep(.el-card__body) {
  padding: 16px 18px;
}

.card-title {
  font-weight: 600;
  font-size: 15px;
  color: #1e293b;
}

.panel-card--content :deep(.el-tabs__header) {
  margin-bottom: 14px;
}

.panel-card--content :deep(.el-tabs__item) {
  font-weight: 500;
}

.journalist-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.security-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.news-layout {
  display: grid;
  gap: 14px;
  min-height: 0;
}

.news-list-wrap {
  padding: 12px;
  border: 1px solid #e8eef7;
  border-radius: 12px;
  background: #ffffff;
  min-height: 0;
}

.news-table-scroll {
  min-height: 0;
  overflow-x: auto;
  overflow-y: hidden;
}

.news-table-scroll :deep(.el-table) {
  border-radius: 10px;
  overflow: hidden;
}

.news-section-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #334155;
}

.publish-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #64748b;
}

.history-link {
  color: #001b44;
  cursor: pointer;
  text-decoration: none;
}

.history-link:hover {
  text-decoration: underline;
}

.avatar-upload-area {
  display: flex;
  align-items: center;
}

.media-upload-row {
  margin-top: 10px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.content-preview {
  margin-top: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fafafa;
}

.content-preview-title {
  font-size: 13px;
  color: #475569;
  font-weight: 600;
  padding: 8px 10px;
  border-bottom: 1px solid #e5e7eb;
}

.content-preview-body {
  padding: 10px;
  color: #1e293b;
  line-height: 1.8;
  max-height: 250px;
  overflow-y: auto;
}

.news-edit-scroll-container {
  max-height: 60vh;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 6px;
}

/* Custom scrollbar for better appearance */
.news-edit-scroll-container::-webkit-scrollbar,
.content-preview-body::-webkit-scrollbar {
  width: 6px;
}
.news-edit-scroll-container::-webkit-scrollbar-thumb,
.content-preview-body::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

.content-preview-body :deep(img),
.content-preview-body :deep(video) {
  max-width: 100%;
  border-radius: 8px;
  margin: 8px 0;
}

.avatar-preview {
  flex-shrink: 0;
  font-size: 22px;
  font-weight: 600;
  background: #dbeafe;
  color: #1d4ed8;
}

.comment-input-area { margin-bottom: 20px; padding: 16px; background: #f8fafc; border-radius: 8px; }
.comment-actions { margin-top: 10px; text-align: right; }

.comment-list { max-height: 500px; overflow-y: auto; }
.comment-item { display: flex; gap: 12px; padding: 16px; border-bottom: 1px solid #e5e7eb; }
.comment-item:last-child { border-bottom: none; }
.comment-avatar { flex-shrink: 0; }
.comment-body { flex: 1; min-width: 0; }
.comment-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.comment-username { font-weight: 600; color: #1e293b; font-size: 14px; }
.comment-time { font-size: 12px; color: #94a3b8; }
.comment-content { color: #475569; font-size: 14px; line-height: 1.6; word-wrap: break-word; }

@media (max-width: 1200px) {
  .profile-page {
    max-width: 100%;
  }
}

@media (max-width: 992px) {
  .profile-page {
    padding: 4px 0 14px;
  }

  .panel-card :deep(.el-card__header),
  .panel-card :deep(.el-card__body) {
    padding-left: 14px;
    padding-right: 14px;
  }

  .news-list-wrap {
    padding: 12px;
  }

  .news-table-scroll :deep(.el-table) {
    font-size: 12px;
  }
}
</style>
