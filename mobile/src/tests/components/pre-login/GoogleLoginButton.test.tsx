/// <reference types="jest" />

import React from "react";
import { render, fireEvent, waitFor } from "@testing-library/react-native";
import { TouchableOpacity } from "react-native";
import GoogleLoginButton from "../../../components/pre-login/GoogleLoginButton";

const mockSignWithGoogle = jest.fn();
const mockNavigate = jest.fn();
const mockGetErrorMessage = jest.fn((error: string) => `mapped:${error}`);

jest.mock("@expo/vector-icons", () => ({
  AntDesign: "AntDesign",
}));

jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

jest.mock("@react-navigation/native", () => ({
  useNavigation: () => ({
    navigate: mockNavigate,
  }),
}));

jest.mock("../../../contexts/AuthContext", () => ({
  useAuth: () => ({
    signWithGoogle: mockSignWithGoogle,
  }),
}));

jest.mock("../../../constans/errorMessages", () => ({
  getErrorMessage: (error: string, _t: (key: string) => string) =>
    mockGetErrorMessage(error),
}));

describe("GoogleLoginButton component", () => {
  beforeEach(() => {
    mockSignWithGoogle.mockReset();
    mockNavigate.mockReset();
    mockGetErrorMessage.mockClear();
  });

  it("renders separator label", () => {
    const { getByText } = render(
      <GoogleLoginButton screen="Home" setError={jest.fn()} />,
    );

    expect(getByText("login.or")).toBeTruthy();
  });

  it("calls google auth on logo press", async () => {
    mockSignWithGoogle.mockResolvedValue({ status: "LOGGED_IN" });
    const setError = jest.fn();

    const { UNSAFE_getByType } = render(
      <GoogleLoginButton screen="Home" setError={setError} />,
    );

    fireEvent.press(UNSAFE_getByType(TouchableOpacity));

    await waitFor(() => {
      expect(mockSignWithGoogle).toHaveBeenCalledTimes(1);
    });

    expect(setError).not.toHaveBeenCalled();
  });

  it("maps auth error and calls setError", async () => {
    mockSignWithGoogle.mockResolvedValue({ status: "ERROR", error: "boom" });
    const setError = jest.fn();

    const { UNSAFE_getByType } = render(
      <GoogleLoginButton screen="Home" setError={setError} />,
    );

    fireEvent.press(UNSAFE_getByType(TouchableOpacity));

    await waitFor(() => {
      expect(setError).toHaveBeenCalledWith("mapped:boom");
    });
  });
});
