<template>
  <div class="news-list-page">
    <div class="app-card motion-block motion-delay-1">
      <div class="head-row motion-block motion-delay-2">
        <div>
          <div class="app-page-title">热点新闻</div>
          <div class="app-page-subtitle">浏览最新热点资讯，支持分类、标签和关键词检索</div>
        </div>
        <el-button v-if="canPublishNews" type="primary" @click="openPublishDialog">发布新闻</el-button>
      </div>

      <el-alert
          v-if="currentUser?.id && !canPublishNews"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 12px"
          title="你当前还不是审核通过的新闻工作者，无法发布新闻"
      />

      <div class="category-tabs motion-block motion-delay-3">
        <el-button
            :type="query.categoryId === undefined ? 'primary' : 'default'"
            round
            size="small"
            @click="onCategoryChange(undefined)"
        >全部</el-button>
        <el-button
            v-for="cat in categoryOptions"
            :key="cat.id"
            :type="query.categoryId === cat.id ? 'primary' : 'default'"
            round
            size="small"
            @click="onCategoryChange(cat.id)"
        >{{ cat.name }}</el-button>
      </div>

      <div class="toolbar motion-block motion-delay-4">
        <el-input
            v-model="query.keyword"
            placeholder="按标题检索热点新闻"
            clearable
            @keyup.enter="onSearch"
            style="width: 240px"
        />
        <el-input
            v-model="query.authorName"
            placeholder="按作者名检索"
            clearable
            @keyup.enter="onSearch"
            style="width: 180px"
        />
        <el-select v-model="query.tagId" clearable placeholder="按标签检索" style="width: 160px">
          <el-option v-for="tag in tagOptions" :key="tag.id" :label="tag.name" :value="tag.id" />
        </el-select>
        <el-select v-model="query.sortBy" placeholder="排序方式" style="width: 140px" @change="onSearch">
          <el-option label="最新发布" value="" />
          <el-option label="点赞最多" value="likeCount" />
          <el-option label="收藏最多" value="collectCount" />
        </el-select>
        <el-button type="primary" @click="onSearch">检索</el-button>
      </div>

      <el-skeleton v-if="loading" :rows="8" animated style="margin-bottom: 10px" />

      <el-empty v-if="!loading && tableData.length === 0" description="没有找到符合条件的新闻" />

      <div v-else-if="!loading" class="news-grid motion-block motion-delay-5">
        <div
            v-for="item in tableData"
            :key="item.id"
            class="news-card js-news-item"
            @click="goDetail(item.id)"
        >
          <div class="news-card-cover">
            <img :src="item.displayCover || defaultNewsImage" alt="新闻封面" class="news-cover-img" />
            <div class="news-card-cat" v-if="getCategoryName(item.categoryId)">
              {{ getCategoryName(item.categoryId) }}
            </div>
            <div class="news-card-date" v-if="item.publishTime">
              {{ (item.publishTime || '').split(' ')[0] }}
            </div>
          </div>
          <div class="news-card-body">
            <div class="news-card-title">{{ item.title }}</div>
            <div class="news-card-info" v-if="item.authorName">
              <el-icon><User /></el-icon> {{ item.authorName }}
            </div>
            <div class="news-card-meta">
              <span>点击 {{ item.viewCount || 0 }}</span>
              <span class="meta-divider">·</span>
              <span>收藏 {{ item.collectCount || 0 }}</span>
              <span class="meta-divider">·</span>
              <span>点赞 {{ item.likeCount || 0 }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="pager motion-block motion-delay-6">
        <el-pagination
            background
            layout="prev, pager, next, jumper"
            :current-page="query.pageNum"
            :page-size="query.pageSize"
            :total="total"
            @current-change="onPageChange"
        />
      </div>

<el-dialog
v-model="publishVisible"
class="publish-news-dialog"
title="发布新闻"
width="760px"
top="5vh"
style="max-width: 95vw;"
draggable
        overflow
      >
        <el-form :model="newsForm" label-width="70px">
          <el-form-item label="标题"><el-input v-model="newsForm.title" maxlength="100" show-word-limit /></el-form-item>
          <el-form-item label="摘要"><el-input v-model="newsForm.summary" type="textarea" :rows="2" maxlength="300" show-word-limit /></el-form-item>
          <el-form-item label="内容">
            <el-input v-model="newsForm.content" type="textarea" :rows="8" maxlength="20000" show-word-limit placeholder="可输入文本，或通过下方按钮插入图片/GIF/视频" />
            <div class="media-upload-row">
              <el-upload
                  action="/api/upload/image"
                  :show-file-list="false"
                  accept="image/*"
                  :before-upload="beforeImageUpload"
                  :on-success="onImageUploadSuccess"
              >
                <el-button size="small" plain>插入图片/GIF</el-button>
              </el-upload>
              <el-upload
                  action="/api/upload/video"
                  :show-file-list="false"
                  accept="video/mp4,video/webm,video/ogg"
                  :before-upload="beforeVideoUpload"
                  :on-success="onVideoUploadSuccess"
              >
                <el-button size="small" plain>插入视频</el-button>
              </el-upload>
            </div>
            <div class="content-preview" v-if="newsForm.content.trim()">
              <div class="content-preview-title">内容预览</div>
              <div class="content-preview-body" v-html="newsForm.content"></div>
            </div>
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="newsForm.categoryId" clearable style="width:100%" placeholder="选择分类">
              <el-option v-for="item in categoryOptions" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="标签">
            <el-select v-model="newsForm.tagIds" multiple collapse-tags collapse-tags-tooltip style="width:100%" placeholder="可多选标签">
              <el-option v-for="item in tagOptions" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="publishVisible = false">取消</el-button>
          <el-button type="primary" :loading="publishingNews" @click="onPublishNews">发布新闻</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
// 导入Vue核心API
import { computed, reactive, ref, onMounted } from 'vue';
// 导入路由
import { useRouter } from 'vue-router';
// 导入Element Plus组件
import { ElMessage } from 'element-plus';
import { View, Collection, Star, User } from '@element-plus/icons-vue';
// 导入状态管理和API
import { useUserStore } from '../store/user';
import { fetchNewsPage, fetchNewsTags, fetchNewsCategories, createNews } from '../api/news';
import { defaultNewsImage, normalizeNewsCard } from '../utils/news';

// 初始化路由实例
const router = useRouter();
const userStore = useUserStore();
// 当前登录用户（计算属性）
const currentUser = computed(() => userStore.user);
// 是否有发布新闻权限（需登录且是审核通过的新闻工作者）
const canPublishNews = computed(() => !!currentUser.value?.id && currentUser.value?.isJournalist === 1);

// 响应式数据
const loading = ref(false); // 列表加载状态
const tableData = ref([]); // 新闻列表数据
const publishVisible = ref(false); // 发布弹窗显示状态
const publishingNews = ref(false); // 发布新闻加载状态
const total = ref(0); // 新闻总数
const tagOptions = ref([]); // 标签下拉选项
const categoryOptions = ref([]); // 分类下拉选项

// 发布新闻表单
const newsForm = reactive({
  title: '',        // 标题
  summary: '',      // 摘要
  content: '',      // 内容
  categoryId: null, // 分类ID
  tagIds: []        // 标签ID集合
});

// 检索查询参数
const query = reactive({
  keyword: '',      // 关键词
  categoryId: undefined, // 分类ID
  tagId: undefined, // 标签ID
  sortBy: '',       // 排序字段
  pageNum: 1,       // 当前页码
  pageSize: 10      // 每页条数
});

/**
 * 根据分类ID获取分类名称
 * @param {Number} id 分类ID
 * @returns {String} 分类名称
 */
const getCategoryName = (id) => {
  const cat = categoryOptions.value.find((c) => c.id === id);
  return cat ? cat.name : '';
};

/**
 * 加载新闻列表数据
 */
const loadData = async () => {
  loading.value = true;
  try {
    const page = await fetchNewsPage(query);
    tableData.value = (page.records || []).map(normalizeNewsCard);
    total.value = page.total || 0;
  } finally {
    loading.value = false;
  }
};

/**
 * 加载标签列表
 */
const loadTags = async () => {
  try {
    tagOptions.value = await fetchNewsTags();
  } catch {
    tagOptions.value = [];
  }
};

/**
 * 加载分类列表
 */
const loadCategories = async () => {
  try {
    categoryOptions.value = await fetchNewsCategories();
  } catch {
    categoryOptions.value = [];
  }
};

/**
 * 检索事件（重置页码并加载数据）
 */
const onSearch = () => {
  query.pageNum = 1;
  loadData();
};

/**
 * 分类切换事件
 * @param {Number} id 分类ID
 */
const onCategoryChange = (id) => {
  query.categoryId = id;
  query.pageNum = 1;
  loadData();
};

/**
 * 分页切换事件
 * @param {Number} page 目标页码
 */
const onPageChange = (page) => {
  query.pageNum = page;
  loadData();
};

/**
 * 跳转到新闻详情页
 * @param {Number} id 新闻ID
 */
const goDetail = (id) => {
  router.push(`/news/${id}`);
};

/**
 * 打开发布新闻弹窗（重置表单）
 */
const openPublishDialog = () => {
  newsForm.title = '';
  newsForm.summary = '';
  newsForm.content = '';
  newsForm.categoryId = null;
  newsForm.tagIds = [];
  publishVisible.value = true;
};

/**
 * 图片上传前校验
 * @param {File} file 上传文件
 * @returns {Boolean} 是否允许上传
 */
const beforeImageUpload = (file) => {
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

/**
 * 视频上传前校验
 * @param {File} file 上传文件
 * @returns {Boolean} 是否允许上传
 */
const beforeVideoUpload = (file) => {
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

/**
 * 图片上传成功处理
 * @param {Object} res 接口响应
 */
const onImageUploadSuccess = (res) => {
  if (res.code !== 0 || !res.data) return ElMessage.error(res.message || '图片上传失败');
  newsForm.content += `\n<img src="${res.data}" alt="image" style="max-width:100%;border-radius:8px;" />\n`;
  ElMessage.success('图片已插入内容');
};

/**
 * 视频上传成功处理
 * @param {Object} res 接口响应
 */
const onVideoUploadSuccess = (res) => {
  if (res.code !== 0 || !res.data) return ElMessage.error(res.message || '视频上传失败');
  newsForm.content += `\n<video src="${res.data}" controls style="max-width:100%;border-radius:8px;"></video>\n`;
  ElMessage.success('视频已插入内容');
};

/**
 * 发布新闻提交处理
 */
const onPublishNews = async () => {
  // 权限校验
  if (!currentUser.value?.id) return;
  if (currentUser.value?.isJournalist !== 1) return ElMessage.warning('请先通过新闻工作者审核');
  // 表单校验
  if (!newsForm.title.trim() || !newsForm.content.trim()) return ElMessage.warning('标题和内容不能为空');

  publishingNews.value = true;
  try {
    // 调用发布接口
    await createNews({
      authorId: currentUser.value.id,
      title: newsForm.title,
      summary: newsForm.summary,
      content: newsForm.content,
      categoryId: newsForm.categoryId,
      status: 1,
      tagIds: newsForm.tagIds
    });
    ElMessage.success('新闻发布成功');
    // 重置表单并关闭弹窗
    newsForm.title = '';
    newsForm.summary = '';
    newsForm.content = '';
    newsForm.categoryId = null;
    newsForm.tagIds = [];
    publishVisible.value = false;
    // 重新加载列表
    query.pageNum = 1;
    await loadData();
  } catch (error) {
    void error;
  } finally {
    publishingNews.value = false;
  }
};

// 页面挂载时加载初始化数据
onMounted(() => {
  loadTags();
  loadCategories();
  loadData();
});
</script>

<style scoped>
.news-list-page {
  max-width: 1240px;
  margin: 0 auto;
}

.head-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}

.category-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 14px;
  padding: 10px;
  border: 1px solid #e8eef8;
  border-radius: 12px;
  background: #f8fbff;
}

.toolbar {
  display: flex;
  justify-content: flex-start;
  gap: 10px;
  margin-bottom: 12px;
  flex-wrap: wrap;
  padding: 12px;
  border: 1px solid #e8eef8;
  border-radius: 12px;
  background: #ffffff;
}

.pager {
  margin-top: 18px;
  display: flex;
  justify-content: flex-end;
}

.title-link {
  color: #001b44;
  cursor: pointer;
  font-weight: 500;
}

.title-link:hover {
  text-decoration: underline;
}

.stat-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #64748b;
  font-size: 14px;
}

