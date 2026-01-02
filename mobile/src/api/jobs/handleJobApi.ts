import { tryCatch } from "../../utils/try-catch";
import { apiFetch } from "../client";

export const getJobById = async (jobId: string) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/job/${jobId}`,
      {
        method: "GET",
      },
      true,
    ),
  );
  if (error) console.error("get job by id error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const getJobsAsContractor = async () => {
  const [response, error] = await tryCatch(
    apiFetch(
      "/job/contractor",
      {
        method: "GET",
      },
      true,
    ),
  );
  if (error) console.error("get jobs as contractor error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const getJobsAsOwner = async () => {
  const [response, error] = await tryCatch(
    apiFetch(
      "/job/owner",
      {
        method: "GET",
      },
      true,
    ),
  );
  if (error) console.error("get jobs as owner error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const createJob = async (offerId: string) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/job/${offerId}`,
      {
        method: "POST",
      },
      true,
    ),
  );
  if (error) console.error("create job error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const deleteJob = async (jobId: string) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/job/${jobId}`,
      {
        method: "DELETE",
      },
      true,
    ),
  );
  if (error) console.error("delete job error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export default {
  getJobById,
  getJobsAsContractor,
  getJobsAsOwner,
  createJob,
  deleteJob,
};
