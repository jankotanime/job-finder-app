import React from "react";
import { View, StyleSheet } from "react-native";
import Ionicons from "@expo/vector-icons/Ionicons";

const OnSwipeLeft = ({ isActive }: { isActive: boolean }) => {
  return (
    <View style={isActive ? styles.containerActive : styles.container}>
      <Ionicons name="close" size={isActive ? 200 : 48} color="#fff" />
    </View>
  );
};

export default OnSwipeLeft;

const styles = StyleSheet.create({
  container: {
    position: "absolute",
    width: 50,
    height: 50,
    right: 0,
    backgroundColor: "#e74c3c",
    borderRadius: 16,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.2,
    shadowRadius: 6,
    elevation: 6,
  },
  containerActive: {
    position: "absolute",
    alignSelf: "center",
    justifyContent: "center",
    backgroundColor: "#e74c3c",
    width: 200,
    height: 200,
    borderRadius: 16,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.2,
    shadowRadius: 6,
    elevation: 6,
  },
});
