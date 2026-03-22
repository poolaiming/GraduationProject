<template>
  <div class="admin-page">
    <el-card>
      <template #header>
        <span>新闻分类</span>
        <el-button type="primary" style="float: right" @click="openDialog()">新增</el-button>
      </template>
      <el-table :data="list" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">{{ row.status === 1 ? '启用' : '禁用' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="500">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="分类名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="分类描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { adminNewsCategory } from '../api/admin';

const loading = ref(false);
const list = ref([]);
const dialogVisible = ref(false);
const form = reactive({ id: null, name: '', description: '', sort: 0, status: 1 });

const loadData = async () => {
  loading.value = true;
  try {
    list.value = await adminNewsCategory.list();
  } finally {
    loading.value = false;
  }
};

const openDialog = (row) => {
  if (row) {
    Object.assign(form, {
      id: row.id,
      name: row.name,
      description: row.description || '',
      sort: row.sort || 0,
      status: row.status ?? 1
    });
  } else {
    Object.assign(form, { id: null, name: '', description: '', sort: 0, status: 1 });
  }
  dialogVisible.value = true;
};

const submit = async () => {
  if (!form.name) {
    ElMessage.warning('请输入名称');
    return;
  }
  try {
    if (form.id) {
      await adminNewsCategory.update(form.id, form);
    } else {
      await adminNewsCategory.create(form);
    }
    ElMessage.success('保存成功');
    dialogVisible.value = false;
    loadData();
  } catch (error) {
    void error;
  }
};

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除吗', '提示', { type: 'warning' });
    await adminNewsCategory.delete(row.id);
    ElMessage.success('删除成功');
    loadData();
  } catch (e) {
    if (e === 'cancel' || e === 'close') return;
    void e;
  }
};

onMounted(loadData);
</script>
