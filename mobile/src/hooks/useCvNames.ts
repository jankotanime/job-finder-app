import { useCallback, useEffect, useMemo, useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useAuth } from "../contexts/AuthContext";
import { tryCatch } from "../utils/try-catch";

type NamesMap = Record<string, string>;

export const useCvNames = () => {
  const { userInfo } = useAuth();
  const userId = userInfo?.userId ? String(userInfo.userId) : null;
  const storageKey = useMemo(
    () => (userId ? `cvNames:${userId}` : null),
    [userId],
  );
  const [namesMap, setNamesMap] = useState<NamesMap>({});
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      if (!storageKey) return;
      const [raw] = await tryCatch(AsyncStorage.getItem(storageKey));
      const parsed: unknown = raw ? JSON.parse(raw) : null;
      if (!cancelled && parsed && typeof parsed === "object") {
        setNamesMap(parsed as NamesMap);
      } else if (!cancelled) {
        setNamesMap({});
      }
      if (!cancelled) setLoaded(true);
    };
    setLoaded(false);
    setNamesMap({});
    load();
    return () => {
      cancelled = true;
    };
  }, [storageKey]);

  useEffect(() => {
    const save = async () => {
      if (!storageKey || !loaded) return;
      await tryCatch(
        AsyncStorage.setItem(storageKey, JSON.stringify(namesMap)),
      );
    };
    save();
  }, [namesMap, storageKey, loaded]);

  const setName = useCallback((cvId: string, name: string) => {
    const trimmed = String(name || "").trim();
    if (!cvId || !trimmed) return;
    setNamesMap((prev) => ({ ...prev, [cvId]: trimmed }));
  }, []);

  const getName = useCallback(
    (cvId: string) => {
      return namesMap[cvId];
    },
    [namesMap],
  );

  const reload = useCallback(async () => {
    if (!storageKey) return;
    const [raw] = await tryCatch(AsyncStorage.getItem(storageKey));
    const parsed: unknown = raw ? JSON.parse(raw) : null;
    if (parsed && typeof parsed === "object") setNamesMap(parsed as NamesMap);
  }, [storageKey]);

  const clear = useCallback(async () => {
    if (!storageKey) return;
    await tryCatch(AsyncStorage.removeItem(storageKey));
    setNamesMap({});
  }, [storageKey]);

  return { namesMap, setName, getName, reload, clear };
};

export default useCvNames;
