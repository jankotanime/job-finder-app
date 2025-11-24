import React from "react";
import { View, Text, StyleSheet, Dimensions } from "react-native";
import { useTheme } from "react-native-paper";

type error = { error: string };
const { width } = Dimensions.get("window");
const Error = ({ error }: error) => {
  const { colors } = useTheme();
  return (
    <View style={[styles.container, { backgroundColor: colors.error }]}>
      <Text style={[styles.text, { color: colors.onError }]}>{error}</Text>
    </View>
  );
};
const styles = StyleSheet.create({
  container: {
    padding: 10,
    borderRadius: 15,
    width: width * 0.8,
    alignSelf: "center",
    marginBottom: 10,
  },
  text: {
    textAlign: "center",
    fontSize: 14,
  },
});

export default Error;
