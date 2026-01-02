import { tryCatch } from "../../utils/try-catch";
import { apiFetch } from "../client";

const appendPdfToForm = (form: FormData, uri?: string) => {
  if (!uri) return;
  const lastSlash = uri.lastIndexOf("/");
  const filename = lastSlash >= 0 ? uri.substring(lastSlash + 1) : "cv.pdf";
  form.append("file", { uri, name: filename, type: "application/pdf" } as any);
};

export const uploadCv = async (fileUri: string) => {
  const form = new FormData();
  appendPdfToForm(form, fileUri);
  const [response, error] = await tryCatch(
    apiFetch(
      "/cv",
      {
        method: "POST",
        body: form,
      },
      true,
    ),
  );
  if (error) console.error("upload cv error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const getCvById = async (cvId: string) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/cv/${cvId}`,
      {
        method: "GET",
      },
      true,
    ),
  );
  if (error) console.error("get cv by id error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const getCvsByUser = async () => {
  const [response, error] = await tryCatch(
    apiFetch(
      "/cv",
      {
        method: "GET",
      },
      true,
    ),
  );
  if (error) console.error("get cvs by user error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const updateCv = async (cvId: string, fileUri: string) => {
  const form = new FormData();
  appendPdfToForm(form, fileUri);
  const [response, error] = await tryCatch(
    apiFetch(
      `/cv/${cvId}`,
      {
        method: "PUT",
        body: form,
      },
      true,
    ),
  );
  if (error) console.error("update cv error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const deleteCv = async (cvId: string) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/cv/${cvId}`,
      {
        method: "DELETE",
      },
      true,
    ),
  );
  if (error) console.error("delete cv error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export const deleteCvsByUser = async () => {
  const [response, error] = await tryCatch(
    apiFetch(
      "/cv",
      {
        method: "DELETE",
      },
      true,
    ),
  );
  if (error) console.error("delete cvs by user error:", error);
  if (!response) throw new Error("No response received");
  return response;
};

export default {
  uploadCv,
  getCvById,
  getCvsByUser,
  updateCv,
  deleteCv,
  deleteCvsByUser,
};
