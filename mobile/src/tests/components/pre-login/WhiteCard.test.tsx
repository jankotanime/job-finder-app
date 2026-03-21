/// <reference types="jest" />

import React from "react";
import { render } from "@testing-library/react-native";
import { Text } from "react-native";
import WhiteCard from "../../../components/pre-login/WhiteCard";

describe("WhiteCard component", () => {
  it("renders children inside card container", () => {
    const { getByText } = render(
      <WhiteCard>
        <Text>child-content</Text>
      </WhiteCard>,
    );

    expect(getByText("child-content")).toBeTruthy();
  });
});
