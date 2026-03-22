<template>
  <div class="admin-page">
    <el-card>
      <template #header><span>用户管理</span></template>

      <div class="toolbar">
        <el-input v-model="searchId" placeholder="用户ID" clearable style="width: 120px" @keyup.enter="loadData" />
        <el-input v-model="searchUsername" placeholder="用户名" clearable style="width: 160px" @keyup.enter="loadData" />
        <el-input v-model="keyword" placeholder="昵称" clearable style="width: 160px" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">搜索</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />

        <el-table-column prop="isJournalist" label="新闻工作者" width="140">
          <template #default="{ row }">
            <el-tag v-if="row.isJournalist === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="row.isJournalist === 2" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.isJournalist === 3" type="danger">已拒绝</el-tag>
            <el-tag v-else>未申请</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="账号状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else-if="row.status === 2" type="warning">待注销</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="注册时间" width="180" />

        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="danger" link @click="toggleStatus(row, 0)">禁用</el-button>
            <el-button v-else-if="row.status === 0" type="success" link @click="toggleStatus(row, 1)">启用</el-button>

            <el-button v-if="row.isJournalist === 2" type="success" link @click="reviewJournalist(row, 1)">通过记者申请</el-button>
            <el-button v-if="row.isJournalist === 2" type="warning" link @click="reviewJournalist(row, 3)">拒绝记者申请</el-button>

            <el-button v-if="row.status === 2" type="warning" link @click="handleApproveCancellation(row)">通过注销</el-button>
            <el-button v-if="row.status === 2" type="info" link @click="handleRejectCancellation(row)">驳回注销</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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
          @current-change="onPageChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { adminUser } from '../api/admin';

const loading = ref(false);
const tableData = ref([]);
const keyword = ref('');
const searchId = ref('');
const searchUsername = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const loadData = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    };
    if (searchId.value) params.id = searchId.value;
    if (searchUsername.value) params.username = searchUsername.value;
    if (keyword.value) params.keyword = keyword.value;

    const res = await adminUser.page(params);
    tableData.value = res.records || [];
    total.value = res.total || 0;
  } finally {
    loading.value = false;
  }
};

const toggleStatus = async (row, status) => {
  try {
    await adminUser.updateStatus(row.id, status);
    ElMessage.success('操作成功');
    loadData();
  } catch (error) {
    void error;
  }
};

const reviewJournalist = async (row, journalistStatus) => {
  const actionText = journalistStatus === 1 ? '通过' : '拒绝';
  try {
    await ElMessageBox.confirm(`确认${actionText}该用户的新闻工作者申请？`, '审核确认', { type: 'warning' });
    await adminUser.updateJournalistStatus(row.id, journalistStatus);
    ElMessage.success('审核成功');
    loadData();
  } catch (e) {
    if (e === 'cancel' || e === 'close') return;
    void e;
  }
};

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该用户？删除后该用户所有数据将被清理，且不可恢复', '提示', { type: 'warning' });
    await adminUser.delete(row.id);
    ElMessage.success('删除成功');
    loadData();
  } catch (e) {
    if (e === 'cancel' || e === 'close') return;
    void e;
  }
};

const handleApproveCancellation = async (row) => {
  try {
    await ElMessageBox.confirm('确认通过该用户的注销申请？通过后将删除该用户及全部数据', '注销确认', { type: 'warning' });
    await adminUser.delete(row.id);
    ElMessage.success('已通过注销并删除用户');
    loadData();
  } catch (e) {
    if (e === 'cancel' || e === 'close') return;
    void e;
  }
};

const handleRejectCancellation = async (row) => {
  try {
    await ElMessageBox.confirm('确认驳回该用户的注销申请？驳回后用户状态将恢复正常', '驳回确认', { type: 'warning' });
    await adminUser.updateStatus(row.id, 1);
    ElMessage.success('已驳回注销申请');
    loadData();
  } catch (e) {
    if (e === 'cancel' || e === 'close') return;
    void e;
  }
};

const onPageChange = (p) => {
  pageNum.value = p;
  loadData();
};

onMounted(loadData);
</script>

<style scoped>
.toolbar { margin-bottom: 16px; display: flex; gap: 12px; }
.pager { margin-top: 16px; }
</style>
