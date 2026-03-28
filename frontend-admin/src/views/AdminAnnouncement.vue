<template>
  <div class="admin-page">
    <el-card>
      <template #header><span>公告资讯管理</span></template>
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="按标题搜索" clearable style="width: 240px" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button type="success" @click="openDialog()">新增公告</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" size="small">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="isTop" label="置顶" width="90">
          <template #default="{ row }">
            <el-tag :type="row.isTop === 1 ? 'danger' : 'info'">{{ row.isTop === 1 ? '置顶' : '普通' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '发布' : '下线' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />

        <el-table-column label="操作" width="280" fixed="right">

          <template #default="{ row }">



            <el-button type="primary" link @click="toggleTop(row)">{{ row.isTop === 1 ? '取消置顶' : '置顶' }}</el-button>



            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>



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







    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑公告' : '新增公告'" width="640px">



      <el-form :model="form" label-width="84px">



        <el-form-item label="标题">



          <el-input v-model="form.title" maxlength="100" show-word-limit />



        </el-form-item>



        <el-form-item label="内容">



          <el-input v-model="form.content" type="textarea" :rows="5" maxlength="1000" show-word-limit />



        </el-form-item>



        <el-form-item label="状态">



          <el-radio-group v-model="form.status">



            <el-radio :label="1">发布</el-radio>



            <el-radio :label="0">下线</el-radio>



          </el-radio-group>



        </el-form-item>



        <el-form-item label="置顶">



          <el-switch v-model="topSwitch" />



        </el-form-item>



      </el-form>



      <template #footer>



        <el-button @click="dialogVisible = false">取消</el-button>



        <el-button type="primary" @click="save">保存</el-button>



      </template>



    </el-dialog>



  </div>



</template>







<script setup>



import { computed, onMounted, reactive, ref } from 'vue';



import { ElMessage, ElMessageBox } from 'element-plus';



import { adminAnnouncement } from '../api/admin';







const loading = ref(false);



const tableData = ref([]);



const keyword = ref('');



const pageNum = ref(1);



const pageSize = ref(10);



const total = ref(0);







const dialogVisible = ref(false);



const form = reactive({ id: null, title: '', content: '', status: 1, isTop: 0, publishTime: '' });



const topSwitch = computed({



  get: () => form.isTop === 1,



  set: (v) => { form.isTop = v ? 1 : 0; }



});







const loadData = async () => {



  loading.value = true;



  try {



    const page = await adminAnnouncement.page({ keyword: keyword.value || undefined, pageNum: pageNum.value, pageSize: pageSize.value });



    tableData.value = page.records || [];



    total.value = page.total || 0;



  } finally {



    loading.value = false;



  }



};







const openDialog = (row) => {



  if (row) {



    Object.assign(form, row);



  } else {



    Object.assign(form, { id: null, title: '', content: '', status: 1, isTop: 0, publishTime: '' });



  }



  dialogVisible.value = true;



};







const save = async () => {



  if (!String(form.title || '').trim()) return ElMessage.warning("请输入标题");



  if (!String(form.content || '').trim()) return ElMessage.warning("请输入内容");



  if (form.id) await adminAnnouncement.update(form.id, form);



  else await adminAnnouncement.create(form);



  ElMessage.success('保存成功');



  dialogVisible.value = false;



  loadData();



};







const toggleTop = async (row) => {



  await adminAnnouncement.setTop(row.id, row.isTop === 1 ? 0 : 1);



  ElMessage.success('操作成功');



  loadData();



};







const handleDelete = async (row) => {



  await ElMessageBox.confirm('确定删除该公告？', '提示', { type: 'warning' });



  await adminAnnouncement.delete(row.id);



  ElMessage.success('删除成功');



  loadData();



};







const onPageChange = (p) => {



  pageNum.value = p;



  loadData();



};







onMounted(loadData);



</script>







<style scoped>



.toolbar { margin-bottom: 14px; display: flex; gap: 10px; flex-wrap: wrap; }



.pager { margin-top: 16px; }



</style>



