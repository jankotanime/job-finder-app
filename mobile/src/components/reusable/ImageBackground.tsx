import React from "react";
import { Image, Dimensions, ImageSourcePropType } from "react-native";

const { height, width } = Dimensions.get("window");

const ImageBackground = () => {
  const backgroundImage: ImageSourcePropType = require("../../../assets/images/background.png");
  return (
    <Image
      testID="background-image"
      source={backgroundImage}
      style={{ width, height, resizeMode: "cover", zIndex: -1 }}
    />
  );
};

export default ImageBackground;
