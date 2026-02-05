import AsyncStorage from "@react-native-async-storage/async-storage";

export type ActiveJobTimer = {
  jobId: string;
  role: "owner" | "contractor";
  startedAt: number;
};

const ACTIVE_JOB_KEY = "activeJobTimer";
const startKey = (jobId: string) => `jobStart:${jobId}`;

export const setJobStartAt = async (jobId: string, startedAt: number) => {
  await AsyncStorage.setItem(startKey(jobId), String(startedAt));
};

export const getJobStartAt = async (jobId: string): Promise<number | null> => {
  const saved = await AsyncStorage.getItem(startKey(jobId));
  if (!saved) return null;
  const parsed = Number(saved);
  return Number.isFinite(parsed) ? parsed : null;
};

export const setActiveJobTimer = async (active: ActiveJobTimer) => {
  await AsyncStorage.setItem(ACTIVE_JOB_KEY, JSON.stringify(active));
  await setJobStartAt(active.jobId, active.startedAt);
};

export const getActiveJobTimer = async (): Promise<ActiveJobTimer | null> => {
  const raw = await AsyncStorage.getItem(ACTIVE_JOB_KEY);
  if (!raw) return null;
  try {
    const parsed = JSON.parse(raw);
    if (!parsed?.jobId || !parsed?.role || !parsed?.startedAt) return null;
    return parsed as ActiveJobTimer;
  } catch {
    return null;
  }
};

export const clearActiveJobTimer = async (jobId?: string) => {
  await AsyncStorage.removeItem(ACTIVE_JOB_KEY);
  if (jobId) {
    await AsyncStorage.removeItem(startKey(jobId));
  }
};
