import React, { useRef, useEffect } from "react";
import { Animated, Text, StyleSheet, Dimensions } from "react-native";
import { useTheme } from "react-native-paper";
import { createAnimation } from "../../utils/animationHelper";
const { width } = Dimensions.get("window");

type error = { error: string };
const ErrorNotification = ({ error }: error) => {
  const slideAnim = useRef(new Animated.Value(-100)).current;
  const { colors } = useTheme();
  useEffect(() => {
    Animated.sequence([
      createAnimation(slideAnim, 0, 500, 0, true),
      Animated.delay(6500),
      createAnimation(slideAnim, -100, 500, 0, true),
    ]).start();
  }, []);
  const cancel = () => {
    createAnimation(slideAnim, -100, 500, 0, true).start();
  };
  return (
    <Animated.View
      testID="error-notification"
      style={[
        styles.container,
        {
          transform: [{ translateY: slideAnim }],
          backgroundColor: colors.error,
        },
      ]}
      onTouchEnd={cancel}
    >
      <Text style={[styles.text, { color: colors.onError }]}>{error}</Text>
    </Animated.View>
  );
};

export default ErrorNotification;

const styles = StyleSheet.create({
  container: {
    position: "absolute",
    top: 10,
    padding: 10,
    borderRadius: 15,
    width: width * 0.8,
    alignSelf: "center",
    marginBottom: 10,
  },
  text: {
    textAlign: "center",
    fontSize: 14,
  },
});
