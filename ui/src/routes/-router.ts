import {
  createRouter,
  createRoute,
  redirect,
} from '@tanstack/react-router';
import { QueryClient } from '@tanstack/react-query';
import { rootRoute } from './__root';
import { useAuthStore } from '../store/auth';

import { MenuPage } from '../pages/Menu/MenuPage';
import { LoginPage } from '../pages/Login/LoginPage';
import { AdminLayout } from '../pages/Admin/AdminLayout';
import { AdminDishList } from '../pages/Admin/AdminDishList';

const menuRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/',
  component: MenuPage,
});

const loginRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/login',
  component: LoginPage,
});

const adminRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/admin',
  component: AdminLayout,
  beforeLoad: () => {
    const { token } = useAuthStore.getState();
    if (!token) {
      throw redirect({ to: '/login' });
    }
  },
});

const adminDishesRoute = createRoute({
  getParentRoute: () => adminRoute,
  path: '/',
  component: AdminDishList,
});

const routeTree = rootRoute.addChildren([
  menuRoute,
  loginRoute,
  adminRoute.addChildren([adminDishesRoute]),
]);

export function createAppRouter(queryClient: QueryClient) {
  return createRouter({
    routeTree,
    context: { queryClient },
    defaultPreload: 'intent',
  });
}

declare module '@tanstack/react-router' {
  interface Register {
    router: ReturnType<typeof createAppRouter>;
  }
}
