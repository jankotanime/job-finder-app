import { Tag } from "./Tag";
export type OfferStatus = "OPEN" | "CLOSED";
export type OfferPhoto = {
  name: string;
  mimeType: string;
  data: string;
};
export type Offer = {
  id?: string;
  title: string;
  description: string;
  salary: number;
  maxParticipants: number;
  tags: Tag[];
  offerPhoto?: string;
  photo?: {
    storageKey: string;
  };
  dateAndTime: string;
};
