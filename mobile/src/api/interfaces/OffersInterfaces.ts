export interface CreateOffer {
  title: string;
  description: string;
  salary: number;
  maxParticipants: number;
  ownerId: string;
  tags: string[];
}
export interface UpdateOffer {
  title: string;
  description: string;
  salary: number;
  maxParticipants: number;
  tags: string[];
}
