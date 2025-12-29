import { tryCatch } from "../../utils/try-catch";
import { apiFetch } from "../client";
import { CreateOffer, UpdateOffer } from "../interfaces/OffersInterfaces";
import { Offer } from "../../types/Offer";

export const getAllOffers = async () => {
  const [response, error] = await tryCatch(
    apiFetch(
      "/offer",
      {
        method: "GET",
      },
      true,
    ),
  );
  if (error) console.error("get error:", error);
  if (!response) throw new Error("No response received");
  return response;
};
export const getOfferById = async (id: string) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/offer/${id}`,
      {
        method: "GET",
      },
      true,
    ),
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
  const form = new FormData();
  const title = (formState as any).title;
  const description = (formState as any).description;
  const salary = (formState as any).salary;
  const maxParticipants = (formState as any).maxParticipants;
  const dateAndTime = (formState as any).dateAndTime;
  const ownerId = (formState as any).ownerId;
  const offerPhoto = (formState as any).offerPhoto;
  const photosArray = (formState as any).photos;

  if (title) form.append("title", String(title));
  if (description) form.append("description", String(description));
  if (typeof salary !== "undefined") form.append("salary", String(salary));
  if (typeof maxParticipants !== "undefined")
    form.append("maxParticipants", String(maxParticipants));
  if (dateAndTime) form.append("dateAndTime", String(dateAndTime));
  if (ownerId) form.append("ownerId", String(ownerId));
  tagIds.forEach((id: string) => form.append("tags", id));

  const appendPhoto = (uri: string) => {
    if (!uri) return;
    const lastSlash = uri.lastIndexOf("/");
    const filename =
      lastSlash >= 0 ? uri.substring(lastSlash + 1) : `photo.jpg`;
    const ext = filename.split(".").pop()?.toLowerCase();
    const mime =
      ext === "png"
        ? "image/png"
        : ext === "jpg" || ext === "jpeg"
          ? "image/jpeg"
          : "application/octet-stream";
    form.append("photos", {
      uri,
      name: filename,
      type: mime,
    } as any);
  };
  if (offerPhoto) appendPhoto(offerPhoto);
  else if (Array.isArray(photosArray) && photosArray.length > 0) {
    photosArray.forEach((p: string) => appendPhoto(p));
  }

  const [response, error] = await tryCatch(
    apiFetch(
      `/offer`,
      {
        method: "POST",
        body: form,
      },
      true,
    ),
  );
  if (error) console.error("create error:", error);
  if (!response) throw new Error("No response received");
  return response;
};
export const updateOffer = async (formState: UpdateOffer, id: string) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/offer/${id}`,
      {
        method: "PUT",
        body: JSON.stringify({
          title: formState.title,
          description: formState.description,
          salary: formState.salary,
          maxParticipants: formState.maxParticipants,
          tags: formState.tags,
        }),
      },
      true,
    ),
  );
  if (error) console.error("put error:", error);
  if (!response) throw new Error("No response received");
  return response;
};
export const deleteOffer = async (id: string) => {
  const [response, error] = await tryCatch(
    apiFetch(
      `/offer/${id}`,
      {
        method: "DELETE",
      },
      true,
    ),
  );
  if (error) console.error("delete error:", error);
  if (!response) throw new Error("No response received");
  return response;
};
