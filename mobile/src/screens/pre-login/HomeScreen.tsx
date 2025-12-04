import React, { useState, useEffect } from "react";
import { View, StyleSheet, Dimensions, Animated, Text } from "react-native";
import ImageBackground from "../../components/reusable/ImageBackground";
import { useTranslation } from "react-i18next";
import { useNavigation } from "@react-navigation/native";
import { useRoute } from "@react-navigation/native";
import ErrorNotification from "../../components/reusable/ErrorNotification";
import makeTextAnim from "../../utils/textAnim";
import { Button } from "react-native-paper";
import { useTheme } from "react-native-paper";
import GoogleButton from "../../components/pre-login/GoogleButton";

const { width, height } = Dimensions.get("window");
const HomeScreen = () => {
  const { t } = useTranslation();
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const authError = route?.params?.authError as string | undefined;
  const [animatedValues, setAnimatedValues] = useState<Animated.Value[]>([]);
  const [words, setWords] = useState<string[]>([]);
  const { colors } = useTheme();
  const [error, setError] = useState<string>("");
  const [pressed, setPressed] = useState<boolean>(false);
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
      <Button
        mode="outlined"
        contentStyle={styles.buttonContent}
        labelStyle={[styles.buttonLabel, { color: colors.onPrimary }]}
        style={[
          styles.outlinedButton,
          { borderColor: colors.primaryContainer },
        ]}
        onPress={() => navigation.navigate("Register")}
        onPressIn={() => setPressed(true)}
        onPressOut={() => setPressed(false)}
        buttonColor={pressed ? colors.onTertiary : colors.primary}
      >
        {t("pre-login-home.sign_up_with_email")}
      </Button>
      <GoogleButton screen="LOGIN" setError={setError} />
      <View style={styles.main}>
        <Text style={{ color: colors.onPrimary }}>
          {t("pre-login-home.already_have_an_account")}
        </Text>
        <Button
          mode="text"
          compact
          labelStyle={[
            styles.link,
            { color: colors.onTertiary, fontWeight: 700 },
          ]}
          onPress={() => navigation.navigate("Login")}
        >
          {t("signin")}
        </Button>
      </View>
    </View>
  );
};

export default HomeScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  main: {
    display: "flex",
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    alignSelf: "center",
    position: "absolute",
    bottom: height * 0.15,
    width: width * 0.7,
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
  animatedTextWrapper: {
    flexDirection: "row",
    flexWrap: "wrap",
    position: "absolute",
    top: height * 0.35,
    justifyContent: "center",
    alignSelf: "center",
    width: width * 0.8,
  },
  buttonContent: {
    flexDirection: "row",
    justifyContent: "center",
    alignSelf: "center",
  },
  buttonLabel: {
    fontSize: 16,
    fontWeight: "600",
  },
  outlinedButton: {
    position: "absolute",
    alignSelf: "center",
    bottom: height * 0.2,
    width: width * 0.7,
    borderRadius: 30,
    borderWidth: 2,
    marginBottom: 7,
  },
  link: {
    fontSize: 14,
  },
});
