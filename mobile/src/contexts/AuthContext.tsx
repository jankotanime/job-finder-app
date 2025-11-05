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
import { login } from "../utils/login";
import { register } from "../utils/register";
import { extractTokens } from "../utils/extractTokens";
import { useTranslation } from "react-i18next";

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
const AuthContext = createContext<AuthContextType>({
  user: "",
  loading: true,
  isAuthenticated: false,
  signIn: async () => ({ ok: false, error: "not-initialized" }),
  signOut: async () => {},
  signUp: async () => ({ ok: false, error: "not-initialized" }),
  refreshAuth: async () => {},
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
    setUser(formState.username);
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
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
