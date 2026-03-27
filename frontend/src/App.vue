<template>
  <div class="min-h-screen bg-background flex flex-col font-body text-on-background relative overflow-hidden text-base">
    <header 
      :class="[
        'site-header sticky top-0 z-50 transition-all duration-300 backdrop-blur-md',
        scrolled ? 'site-header-scrolled py-2' : 'py-4'
      ]"
    >
      <div class="max-w-7xl mx-auto px-6 lg:px-8 flex items-center justify-between">
        <div class="flex items-center gap-3 relative z-20 cursor-pointer" @click="router.push('/home')">
          <div class="w-10 h-10 rounded-xl bg-primary-container flex items-center justify-center shadow-inner border site-soft-border">
            <span class="material-symbols-outlined text-on-primary" style="font-variation-settings: 'FILL' 1;">public</span>
          </div>
          <h1 class="text-xl font-extrabold tracking-tight font-headline text-[color:var(--app-header-text)] hidden sm:block">新闻资讯互动平台</h1>
        </div>
        
        <nav class="hidden md:flex flex-1 mx-8 justify-center relative z-20">
          <ul class="flex items-center gap-2">
            <li v-for="menu in menuItems" :key="menu.path">
              <router-link 
                v-if="!menu.requiresAuth || isLoggedIn"
                :to="menu.path"
                class="px-5 py-2.5 rounded-full text-sm font-semibold transition-all duration-300"
                :class="activeMenu === menu.path ? 'site-nav-link-active' : 'site-nav-link-idle'"
              >
                {{ menu.name }}
              </router-link>
            </li>
          </ul>
        </nav>

        <div class="flex items-center gap-4 relative z-20">
          <button
            class="theme-switch-button"
            @click="handleThemeToggle"
            :title="isDarkTheme ? '切换到白天模式' : '切换到黑夜模式'"
          >
            <span class="material-symbols-outlined text-lg">{{ isDarkTheme ? 'light_mode' : 'dark_mode' }}</span>
          </button>
          <template v-if="isLoggedIn">
            <div 
              class="site-user-pill flex items-center gap-3 cursor-pointer p-1.5 pr-4 rounded-full transition-colors"
              @click="router.push('/profile')"
            >
              <img v-if="currentUser?.avatar" :src="currentUser.avatar" class="w-8 h-8 rounded-full border site-strong-border object-cover" />
              <div v-else class="w-8 h-8 rounded-full site-avatar-fallback flex items-center justify-center text-xs font-bold uppercase">{{ avatarText }}</div>
              <span class="text-sm font-bold text-[color:var(--app-header-text)] max-w-[100px] truncate hidden sm:block">{{ displayName }}</span>
            </div>
            <button @click="handleLogout" class="w-10 h-10 rounded-full flex items-center justify-center hover:bg-error/20 transition-colors cursor-pointer text-error/80 hover:text-error" title="退出">
               <span class="material-symbols-outlined text-lg">logout</span>
            </button>
          </template>
          <template v-else>
            <router-link to="/login" class="text-sm font-bold text-[color:var(--app-header-subtle)] hover:text-[color:var(--app-header-text)] transition-colors px-3 py-2">登录</router-link>
            <router-link to="/register" class="text-sm font-bold bg-secondary text-on-secondary px-5 py-2.5 rounded-full hover:bg-secondary-fixed-dim transition-colors shadow-lg shadow-secondary/20">注册</router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- Mobile Nav -->
    <nav class="site-mobile-nav md:hidden sticky top-[72px] z-40 overflow-x-auto custom-scrollbar shadow-md">
      <ul class="flex items-center px-4 py-2 gap-2 min-w-max">
        <li v-for="menu in menuItems" :key="`mobile-${menu.path}`">
          <router-link 
            v-if="!menu.requiresAuth || isLoggedIn"
            :to="menu.path"
            class="px-4 py-2 rounded-xl text-xs font-semibold transition-colors block"
            :class="activeMenu === menu.path ? 'site-mobile-link-active' : 'site-mobile-link-idle'"
          >
            {{ menu.name }}
          </router-link>
        </li>
      </ul>
    </nav>

    <main class="flex-1 w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 md:py-12 relative z-10 flex flex-col">
      <router-view v-slot="{ Component, route }">
        <transition name="page-fade-slide" mode="out-in" appear>
          <div :key="route.fullPath" class="page-shell flex-1 flex flex-col">
            <component :is="Component" />
          </div>
        </transition>
      </router-view>
    </main>

    <footer class="site-footer py-16 relative overflow-hidden mt-auto shrink-0">
      <div class="absolute inset-0 z-0 site-footer-surface"></div>
      <div class="absolute top-0 right-1/4 w-96 h-96 bg-primary-container blur-3xl rounded-full opacity-50 mix-blend-screen pointer-events-none"></div>
      <div class="absolute bottom-0 left-10 w-64 h-64 bg-secondary/10 blur-3xl rounded-full opacity-50 mix-blend-screen pointer-events-none"></div>

      <div class="max-w-7xl mx-auto px-6 lg:px-8 relative z-10">
        <div class="grid grid-cols-1 md:grid-cols-12 gap-12 lg:gap-8">
          <div class="md:col-span-5 flex flex-col items-start">
            <div class="flex items-center gap-3 mb-6">
              <div class="w-10 h-10 rounded-xl bg-primary-container flex items-center justify-center border site-soft-border">
                <span class="material-symbols-outlined text-on-primary">public</span>
              </div>
              <h2 class="text-xl font-extrabold font-headline text-[color:var(--app-header-text)]">新闻资讯互动平台</h2>
            </div>
            <h3 class="text-sm font-bold text-secondary-fixed mb-4 tracking-widest uppercase">洞察 · 及时 · 互动</h3>
            <p class="text-sm text-[color:var(--app-header-subtle)] leading-relaxed max-w-sm mb-8">
              致力于打造更及时、更有趣、更具互动性的新闻资讯平台，让每位读者获取高质量信息，发现更有价值的声音。
            </p>
          </div>

          <div class="md:col-span-3 md:col-start-7">
            <h4 class="text-sm font-bold text-[color:var(--app-header-text)] uppercase tracking-wider mb-6 pb-2 border-b site-soft-border inline-block">友情链接</h4>
            <ul v-if="friendLinks.length" class="space-y-4">
              <li v-for="link in friendLinks" :key="link.id">
                <a :href="normalizeLink(link.url)" target="_blank" rel="noopener noreferrer" class="text-sm text-[color:var(--app-header-subtle)] hover:text-[color:var(--app-header-text)] transition-colors flex items-center gap-2 group">
                  <span class="w-1.5 h-1.5 rounded-full bg-secondary-fixed group-hover:scale-150 transition-transform"></span>
                  {{ link.name }}
                </a>
              </li>
            </ul>
            <p v-else class="text-sm text-[color:var(--app-header-subtle)] opacity-70">暂无友情链接</p>
          </div>

          <div class="md:col-span-3">
            <h4 class="text-sm font-bold text-[color:var(--app-header-text)] uppercase tracking-wider mb-6 pb-2 border-b site-soft-border inline-block">关于我们</h4>
            <p class="text-sm text-[color:var(--app-header-subtle)] leading-relaxed mb-6">{{ aboutText || '快速了解最新时事，发现生活点滴。' }}</p>
            <router-link to="/about" class="inline-flex items-center gap-2 text-sm font-bold text-secondary-fixed hover:text-[color:var(--app-header-text)] transition-colors group">
              了解更多 
              <span class="material-symbols-outlined text-sm group-hover:translate-x-1 transition-transform">arrow_forward</span>
            </router-link>
          </div>
        </div>

        <div class="mt-16 pt-8 border-t site-soft-border flex flex-col md:flex-row justify-between items-center gap-4">
          <p class="text-xs text-[color:var(--app-header-subtle)] opacity-75">© 2026 新闻资讯互动平台. All rights reserved.</p>
          <div class="flex gap-4">
             <a href="#" class="site-footer-icon"><span class="material-symbols-outlined text-sm text-[color:var(--app-header-subtle)]">share</span></a>
             <a href="#" class="site-footer-icon"><span class="material-symbols-outlined text-sm text-[color:var(--app-header-subtle)]">mail</span></a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed, onMounted, onBeforeUnmount, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from './store/user';
