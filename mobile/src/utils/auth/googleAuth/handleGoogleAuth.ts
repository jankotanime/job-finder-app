import {
  GoogleSignin,
  isSuccessResponse,
} from "@react-native-google-signin/google-signin";
import { tryCatch } from "../../try-catch";

interface GoogleLoginProps {
  setIsSubmiting: (value: boolean) => void;
  navigation: any;
}
async function checkUserExistence(idToken: string | null) {
  const response = await fetch(
    `${process.env.EXPO_PUBLIC_API_GOOGLE_AUTH_URL}/check-user-existence`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ googleToken: idToken }),
    },
  );
  const result = await response.json();
  if (result.data.exist === "USER_NOT_EXIST") {
    return "USER_NOT_EXIST";
  }
  if (result.data.exist === "USER_EXIST_WITH_EMAIL") {
    return "USER_EXIST_WITH_EMAIL";
  }
  return "USER_EXIST";
}

export async function handleGoogleAuth({
  setIsSubmiting,
  navigation,
}: GoogleLoginProps) {
  setIsSubmiting(true);
  await GoogleSignin.hasPlayServices();
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

  const userExists: string = await checkUserExistence(idToken);
  if (userExists === "USER_NOT_EXIST") {
    console.log("USER_NOT_EXIST");
    navigation.navigate("ProfileCompletionGoogle", {
      idToken,
      onRegisterSuccess: async (username: string, phoneNumber: string) => {
        const response = await fetch(
          `${process.env.EXPO_PUBLIC_API_GOOGLE_AUTH_URL}/register`,
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              googleToken: idToken,
              username,
              phoneNumber,
            }),
          },
        );
        const data = await response.json();
        console.log("REGISTER SUCCESS", data);
        navigation.replace("ProfileCompletion");
        return { ok: true, data, name };
      },
    });
  } else if (userExists === "USER_EXIST") {
    console.log("USER_EXIST");
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
  } else {
    console.log("USER_EXIST_WITH_EMAIL");
    return { ok: false };
  }
}
