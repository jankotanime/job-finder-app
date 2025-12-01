import React, { useEffect, useState } from "react";
import { Image, Dimensions, ImageSourcePropType } from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { tryCatch } from "../../utils/try-catch";
const { height, width } = Dimensions.get("window");

const ImageBackground = () => {
  const [darkMode, setDarkMode] = useState<boolean>();
  useEffect(() => {
    const getDakMode = async () => {
      const [isDarkMode, error] = await tryCatch(
        AsyncStorage.getItem("isDarkMode"),
      );
      if (error) console.error("error background: ", error);
      setDarkMode(isDarkMode == null ? undefined : isDarkMode === "true");
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
