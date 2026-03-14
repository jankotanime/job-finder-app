/// <reference types="jest" />

import React from "react";
import { render, fireEvent } from "@testing-library/react-native";
import FilterCollapsibleSection from "../../../components/filter/FilterCollapsibleSection";

describe("FilterCollapsibleSection component", () => {
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
});
