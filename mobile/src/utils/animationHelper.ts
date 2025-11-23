import { Animated } from "react-native";

export const createAnimation = (
  animatedValue: Animated.Value,
  toValue: number,
  duration: number = 400,
  delay: number = 0,
  useNativeDriver: boolean = false,
) => {
  return Animated.timing(animatedValue, {
    toValue,
    duration,
    delay,
    useNativeDriver,
  });
};