.stat-badge .el-icon {
font-size: 16px;
}

:deep(.publish-news-dialog .el-dialog) {
margin-bottom: 5vh;
}

:deep(.publish-news-dialog .el-dialog__body) {
max-height: calc(90vh - 170px);
overflow-y: auto;
overscroll-behavior: contain;
padding-right: 12px;
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
}

.content-preview-body :deep(img),
.content-preview-body :deep(video) {
  max-width: 100%;
  border-radius: 8px;
  margin: 8px 0;
}

/* 提取出来的卡片样式 */
.news-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  padding-top: 10px;
}

.news-card {
  cursor: pointer;
  background: #ffffff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(15, 23, 42, 0.04);
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
}

.news-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 32px rgba(37, 99, 235, 0.1);
  border-color: #bfdbfe;
}

.news-card:hover .news-cover-img {
  transform: scale(1.06);
}

.news-card:hover .news-card-title {
  color: var(--app-primary, #001b44);
}

.news-card-cover {
  position: relative;
  width: 100%;
  height: 160px;
  overflow: hidden;
  background-color: #f1f5f9;
}

.news-cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.news-card-date {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(15, 23, 42, 0.7);
  backdrop-filter: blur(4px);
  color: #fff;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 20px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.news-card-cat {
  position: absolute;
  top: 12px;
  left: 12px;
  background: #3b82f6;
  color: #fff;
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.news-card-body {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.news-card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.5;
  margin-bottom: auto;
  min-height: 48px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s ease;
}

.news-card-info {
  margin-top: 10px;
  font-size: 13px;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 6px;
}

.news-card-meta {
  margin-top: 12px;
  font-size: 13px;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 10px;
}

.meta-divider {
  color: #cbd5e1;
  font-size: 14px;
}

/* 响应式适配 */
@media (max-width: 992px) {
  .toolbar,
  .category-tabs {
    padding: 10px;
  }
  .news-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .news-grid {
    grid-template-columns: 1fr;
  }
}
</style>
