/// <reference types="jest" />

import React from "react";
import { render, fireEvent, waitFor } from "@testing-library/react-native";
import FilterContent from "../../../components/filter/FilterContent";

const mockGetAllTags = jest.fn();
const mockHandleFilterOffers = jest.fn();
const mockBuildPhotoUrl = jest.fn((storageKey?: string) =>
  storageKey ? `photo:${storageKey}` : "",
);
const mockSetFiltersList = jest.fn();
const mockClearFilters = jest.fn();
const mockSignOut = jest.fn();
const mockUseFilterState: {
  filters: string[];
  setFiltersList: jest.Mock;
  clearFilters: jest.Mock;
} = {
  filters: [],
  setFiltersList: mockSetFiltersList,
  clearFilters: mockClearFilters,
};

jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

jest.mock("../../../api/filter/handleTags", () => ({
  getAllTags: (...args: any[]) => mockGetAllTags(...args),
}));

jest.mock("../../../api/filter/handleFilterOffers", () => ({
  handleFilterOffers: (...args: any[]) => mockHandleFilterOffers(...args),
}));

jest.mock("../../../utils/photoUrl", () => ({
  buildPhotoUrl: (...args: any[]) => mockBuildPhotoUrl(...args),
}));

jest.mock("../../../contexts/AuthContext", () => ({
  useAuth: () => ({
    userInfo: { userId: "owner-1" },
    signOut: mockSignOut,
  }),
}));

jest.mock("../../../hooks/useFilter", () => ({
  __esModule: true,
  default: () => mockUseFilterState,
}));

jest.mock("../../../components/filter/FilterCollapsibleSection", () => {
  const React = require("react");
  const { View, Text, Pressable } = require("react-native");

  return ({ section, toggleTag }: any) => (
    <View>
      <Text>{section.title}</Text>
      {section.tags.map((tag: any) => (
        <Pressable key={tag.id} onPress={() => toggleTag(tag.id)}>
          <Text>{tag.name}</Text>
        </Pressable>
      ))}
    </View>
  );
});

describe("FilterContent component", () => {
  beforeEach(() => {
    mockGetAllTags.mockReset();
    mockHandleFilterOffers.mockReset();
    mockBuildPhotoUrl.mockClear();
    mockSetFiltersList.mockReset();
    mockClearFilters.mockReset();
    mockSignOut.mockReset();
    mockUseFilterState.filters = [];

    mockSetFiltersList.mockResolvedValue(undefined);
    mockClearFilters.mockResolvedValue(undefined);

    mockGetAllTags.mockResolvedValue({
      body: {
        data: {
          content: [
            {
              id: "tag-1",
              name: "React",
              categoryName: "Skills",
              categoryColor: "#000",
            },
            {
              id: "tag-2",
              name: "Node",
              categoryName: "Skills",
              categoryColor: "#000",
            },
          ],
        },
      },
    });

    mockHandleFilterOffers.mockResolvedValue({
      body: {
        data: {
          content: [
            {
              id: "offer-own",
              owner: { id: "owner-1" },
              photo: { storageKey: "own-photo" },
            },
            {
              id: "offer-visible",
              owner: { id: "owner-2" },
              photo: { storageKey: "visible-photo" },
            },
          ],
        },
      },
    });
  });

  it("loads and renders grouped categories", async () => {
    const { getByText } = render(<FilterContent setOffersData={jest.fn()} />);

    await waitFor(() => {
      expect(getByText("Skills")).toBeTruthy();
      expect(getByText("React")).toBeTruthy();
    });
  });

  it("applies selected tags and updates offers", async () => {
    const setOffersData = jest.fn();
    const onClose = jest.fn();

    const { getByText } = render(
      <FilterContent setOffersData={setOffersData} onClose={onClose} />,
    );

    await waitFor(() => {
      expect(getByText("React")).toBeTruthy();
    });

    fireEvent.press(getByText("React"));
    fireEvent.press(getByText("filter.apply"));

    await waitFor(() => {
      expect(mockSetFiltersList).toHaveBeenCalledWith(["tag-1"]);
      expect(mockHandleFilterOffers).toHaveBeenCalledWith(
        { tags: ["tag-1"] },
        { page: 0, size: 20 },
      );
      expect(setOffersData).toHaveBeenCalledWith([
        expect.objectContaining({ id: "offer-visible" }),
      ]);
      expect(onClose).toHaveBeenCalled();
    });
  });

  it("clears filters and refreshes offers", async () => {
    const setOffersData = jest.fn();
    const onClose = jest.fn();

    const { getByText } = render(
      <FilterContent setOffersData={setOffersData} onClose={onClose} />,
    );

    await waitFor(() => {
      expect(getByText("filter.clear")).toBeTruthy();
    });

    fireEvent.press(getByText("filter.clear"));

    await waitFor(() => {
      expect(mockClearFilters).toHaveBeenCalledTimes(1);
      expect(mockHandleFilterOffers).toHaveBeenCalledWith(
        { tags: [] },
        { page: 0, size: 20 },
      );
      expect(setOffersData).toHaveBeenCalledWith([
        expect.objectContaining({ id: "offer-visible" }),
      ]);
      expect(onClose).toHaveBeenCalled();
    });
  });
});
