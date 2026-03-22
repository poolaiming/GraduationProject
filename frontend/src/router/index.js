import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import NewsList from '../views/NewsList.vue';
import NewsDetail from '../views/NewsDetail.vue';
import Forum from '../views/Forum.vue';
import Survey from '../views/Survey.vue';
import Announcement from '../views/Announcement.vue';
import Message from '../views/Message.vue';
import Profile from '../views/Profile.vue';
import About from '../views/About.vue';
import { useUserStore } from '../store/user';

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/home', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login, meta: { activeMenu: '/home' } },
  { path: '/register', name: 'Register', component: Register, meta: { activeMenu: '/home' } },
  { path: '/news', name: 'NewsList', component: NewsList },
  { path: '/news/:id', name: 'NewsDetail', component: NewsDetail, meta: { activeMenu: '/news' } },
  { path: '/forum', name: 'Forum', component: Forum },
  { path: '/survey', name: 'Survey', component: Survey, meta: { requiresAuth: true } },
  { path: '/announcement', name: 'Announcement', component: Announcement },
  { path: '/message', name: 'Message', component: Message, meta: { requiresAuth: true } },
  { path: '/profile', name: 'Profile', component: Profile, meta: { requiresAuth: true } },
  { path: '/about', name: 'About', component: About },
  { path: '/:pathMatch(.*)*', redirect: '/home' }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const userStore = useUserStore();
  const isAuthPage = to.path === '/login' || to.path === '/register';

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } });
    return;
  }

  if (isAuthPage && userStore.isLoggedIn) {
    next('/home');
    return;
  }

  next();
});

export default router;
