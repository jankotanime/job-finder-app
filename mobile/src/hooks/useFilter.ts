import AsyncStorage from "@react-native-async-storage/async-storage";
import { useState, useEffect, useCallback } from "react";
import { tryCatch } from "../utils/try-catch";
import { useAuth } from "../contexts/AuthContext";

const LEGACY_STORAGE_KEY = "filters";

const isStringArray = (x: unknown): x is string[] =>
  Array.isArray(x) && x.every((v) => typeof v === "string");

const toUniqueStrings = (arr: string[]): string[] =>
  Array.from(new Set(arr.filter((s) => typeof s === "string")));

const useFilter = () => {
  const [filters, setFilters] = useState<string[]>([]);
  const { userInfo } = useAuth();

  const getStorageKey = () =>
    userInfo?.userId ? `filters:${userInfo.userId}` : "filters:guest";

  useEffect(() => {
    loadFilters();
  }, [userInfo?.userId]);

  const loadFilters = async () => {
    const storageKey = getStorageKey();
    const [json, error] = await tryCatch(AsyncStorage.getItem(storageKey));
    if (error) {
      console.error("failed to load filters from storage:", error);
      return;
    }
    if (json) {
      try {
        const parsed = JSON.parse(json);
        if (isStringArray(parsed)) {
          setFilters(toUniqueStrings(parsed));
        } else {
          setFilters([]);
          await AsyncStorage.setItem(storageKey, JSON.stringify([]));
        }
      } catch (e) {
        console.error("failed to parse filters json:", e);
        setFilters([]);
      }
    } else {
      const [legacyJson] = await tryCatch(
        AsyncStorage.getItem(LEGACY_STORAGE_KEY),
      );
      if (legacyJson) {
        try {
          const parsed = JSON.parse(legacyJson);
          if (isStringArray(parsed)) {
            const unique = toUniqueStrings(parsed);
            setFilters(unique);
            await AsyncStorage.setItem(storageKey, JSON.stringify(unique));
          }
        } catch (e) {}
      }
    }
  };

  const addFilter = useCallback(
    async (filter: string) => {
      if (typeof filter !== "string") return;
      const next = toUniqueStrings([...filters, filter]);
      setFilters(next);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem(getStorageKey(), JSON.stringify(next)),
      );
      if (error) console.error("failed to add filter:", error);
    },
    [filters, userInfo?.userId],
  );
  const removeFilter = useCallback(
    async (filter: string) => {
      const next = filters.filter((current) => current !== filter);
      setFilters(next);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem(getStorageKey(), JSON.stringify(next)),
      );
      if (error) console.error("failed to remove filter:", error);
    },
    [filters, userInfo?.userId],
  );

  const setFiltersList = useCallback(
    async (list: string[]) => {
      const next = toUniqueStrings(Array.isArray(list) ? list : []);
      setFilters(next);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem(getStorageKey(), JSON.stringify(next)),
      );
      if (error) console.error("failed to set filters list:", error);
    },
    [userInfo?.userId],
  );

  const clearFilters = useCallback(async () => {
    setFilters([]);
    const [_, error] = await tryCatch(
      AsyncStorage.setItem(getStorageKey(), JSON.stringify([])),
    );
    if (error) console.error("failed to clear filters:", error);
  }, [userInfo?.userId]);

  return {
    filters,
    addFilter,
    removeFilter,
    setFiltersList,
    clearFilters,
  };
};

export default useFilter;
