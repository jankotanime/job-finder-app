/// <reference types="jest" />

import React from "react";
import { render, fireEvent } from "@testing-library/react-native";
import { TouchableOpacity } from "react-native";
import CvChoseButton from "../../../components/main/CvChoseButton";

const mockNavigate = jest.fn();

jest.mock("@expo/vector-icons", () => ({
  AntDesign: "AntDesign",
}));

jest.mock("@react-navigation/native", () => ({
  useNavigation: () => ({
    navigate: mockNavigate,
  }),
}));

describe("CvChoseButton component", () => {
  beforeEach(() => {
    mockNavigate.mockReset();
  });

  it("navigates to CvMain after press", () => {
    const { UNSAFE_getByType } = render(<CvChoseButton />);
    const button = UNSAFE_getByType(TouchableOpacity);

    fireEvent.press(button);

    expect(mockNavigate).toHaveBeenCalledWith("CvMain");
  });
});
