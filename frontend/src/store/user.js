import { defineStore } from 'pinia';

const STORAGE_KEY = 'user';

function loadUser() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

function syncUserStorage(user) {
  if (user) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    return;
  }
  localStorage.removeItem(STORAGE_KEY);
}

export const useUserStore = defineStore('user', {
  state: () => ({
    user: loadUser()
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.user?.id),
    userId: (state) => state.user?.id ?? null,
    displayName: (state) => state.user?.nickname || state.user?.username || ''
  },
  actions: {
    setUser(user) {
      this.user = user || null;
      syncUserStorage(this.user);
    },
    logout() {
      this.setUser(null);
    }
  }
});

export function setUser(user) {
  useUserStore().setUser(user);
}

export function logout() {
  useUserStore().logout();
}
