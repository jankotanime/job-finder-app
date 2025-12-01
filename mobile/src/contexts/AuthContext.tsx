import React, {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode,
} from "react";
import EncryptedStorage from "react-native-encrypted-storage";
import getUsernameFromAccessToken from "../utils/getUsernameFromAccessToken";
import { tryCatch } from "../utils/try-catch";
import { login } from "../utils/auth/login";
import { register } from "../utils/auth/register";
import { extractTokens } from "../utils/auth/tokens/extractTokens";
import { useTranslation } from "react-i18next";
import {
  handleGoogleLogin,
  handleGoogleRegister,
} from "../utils/auth/handleGoogleAuth";

type AuthContextType = {
  user: string;
  loading: boolean;
  isAuthenticated: boolean;
  signIn: (
    formState: FormStateLoginProps,
  ) => Promise<{ ok: boolean; error?: string }>;
  signOut: () => Promise<void>;
  signUp: (
    formState: FormStateRegisterProps,
  ) => Promise<{ ok: boolean; error?: string }>;
  refreshAuth: () => Promise<void>;
  signInGoogle: (
    formState: GoogleLoginProps,
  ) => Promise<{ ok: boolean; error?: string }>;
  signUpGoogle: (
    formState: GoogleLoginProps,
  ) => Promise<{ ok: boolean; error?: string }>;
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
  signIn: async () => ({ ok: false, error: "not-initialized" }),
  signOut: async () => {},
  signUp: async () => ({ ok: false, error: "not-initialized" }),
  refreshAuth: async () => {},
  signInGoogle: async () => ({ ok: false, error: "not-initialized" }),
  signUpGoogle: async () => ({ ok: false, error: "not-initialized" }),
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
  const { t } = useTranslation();

  useEffect(() => {
    loadTokens();
  }, []);
  const loadTokens = async () => {
    const [saved, error] = await tryCatch(EncryptedStorage.getItem("auth"));
    console.log(saved);
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
  const signInGoogle = async (formState: GoogleLoginProps) => {
    const [dataResponse, error] = await tryCatch(handleGoogleLogin(formState));
    const data = dataResponse?.dataLogin;
    if (error) return { ok: false, error: error?.message || String(error) };
    if (data?.error) return { ok: false, error: data.code };
    const { accessToken, refreshToken, refreshTokenId } = extractTokens(
      data.data,
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
    setUser(dataResponse?.name ?? "");
    return { ok: true };
  };

  const signUpGoogle = async (formState: GoogleLoginProps) => {
    const [dataResponse, error] = await tryCatch(
      handleGoogleRegister(formState),
    );
    const data = dataResponse?.data;
    if (error) return { ok: false, error: error?.message || String(error) };
    if (data?.error) return { ok: false, error: data.code };
    const { accessToken, refreshToken, refreshTokenId } = extractTokens(
      data.data,
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
    setUser(dataResponse?.name ?? "");
    return { ok: true };
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        isAuthenticated,
        signIn,
        signOut,
        signUp,
        refreshAuth: loadTokens,
        signInGoogle,
        signUpGoogle,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
