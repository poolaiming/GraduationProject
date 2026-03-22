import http from './http';

export function fetchCaptcha() {
  return http.get('/auth/captcha');
}

export function login(data) {
  return http.post('/auth/login', data);
}

export function register(data) {
  return http.post('/auth/register', data);
}
