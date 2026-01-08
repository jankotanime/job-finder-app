import { useCallback, useEffect, useMemo, useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useAuth } from "../contexts/AuthContext";
import { tryCatch } from "../utils/try-catch";

export type UseSelectCvParams = {
  limit?: number;
};

export const useSelectCv = (params?: UseSelectCvParams) => {
  const { userInfo } = useAuth();
  const userId = userInfo?.userId ? String(userInfo.userId) : null;
  const limit =
    typeof params?.limit === "number" && params.limit > 0 ? params.limit : 1;
  const storageKey = useMemo(
    () => (userId ? `selectedCvs:${userId}` : null),
    [userId],
  );
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      if (!storageKey) return;
      const [raw] = await tryCatch(AsyncStorage.getItem(storageKey));
      const parsed: unknown = raw ? JSON.parse(raw) : null;
      if (!cancelled && Array.isArray(parsed)) {
        const unique = Array.from(
          new Set(parsed.filter((x) => typeof x === "string")),
        ) as string[];
        setSelectedIds(unique.slice(0, limit));
      } else if (!cancelled) {
        setSelectedIds([]);
      }
      if (!cancelled) setLoaded(true);
    };
    setSelectedIds([]);
    setLoaded(false);
    load();
    return () => {
      cancelled = true;
    };
  }, [storageKey, limit]);

  useEffect(() => {
    const save = async () => {
      if (!storageKey || !loaded) return;
      await tryCatch(
        AsyncStorage.setItem(
          storageKey,
          JSON.stringify(selectedIds.slice(0, limit)),
        ),
      );
    };
    save();
  }, [selectedIds, storageKey, loaded, limit]);

  const selectCv = useCallback(
    (id: string) => {
      setSelectedIds((prev) => {
        if (prev.includes(id)) return prev;
        if (limit === 1) return [id];
        if (prev.length >= limit) return prev;
        return [...prev, id];
      });
    },
    [limit],
  );

  const unselectCv = useCallback((id: string) => {
    setSelectedIds((prev) => prev.filter((x) => x !== id));
  }, []);

  const setSelection = useCallback(
    (ids: string[]) => {
      const unique = Array.from(
        new Set(ids.filter((x) => typeof x === "string")),
      ) as string[];
      setSelectedIds(unique.slice(0, limit));
    },
    [limit],
  );

  const resetSelection = useCallback(() => {
    setSelectedIds([]);
  }, []);

  const reload = useCallback(async () => {
    if (!storageKey) return;
    const [raw] = await tryCatch(AsyncStorage.getItem(storageKey));
    const parsed: unknown = raw ? JSON.parse(raw) : null;
    if (Array.isArray(parsed)) {
      const unique = Array.from(
        new Set(parsed.filter((x) => typeof x === "string")),
      ) as string[];
      setSelectedIds(unique.slice(0, limit));
    }
  }, [storageKey, limit]);

  const clearStorage = useCallback(async () => {
    if (!storageKey) return;
    await tryCatch(AsyncStorage.removeItem(storageKey));
    setSelectedIds([]);
  }, [storageKey]);

  const isReady = Boolean(storageKey);

  return {
    selectedIds,
    selectCv,
    unselectCv,
    setSelection,
    resetSelection,
    reload,
    clearStorage,
    isReady,
  };
};

export default useSelectCv;
