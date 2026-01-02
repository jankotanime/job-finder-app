import { useCallback, useEffect, useMemo, useState } from "react";
import type { ApplicationItem } from "../types/Applicants";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useAuth } from "../contexts/AuthContext";

type UseApplicantsParams = {
  offerId?: string;
  initial?: ApplicationItem[];
};

export const useApplicants = (params?: UseApplicantsParams) => {
  const { userInfo } = useAuth();
  const offerId = params?.offerId;
  const initial = params?.initial;
  const userId = userInfo?.userId ? String(userInfo.userId) : null;
  const storageKey = useMemo(
    () =>
      userId
        ? `chosenApplicants:${userId}${offerId ? ":" + offerId : ""}`
        : null,
    [userId, offerId],
  );

  const [chosenApplicants, setChosenApplicants] = useState<ApplicationItem[]>(
    initial ?? [],
  );
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      if (!storageKey) return;
      try {
        const raw = await AsyncStorage.getItem(storageKey);
        if (!raw) return;
        const parsed: ApplicationItem[] = JSON.parse(raw);
        if (!cancelled && Array.isArray(parsed)) {
          const base = initial ?? [];
          const map = new Map<string, ApplicationItem>();
          [...base, ...parsed].forEach((a) => {
            if (a && a.id) map.set(a.id, a);
          });
          setChosenApplicants(Array.from(map.values()));
        }
      } catch (e) {
        console.warn("useApplicants: failed to load from storage", e);
      } finally {
        if (!cancelled) setLoaded(true);
      }
    };
    load();
    return () => {
      cancelled = true;
    };
  }, [storageKey, initial]);

  const addApplicant = useCallback((applicant: ApplicationItem) => {
    if (!applicant) return;
    setChosenApplicants((prev) => {
      const exists = prev.some((a) => a.id === applicant.id);
      return exists ? prev : [...prev, applicant];
    });
  }, []);

  const addApplicantAndSave = useCallback(
    async (applicant: ApplicationItem) => {
      if (!applicant) return;
      setChosenApplicants((prev) => {
        const exists = prev.some((a) => a.id === applicant.id);
        return exists ? prev : [...prev, applicant];
      });
      if (!storageKey) return;
      try {
        const raw = await AsyncStorage.getItem(storageKey);
        const parsed: ApplicationItem[] = raw ? JSON.parse(raw) : [];
        const map = new Map<string, ApplicationItem>();
        parsed.forEach((a) => {
          if (a && a.id) map.set(a.id, a);
        });
        map.set(applicant.id, applicant);
        const next = Array.from(map.values());
        await AsyncStorage.setItem(storageKey, JSON.stringify(next));
      } catch (e) {
        console.warn("useApplicants: failed to add+save applicant", e);
      }
    },
    [storageKey],
  );

  const removeApplicant = useCallback((applicantId: string) => {
    setChosenApplicants((prev) => prev.filter((a) => a.id !== applicantId));
  }, []);

  const resetApplicants = useCallback(() => {
    setChosenApplicants([]);
  }, []);

  useEffect(() => {
    const save = async () => {
      if (!storageKey || !loaded) return;
      try {
        await AsyncStorage.setItem(
          storageKey,
          JSON.stringify(chosenApplicants),
        );
      } catch (e) {
        console.warn("useApplicants: failed to save to storage", e);
      }
    };
    save();
  }, [chosenApplicants, storageKey, loaded]);

  const clearStorage = useCallback(async () => {
    if (!storageKey) return;
    try {
      await AsyncStorage.removeItem(storageKey);
    } catch (e) {
      console.warn("useApplicants: failed to clear storage", e);
    }
  }, [storageKey]);

  const reload = useCallback(async () => {
    if (!storageKey) return;
    try {
      const raw = await AsyncStorage.getItem(storageKey);
      const parsed: ApplicationItem[] | null = raw ? JSON.parse(raw) : null;
      if (Array.isArray(parsed)) {
        const map = new Map<string, ApplicationItem>();
        parsed.forEach((a) => {
          if (a && a.id) map.set(a.id, a);
        });
        setChosenApplicants(Array.from(map.values()));
      }
    } catch (e) {
      console.warn("useApplicants: failed to reload from storage", e);
    }
  }, [storageKey]);

  const isReady = Boolean(storageKey);
  return {
    chosenApplicants,
    addApplicant,
    addApplicantAndSave,
    removeApplicant,
    resetApplicants,
    clearStorage,
    reload,
    isReady,
  };
};

export default useApplicants;
