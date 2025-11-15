import React, { useEffect } from "react";
import { View, StyleSheet, Dimensions } from "react-native";
const { height, width } = Dimensions.get("window");

const Card = ({ children }: { children: React.ReactNode }) => {
  return (
    <View style={styles.shadowWrap}>
      <View style={styles.main}>{children}</View>
    </View>
  );
};

export default Card;

const styles = StyleSheet.create({
  shadowWrap: {
    alignSelf: "center",
    height: height * 0.72,
    width: width * 0.93,
    marginTop: height * 0.05,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.22,
    shadowRadius: 10,
    elevation: 6,
  },
  main: {
    flex: 1,
    borderRadius: 20,
    overflow: "hidden",
    backgroundColor: "red",
    justifyContent: "center",
    alignItems: "center",
  },
});
