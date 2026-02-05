import { tryCatch } from "../../utils/try-catch";
import { apiFetch } from "../client";

interface FilterParams {
  tags: string[];
}
interface PageableParams {
  page: number;
  size: number;
  sort?: string;
}

export async function handleFilterOffers(
  filters: FilterParams,
  pageable: PageableParams,
) {
  const params = new URLSearchParams();
  if (filters.tags && filters.tags.length > 0) {
    filters.tags.forEach((tag) => {
      params.append("tags", tag);
    });
  }
  params.append("page", pageable.page.toString());
  params.append("size", pageable.size.toString());
  if (pageable.sort) {
    params.append("sort", pageable.sort);
  }
  const [response, error] = await tryCatch(
    apiFetch(`/offer?${params.toString()}`, {
      method: "GET",
    }),
  );
  if (error) console.error("filter offers error:", error);
  if (!response) throw new Error("No response received");
  return response;
}
