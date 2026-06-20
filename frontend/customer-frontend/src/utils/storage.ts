// src / utils / storage.ts
export const storage = {
  get: (k: string) => localStorage.getItem(k),
  set: (k: string, v: string) => localStorage.setItem(k, v),
  remove: (k: string) => localStorage.removeItem(k),
};