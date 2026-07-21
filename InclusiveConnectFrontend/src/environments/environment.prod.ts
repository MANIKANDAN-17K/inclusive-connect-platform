export const environment = {
  production: true,
  // Nginx proxies /api → backend container, so relative path works in any deployment
  apiUrl: '/api/v1',
  wsUrl: '/ws',
};
