import { apiRequest } from './client';

interface AuthRequest {
  username: string;
  password: string;
}

export function signup(data: AuthRequest) {
  return apiRequest<void>('/admins/signup', { method: 'POST', body: data });
}

export function signin(data: AuthRequest) {
  return apiRequest<void>('/admins/signin', { method: 'POST', body: data });
}
