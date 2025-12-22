import { Offer } from "../types/Offer";
export const renderCardText = (item: Offer) => {
  return [
    { label: "Description", value: item.description },
    { label: "Salary", value: `$${item.salary}` },
    {
      label: "Tags",
      value: item.tags.map((tag) => tag).join(", "),
    },
  ];
};
