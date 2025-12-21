import {
  GoogleSignin,
  isSuccessResponse,
} from "@react-native-google-signin/google-signin";
import { tryCatch } from "../../utils/try-catch";

export async function handleGoogleAuth() {
  await GoogleSignin.hasPlayServices();
  const [response, signInError] = await tryCatch(GoogleSignin.signIn());
  if (signInError || !response) {
    return { ok: false, error: signInError };
  }
  if (!isSuccessResponse(response)) {
    return { ok: false, error: response.data };
  }
  const { idToken, user } = response.data;
  const { name } = user;
  return { idToken, user, name };
}
