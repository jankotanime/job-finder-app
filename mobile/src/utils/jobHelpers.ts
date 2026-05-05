import type { Job } from "../types/Job";

export type JobDispatcher = {
  finishedAt?: string | null;
  startedAt?: string | number | null;
  startAt?: string | number | null;
  started?: boolean | null;
};

export const getJobFromPayload = (payload: any): Job | null => {
  const data = payload?.body?.data;
  if (data && typeof data === "object") return data as Job;
  if (payload?.body && typeof payload.body === "object") {
    return payload.body as Job;
  }
  return null;
};

export const getJobsArrayFromPayload = (payload: any): any[] => {
  const data = payload?.body?.data;
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.content)) return data.content;
  if (Array.isArray(payload?.body)) return payload.body;
  if (Array.isArray(payload)) return payload;
  return [];
};

export const getIdFromListItem = (item: any): string | null => {
  const raw = item?.id;
  if (raw == null) return null;
  return String(raw);
};

export const getJobFromListItem = (item: any): Job | null => {
  const first = item?.job ?? item?.offer?.job ?? item?.offer ?? item;
  if (!first || typeof first !== "object") return null;

  const direct = first as any;
  if (direct?.id && typeof direct?.title === "string") return direct as Job;

  const nested = direct?.job;
  if (nested?.id && typeof nested?.title === "string") return nested as Job;

  return null;
};

export const getDispatcherFromPayload = (
  payload: any,
): JobDispatcher | null => {
  const data = payload?.body?.data;
  if (data && typeof data === "object") return data as JobDispatcher;
  if (payload?.body && typeof payload.body === "object") {
    return payload.body as JobDispatcher;
  }
  return null;
};

export const dispatcherIndicatesStarted = (
  d: JobDispatcher | null,
): boolean => {
  if (!d) return false;
  const startedAt = d.startedAt ?? d.startAt ?? null;
  const startedFlag = d.started;
  if (startedFlag === true) return true;
  return Boolean(startedAt);
};

export const formatDuration = (valueMs: number | null) => {
  if (valueMs == null || !Number.isFinite(valueMs) || valueMs < 0) {
    return "--:--:--";
  }

  const totalSeconds = Math.floor(valueMs / 1000);
  const hours = Math.floor(totalSeconds / 3600)
    .toString()
    .padStart(2, "0");
  const minutes = Math.floor((totalSeconds % 3600) / 60)
    .toString()
    .padStart(2, "0");
  const seconds = (totalSeconds % 60).toString().padStart(2, "0");
  return `${hours}:${minutes}:${seconds}`;
};
