import React, { useEffect, useRef, useState } from "react";
import { View, ActivityIndicator, StyleSheet } from "react-native";
import { useTheme } from "react-native-paper";
import { useAuth } from "../../contexts/AuthContext";
import { useNavigation } from "@react-navigation/native";
import { rotateTokens } from "../../utils/auth/tokens/rotateTokens";
import { getTokens } from "../../utils/auth/tokens/getTokens";
import EncryptedStorage from "react-native-encrypted-storage";
import { useTranslation } from "react-i18next";
import { tryCatch } from "../../utils/try-catch";
import { getErrorMessage } from "../../constans/errorMessages";

const AuthLoadingScreen = () => {
  const { colors } = useTheme();
  const { user, loading, isAuthenticated, refreshAuth } = useAuth();
  const hasNavigated = useRef(false);
  const navigation = useNavigation<any>();
  const [error, setError] = useState<string>("");
  const { t } = useTranslation();

  useEffect(() => {
    const doRotate = async () => {
      const [tokensRaw, getErr] = await tryCatch(getTokens(setError));
      if (getErr) {
        const msg =
          getErrorMessage(getErr?.message, t) || String(getErr?.message);
        setError(msg);
        navigation.replace("Home", { authError: msg });
        return;
      }
      if (!tokensRaw) return;
      const parseJsonAsync = (s: string) =>
        new Promise<any>((resolve, reject) => {
          try {
            resolve(JSON.parse(s));
          } catch (e) {
            reject(e);
          }
        });
      const [parsed, parseErr] = await tryCatch(parseJsonAsync(tokensRaw));
      if (parseErr) {
        const msg =
          getErrorMessage(parseErr?.message, t) || String(parseErr?.message);
        setError(msg);
        navigation.replace("Home", { authError: msg });
        return;
      }
      if (!parsed?.refreshToken || !parsed?.refreshTokenId) return;
      const [rotated, rotateErr] = await tryCatch(
        rotateTokens({
          tokens: {
            refreshToken: parsed.refreshToken,
            refreshTokenId: parsed.refreshTokenId,
          },
          setError,
          t,
        }),
      );
      if (rotateErr) {
        const msg =
          getErrorMessage(rotateErr?.message, t) || String(rotateErr?.message);
        setError(msg);
        navigation.replace("Home", { authError: msg });
        return;
      }
      if (!rotated) return;
      const tokens = rotated.data;
      if (tokens && tokens.accessToken) {
        const [, setItemErr] = await tryCatch(
          EncryptedStorage.setItem(
            "auth",
            JSON.stringify({
              accessToken: tokens.accessToken,
              refreshToken: tokens.refreshToken,
              refreshTokenId: tokens.refreshTokenId,
            }),
          ),
        );
        if (setItemErr) {
          const msg =
            getErrorMessage(setItemErr?.message, t) || String(setItemErr);
          setError(msg);
          navigation.replace("Home", { authError: msg });
        } else {
          const [, refreshErr] = await tryCatch(refreshAuth());
          if (refreshErr) {
            const msg =
              getErrorMessage(refreshErr?.message, t) || String(refreshErr);
            setError(msg);
            navigation.replace("Home", { authError: msg });
          }
        }
      }
    };
    doRotate();
  }, []);

  useEffect(() => {
    const checkAuthStatus = async () => {
      if (hasNavigated.current || loading) return;
      hasNavigated.current = true;
      if (isAuthenticated && user) {
        navigation.replace("Main");
      } else {
        hasNavigated.current = true;
        navigation.replace("Home");
      }
    };
    checkAuthStatus();
  }, [user, loading, isAuthenticated, error]);
  if (error) console.error("error: ", error);
  return (
    <View style={[styles.main, { backgroundColor: colors.background }]}>
      <ActivityIndicator size="large" color={colors.primary} />
    </View>
  );
};

export default AuthLoadingScreen;

const styles = StyleSheet.create({
  main: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
});
