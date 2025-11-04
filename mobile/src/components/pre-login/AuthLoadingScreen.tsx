import React, { useEffect, useRef, useState } from "react";
import { View, ActivityIndicator, StyleSheet } from "react-native";
import { useTheme } from "react-native-paper";
import { useAuth } from "../../contexts/AuthContext";
import { useNavigation } from "@react-navigation/native";
import { rotateTokens } from "../../utils/rotateTokens";
import { getTokens } from "../../utils/getTokens";
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
        setError(
          getErrorMessage(getErr?.message, t) || String(getErr?.message),
        );
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
        setError(
          getErrorMessage(parseErr?.message, t) || String(parseErr?.message),
        );
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
        setError(
          getErrorMessage(rotateErr?.message, t) || String(rotateErr?.message),
        );
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
          setError(
            getErrorMessage(setItemErr?.message, t) || String(setItemErr),
          );
        } else {
          const [, refreshErr] = await tryCatch(refreshAuth());
          if (refreshErr)
            setError(
              getErrorMessage(refreshErr?.message, t) || String(refreshErr),
            );
        }
      }
    };
    doRotate();
  }, []);

  useEffect(() => {
    const checkAuthStatus = async () => {
      if (hasNavigated.current || loading) return;
      if (isAuthenticated && user) {
        hasNavigated.current = true;
        navigation.replace("Main");
      } else {
        hasNavigated.current = true;
        navigation.replace("Home");
      }
    };
    checkAuthStatus();
  }, [user, loading, isAuthenticated]);
  return (
    <View style={styles.main}>
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
