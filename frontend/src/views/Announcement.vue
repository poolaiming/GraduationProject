<template>
  <div class="announcement-page">
    <div class="app-card">
      <div class="app-page-title">公告资讯</div>
      <div class="app-page-subtitle">查看平台发布的官方公告与通知</div>

      <!-- 公告列表卡片 -->
      <el-skeleton v-if="loading" :rows="8" animated />
      <div v-else-if="tableData.length > 0" class="announcement-grid">
        <div
            v-for="item in tableData"
            :key="item.id"
            class="announcement-card"
            :class="{ 'is-active': current?.id === item.id }"
            @click="openDetail(item.id)"
        >
          <div class="announcement-card-header">
            <el-tag v-if="item.isTop === 1" type="warning" size="small" effect="dark" class="top-tag">置顶</el-tag>
            <h3 class="announcement-card-title" :title="item.title">{{ item.title }}</h3>
          </div>
          <div class="announcement-card-meta">
            <span>{{ (item.publishTime || '').split(' ')[0] }}</span>
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <!-- 空状态提示 -->
      <el-empty v-if="!loading && tableData.length === 0" description="暂无已发布公告" /> <!-- 修复编码异常 -->
    </div>

    <el-dialog
      v-model="detailVisible"
      title="公告详情"
      width="780px"
      top="8vh"
      class="announcement-detail-dialog"
      destroy-on-close
    >
      <div v-if="current" class="announcement-detail-container">
        <h2 class="detail-title">{{ current.title }}</h2>
        
        <div class="detail-header-panel">
          <div class="detail-meta">
            <span class="meta-badge"><el-icon><Calendar /></el-icon>{{ (current.publishTime || '-').split(' ')[0] }}</span>
            <span class="meta-badge"><el-icon><View /></el-icon>{{ current.viewCount || 0 }} 浏览</span>
            <span class="meta-badge"><el-icon><Star /></el-icon>{{ current.likeCount || 0 }} 喜欢</span>
          </div>

          <el-button
            class="like-btn"
            :class="{ 'is-liked': liked }"
            round
            @click="toggleLike"
            :loading="liking"
          >
            <template #icon>
              <el-icon v-if="liked"><StarFilled /></el-icon>
              <el-icon v-else><Star /></el-icon>
            </template>
            {{ liked ? '已特别喜欢' : '特别喜欢' }}
          </el-button>
        </div>

        <el-divider border-style="dashed" class="detail-divider" />

        <div class="detail-content" v-html="current.content"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { ArrowRight, Calendar, View, Star, StarFilled } from '@element-plus/icons-vue';
import { useUserStore } from '../store/user';
// 导入API请求函数
import {
  fetchAnnouncementList,
  fetchAnnouncementDetail,
  likeAnnouncement,
  unlikeAnnouncement,
  getAnnouncementStatus
} from '../api/announcement';

const userStore = useUserStore();
const currentUser = computed(() => userStore.user);
// 响应式数据定义
const loading = ref(false); // 列表加载状态
const tableData = ref([]); // 公告列表数据
const current = ref(null); // 当前选中的公告详情
const detailVisible = ref(false); // 公告详情弹窗状态
const liked = ref(false); // 当前用户是否点赞
const liking = ref(false); // 点赞/取消点赞操作状态

/**
 * 加载公告列表数据
 */
const loadData = async () => {
  loading.value = true;
  try {
    const list = await fetchAnnouncementList();
    tableData.value = list || [];
    current.value = null;
    detailVisible.value = false;
  } catch (error) {
    void error;
    tableData.value = [];
    current.value = null;
  } finally {
    loading.value = false;
  }
};

/**
 * 打开公告详情
 * @param {number|string} id 公告ID
 */
const openDetail = async (id) => {
  try {
    current.value = await fetchAnnouncementDetail(id);
    await loadLikeStatus(id); // 加载当前用户的点赞状态
    detailVisible.value = true;
  } catch (error) {
    void error;
  }
};

/**
 * 加载当前用户对指定公告的点赞状态
 * @param {number|string} announcementId 公告ID
 */