import { getAbout, getFriendLinkList } from './api/system';
import { THEME_CHANGE_EVENT, getStoredTheme, toggleTheme } from './utils/theme';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const scrolled = ref(false);
const friendLinks = ref([]);
const aboutText = ref('');
const currentTheme = ref(getStoredTheme());

const isLoggedIn = computed(() => userStore.isLoggedIn);
const currentUser = computed(() => userStore.user);
const displayName = computed(() => userStore.displayName);
const avatarText = computed(() => displayName.value?.[0] || '?');
const isDarkTheme = computed(() => currentTheme.value === 'dark');

const menuItems = [
  { path: '/home', name: '首页', requiresAuth: false },
  { path: '/news', name: '热点新闻', requiresAuth: false },
  { path: '/forum', name: '论坛社区', requiresAuth: false },
  { path: '/survey', name: '问卷调查', requiresAuth: false },
  { path: '/announcement', name: '公告资讯', requiresAuth: false },
  { path: '/message', name: '在线留言', requiresAuth: true }
];

const activeMenu = computed(() => {
  if (route.meta && route.meta.activeMenu) return route.meta.activeMenu;
  return route.path;
});

const handleScroll = () => {
  scrolled.value = window.scrollY > 8;
};

const syncThemeState = (event) => {
  currentTheme.value = event?.detail || document.documentElement.dataset.theme || getStoredTheme();
};

