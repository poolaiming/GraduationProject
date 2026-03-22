<template>

  <div class="admin-page">

    <el-card>

      <el-tabs v-model="activeTab">

        <el-tab-pane label="关于我们" name="about">

          <el-form :model="aboutForm" label-width="80px">

            <el-form-item label="内容">

              <el-input v-model="aboutForm.content" type="textarea" :rows="8" placeholder="关于我们内容" />

            </el-form-item>

            <el-form-item>

              <el-button type="primary" @click="saveAbout">保存</el-button>

            </el-form-item>

          </el-form>

        </el-tab-pane>

        <el-tab-pane label="友情链接" name="friendLink">

          <el-button type="primary" style="margin-bottom: 16px" @click="openFriendLinkDialog()">新增</el-button>

          <el-table :data="friendLinkList">

            <el-table-column prop="name" label="名称" />

            <el-table-column prop="url" label="链接" />

            <el-table-column prop="sort" label="排序" width="80" />

            <el-table-column label="操作" width="150">

              <template #default="{ row }">

                <el-button type="primary" link @click="openFriendLinkDialog(row)">编辑</el-button>

                <el-button type="danger" link @click="deleteFriendLink(row)">删除</el-button>

              </template>

            </el-table-column>

          </el-table>

        </el-tab-pane>

      </el-tabs>

    </el-card>

    <el-dialog v-model="friendLinkVisible" :title="friendLinkForm.id ? '编辑链接' : '新增链接'" width="500">

      <el-form :model="friendLinkForm" label-width="80px">

        <el-form-item label="名称"><el-input v-model="friendLinkForm.name" /></el-form-item>

        <el-form-item label="URL"><el-input v-model="friendLinkForm.url" /></el-form-item>

        <el-form-item label="排序"><el-input-number v-model="friendLinkForm.sort" :min="0" /></el-form-item>

      </el-form>

      <template #footer>

        <el-button @click="friendLinkVisible = false">取消</el-button>

        <el-button type="primary" @click="saveFriendLink">确定</el-button>

      </template>

    </el-dialog>

  </div>

</template>

<script setup>

import { ref, reactive, onMounted } from 'vue';

import { ElMessage, ElMessageBox } from 'element-plus';

import { adminSystem } from '../api/admin';

const activeTab = ref('about');

const aboutForm = reactive({ content: '' });

const friendLinkList = ref([]);

const friendLinkForm = reactive({ id: null, name: '', url: '', sort: 0 });

// 新增：声明 friendLinkVisible 变量（原代码遗漏）
const friendLinkVisible = ref(false);

const loadAbout = async () => {

  const d = await adminSystem.getAbout();

  aboutForm.content = d?.content || '';

};

const loadFriendLink = async () => {

  friendLinkList.value = await adminSystem.listFriendLink();

};

const saveAbout = async () => {

  await adminSystem.saveAbout(aboutForm);

  ElMessage.success('保存成功');

};

const openFriendLinkDialog = (row) => {

  if (row) Object.assign(friendLinkForm, row);

  else Object.assign(friendLinkForm, { id: null, name: '', url: '', sort: 0 });

  friendLinkVisible.value = true;

};

const saveFriendLink = async () => {

  if (friendLinkForm.id) await adminSystem.updateFriendLink(friendLinkForm.id, friendLinkForm);

  else await adminSystem.createFriendLink(friendLinkForm);

  ElMessage.success('保存成功');

  friendLinkVisible.value = false;

  loadFriendLink();

};

const deleteFriendLink = async (row) => {

  // 修正：乱码文字改为 "确定删除吗？"
  await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' });

  await adminSystem.deleteFriendLink(row.id);

  ElMessage.success('删除成功');

  loadFriendLink();

};

onMounted(() => {

  loadAbout();

  loadFriendLink();

});

</script>
