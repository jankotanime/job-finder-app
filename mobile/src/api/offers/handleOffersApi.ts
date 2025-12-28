import { tryCatch } from "../../utils/try-catch";
import { apiFetch } from "../client";
import { CreateOffer, UpdateOffer } from "../interfaces/OffersInterfaces";
import { Offer } from "../../types/Offer";

export const getAllOffers = async () => {
  const [response, error] = await tryCatch(
    apiFetch("/offer", {
      method: "GET",
    }),
  );
  if (error) console.error("get error:", error);
  if (!response) throw new Error("No response received");
  return response;
};
export const getOfferById = async (id: string) => {
  const [response, error] = await tryCatch(
    apiFetch(`/offer/${id}`, {
      method: "GET",
    }),
  );
  if (error) console.error("get id error:", error);
  if (!response) throw new Error("No response received");
  return response;
};
export const createOffer = async (
  formState: Offer | (CreateOffer & { dateAndTime?: string }),
) => {
  const tagIds = Array.isArray((formState as any).tags)
    ? (formState as any).tags.map((t: any) =>
        typeof t === "string" ? t : t.id,
      )
    : [];
  const payload: any = {
    title: (formState as any).title,
    description: (formState as any).description,
    salary: (formState as any).salary,
    maxParticipants: (formState as any).maxParticipants,
    tags: tagIds,
  };
  if ((formState as any).dateAndTime)
    payload.dateAndTime = (formState as any).dateAndTime;
  if ((formState as any).ownerId) payload.ownerId = (formState as any).ownerId;
  const [response, error] = await tryCatch(
    apiFetch(`/offer`, {
      method: "POST",
      body: JSON.stringify(payload),
    }),
  );
  if (error) console.error("create error:", error);
  if (!response) throw new Error("No response received");
  return response;
};
export const updateOffer = async (formState: UpdateOffer, id: string) => {
  const [response, error] = await tryCatch(
    apiFetch(`/offer${id}`, {
      method: "PUT",
      body: JSON.stringify({
        title: formState.title,
        description: formState.description,
        salary: formState.salary,
        maxParticipants: formState.maxParticipants,
        tags: formState.tags,
      }),
    }),
  );
  if (error) console.error("put error:", error);
  if (!response) throw new Error("No response received");
  return response;
};
export const deleteOffer = async (id: string) => {
  const [response, error] = await tryCatch(
    apiFetch(`/offer${id}`, {
      method: "DELETE",
    }),
  );
  if (error) console.error("delete error:", error);
  if (!response) throw new Error("No response received");
  return response;
};
