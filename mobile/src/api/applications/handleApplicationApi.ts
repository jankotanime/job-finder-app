import { tryCatch } from "../../utils/try-catch";
import { apiFetch } from "../client";

interface PageableParams {
  page: number;
  size: number;
  sort?: string;
}

interface ApplicationPayload {
  cvId: string;
  [key: string]: any;
}

export const getAllApplicationsByOfferId = async (
  offerId: string,
  { page, size, sort }: PageableParams,
) => {
  const params = new URLSearchParams();
  params.append("page", page.toString());
  params.append("size", size.toString());
  if (sort) params.append("sort", sort);

  const [response, error] = await tryCatch(
    apiFetch(
      `/offer/${offerId}/application?${params.toString()}`,
      { method: "GET" },
      true,
    ),
  );
  if (error) console.error("getAllApplications error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const getApplicationById = async (
  offerId: string,
  applicationId: string,
) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/offer/${offerId}/application/${applicationId}`,
      { method: "GET" },
      true,
    ),
  );
  if (error) console.error("getApplicationById error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const sendApplication = async (
  offerId: string,
  payload: ApplicationPayload,
) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/offer/${offerId}/application`,
      {
        method: "POST",
        body: JSON.stringify(payload),
      },
      true,
    ),
  );
  if (error) console.error("sendApplication error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const acceptApplication = async (
  offerId: string,
  applicationId: string,
) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/offer/${offerId}/application/${applicationId}/accept`,
      { method: "PATCH" },
      true,
    ),
  );
  if (error) console.error("acceptApplication error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const rejectApplication = async (
  offerId: string,
  applicationId: string,
) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/offer/${offerId}/application/${applicationId}/reject`,
      { method: "PATCH" },
      true,
    ),
  );
  if (error) console.error("rejectApplication error:", error);
  if (!response) throw new Error("No response received");
  return response;
};
