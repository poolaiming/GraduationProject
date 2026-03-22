<template>
  <div class="page-login">
    <div class="login-bg" aria-hidden="true">
      <svg class="login-bg-svg" viewBox="0 0 1440 900" fill="none" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <linearGradient id="loginGradientA" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0%" stop-color="#38BDF8" stop-opacity="0.28" />
            <stop offset="100%" stop-color="#2563EB" stop-opacity="0.08" />
          </linearGradient>
          <linearGradient id="loginGradientB" x1="1" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="#7DD3FC" stop-opacity="0.22" />
            <stop offset="100%" stop-color="#1D4ED8" stop-opacity="0.05" />
          </linearGradient>
        </defs>
        <circle class="svg-orb orb-a" cx="260" cy="180" r="180" fill="url(#loginGradientA)" />
        <circle class="svg-orb orb-b" cx="1180" cy="180" r="140" fill="url(#loginGradientB)" />
        <path class="svg-wave wave-a" d="M0 650C180 590 320 590 480 640C650 694 780 754 960 712C1120 674 1250 600 1440 620V900H0V650Z" fill="url(#loginGradientA)" />
        <path class="svg-wave wave-b" d="M0 720C160 760 360 788 560 744C726 708 850 632 1030 636C1180 640 1288 696 1440 750V900H0V720Z" fill="url(#loginGradientB)" />
      </svg>
    </div>

    <div class="app-card login-card">
      <div class="brand-head">
        <div class="brand-mark">N</div>
        <div class="brand-copy">
          <div class="brand-name">新闻素材互动平台</div>
          <div class="brand-slogan">继续你的专属投稿体验</div>
        </div>
      </div>
      <div class="card-badge">SIGN IN</div>
      <div class="app-page-title">用户登录</div>
      <div class="app-page-subtitle">填写以下信息完成登录，继续你的创作之旅</div>
      <el-form class="auth-form" :model="form" :rules="rules" ref="formRef" label-width="88px" @submit.prevent="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" @keyup.enter="onSubmit" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" @keyup.enter="onSubmit" />
        </el-form-item>
        <el-form-item label="验证码" prop="captchaCode">
          <div class="captcha-row">
            <el-input v-model="form.captchaCode" placeholder="请输入验证码" @keyup.enter="onSubmit" />
            <button class="captcha-box" type="button" @click="loadCaptcha" title="点击刷新验证码">
              <img v-if="captchaUrl" class="captcha-image" :src="captchaUrl" alt="验证码" />
              <span v-else class="captcha-placeholder">点击刷新</span>
            </button>
          </div>
        </el-form-item>
        <el-form-item class="form-action">
          <el-button class="auth-submit" type="primary" :loading="loading" @click="onSubmit" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="form-switch">
        <span class="switch-text">还没有账号？</span>
        <button type="button" class="switch-button" @click="router.push('/register')">立即注册</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';
import { login, fetchCaptcha } from '../api/auth';
import { useUserStore } from '../store/user';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const formRef = ref();
const loading = ref(false);
const captchaKey = ref('');
const captchaUrl = ref('');

const form = reactive({
  username: '',
  password: '',
  captchaCode: ''
});

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
};

const loadCaptcha = async () => {
  try {
    const data = await fetchCaptcha();
    captchaKey.value = data.captchaKey;
    captchaUrl.value = data.captchaUrl;
  } catch {
    captchaKey.value = '';
    captchaUrl.value = '';
  }
};

const onSubmit = async () => {
  try {
    await formRef.value.validate();
  } catch {
    return;
  }
  loading.value = true;
  try {
    const payload = {
      username: form.username,
      password: form.password,
      captchaKey: captchaKey.value,
      captchaCode: form.captchaCode
    };

    const user = await login(payload);
    userStore.setUser(user);
    ElMessage.success('登录成功');
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/home';
    router.replace(redirect);
  } catch {
    // 错误提示已由 http.js 全局拦截器统一处理，此处仅做页面收尾
    form.captchaCode = '';
    await loadCaptcha();
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadCaptcha();
});
</script>

<style scoped>
.page-login {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 64px);
  position: relative;
  overflow: hidden;
  isolation: isolate;
}

.login-bg {
  position: absolute;
  inset: 0;
  z-index: -1;
  pointer-events: none;
}

.login-bg-svg {
  width: 100%;
  height: 100%;
  min-height: 100%;
}

.svg-orb,
.svg-wave {
  transform-origin: center;
  will-change: transform;
}

.orb-a {
  animation: login-orb-a 6s ease-in-out infinite;
}

.orb-b {
  animation: login-orb-b 7s ease-in-out infinite;
}

.wave-a {
  animation: login-wave-a 8s ease-in-out infinite;
}

.wave-b {
  animation: login-wave-b 9s ease-in-out infinite;
}

.login-card {
  width: 520px;
  position: relative;
  overflow: hidden;
  padding: 30px 32px 24px;
  border-radius: 22px !important;
  backdrop-filter: blur(10px);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 250, 252, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.74);
  box-shadow: 0 24px 60px rgba(14, 165, 233, 0.12), 0 10px 24px rgba(15, 23, 42, 0.08) !important;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-rendering: optimizeLegibility;
}

