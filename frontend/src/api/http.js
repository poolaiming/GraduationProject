import axios from 'axios';
import { ElMessage } from 'element-plus';
import { clearUser, getLoginPath } from '../utils/auth';
import router from '../router';

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 10000
});

/* ========== 请求拦截器 ========== */
instance.interceptors.request.use(
  (config) => {
    // 预留认证 header 扩展点（当前后端无 JWT，暂不注入 Bearer Token）
    // 未来若后端支持 Token，可在此统一添加：
    // const token = getToken();
    // if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/* ========== 响应拦截器 ========== */
instance.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code !== 0) {
      // 支持静默模式：调用方可通过 config.meta.silentError = true 跳过全局弹窗
      const silentError = response.config?.meta?.silentError;
      if (!silentError) {
        ElMessage.error(res.message || '请求失败');
      }
      return Promise.reject(new Error(res.message || '请求失败'));
    }
    return res.data;
  },
  (error) => {
    const status = error.response?.status;

    if (status === 401) {
      // 认证失效：清除本地登录态，跳转登录页
      clearUser();
      ElMessage.error('登录已失效，请重新登录');
      router.push(getLoginPath());
    } else {
      // 网络异常或其他 HTTP 错误
      const silentError = error.config?.meta?.silentError;
      if (!silentError) {
        const msg = error.response?.data?.message || error.message || '网络异常，请稍后重试';
        ElMessage.error(msg);
      }
    }

    return Promise.reject(error);
  }
);

export default instance;
