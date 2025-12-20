import { AuthStatus } from "../../enums/authStatus";

export async function checkUserExistence(idToken: string | null) {
  const response = await fetch(
    `${process.env.EXPO_PUBLIC_API_GOOGLE_AUTH_URL}/check-user-existence`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ googleToken: idToken }),
    },
  );
  const result = await response.json();
  if (result.data.exist === AuthStatus.USER_NOT_EXIST) {
    return AuthStatus.USER_NOT_EXIST;
  }
  if (result.data.exist === AuthStatus.USER_EXIST_WITH_EMAIL) {
    return AuthStatus.USER_EXIST_WITH_EMAIL;
  }
  return AuthStatus.USER_EXIST;
}
