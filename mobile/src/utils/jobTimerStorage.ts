import AsyncStorage from "@react-native-async-storage/async-storage";

export type ActiveJobTimer = {
  jobId: string;
  role: "owner" | "contractor";
  startedAt: number;
  username: string;
};

const LEGACY_ACTIVE_JOB_KEY = "activeJobTimer";
const activeKey = (username: string) => `activeJobTimer:${username}`;
const startKey = (jobId: string, username: string) =>
  `jobStart:${jobId}:${username}`;

const safeUsername = (username?: string | null) => {
  const v = (username ?? "").trim();
  return v.length ? v : "anon";
};

export const setJobStartAt = async (
  jobId: string,
  startedAt: number,
  username?: string | null,
) => {
  const u = safeUsername(username);
  await AsyncStorage.setItem(startKey(jobId, u), String(startedAt));
};

export const getJobStartAt = async (
  jobId: string,
  username?: string | null,
): Promise<number | null> => {
  const u = safeUsername(username);
  const saved = await AsyncStorage.getItem(startKey(jobId, u));
  if (!saved) return null;
  const parsed = Number(saved);
  return Number.isFinite(parsed) ? parsed : null;
};

export const setActiveJobTimer = async (
  active: Omit<ActiveJobTimer, "username">,
  username?: string | null,
) => {
  const u = safeUsername(username);
  const full: ActiveJobTimer = { ...active, username: u };
  await AsyncStorage.setItem(activeKey(u), JSON.stringify(full));
  await setJobStartAt(full.jobId, full.startedAt, u);
};

export const getActiveJobTimer = async (
  username?: string | null,
): Promise<ActiveJobTimer | null> => {
  const u = safeUsername(username);

  // legacy cleanup (older builds)
  const legacy = await AsyncStorage.getItem(LEGACY_ACTIVE_JOB_KEY);
  if (legacy) {
    await AsyncStorage.removeItem(LEGACY_ACTIVE_JOB_KEY);
  }

  const raw = await AsyncStorage.getItem(activeKey(u));
  if (!raw) return null;
  try {
    const parsed = JSON.parse(raw);
    if (!parsed?.jobId || !parsed?.role || !parsed?.startedAt) return null;
    if (parsed?.username && String(parsed.username) !== String(u)) return null;
    return { ...parsed, username: u } as ActiveJobTimer;
  } catch {
    return null;
  }
};

export const clearActiveJobTimer = async (
  jobId?: string,
  username?: string | null,
) => {
  const u = safeUsername(username);

  if (!jobId) {
    await AsyncStorage.removeItem(activeKey(u));
    await AsyncStorage.removeItem(LEGACY_ACTIVE_JOB_KEY);
    return;
  }

  const active = await getActiveJobTimer(u);
  if (!active || active.jobId === jobId) {
    await AsyncStorage.removeItem(activeKey(u));
  }

  await AsyncStorage.removeItem(startKey(jobId, u));
};
