import http from './http';

export function fetchAnnouncementList() {
  return http.get('/announcement/list');
}

export function fetchAnnouncementDetail(id) {
  return http.get(`/announcement/${id}`);
}

export function likeAnnouncement(id, userId) {
  return http.post(`/announcement/${id}/like`, null, { params: { userId } });
}

export function unlikeAnnouncement(id, userId) {
  return http.delete(`/announcement/${id}/like`, { params: { userId } });
}

export function getAnnouncementStatus(id, userId) {
  return http.get(`/announcement/${id}/status`, { params: { userId } });
}

export function fetchCarouselList() {
  return http.get('/system/carousel/list');
}
