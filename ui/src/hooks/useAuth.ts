import {
  useAuthStore,
  selectIsAuthenticated,
  selectIsAdmin,
  selectToken,
} from '../store/auth';

export function useAuth() {
  const token = useAuthStore(selectToken);
  const isAuthenticated = useAuthStore(selectIsAuthenticated);
  const isAdmin = useAuthStore(selectIsAdmin);
  const name = useAuthStore((s) => s.name);
  const setAuth = useAuthStore((s) => s.setAuth);
  const logout = useAuthStore((s) => s.logout);

  return { token, isAuthenticated, isAdmin, name, setAuth, logout };
}
