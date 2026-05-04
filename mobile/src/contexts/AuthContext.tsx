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
import { AuthStatus } from "../enums/authStatus";
import getUserInfo, { User } from "../auth/tokens/getUserInfo";
import { setTokensApiFetch } from "../api/client";

type AuthContextType = {
  user: string;
  loading: boolean;
  isAuthenticated: boolean;
  userInfo: User | null;
  pendingGoogleIdToken: string | null;
  tokens: {
    accessToken: string;
    refreshToken: string;
    refreshTokenId: string;
  } | null;
  signIn: (
    formState: FormStateLoginProps,
    navigation: any,
  ) => Promise<{ ok: boolean; error?: string; status?: AuthStatus }>;
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
  completeFinalRegistration: (
    accessToken: string,
    refreshToken: string,
    refreshTokenId: string,
    username: string | null,
  ) => Promise<{ ok: boolean; status: AuthStatus }>;
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
  userInfo: null,
  pendingGoogleIdToken: "",
  tokens: null,
  signIn: async () => ({
    ok: false,
    error: "not-initialized",
    status: AuthStatus.ERROR,
  }),
  signOut: async () => {},
  signUp: async () => ({ ok: false, error: "not-initialized" }),
  refreshAuth: async () => {},
  signWithGoogle: async () => ({
    status: AuthStatus.ERROR,
    error: "not-initialized",
  }),
  completeGoogleRegistration: async () => ({
    status: AuthStatus.ERROR,
    error: "not-initialized",
  }),
  completeFinalRegistration: async () => ({
    ok: false,
    status: AuthStatus.ERROR,
  }),
});
export const useAuth = () => useContext(AuthContext);
export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(true);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [userInfo, setUserInfo] = useState<User | null>(null);
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
      const userProps = getUserInfo(parsed?.accessToken);
      if (username) setUser(username);
      if (userProps) setUserInfo(userProps);
      setTokensApiFetch(parsed);
      setTokens(parsed);
      setIsAuthenticated(true);
    }
    if (error) throw new Error("error while loading tokens");
    setLoading(false);
  };
  const signIn = async (
    formState: FormStateLoginProps,
    _navigation: any,
  ): Promise<{ ok: boolean; error?: string; status?: AuthStatus }> => {
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
    setTokensApiFetch({
      accessToken: accessToken || "",
      refreshToken: refreshToken || "",
      refreshTokenId: refreshTokenId || "",
    });
    const username = getUsernameFromAccessToken(accessToken);
    const info = getUserInfo(accessToken);
    setUserInfo(info);
    const userRegisterStatus = await EncryptedStorage.getItem(
      `auth:${username}`,
    );
    const userRegisterStatusParsed = userRegisterStatus
      ? JSON.parse(userRegisterStatus)
      : null;
    const status = userRegisterStatusParsed?.status;
    if (status === AuthStatus.REGISTER_REQUIRED) {
      return { ok: true, status: AuthStatus.REGISTER_REQUIRED };
    }
    setIsAuthenticated(true);
    setUser(info?.username ?? formState.loginData);
    return { ok: true, status: AuthStatus.LOGGED_IN };
  };
  const signOut = async () => {
    await EncryptedStorage.removeItem("auth");
    setTokens(null);
    setTokensApiFetch({
      accessToken: "",
      refreshToken: "",
      refreshTokenId: "",
    });
    setUser("");
    setUserInfo(null);
    setIsAuthenticated(false);
  };
  const signUp = async (formState: FormStateRegisterProps) => {
    const [data, error] = await tryCatch(register(formState));
    if (error) return { ok: false, error: error?.message || String(error) };
    if (data?.error) return { ok: false, error: data.error };
    const { accessToken, refreshToken, refreshTokenId } = data.data;
    if (!accessToken || typeof accessToken !== "string") {
      return { ok: false, error: t("errors.no_access_token") };
    }

    await EncryptedStorage.setItem(
      "auth",
      JSON.stringify({ accessToken, refreshToken, refreshTokenId }),
    );
    await EncryptedStorage.setItem(
      `auth:${formState.username}`,
      JSON.stringify({
        status: AuthStatus.REGISTER_REQUIRED,
      }),
    );
    setTokens({
      accessToken: accessToken || "",
      refreshToken: refreshToken || "",
      refreshTokenId: refreshTokenId || "",
    });
    setTokensApiFetch({
      accessToken: accessToken || "",
      refreshToken: refreshToken || "",
      refreshTokenId: refreshTokenId || "",
    });
    const info = getUserInfo(accessToken);
    setUserInfo(info);
    setIsAuthenticated(true);
    setUser(info?.username ?? formState.username);
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
      return { status: AuthStatus.ERROR, error: googleResult.error.message };
    }
    const { idToken, user, name } = googleResult;
    if (!idToken) {
      setIsSubmiting(false);
      return { status: AuthStatus.ERROR, error: "No ID token received" };
    }
    const userStatus = await checkUserExistence(idToken);
    if (userStatus === AuthStatus.USER_EXIST) {
      const loginResult = await loginWithGoogle({ idToken, name });
      if (loginResult.error) {
        setIsSubmiting(false);
        return { status: AuthStatus.ERROR, error: loginResult.error.message };
      }
      const { accessToken, refreshToken, refreshTokenId } =
        loginResult.data.data.tokens;
      await EncryptedStorage.setItem(
        "auth",
        JSON.stringify({ accessToken, refreshToken, refreshTokenId }),
      );
      setTokens({
        accessToken: accessToken || "",
        refreshToken: refreshToken || "",
        refreshTokenId: refreshTokenId || "",
      });
      setTokensApiFetch({
        accessToken: accessToken || "",
        refreshToken: refreshToken || "",
        refreshTokenId: refreshTokenId || "",
      });
      const info = getUserInfo(accessToken);
      setUserInfo(info);
      const username = getUsernameFromAccessToken(accessToken);
      const userRegisterStatus = await EncryptedStorage.getItem(
        `auth:${username}`,
      );
      const userRegisterStatusParsed = userRegisterStatus
        ? JSON.parse(userRegisterStatus)
        : null;
      const status = userRegisterStatusParsed?.status;
      if (status === AuthStatus.REGISTER_REQUIRED) {
        setIsSubmiting(false);
        navigation.navigate("ProfileCompletion");
        return { status: AuthStatus.REGISTER_REQUIRED };
      }
      setIsAuthenticated(true);
      setIsSubmiting(false);
      navigation.replace("Main");
      return { status: AuthStatus.LOGGED_IN };
    }
    if (userStatus === AuthStatus.USER_NOT_EXIST) {
      setIsSubmiting(false);
      setPendingGoogleIdToken(idToken);
      navigation.navigate("ProfileCompletionGoogle");
      return { status: AuthStatus.REGISTER_REQUIRED };
    }
    if (userStatus === AuthStatus.USER_EXIST_WITH_EMAIL) {
      setIsSubmiting(false);
      navigation.navigate("SmsGoogleCode");
      return { status: AuthStatus.REGISTER_REQUIRED };
    }
    setIsSubmiting(false);
    return { status: AuthStatus.ERROR, error: "Unknown user status" };
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
      return { status: AuthStatus.ERROR, error: registerResult.error };
    }
    const { accessToken, refreshToken, refreshTokenId } = registerResult.data;

    await EncryptedStorage.setItem(
      "auth",
      JSON.stringify({ accessToken, refreshToken, refreshTokenId }),
    );
    await EncryptedStorage.setItem(
      `auth:${username}`,
      JSON.stringify({
        status: AuthStatus.REGISTER_REQUIRED,
      }),
    );
    setTokens({
      accessToken: accessToken || "",
      refreshToken: refreshToken || "",
      refreshTokenId: refreshTokenId || "",
    });
    setTokensApiFetch({
      accessToken: accessToken || "",
      refreshToken: refreshToken || "",
      refreshTokenId: refreshTokenId || "",
    });
    const info = getUserInfo(accessToken);
    setUserInfo(info);
    setUser(info?.username ?? username);
    setIsAuthenticated(true);

    return { status: AuthStatus.REGISTER_REQUIRED };
  };

  const completeFinalRegistration = async (
    accessToken: string,
    refreshToken: string,
    refreshTokenId: string,
    username: string | null,
  ) => {
    if (!accessToken || !refreshToken || !refreshTokenId)
      return { ok: false, status: AuthStatus.REGISTER_REQUIRED };
    await EncryptedStorage.setItem(
      "auth",
      JSON.stringify({ accessToken, refreshToken, refreshTokenId }),
    );
    await EncryptedStorage.setItem(
      `auth:${username}`,
      JSON.stringify({
        status: AuthStatus.REGISTERED,
      }),
    );
    setTokens({
      accessToken: accessToken || "",
      refreshToken: refreshToken || "",
      refreshTokenId: refreshTokenId || "",
    });
    setTokensApiFetch({
      accessToken: accessToken || "",
      refreshToken: refreshToken || "",
      refreshTokenId: refreshTokenId || "",
    });
    const info = getUserInfo(accessToken);
    setUserInfo(info);
    setUser(info?.username ?? username ?? "");
    setIsAuthenticated(true);
    return { ok: true, status: AuthStatus.REGISTERED };
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        userInfo,
        loading,
        isAuthenticated,
        pendingGoogleIdToken,
        tokens,
        signIn,
        signOut,
        signUp,
        refreshAuth: loadTokens,
        signWithGoogle,
        completeGoogleRegistration,
        completeFinalRegistration,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
