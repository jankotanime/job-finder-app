/// <reference types="jest" />

import React from "react";
import { render, fireEvent, waitFor } from "@testing-library/react-native";
import { RefreshControl, TouchableOpacity } from "react-native";
import OfferGrid from "../../../../components/main/offers/OfferGrid";

const mockNavigate = jest.fn();
const mockRefreshOffers = jest.fn();
const mockBuildPhotoUrl = jest.fn((raw?: string) =>
  raw ? `https://cdn/${raw}` : "",
);

const mockStorageOffers = [
  {
    id: "offer-1",
    title: "Frontend React",
    dateAndTime: "2026-03-14T10:00:00",
    salary: 120,
    offerPhoto: "first.png",
  },
  {
    id: "offer-2",
    title: "Backend Node",
    dateAndTime: "2026-03-14T11:00:00",
    photo: { storageKey: "second.png" },
  },
];

jest.mock("@react-navigation/native", () => ({
  useNavigation: () => ({
    navigate: mockNavigate,
  }),
}));

jest.mock("../../../../contexts/OfferStorageContext", () => ({
  useOfferStorageContext: () => ({
    storageOffers: mockStorageOffers,
    refreshOffers: mockRefreshOffers,
  }),
}));

jest.mock("../../../../utils/photoUrl", () => ({
  buildPhotoUrl: (...args: any[]) => mockBuildPhotoUrl(...args),
}));

describe("OfferGrid component", () => {
  beforeEach(() => {
    mockNavigate.mockReset();
    mockRefreshOffers.mockReset();
    mockBuildPhotoUrl.mockClear();
  });

  it("renders offers from storage", () => {
    const { getByText } = render(<OfferGrid />);

    expect(getByText("Frontend React")).toBeTruthy();
    expect(getByText("Backend Node")).toBeTruthy();
    expect(getByText("120 zł")).toBeTruthy();
  });

  it("opens offer details after tile press", () => {
    const { getByText } = render(<OfferGrid />);

    fireEvent.press(getByText("Frontend React"));

    expect(mockNavigate).toHaveBeenCalledWith("StorageOfferDetails", {
      offer: expect.objectContaining({ id: "offer-1" }),
    });
  });

  it("calls refreshOffers on pull to refresh", async () => {
    const { UNSAFE_getByType } = render(<OfferGrid />);

    const refreshControl = UNSAFE_getByType(RefreshControl);
    fireEvent(refreshControl, "refresh");

    await waitFor(() => {
      expect(mockRefreshOffers).toHaveBeenCalledTimes(1);
    });
  });
});
