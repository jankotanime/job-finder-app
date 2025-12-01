import React, { useState } from "react";
import {
  View,
  StyleSheet,
  Dimensions,
  Pressable,
  ScrollView,
  KeyboardAvoidingView,
  Platform,
} from "react-native";
import ImageBackground from "../../components/reusable/ImageBackground";
import WhiteCard from "../../components/pre-login/WhiteCard";
import { useTranslation } from "react-i18next";
import { useTheme, Button, Text } from "react-native-paper";
import Input from "../../components/reusable/Input";
import { fieldsLogin } from "../../constans/formFields";
import { useNavigation } from "@react-navigation/native";
import { handleLoginSubmit } from "../../utils/auth/handleLoginSubmit";
import { useAuth } from "../../contexts/AuthContext";
import Error from "../../components/reusable/Error";
import GoogleLoginButton from "../../components/pre-login/GoogleLoginButton";

interface FormState {
  loginData: string;
  password: string;
}
const { width, height } = Dimensions.get("window");
const LoginScreen = () => {
  const { t } = useTranslation();
  const theme = useTheme();
  const navigation = useNavigation<any>();
  const { signIn } = useAuth();
  const [formState, setFormState] = useState<FormState>({
    loginData: "",
    password: "",
  });
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");
  return (
    <View>
      <ImageBackground />
      <WhiteCard>
        <KeyboardAvoidingView
          style={{ flex: 1 }}
          behavior={Platform.OS === "ios" ? "padding" : "height"}
          keyboardVerticalOffset={Platform.OS === "ios" ? 230 : 30}
        >
          <ScrollView>
            <View style={styles.header}>
              <Text
                style={[styles.headerText, { color: theme.colors.primary }]}
              >
                {t("login.welcome_back")}
              </Text>
            </View>
            <View style={styles.main}>
              {fieldsLogin(t).map((field) => (
                <Input
                  key={field.key}
                  placeholder={field.placeholder}
                  value={formState[field.key as keyof FormState]}
                  onChangeText={(text) =>
                    setFormState((prev) => ({
                      ...prev,
                      [field.key]: text,
                    }))
                  }
                  mode="outlined"
                  secure={field.secure}
                />
              ))}
            </View>
            <View style={styles.forgot}>
              <Pressable>
                {({ pressed }) => (
                  <Text
                    style={{
                      color: pressed
                        ? theme.colors.onSecondary
                        : theme.colors.primary,
                      fontWeight: "600",
                      marginLeft: 5,
                    }}
                  >
                    {t("login.forgot_password")}
                  </Text>
                )}
              </Pressable>
            </View>
            {error ? <Error error={error} /> : null}
            <Button
              mode="contained"
              style={styles.signInButton}
              contentStyle={{ height: 48 }}
              onPress={() => {
                handleLoginSubmit({
                  formState,
                  setError,
                  setIsLoading,
                  navigation,
                  t,
                  signIn,
                });
              }}
              disabled={
                isLoading ||
                Object.values(formState).some((value) => value.trim() === "")
              }
              loading={isLoading}
            >
              {isLoading ? t("login.signing_in") : t("signin")}
            </Button>
            <View style={styles.footer}>
              <Text style={{ color: theme.colors.primary }}>
                {t("login.sign_up_question")}
              </Text>
              <Pressable onPress={() => navigation.navigate("Register")}>
                {({ pressed }) => (
                  <Text
                    style={{
                      color: pressed
                        ? theme.colors.onSecondary
                        : theme.colors.primary,
                      fontWeight: "600",
                      marginLeft: 5,
                    }}
                  >
                    {t("signup")}
                  </Text>
                )}
              </Pressable>
            </View>
            <GoogleLoginButton screen="LOGIN" />
          </ScrollView>
        </KeyboardAvoidingView>
      </WhiteCard>
    </View>
  );
};

export default LoginScreen;

const styles = StyleSheet.create({
  header: {
    top: 30,
    justifyContent: "center",
    alignItems: "center",
  },
  headerText: {
    fontWeight: "bold",
    textAlign: "center",
    fontSize: 25,
  },
  main: {
    height: height * 0.28,
  },
  signInButton: {
    width: width * 0.8,
    height: 48,
    alignSelf: "center",
  },
  footer: {
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center",
    marginTop: 15,
  },
  forgot: {
    position: "absolute",
    alignItems: "flex-end",
    width: width * 0.88,
    top: height * 0.265,
  },
});
