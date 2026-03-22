import { useAdminStore } from '../store/admin';

const STORAGE_KEY = 'admin';

function loadAdminFromStorage() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

function removeAdminFromStorage() {
  localStorage.removeItem(STORAGE_KEY);
}

function getAdminStore() {
  try {
    return useAdminStore();
  } catch {
    return null;
  }
}

export function getUser() {
  const adminStore = getAdminStore();
  return adminStore?.admin ?? loadAdminFromStorage();
}

export function clearUser() {
  const adminStore = getAdminStore();
  if (adminStore) {
    adminStore.logout();
    return;
  }
  removeAdminFromStorage();
}

export function getLoginPath() {
  return '/login';
}
