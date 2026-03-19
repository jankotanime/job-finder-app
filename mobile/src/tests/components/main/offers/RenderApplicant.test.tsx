/// <reference types="jest" />

import React from "react";
import { render, fireEvent } from "@testing-library/react-native";
import RenderApplicant from "../../../../components/main/offers/RenderApplicant";

jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

jest.mock("@expo/vector-icons", () => ({
  Ionicons: "Ionicons",
}));

describe("RenderApplicant component", () => {
  it("renders candidate name and status", () => {
    const onPress = jest.fn();
    const item = {
      id: "app-1",
      status: "PENDING",
      candidate: { firstName: "Jan", lastName: "Kowalski" },
    };

    const { getByText } = render(
      <RenderApplicant item={item} onPress={onPress} selected={false} />,
    );

    expect(getByText("Jan Kowalski")).toBeTruthy();
    expect(getByText("offer.status: PENDING")).toBeTruthy();
  });

  it("calls onPress with item", () => {
    const onPress = jest.fn();
    const item = {
      id: "app-2",
      status: "ACCEPTED",
      candidate: { username: "jk" },
    };

    const { getByText } = render(
      <RenderApplicant item={item} onPress={onPress} selected={true} />,
    );

    fireEvent.press(getByText("jk"));

    expect(onPress).toHaveBeenCalledWith(item);
  });
});
