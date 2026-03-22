<template>
  <div class="app-container">
    <el-container>
      <el-header class="app-header" :class="{ 'is-scrolled': scrolled }">
        <div class="logo">新闻资讯互动平台</div>
        <el-menu mode="horizontal" :default-active="activeMenu" class="app-menu" @select="onSelect">
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/news">热点新闻</el-menu-item>
          <el-menu-item index="/forum">论坛社区</el-menu-item>
          <el-menu-item index="/survey">问卷调查</el-menu-item>
          <el-menu-item index="/announcement">公告资讯</el-menu-item>
          <el-menu-item v-if="isLoggedIn" index="/message">在线留言</el-menu-item>
        </el-menu>
        <div class="app-header-right">
          <template v-if="isLoggedIn">
            <div class="profile-entry" @click="router.push('/profile')">
              <el-avatar :size="30" :src="currentUser?.avatar || undefined" class="header-avatar">
                {{ avatarText }}
              </el-avatar>
              <span class="user-name">{{ displayName }}</span>
            </div>
            <el-button type="primary" link @click="handleLogout">退出</el-button>
          </template>
          <template v-else>
            <router-link to="/login"><el-button type="primary" link>登录</el-button></router-link>
            <router-link to="/register"><el-button type="primary" link>注册</el-button></router-link>
          </template>
        </div>
      </el-header>

      <el-main class="app-main">
        <router-view v-slot="{ Component, route }">
          <transition name="page-fade-slide" mode="out-in" appear>
            <div :key="route.fullPath" class="page-shell">
              <component :is="Component" />
            </div>
          </transition>
        </router-view>
      </el-main>

      <el-footer class="app-footer footer-animate">
        <div class="footer-inner">
          <div class="footer-brand footer-card">
            <div class="footer-logo">新闻资讯互动平台</div>
            <p class="footer-tagline">洞察 · 及时 · 互动</p>
            <p class="footer-desc">致力于打造更及时、更有趣、更具互动性的新闻资讯平台，让每位读者获取高质量信息。</p>
          </div>

          <div class="footer-col footer-card">
            <h4 class="footer-col-title">友情链接</h4>
            <div v-if="friendLinks.length" class="friend-links">
              <a
                  v-for="link in friendLinks"
                  :key="link.id"
                  :href="normalizeLink(link.url)"
                  target="_blank"
                  rel="noopener noreferrer"
              >{{ link.name }}</a>
            </div>
            <span v-else class="empty-text">暂无友情链接</span>
          </div>

          <div class="footer-col footer-card">
            <h4 class="footer-col-title">关于我们</h4>
            <p class="about-preview">{{ aboutText || '致力于打造更及时、更有趣、更具互动性的新闻资讯平台。' }}</p>
            <router-link to="/about" class="about-more">了解更多 →</router-link>
          </div>
        </div>

        <div class="footer-copyright">
          <p>© 2026 新闻资讯互动平台 &nbsp;|&nbsp; 版权所有</p>
        </div>
      </el-footer>
    </el-container>
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
      aboutText.value = String(about.content).replace(/<[^>]+>/g, '').trim().slice(0, 80);
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

const activeMenu = computed(() => {
  if (route.meta && route.meta.activeMenu) return route.meta.activeMenu;
  return route.path;
});

const onSelect = (index) => {
  router.push(index);
};
</script>

