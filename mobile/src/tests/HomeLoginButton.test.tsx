/// <reference types="jest" />

import React from "react";
import { render, fireEvent } from "@testing-library/react-native";
import HomeLoginButton from "../components/pre-login/HomeLoginButton";

describe("HomeLoginButton component", () => {
  it("renders correctly with good props", () => {
    const { getByText, toJSON } = render(
      <HomeLoginButton
        styles={{ width: 50, height: 48 }}
        onPress={jest.fn()}
        text="login"
      />,
    );
    expect(getByText("login")).toBeTruthy();
    expect(toJSON()).toMatchSnapshot();
  });
  it("calls onPress when pressed", () => {
    const onPress = jest.fn();
    const { getByText } = render(
      <HomeLoginButton
        styles={{ width: 50, height: 48 }}
        onPress={onPress}
        text="login"
      />,
    );
    const button = getByText("login");
    fireEvent.press(button);
    expect(onPress).toHaveBeenCalled();
  });
});
