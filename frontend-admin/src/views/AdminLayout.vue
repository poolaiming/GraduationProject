<template>
  <div class="bg-background text-on-background flex h-screen overflow-hidden font-body">
    <!-- SideNavBar -->
    <aside class="w-64 bg-primary text-on-primary flex flex-col shrink-0">
      <div class="p-6">
        <div class="flex items-center gap-3 mb-1">
          <div class="w-8 h-8 rounded-lg bg-primary-container flex items-center justify-center">
            <span class="material-symbols-outlined text-on-primary-container" style="font-variation-settings: 'FILL' 1;">newspaper</span>
          </div>
          <h1 class="text-xl font-extrabold tracking-tight font-headline">极客新闻后台</h1>
        </div>
        <p class="text-xs text-on-primary-container font-medium opacity-80">Platform Admin</p>
      </div>

      <nav class="flex-1 px-4 space-y-2 mt-4 custom-scrollbar overflow-y-auto">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          :class="[
            'flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
            route.path === item.path || (item.path !== '/dashboard' && route.path.startsWith(item.path))
              ? 'bg-primary-container text-white'
              : 'text-on-primary-container hover:bg-white/5'
          ]"
        >
          <span
            class="material-symbols-outlined"
            :style="{ fontVariationSettings: (route.path === item.path || (item.path !== '/dashboard' && route.path.startsWith(item.path))) ? `'FILL' 1` : `'FILL' 0` }"
          >{{ item.icon }}</span>
          <span class="font-medium">{{ item.name }}</span>
        </router-link>
      </nav>

      <div class="p-6 border-t border-white/10 flex items-center gap-3">
        <div class="w-10 h-10 rounded-full border-2 border-primary-container bg-surface-container flex items-center justify-center overflow-hidden">
          <span class="material-symbols-outlined text-primary">person</span>
        </div>
        <div>
          <p class="text-sm font-bold">{{ adminStore.adminInfo?.username || 'Admin User' }}</p>
          <p class="text-[10px] text-on-primary-container uppercase tracking-wider">Super Admin</p>
        </div>
      </div>
    </aside>

    <main class="flex-1 flex flex-col overflow-hidden">
      <!-- TopNavBar -->
      <header class="h-16 bg-surface-container-lowest flex items-center justify-between px-8 border-b border-outline-variant/10 shrink-0">
        <div class="flex items-center flex-1">
          <div class="relative w-96">
            <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant text-sm">search</span>
            <input class="w-full pl-10 pr-4 py-2 bg-surface-container-low border-none rounded-full text-sm focus:ring-2 focus:ring-primary/20 outline-none transition-shadow" placeholder="搜索内容..." type="text"/>
          </div>
        </div>
        <div class="flex items-center gap-6">
          <h2 class="text-sm font-bold text-primary mr-4 font-headline">{{ route.meta?.title || '管理后台' }}</h2>
          <div class="flex items-center gap-4 border-l border-outline-variant/20 pl-6">
            <button class="relative hover:text-primary transition-colors cursor-pointer group" @click="goHome" title="返回前台">
              <span class="material-symbols-outlined text-on-surface-variant group-hover:text-primary">home</span>
            </button>
            <button class="relative hover:text-error transition-colors cursor-pointer group" @click="handleLogout" title="退出登录">
              <span class="material-symbols-outlined text-on-surface-variant group-hover:text-error">logout</span>
            </button>
          </div>
        </div>
      </header>

      <!-- Main Content -->
      <div class="flex-1 flex flex-col overflow-y-auto p-6 space-y-6 custom-scrollbar bg-background">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router';
import { useAdminStore } from '../store/admin';

const route = useRoute();
const router = useRouter();
const adminStore = useAdminStore();

const menuItems = [
  { path: '/dashboard', name: '数据统计', icon: 'monitoring' },
  { path: '/user', name: '用户管理', icon: 'group' },
  { path: '/news-category', name: '新闻分类', icon: 'category' },
  { path: '/news', name: '新闻列表', icon: 'article' },
  { path: '/announcement', name: '公告资讯', icon: 'campaign' },
  { path: '/forum', name: '论坛管理', icon: 'forum' },
  { path: '/survey', name: '问卷调查', icon: 'ballot' },
  { path: '/message', name: '在线留言', icon: 'chat' },
  { path: '/system', name: '系统管理', icon: 'settings' },
];

const goHome = () => {
  window.location.href = 'http://localhost:3000';
};

const handleLogout = () => {
  adminStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #c4c6d2; border-radius: 10px; }

.material-symbols-outlined {
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  vertical-align: middle;
}
</style>
