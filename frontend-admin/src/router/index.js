import { createRouter, createWebHistory } from 'vue-router';
import AdminLayout from '../views/AdminLayout.vue';
import AdminLogin from '../views/AdminLogin.vue';
import AdminDashboard from '../views/AdminDashboard.vue';
import AdminUser from '../views/AdminUser.vue';
import AdminNewsCategory from '../views/AdminNewsCategory.vue';
import AdminNews from '../views/AdminNews.vue';
import AdminForum from '../views/AdminForum.vue';
import AdminSurvey from '../views/AdminSurvey.vue';
import AdminMessage from '../views/AdminMessage.vue';
import AdminSystem from '../views/AdminSystem.vue';
import AdminAnnouncement from '../views/AdminAnnouncement.vue';
import { useAdminStore } from '../store/admin';

const routes = [
  { path: '/login', name: 'AdminLogin', component: AdminLogin },
  {
    path: '/',
    component: AdminLayout,
    meta: { requiresAdmin: true },
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', name: 'Dashboard', component: AdminDashboard, meta: { title: '数据统计' } },
      { path: 'user', name: 'User', component: AdminUser, meta: { title: '用户管理' } },
      { path: 'news-category', name: 'NewsCategory', component: AdminNewsCategory, meta: { title: '新闻分类' } },
      { path: 'news', name: 'News', component: AdminNews, meta: { title: '新闻管理' } },
      { path: 'announcement', name: 'AnnouncementAdmin', component: AdminAnnouncement, meta: { title: '公告资讯' } },
      { path: 'forum', name: 'Forum', component: AdminForum, meta: { title: '论坛管理' } },
      { path: 'survey', name: 'Survey', component: AdminSurvey, meta: { title: '问卷调查' } },
      { path: 'message', name: 'Message', component: AdminMessage, meta: { title: '在线留言' } },
      { path: 'system', name: 'System', component: AdminSystem, meta: { title: '系统管理' } }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const adminStore = useAdminStore();
  const requiresAdmin = to.matched.some((record) => record.meta.requiresAdmin);

  if (to.path === '/login' && adminStore.isLoggedIn) {
    next('/dashboard');
    return;
  }

  if (requiresAdmin && !adminStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } });
    return;
  }

  next();
});

export default router;
