import { useUserStore } from '../store/user';

const STORAGE_KEY = 'user';

function loadUserFromStorage() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

function removeUserFromStorage() {
  localStorage.removeItem(STORAGE_KEY);
}

function getUserStore() {
  try {
    return useUserStore();
  } catch {
    return null;
  }
}

export function getUser() {
  const userStore = getUserStore();
  return userStore?.user ?? loadUserFromStorage();
}

export function clearUser() {
  const userStore = getUserStore();
  if (userStore) {
    userStore.logout();
    return;
  }
  removeUserFromStorage();
}

export function getLoginPath() {
  return '/login';
}
