import { Tag } from "./Tag";
export type OfferStatus = "accepted" | "declined" | "pending";
type OfferPhoto = {
  id: string;
  name: string;
  mimeType: string;
  data: string;
};
export type Offer = {
  id: string;
  title: string;
  description: string;
  dateAndTime: string;
  salary: number;
  location: string;
  status: OfferStatus;
  maxParticipants: number;
  owner: string;
  chosenCandidate: string;
  candidates: string[];
  tags: Tag[];
  offerPhoto?: OfferPhoto;
  createdAt: string;
  updatedAt: string;
};
