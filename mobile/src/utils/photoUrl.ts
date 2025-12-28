export const getCloudflareBase = (): string | undefined => {
  const envBase = process.env.EXPO_PUBLIC_API_CLOUDFLARE_URL;
  if (typeof envBase !== "string" || envBase.length === 0) return undefined;
  return envBase;
};

const isAbsoluteUrl = (s?: string): boolean => {
  if (!s) return false;
  return /^[a-z][a-z0-9+\-.]*:/i.test(s) || /^\/\//.test(s);
};

export const buildPhotoUrl = (path?: string): string | undefined => {
  if (!path || isAbsoluteUrl(path)) return path;
  const base = getCloudflareBase();
  if (!base) return path;
  const cleanBase = base.endsWith("/") ? base.slice(0, -1) : base;
  const cleanPath = path.startsWith("/") ? path : `/${path}`;
  return `${cleanBase}${cleanPath}`;
};
