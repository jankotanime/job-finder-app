import {
  GoogleSignin,
  isSuccessResponse,
} from "@react-native-google-signin/google-signin";
import { tryCatch } from "../try-catch";

interface GoogleLoginProps {
  setIsSubmiting: (value: boolean) => void;
  navigation: any;
}
async function checkUserExistence(idToken: string | null) {
  const [response, error] = await tryCatch(
    fetch(
      `${process.env.EXPO_PUBLIC_API_GOOGLE_AUTH_URL}/check-user-existence`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ googleToken: idToken }),
      },
    ),
  );
  if (error) return { ok: false, error: error };
  const data = await response.json();
  if (data?.error) return { ok: false, error: data.code };
  return { ok: true, data: data };
}
export async function handleGoogleRegister({
  setIsSubmiting,
  navigation,
}: GoogleLoginProps) {
  setIsSubmiting(true);
  const [, playServicesError] = await tryCatch(GoogleSignin.hasPlayServices());
  if (playServicesError) {
    setIsSubmiting(false);
    return { ok: false, error: playServicesError };
  }
  const [response, signInError] = await tryCatch(GoogleSignin.signIn());
  if (signInError || !response) {
    setIsSubmiting(false);
    return { ok: false, error: signInError };
  }
  if (!isSuccessResponse(response)) {
    setIsSubmiting(false);
    return { ok: false, error: response.data };
  }
  const { idToken, user } = response.data;
  const { name, email, photo } = user;

  const {
    ok,
    data: dataCheckUser,
    error: error,
  } = await checkUserExistence(idToken);
  if (ok) {
    setIsSubmiting(false);
    return { ok: false, error: dataCheckUser };
  }
  const [responseBackend, backendError] = await tryCatch(
    fetch(`${process.env.EXPO_PUBLIC_API_GOOGLE_AUTH_URL}/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ googleToken: idToken }),
    }),
  );
  if (backendError || !responseBackend) {
    setIsSubmiting(false);
    return { ok: false, error: backendError };
  }
  const [data, jsonError] = await tryCatch(responseBackend.json());
  if (jsonError || !responseBackend.ok) {
    setIsSubmiting(false);
    return { ok: false, error: data };
  }
  setIsSubmiting(false);
  navigation.replace("ProfileCompletionGoogle", { name, email, photo });
  return { ok: true, data, name };
}

export async function handleGoogleLogin({
  setIsSubmiting,
  navigation,
}: GoogleLoginProps) {
  setIsSubmiting(true);
  const [, playServicesError] = await tryCatch(GoogleSignin.hasPlayServices());
  if (playServicesError) {
    setIsSubmiting(false);
    return { ok: false, error: playServicesError };
  }
  const [response, signInError] = await tryCatch(GoogleSignin.signIn());
  if (signInError || !response) {
    setIsSubmiting(false);
    return { ok: false, error: response };
  }
  if (!isSuccessResponse(response)) {
    setIsSubmiting(false);
    return;
  }
  const { idToken, user } = response.data;
  const { name, email, photo } = user;

  const [responseLogin, backendError] = await tryCatch(
    fetch(`${process.env.EXPO_PUBLIC_API_GOOGLE_AUTH_URL}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ googleToken: idToken }),
    }),
  );
  if (backendError || !responseLogin) {
    setIsSubmiting(false);
    return { ok: false, error: responseLogin };
  }
  const [dataLogin, jsonError] = await tryCatch(responseLogin.json());
  if (jsonError || !responseLogin.ok) {
    setIsSubmiting(false);
    return { ok: false, error: dataLogin };
  }
  setIsSubmiting(false);
  navigation.navigate("Main", { name, email, photo });
  return { ok: true, dataLogin, name };
}
