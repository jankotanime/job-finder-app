import React from "react";
import { View, StyleSheet, Dimensions } from "react-native";
import { useTheme } from "react-native-paper";
const { height, width } = Dimensions.get("window");

const Card = ({ children }: { children: React.ReactNode }) => {
  const { colors } = useTheme();
  return (
    <View
      style={[
        styles.shadowWrap,
        {
          shadowColor: colors.shadow ?? "#000",
          backgroundColor: "transparent",
          zIndex: 10,
        },
      ]}
    >
      <View
        style={[
          styles.main,
          {
            borderColor: "rgba(0,0,0,0.06)",
            backgroundColor: colors.onBackground,
          },
        ]}
      >
        <View style={[styles.accentBar, { backgroundColor: colors.primary }]} />
        <View style={styles.content}>{children}</View>
      </View>
    </View>
  );
};

export default Card;

const styles = StyleSheet.create({
  shadowWrap: {
    alignSelf: "center",
    height: height * 0.73,
    width: width * 0.93,
    marginTop: height * 0.03,
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.12,
    shadowRadius: 12,
    elevation: 8,
    borderRadius: 20,
  },
  main: {
    flex: 1,
    borderRadius: 20,
    overflow: "hidden",
  },
  accentBar: {
    height: 10,
    width: "100%",
  },
  content: {
    flex: 1,
    paddingHorizontal: 20,
    paddingVertical: 25,
    justifyContent: "flex-start",
    alignItems: "stretch",
    width: "100%",
  },
});
