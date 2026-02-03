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
  hasNext: boolean;
  estimatedCount: number | null;
}

export interface SearchLogsParams {
  appIds?: number[];
  from?: string;
  to?: string;
  logLevels?: LogLevel[];
  query?: string;
  cursorId?: number | null;
  size?: number;
}

export function searchLogs(params: SearchLogsParams = {}) {
  const searchParams = new URLSearchParams();

  if (params.appIds && params.appIds.length > 0) {
    params.appIds.forEach((id) => searchParams.append('appIds', String(id)));
  }
  if (params.from) searchParams.set('timeRange.from', params.from);
  if (params.to) searchParams.set('timeRange.to', params.to);
  if (params.logLevels && params.logLevels.length > 0) {
    params.logLevels.forEach((level) => searchParams.append('logLevels', level));
  }
  if (params.query) searchParams.set('query', params.query);
  if (params.cursorId != null) searchParams.set('cursorId', String(params.cursorId));
  searchParams.set('size', String(params.size ?? 20));

  const qs = searchParams.toString();
  return apiRequest<SearchLogsResponse>(`/logs?${qs}`, {
    headers: { 'X-Api-Key': 'dashboard' },
  });
}
