<template>
  <div class="admin-page">
    <el-card>
      <template #header><span>在线留言</span></template>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="留言内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">未处理</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已回复</el-tag>
            <el-tag v-else type="info">已关闭</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="replyContent" label="回复内容" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" label="留言时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="!row.replyContent" type="primary" link @click="openReply(row)">回复</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          class="pager"
          background
          layout="prev, pager, next"
          :current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          @current-change="p => { pageNum = p; loadData(); }"
      />
    </el-card>

    <el-dialog v-model="replyVisible" title="回复留言" width="500">
      <el-input
          v-model="replyContent"
          type="textarea"
          :rows="4"
          placeholder="请输入回复内容"
      />
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { adminMessage } from '../api/admin';

const loading = ref(false);
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const replyVisible = ref(false);
const replyContent = ref('');
let currentId = null;

const loadData = async () => {
  loading.value = true;
  try {
    const res = await adminMessage.page({ pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.records || [];
    total.value = res.total || 0;
  } finally {
    loading.value = false;
  }
};

const openReply = (row) => {
  currentId = row.id;
  replyContent.value = '';
  replyVisible.value = true;
};

const submitReply = async () => {
  if (!replyContent.value) {
    ElMessage.warning('请输入回复内容');
    return;
  }
  try {
    await adminMessage.reply(currentId, { replyContent: replyContent.value });
    ElMessage.success('回复成功');
    replyVisible.value = false;
    loadData();
  } catch (error) {
    void error;
  }
};

onMounted(loadData);
</script>

<style scoped>
.pager { margin-top: 16px; }
</style>
