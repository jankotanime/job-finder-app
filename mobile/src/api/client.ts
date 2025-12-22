import { rotateTokens } from "../auth/tokens/rotateTokens";

let accessToken: string | null = null;
let refreshTokenValue: string | null = null;
let refreshTokenId: string | null = null;

let isRefreshing = false;
let refreshPromise: Promise<string | null> | null = null;

export const setTokensApiFetch = (tokens: {
  accessToken: string;
  refreshToken: string;
  refreshTokenId: string;
}) => {
  accessToken = tokens.accessToken;
  refreshTokenValue = tokens.refreshToken;
  refreshTokenId = tokens.refreshTokenId;
};

export const setAccessToken = (token: string | null) => {
  accessToken = token;
};

export const apiFetch = async (
  url: string,
  options: RequestInit = {},
): Promise<Response> => {
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

  //! fetch interceptor
  const refreshTokenFunc = async (): Promise<string | null> => {
    if (isRefreshing && refreshPromise) return refreshPromise;
    if (!refreshTokenValue || !refreshTokenId) {
      console.error("No tokens for refresh");
      return null;
    }
    isRefreshing = true;
    refreshPromise = (async () => {
      try {
        const data = await rotateTokens({
          tokens: { refreshToken: refreshTokenValue, refreshTokenId },
          setError: (err: string) => console.error(err),
          t: (text: string) => text,
        });
        if (!data?.data?.accessToken) throw new Error("Refresh failed");
        setAccessToken(data.data.accessToken);
        return data.data.accessToken;
      } catch (err) {
        setAccessToken(null);
        refreshTokenValue = null;
        refreshTokenId = null;
        console.error("Refresh failed", err);
        return null;
      } finally {
        isRefreshing = false;
        refreshPromise = null;
      }
    })();
    return refreshPromise;
  };

  let response = await fetchWithToken();
  if (response.status === 401) {
    const newToken = await refreshTokenFunc();
    if (newToken) {
      response = await fetchWithToken();
    }
  }
  return response;
};
