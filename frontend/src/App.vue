<template>
  <div class="min-h-screen bg-background flex flex-col font-body text-on-background relative overflow-hidden text-base">
    <header 
      :class="[
        'sticky top-0 z-50 transition-all duration-300 border-b border-primary/10 backdrop-blur-md',
        scrolled ? 'bg-primary/95 shadow-lg shadow-primary/10 text-on-primary py-2' : 'bg-primary text-on-primary py-4'
      ]"
    >
      <div class="max-w-7xl mx-auto px-6 lg:px-8 flex items-center justify-between">
        <div class="flex items-center gap-3 relative z-20 cursor-pointer" @click="router.push('/home')">
          <div class="w-10 h-10 rounded-xl bg-primary-container flex items-center justify-center shadow-inner border border-white/10">
            <span class="material-symbols-outlined text-white" style="font-variation-settings: 'FILL' 1;">public</span>
          </div>
          <h1 class="text-xl font-extrabold tracking-tight font-headline text-white hidden sm:block">新闻资讯互动平台</h1>
        </div>
        
        <nav class="hidden md:flex flex-1 mx-8 justify-center relative z-20">
          <ul class="flex items-center gap-2">
            <li v-for="menu in menuItems" :key="menu.path">
              <router-link 
                v-if="!menu.requiresAuth || isLoggedIn"
                :to="menu.path"
                class="px-5 py-2.5 rounded-full text-sm font-semibold transition-all duration-300"
                :class="activeMenu === menu.path ? 'bg-primary-container text-white shadow-md' : 'text-on-primary/80 hover:bg-white/10 hover:text-white'"
              >
                {{ menu.name }}
              </router-link>
            </li>
          </ul>
        </nav>

        <div class="flex items-center gap-4 relative z-20">
          <template v-if="isLoggedIn">
            <div 
              class="flex items-center gap-3 cursor-pointer p-1.5 pr-4 rounded-full bg-primary-container hover:bg-primary-container/80 transition-colors border border-white/10"
              @click="router.push('/profile')"
            >
              <img v-if="currentUser?.avatar" :src="currentUser.avatar" class="w-8 h-8 rounded-full border border-white/20 object-cover" />
              <div v-else class="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center border border-white/10 text-xs font-bold text-white uppercase">{{ avatarText }}</div>
              <span class="text-sm font-bold text-white max-w-[100px] truncate hidden sm:block">{{ displayName }}</span>
            </div>
            <button @click="handleLogout" class="w-10 h-10 rounded-full flex items-center justify-center hover:bg-error/20 transition-colors cursor-pointer text-error/80 hover:text-error" title="退出">
               <span class="material-symbols-outlined text-lg">logout</span>
            </button>
          </template>
          <template v-else>
            <router-link to="/login" class="text-sm font-bold text-on-primary/90 hover:text-white transition-colors px-3 py-2">登录</router-link>
            <router-link to="/register" class="text-sm font-bold bg-secondary text-on-secondary px-5 py-2.5 rounded-full hover:bg-secondary-fixed-dim transition-colors shadow-lg shadow-secondary/20">注册</router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- Mobile Nav -->
    <nav class="md:hidden bg-primary-container border-t border-white/5 sticky top-[72px] z-40 overflow-x-auto custom-scrollbar shadow-md">
      <ul class="flex items-center px-4 py-2 gap-2 min-w-max">
        <li v-for="menu in menuItems" :key="`mobile-${menu.path}`">
          <router-link 
            v-if="!menu.requiresAuth || isLoggedIn"
            :to="menu.path"
            class="px-4 py-2 rounded-xl text-xs font-semibold transition-colors block"
            :class="activeMenu === menu.path ? 'bg-white/10 text-white' : 'text-on-primary/70 hover:bg-white/5'"
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

    <footer class="bg-primary text-on-primary py-16 relative overflow-hidden mt-auto border-t border-primary/20 shrink-0">
      <div class="absolute inset-0 z-0 bg-primary opacity-90"></div>
      <div class="absolute top-0 right-1/4 w-96 h-96 bg-primary-container blur-3xl rounded-full opacity-50 mix-blend-screen pointer-events-none"></div>
      <div class="absolute bottom-0 left-10 w-64 h-64 bg-secondary/10 blur-3xl rounded-full opacity-50 mix-blend-screen pointer-events-none"></div>

      <div class="max-w-7xl mx-auto px-6 lg:px-8 relative z-10">
        <div class="grid grid-cols-1 md:grid-cols-12 gap-12 lg:gap-8">
          <div class="md:col-span-5 flex flex-col items-start">
            <div class="flex items-center gap-3 mb-6">
              <div class="w-10 h-10 rounded-xl bg-primary-container flex items-center justify-center border border-white/10">
                <span class="material-symbols-outlined text-white">public</span>
              </div>
              <h2 class="text-xl font-extrabold font-headline text-white">新闻资讯互动平台</h2>
            </div>
            <h3 class="text-sm font-bold text-secondary-fixed mb-4 tracking-widest uppercase">洞察 · 及时 · 互动</h3>
            <p class="text-sm text-on-primary/70 leading-relaxed max-w-sm mb-8">
              致力于打造更及时、更有趣、更具互动性的新闻资讯平台，让每位读者获取高质量信息，发现更有价值的声音。
            </p>
          </div>

          <div class="md:col-span-3 md:col-start-7">
            <h4 class="text-sm font-bold text-white uppercase tracking-wider mb-6 pb-2 border-b border-white/10 inline-block">友情链接</h4>
            <ul v-if="friendLinks.length" class="space-y-4">
              <li v-for="link in friendLinks" :key="link.id">
                <a :href="normalizeLink(link.url)" target="_blank" rel="noopener noreferrer" class="text-sm text-on-primary/70 hover:text-white transition-colors flex items-center gap-2 group">
                  <span class="w-1.5 h-1.5 rounded-full bg-secondary-fixed group-hover:scale-150 transition-transform"></span>
                  {{ link.name }}
                </a>
              </li>
            </ul>
            <p v-else class="text-sm text-on-primary/50">暂无友情链接</p>
          </div>

          <div class="md:col-span-3">
            <h4 class="text-sm font-bold text-white uppercase tracking-wider mb-6 pb-2 border-b border-white/10 inline-block">关于我们</h4>
            <p class="text-sm text-on-primary/70 leading-relaxed mb-6">{{ aboutText || '快速了解最新时事，发现生活点滴。' }}</p>
            <router-link to="/about" class="inline-flex items-center gap-2 text-sm font-bold text-secondary-fixed hover:text-white transition-colors group">
              了解更多 
              <span class="material-symbols-outlined text-sm group-hover:translate-x-1 transition-transform">arrow_forward</span>
            </router-link>
          </div>
        </div>

        <div class="mt-16 pt-8 border-t border-white/10 flex flex-col md:flex-row justify-between items-center gap-4">
          <p class="text-xs text-on-primary/50">© 2026 新闻资讯互动平台. All rights reserved.</p>
          <div class="flex gap-4">
             <a href="#" class="w-8 h-8 rounded-full bg-white/5 flex items-center justify-center hover:bg-white/10 transition-colors"><span class="material-symbols-outlined text-sm text-on-primary/70">share</span></a>
             <a href="#" class="w-8 h-8 rounded-full bg-white/5 flex items-center justify-center hover:bg-white/10 transition-colors"><span class="material-symbols-outlined text-sm text-on-primary/70">mail</span></a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router';
import { computed, onMounted, onBeforeUnmount, ref } from 'vue';
import { useUserStore } from './store/user';
import { getAbout, getFriendLinkList } from './api/system';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const scrolled = ref(false);
const friendLinks = ref([]);
const aboutText = ref('');

const isLoggedIn = computed(() => userStore.isLoggedIn);
const currentUser = computed(() => userStore.user);
const displayName = computed(() => userStore.displayName);
const avatarText = computed(() => displayName.value?.[0] || '?');

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
  handleScroll();
  loadFriendLinks();
  loadAbout();
});

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll);
});

const handleLogout = () => {
  userStore.logout();
  router.push('/home');
};
</script>

<style scoped>
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
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.2); border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(255,255,255,0.4); }
</style>
