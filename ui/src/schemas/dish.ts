import { z } from 'zod';

// Matches the backend DishEntity JSON (snake_case from @JsonProperty)
export const dishSchema = z.object({
  id: z.string(),
  title: z.string(),
  description: z.string(),
  category: z.string(),
  price: z.number(),
  image_url: z.string().nullable().default(null),
  ingredients: z.array(z.string()),
  saved_at: z.string(),
  updated_at: z.string(),
});

export type Dish = z.infer<typeof dishSchema>;

// Matches DishRequest record on the backend (camelCase)
export const dishFormSchema = z.object({
  title: z.string().min(1, 'Título é obrigatório'),
  description: z.string().min(1, 'Descrição é obrigatória'),
  category: z.string().min(1, 'Categoria é obrigatória'),
  price: z.coerce
    .number({ invalid_type_error: 'Preço deve ser um número' })
    .positive('Preço deve ser positivo'),
  imageUrl: z.string().url('URL inválida').or(z.literal('')),
  ingredients: z.array(z.string().min(1)).min(1, 'Ao menos um ingrediente'),
});

export type DishFormData = z.infer<typeof dishFormSchema>;

// For the PATCH imageUrl update
export const updateImageUrlSchema = z.object({
  imageUrl: z.string().url('Selecione uma imagem válida'),
});

// Image search response
export const imageSearchResultSchema = z.object({
  images: z.array(
    z.object({
      image_url: z.string().url(),
    }),
  ),
});

export type ImageSearchResult = z.infer<typeof imageSearchResultSchema>;