const normalizeLink = (url) => {
  if (!url) return '#';
  return /^https?:\/\//i.test(url) ? url : `https://${url}`;
};

const loadFriendLinks = async () => {
  try {
    const list = await getFriendLinkList();
    friendLinks.value = (list || []).filter(item => item?.name && item?.url);
  } catch (error) {
    console.error('加载友情链接失败:', error);
  }
};

const loadAbout = async () => {
  try {
    const about = await getAbout();
    if (about?.content) {
      aboutText.value = String(about.content).replace(/<[^>]+>/g, '').trim().slice(0, 80) + '...';
    }
  } catch (error) {
    console.error('加载关于我们失败:', error);
  }
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true });
  window.addEventListener(THEME_CHANGE_EVENT, syncThemeState);
  handleScroll();
  loadFriendLinks();
  loadAbout();
});

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll);
  window.removeEventListener(THEME_CHANGE_EVENT, syncThemeState);
});

const handleLogout = () => {
  userStore.logout();
  router.push('/home');
};

const handleThemeToggle = () => {
  currentTheme.value = toggleTheme();
};
</script>

<style scoped>
.site-header {
  background: color-mix(in srgb, rgb(var(--twc-primary)) 92%, transparent);
  border-bottom: 1px solid var(--app-header-border);
  color: var(--app-header-text);
}

.site-header-scrolled {
  box-shadow: 0 16px 28px rgb(var(--app-accent-rgb) / 0.12);
}

.site-soft-border {
  border-color: var(--app-header-border);
}

.site-nav-link-idle {
  color: var(--app-header-subtle);
}

.site-nav-link-idle:hover {
  background: var(--app-header-hover);
  color: var(--app-header-text);
}

.site-nav-link-active {
  background: var(--app-header-panel-strong);
  color: var(--app-header-text);
  box-shadow: 0 10px 22px rgb(var(--app-accent-rgb) / 0.18);
}

.site-user-pill {
  background: var(--app-header-panel);
  border: 1px solid var(--app-header-border);
}

.site-user-pill:hover {
  background: var(--app-header-panel-strong);
}

.site-avatar-fallback {
  background: var(--app-header-panel-soft);
  border: 1px solid var(--app-header-border);
  color: var(--app-header-text);
}

.theme-switch-button {
  width: 42px;
  height: 42px;
  border: 1px solid var(--app-header-border);
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--app-header-panel);
  color: var(--app-header-text);
  cursor: pointer;
  transition: transform 0.2s ease, background 0.2s ease, border-color 0.2s ease;
}

.theme-switch-button:hover {
  transform: translateY(-1px);
  background: var(--app-header-panel-strong);
}

.site-mobile-nav {
  background: var(--app-header-panel-strong);
  border-top: 1px solid var(--app-header-border);
}

.site-mobile-link-idle {
  color: var(--app-header-subtle);
}

.site-mobile-link-idle:hover {
  background: var(--app-header-hover);
  color: var(--app-header-text);
}

.site-mobile-link-active {
  background: var(--app-header-hover);
  color: var(--app-header-text);
}

.site-footer {
  background: rgb(var(--twc-primary));
  color: var(--app-header-text);
  border-top: 1px solid color-mix(in srgb, var(--app-header-border) 90%, transparent);
}

.site-footer-surface {
  background: color-mix(in srgb, rgb(var(--twc-primary)) 90%, transparent);
}

.site-footer-icon {
  width: 32px;
  height: 32px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--app-header-hover);
  transition: background 0.2s ease, transform 0.2s ease;
}

.site-footer-icon:hover {
  background: var(--app-header-panel-soft);
  transform: translateY(-1px);
}

.page-shell {
  width: 100%;
}
.page-fade-slide-enter-active,
.page-fade-slide-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
.page-fade-slide-enter-from,
.page-fade-slide-leave-to {
  opacity: 0;
  transform: translateY(16px);
}
.custom-scrollbar::-webkit-scrollbar { height: 4px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: var(--app-scrollbar-thumb); border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: var(--app-border-strong); }
</style>
