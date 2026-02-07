import AsyncStorage from "@react-native-async-storage/async-storage";

const contractorFinishedKey = (jobId: string) =>
  `jobContractorFinished:${jobId}`;

export const setContractorFinishedLocally = async (
  jobId: string,
  finishedAt: number = Date.now(),
) => {
  await AsyncStorage.setItem(contractorFinishedKey(jobId), String(finishedAt));
};

export const getContractorFinishedLocally = async (
  jobId: string,
): Promise<number | null> => {
  const raw = await AsyncStorage.getItem(contractorFinishedKey(jobId));
  if (!raw) return null;
  const parsed = Number(raw);
  return Number.isFinite(parsed) ? parsed : null;
};

export const clearContractorFinishedLocally = async (jobId: string) => {
  await AsyncStorage.removeItem(contractorFinishedKey(jobId));
};

export const filterOutLocallyFinishedContractorJobs = async <
  T extends { id: string },
>(
  jobs: T[],
): Promise<T[]> => {
  if (!jobs.length) return jobs;
  const keys = jobs.map((j) => contractorFinishedKey(j.id));
  const results = await AsyncStorage.multiGet(keys);
  const finished = new Set<string>();
  for (let i = 0; i < results.length; i++) {
    const [key, value] = results[i];
    if (value) {
      const jobId = jobs[i]?.id;
      if (jobId) finished.add(jobId);
    }
  }
  return jobs.filter((j) => !finished.has(j.id));
};
