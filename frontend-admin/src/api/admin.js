import http from './http';

export function adminLogin(data) {
  return http.post('/admin/auth/login', data);
}

export function getDashboard() {
  return http.get('/admin/statistics/dashboard');
}

export function getInteractionTrend(days = 7) {
  return http.get('/admin/statistics/interaction-trend', { params: { days } });
}

export const adminUser = {
  page: (params) => http.get('/admin/user/page', { params }),
  updateStatus: (id, status) => http.put(`/admin/user/${id}/status`, null, { params: { status } }),
  updateJournalistStatus: (id, journalistStatus) => http.put(`/admin/user/${id}/journalist-status`, null, { params: { journalistStatus } }),
  delete: (id) => http.delete(`/admin/user/${id}`)
};

export const adminNewsCategory = {
  list: () => http.get('/admin/news-category/list'),
  create: (data) => http.post('/admin/news-category', data),
  update: (id, data) => http.put(`/admin/news-category/${id}`, data),
  delete: (id) => http.delete(`/admin/news-category/${id}`)
};

export const adminNews = {
  page: (params) => http.get('/admin/news/page', { params }),
  get: (id) => http.get(`/admin/news/${id}`),
  listTags: () => http.get('/news/tags'),
  getTags: (id) => http.get(`/news/${id}/tags`),
  create: (data) => http.post('/admin/news', data),
  importBatch: (list) => http.post('/admin/news/import', list),
  update: (id, data) => http.put(`/admin/news/${id}`, data),
  updateStatus: (id, status, reviewRemark) => http.put(`/admin/news/${id}/status`, null, { params: { status, reviewRemark } }),
  delete: (id) => http.delete(`/admin/news/${id}`)
};

export const adminForum = {
  page: (params) => http.get('/admin/forum/page', { params }),
  setTop: (id, isTop) => http.put(`/admin/forum/${id}/top`, null, { params: { isTop } }),
  updateStatus: (id, status, reviewRemark) => http.put(`/admin/forum/${id}/status`, null, { params: { status, reviewRemark } }),
  delete: (id) => http.delete(`/admin/forum/${id}`),
  comments: (id, params) => http.get(`/admin/forum/${id}/comments`, { params }),
  deleteComment: (id) => http.delete(`/admin/forum/comment/${id}`)
};

export const adminSurvey = {
  page: (params) => http.get('/admin/survey/page', { params }),
  create: (data) => http.post('/admin/survey', data),
  update: (id, data) => http.put(`/admin/survey/${id}`, data),
  delete: (id) => http.delete(`/admin/survey/${id}`),
  getQuestions: (id) => http.get(`/admin/survey/${id}/questions`),
  createQuestion: (id, data) => http.post(`/admin/survey/${id}/questions`, data),
  updateQuestion: (questionId, data) => http.put(`/admin/survey/question/${questionId}`, data),
  deleteQuestion: (questionId) => http.delete(`/admin/survey/question/${questionId}`),
  createOption: (questionId, data) => http.post(`/admin/survey/question/${questionId}/options`, data),
  updateOption: (optionId, data) => http.put(`/admin/survey/option/${optionId}`, data),
  deleteOption: (optionId) => http.delete(`/admin/survey/option/${optionId}`),
  getStats: (id) => http.get(`/admin/survey/${id}/stats`)
};

export const adminMessage = {
  page: (params) => http.get('/admin/message/page', { params }),
  reply: (id, data) => http.put(`/admin/message/${id}/reply`, data)
};

export const adminSystem = {
  getAbout: () => http.get('/admin/system/about'),
  saveAbout: (data) => http.put('/admin/system/about', data),
  listCarousel: () => http.get('/admin/system/carousel/list'),
  createCarousel: (data) => http.post('/admin/system/carousel', data),
  updateCarousel: (id, data) => http.put(`/admin/system/carousel/${id}`, data),
  deleteCarousel: (id) => http.delete(`/admin/system/carousel/${id}`),
  listFriendLink: () => http.get('/admin/system/friend-link/list'),
  createFriendLink: (data) => http.post('/admin/system/friend-link', data),
  updateFriendLink: (id, data) => http.put(`/admin/system/friend-link/${id}`, data),
  deleteFriendLink: (id) => http.delete(`/admin/system/friend-link/${id}`)
};

export const adminAnnouncement = {
  page: (params) => http.get('/admin/announcement/page', { params }),
  create: (data) => http.post('/admin/announcement', data),
  update: (id, data) => http.put(`/admin/announcement/${id}`, data),
  setTop: (id, isTop) => http.put(`/admin/announcement/${id}/top`, null, { params: { isTop } }),
  delete: (id) => http.delete(`/admin/announcement/${id}`)
};
