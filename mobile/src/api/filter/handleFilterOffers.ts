import { tryCatch } from "../../utils/try-catch";
import { apiFetch } from "../client";

export async function handleFilterOffers() {
  const [response, error] = await tryCatch(
    apiFetch("/offer", {
      method: "GET",
    }),
  );
  if (error) console.error("filter offers error:", error);
  if (!response) throw new Error("No response received");
  if (!response) console.error("filter offers response not ok:", response);
  return response;
}
