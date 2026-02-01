import { apiRequest } from './client';

interface AuthRequest {
  username: string;
  password: string;
}

export function signup(data: AuthRequest) {
  return apiRequest<void>('/admins/sign-up', { method: 'POST', body: data });
}

export function signin(data: AuthRequest) {
  return apiRequest<void>('/admins/sign-in', { method: 'POST', body: data });
}
