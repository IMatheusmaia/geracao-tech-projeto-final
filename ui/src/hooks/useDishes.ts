import {
  useQuery,
  useMutation,
  useQueryClient,
} from '@tanstack/react-query';
import * as dishesApi from '../api/dishes';
import type { DishFormData } from '../schemas/dish';
import { useAuthStore, selectToken } from '../store/auth';

export const dishKeys = {
  all: ['dishes'] as const,
  lists: () => [...dishKeys.all, 'list'] as const,
  details: () => [...dishKeys.all, 'detail'] as const,
  detail: (id: string) => [...dishKeys.details(), id] as const,
  images: (id: string) => [...dishKeys.detail(id), 'images'] as const,
};

export function useDishes() {
  return useQuery({
    queryKey: dishKeys.lists(),
    queryFn: dishesApi.fetchAllDishes,
  });
}

export function useDish(id: string) {
  return useQuery({
    queryKey: dishKeys.detail(id),
    queryFn: () => dishesApi.fetchDishById(id),
  });
}

export function useDishImageSearch(id: string) {
  const token = useAuthStore(selectToken);
  return useQuery({
    queryKey: dishKeys.images(id),
    queryFn: () => dishesApi.searchDishImages(id, token!),
    enabled: false,
  });
}

export function useCreateDish() {
  const queryClient = useQueryClient();
  const token = useAuthStore(selectToken);

  return useMutation({
    mutationFn: (data: DishFormData) => dishesApi.createDish(data, token!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dishKeys.lists() });
    },
  });
}

export function useUpdateDish() {
  const queryClient = useQueryClient();
  const token = useAuthStore(selectToken);

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: DishFormData }) =>
      dishesApi.updateDish(id, data, token!),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: dishKeys.lists() });
      queryClient.invalidateQueries({
        queryKey: dishKeys.detail(variables.id),
      });
    },
  });
}

export function useUpdateDishImage() {
  const queryClient = useQueryClient();
  const token = useAuthStore(selectToken);

  return useMutation({
    mutationFn: ({ id, imageUrl }: { id: string; imageUrl: string }) =>
      dishesApi.updateDishImage(id, imageUrl, token!),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({
        queryKey: dishKeys.detail(variables.id),
      });
      queryClient.invalidateQueries({ queryKey: dishKeys.lists() });
    },
  });
}

export function useDeleteDish() {
  const queryClient = useQueryClient();
  const token = useAuthStore(selectToken);

  return useMutation({
    mutationFn: (id: string) => dishesApi.deleteDish(id, token!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dishKeys.lists() });
    },
  });
}
