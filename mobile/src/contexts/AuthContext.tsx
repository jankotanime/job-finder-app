import React, {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode,
} from "react";
import EncryptedStorage from "react-native-encrypted-storage";
import getUsernameFromAccessToken from "../auth/tokens/getUsernameFromAccessToken";
import { tryCatch } from "../utils/try-catch";
import { login } from "../auth/app/login";
import { register } from "../auth/app/register";
import { extractTokens } from "../auth/tokens/extractTokens";
import { useTranslation } from "react-i18next";
import { handleGoogleAuth } from "../auth/google/handleGoogleAuth";
import { SignWithGoogleResult } from "../types/SignWithGoogleResult";
import { checkUserExistence } from "../auth/google/checkUserExistence";
import { loginWithGoogle } from "../auth/google/loginWithGoogle";
import { registerWithGoogle } from "../auth/google/registerWithGoogle";

type AuthContextType = {
  user: string;
  loading: boolean;
  isAuthenticated: boolean;
  pendingGoogleIdToken: string | null;
  signIn: (
    formState: FormStateLoginProps,
  ) => Promise<{ ok: boolean; error?: string }>;
  signOut: () => Promise<void>;
  signUp: (
    formState: FormStateRegisterProps,
  ) => Promise<{ ok: boolean; error?: string }>;
  refreshAuth: () => Promise<void>;
  signWithGoogle: (
    formState: GoogleLoginProps,
  ) => Promise<SignWithGoogleResult>;
  completeGoogleRegistration: (
    idToken: string,
    username: string,
    phoneNumber: string,
  ) => Promise<
    | {
        status: string;
        error: any;
      }
    | {
        status: string;
        error?: undefined;
      }
  >;
};
interface FormStateLoginProps {
  loginData: string;
  password: string;
}
interface FormStateRegisterProps {
  username: string;
  email: string;
  password: string;
  phoneNumber: string;
}
interface GoogleLoginProps {
  setIsSubmiting: (value: boolean) => void;
  navigation: any;
}
const AuthContext = createContext<AuthContextType>({
  user: "",
  loading: true,
  isAuthenticated: false,
  pendingGoogleIdToken: "",
  signIn: async () => ({ ok: false, error: "not-initialized" }),
  signOut: async () => {},
  signUp: async () => ({ ok: false, error: "not-initialized" }),
  refreshAuth: async () => {},
  signWithGoogle: async () => ({ status: "ERROR", error: "not-initialized" }),
  completeGoogleRegistration: async () => ({
    status: "ERROR",
    error: "not-initialized",
  }),
});
export const useAuth = () => useContext(AuthContext);
export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(true);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [tokens, setTokens] = useState<{
    accessToken: string;
    refreshToken: string;
    refreshTokenId: string;
  } | null>(null);
  const [pendingGoogleIdToken, setPendingGoogleIdToken] = useState<
    string | null
  >("");
  const { t } = useTranslation();

  useEffect(() => {
    loadTokens();
  }, []);
  const loadTokens = async () => {
    const [saved, error] = await tryCatch(EncryptedStorage.getItem("auth"));
    if (saved) {
      const parsed = JSON.parse(saved);
      const username = getUsernameFromAccessToken(parsed?.accessToken);
      if (username) setUser(username);
      setTokens(parsed);
      setIsAuthenticated(true);
    }
    if (error) throw new Error("error while loading tokens");
    setLoading(false);
  };
  const signIn = async (
    formState: FormStateLoginProps,
  ): Promise<{ ok: boolean; error?: string }> => {
    const [data, error] = await tryCatch(login(formState));
    if (error) return { ok: false, error: error?.message || String(error) };
    if (data?.error) return { ok: false, error: data.error };
    const { accessToken, refreshToken, refreshTokenId } = extractTokens(data);

    if (!accessToken || typeof accessToken !== "string") {
      return { ok: false, error: t("errors.no_access_token") };
    }

    await EncryptedStorage.setItem(
      "auth",
      JSON.stringify({ accessToken, refreshToken, refreshTokenId }),
    );
    setTokens({
      accessToken: accessToken || "",
      refreshToken: refreshToken || "",
      refreshTokenId: refreshTokenId || "",
    });
    setIsAuthenticated(true);
    setUser(formState.loginData);
    return { ok: true };
  };
  const signOut = async () => {
    console.log("doszlo");
    await EncryptedStorage.removeItem("auth");
    setTokens(null);
    setUser("");
    setIsAuthenticated(false);
  };
  const signUp = async (formState: FormStateRegisterProps) => {
    const [data, error] = await tryCatch(register(formState));
    if (error) return { ok: false, error: error?.message || String(error) };
    if (data?.error) return { ok: false, error: data.error };
    const { accessToken, refreshToken, refreshTokenId } = extractTokens(
      data.tokens,
    );
    if (!accessToken || typeof accessToken !== "string") {
      return { ok: false, error: t("errors.no_access_token") };
    }

    await EncryptedStorage.setItem(
      "auth",
      JSON.stringify({ accessToken, refreshToken, refreshTokenId }),
    );
    setTokens({
      accessToken: accessToken || "",
      refreshToken: refreshToken || "",
      refreshTokenId: refreshTokenId || "",
    });
    setIsAuthenticated(true);
    setUser(formState.username);
    return { ok: true };
  };

  const signWithGoogle = async ({
    setIsSubmiting,
    navigation,
  }: GoogleLoginProps): Promise<SignWithGoogleResult> => {
    setIsSubmiting(true);
    const googleResult = await handleGoogleAuth();
    if (googleResult.error) {
      setIsSubmiting(false);
      return { status: "ERROR", error: googleResult.error.message };
    }
    const { idToken, user, name } = googleResult;
    if (!idToken) {
      setIsSubmiting(false);
      return { status: "ERROR", error: "No ID token received" };
    }
    const userStatus = await checkUserExistence(idToken);
    if (userStatus === "USER_EXIST") {
      const loginResult = await loginWithGoogle({ idToken, name });
      console.log("AuthContextLoginUserExist: ", loginResult.data);
      if (loginResult.error) {
        setIsSubmiting(false);
        return { status: "ERROR", error: loginResult.error.message };
      }
      const { accessToken, refreshToken, refreshTokenId } = extractTokens(
        loginResult.data.tokens,
      );
      await EncryptedStorage.setItem(
        "auth",
        JSON.stringify({ accessToken, refreshToken, refreshTokenId }),
      );
      setTokens({
        accessToken: accessToken || "",
        refreshToken: refreshToken || "",
        refreshTokenId: refreshTokenId || "",
      });
      console.log("userName: ", user.name);
      setIsAuthenticated(true);
      setIsSubmiting(false);
      navigation.replace("Main");
      return { status: "LOGGED_IN" };
    }
    if (userStatus === "USER_NOT_EXIST") {
      setIsSubmiting(false);
      setPendingGoogleIdToken(idToken);
      navigation.navigate("ProfileCompletionGoogle");
      return { status: "REGISTER_REQUIRED" };
    }
    if (userStatus === "USER_EXIST_WITH_EMAIL") {
      setIsSubmiting(false);
      return { status: "REGISTER_REQUIRED" };
    }
    setIsSubmiting(false);
    return { status: "ERROR", error: "Unknown user status" };
  };

  const completeGoogleRegistration = async (
    idToken: string,
    username: string,
    phoneNumber: string,
  ) => {
    const registerResult = await registerWithGoogle({
      idToken,
      username,
      phoneNumber,
    });
    if (!registerResult.ok) {
      return { status: "ERROR", error: registerResult.error };
    }
    console.log(
      "registerResultCompleteGoogleRegistrationData: ",
      registerResult.data,
    );
    const { accessToken, refreshToken, refreshTokenId } = extractTokens(
      registerResult.data,
    );

    await EncryptedStorage.setItem(
      "auth",
      JSON.stringify({ accessToken, refreshToken, refreshTokenId }),
    );

    setTokens({
      accessToken: accessToken || "",
      refreshToken: refreshToken || "",
      refreshTokenId: refreshTokenId || "",
    });

    setUser(username);
    setIsAuthenticated(true);

    return { status: "REGISTER_REQUIRED" };
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        isAuthenticated,
        pendingGoogleIdToken,
        signIn,
        signOut,
        signUp,
        refreshAuth: loadTokens,
        signWithGoogle,
        completeGoogleRegistration,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
