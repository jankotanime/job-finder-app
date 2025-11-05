import React, { useRef, useEffect } from "react";
import { Animated, Text, StyleSheet, Dimensions } from "react-native";
const { width } = Dimensions.get("window");

type error = { error: string };
const ErrorNotification = ({ error }: error) => {
  const slideAnim = useRef(new Animated.Value(-100)).current;
  useEffect(() => {
    Animated.sequence([
      Animated.timing(slideAnim, {
        toValue: 0,
        duration: 500,
        useNativeDriver: true,
      }),
      Animated.delay(6500),
      Animated.timing(slideAnim, {
        toValue: -100,
        duration: 500,
        useNativeDriver: true,
      }),
    ]).start();
  }, []);
  const cancel = () => {
    Animated.timing(slideAnim, {
      toValue: -100,
      duration: 500,
      useNativeDriver: true,
    }).start();
  };
  return (
    <Animated.View
      style={[styles.container, { transform: [{ translateY: slideAnim }] }]}
      onTouchEnd={cancel}
    >
      <Text style={styles.text}>{error}</Text>
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
    backgroundColor: "#ffebee",
    marginBottom: 10,
  },
  text: {
    textAlign: "center",
    fontSize: 14,
    color: "#c62828",
  },
});
