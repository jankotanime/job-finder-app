import { tryCatch } from "../../utils/try-catch";
import { apiFetch } from "../client";

export const getAllTags = async () => {
  const [response, error] = await tryCatch(
    apiFetch("/tag", {
      method: "GET",
    }),
  );
  if (error) console.error("get error:", error);
  if (!response) throw new Error("No response received");
  return response;
};
