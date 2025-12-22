import React, { useRef, useState } from "react";
import {
  View,
  StyleSheet,
  Dimensions,
  TouchableOpacity,
  Animated,
  Text,
} from "react-native";
import { useTheme } from "react-native-paper";
import { useNavigation } from "@react-navigation/native";
import { useTranslation } from "react-i18next";
import { createAnimation } from "../../utils/animationHelper";
import { handleFilterOffers } from "../../api/filter/handleFilterOffers";
import { useAuth } from "../../contexts/AuthContext";
import { getAllOffers } from "../../api/offers/handleOffersApi";
import { getAllTags } from "../../api/filter/handleTags";

const { width, height } = Dimensions.get("window");
const Filter = () => {
  const { colors } = useTheme();
  const [isActive, setIsActive] = useState(false);
  const slideAnim = useRef(new Animated.Value(-height)).current;
  const firstBarRotation = useRef(new Animated.Value(0)).current;
  const secondBarRotation = useRef(new Animated.Value(0)).current;
  const moveThirdBarY = useRef(new Animated.Value(0)).current;
  const moveThirdBarX = useRef(new Animated.Value(0)).current;
  const menuDisplay = useRef(new Animated.Value(0)).current;
  const navigation = useNavigation<any>();
  const { t } = useTranslation();
  const widthAnim = useRef(new Animated.Value(10)).current;
  const hasPressed = useRef(false);
  const { tokens } = useAuth();

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
        createAnimation(widthAnim, next ? 30 : 10, 500, 0, false),
        createAnimation(slideAnim, next ? 0 : -height, 350, 0, true),
      ]).start(() => {
        hasPressed.current = false;
      });
      return next;
    });
  };

  const filterOffers = async () => {
    if (!tokens) return;
    const response = await handleFilterOffers();
    console.log(response);
  };
  const getOffers = async () => {
    if (!tokens) return;
    const response = await getAllOffers();
    console.log(response);
  };
  const getTags = async () => {
    if (!tokens) return;
    const response = await getAllTags();
    console.log(response);
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
            backgroundColor: colors.onBackground,
            borderColor: colors.primary,
          },
        ]}
      />
      {isActive && (
        <View style={styles.exitBox}>
          <TouchableOpacity onPress={filterOffers}>
            <Text style={{ color: colors.primary, fontSize: 16 }}>filtruj</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={getOffers}>
            <Text style={{ color: colors.primary, fontSize: 16 }}>
              getOffers
            </Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={getTags}>
            <Text style={{ color: colors.primary, fontSize: 16 }}>getTags</Text>
          </TouchableOpacity>
        </View>
      )}
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
    width: 30,
    height: 3.2,
    backgroundColor: "#5F5100",
    marginBottom: 4,
    borderRadius: 20,
  },
  bar_2: {
    width: 20,
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
