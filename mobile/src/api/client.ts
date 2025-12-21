let accessToken: string | null = null;

export const setAccessToken = (token: string | null) => {
  accessToken = token;
};

export const apiFetch = async (url: string, options: RequestInit = {}) => {
  return fetch(`${process.env.EXPO_PUBLIC_API_URL}${url}`, {
    ...options,
    headers: {
      ...(options.headers || {}),
      Authorization: accessToken ? `Bearer ${accessToken}` : "",
      "Content-Type": "application/json",
    },
  });
};
