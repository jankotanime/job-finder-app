import React from "react";
import { View, StyleSheet, Dimensions } from "react-native";
import { useTheme } from "react-native-paper";
const { height, width } = Dimensions.get("window");

const Card = ({ children }: { children: React.ReactNode }) => {
  const { colors } = useTheme();
  return (
    <View style={styles.shadowWrap}>
      <View style={[styles.main, { backgroundColor: colors.primary }]}>
        {children}
      </View>
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
    justifyContent: "center",
    alignItems: "center",
  },
});
