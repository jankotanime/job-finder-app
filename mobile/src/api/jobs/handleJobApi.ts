import { tryCatch } from "../../utils/try-catch";
import { apiFetch } from "../client";

type JobMultipartPayload = {
  description: string;
  photoUri?: string;
};

const buildPhotoPart = (uri: string) => {
  const lastSlash = uri.lastIndexOf("/");
  const filename = lastSlash >= 0 ? uri.substring(lastSlash + 1) : "photo.jpg";
  const ext = filename.split(".").pop()?.toLowerCase();
  const mime =
    ext === "png"
      ? "image/png"
      : ext === "jpg" || ext === "jpeg"
        ? "image/jpeg"
        : "application/octet-stream";
  return { uri, name: filename, type: mime } as any;
};

const buildJobFormData = (payload: JobMultipartPayload) => {
  const form = new FormData();
  form.append("description", payload.description);
  if (payload.photoUri) {
    form.append("photo", buildPhotoPart(payload.photoUri));
  }
  return form;
};

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

export const getJobDispatcher = async (jobId: string) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/job/${jobId}/dispatcher`,
      {
        method: "GET",
      },
      true,
    ),
  );
  if (error) console.error("get job dispatcher error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const startJob = async (jobId: string) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/job/${jobId}/start-job`,
      {
        method: "POST",
      },
      true,
    ),
  );
  if (error) console.error("start job error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const reportProblemTrue = async (
  jobId: string,
  payload: JobMultipartPayload,
) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/job/${jobId}/report-problem-true`,
      {
        method: "PATCH",
        body: buildJobFormData(payload),
      },
      true,
    ),
  );
  if (error) console.error("report problem true error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const reportProblemFalse = async (
  jobId: string,
  payload: JobMultipartPayload,
) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/job/${jobId}/report-problem-false`,
      {
        method: "PATCH",
        body: buildJobFormData(payload),
      },
      true,
    ),
  );
  if (error) console.error("report problem false error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const finishJob = async (
  jobId: string,
  payload: JobMultipartPayload,
) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/job/${jobId}/finish-job`,
      {
        method: "POST",
        body: buildJobFormData(payload),
      },
      true,
    ),
  );
  if (error) console.error("finish job error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export default {
  getJobById,
  getJobsAsContractor,
  getJobsAsOwner,
  createJob,
  deleteJob,
  getJobDispatcher,
  startJob,
  reportProblemTrue,
  reportProblemFalse,
  finishJob,
};
