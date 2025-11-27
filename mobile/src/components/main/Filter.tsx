import React, { useRef, useState } from "react";
import {
  View,
  StyleSheet,
  Dimensions,
  TouchableOpacity,
  Animated,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { createAnimation } from "../../utils/animationHelper";
import { useTheme } from "react-native-paper";

const { width, height } = Dimensions.get("window");
const Filter = () => {
  const { colors } = useTheme();
  const [isActive, setIsActive] = useState(false);
  const slideAnim = useRef(new Animated.Value(-height * 0.5)).current;

  const handlePress = () => {
    setIsActive((prev) => {
      const next = !prev;
      createAnimation(
        slideAnim,
        next ? 0 : -height * 0.5,
        350,
        0,
        true,
      ).start();
      return next;
    });
  };
  const cancel = () => {
    setIsActive(false);
    createAnimation(slideAnim, -height * 0.5, 350, 0, true).start();
  };
  return (
    <>
      <TouchableOpacity style={styles.filter} onPress={handlePress}>
        <Ionicons name="filter" size={30} color={colors.primary} />
      </TouchableOpacity>
      <Animated.View
        style={[
          styles.animBox,
          {
            transform: [{ translateY: slideAnim }],
            backgroundColor: colors.onBackground,
            borderColor: colors.primary,
          },
        ]}
        onTouchEnd={cancel}
      />
      {isActive && <TouchableOpacity style={styles.exitBox} onPress={cancel} />}
    </>
  );
};

export default Filter;

const styles = StyleSheet.create({
  filter: {
    position: "absolute",
    top: height * 0.08,
    left: 30,
    zIndex: 12,
  },
  animBox: {
    position: "absolute",
    width: width + 10,
    height: height * 0.4,
    borderBottomLeftRadius: 40,
    borderBottomRightRadius: 40,
    backgroundColor: "white",
    top: -10,
    left: -5,
    zIndex: 16,
    borderBottomWidth: 10,
  },
  exitBox: {
    position: "absolute",
    backgroundColor: "transparent",
    zIndex: 15,
    width: width,
    height: height,
  },
});
