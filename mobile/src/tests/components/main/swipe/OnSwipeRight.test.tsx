/// <reference types="jest" />

import React from "react";
import { render } from "@testing-library/react-native";
import OnSwipeRight from "../../../../components/main/swipe/OnSwipeRight";

jest.mock("@expo/vector-icons/Ionicons", () => {
  const React = require("react");
  const { Text } = require("react-native");
  return ({ name, size }: { name: string; size: number }) => (
    <Text>{`${name}:${size}`}</Text>
  );
});

describe("OnSwipeRight component", () => {
  it("renders inactive icon size", () => {
    const { getByText } = render(<OnSwipeRight isActive={false} />);
    expect(getByText("checkmark:48")).toBeTruthy();
  });

  it("renders active icon size", () => {
    const { getByText } = render(<OnSwipeRight isActive={true} />);
    expect(getByText("checkmark:200")).toBeTruthy();
  });
});
