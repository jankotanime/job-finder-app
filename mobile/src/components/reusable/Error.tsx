import React from "react";
import { View, Text, StyleSheet, Dimensions } from "react-native";

type error = { error: string };
const { width } = Dimensions.get("window");
const Error = ({ error }: error) => {
  return (
    <View style={styles.container}>
      <Text style={styles.text}>{error}</Text>
    </View>
  );
};
const styles = StyleSheet.create({
  container: {
    padding: 10,
    borderRadius: 15,
    width: width * 0.8,
    alignSelf: "center",
    backgroundColor: "#ffebee",
    marginBottom: 10,
  },
  text: {
    textAlign: "center",
    fontSize: 14,
    color: "#c62828",
  },
});

export default Error;
