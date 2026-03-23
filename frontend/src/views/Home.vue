<template>
  <div class="home-page">
    <div class="home-bg" aria-hidden="true">
      <svg class="home-bg-svg" viewBox="0 0 1440 900" fill="none" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <linearGradient id="homeGradientA" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0%" stop-color="#60A5FA" stop-opacity="0.22" />
            <stop offset="100%" stop-color="#001b44" stop-opacity="0.06" />
          </linearGradient>
          <linearGradient id="homeGradientB" x1="1" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="#93C5FD" stop-opacity="0.18" />
            <stop offset="100%" stop-color="#1D4ED8" stop-opacity="0.04" />
          </linearGradient>
        </defs>
        <circle class="home-orb orb-a" cx="180" cy="140" r="150" fill="url(#homeGradientA)" />
        <circle class="home-orb orb-b" cx="1250" cy="180" r="120" fill="url(#homeGradientB)" />
        <path class="home-wave wave-a" d="M0 620C170 566 366 574 534 640C712 710 844 752 1026 712C1178 678 1300 618 1440 632V900H0V620Z" fill="url(#homeGradientA)" />
        <path class="home-wave wave-b" d="M0 720C184 770 350 790 546 754C730 720 850 646 1022 642C1188 640 1300 690 1440 736V900H0V720Z" fill="url(#homeGradientB)" />
      </svg>
    </div>

    <div class="app-card motion-block motion-delay-1">
      <div class="app-page-title motion-block motion-delay-2">{{ recommendPanelTitle }}</div>
      <div class="app-page-subtitle motion-block motion-delay-2">
        {{ recommendPanelSubtitle }}
      </div>

      <section ref="heroVisualRef" class="hero-visual-zone motion-block motion-delay-2">
        <div class="hero-visual-bg" aria-hidden="true">
          <span class="hero-bg-orb orb-a js-hero-bg-orb"></span>
          <span class="hero-bg-orb orb-b js-hero-bg-orb"></span>
          <span class="hero-bg-orb orb-c js-hero-bg-orb"></span>
        </div>

        <el-carousel height="280px" class="hero-carousel" indicator-position="outside">
          <el-carousel-item v-for="item in carouselImages" :key="item.url">
            <div class="hero-slide" :style="{ backgroundImage: `url(${item.url})` }">
              <div class="hero-mask" @click="item.linkUrl ? router.push(item.linkUrl) : null" :style="item.linkUrl ? 'cursor:pointer' : ''">
                <div class="hero-title">{{ item.title || '新闻资讯互动平台' }}</div>
                <div class="hero-subtitle">{{ item.subtitle || '全面、及时、专业的内容呈现' }}</div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </section>

      <el-skeleton v-if="loading" animated :rows="5" />
      <div v-else>
        <div class="section-title motion-block motion-delay-3">{{ recommendSectionTitle }}</div>
        <el-empty v-if="newsList.length === 0" description="暂无推荐" />
        <div v-else class="recommend-grid" ref="recommendTimelineRef">
          <div
              v-for="item in newsList"
              :key="item.id"
              class="news-card js-recommend-item"
              @click="goNewsDetail(item.id)"
          >
            <div class="news-card-cover">
              <img :src="item.displayCover || defaultNewsImage" alt="新闻封面" class="news-cover-img" />
              <div class="news-card-date" v-if="item.publishTime">
                {{ (item.publishTime || '').split(' ')[0] }}
              </div>
            </div>
            <div class="news-card-body">
              <div class="news-card-title">{{ item.title }}</div>
              <div class="news-card-meta">
                <span>点击 {{ item.viewCount || 0 }}</span>
                <span class="meta-divider">·</span>
                <span>收藏 {{ item.collectCount || 0 }}</span>
                <span class="meta-divider">·</span>
                <span>点赞 {{ item.likeCount || 0 }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { gsap } from 'gsap';
import { useUserStore } from '../store/user';
import { fetchNewsPage, fetchRecommendNews } from '../api/news';
import { defaultNewsImage, normalizeNewsCard } from '../utils/news';

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);
const newsList = ref([]);
const recommendTimelineRef = ref(null);
const heroVisualRef = ref(null);
const isPersonalizedRecommend = computed(() => userStore.isLoggedIn);
const recommendPanelTitle = computed(() => isPersonalizedRecommend.value ? '首页推荐' : '首页推荐');
const recommendPanelSubtitle = computed(() => (
  isPersonalizedRecommend.value
    ? '基于你的点赞与收藏行为进行协同过滤推荐新闻'
    : '登录后将基于你的点赞与收藏行为进行协同过滤推荐，当前展示最新发布新闻'
));
const recommendSectionTitle = computed(() => (
  isPersonalizedRecommend.value ? '为你推荐的新闻' : '最新发布的新闻'
));

