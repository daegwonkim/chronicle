import { apiRequest } from './client';

export type LogLevel = 'TRACE' | 'DEBUG' | 'INFO' | 'WARN' | 'ERROR';

export interface Log {
  id: number;
  appName: string;
  level: LogLevel;
  message: string;
  logger: string;
  loggedAt: string;
}

export interface SearchLogsResponse {
  logs: Log[];
  totalCount: number;
}

export interface SearchLogsParams {
  appIds?: number[];
  from?: string;
  to?: string;
  logLevel?: LogLevel;
  query?: string;
  page?: number;
  size?: number;
}

export function searchLogs(params: SearchLogsParams = {}) {
  const searchParams = new URLSearchParams();

  if (params.appIds && params.appIds.length > 0) {
    params.appIds.forEach((id) => searchParams.append('appIds', String(id)));
  }
  if (params.from) searchParams.set('timeRange.from', params.from);
  if (params.to) searchParams.set('timeRange.to', params.to);
  if (params.logLevel) searchParams.set('logLevel', params.logLevel);
  if (params.query) searchParams.set('query', params.query);
  searchParams.set('page', String(params.page ?? 0));
  searchParams.set('size', String(params.size ?? 20));

  const qs = searchParams.toString();
  return apiRequest<SearchLogsResponse>(`/logs?${qs}`, {
    headers: { 'X-Api-Key': 'dashboard' },
  });
}
