import {
  GoogleSignin,
  isSuccessResponse,
  isErrorWithCode,
  statusCodes,
} from "@react-native-google-signin/google-signin";

interface GoogleLoginProps {
  setIsSubmiting: (value: boolean) => void;
  navigation: any;
}
export async function handleGoogleRegister({
  setIsSubmiting,
  navigation,
}: GoogleLoginProps) {
  try {
    setIsSubmiting(true);
    await GoogleSignin.hasPlayServices();
    const response = await GoogleSignin.signIn();
    if (isSuccessResponse(response)) {
      const { idToken, user } = response.data;
      const { name, email, photo } = user;
      try {
        const responseBackend = await fetch(
          `${process.env.EXPO_PUBLIC_API_URL}/auth/google-auth/ios/register`,
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              googleToken: idToken,
            }),
          },
        );
        const data = await responseBackend.json();
        if (!responseBackend.ok) {
          console.error("error while fetching backend: ", data);
          return { data, name };
        }
        setIsSubmiting(false);
        navigation.navigate("Main", { name, email, photo });
        return { data, name };
      } catch (e) {
        console.error("error while trying to connect to backend: ", e);
      }
    }
    setIsSubmiting(false);
  } catch (e) {
    console.error("error while trying to register with google: ", e);
    setIsSubmiting(false);
  }
}

export async function handleGoogleLogin({
  setIsSubmiting,
  navigation,
}: GoogleLoginProps) {
  try {
    setIsSubmiting(true);
    await GoogleSignin.hasPlayServices();
    const response = await GoogleSignin.signIn();
    if (isSuccessResponse(response)) {
      const { idToken, user } = response.data;
      const { name, email, photo } = user;
      try {
        const responseLogin = await fetch(
          `${process.env.EXPO_PUBLIC_API_URL}/auth/google-auth/ios/login`,
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              googleToken: idToken,
            }),
          },
        );
        const dataLogin = await responseLogin.json();
        if (!responseLogin.ok) {
          console.error("error while logging backend: ", dataLogin);
          return { dataLogin, name };
        }
        setIsSubmiting(false);
        navigation.navigate("Main", { name, email, photo });
        return { dataLogin, name };
      } catch (e) {
        console.error("error while trying to connect to backend: ", e);
      }
    }
    setIsSubmiting(false);
  } catch (e) {
    console.error("error while trying to log in with google: ", e);
    setIsSubmiting(false);
  }
}
