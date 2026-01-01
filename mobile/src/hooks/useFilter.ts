import AsyncStorage from "@react-native-async-storage/async-storage";
import { useState, useEffect, useCallback } from "react";
import { tryCatch } from "../utils/try-catch";

const STORAGE_KEY = "filters";

const isStringArray = (x: unknown): x is string[] =>
  Array.isArray(x) && x.every((v) => typeof v === "string");

const toUniqueStrings = (arr: string[]): string[] =>
  Array.from(new Set(arr.filter((s) => typeof s === "string")));

const useFilter = () => {
  const [filters, setFilters] = useState<string[]>([]);

  useEffect(() => {
    loadFilters();
  }, []);

  const loadFilters = async () => {
    const [json, error] = await tryCatch(AsyncStorage.getItem(STORAGE_KEY));
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
          await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify([]));
        }
      } catch (e) {
        console.error("failed to parse filters json:", e);
        setFilters([]);
      }
    }
  };

  const addFilter = useCallback(
    async (filter: string) => {
      if (typeof filter !== "string") return;
      const next = toUniqueStrings([...filters, filter]);
      setFilters(next);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(next)),
      );
      if (error) console.error("failed to add filter:", error);
    },
    [filters],
  );
  const removeFilter = useCallback(
    async (filter: string) => {
      const next = filters.filter((current) => current !== filter);
      setFilters(next);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(next)),
      );
      if (error) console.error("failed to remove filter:", error);
    },
    [filters],
  );

  const setFiltersList = useCallback(async (list: string[]) => {
    const next = toUniqueStrings(Array.isArray(list) ? list : []);
    setFilters(next);
    const [_, error] = await tryCatch(
      AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(next)),
    );
    if (error) console.error("failed to set filters list:", error);
  }, []);

  const clearFilters = useCallback(async () => {
    setFilters([]);
    const [_, error] = await tryCatch(
      AsyncStorage.setItem(STORAGE_KEY, JSON.stringify([])),
    );
    if (error) console.error("failed to clear filters:", error);
  }, []);

  return {
    filters,
    addFilter,
    removeFilter,
    setFiltersList,
    clearFilters,
  };
};

export default useFilter;
