/// <reference types="jest" />

import React from "react";
import { render, fireEvent } from "@testing-library/react-native";
import { Animated, TouchableOpacity } from "react-native";
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
  let parallelSpy: jest.SpyInstance;

  beforeEach(() => {
    mockCreateAnimation.mockReset();
    mockCreateAnimation.mockReturnValue({
      start: (cb?: (result: { finished: boolean }) => void) =>
        cb && cb({ finished: true }),
    });

    parallelSpy = jest.spyOn(Animated, "parallel").mockImplementation(
      () =>
        ({
          start: (cb?: (result: { finished: boolean }) => void) =>
            cb && cb({ finished: true }),
          stop: jest.fn(),
          reset: jest.fn(),
        }) as any,
    );
  });

  afterEach(() => {
    if (parallelSpy) {
      parallelSpy.mockRestore();
    }
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

  it("ignores close when filter is not active", () => {
    const { getByText } = render(<Filter setOffersData={jest.fn()} />);

    fireEvent.press(getByText("close-filter"));

    expect(mockCreateAnimation).not.toHaveBeenCalled();
  });

  it("ignores repeated presses while animation is in progress", () => {
    parallelSpy.mockImplementation(
      () =>
        ({
          start: () => {
            // intentionally do not call callback - keeps hasPressed=true
          },
          stop: jest.fn(),
          reset: jest.fn(),
        }) as any,
    );

    const { UNSAFE_getByType, getByText } = render(
      <Filter setOffersData={jest.fn()} />,
    );

    const button = UNSAFE_getByType(TouchableOpacity);
    fireEvent.press(button);
    const callsAfterFirstPress = mockCreateAnimation.mock.calls.length;

    fireEvent.press(button);
    fireEvent.press(getByText("close-filter"));

    expect(mockCreateAnimation.mock.calls.length).toBe(callsAfterFirstPress);
  });

  it("runs close branch when filter button is pressed second time", () => {
    const { UNSAFE_getByType } = render(<Filter setOffersData={jest.fn()} />);

    const button = UNSAFE_getByType(TouchableOpacity);
    fireEvent.press(button);
    mockCreateAnimation.mockClear();

    fireEvent.press(button);

    const calledWithCloseValues = mockCreateAnimation.mock.calls.some(
      (call) => call[1] === 0,
    );

    expect(calledWithCloseValues).toBe(true);
  });
});
