import React from "react";
import { TextInput } from "react-native-paper";
import { StyleSheet, Dimensions } from "react-native";
import type { KeyboardTypeOptions } from "react-native";

interface TextInputProps {
  placeholder: string;
  secure?: boolean;
  onChangeText?: (text: string) => void;
  mode: "flat" | "outlined";
  value: string;
  multiline?: boolean;
  numberOfLines?: number;
  style?: any;
  onFocus?: () => void;
  onBlur?: () => void;
  keyboardType?: KeyboardTypeOptions;
}

const { width, height } = Dimensions.get("window");

const Input = ({
  placeholder,
  value,
  mode,
  onChangeText,
  secure,
  multiline,
  numberOfLines,
  style,
  onFocus,
  onBlur,
  keyboardType,
}: TextInputProps) => {
  return (
    <TextInput
      label={placeholder}
      value={value}
      mode={mode}
      multiline={multiline}
      numberOfLines={numberOfLines}
      onChangeText={onChangeText}
      secureTextEntry={secure}
      keyboardType={keyboardType}
      style={[styles.input, multiline && styles.textarea, style]}
      theme={{ roundness: 20 }}
      onFocus={onFocus}
      onBlur={onBlur}
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
  textarea: {
    minHeight: 140,
    textAlignVertical: "top",
  },
});
