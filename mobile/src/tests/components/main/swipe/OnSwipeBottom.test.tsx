/// <reference types="jest" />

import React from "react";
import { render } from "@testing-library/react-native";
import OnSwipeBottom from "../../../../components/main/swipe/OnSwipeBottom";

jest.mock("@expo/vector-icons/Entypo", () => {
  const React = require("react");
  const { Text } = require("react-native");
  return ({ name, size }: { name: string; size: number }) => (
    <Text>{`${name}:${size}`}</Text>
  );
});

describe("OnSwipeBottom component", () => {
  it("renders inactive icon size", () => {
    const { getByText } = render(<OnSwipeBottom isActive={false} />);
    expect(getByText("box:48")).toBeTruthy();
  });

  it("renders active icon size", () => {
    const { getByText } = render(<OnSwipeBottom isActive={true} />);
    expect(getByText("box:200")).toBeTruthy();
  });
});
