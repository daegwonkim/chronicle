const API_BASE_URL = 'http://localhost:8080/v1/api';

interface RequestOptions {
  method: string;
  headers: Record<string, string>;
  body?: string;
}

export async function apiRequest<T>(
  path: string,
  options: { method?: string; body?: unknown; headers?: Record<string, string> } = {},
): Promise<T | undefined> {
  const { method = 'GET', body, headers } = options;

  const requestOptions: RequestOptions = {
    method,
    headers: { 'Content-Type': 'application/json', ...headers },
  };

  if (body) {
    requestOptions.body = JSON.stringify(body);
  }

  const response = await fetch(`${API_BASE_URL}${path}`, requestOptions);

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || `Request failed with status ${response.status}`);
  }

  const text = await response.text();
  return text ? JSON.parse(text) : undefined;
}
