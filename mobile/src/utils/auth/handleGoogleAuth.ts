import {
  GoogleSignin,
  isSuccessResponse,
} from "@react-native-google-signin/google-signin";
import { tryCatch } from "../try-catch";

interface GoogleLoginProps {
  setIsSubmiting: (value: boolean) => void;
  navigation: any;
}
// export async function checkUserExistence({
//   setIsSubmiting,
//   navigation,
// }: GoogleLoginProps) {
//   setIsSubmiting(true);

//   try {
//     await GoogleSignin.hasPlayServices();

//     let currentUser: any = await GoogleSignin.getCurrentUser();
//     let idToken: string | null = null;

//     if (currentUser) {
//       const tokens = await GoogleSignin.getTokens();
//       idToken = tokens?.idToken ?? null;
//     }

//     if (!idToken) {
//       const signInResult = await GoogleSignin.signIn();

//       if (!signInResult?.data) {
//         setIsSubmiting(false);
//         return { ok: false, error: "Brak danych logowania" };
//       }

//       idToken = signInResult.data.idToken ?? null;
//       currentUser = signInResult.data.user ?? null;
//     }

//     if (!idToken) {
//       setIsSubmiting(false);
//       return { ok: false, error: "Brak idToken" };
//     }

//     const response = await fetch(
//       `${process.env.EXPO_PUBLIC_API_GOOGLE_AUTH_URL}/check-user-existence`,
//       {
//         method: "POST",
//         headers: { "Content-Type": "application/json" },
//         body: JSON.stringify({ googleToken: idToken }),
//       }
//     );

//     const result = await response.json();

//     if (result.data.exist === "USER_NOT_EXIST") {
//       setIsSubmiting(false);
//       console.log("nie istnieje")
//       const { ok, data, name } =  await handleGoogleRegister({ setIsSubmiting, navigation });
//       return { ok, data, name }
//     }

//     if (result.data.exist === "USER_EXIST_WITH_EMAIL") {
//       setIsSubmiting(false);
//       return { ok: true, data: result };
//     }
//     console.log("chuj")
//     const { ok, data, name } = await handleGoogleLogin({ setIsSubmiting, navigation });
//     setIsSubmiting(false);
//     return { ok, data, name };

//   } catch (error: any) {
//     setIsSubmiting(false);
//     return { ok: false, error: error?.message ?? error };
//   }
// }

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
    return { ok: false, error: signInError || response };
  }
  if (!isSuccessResponse(response)) {
    setIsSubmiting(false);
    return { ok: false, error: response?.data };
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
    return { ok: false, error: backendError || responseLogin };
  }
  const [dataLogin, jsonError] = await tryCatch(responseLogin.json());
  if (jsonError || !responseLogin.ok) {
    setIsSubmiting(false);
    return { ok: false, error: dataLogin };
  }
  setIsSubmiting(false);
  navigation.navigate("Main", { name, email, photo });
  return { ok: true, data: dataLogin, name };
}