<style scoped>
.app-container { min-height: 100vh; background: transparent; display: flex; flex-direction: column; }
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  background: linear-gradient(92deg, rgba(23, 67, 190, 0.94) 0%, rgba(37, 99, 235, 0.94) 58%, rgba(56, 147, 255, 0.9) 100%);
  color: #eff6ff;
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(147, 197, 253, 0.32);
  transition: box-shadow 0.25s ease, background 0.25s ease;
}
.app-header.is-scrolled {
  background: linear-gradient(92deg, rgba(23, 67, 190, 0.98) 0%, rgba(37, 99, 235, 0.98) 58%, rgba(56, 147, 255, 0.96) 100%);
  box-shadow: 0 10px 30px rgba(30, 64, 175, 0.28);
}
.logo { font-size: 19px; font-weight: 700; color: #f8fafc; letter-spacing: 0.2px; flex-shrink: 0; }
.app-menu { flex: 1; margin-left: 32px; background: transparent; border-bottom: none; }
:deep(.app-menu .el-menu-item) { color: #dbeafe !important; border-bottom: 2px solid transparent !important; transition: all 0.2s ease; border-radius: 8px 8px 0 0; }
:deep(.app-menu .el-menu-item:hover) { color: #ffffff !important; background: rgba(255, 255, 255, 0.14) !important; }
:deep(.app-menu .el-menu-item.is-active) { color: #ffffff !important; border-bottom-color: #bfdbfe !important; background: rgba(255, 255, 255, 0.18) !important; }
.app-header-right { display: flex; align-items: center; gap: 10px; flex-shrink: 0; }
.profile-entry {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px 10px 4px 4px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.16s ease;
}
.profile-entry:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-1px);
}
.header-avatar { font-size: 14px; font-weight: 600; background: #bfdbfe; color: #1e40af; flex-shrink: 0; border: 2px solid rgba(255,255,255,.6); }
.user-name { color: #eff6ff; font-size: 14px; max-width: 120px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.app-main { padding: 20px 24px 26px; flex: 1; overflow: hidden; position: relative; }

.app-footer {
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 58%, #1d4ed8 100%);
  color: #e0e7ff;
  padding: 0;
  height: auto;
}
.app-footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: -20%;
  width: 36%;
  height: 1px;
  background: linear-gradient(90deg, rgba(255,255,255,0), rgba(191,219,254,.78), rgba(255,255,255,0));
  opacity: .75;
  animation: footer-light-sweep 5.5s ease-in-out infinite;
}
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 36px 24px 24px;
  display: grid;
  grid-template-columns: 1.2fr 1fr 1fr;
  gap: 36px;
  align-items: flex-start;
  animation: footer-rise-in 0.35s ease-out both;
}
.footer-brand { min-width: 0; }
.footer-logo { font-size: 18px; font-weight: 700; color: #f8fafc; letter-spacing: 0.3px; margin-bottom: 6px; }
.footer-tagline { font-size: 12px; color: #93c5fd; letter-spacing: 2px; margin-bottom: 14px; }
.footer-desc { font-size: 13px; color: #a8b6d3; line-height: 1.8; max-width: 340px; }
.footer-col { min-width: 0; }
.footer-card {
  transition: transform 0.24s ease, box-shadow 0.24s ease, opacity 0.3s ease;
}
.footer-card:hover {
  transform: translateY(-4px);
}
.footer-col:nth-of-type(1) { animation: footer-rise-in 0.4s ease-out both; }
.footer-col:nth-of-type(2) { animation: footer-rise-in 0.46s ease-out both; }
.footer-col-title { color: #f8fafc; font-size: 15px; font-weight: 600; margin-bottom: 14px; padding-bottom: 8px; border-bottom: 1px solid rgba(255, 255, 255, 0.12); }
.friend-links { display: flex; flex-direction: column; gap: 10px; }
.friend-links a {
  color: #c7d2fe;
  text-decoration: none;
  font-size: 13px;
  transition: color 0.2s ease, transform 0.2s ease, opacity 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}
.friend-links a::before { content: '•'; color: #818cf8; }
.friend-links a:hover { color: #ffffff; transform: translateX(4px); }
.about-preview { font-size: 13px; color: #a8b6d3; line-height: 1.8; margin-bottom: 14px; }
.about-more {
  display: inline-block;
  color: #a5b4fc;
  text-decoration: none;
  font-size: 13px;
  font-weight: 500;
  transition: color 0.2s ease, transform 0.2s ease;
}
.about-more:hover { color: #ffffff; transform: translateX(4px); }
.empty-text { color: #64748b; font-size: 13px; }
.footer-copyright { border-top: 1px solid rgba(255, 255, 255, 0.09); text-align: center; padding: 14px 24px; color: #607091; font-size: 12px; }
.footer-copyright p { margin: 0; }

@keyframes footer-rise-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes footer-light-sweep {
  0%, 100% { transform: translateX(0); opacity: 0.15; }
  50% { transform: translateX(260%); opacity: 0.85; }
}

@media (max-width: 992px) {
  .app-header { padding: 0 14px; }
  .logo { font-size: 16px; }
  .app-menu { margin-left: 10px; overflow-x: auto; }
  .user-name { display: none; }
  .app-main { padding: 12px 10px 16px; }
  .footer-inner { grid-template-columns: 1fr; gap: 24px; padding: 28px 16px 18px; }
}
</style>