.login-card::after {
  content: '';
  position: absolute;
  inset: 0 0 auto;
  height: 1px;
  background: linear-gradient(90deg, rgba(56,189,248,.0), rgba(56,189,248,.55), rgba(56,189,248,.0));
}

.brand-head {
  display:flex;
  align-items:center;
  gap:12px;
  margin-bottom:16px;
}
.brand-mark {
  width:42px;
  height:42px;
  border-radius:14px;
  display:flex;
  align-items:center;
  justify-content:center;
  background:linear-gradient(135deg,#0ea5e9,#38bdf8);
  color:#fff;
  font-size:18px;
  font-weight:800;
  box-shadow:0 12px 24px rgba(37,99,235,.22);
}
.brand-copy { min-width:0; }
.brand-name {
  font-size:16px;
  font-weight:700;
  color:#0f172a;
  line-height:1.3;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
.brand-slogan {
  margin-top:4px;
  font-size:12px;
  color:#475569;
  letter-spacing:.5px;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
.card-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  margin-bottom: 14px;
  border-radius: 999px;
  background: rgba(14, 165, 233, 0.10);
  color: #0284c7;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 1.2px;
}

.auth-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.auth-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #1e293b;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.auth-form :deep(.el-input__wrapper) {
  min-height: 44px;
  border-radius: 12px;
  box-shadow: 0 0 0 1px rgba(148, 163, 184, 0.22) inset;
  background: rgba(255, 255, 255, 0.95);
}

.auth-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px rgba(14, 165, 233, 0.55) inset, 0 0 0 4px rgba(14, 165, 233, 0.08);
}

.form-action {
  margin-top: 8px;
}

.captcha-row {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 130px;
  gap: 8px;
}

.captcha-box {
  height: 44px;
  border: 1px solid #d7e2f2;
  border-radius: 12px;
  cursor: pointer;
  overflow: hidden;
  background: #f8fbff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.captcha-image {
  width: 100%;
  height: 100%;
  display: block;
}

.captcha-placeholder {
  color: #64748b;
  font-size: 12px;
}

@keyframes login-orb-a {
  0%, 100% { transform: translate(0, 0) scale(1); opacity: 0.3; }
  33% { transform: translate(30px, 20px) scale(1.08); opacity: 0.35; }
  66% { transform: translate(-10px, 30px) scale(0.95); opacity: 0.28; }
}

@keyframes login-orb-b {
  0%, 100% { transform: translate(0, 0) scale(1); opacity: 0.26; }
  33% { transform: translate(-25px, 20px) scale(1.1); opacity: 0.3; }
  66% { transform: translate(15px, -15px) scale(0.92); opacity: 0.24; }
}

@keyframes login-wave-a {
  0%, 100% { transform: translateY(0) translateX(0); }
  33% { transform: translateY(12px) translateX(8px); }
  66% { transform: translateY(-6px) translateX(-5px); }
}

@keyframes login-wave-b {
  0%, 100% { transform: translateY(0) translateX(0); }
  33% { transform: translateY(-10px) translateX(-6px); }
  66% { transform: translateY(8px) translateX(10px); }
}

.auth-switch {
  margin-top: 6px;
  padding-top: 14px;
  border-top: 1px solid rgba(148,163,184,.16);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.switch-text { color: #64748b; }
.switch-button {
  border: 1px solid rgba(14, 165, 233, 0.24);
  background: rgba(14, 165, 233, 0.08);
  color: #0284c7;
  font-weight: 600;
  font-size: 13px;
  line-height: 1;
  padding: 9px 14px;
  border-radius: 999px;
  cursor: pointer;
  transition: background-color .2s ease, border-color .2s ease, color .2s ease, transform .2s ease;
}
.switch-button:hover {
  background: rgba(14, 165, 233, 0.14);
  border-color: rgba(14, 165, 233, 0.36);
  color: #0369a1;
  transform: translateY(-1px);
}
.auth-submit {
  position: relative;
  overflow: hidden;
  min-height: 44px;
  border-radius: 12px;
  font-weight: 700;
  letter-spacing: .5px;
  box-shadow: 0 14px 28px rgba(14,165,233,.22);
}
.auth-submit::before {
  content: "";
  position: absolute;
  top: 0;
  bottom: 0;
  left: -30%;
  width: 36%;
  background: linear-gradient(90deg, rgba(255,255,255,0), rgba(255,255,255,.28), rgba(255,255,255,0));
  transform: skewX(-24deg);
  transition: left .45s ease;
}
.auth-submit:hover::before { left: 110%; }
.auth-submit:hover {
  transform: translateY(-1px);
  box-shadow: 0 18px 32px rgba(14,165,233,.26);
}
.auth-submit:active { transform: translateY(0); }

@media (max-width: 768px) {
  .login-card {
    width: 100%;
    max-width: 460px;
    margin: 0 14px;
    padding: 24px 20px 18px;
  }
}
</style>
