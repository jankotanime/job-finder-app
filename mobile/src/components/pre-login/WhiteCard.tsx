import React from "react";
import { View, StyleSheet, Dimensions } from "react-native";
import { useTheme } from "react-native-paper";

const { width, height } = Dimensions.get("window");
const WhiteCard = ({ children }: { children: React.ReactNode }) => {
  const { colors } = useTheme();
  return (
    <View style={[styles.card, { backgroundColor: colors.background }]}>
      {children}
    </View>
  );
};
export default WhiteCard;

const styles = StyleSheet.create({
  card: {
    position: "absolute",
    bottom: 0,
    height: height * 0.76,
    width: width,
    borderTopLeftRadius: 45,
    borderTopRightRadius: 45,
  },
});