const carouselImages = ref([
  {
    url: 'https://images.unsplash.com/photo-1495020689067-958852a7765e?auto=format&fit=crop&w=1600&q=80',
    title: '全球热点 · 实时掌握',
    subtitle: '整合全面资讯，第一时间汇总世界动态',
    linkUrl: '/news'
  },
  {
    url: 'https://images.unsplash.com/photo-1504711434969-e33886168f5c?auto=format&fit=crop&w=1600&q=80',
    title: '深度报道 · 多维视角',
    subtitle: '资深编辑团队，呈现新闻背后的复杂故事',
    linkUrl: '/news'
  },
  {
    url: 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=1600&q=80',
    title: '资讯互动 · 百家争鸣',
    subtitle: '与志同道合的网友分享观点，共话时事',
    linkUrl: '/forum'
  },
  {
    url: 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1600&q=80',
    title: '科技前沿 · 数字未来',
    subtitle: '最新科技动态，预见未来发展趋势',
    linkUrl: '/news'
  }
]);

const loadRecommend = async () => {
  loading.value = true;
  try {
    if (userStore.userId) {
      const recommendList = await fetchRecommendNews(userStore.userId, 6);
      newsList.value = (recommendList || []).map(normalizeNewsCard);
    } else {
      const page = await fetchNewsPage({ pageNum: 1, pageSize: 6 });
      newsList.value = (page.records || []).map(normalizeNewsCard);
    }
  } catch {
    newsList.value = [];
  } finally {
    loading.value = false;
  }
};

const goNewsDetail = (id) => {
  router.push(`/news/${id}`);
};

const runRecommendAnimation = async () => {
  await nextTick();
  if (!recommendTimelineRef.value) return;
  const rootNode = recommendTimelineRef.value.$el || recommendTimelineRef.value;
  const items = rootNode?.querySelectorAll('.js-recommend-item');
  if (!items || !items.length) return;

  gsap.fromTo(
      items,
      { autoAlpha: 0, y: 20 },
      {
        autoAlpha: 1,
        y: 0,
        duration: 0.55,
        ease: 'power2.out',
        stagger: 0.12,
        clearProps: 'transform,opacity,visibility'
      }
  );
};

const runHeroVisualAnimation = async () => {
  await nextTick();
  const root = heroVisualRef.value;
  if (!root) return;

  const orbs = root.querySelectorAll('.js-hero-bg-orb');
  if (!orbs || !orbs.length) return;

  gsap.fromTo(
      root,
      { autoAlpha: 0, y: 10 },
      {
        autoAlpha: 1,
        y: 0,
        duration: 0.65,
        ease: 'power2.out'
      }
  );

  orbs.forEach((orb, index) => {
    gsap.to(orb, {
      x: index === 0 ? 22 : index === 1 ? -18 : 12,
      y: index === 0 ? 16 : index === 1 ? -20 : 14,
      scale: index === 1 ? 1.12 : 1.08,
      duration: 6.2 + index,
      repeat: -1,
      yoyo: true,
      ease: 'sine.inOut'
    });
  });
};

onMounted(async () => {
  await loadRecommend();
  await runRecommendAnimation();
  await runHeroVisualAnimation();
});
</script>

<style scoped>
.home-page {
  max-width: 1240px;
  margin: 0 auto;
  position: relative;
  isolation: isolate;
}

.home-bg {
  position: fixed;
  inset: 0;
  z-index: -2;
  pointer-events: none;
}

.home-bg-svg {
  width: 100%;
  height: 100%;
}

.home-orb,
.home-wave {
  transform-origin: center;
  will-change: transform;
}

.home-wave.wave-a {
  animation: home-wave-a 9s ease-in-out infinite;
}

.home-wave.wave-b {
  animation: home-wave-b 10s ease-in-out infinite;
}

.home-orb.orb-a {
  animation: home-orb-a 6.5s ease-in-out infinite;
}

.home-orb.orb-b {
  animation: home-orb-b 7.5s ease-in-out infinite;
}

.hero-visual-zone {
  position: relative;
  isolation: isolate;
  margin-bottom: 20px;
}

.hero-visual-bg {
  position: absolute;
  inset: -26px -18px -24px;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
  border-radius: 22px;
  opacity: 0;
}

.hero-bg-orb {
  position: absolute;
  border-radius: 999px;
  filter: blur(8px);
  will-change: transform;
}

.hero-bg-orb.orb-a {
  width: 220px;
  height: 220px;
  left: -44px;
  top: -36px;
  background: radial-gradient(circle at 35% 35%, rgba(125, 211, 252, 0.35), rgba(96, 165, 250, 0.08) 70%, rgba(96, 165, 250, 0) 100%);
}

