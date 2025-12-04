import React, { useEffect, useState } from "react";
import {
  View,
  StyleSheet,
  Dimensions,
  TouchableOpacity,
  ActivityIndicator,
} from "react-native";
import { useTranslation } from "react-i18next";
import { useTheme, Text, Button } from "react-native-paper";
import { AntDesign } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import { useAuth } from "../../contexts/AuthContext";
import { getErrorMessage } from "../../constans/errorMessages";

const { width, height } = Dimensions.get("window");
interface GoogleLoginProps {
  screen: string;
  setError: (err: string) => void;
}
const GoogleButton = ({ screen, setError }: GoogleLoginProps) => {
  const { colors } = useTheme();
  const { t } = useTranslation();
  const [isSubmiting, setIsSubmiting] = useState<boolean>(false);
  const navigation = useNavigation<any>();
  const { signInGoogle, signUpGoogle } = useAuth();

  const handleGoogleAuth = async () => {
    if (screen == "REGISTER") {
      const { ok, error } = await signUpGoogle({ setIsSubmiting, navigation });
      if (error) setError(getErrorMessage(error, t) || error);
    } else {
      const { ok, error } = await signInGoogle({ setIsSubmiting, navigation });
      if (error) setError(getErrorMessage(error, t) || error);
    }
  };

  return (
    <Button
      mode="contained"
      icon={() => <AntDesign name="google" size={20} color="white" />}
      contentStyle={{ flexDirection: "row", justifyContent: "center" }}
      labelStyle={{ color: "white", fontSize: 16, fontWeight: "600" }}
      style={[
        styles.googleButton,
        { backgroundColor: colors.primaryContainer },
      ]}
      onPress={handleGoogleAuth}
      disabled={isSubmiting}
      loading={isSubmiting}
    >
      {isSubmiting
        ? t("pre-login-home.signing_up_with_google")
        : t("pre-login-home.sign_up_with_google")}
    </Button>
  );
};

export default GoogleButton;

const styles = StyleSheet.create({
  googleButton: {
    position: "absolute",
    alignSelf: "center",
    bottom: height * 0.27,
    width: width * 0.7,
    borderRadius: 30,
    borderWidth: 1,
  },
});
