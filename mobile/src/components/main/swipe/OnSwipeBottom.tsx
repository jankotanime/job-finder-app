import React from "react";
import { View, StyleSheet } from "react-native";
import Entypo from "@expo/vector-icons/Entypo";

const OnSwipeBottom = ({ isActive }: { isActive: boolean }) => {
  return (
    <View style={isActive ? styles.containerActive : styles.container}>
      <Entypo name="box" size={isActive ? 200 : 48} color={"#8B5E3C"} />
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
  containerActive: {
    position: "absolute",
    alignSelf: "center",
    justifyContent: "center",
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
