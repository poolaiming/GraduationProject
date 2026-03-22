import http from './http';

export function updateProfile(data) {
  return http.put('/user/profile', data);
}

export function changePassword(data) {
  return http.put('/user/password', data);
}

export function requestCancelAccount(data) {
  return http.put('/user/cancel-request', data);
}

export function registerJournalist(data) {
  return http.post('/user/journalist/register', data);
}
