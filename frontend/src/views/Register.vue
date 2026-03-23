<template>
  <div class="page-register">


    <div class="app-card register-card">
      <div class="brand-head">
        <div class="brand-mark">N</div>
        <div class="brand-copy">
          <div class="brand-name">新闻素材互动平台</div>
          <div class="brand-slogan">开启你的专属投稿体验</div>
        </div>
      </div>
      <div class="card-badge">CREATE ACCOUNT</div>
      <div class="app-page-title">用户注册</div>
      <div class="app-page-subtitle">填写以下信息完成注册，开启你的创作之旅</div>
      <el-form class="auth-form" :model="form" :rules="rules" ref="formRef" label-width="88px" @submit.prevent="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" @keyup.enter="onSubmit" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" @keyup.enter="onSubmit" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            show-password 
            placeholder="请输入密码" 
            @keyup.enter="onSubmit" 
            @focus="isBlind = true"
            @blur="isBlind = false"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="form.confirmPassword" 
            type="password" 
            show-password 
            placeholder="请再次输入密码" 
            @keyup.enter="onSubmit"
            @focus="isBlind = true"
            @blur="isBlind = false"
          />
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
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="form-switch">
        <span class="switch-text">已经有账号？</span>
        <button type="button" class="switch-button" @click="router.push('/login')">立即登录</button>
      </div>
    </div>

    <div class="avatar-container">
      <InteractiveAvatar :isBlind="isBlind" />
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { register, fetchCaptcha } from '../api/auth';
import InteractiveAvatar from '../components/InteractiveAvatar.vue';

const router = useRouter();
const formRef = ref(null);
const loading = ref(false);
const isBlind = ref(false);
const captchaKey = ref('');
const captchaUrl = ref('');

const form = reactive({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: '',
  captchaCode: ''
});

const validateConfirm = (rule, value, callback) => {
  if (!value) {
    callback();
    return;
  }
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致，请重新输入'));
  } else {
    callback();
  }
};

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'));
    return;
  }
  if (value.length < 6) {
    callback(new Error('密码长度不能少于 6 位'));
    return;
  }

  let categoryCount = 0;
  if (/[A-Za-z]/.test(value)) categoryCount += 1;
  if (/\d/.test(value)) categoryCount += 1;
  if (/[^A-Za-z0-9]/.test(value)) categoryCount += 1;

  if (categoryCount < 2) {
    callback(new Error('密码需包含字母、数字、特殊符号中的至少两种'));
    return;
  }

  callback();
};

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ validator: validatePassword, trigger: ['blur', 'change'] }],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: ['blur', 'change'] }
  ],
  captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
};

const loadCaptcha = async () => {
  try {
    const data = await fetchCaptcha();
    captchaKey.value = data.captchaKey;
    captchaUrl.value = data.captchaUrl;
  } catch (error) {
    console.error('获取验证码失败', error);
    captchaKey.value = '';
    captchaUrl.value = '';
    ElMessage.warning('验证码加载失败，请点击验证码图片重新加载');
  }
};

const onSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return;

    loading.value = true;
    try {
      await register({
        username: form.username,
        password: form.password,
        nickname: form.nickname,
        captchaKey: captchaKey.value,
        captchaCode: form.captchaCode
      });
      ElMessage.success('注册成功，即将跳转到登录页');
      router.push('/login');
    } catch (error) {
      void error;
      form.captchaCode = '';
      await loadCaptcha();
    } finally {
      loading.value = false;
    }
  });
};

onMounted(() => {
  loadCaptcha();
});
</script>

<style scoped>
.page-register {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 120px;
  min-height: calc(100vh - 64px);
  position: relative;
  overflow: hidden;
  isolation: isolate;
}

.avatar-container {
  width: 320px;
  height: 320px;
  pointer-events: none;
}

.register-bg {
  position: absolute;
  inset: 0;
  z-index: -1;
  pointer-events: none;
}

.register-bg-svg {
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
  animation: register-orb-a 6.5s ease-in-out infinite;
}

.orb-b {
  animation: register-orb-b 7.5s ease-in-out infinite;
}

.wave-a {
  animation: register-wave-a 8.5s ease-in-out infinite;
}

.wave-b {
  animation: register-wave-b 9.5s ease-in-out infinite;
}

.register-card {
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

.register-card::after {
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
  box-shadow:0 12px 24px rgba(14,165,233,.22);
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
  align-items: center;
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

@keyframes register-orb-a {
  0%, 100% { transform: translate(0, 0) scale(1); opacity: 0.28; }
  33% { transform: translate(-28px, 22px) scale(1.1); opacity: 0.32; }
  66% { transform: translate(12px, -18px) scale(0.94); opacity: 0.26; }
}

@keyframes register-orb-b {
  0%, 100% { transform: translate(0, 0) scale(1); opacity: 0.22; }
  33% { transform: translate(24px, -20px) scale(1.06); opacity: 0.26; }
  66% { transform: translate(-15px, 25px) scale(0.96); opacity: 0.2; }
}

@keyframes register-wave-a {
  0%, 100% { transform: translateY(0) translateX(0); }
  33% { transform: translateY(10px) translateX(-7px); }
  66% { transform: translateY(-8px) translateX(9px); }
}

@keyframes register-wave-b {
  0%, 100% { transform: translateY(0) translateX(0); }
  33% { transform: translateY(-12px) translateX(8px); }
  66% { transform: translateY(10px) translateX(-6px); }
}

.auth-switch {
  margin-top: 8px;
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

@media (max-width: 992px) {
  .avatar-container {
    display: none;
  }
}

@media (max-width: 768px) {
  .register-card {
    width: 100%;
    max-width: 460px;
    margin: 0 14px;
    padding: 24px 20px 18px;
  }
}
</style>
