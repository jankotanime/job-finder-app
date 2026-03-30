import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { Offer } from "../types/Offer";
import { handleFilterOffers } from "../api/filter/handleFilterOffers";
import { getAllOffers } from "../api/offers/handleOffersApi";
import { buildPhotoUrl } from "../utils/photoUrl";
import { tryCatch } from "../utils/try-catch";

type UseMainOffersDeckParams = {
  userId?: string | number;
  filters: string[];
  offersVersion: unknown;
  enabled: boolean;
  pageSize?: number;
};

export const useMainOffersDeck = ({
  userId,
  filters,
  offersVersion,
  enabled,
  pageSize = 20,
}: UseMainOffersDeckParams) => {
  const [offersData, rawSetOffersData] = useState<Offer[]>([]);
  const [currentCardIndex, setCurrentCardIndex] = useState(0);
  const [swipedCount, setSwipedCount] = useState(0);
  const [dismissedOfferIds, setDismissedOfferIds] = useState<string[]>([]);
  const [page, setPage] = useState(0);
  const [last, setLast] = useState(false);
  const [fetching, setFetching] = useState(false);
  const [isDeckHydrated, setIsDeckHydrated] = useState(false);
  const offersDataRef = useRef<Offer[]>([]);
  const swipedCountRef = useRef(0);
  const pageRef = useRef(0);
  const lastRef = useRef(false);
  const dismissedOfferIdsRef = useRef<string[]>([]);
  const isDeckHydratedRef = useRef(false);
  const offersDeckKeyRef = useRef<string | null>(null);
  const loadInFlightRef = useRef(false);

  const getOfferId = useCallback((offer: Offer | undefined | null) => {
    if (!offer) return undefined;
    const id = (offer as any)?.id ?? (offer as any)?.dateAndTime;
    if (typeof id === "string") {
      return id.length > 0 ? id : undefined;
    }
    if (typeof id === "number" && Number.isFinite(id)) {
      return String(id);
    }
    return undefined;
  }, []);

  const offersDeckKey = useMemo(() => {
    if (!userId) return null;
    const normalizedFilters = [...filters].sort().join("|");
    return `mainOffersDeck:${String(userId)}:${normalizedFilters}`;
  }, [userId, filters]);
  const dismissedOffersKey = useMemo(() => {
    if (!userId) return null;
    return `dismissedOffers:${String(userId)}`;
  }, [userId]);

  const filterDismissed = useCallback(
    (list: Offer[], dismissedIds: string[]) => {
      if (!dismissedIds.length) return list;
      const dismissedSet = new Set(dismissedIds);
      return list.filter((offer) => {
        const id = getOfferId(offer);
        if (!id) return true;
        return !dismissedSet.has(id);
      });
    },
    [getOfferId],
  );

  const dedupeOffers = useCallback(
    (list: Offer[]) => {
      const seen = new Set<string>();
      const out: Offer[] = [];
      for (const offer of list) {
        const id = getOfferId(offer);
        if (!id) {
          out.push(offer);
          continue;
        }
        if (seen.has(id)) continue;
        seen.add(id);
        out.push(offer);
      }
      return out;
    },
    [getOfferId],
  );

  const setOffersData = useCallback(
    (data: Offer[] | ((prev: Offer[]) => Offer[])) => {
      rawSetOffersData((prev) => {
        const next =
          typeof data === "function"
            ? (data as (prev: Offer[]) => Offer[])(prev)
            : data;
        const filtered = filterDismissed(next, dismissedOfferIdsRef.current);
        return dedupeOffers(filtered);
      });
    },
    [filterDismissed, dedupeOffers],
  );

  useEffect(() => {
    offersDataRef.current = offersData;
  }, [offersData]);

  useEffect(() => {
    swipedCountRef.current = swipedCount;
  }, [swipedCount]);

  useEffect(() => {
    pageRef.current = page;
  }, [page]);

  useEffect(() => {
    lastRef.current = last;
  }, [last]);

  useEffect(() => {
    dismissedOfferIdsRef.current = dismissedOfferIds;
  }, [dismissedOfferIds]);

  useEffect(() => {
    isDeckHydratedRef.current = isDeckHydrated;
  }, [isDeckHydrated]);

  useEffect(() => {
    offersDeckKeyRef.current = offersDeckKey;
  }, [offersDeckKey]);

  useEffect(() => {
    // Isolate deck state across accounts/filters before async hydration runs.
    rawSetOffersData([]);
    setCurrentCardIndex(0);
    setSwipedCount(0);
    setPage(0);
    setLast(false);
    setFetching(false);
    setDismissedOfferIds([]);
    setIsDeckHydrated(false);

    offersDataRef.current = [];
    swipedCountRef.current = 0;
    pageRef.current = 0;
    lastRef.current = false;
    dismissedOfferIdsRef.current = [];
    isDeckHydratedRef.current = false;
  }, [offersDeckKey]);

  const persistSnapshot = useCallback(
    async (progressOverride?: number) => {
      const key = offersDeckKeyRef.current;
      if (!key || !isDeckHydratedRef.current) return;

      const offers = offersDataRef.current;
      const pageValue = pageRef.current;
      const lastValue = lastRef.current;
      const dismissed = dismissedOfferIdsRef.current;
      const progressBase =
        typeof progressOverride === "number"
          ? progressOverride
          : Math.max(swipedCountRef.current, currentCardIndex);
      const safeProgress = Math.max(0, Math.min(progressBase, offers.length));
      const remainingOffers = offers.slice(safeProgress);

      await tryCatch(
        AsyncStorage.setItem(
          key,
          JSON.stringify({
            offersData: remainingOffers,
            currentCardIndex: 0,
            swipedCount: 0,
            dismissedOfferIds: dismissed,
            page: pageValue,
            last: lastValue,
            updatedAt: Date.now(),
          }),
        ),
      );
    },
    [currentCardIndex],
  );

  const loadOffers = useCallback(
    async (reset = false) => {
      if (!userId || fetching || loadInFlightRef.current) return;
      if (!reset && last) return;

      loadInFlightRef.current = true;
      setFetching(true);
      try {
        const currentPage = reset ? 0 : page;
        let response;

        if (filters.length > 0) {
          response = await handleFilterOffers(
            { tags: Array.from(new Set(filters)) },
            { page: currentPage, size: pageSize },
          );
        } else {
          response = await getAllOffers({
            page: currentPage,
            size: pageSize,
          });
        }

        const pageData = response?.body?.data;
        const items = Array.isArray(pageData?.content) ? pageData.content : [];
        const normalized = items
          .map((it: any) => ({
            ...it,
            offerPhoto: buildPhotoUrl(it?.photo?.storageKey),
          }))
          .filter((it: any) => it?.owner?.id !== userId);

        const dismissedSet = new Set(dismissedOfferIdsRef.current);
        const filteredNormalized = normalized.filter((it: any) => {
          const id = getOfferId(it as Offer);
          if (!id) return true;
          return !dismissedSet.has(id);
        });

        rawSetOffersData((prev) => {
          const merged = reset
            ? filteredNormalized
            : [...prev, ...filteredNormalized];
          return dedupeOffers(merged);
        });
        setLast(pageData?.last ?? true);
        setPage(currentPage + 1);
      } catch (e) {
        console.error("failed to load offers", e);
      } finally {
        loadInFlightRef.current = false;
        setFetching(false);
      }
    },
    [filters, page, last, fetching, userId, pageSize, dedupeOffers, getOfferId],
  );

  useEffect(() => {
    let cancelled = false;

    const hydrateDeck = async () => {
      if (!offersDeckKey) return;

      setIsDeckHydrated(false);
      const [raw] = await tryCatch(AsyncStorage.getItem(offersDeckKey));
      if (cancelled) return;

      let parsed: any = null;
      if (raw) {
        try {
          parsed = JSON.parse(raw);
        } catch {
          parsed = null;
        }
      }

      const rawSavedOffers = Array.isArray(parsed?.offersData)
        ? (parsed.offersData as Offer[])
        : [];
      const savedPage = Number.isFinite(parsed?.page) ? Number(parsed.page) : 0;
      const savedLast = Boolean(parsed?.last);
      const savedProgress = Number.isFinite(parsed?.swipedCount)
        ? Number(parsed.swipedCount)
        : Number.isFinite(parsed?.currentCardIndex)
          ? Number(parsed.currentCardIndex)
          : 0;
      const [dismissedRaw] = dismissedOffersKey
        ? await tryCatch(AsyncStorage.getItem(dismissedOffersKey))
        : [null];
      let parsedDismissed: any = null;
      if (dismissedRaw) {
        try {
          parsedDismissed = JSON.parse(dismissedRaw);
        } catch {
          parsedDismissed = null;
        }
      }
      const savedDismissedFromDedicated = Array.isArray(parsedDismissed)
        ? parsedDismissed.filter(
            (id: unknown) => typeof id === "string" && id.length > 0,
          )
        : [];
      const savedDismissedFromDeck = Array.isArray(parsed?.dismissedOfferIds)
        ? parsed.dismissedOfferIds.filter(
            (id: unknown) => typeof id === "string" && id.length > 0,
          )
        : [];
      const savedDismissedIds = Array.from(
        new Set([...savedDismissedFromDedicated, ...savedDismissedFromDeck]),
      );
      const normalizedProgress = Math.max(
        0,
        Math.min(savedProgress, rawSavedOffers.length),
      );
      const savedOffers = rawSavedOffers.slice(normalizedProgress);

      rawSetOffersData(
        dedupeOffers(filterDismissed(savedOffers, savedDismissedIds)),
      );
      setPage(savedPage);
      setLast(savedLast);
      setCurrentCardIndex(0);
      setSwipedCount(0);
      setDismissedOfferIds(savedDismissedIds);
      setIsDeckHydrated(true);
    };

    hydrateDeck();

    return () => {
      cancelled = true;
    };
  }, [offersDeckKey, dismissedOffersKey, filterDismissed, dedupeOffers]);

  useEffect(() => {
    const persistDismissed = async () => {
      if (!dismissedOffersKey) return;
      await tryCatch(
        AsyncStorage.setItem(
          dismissedOffersKey,
          JSON.stringify(dismissedOfferIds),
        ),
      );
    };
    persistDismissed();
  }, [dismissedOffersKey, dismissedOfferIds]);

  useEffect(() => {
    if (!enabled || !isDeckHydrated || !userId) return;
    if (offersData.length > 0) return;

    setPage(0);
    setLast(false);
    loadOffers(true);
  }, [
    enabled,
    isDeckHydrated,
    userId,
    offersVersion,
    filters,
    offersData.length,
    loadOffers,
  ]);

  useEffect(() => {
    void persistSnapshot();
  }, [
    persistSnapshot,
    offersDeckKey,
    isDeckHydrated,
    offersData,
    swipedCount,
    currentCardIndex,
    page,
    last,
  ]);

  useEffect(() => {
    if (currentCardIndex <= offersData.length) return;
    setCurrentCardIndex(offersData.length);
  }, [currentCardIndex, offersData.length]);

  useEffect(() => {
    if (swipedCount <= offersData.length) return;
    setSwipedCount(offersData.length);
  }, [swipedCount, offersData.length]);

  const markSwiped = useCallback(
    (offer?: Offer) => {
      const offerId = getOfferId(offer);
      if (offerId) {
        setDismissedOfferIds((prev) => {
          if (prev.includes(offerId)) return prev;
          const next = [...prev, offerId];
          dismissedOfferIdsRef.current = next;
          return next;
        });
      }
      setSwipedCount((prev) => {
        const next = Math.min(prev + 1, offersDataRef.current.length);
        swipedCountRef.current = next;
        void persistSnapshot(next);
        return next;
      });
    },
    [getOfferId, persistSnapshot],
  );

  return {
    offersData,
    setOffersData,
    currentCardIndex,
    setCurrentCardIndex,
    swipedCount,
    setSwipedCount,
    markSwiped,
    page,
    setPage,
    last,
    setLast,
    fetching,
    loadOffers,
    isDeckHydrated,
  };
};

export default useMainOffersDeck;
