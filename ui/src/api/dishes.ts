import { apiFetch } from './client';
import type { Dish, DishFormData, ImageSearchResult } from '../schemas/dish';

export function fetchAllDishes(): Promise<Dish[]> {
  return apiFetch<Dish[]>('/dish');
}

export function fetchDishById(id: string): Promise<Dish> {
  return apiFetch<Dish>(`/dish/${id}`);
}

export function searchDishImages(
  id: string,
  token: string,
  limitResults = 3,
): Promise<ImageSearchResult> {
  return apiFetch<ImageSearchResult>(
    `/dish/${id}/image-search?limitResults=${limitResults}`,
    { method: 'GET' },
    token,
  );
}

export function createDish(data: DishFormData, token: string): Promise<Dish> {
  return apiFetch<Dish>(
    '/dish',
    {
      method: 'POST',
      body: JSON.stringify(data),
    },
    token,
  );
}

export function updateDish(
  id: string,
  data: DishFormData,
  token: string,
): Promise<Dish> {
  return apiFetch<Dish>(
    `/dish/${id}`,
    {
      method: 'PUT',
      body: JSON.stringify(data),
    },
    token,
  );
}

export function updateDishImage(
  id: string,
  imageUrl: string,
  token: string,
): Promise<Dish> {
  return apiFetch<Dish>(
    `/dish/${id}`,
    {
      method: 'PATCH',
      body: JSON.stringify({ imageUrl }),
    },
    token,
  );
}

export function deleteDish(id: string, token: string): Promise<void> {
  return apiFetch<void>(
    `/dish/${id}`,
    {
      method: 'DELETE',
    },
    token,
  );
}
