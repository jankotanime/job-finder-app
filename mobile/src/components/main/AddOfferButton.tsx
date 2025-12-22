import React from "react";
import { View, StyleSheet, TouchableOpacity, Dimensions } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useTheme } from "react-native-paper";
import { useNavigation } from "@react-navigation/native";

const { height, width } = Dimensions.get("window");
const AddOfferButton = () => {
  const { colors } = useTheme();
  const navigation = useNavigation<any>();
  return (
    <TouchableOpacity
      style={[
        styles.button,
        { backgroundColor: colors.onBackground, borderColor: colors.primary },
      ]}
      onPress={() => navigation.navigate("AddOffer")}
    >
      <Ionicons name="add" size={35} color={colors.primary} />
    </TouchableOpacity>
  );
};

export default AddOfferButton;

const styles = StyleSheet.create({
  button: {
    position: "absolute",
    top: height * 0.91,
    width: 65,
    height: 50,
    borderRadius: 12,
    justifyContent: "center",
    alignItems: "center",
    borderWidth: 0.2,
  },
});
