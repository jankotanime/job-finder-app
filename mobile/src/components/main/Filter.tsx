import React from "react";
import { View, StyleSheet, Dimensions, TouchableOpacity } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useTheme } from "react-native-paper";

const { width, height } = Dimensions.get("window");
const Filter = () => {
  const { colors } = useTheme();
  return (
    <TouchableOpacity style={styles.filter}>
      <Ionicons name="filter" size={30} color={colors.primary} />
    </TouchableOpacity>
  );
};

export default Filter;

const styles = StyleSheet.create({
  filter: {
    position: "absolute",
    top: height * 0.08,
    left: 30,
  },
});
