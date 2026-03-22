import { defineStore } from 'pinia';

const STORAGE_KEY = 'admin';

function loadAdmin() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

function syncAdminStorage(admin) {
  if (admin) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(admin));
    return;
  }
  localStorage.removeItem(STORAGE_KEY);
}

export const useAdminStore = defineStore('admin', {
  state: () => ({
    admin: loadAdmin()
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.admin?.id),
    displayName: (state) => state.admin?.nickname || state.admin?.username || ''
  },
  actions: {
    setAdmin(admin) {
      this.admin = admin || null;
      syncAdminStorage(this.admin);
    },
    logout() {
      this.setAdmin(null);
    }
  }
});

export function setAdmin(admin) {
  useAdminStore().setAdmin(admin);
}

export function logoutAdmin() {
  useAdminStore().logout();
}
