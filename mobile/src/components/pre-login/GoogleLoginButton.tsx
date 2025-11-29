import React from "react";
import { View, StyleSheet, Dimensions, TouchableOpacity } from "react-native";
import { useTranslation } from "react-i18next";
import { useTheme, Text } from "react-native-paper";
import { AntDesign } from "@expo/vector-icons";

const { width } = Dimensions.get("window");
const GoogleLoginButton = () => {
  const { colors } = useTheme();
  const { t } = useTranslation();
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
      >
        <AntDesign
          name="google"
          size={20}
          color="white"
          style={{ alignSelf: "center" }}
        />
      </TouchableOpacity>
    </>
  );
};

export default GoogleLoginButton;

const styles = StyleSheet.create({
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
