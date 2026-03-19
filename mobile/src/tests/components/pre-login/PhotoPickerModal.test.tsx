/// <reference types="jest" />

import React from "react";
import { render, fireEvent } from "@testing-library/react-native";
import PhotoPickerModal from "../../../components/pre-login/PhotoPickerModal";

jest.mock("@expo/vector-icons", () => ({
  Ionicons: "Ionicons",
}));

jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

describe("PhotoPickerModal component", () => {
  it("renders translated labels when visible", () => {
    const { getByText } = render(
      <PhotoPickerModal visible={true} onClose={jest.fn()} />,
    );

    expect(getByText("profileCompletion.photoPicker.title")).toBeTruthy();
    expect(getByText("profileCompletion.photoPicker.camera")).toBeTruthy();
    expect(getByText("profileCompletion.photoPicker.gallery")).toBeTruthy();
    expect(getByText("profileCompletion.photoPicker.cancel")).toBeTruthy();
  });

  it("calls camera, gallery and close handlers", () => {
    const onClose = jest.fn();
    const onPickCamera = jest.fn();
    const onPickGallery = jest.fn();

    const { getByText } = render(
      <PhotoPickerModal
        visible={true}
        onClose={onClose}
        onPickCamera={onPickCamera}
        onPickGallery={onPickGallery}
      />,
    );

    fireEvent.press(getByText("profileCompletion.photoPicker.camera"));
    fireEvent.press(getByText("profileCompletion.photoPicker.gallery"));
    fireEvent.press(getByText("profileCompletion.photoPicker.cancel"));

    expect(onPickCamera).toHaveBeenCalledTimes(1);
    expect(onPickGallery).toHaveBeenCalledTimes(1);
    expect(onClose).toHaveBeenCalledTimes(1);
  });
});
