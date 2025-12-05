import { tryCatch } from "../try-catch";

interface RegisterProps {
  username: string;
  email: string;
  password: string;
  phoneNumber: string;
}
export const register = async ({
  username,
  email,
  password,
  phoneNumber,
}: RegisterProps) => {
  const [response, error] = await tryCatch(
    fetch(`${process.env.EXPO_PUBLIC_API_URL}/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, email, password, phoneNumber }),
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
