import http from './http';

export function createForumPost(data) {
  return http.post('/forum', data);
}

export function fetchForumPage(params) {
  return http.get('/forum/page', { params });
}

export function fetchMyForumPosts(params) {
  return http.get('/forum/my', { params });
}

export function updateForumPost(id, data) {
  return http.put(`/forum/${id}`, data);
}

export function deleteForumPost(id, userId) {
  return http.delete(`/forum/${id}`, { params: { userId } });
}

export function likeForumPost(id, userId) {
  return http.post(`/forum/${id}/like`, null, { params: { userId } });
}

export function unlikeForumPost(id, userId) {
  return http.delete(`/forum/${id}/like`, { params: { userId } });
}

export function fetchForumStatus(id, userId) {
  return http.get(`/forum/${id}/status`, { params: { userId } });
}

export function fetchForumComments(id) {
  return http.get(`/forum/${id}/comments`);
}

export function createForumComment(id, data) {
  return http.post(`/forum/${id}/comment`, data);
}

export function fetchMyLikedForumPosts(userId, size = 50) {
  return http.get('/forum/my-likes', { params: { userId, size } });
}
