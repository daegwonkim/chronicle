import { apiRequest } from './client';

export interface Project {
  id: number;
  name: string;
  description: string;
}

export interface GetProjectsResponse {
  projects: Project[];
  totalCount: number;
}

interface CreateProjectResponse {
  apiKey: string;
}

export function getProjects(params: { query?: string; page?: number; size?: number } = {}) {
  const searchParams = new URLSearchParams();
  if (params.query) searchParams.set('query', params.query);
  searchParams.set('page', String(params.page ?? 0));
  searchParams.set('size', String(params.size ?? 10));

  const qs = searchParams.toString();
  return apiRequest<GetProjectsResponse>(`/projects?${qs}`);
}

export function createProject(data: { name: string; description: string }) {
  return apiRequest<CreateProjectResponse>('/projects', { method: 'POST', body: data });
}

export function deleteProject(id: number) {
  return apiRequest<void>(`/projects/${id}`, { method: 'DELETE' });
}
