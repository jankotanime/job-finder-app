/// <reference types="jest" />

import React from "react";
import { render, fireEvent, waitFor } from "@testing-library/react-native";
import GoogleButton from "../../../components/pre-login/GoogleButton";

const mockSignWithGoogle = jest.fn();
const mockNavigate = jest.fn();
const mockGetErrorMessage = jest.fn((error: string) => `mapped:${error}`);

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

describe("GoogleButton component", () => {
  beforeEach(() => {
    mockSignWithGoogle.mockReset();
    mockNavigate.mockReset();
    mockGetErrorMessage.mockClear();
  });

  it("calls google auth on press", async () => {
    mockSignWithGoogle.mockResolvedValue({ status: "LOGGED_IN" });
    const setError = jest.fn();

    const { getByText } = render(
      <GoogleButton screen="Home" setError={setError} />,
    );

    fireEvent.press(getByText("pre-login-home.sign_up_with_google"));

    await waitFor(() => {
      expect(mockSignWithGoogle).toHaveBeenCalledTimes(1);
    });

    expect(mockSignWithGoogle).toHaveBeenCalledWith(
      expect.objectContaining({
        setIsSubmiting: expect.any(Function),
        navigation: expect.any(Object),
      }),
    );
    expect(setError).not.toHaveBeenCalled();
  });

  it("maps auth error and calls setError", async () => {
    mockSignWithGoogle.mockResolvedValue({ status: "ERROR", error: "boom" });
    const setError = jest.fn();

    const { getByText } = render(
      <GoogleButton screen="Home" setError={setError} />,
    );

    fireEvent.press(getByText("pre-login-home.sign_up_with_google"));

    await waitFor(() => {
      expect(setError).toHaveBeenCalledWith("mapped:boom");
    });
  });
});
