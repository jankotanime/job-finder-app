/// <reference types="jest" />

import React from "react";
import { render, fireEvent, waitFor } from "@testing-library/react-native";
import FilterContent from "../../../components/filter/FilterContent";

const mockGetAllTags = jest.fn();
const mockSetFiltersList = jest.fn();
const mockClearFilters = jest.fn();
const mockSignOut = jest.fn();

jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

jest.mock("../../../api/filter/handleTags", () => ({
  getAllTags: (...args: any[]) => mockGetAllTags(...args),
}));

jest.mock("../../../contexts/AuthContext", () => ({
  useAuth: () => ({
    userInfo: { userId: "owner-1" },
    signOut: mockSignOut,
  }),
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

  const renderFilterContent = (
    props: Partial<React.ComponentProps<typeof FilterContent>> = {},
  ) => {
    const defaults: React.ComponentProps<typeof FilterContent> = {
      setOffersData: jest.fn(),
      filters: [],
      setFiltersList: mockSetFiltersList,
      clearFilters: mockClearFilters,
    };

    return render(<FilterContent {...defaults} {...props} />);
  };

  beforeEach(() => {
    mockGetAllTags.mockReset();
    mockSetFiltersList.mockReset();
    mockClearFilters.mockReset();
    mockSignOut.mockReset();

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
  });

  afterAll(() => {
    consoleErrorSpy.mockRestore();
  });

  it("loads and renders grouped categories", async () => {
    const { getByText } = renderFilterContent();

    await waitFor(() => {
      expect(getByText("Skills")).toBeTruthy();
      expect(getByText("React")).toBeTruthy();
    });
  });

  it("applies selected tags and updates offers", async () => {
    const setOffersData = jest.fn();
    const onClose = jest.fn();

    const { getByText } = renderFilterContent({ setOffersData, onClose });

    await waitFor(() => {
      expect(getByText("React")).toBeTruthy();
    });

    fireEvent.press(getByText("React"));
    fireEvent.press(getByText("filter.apply"));

    await waitFor(() => {
      expect(mockSetFiltersList).toHaveBeenCalledWith(["tag-1"]);
      expect(setOffersData).toHaveBeenCalledWith([]);
      expect(onClose).toHaveBeenCalled();
    });
  });

  it("clears filters and refreshes offers", async () => {
    const setOffersData = jest.fn();
    const onClose = jest.fn();

    const { getByText } = renderFilterContent({ setOffersData, onClose });

    await waitFor(() => {
      expect(getByText("filter.clear")).toBeTruthy();
    });

    fireEvent.press(getByText("filter.clear"));

    await waitFor(() => {
      expect(mockClearFilters).toHaveBeenCalledTimes(1);
      expect(setOffersData).toHaveBeenCalledWith([]);
      expect(onClose).toHaveBeenCalled();
    });
  });

  it("signs out when loading tags fails", async () => {
    mockGetAllTags.mockRejectedValue(new Error("network"));

    renderFilterContent();

    await waitFor(() => {
      expect(mockSignOut).toHaveBeenCalledTimes(1);
      expect(consoleErrorSpy).toHaveBeenCalledWith(
        "error while getting tags:",
        expect.any(Error),
      );
    });
  });

  it("removes tag when toggled twice before apply", async () => {
    const { getByText } = renderFilterContent();

    await waitFor(() => {
      expect(getByText("React")).toBeTruthy();
    });

    fireEvent.press(getByText("React"));
    fireEvent.press(getByText("React"));
    fireEvent.press(getByText("filter.apply"));

    await waitFor(() => {
      expect(mockSetFiltersList).toHaveBeenCalledWith([]);
    });
  });

  it("closes filter even when clear fails", async () => {
    mockClearFilters.mockRejectedValue(new Error("clear failed"));
    const onClose = jest.fn();

    const { getByText } = renderFilterContent({ onClose });

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
    const { getByText } = renderFilterContent({
      filters: "invalid-filters-state" as any,
    });

    await waitFor(() => {
      expect(getByText("React")).toBeTruthy();
    });

    fireEvent.press(getByText("filter.apply"));

    await waitFor(() => {
      expect(mockSetFiltersList).toHaveBeenCalledWith([]);
    });
  });

  it("handles apply by resetting offers list", async () => {
    const setOffersData = jest.fn();

    const { getByText } = renderFilterContent({ setOffersData });

    await waitFor(() => {
      expect(getByText("filter.apply")).toBeTruthy();
    });

    fireEvent.press(getByText("filter.apply"));

    await waitFor(() => {
      expect(setOffersData).toHaveBeenCalledWith([]);
    });
  });

  it("handles clear by resetting offers list", async () => {
    const setOffersData = jest.fn();

    const { getByText } = renderFilterContent({ setOffersData });

    await waitFor(() => {
      expect(getByText("filter.clear")).toBeTruthy();
    });

    fireEvent.press(getByText("filter.clear"));

    await waitFor(() => {
      expect(setOffersData).toHaveBeenCalledWith([]);
    });
  });
});
