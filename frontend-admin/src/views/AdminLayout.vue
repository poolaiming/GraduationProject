<template>
  <div class="bg-background text-on-background flex h-screen overflow-hidden font-body">
    <!-- SideNavBar -->
    <aside class="w-64 bg-primary text-on-primary flex flex-col shrink-0 border-r border-outline-variant/20">
      <div class="p-6">
        <div class="flex items-center gap-3 mb-1">
          <div class="w-8 h-8 rounded-lg bg-primary-container flex items-center justify-center">
            <span class="material-symbols-outlined text-on-primary" style="font-variation-settings: 'FILL' 1;">newspaper</span>
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
            'sidebar-nav-link flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
            isMenuItemActive(item.path)
              ? 'sidebar-nav-link-active shadow-sm'
              : 'text-on-primary-container hover:bg-surface-container-low'
          ]"
        >
          <span
            class="material-symbols-outlined"
            :style="{ fontVariationSettings: isMenuItemActive(item.path) ? `'FILL' 1` : `'FILL' 0` }"
          >{{ item.icon }}</span>
          <span class="font-medium">{{ item.name }}</span>
        </router-link>
      </nav>

      <div class="px-4 pb-4">
        <button
          class="theme-toggle-button w-full rounded-2xl border border-outline-variant/20 bg-surface-container-low px-4 py-3 text-left transition-colors hover:bg-surface-container"
          @click="handleThemeToggle"
          :title="isDarkTheme ? '切换到白天模式' : '切换到黑夜模式'"
        >
          <div class="flex items-center gap-3">
            <span class="material-symbols-outlined text-on-primary-container">
              {{ isDarkTheme ? 'light_mode' : 'dark_mode' }}
            </span>
            <div class="min-w-0">
              <p class="text-sm font-semibold text-on-primary">
                {{ isDarkTheme ? '白天模式' : '黑夜模式' }}
              </p>
              <p class="text-xs text-on-primary-container opacity-80">
                {{ isDarkTheme ? '切回明亮界面' : '切回深色界面' }}
              </p>
            </div>
          </div>
        </button>
      </div>

      <div class="p-6 border-t border-outline-variant/20 flex items-center gap-3">
        <div class="w-10 h-10 rounded-full border-2 border-primary-container bg-surface-container flex items-center justify-center overflow-hidden">
          <span class="material-symbols-outlined text-on-primary">person</span>
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
          <h2 class="text-sm font-bold text-on-primary-container mr-4 font-headline">{{ route.meta?.title || '管理后台' }}</h2>
          <div class="flex items-center gap-4 border-l border-outline-variant/20 pl-6">
            <button class="relative hover:text-on-primary-container transition-colors cursor-pointer group" @click="goHome" title="返回前台">
              <span class="material-symbols-outlined text-on-surface-variant group-hover:text-on-primary-container">home</span>
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
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAdminStore } from '../store/admin';
import { THEME_CHANGE_EVENT, getStoredTheme, toggleTheme } from '../utils/theme';

const route = useRoute();
const router = useRouter();
const adminStore = useAdminStore();
const currentTheme = ref(getStoredTheme());

const getTopLevelPath = (path) => {
  const [firstSegment] = path.split('/').filter(Boolean);
  return firstSegment ? `/${firstSegment}` : '/dashboard';
};

const activeMenuPath = computed(() => getTopLevelPath(route.path));

const isMenuItemActive = (menuPath) => {
  if (menuPath === '/dashboard') {
    return route.path === '/dashboard';
  }

  return activeMenuPath.value === menuPath;
};

const isDarkTheme = computed(() => currentTheme.value === 'dark');

const syncThemeState = (event) => {
  currentTheme.value = event?.detail || document.documentElement.dataset.theme || getStoredTheme();
};

const handleThemeToggle = () => {
  currentTheme.value = toggleTheme();
};

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

onMounted(() => {
  window.addEventListener(THEME_CHANGE_EVENT, syncThemeState);
});

onBeforeUnmount(() => {
  window.removeEventListener(THEME_CHANGE_EVENT, syncThemeState);
});
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: var(--app-scrollbar-thumb); border-radius: 10px; }

.material-symbols-outlined {
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  vertical-align: middle;
}

.theme-toggle-button {
  box-shadow: inset 0 1px 0 rgb(255 255 255 / 0.04);
}

.sidebar-nav-link-active {
  color: var(--app-sidebar-active-text);
  background:
    linear-gradient(135deg, var(--app-sidebar-active-start) 0%, var(--app-sidebar-active-end) 100%);
  border: 1px solid var(--app-sidebar-active-border);
  box-shadow: var(--app-sidebar-active-shadow);
}
</style>