const loadLikeStatus = async (announcementId) => {
  if (!currentUser.value?.id) {
    liked.value = false;
    return;
  }

  try {
    const status = await getAnnouncementStatus(announcementId, currentUser.value.id);
    liked.value = Boolean(status.liked); // 确保是布尔值
  } catch (e) {
    console.warn('加载点赞状态失败：', e);
    liked.value = false;
  }
};

/**
 * 切换点赞/取消点赞状态
 */
const toggleLike = async () => {
  // 检查用户登录状态
  if (!currentUser.value?.id) {
    ElMessage.warning('请先登录');
    return;
  }

  liking.value = true;

  try {
    if (liked.value) {
      // 取消点赞
      await unlikeAnnouncement(current.value.id, currentUser.value.id);
      liked.value = false;
      current.value.likeCount = Math.max(0, (current.value.likeCount || 0) - 1);
      ElMessage.success('取消点赞成功');
    } else {
      // 点赞
      await likeAnnouncement(current.value.id, currentUser.value.id);
      liked.value = true;
      current.value.likeCount = (current.value.likeCount || 0) + 1;
      ElMessage.success('点赞成功');
    }
  } catch (error) {
    void error;
  } finally {
    liking.value = false;
  }
};

// 页面挂载时加载数据
onMounted(loadData);
</script>

<style scoped>
.announcement-page {
  max-width: 1240px;
  margin: 0 auto;
  padding: 16px; /* 增加内边距，适配小屏幕 */
}

.announcement-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-top: 16px;
}

.announcement-card {
  padding: 18px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 110px;
}

.announcement-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(37, 99, 235, 0.08);
  border-color: #bfdbfe;
}

.announcement-card.is-active {
  border-color: var(--app-primary, #2563eb);
  background: #f8fafc;
  box-shadow: 0 0 0 1px var(--app-primary, #2563eb);
}

.announcement-card-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 14px;
}

.top-tag {
  border-radius: 4px;
  font-weight: 600;
  letter-spacing: 1px;
}

.announcement-card-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s ease;
}

.announcement-card:hover .announcement-card-title {
  color: var(--app-primary, #2563eb);
}

.announcement-card-meta {
  font-size: 13px;
  color: #64748b;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.announcement-detail-container {
  padding: 0 10px;
  max-height: 70vh;
  overflow-y: auto;
  overflow-x: hidden;
}

.announcement-detail-container::-webkit-scrollbar {
  width: 6px;
}
.announcement-detail-container::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

.detail-title {
  font-size: 26px;
  font-weight: 800;
  margin-top: 0;
  margin-bottom: 20px;
  color: #0f172a;
  line-height: 1.4;
  letter-spacing: -0.5px;
}

.detail-header-panel {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.meta-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: #f1f5f9;
  color: #64748b;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.meta-badge:hover {
  background: #e2e8f0;
  color: #475569;
}

.meta-badge .el-icon {
  font-size: 15px;
}

.like-btn {
  padding: 10px 24px;
  font-weight: 600;
  border: 1px solid #e2e8f0 !important;
  background: #ffffff !important;
  color: #64748b !important;
  transition: all 0.3s ease;
}

.like-btn:hover {
  border-color: #cbd5e1 !important;
  background: #f8fafc !important;
  color: #475569 !important;
}

.like-btn.is-liked {
  background: #fff1f2 !important;
  border-color: #fecdd3 !important;
  color: #e11d48 !important;
}

.like-btn.is-liked:hover {
  background: #ffe4e6 !important;
  border-color: #fda4af !important;
}

.detail-divider {
  margin: 24px 0;
  border-color: #e2e8f0;
}

.detail-content {
  line-height: 2;
  color: #334155;
  font-size: 16px;
  padding-bottom: 10px;
}

.detail-content :deep(p) {
  margin: 0 0 16px;
}

.detail-content :deep(img) {
  max-width: 100%;
  border-radius: 12px;
  margin: 16px 0;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
  object-fit: contain;
}

/* 响应式适配 */
@media (max-width: 992px) {
  .announcement-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .detail-title {
    font-size: 22px;
  }

  .detail-content {
    font-size: 15px;
  }
}

@media (max-width: 576px) {
  .announcement-grid {
    grid-template-columns: 1fr;
  }

  .detail-header-panel {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .meta-badge {
    padding: 6px 12px;
    font-size: 12px;
  }
}
</style>
