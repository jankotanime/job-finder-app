import React from "react";
import { TextInput } from "react-native-paper";
import { StyleSheet, Dimensions } from "react-native";

interface TextInputProps {
  placeholder: string;
  secure?: boolean;
  onChangeText?: (text: string) => void;
  mode: "flat" | "outlined";
  value: string;
}
const { width, height } = Dimensions.get("window");
const Input = ({
  placeholder,
  value,
  mode,
  onChangeText,
  secure,
}: TextInputProps) => {
  return (
    <TextInput
      label={placeholder}
      value={value}
      mode={mode}
      onChangeText={onChangeText}
      style={styles.input}
      secureTextEntry={secure}
      theme={{ roundness: 20 }}
    />
  );
};

export default Input;

const styles = StyleSheet.create({
  input: {
    alignSelf: "center",
    width: width * 0.8,
    borderRadius: 12,
    marginTop: 10,
    top: height * 0.06,
  },
});
