import { apiFetch } from './client';
import type { LoginFormData, LoginResponse } from '../schemas/auth';

export function loginRequest(data: LoginFormData): Promise<LoginResponse> {
  return apiFetch<LoginResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}
