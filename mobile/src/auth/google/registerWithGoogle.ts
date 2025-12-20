import { tryCatch } from "../../utils/try-catch";
interface RegisterProps {
  idToken: string | null;
  username: string | null;
  phoneNumber: string | null;
}
export async function registerWithGoogle({
  idToken,
  username,
  phoneNumber,
}: RegisterProps) {
  const [responseBackend, backendError] = await tryCatch(
    fetch(`${process.env.EXPO_PUBLIC_API_GOOGLE_AUTH_URL}/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ googleToken: idToken, username, phoneNumber }),
    }),
  );
  if (backendError || !responseBackend) {
    return { ok: false, error: backendError };
  }
  const data = await responseBackend.json();
  if (!data) {
    return { ok: false, error: data };
  }
  return { ok: true, data: data.data, name: username };
}
