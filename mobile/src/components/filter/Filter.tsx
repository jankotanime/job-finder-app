import React, { useRef, useState } from "react";
import {
  View,
  StyleSheet,
  Dimensions,
  TouchableOpacity,
  Animated,
} from "react-native";
import { useTheme } from "react-native-paper";
import { createAnimation } from "../../utils/animationHelper";
import FilterContent from "./FilterContent";
import { Offer } from "../../types/Offer";

export interface FilterProps {
  setOffersData: (data: Offer[]) => void;
}
const { width, height } = Dimensions.get("window");
const Filter = ({ setOffersData }: FilterProps) => {
  const { colors } = useTheme();
  const [isActive, setIsActive] = useState(false);
  const slideAnim = useRef(new Animated.Value(height + 10)).current;
  const firstBarRotation = useRef(new Animated.Value(0)).current;
  const secondBarRotation = useRef(new Animated.Value(0)).current;
  const moveThirdBarY = useRef(new Animated.Value(0)).current;
  const moveThirdBarX = useRef(new Animated.Value(0)).current;
  const menuDisplay = useRef(new Animated.Value(0)).current;
  const widthAnim = useRef(new Animated.Value(10)).current;
  const hasPressed = useRef(false);

  const turnLeft = firstBarRotation.interpolate({
    inputRange: [0, 1],
    outputRange: ["0deg", "45deg"],
  });
  const turnRight = secondBarRotation.interpolate({
    inputRange: [0, 1],
    outputRange: ["0deg", "-45deg"],
  });

  const handlePress = () => {
    if (hasPressed.current) return;
    hasPressed.current = true;
    setIsActive((prev) => {
      const next = !prev;
      Animated.parallel([
        createAnimation(firstBarRotation, next ? 1 : 0),
        createAnimation(secondBarRotation, next ? 1 : 0),
        createAnimation(moveThirdBarY, next ? -10 : 0),
        createAnimation(moveThirdBarX, next ? 10 : 0),
        createAnimation(menuDisplay, next ? -height : 0, 300),
        createAnimation(widthAnim, next ? 25 : 10, 300, 0, false),
        createAnimation(slideAnim, next ? 0 : height + 10, 300, 0, true),
      ]).start(() => {
        hasPressed.current = false;
      });
      return next;
    });
  };

  const closeFilter = () => {
    if (!isActive || hasPressed.current) return;
    hasPressed.current = true;
    setIsActive(false);
    Animated.parallel([
      createAnimation(firstBarRotation, 0),
      createAnimation(secondBarRotation, 0),
      createAnimation(moveThirdBarY, 0),
      createAnimation(moveThirdBarX, 0),
      createAnimation(menuDisplay, 0, 300),
      createAnimation(widthAnim, 10, 300, 0, false),
      createAnimation(slideAnim, height + 10, 300, 0, true),
    ]).start(() => {
      hasPressed.current = false;
    });
  };

  return (
    <>
      <TouchableOpacity
        style={[styles.filter, { zIndex: isActive ? 16 : 13 }]}
        onPress={handlePress}
      >
        <Animated.View
          style={[
            styles.bar_1,
            { transform: [{ rotate: turnLeft }] },
            { backgroundColor: colors.primary },
          ]}
        />
        <Animated.View
          style={[
            styles.bar_2,
            {
              opacity: firstBarRotation.interpolate({
                inputRange: [0, 1],
                outputRange: [1, 0],
              }),
            },
            { backgroundColor: colors.primary },
          ]}
        />
        <Animated.View
          style={[
            styles.bar_3,
            {
              transform: [
                { rotate: turnRight },
                { translateY: moveThirdBarY },
                { translateX: moveThirdBarX },
              ],
              width: widthAnim,
            },
            { backgroundColor: colors.primary },
          ]}
        />
      </TouchableOpacity>
      <Animated.View
        style={[
          styles.animBox,
          {
            transform: [{ translateY: slideAnim }],
            backgroundColor: colors.background,
            borderColor: colors.primary,
          },
        ]}
      >
        <View style={styles.exitBox}>
          <FilterContent setOffersData={setOffersData} onClose={closeFilter} />
        </View>
      </Animated.View>
    </>
  );
};

export default Filter;

const styles = StyleSheet.create({
  filter: {
    position: "absolute",
    top: height * 0.09,
    left: 30,
    zIndex: 12,
    alignItems: "center",
  },
  animBox: {
    position: "absolute",
    width: width + 10,
    height: height + 10,
    borderBottomLeftRadius: 40,
    borderBottomRightRadius: 40,
    backgroundColor: "white",
    top: -10,
    left: -5,
    zIndex: 15,
  },
  exitBox: {
    position: "absolute",
    backgroundColor: "transparent",
    zIndex: 15,
    width: width,
    height: height,
    alignItems: "center",
    justifyContent: "center",
  },
  bar_1: {
    width: 25,
    height: 3.2,
    backgroundColor: "#5F5100",
    marginBottom: 4,
    borderRadius: 20,
  },
  bar_2: {
    width: 18,
    height: 3.2,
    backgroundColor: "#5F5100",
    marginBottom: 4,
    borderRadius: 20,
  },
  bar_3: {
    height: 3.2,
    backgroundColor: "#5F5100",
    marginBottom: 4,
    borderRadius: 20,
  },
});
