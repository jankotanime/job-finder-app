import React from "react";
import { Button } from "react-native-paper";

interface ButtonProps {
  text: string;
  onPress?: () => void;
  styles: object;
  white?: boolean;
}
const HomeLoginButton = ({ styles, white, onPress, text }: ButtonProps) => {
  return (
    <Button
      mode="text"
      style={styles}
      labelStyle={{ fontSize: 20, fontWeight: "600" }}
      textColor={white ? "white" : ""}
      onPress={onPress}
    >
      {text}
    </Button>
  );
};
export default HomeLoginButton;
