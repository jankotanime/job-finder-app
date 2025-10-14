import React from "react"
import { Image, Dimensions, ImageSourcePropType, ImageStyle, StyleProp } from "react-native"

const { height, width} = Dimensions.get("window")

type Props = {
    width?: number
    height?: number
}

const ImageBackground: React.FC<Props> = () => {
    const backgroundImage: ImageSourcePropType = require("../../assets/images/background.png")
    return (
        <Image
            source={backgroundImage}
            style={{ width, height, resizeMode: "cover", zIndex: -1 }}
        />
    )
}

export default ImageBackground