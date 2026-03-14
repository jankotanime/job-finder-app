/// <reference types="jest" />

import React from "react";
import { render, fireEvent } from "@testing-library/react-native";
import { TouchableOpacity } from "react-native";
import Filter from "../../../components/filter/Filter";

const mockCreateAnimation = jest.fn();

jest.mock("../../../utils/animationHelper", () => ({
  createAnimation: (...args: any[]) => mockCreateAnimation(...args),
}));

jest.mock("../../../components/filter/FilterContent", () => {
  const React = require("react");
  const { Pressable, Text } = require("react-native");

  return ({ onClose }: { onClose?: () => void }) => (
    <Pressable onPress={onClose}>
      <Text>close-filter</Text>
    </Pressable>
  );
});

describe("Filter component", () => {
  beforeEach(() => {
    mockCreateAnimation.mockReset();
    mockCreateAnimation.mockReturnValue({
      start: (cb?: (result: { finished: boolean }) => void) =>
        cb && cb({ finished: true }),
    });
  });

  it("runs open animations on filter button press", () => {
    const { UNSAFE_getByType } = render(<Filter setOffersData={jest.fn()} />);

    const button = UNSAFE_getByType(TouchableOpacity);
    fireEvent.press(button);

    expect(mockCreateAnimation).toHaveBeenCalled();
  });

  it("runs close animations when close callback is triggered", () => {
    const { UNSAFE_getByType, getByText } = render(
      <Filter setOffersData={jest.fn()} />,
    );

    const button = UNSAFE_getByType(TouchableOpacity);
    fireEvent.press(button);
    const callsAfterOpen = mockCreateAnimation.mock.calls.length;

    fireEvent.press(getByText("close-filter"));

    expect(mockCreateAnimation.mock.calls.length).toBeGreaterThan(
      callsAfterOpen,
    );
  });
});
