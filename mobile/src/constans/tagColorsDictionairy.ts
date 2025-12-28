export type TagCategoryColor = "YELLOW" | "RED" | "BLUE" | "GREEN" | "ORANGE";

export const TagColorMap: Record<TagCategoryColor | "DEFAULT", string> = {
  YELLOW: "#FBC02D",
  RED: "#E53935",
  BLUE: "#1E88E5",
  GREEN: "#43A047",
  ORANGE: "#FB8C00",
  DEFAULT: "#9E9E9E",
};

export function getTagColor(categoryColor?: string): string {
  if (!categoryColor) return TagColorMap.DEFAULT;
  const key = categoryColor.toUpperCase() as TagCategoryColor;
  return TagColorMap[key] ?? TagColorMap.DEFAULT;
}

export default TagColorMap;
