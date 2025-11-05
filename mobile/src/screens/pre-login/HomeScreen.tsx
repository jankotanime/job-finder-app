import React from "react";
import { View, Text, StyleSheet, Dimensions } from "react-native";
import ImageBackground from "../../components/reusable/ImageBackground";
import { useTranslation } from "react-i18next";
import HomeLoginButton from "../../components/pre-login/HomeLoginButton";
import { useNavigation } from "@react-navigation/native";
import { useRoute } from "@react-navigation/native";
import ErrorNotification from "../../components/reusable/ErrorNotification";

const { width, height } = Dimensions.get("window");
const HomeScreen = () => {
  const { t } = useTranslation();
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const authError = route?.params?.authError as string | undefined;
  return (
    <View style={styles.container}>
      <ImageBackground />
      {authError ? (
        <View style={styles.errorWrapper} pointerEvents="box-none">
          <ErrorNotification error={authError} />
        </View>
      ) : null}
      <Text style={styles.text}>{t("pre-login-home.welcome")}</Text>
      <Text style={styles.subtext}>{t("pre-login-home.welcome_subtext")}</Text>
      <HomeLoginButton
        text={t("signup")}
        styles={styles.signUpButton}
        onPress={() => navigation.navigate("Register")}
      />
      <HomeLoginButton
        text={t("signin")}
        styles={styles.signInButton}
        white={true}
        onPress={() => navigation.navigate("Login")}
      />
    </View>
  );
};

export default HomeScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  errorWrapper: {
    position: "absolute",
    top: 40,
    left: 0,
    right: 0,
    alignItems: "center",
    zIndex: 999,
    elevation: 999,
  },
  text: {
    position: "absolute",
    top: height * 0.35,
    left: 0,
    right: 0,
    textAlign: "center",
    color: "white",
    fontSize: 28,
    fontWeight: "700",
  },
  subtext: {
    position: "absolute",
    top: height * 0.35 + 40,
    left: 20,
    right: 20,
    textAlign: "center",
    color: "white",
    fontSize: 20,
  },
  signUpButton: {
    position: "absolute",
    bottom: height * 0.03,
    right: width * 0.025,
    width: width * 0.4,
  },
  signInButton: {
    position: "absolute",
    bottom: height * 0.03,
    left: width * 0.07,
    width: width * 0.32,
  },
});
