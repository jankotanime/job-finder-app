import React, { useRef, useState, useEffect } from "react";
import { View, Text, StyleSheet, Dimensions, Animated } from "react-native";
import ImageBackground from "../../components/reusable/ImageBackground";
import { useTranslation } from "react-i18next";
import HomeLoginButton from "../../components/pre-login/HomeLoginButton";
import { useNavigation } from "@react-navigation/native";
import { useRoute } from "@react-navigation/native";
import ErrorNotification from "../../components/reusable/ErrorNotification";
import { createAnimation } from "../../utils/animationHelper";

const { width, height } = Dimensions.get("window");
const HomeScreen = () => {
  const { t } = useTranslation();
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const authError = route?.params?.authError as string | undefined;
  const [animatedValues, setAnimatedValues] = useState<Animated.Value[]>([]);
  const [words, setWords] = useState<string[]>([]);

  const makeTextAnim = (text: string) => {
    const split = text.trim().split(" ");
    setWords(split);
    const values = split.map(() => new Animated.Value(0));
    setAnimatedValues(values);
    const animations = values.map((value, index) => {
      return Animated.sequence([
        createAnimation(value, 1, 500, index * 150, true),
        Animated.delay(3000),
        createAnimation(value, 0, 500, 0, true),
      ]);
    });
    Animated.loop(Animated.stagger(150, animations)).start();
  };
  useEffect(() => {
    const welcomeText = t("pre-login-home.welcome_subtext");
    makeTextAnim(welcomeText);
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
