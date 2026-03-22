<template>
  <div class="about-container">
    <el-card class="about-card">
      <template #header>
        <div class="card-header">
          <h2>关于我们</h2>
        </div>
      </template>
      <el-skeleton v-if="loading" :rows="6" animated />
      <div v-else-if="about" class="about-content">
        <div class="content-section" v-html="about.content"></div>
        <div class="meta-info" v-if="about.updateTime">
          <span>更新时间：{{ formatDate(about.updateTime) }}</span>
        </div>
      </div>
      <el-empty v-else description="暂无内容" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getAbout } from '../api/system';

const about = ref(null);
const loading = ref(false);

onMounted(() => {
  loadAbout();
});

const loadAbout = async () => {
  loading.value = true;
  try {
    const data = await getAbout();
    about.value = data || null;
  } catch {
    about.value = null;
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('zh-CN');
};
</script>

<style scoped>
.about-container {
  max-width: 900px;
  margin: 0 auto;
}

.about-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header h2 {
  margin: 0;
  color: #1d4ed8;
  font-size: 24px;
}

.about-content {
  line-height: 1.8;
}

.content-section {
  margin-bottom: 24px;
  color: #374151;
  font-size: 15px;
}

.content-section :deep(p) {
  margin-bottom: 16px;
}

.content-section :deep(h3) {
  color: #1f2937;
  margin-top: 24px;
  margin-bottom: 12px;
}

.meta-info {
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
  color: #6b7280;
  font-size: 13px;
}
</style>



