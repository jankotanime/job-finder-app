import { Animated } from "react-native";

export const createAnimation = (
  animatedValue: Animated.Value,
  toValue: number,
  duration: number = 400,
  useNativeDriver: boolean = false,
) => {
  return Animated.timing(animatedValue, {
    toValue,
    duration,
    useNativeDriver,
  });
};
