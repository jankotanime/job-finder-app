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
  filters: string[] | string;
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
  const consoleErrorSpy = jest
    .spyOn(console, "error")
    .mockImplementation(() => {});

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

  afterAll(() => {
    consoleErrorSpy.mockRestore();
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

  it("signs out when loading tags fails", async () => {
    mockGetAllTags.mockRejectedValue(new Error("network"));

    render(<FilterContent setOffersData={jest.fn()} />);

    await waitFor(() => {
      expect(mockSignOut).toHaveBeenCalledTimes(1);
      expect(consoleErrorSpy).toHaveBeenCalledWith(
        "error while getting tags:",
        expect.any(Error),
      );
    });
  });

  it("removes tag when toggled twice before apply", async () => {
    const { getByText } = render(<FilterContent setOffersData={jest.fn()} />);

    await waitFor(() => {
      expect(getByText("React")).toBeTruthy();
    });

    fireEvent.press(getByText("React"));
    fireEvent.press(getByText("React"));
    fireEvent.press(getByText("filter.apply"));

    await waitFor(() => {
      expect(mockSetFiltersList).toHaveBeenCalledWith([]);
      expect(mockHandleFilterOffers).toHaveBeenCalledWith(
        { tags: [] },
        { page: 0, size: 20 },
      );
    });
  });

  it("closes filter even when clear fails", async () => {
    mockClearFilters.mockRejectedValue(new Error("clear failed"));
    const onClose = jest.fn();

    const { getByText } = render(
      <FilterContent setOffersData={jest.fn()} onClose={onClose} />,
    );

    await waitFor(() => {
      expect(getByText("filter.clear")).toBeTruthy();
    });

    fireEvent.press(getByText("filter.clear"));

    await waitFor(() => {
      expect(consoleErrorSpy).toHaveBeenCalledWith(
        "error while clearing filters:",
        expect.any(Error),
      );
      expect(onClose).toHaveBeenCalledTimes(1);
    });
  });

  it("does not hydrate selected tags when filters is not an array", async () => {
    mockUseFilterState.filters = "invalid-filters-state";

    const { getByText } = render(<FilterContent setOffersData={jest.fn()} />);

    await waitFor(() => {
      expect(getByText("React")).toBeTruthy();
    });

    fireEvent.press(getByText("filter.apply"));

    await waitFor(() => {
      expect(mockSetFiltersList).toHaveBeenCalledWith([]);
    });
  });

  it("handles apply when offers response has non-array content", async () => {
    const setOffersData = jest.fn();

    mockHandleFilterOffers.mockResolvedValueOnce({
      body: {
        data: {
          content: null,
        },
      },
    });

    const { getByText } = render(
      <FilterContent setOffersData={setOffersData} />,
    );

    await waitFor(() => {
      expect(getByText("filter.apply")).toBeTruthy();
    });

    fireEvent.press(getByText("filter.apply"));

    await waitFor(() => {
      expect(setOffersData).toHaveBeenCalledWith([]);
    });
  });

  it("handles clear when offers response has non-array content", async () => {
    const setOffersData = jest.fn();

    mockHandleFilterOffers.mockResolvedValueOnce({
      body: {
        data: {
          content: null,
        },
      },
    });

    const { getByText } = render(
      <FilterContent setOffersData={setOffersData} />,
    );

    await waitFor(() => {
      expect(getByText("filter.clear")).toBeTruthy();
    });

    fireEvent.press(getByText("filter.clear"));

    await waitFor(() => {
      expect(setOffersData).toHaveBeenCalledWith([]);
    });
  });
});
