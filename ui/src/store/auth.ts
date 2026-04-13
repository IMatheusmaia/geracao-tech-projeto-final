import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { LoginResponse } from '../schemas/auth';

interface AuthState {
  token: string | null;
  name: string | null;
  email: string | null;
  role: 'USER' | 'ADMIN' | null;
  setAuth: (response: LoginResponse) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      name: null,
      email: null,
      role: null,
      setAuth: (response: LoginResponse) =>
        set({
          token: response.token,
          name: response.name,
          email: response.email,
          role: response.role,
        }),
      logout: () =>
        set({
          token: null,
          name: null,
          email: null,
          role: null,
        }),
    }),
    {
      name: 'smart-menu-auth',
      partialize: (state) => ({
        token: state.token,
        name: state.name,
        email: state.email,
        role: state.role,
      }),
    },
  ),
);

// Selectors
export const selectIsAuthenticated = (s: AuthState) => s.token !== null;
export const selectIsAdmin = (s: AuthState) => s.role === 'ADMIN';
export const selectToken = (s: AuthState) => s.token;
