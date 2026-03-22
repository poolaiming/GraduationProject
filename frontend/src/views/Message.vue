<template>
  <div class="message-page">
    <div class="app-card" style="margin-bottom: 16px;">
      <div class="app-page-title">在线留言</div>
      <!-- 修复：补全截断的中文 -->
      <div class="app-page-subtitle">向管理员反馈你的问题和建议</div>
      <el-form
          :model="form"
          label-width="80px"
          style="max-width: 700px; margin-top: 12px;"
      >
        <el-form-item label="留言内容">
          <el-input
              v-model="form.content"
              type="textarea"
              :rows="4"
              maxlength="500"
              show-word-limit
              placeholder="请输入你想反馈的问题或建议"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button
              type="primary"
              :loading="loading"
              @click="onSubmit"
          >提交留言</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="app-card">
      <div class="section-title">我的留言</div>
      <el-table :data="tableData" v-loading="loadingList" size="small">
        <el-table-column prop="content" label="留言内容" min-width="280" show-overflow-tooltip />
        <!-- 修复：补全截断的列名 -->
        <el-table-column prop="replyContent" label="管理员回复" min-width="220" show-overflow-tooltip />
        <!-- 修复：补全截断的列名 -->
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <!-- 修复：补全截断的状态文字 -->
            <el-tag v-if="row.status === 0" type="warning">未处理</el-tag>
            <el-tag v-else type="success">已处理</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180" />
      </el-table>
      <el-pagination
          class="pager"
          background
          layout="prev, pager, next"
          :current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          @current-change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useUserStore } from '../store/user';
import { createMessage, fetchMyMessages } from '../api/message';

const userStore = useUserStore();
const loading = ref(false);
const loadingList = ref(false);
const form = reactive({
  content: ''
});

const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 封装登录校验（无修改，逻辑保留）
const ensureLogin = () => {
  if (!userStore.userId) {
    ElMessage.warning('请先登录');
    return false;
  }
  return true;
};

// 加载我的留言列表（无核心逻辑修改，仅保留原有健壮性）
const loadMyMessages = async () => {
  if (!ensureLogin()) return;
  loadingList.value = true;
  try {
    const res = await fetchMyMessages({
      userId: userStore.userId,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    });
    tableData.value = res.records || [];
    total.value = res.total || 0;
  } catch (error) {
    void error;
  } finally {
    loadingList.value = false;
  }
};

// 提交留言（无核心逻辑修改，保留原有校验）
const onSubmit = async () => {
  if (!ensureLogin()) return;
  if (!form.content || !form.content.trim()) {
    ElMessage.warning('请先填写留言内容');
    return;
  }
  loading.value = true;
  try {
    await createMessage({
      userId: userStore.userId,
      content: form.content
    });
    ElMessage.success('留言已提交，感谢你的反馈');
    form.content = '';
    pageNum.value = 1;
    loadMyMessages();
  } catch (error) {
    void error;
  } finally {
    loading.value = false;
  }
};

// 分页切换（无修改）
const onPageChange = (p) => {
  pageNum.value = p;
  loadMyMessages();
};

// 挂载时加载列表（无修改）
onMounted(loadMyMessages);
</script>

<style scoped>
.message-page {
  max-width: 1000px;
  margin: 0 auto;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}
.pager {
  margin-top: 16px;
}
</style>
