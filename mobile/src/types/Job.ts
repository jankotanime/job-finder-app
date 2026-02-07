import { Tag } from "./Tag";

export type JobStatus =
  | "READY"
  | "IN_PROGRESS"
  | "FINISHED_FAILURE"
  | "FINISHED_SUCCESS";

export type UserInJob = {
  id: string;
  username: string;
  firstName: string;
  lastName: string;
  phoneNumber: number;
  profilePhoto?: { storageKey: string } | null;
};

export type Job = {
  id: string;
  title: string;
  description: string;
  dateAndTime: string;
  salary: number;
  status: JobStatus;
  owner: UserInJob;
  contractor: UserInJob;
  tags: Tag[];
  photo?: { storageKey: string } | null;
};
