import http from './http';

export function fetchSurveyPage(params) {
  return http.get('/survey/page', { params });
}

export function fetchSurveyDetail(id) {
  return http.get(`/survey/${id}/detail`);
}

export function checkSurveySubmitted(id, userId) {
  return http.get(`/survey/${id}/check`, { params: { userId } });
}

export function submitSurveyAnswer(id, data) {
  return http.post(`/survey/${id}/answer`, data);
}

export function fetchSurveyStats(id) {
  return http.get(`/survey/${id}/stats`);
}
