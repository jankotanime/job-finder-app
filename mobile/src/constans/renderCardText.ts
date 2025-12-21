import { Offer } from "../types/Offer";
export const renderCardText = (item: Offer) => {
  return [
    { label: "ID", value: item.id },
    { label: "Description", value: item.description },
    { label: "Date", value: item.dateAndTime },
    { label: "Location", value: item.location },
    { label: "Salary", value: `$${item.salary}` },
    { label: "Status", value: item.status },
    { label: "Owner", value: item.owner },
    { label: "Candidates", value: item.candidates.join(", ") },
    {
      label: "Tags",
      value: item.tags.map((tag) => `${tag.name} (${tag.id})`).join(", "),
    },
    { label: "Created at", value: item.createdAt },
    { label: "Updated at", value: item.updatedAt },
  ];
};
