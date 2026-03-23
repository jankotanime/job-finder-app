/// <reference types="jest" />

import React from "react";
import { render, fireEvent } from "@testing-library/react-native";
import { Animated, TouchableOpacity } from "react-native";
import FilterCollapsibleSection from "../../../components/filter/FilterCollapsibleSection";

jest.mock("@expo/vector-icons", () => ({
  MaterialIcons: "MaterialIcons",
}));

describe("FilterCollapsibleSection component", () => {
  let animatedTimingSpy: jest.SpyInstance;

  beforeEach(() => {
    animatedTimingSpy = jest.spyOn(Animated, "timing").mockReturnValue({
      start: (cb?: (result: { finished: boolean }) => void) =>
        cb && cb({ finished: true }),
      stop: jest.fn(),
      reset: jest.fn(),
    } as any);
  });

  afterEach(() => {
    animatedTimingSpy.mockRestore();
  });

  it("expands section and toggles selected tag", () => {
    const toggleTag = jest.fn();

    const section = {
      title: "Skills",
      tags: [
        { id: "tag-1", name: "React" },
        { id: "tag-2", name: "TypeScript" },
      ],
    };

    const theme = {
      colors: {
        primary: "#337EFF",
      },
    };

    const { getByText } = render(
      <FilterCollapsibleSection
        section={section}
        selectedTags={[]}
        toggleTag={toggleTag}
        theme={theme}
      />,
    );

    fireEvent.press(getByText("Skills"));
    fireEvent.press(getByText("React"));

    expect(toggleTag).toHaveBeenCalledWith("tag-1");
  });

  it("animates border radius back when collapsing", () => {
    jest.useFakeTimers();

    const section = {
      title: "Skills",
      tags: [{ id: "tag-1", name: "React" }],
    };

    const theme = {
      colors: {
        primary: "#337EFF",
      },
    };

    const { getByText } = render(
      <FilterCollapsibleSection
        section={section}
        selectedTags={[]}
        toggleTag={jest.fn()}
        theme={theme}
      />,
    );

    fireEvent.press(getByText("Skills"));
    fireEvent.press(getByText("Skills"));
    jest.runAllTimers();

    const hasCollapseRadiusAnimation = animatedTimingSpy.mock.calls.some(
      (_call) => _call[1]?.toValue === 20,
    );

    expect(hasCollapseRadiusAnimation).toBe(true);

    jest.useRealTimers();
  });

  it("renders selected tag with selected styles", () => {
    const section = {
      title: "Skills",
      tags: [{ id: "tag-1", name: "React" }],
    };

    const theme = {
      colors: {
        primary: "#337EFF",
      },
    };

    const { UNSAFE_getAllByType } = render(
      <FilterCollapsibleSection
        section={section}
        selectedTags={["tag-1"]}
        toggleTag={jest.fn()}
        theme={theme}
      />,
    );

    const touchables = UNSAFE_getAllByType(TouchableOpacity);
    const selectedChip = touchables[1];
    const selectedChipStyle = Array.isArray(selectedChip.props.style)
      ? Object.assign({}, ...selectedChip.props.style)
      : selectedChip.props.style;

    expect(selectedChipStyle.backgroundColor).toBe("#337EFF");
  });
});