.hero-bg-orb.orb-b {
  width: 260px;
  height: 260px;
  right: -70px;
  top: -58px;
  background: radial-gradient(circle at 45% 45%, rgba(167, 139, 250, 0.28), rgba(139, 92, 246, 0.08) 72%, rgba(139, 92, 246, 0) 100%);
}

.hero-bg-orb.orb-c {
  width: 250px;
  height: 250px;
  right: 18%;
  bottom: -130px;
  background: radial-gradient(circle at 50% 50%, rgba(110, 231, 183, 0.2), rgba(59, 130, 246, 0.07) 70%, rgba(59, 130, 246, 0) 100%);
}

.hero-carousel {
  position: relative;
  z-index: 1;
}

.hero-slide {
  width: 100%;
  height: 300px;
  background-size: cover;
  background-position: center;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.2);
}

.hero-mask {
  height: 100%;
  background: linear-gradient(96deg, rgba(15, 23, 42, 0.74) 0%, rgba(15, 23, 42, 0.34) 55%, rgba(15, 23, 42, 0.16) 100%);
  color: #f8fafc;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 24px;
  text-decoration: none;
  transition: background 0.25s ease;
}

.hero-mask:hover {
  background: linear-gradient(96deg, rgba(15, 23, 42, 0.78) 0%, rgba(15, 23, 42, 0.36) 60%, rgba(15, 23, 42, 0.18) 100%);
}

.hero-title {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 0.2px;
}

.hero-subtitle {
  margin-top: 8px;
  font-size: 14px;
  color: #dbeafe;
}

.section-title {
  font-weight: 700;
  font-size: 16px;
  margin-bottom: 12px;
  color: #1e293b;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  padding-top: 10px;
}

.news-card {
  cursor: pointer;
  background: #ffffff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(15, 23, 42, 0.04);
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
}

.news-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 32px rgba(37, 99, 235, 0.1);
  border-color: #bfdbfe;
}

.news-card:hover .news-cover-img {
  transform: scale(1.06);
}

.news-card:hover .news-card-title {
  color: var(--app-primary, #001b44);
}

.news-card-cover {
  position: relative;
  width: 100%;
  height: 160px;
  overflow: hidden;
  background-color: #f1f5f9;
}

.news-cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.news-card-date {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(15, 23, 42, 0.7);
  backdrop-filter: blur(4px);
  color: #fff;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 20px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.news-card-body {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.news-card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.5;
  margin-bottom: auto;
  min-height: 48px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s ease;
}

.news-card-meta {
  margin-top: 14px;
  font-size: 13px;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 10px;
}

.meta-divider {
  color: #cbd5e1;
  font-size: 14px;
}

@media (max-width: 820px) {
  .recommend-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 540px) {
  .recommend-grid {
    grid-template-columns: 1fr;
  }
}

@keyframes home-wave-a {
  0%, 100% { transform: translateY(0) translateX(0); opacity: 0.22; }
  33% { transform: translateY(12px) translateX(6px); opacity: 0.26; }
  66% { transform: translateY(-5px) translateX(-8px); opacity: 0.2; }
}

@keyframes home-wave-b {
  0%, 100% { transform: translateY(0) translateX(0); opacity: 0.18; }
  33% { transform: translateY(-10px) translateX(-5px); opacity: 0.22; }
  66% { transform: translateY(9px) translateX(7px); opacity: 0.16; }
}

@keyframes home-orb-a {
  0%, 100% { transform: translate(0, 0) scale(1); opacity: 0.22; }
  33% { transform: translate(22px, 18px) scale(1.08); opacity: 0.26; }
  66% { transform: translate(-10px, 24px) scale(0.94); opacity: 0.2; }
}

@keyframes home-orb-b {
  0%, 100% { transform: translate(0, 0) scale(1); opacity: 0.18; }
  33% { transform: translate(-20px, 22px) scale(1.06); opacity: 0.22; }
  66% { transform: translate(14px, -16px) scale(0.96); opacity: 0.16; }
}

@media (max-width: 992px) {
  .hero-slide {
    height: 230px;
  }

  .hero-title {
    font-size: 22px;
  }

  .hero-visual-bg {
    inset: -18px -10px -14px;
    border-radius: 18px;
  }

  .hero-bg-orb.orb-a {
    width: 170px;
    height: 170px;
    left: -36px;
    top: -30px;
  }

  .hero-bg-orb.orb-b {
    width: 190px;
    height: 190px;
    right: -52px;
    top: -40px;
  }

  .hero-bg-orb.orb-c {
    width: 180px;
    height: 180px;
    right: 12%;
    bottom: -88px;
  }
}
</style>
