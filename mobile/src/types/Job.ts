import { Tag } from "./Tag";
type JobStatus = "accepted" | "declined" | "pending";
type ApprovalPhoto = {
  name: string;
  mimeType: string;
  data: string;
};
type JobDispatcher = {
  isAnyIssueOwner: boolean;
  isAnyIssueContractor: boolean;
  ownerApprovalPhoto: ApprovalPhoto;
  contractorApprovalPhoto: ApprovalPhoto;
  finishedAt: string;
  createdAt: string;
  updatedAt: string;
};
type JobPhoto = {
  name: string;
  mimeType: string;
  data: string;
};
export type Job = {
  title: string;
  description: string;
  dateAndTime: string;
  salary: number;
  location: string;
  status: JobStatus;
  owner: string;
  contractor: string;
  tags: Tag[];
  jobPhoto?: JobPhoto;
  deposit: number;
  jobDispatcher: JobDispatcher;
  createdAt: string;
  updatedAt: string;
};
