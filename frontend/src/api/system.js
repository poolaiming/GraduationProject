import http from './http';

// 获取友情链接列表
export function getFriendLinkList() {
  return http.get('/system/friend-link/list');
}

// 获取关于我们信息
export function getAbout() {
  return http.get('/system/about');
}
