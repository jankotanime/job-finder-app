import { tryCatch } from "../try-catch";

interface LoginProps {
  loginData: string;
  password: string;
}
export const login = async ({ loginData, password }: LoginProps) => {
  const [response, error] = await tryCatch(
    fetch(`${process.env.EXPO_PUBLIC_API_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ loginData, password }),
      credentials: "include",
    }),
  );
  const data = await response?.json();
  if (error || !response) return { error: error?.message || String(error) };
  if (!response.ok) {
    return {
      error:
        data?.message ||
        data?.err ||
        `Error ${response.status}: ${response.statusText || "Unknown error"}`,
    };
  }
  return data;
};
