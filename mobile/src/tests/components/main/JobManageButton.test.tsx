/// <reference types="jest" />

import React from "react";
import { render, fireEvent } from "@testing-library/react-native";
import { TouchableOpacity } from "react-native";
import JobManageButton from "../../../components/main/JobManageButton";

const mockNavigate = jest.fn();

jest.mock("@expo/vector-icons", () => ({
  Entypo: "Entypo",
}));

jest.mock("@react-navigation/native", () => ({
  useNavigation: () => ({
    navigate: mockNavigate,
  }),
}));

describe("JobManageButton component", () => {
  beforeEach(() => {
    mockNavigate.mockReset();
  });

  it("navigates to ChooseJobScreen after press", () => {
    const { UNSAFE_getByType } = render(<JobManageButton />);
    const button = UNSAFE_getByType(TouchableOpacity);

    fireEvent.press(button);

    expect(mockNavigate).toHaveBeenCalledWith("ChooseJobScreen");
  });
});
