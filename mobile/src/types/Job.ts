import { Tag } from "./Tag";
export type JobStatus = "accepted" | "declined" | "pending";
export type Job = {
  id: string;
  title: string;
  description: string;
  startAt: string;
  location: string;
  salary: number;
  status: JobStatus;
  owner: string;
  candidates: string[];
  tags: Tag[];
  createdAt: string;
  updatedAt: string;
  logoUrl?: string;
};
