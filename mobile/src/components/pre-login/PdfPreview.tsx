import React from "react";
import { View, Dimensions } from "react-native";
import Pdf from "react-native-pdf";

const PDFPreview = ({ uri }: { uri: string }) => {
  return (
    <View style={{ flex: 1 }}>
      <Pdf
        source={{ uri }}
        style={{ flex: 1, width: Dimensions.get("window").width }}
      />
    </View>
  );
};

export default PDFPreview;
