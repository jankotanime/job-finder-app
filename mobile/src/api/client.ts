import { rotateTokens } from "../auth/tokens/rotateTokens";
import EncryptedStorage from "react-native-encrypted-storage";
import { tryCatch } from "../utils/try-catch";

let accessToken: string | null = null;
let refreshTokenValue: string | null = null;
let refreshTokenId: string | null = null;
let isRefreshing = false;
let refreshPromise: Promise<string | null> | null = null;
let rotationTimer: ReturnType<typeof setInterval> | null = null;

export const setTokensApiFetch = (tokens: {
  accessToken: string;
  refreshToken: string;
  refreshTokenId: string;
}) => {
  accessToken = tokens.accessToken;
  refreshTokenValue = tokens.refreshToken;
  refreshTokenId = tokens.refreshTokenId;
};

const ensureTokensFromStorage = async () => {
  if (refreshTokenValue && refreshTokenId) return;
  const [tokensRaw] = await tryCatch(EncryptedStorage.getItem("auth"));
  if (!tokensRaw) return;
  const parsed = JSON.parse(tokensRaw);
  accessToken = parsed.accessToken ?? null;
  refreshTokenValue = parsed.refreshToken ?? null;
  refreshTokenId = parsed.refreshTokenId ?? null;
};

export const refreshAccessToken = async (): Promise<string | null> => {
  await ensureTokensFromStorage();
  if (!refreshTokenValue || !refreshTokenId) return null;

  if (isRefreshing && refreshPromise) return refreshPromise;

  isRefreshing = true;
  refreshPromise = (async () => {
    try {
      const data = await rotateTokens({
        tokens: {
          refreshToken: refreshTokenValue!,
          refreshTokenId: refreshTokenId!,
        },
        setError: () => {},
        t: (x) => x,
      });
      if (!data || !data.data || !data.data.accessToken) {
        return null;
      }
      accessToken = data.data.accessToken ?? null;
      refreshTokenValue = data.data.refreshToken ?? refreshTokenValue;
      refreshTokenId = data.data.refreshTokenId ?? refreshTokenId;

      await EncryptedStorage.setItem(
        "auth",
        JSON.stringify({
          accessToken,
          refreshToken: refreshTokenValue,
          refreshTokenId,
        }),
      );

      if (accessToken && refreshTokenId && refreshTokenValue)
        setTokensApiFetch({
          accessToken,
          refreshToken: refreshTokenValue,
          refreshTokenId,
        });

      return accessToken;
    } catch (err) {
      accessToken = null;
      refreshTokenValue = null;
      refreshTokenId = null;
      return null;
    } finally {
      isRefreshing = false;
      refreshPromise = null;
    }
  })();

  return refreshPromise;
};

export const startTokenAutoRotate = (intervalMs: number = 5 * 60 * 1000) => {
  if (rotationTimer) return;
  rotationTimer = setInterval(() => {
    refreshAccessToken().catch((e) => console.warn("Auto-rotate failed", e));
  }, intervalMs);
};

export const stopTokenAutoRotate = () => {
  if (rotationTimer) {
    clearInterval(rotationTimer);
    rotationTimer = null;
  }
};
export const apiFetch = async (
  url: string,
  options: RequestInit = {},
  retry: boolean = true,
): Promise<{ response: Response; body: any }> => {
  const fetchWithToken = async (): Promise<Response> => {
    return fetch(`${process.env.EXPO_PUBLIC_API_URL}${url}`, {
      ...options,
      headers: {
        ...(options.headers || {}),
        Authorization: accessToken ? `Bearer ${accessToken}` : "",
        "Content-Type": "application/json",
      },
    });
  };
  let response: Response;
  try {
    response = await fetchWithToken();
  } catch (e) {
    const newToken = await refreshAccessToken();
    if (newToken) {
      try {
        response = await fetchWithToken();
      } catch {
        return {
          response: { ok: false, status: 0 } as Response,
          body: { code: "NETWORK_ERROR" },
        };
      }
    } else {
      return {
        response: { ok: false, status: 0 } as Response,
        body: { code: "NETWORK_ERROR" },
      };
    }
  }
  let body: any;
  try {
    body = await response.json();
  } catch {
    body = null;
  }

  if (
    (body?.code === "INVALID_ACCESS_TOKEN" || response.status === 401) &&
    retry
  ) {
    let newToken = await refreshAccessToken();
    if (!newToken) {
      try {
        await new Promise((r) => setTimeout(r, 150));
        newToken = await refreshAccessToken();
      } catch {}
    }
    if (newToken) {
      return apiFetch(url, options, false);
    }
  }
  return { response, body };
};
