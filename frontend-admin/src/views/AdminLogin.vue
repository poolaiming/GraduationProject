<template>



  <div class="admin-login-page">



    <div class="login-box">



      <h1 class="title">后台管理系统</h1>



      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">



        <el-form-item label="账号" prop="username">



          <el-input v-model="form.username" placeholder="请输入管理员账号" />



        </el-form-item>



        <el-form-item label="密码" prop="password">



          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />



        </el-form-item>



        <el-form-item>



          <el-button type="primary" :loading="loading" @click="onSubmit" style="width: 100%">登录</el-button>



        </el-form-item>



      </el-form>



      <p class="tip">默认账号：admin / admin123</p>



    </div>



  </div>



</template>







<script setup>
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';
import { adminLogin } from '../api/admin';
import { useAdminStore } from '../store/admin';

const route = useRoute();
const router = useRouter();
const adminStore = useAdminStore();
const formRef = ref();
const loading = ref(false);
const form = reactive({ username: '', password: '' });
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

const onSubmit = async () => {
  try {
    await formRef.value.validate();
  } catch {
    return;
  }

  loading.value = true;
  try {
    const admin = await adminLogin(form);
    adminStore.setAdmin(admin);
    ElMessage.success('登录成功');
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard';
    router.replace(redirect);
  } catch (error) {
    void error;
    // 错误提示已由 http.js 全局拦截器统一处理，此处无需重复弹窗
  } finally {
    loading.value = false;
  }
};
</script>







<style scoped>



.admin-login-page {



  min-height: 100vh;



  display: flex;



  align-items: center;



  justify-content: center;



  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);



}



.login-box {



  width: 400px;



  padding: 40px;



  background: #fff;



  border-radius: 8px;



  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);



}



.title {



  margin: 0 0 24px;



  font-size: 22px;



  font-weight: 600;



  text-align: center;



  color: #1e293b;



}



.tip {



  margin: 16px 0 0;



  font-size: 12px;



  color: #94a3b8;



  text-align: center;



}



</style>



