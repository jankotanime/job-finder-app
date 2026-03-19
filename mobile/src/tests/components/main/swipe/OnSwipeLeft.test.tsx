/// <reference types="jest" />

import React from "react";
import { render } from "@testing-library/react-native";
import OnSwipeLeft from "../../../../components/main/swipe/OnSwipeLeft";

jest.mock("@expo/vector-icons/Ionicons", () => {
  const React = require("react");
  const { Text } = require("react-native");
  return ({ name, size }: { name: string; size: number }) => (
    <Text>{`${name}:${size}`}</Text>
  );
});

describe("OnSwipeLeft component", () => {
  it("renders inactive icon size", () => {
    const { getByText } = render(<OnSwipeLeft isActive={false} />);
    expect(getByText("close:48")).toBeTruthy();
  });

  it("renders active icon size", () => {
    const { getByText } = render(<OnSwipeLeft isActive={true} />);
    expect(getByText("close:200")).toBeTruthy();
  });
});
