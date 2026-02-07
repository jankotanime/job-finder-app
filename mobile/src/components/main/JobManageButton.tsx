import React from "react";
import { View, StyleSheet, TouchableOpacity, Dimensions } from "react-native";
import { Entypo } from "@expo/vector-icons";
import { useTheme } from "react-native-paper";
import { useNavigation } from "@react-navigation/native";

const { height, width } = Dimensions.get("window");
const JobManageButton = () => {
  const { colors } = useTheme();
  const navigation = useNavigation<any>();
  return (
    <TouchableOpacity
      style={[styles.button]}
      onPress={() => navigation.navigate("ChooseJobScreen")}
    >
      <Entypo name="briefcase" size={35} color={colors.primary} />
    </TouchableOpacity>
  );
};

export default JobManageButton;

const styles = StyleSheet.create({
  button: {
    width: 60,
    height: 40,
    borderRadius: 12,
    justifyContent: "center",
    alignItems: "center",
  },
});
