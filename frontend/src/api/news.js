import http from './http';

export function fetchNewsPage(params) {
  return http.get('/news/page', { params });
}

export function fetchNewsTags() {
  return http.get('/news/tags');
}

export function fetchNewsCategories() {
  return http.get('/news/categories');
}

export function fetchTagsByNewsId(id) {
  return http.get(`/news/${id}/tags`);
}

export function fetchNewsDetail(id, userId) {
  return http.get(`/news/${id}`, { params: { userId } });
}

export function fetchRecommendNews(userId, size = 6) {
  return http.get('/news/recommend', { params: { userId, size } });
}

export function fetchNewsStatus(id, userId) {
  return http.get(`/news/${id}/status`, { params: { userId } });
}

export function likeNews(id, userId) {
  return http.post(`/news/${id}/like`, null, { params: { userId } });
}

export function unlikeNews(id, userId) {
  return http.delete(`/news/${id}/like`, { params: { userId } });
}

export function collectNews(id, userId) {
  return http.post(`/news/${id}/collect`, null, { params: { userId } });
}

export function uncollectNews(id, userId) {
  return http.delete(`/news/${id}/collect`, { params: { userId } });
}

export function fetchNewsComments(id) {
  return http.get(`/news/${id}/comments`);
}

export function createNewsComment(id, data) {
  return http.post(`/news/${id}/comment`, data);
}

export function fetchBrowseHistory(userId, size = 20) {
  return http.get('/news/history', { params: { userId, size } });
}

export function fetchMyLikedNews(userId, size = 50) {
  return http.get('/news/my-likes', { params: { userId, size } });
}

export function fetchMyCollectedNews(userId, size = 50) {
  return http.get('/news/my-collects', { params: { userId, size } });
}

export function fetchMyNews(params) {
  return http.get('/news/page', { params });
}

export function createNews(data) {
  return http.post('/news', data);
}

export function updateNews(id, data) {
  return http.put(`/news/${id}`, data);
}

export function deleteNews(id) {
  return http.delete(`/news/${id}`);
}
