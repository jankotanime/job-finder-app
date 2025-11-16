import React, { useEffect, useState } from "react";
import { Image, Dimensions, ImageSourcePropType } from "react-native";
import { useThemeContext } from "../../contexts/ThemeContext";
import AsyncStorage from "@react-native-async-storage/async-storage";
const { height, width } = Dimensions.get("window");

const ImageBackground = () => {
  const [darkMode, setDarkMode] = useState<boolean>();
  useEffect(() => {
    const getDakMode = async () => {
      try {
        const isDarkMode = await AsyncStorage.getItem("isDarkMode");
        setDarkMode(isDarkMode == null ? undefined : isDarkMode === "true");
      } catch (e) {
        console.error("error background: ", e);
      }
    };
    getDakMode();
  }, []);
  const backgroundImage: ImageSourcePropType = darkMode
    ? require("../../../assets/images/background_dark.png")
    : require("../../../assets/images/background.png");
  return (
    <Image
      testID="background-image"
      source={backgroundImage}
      style={{ width, height, resizeMode: "cover", zIndex: -1 }}
    />
  );
};

export default ImageBackground;
