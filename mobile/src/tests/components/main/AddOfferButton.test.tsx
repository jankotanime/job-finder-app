/// <reference types="jest" />

import React from "react";
import { render, fireEvent } from "@testing-library/react-native";
import { TouchableOpacity } from "react-native";
import AddOfferButton from "../../../components/main/AddOfferButton";

const mockNavigate = jest.fn();

jest.mock("@expo/vector-icons", () => ({
  Ionicons: "Ionicons",
}));

jest.mock("@react-navigation/native", () => ({
  useNavigation: () => ({
    navigate: mockNavigate,
  }),
}));

describe("AddOfferButton component", () => {
  beforeEach(() => {
    mockNavigate.mockReset();
  });

  it("navigates to AddOffer after press", () => {
    const { UNSAFE_getByType } = render(<AddOfferButton />);
    const button = UNSAFE_getByType(TouchableOpacity);

    fireEvent.press(button);

    expect(mockNavigate).toHaveBeenCalledWith("AddOffer");
  });
});
