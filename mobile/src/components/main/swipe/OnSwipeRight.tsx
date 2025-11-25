import React from "react";
import { View, StyleSheet } from "react-native";
import Ionicons from "@expo/vector-icons/Ionicons";

const OnSwipeRight = () => {
  return (
    <View style={styles.container}>
      <Ionicons name="checkmark" size={48} color="#fff" />
    </View>
  );
};

export default OnSwipeRight;

const styles = StyleSheet.create({
  container: {
    width: 50,
    height: 50,
    backgroundColor: "#2ecc71",
    borderRadius: 16,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.2,
    shadowRadius: 6,
    elevation: 6,
  },
});
