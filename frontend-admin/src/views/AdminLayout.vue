<template>



  <el-container class="admin-layout">



    <el-aside width="220px" class="admin-aside">



      <div class="logo">后台管理</div>



      <el-menu



        :default-active="route.path"



        router



        class="side-menu"



        background-color="transparent"



        text-color="#dbeafe"



        active-text-color="#ffffff"



      >



        <el-menu-item index="/dashboard">



          <el-icon><DataAnalysis /></el-icon>



          <span>数据统计</span>



        </el-menu-item>



        <el-menu-item index="/user">



          <el-icon><User /></el-icon>



          <span>用户管理</span>



        </el-menu-item>



        <el-sub-menu index="news">



          <template #title>



            <el-icon><Document /></el-icon>



            <span>新闻管理</span>



          </template>



          <el-menu-item index="/news-category">新闻分类</el-menu-item>



          <el-menu-item index="/news">新闻列表</el-menu-item>



        </el-sub-menu>



        <el-menu-item index="/announcement">



          <el-icon><Bell /></el-icon>



          <span>公告资讯</span>



        </el-menu-item>



        <el-menu-item index="/forum">



          <el-icon><ChatDotRound /></el-icon>



          <span>论坛管理</span>



        </el-menu-item>



        <el-menu-item index="/survey">



          <el-icon><Tickets /></el-icon>



          <span>问卷调查</span>



        </el-menu-item>



        <el-menu-item index="/message">



          <el-icon><Message /></el-icon>



          <span>在线留言</span>



        </el-menu-item>



        <el-menu-item index="/system">



          <el-icon><Setting /></el-icon>



          <span>系统管理</span>



        </el-menu-item>



      </el-menu>



    </el-aside>



    <el-container>



      <el-header class="admin-header">



        <span class="header-title">{{ route.meta?.title || '管理后台' }}</span>



        <div class="header-right">



          <el-button class="header-link" link @click="goHome">返回前台</el-button>



          <el-button class="header-link" link @click="handleLogout">退出</el-button>



        </div>



      </el-header>



      <el-main class="admin-main">



        <router-view />



      </el-main>



    </el-container>



  </el-container>



</template>







<script setup>



import { useRoute, useRouter } from 'vue-router';



import { DataAnalysis, User, Document, Bell, ChatDotRound, Tickets, Message, Setting } from '@element-plus/icons-vue';



import { useAdminStore } from '../store/admin';







const route = useRoute();



const router = useRouter();

const adminStore = useAdminStore();







const goHome = () => {



  window.location.href = 'http://localhost:3000';



};







const handleLogout = () => {



  adminStore.logout();



  router.push('/login');



};



</script>







<style scoped>



.admin-layout {



  min-height: 100vh;



  background: linear-gradient(180deg, #f8fbff 0%, #f3f6fb 100%);



}







.admin-aside {



  background: linear-gradient(180deg, #1d4ed8 0%, #2563eb 55%, #3b82f6 100%);



  border-right: 1px solid rgba(255, 255, 255, 0.18);



}







.logo {



  height: 56px;



  line-height: 56px;



  text-align: center;



  font-weight: 700;



  color: #eff6ff;



  font-size: 16px;



  letter-spacing: 0.4px;



}







.side-menu {



  border-right: none;



}







:deep(.side-menu .el-menu-item),



:deep(.side-menu .el-sub-menu__title) {



  color: #dbeafe !important;



}







:deep(.side-menu .el-menu-item:hover),



:deep(.side-menu .el-sub-menu__title:hover) {



  background: rgba(255, 255, 255, 0.16) !important;



  color: #ffffff !important;



}







:deep(.side-menu .el-menu-item.is-active) {



  background: rgba(255, 255, 255, 0.22) !important;



  color: #ffffff !important;



  border-right: 3px solid #bfdbfe;



}







.admin-header {



  display: flex;



  align-items: center;



  justify-content: space-between;



  background: #ffffffcc;



  backdrop-filter: blur(8px);



  border-bottom: 1px solid #dbe7f5;



  padding: 0 24px;



}







.header-title {



  font-size: 16px;



  font-weight: 600;



  color: #0f172a;



}







.header-link {



  color: #2563eb;



}







.admin-main {



  padding: 24px;



}



</style>



