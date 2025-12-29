import { tryCatch } from "../../utils/try-catch";
import { apiFetch } from "../client";

interface FilterParams {
  tags: string[];
}

export async function handleFilterOffers({ tags }: FilterParams) {
  const params = new URLSearchParams();
  if (tags.length > 0) {
    params.set("tags", tags.join(","));
  }
  console.log("queryString:", params.toString());
  const [response, error] = await tryCatch(
    apiFetch(`/offer?${params.toString()}`, {
      method: "GET",
    }),
  );
  if (error) console.error("filter offers error:", error);
  if (!response) throw new Error("No response received");
  return response;
}
