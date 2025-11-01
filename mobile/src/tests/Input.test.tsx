/// <reference types="jest" />

import React from "react";
import { render, fireEvent } from "@testing-library/react-native";
import Input from "../components/reusable/Input";

describe("Input component", () => {
  it("renders correctly with an initial value and matches snapshot", () => {
    const { getByDisplayValue, toJSON } = render(
      <Input
        placeholder="email"
        secure={true}
        onChangeText={jest.fn()}
        mode="flat"
        value="jan.kowalski@gmail.com"
      />,
    );
    expect(getByDisplayValue("jan.kowalski@gmail.com")).toBeTruthy();
    expect(toJSON()).toMatchSnapshot();
  });
  it("calls onChangeText when the text changes", () => {
    const onChangeText = jest.fn();
    const { getByDisplayValue } = render(
      <Input
        placeholder="email"
        secure={false}
        onChangeText={onChangeText}
        mode="flat"
        value="jan.kowalski@gmail.com"
      />,
    );
    const input = getByDisplayValue("jan.kowalski@gmail.com");
    fireEvent.changeText(input, "new@example.com");
    expect(onChangeText).toHaveBeenCalledWith("new@example.com");
  });
});
