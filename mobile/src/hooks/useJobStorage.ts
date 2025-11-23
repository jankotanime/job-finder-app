import AsyncStorage from "@react-native-async-storage/async-storage";
import { useState, useEffect, useCallback } from "react";
import { tryCatch } from "../utils/try-catch";
import { Job } from "../types/Job";

export const useJobStorage = () => {
  const [acceptedJobs, setAcceptedJobs] = useState<Job[]>([]);
  const [declinedJobs, setDeclinedJobs] = useState<Job[]>([]);
  const [storageJobs, setStorageJobs] = useState<Job[]>([]);

  useEffect(() => {
    const loadJobs = async () => {
      const [acceptedJson, error] = await tryCatch(
        AsyncStorage.getItem("acceptedJobs"),
      );
      if (acceptedJson) setAcceptedJobs(JSON.parse(acceptedJson));
      if (error) console.error("failed to load accepted jobs: ", error);

      const [declinedJson, err] = await tryCatch(
        AsyncStorage.getItem("declinedJobs"),
      );
      if (declinedJson) setDeclinedJobs(JSON.parse(declinedJson));
      if (err) console.error("failed to load declined jobs: ", err);

      const [storageJson, errS] = await tryCatch(
        AsyncStorage.getItem("storageJobs"),
      );
      if (storageJson) setStorageJobs(JSON.parse(storageJson));
      if (errS) console.error("failed to load storage jobs: ", errS);
    };
    loadJobs();
  }, []);
  const addAcceptedJob = useCallback(
    async (job: Job) => {
      const newJobs = [...acceptedJobs, job];
      setAcceptedJobs(newJobs);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem("acceptedJobs", JSON.stringify(newJobs)),
      );
      if (error) console.error("failed to add accepted job: ", error);
    },
    [acceptedJobs],
  );

  const removeAcceptedJob = useCallback(
    async (job: Job) => {
      const newJobs = acceptedJobs.filter((acceptedJob) => acceptedJob !== job);
      setAcceptedJobs(newJobs);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem("acceptedJobs", JSON.stringify(newJobs)),
      );
      if (error) console.error("failed to remove accepted job: ", error);
    },
    [acceptedJobs],
  );

  const addDeclinedJob = useCallback(
    async (job: Job) => {
      const newJobs = [...declinedJobs, job];
      setDeclinedJobs(newJobs);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem("declinedJobs", JSON.stringify(newJobs)),
      );
      if (error) console.error("failed to add declined job: ", error);
    },
    [declinedJobs],
  );

  const removeDeclinedJob = useCallback(
    async (job: Job) => {
      const newJobs = declinedJobs.filter((declinedJob) => declinedJob !== job);
      setDeclinedJobs(newJobs);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem("declinedJobs", JSON.stringify(newJobs)),
      );
      if (error) console.error("failed to remove declined job: ", error);
    },
    [declinedJobs],
  );

  const addStorageJob = useCallback(
    async (job: Job) => {
      const newJobs = [...storageJobs, job];
      setStorageJobs(newJobs);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem("storageJobs", JSON.stringify(newJobs)),
      );
      if (error) console.error("failed to add storage job: ", error);
    },
    [storageJobs],
  );

  const removeStorageJob = useCallback(
    async (job: Job) => {
      const newJobs = storageJobs.filter((storageJob) => storageJob !== job);
      setStorageJobs(newJobs);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem("storageJobs", JSON.stringify(newJobs)),
      );
      if (error) console.error("failed to remove storage job: ", error);
    },
    [storageJobs],
  );

  return {
    acceptedJobs,
    declinedJobs,
    storageJobs,
    addAcceptedJob,
    removeAcceptedJob,
    addDeclinedJob,
    removeDeclinedJob,
    addStorageJob,
    removeStorageJob,
  };
};

export default useJobStorage;
