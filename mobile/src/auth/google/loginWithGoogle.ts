import { tryCatch } from "../../utils/try-catch";
interface LoginProps {
  idToken: string | null;
  name: string | null;
}
export async function loginWithGoogle({ idToken, name }: LoginProps) {
  const [responseLogin, backendError] = await tryCatch(
    fetch(`${process.env.EXPO_PUBLIC_API_GOOGLE_AUTH_URL}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ googleToken: idToken }),
    }),
  );
  if (backendError || !responseLogin) {
    return { ok: false, error: backendError || responseLogin };
  }
  const [dataLogin, jsonError] = await tryCatch(responseLogin.json());
  if (jsonError || !responseLogin.ok) {
    return { ok: false, error: dataLogin };
  }
  return { ok: true, data: dataLogin, name };
}
