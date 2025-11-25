import React from "react";
import { View, StyleSheet } from "react-native";
import Entypo from "@expo/vector-icons/Entypo";

const OnSwipeBottom = () => {
  return (
    <View style={styles.container}>
      <Entypo name="box" size={48} color={"#8B5E3C"} />
    </View>
  );
};

export default OnSwipeBottom;

const styles = StyleSheet.create({
  container: {
    position: "absolute",
    alignSelf: "center",
    width: 50,
    height: 50,
    borderRadius: 16,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.2,
    shadowRadius: 6,
    elevation: 6,
  },
});
