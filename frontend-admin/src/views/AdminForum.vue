<template>



  <div class="admin-page">



    <el-card>



      <template #header><span>论坛管理</span></template>



      <div class="toolbar">



        <el-input v-model="keyword" placeholder="标题" clearable style="width: 200px" @keyup.enter="loadData" />



        <el-button type="primary" @click="loadData">搜索</el-button>



      </div>



      <el-table :data="tableData" v-loading="loading">



        <el-table-column prop="id" label="ID" width="80" />



        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />



        <el-table-column prop="status" label="状态" width="100">



          <template #default="{ row }">



            <el-tag v-if="row.status === 1" type="warning">待审核</el-tag>



            <el-tag v-else-if="row.status === 2" type="success">已发布</el-tag>



            <el-tag v-else-if="row.status === 3" type="danger">驳回</el-tag>



            <el-tag v-else>未知</el-tag>



          </template>



        </el-table-column>



        <el-table-column prop="reviewRemark" label="审核意见" min-width="180" show-overflow-tooltip />



        <el-table-column prop="viewCount" label="浏览" width="80" />



        <el-table-column prop="likeCount" label="点赞" width="80" />



        <el-table-column prop="commentCount" label="评论" width="80" />



        <el-table-column prop="isTop" label="置顶" width="80">



          <template #default="{ row }">{{ row.isTop ? '是' : '否' }}</template>



        </el-table-column>



        <el-table-column prop="createTime" label="发布时间" width="180" />



        <el-table-column label="操作" width="360" fixed="right">



          <template #default="{ row }">



            <el-button v-if="row.status === 1 || row.status === 3" type="success" link @click="reviewPost(row, 2)">审核通过</el-button>



            <el-button v-if="row.status === 1 || row.status === 2" type="warning" link @click="reviewPost(row, 3)">驳回</el-button>



            <el-button type="primary" link @click="setTop(row, row.isTop ? 0 : 1)">{{ row.isTop ? '取消置顶' : '置顶' }}</el-button>



            <el-button type="info" link @click="openComments(row)">评论管理</el-button>



            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>



          </template>



        </el-table-column>



      </el-table>



      <el-pagination class="pager" background layout="prev, pager, next" :current-page="pageNum" :page-size="pageSize" :total="total" @current-change="p => { pageNum = p; loadData(); }" />



    </el-card>







    <el-dialog v-model="commentVisible" title="论坛评论管理" width="820px" @closed="loadingComments = false">



      <el-table :data="commentData" v-loading="loadingComments" size="small">



        <el-table-column prop="id" label="评论ID" width="90" />



        <el-table-column prop="userId" label="用户ID" width="90" />



        <el-table-column prop="content" label="评论内容" min-width="280" show-overflow-tooltip />



        <el-table-column prop="createTime" label="评论时间" width="180" />



        <el-table-column label="操作" width="90" fixed="right">



          <template #default="{ row }">



            <el-button type="danger" link @click="deleteComment(row)">删除</el-button>



          </template>



        </el-table-column>



      </el-table>



    </el-dialog>



  </div>



</template>







<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { adminForum } from '../api/admin';

const loading = ref(false);
const tableData = ref([]);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const commentVisible = ref(false);
const loadingComments = ref(false);
const commentData = ref([]);
const currentPostId = ref(null);

const loadData = async () => {
  loading.value = true;
  try {
    const res = await adminForum.page({ keyword: keyword.value || undefined, pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.records || [];
    total.value = res.total || 0;
  } finally {
    loading.value = false;
  }
};

const reviewPost = async (row, status) => {
  let reviewRemark = '';
  try {
    if (status === 3) {
      const { value } = await ElMessageBox.prompt('请输入驳回原因', '审核驳回', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        inputPlaceholder: '如：内容不规范，请修改后重发'
      });
      reviewRemark = value || '';
    } else {
      reviewRemark = '审核通过';
    }

    await adminForum.updateStatus(row.id, status, reviewRemark);
    ElMessage.success('审核操作成功');
    loadData();
  } catch (e) {
    if (e === 'cancel' || e === 'close') return;
    void e;
  }
};

const setTop = async (row, isTop) => {
  try {
    await adminForum.setTop(row.id, isTop);
    ElMessage.success('操作成功');
    loadData();
  } catch (error) {
    void error;
  }
};

const openComments = async (row) => {
  currentPostId.value = row.id;
  commentVisible.value = true;
  loadingComments.value = true;
  try {
    const res = await adminForum.comments(row.id, {});
    commentData.value = res.records || [];
  } catch (e) {
    void e;
    commentData.value = [];
  } finally {
    loadingComments.value = false;
  }
};

const deleteComment = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该评论？', '提示', { type: 'warning' });
    await adminForum.deleteComment(row.id);
    ElMessage.success('删除成功');
    await openComments({ id: currentPostId.value });
  } catch (e) {
    if (e === 'cancel' || e === 'close') return;
    void e;
  }
};

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该帖子？', '提示', { type: 'warning' });
    await adminForum.delete(row.id);
    ElMessage.success('删除成功');
    loadData();
  } catch (e) {
    if (e === 'cancel' || e === 'close') return;
    void e;
  }
};

onMounted(loadData);
</script>







<style scoped>



.toolbar { margin-bottom: 16px; display: flex; gap: 12px; }



.pager { margin-top: 16px; }



</style>



