import http from './http';

// 创建在线留言
export function createMessage(data) {
  return http.post('/message', data);
}

// 分页查询当前用户自己的留言
export function fetchMyMessages(params) {
  return http.get('/message/my', { params });
}

// 修改本人发布的留言（仅未处理）
export function updateMessage(id, data) {
  return http.put(`/message/${id}`, data);
}

