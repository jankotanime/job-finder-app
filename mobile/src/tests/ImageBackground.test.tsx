/// <reference types="jest" />

import React from "react";
import { render, fireEvent } from "@testing-library/react-native";
import ImageBackground from "../components/reusable/ImageBackground";
import { Dimensions } from "react-native";

jest.spyOn(Dimensions, "get").mockReturnValue({
  width: 360,
  height: 640,
  scale: 2,
  fontScale: 2,
});

describe("ImageBackground component", () => {
  it("renders correctly", () => {
    const { toJSON, getByTestId } = render(<ImageBackground />);
    expect(toJSON()).toMatchSnapshot();
    expect(getByTestId("background-image")).toBeTruthy();
  });
});
