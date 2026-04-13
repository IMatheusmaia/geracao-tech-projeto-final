import { z } from 'zod';

export const loginSchema = z.object({
  email: z.string().email('Email inválido'),
  password: z.string().min(1, 'Senha é obrigatória'),
});

export type LoginFormData = z.infer<typeof loginSchema>;

export const loginResponseSchema = z.object({
  token: z.string(),
  name: z.string(),
  email: z.string(),
  role: z.enum(['USER', 'ADMIN']),
});

export type LoginResponse = z.infer<typeof loginResponseSchema>;
