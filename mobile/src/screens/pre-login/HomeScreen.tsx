import React, { useState, useEffect } from "react";
import { View, StyleSheet, Dimensions, Animated } from "react-native";
import ImageBackground from "../../components/reusable/ImageBackground";
import { useTranslation } from "react-i18next";
import HomeLoginButton from "../../components/pre-login/HomeLoginButton";
import { useNavigation } from "@react-navigation/native";
import { useRoute } from "@react-navigation/native";
import ErrorNotification from "../../components/reusable/ErrorNotification";
import makeTextAnim from "../../utils/textAnim";

const { width, height } = Dimensions.get("window");
const HomeScreen = () => {
  const { t } = useTranslation();
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const authError = route?.params?.authError as string | undefined;
  const [animatedValues, setAnimatedValues] = useState<Animated.Value[]>([]);
  const [words, setWords] = useState<string[]>([]);

  useEffect(() => {
    const welcomeText = t("pre-login-home.welcome_subtext");
    makeTextAnim({ text: welcomeText, setWords, setAnimatedValues });
  }, [t]);
  return (
    <View style={styles.container}>
      <ImageBackground />
      {authError ? (
        <View style={styles.errorWrapper} pointerEvents="box-none">
          <ErrorNotification error={authError} />
        </View>
      ) : null}
      <View style={styles.animatedTextWrapper}>
        {words.map((word, i) => {
          const opacity = animatedValues[i] || new Animated.Value(0);
          const translateY = opacity.interpolate({
            inputRange: [0, 1],
            outputRange: [10, 0],
          });

          return (
            <Animated.Text
              key={i}
              style={[
                styles.text,
                {
                  opacity,
                  transform: [{ translateY }],
                },
              ]}
            >
              {word + " "}
            </Animated.Text>
          );
        })}
      </View>
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
    top: height * 0.35,
    left: 0,
    right: 0,
    textAlign: "center",
    color: "white",
    fontSize: 28,
    fontWeight: "600",
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
  animatedTextWrapper: {
    flexDirection: "row",
    flexWrap: "wrap",
    position: "absolute",
    top: height * 0.35,
    justifyContent: "center",
    alignSelf: "center",
    width: width * 0.8,
  },
});
