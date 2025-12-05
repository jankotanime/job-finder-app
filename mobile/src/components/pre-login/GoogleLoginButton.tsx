import React, { useEffect, useState } from "react";
import {
  View,
  StyleSheet,
  Dimensions,
  TouchableOpacity,
  ActivityIndicator,
} from "react-native";
import { useTranslation } from "react-i18next";
import { useTheme, Text } from "react-native-paper";
import { AntDesign } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import { useAuth } from "../../contexts/AuthContext";
import { getErrorMessage } from "../../constans/errorMessages";

const { width } = Dimensions.get("window");
interface GoogleLoginProps {
  screen: string;
  setError: (err: string) => void;
}
const GoogleLoginButton = ({ screen, setError }: GoogleLoginProps) => {
  const { colors } = useTheme();
  const { t } = useTranslation();
  const [isSubmiting, setIsSubmiting] = useState<boolean>(false);
  const navigation = useNavigation<any>();
  const { signWithGoogle } = useAuth();

  const handleGoogleAuth = async () => {
    const { ok, error } = await signWithGoogle({ setIsSubmiting, navigation });
    if (error) setError(getErrorMessage(error, t) || error);
  };

  return (
    <>
      <View style={styles.separator}>
        <View style={[styles.line, { backgroundColor: colors.primary }]} />
        <Text style={{ color: colors.primary, fontWeight: "600" }}>
          {t("login.or")}
        </Text>
        <View style={[styles.line, { backgroundColor: colors.primary }]} />
      </View>
      <TouchableOpacity
        style={[styles.googleLogo, { backgroundColor: colors.primary }]}
        onPress={() => handleGoogleAuth()}
      >
        {isSubmiting ? (
          <View style={[styles.main, { backgroundColor: colors.background }]}>
            <ActivityIndicator size="large" color={colors.primary} />
          </View>
        ) : (
          <AntDesign
            name="google"
            size={20}
            color="white"
            style={{ alignSelf: "center" }}
          />
        )}
      </TouchableOpacity>
    </>
  );
};

export default GoogleLoginButton;

const styles = StyleSheet.create({
  main: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  separator: {
    display: "flex",
    flexDirection: "row",
    alignSelf: "center",
    marginTop: 20,
  },
  line: {
    width: width * 0.3,
    height: 0.5,
    marginLeft: 10,
    marginRight: 10,
    alignSelf: "center",
  },
  googleLogo: {
    width: 32,
    height: 32,
    borderRadius: 32,
    alignSelf: "center",
    justifyContent: "center",
    marginTop: 15,
  },
});
