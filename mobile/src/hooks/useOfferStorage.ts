import AsyncStorage from "@react-native-async-storage/async-storage";
import { useState, useEffect, useCallback } from "react";
import { tryCatch } from "../utils/try-catch";
import { Offer } from "../types/Offer";
import { useAuth } from "../contexts/AuthContext";

export const useOfferStorage = () => {
  const { userInfo } = useAuth();
  const userId = userInfo?.userId ? String(userInfo.userId) : null;

  const [acceptedOffers, setAcceptedOffers] = useState<Offer[]>([]);
  const [declinedOffers, setDeclinedOffers] = useState<Offer[]>([]);
  const [storageOffers, setStorageOffers] = useState<Offer[]>([]);
  const [savedOffers, setSavedOffers] = useState<Offer[]>([]);

  const makeOfferKey = (offer: Offer) => `${offer.title}|${offer.dateAndTime}`;

  const keyFor = useCallback(
    (base: string) => (userId ? `${base}:${userId}` : null),
    [userId],
  );

  useEffect(() => {
    const loadOffers = async () => {
      setAcceptedOffers([]);
      setDeclinedOffers([]);
      setStorageOffers([]);
      setSavedOffers([]);

      const acceptedKey = keyFor("acceptedOffers");
      const declinedKey = keyFor("declinedOffers");
      const storageKey = keyFor("storageOffers");
      const savedKey = keyFor("savedOffers");

      const [acceptedJson, error] = await tryCatch(
        acceptedKey ? AsyncStorage.getItem(acceptedKey) : Promise.resolve(null),
      );
      if (acceptedJson) setAcceptedOffers(JSON.parse(acceptedJson));
      if (error) console.error("failed to load accepted offers: ", error);

      const [declinedJson, err] = await tryCatch(
        declinedKey ? AsyncStorage.getItem(declinedKey) : Promise.resolve(null),
      );
      if (declinedJson) setDeclinedOffers(JSON.parse(declinedJson));
      if (err) console.error("failed to load declined offers: ", err);

      const [storageJson, errS] = await tryCatch(
        storageKey ? AsyncStorage.getItem(storageKey) : Promise.resolve(null),
      );
      if (storageJson) setStorageOffers(JSON.parse(storageJson));
      if (errS) console.error("failed to load storage offers: ", errS);

      const [savedJson, errSaved] = await tryCatch(
        savedKey ? AsyncStorage.getItem(savedKey) : Promise.resolve(null),
      );
      if (savedJson) setSavedOffers(JSON.parse(savedJson));
      if (errSaved) console.error("failed to load saved offers: ", errSaved);
    };
    loadOffers();
  }, [keyFor]);
  const addAcceptedOffer = useCallback(
    async (offer: Offer) => {
      const newOffers = [...acceptedOffers, offer];
      setAcceptedOffers(newOffers);
      const k = keyFor("acceptedOffers");
      const [_, error] = await tryCatch(
        k
          ? AsyncStorage.setItem(k, JSON.stringify(newOffers))
          : Promise.resolve(),
      );
      if (error) console.error("failed to add accepted offer: ", error);
    },
    [acceptedOffers, keyFor],
  );

  const removeAcceptedOffer = useCallback(
    async (offer: Offer) => {
      const newOffers = acceptedOffers.filter(
        (acceptedOffer) => acceptedOffer !== offer,
      );
      setAcceptedOffers(newOffers);
      const k = keyFor("acceptedOffers");
      const [_, error] = await tryCatch(
        k
          ? AsyncStorage.setItem(k, JSON.stringify(newOffers))
          : Promise.resolve(),
      );
      if (error) console.error("failed to remove accepted offer: ", error);
    },
    [acceptedOffers, keyFor],
  );

  const addDeclinedOffer = useCallback(
    async (offer: Offer) => {
      const newOffers = [...declinedOffers, offer];
      setDeclinedOffers(newOffers);
      const k = keyFor("declinedOffers");
      const [_, error] = await tryCatch(
        k
          ? AsyncStorage.setItem(k, JSON.stringify(newOffers))
          : Promise.resolve(),
      );
      if (error) console.error("failed to add declined offer: ", error);
    },
    [declinedOffers, keyFor],
  );

  const removeDeclinedOffer = useCallback(
    async (offer: Offer) => {
      const newOffers = declinedOffers.filter(
        (declinedOffer) => declinedOffer !== offer,
      );
      setDeclinedOffers(newOffers);
      const k = keyFor("declinedOffers");
      const [_, error] = await tryCatch(
        k
          ? AsyncStorage.setItem(k, JSON.stringify(newOffers))
          : Promise.resolve(),
      );
      if (error) console.error("failed to remove declined offer: ", error);
    },
    [declinedOffers, keyFor],
  );

  const addStorageOffer = useCallback(
    async (offer: Offer) => {
      const newOffers = [...storageOffers, offer];
      setStorageOffers(newOffers);
      const k = keyFor("storageOffers");
      const [_, error] = await tryCatch(
        k
          ? AsyncStorage.setItem(k, JSON.stringify(newOffers))
          : Promise.resolve(),
      );
      if (error) console.error("failed to add storage offer: ", error);
    },
    [storageOffers, keyFor],
  );

  const removeStorageOffer = useCallback(
    async (offer: Offer) => {
      const newOffers = storageOffers.filter(
        (storageOffer) => storageOffer !== offer,
      );
      setStorageOffers(newOffers);
      const k = keyFor("storageOffers");
      const [_, error] = await tryCatch(
        k
          ? AsyncStorage.setItem(k, JSON.stringify(newOffers))
          : Promise.resolve(),
      );
      if (error) console.error("failed to remove storage offer: ", error);
    },
    [storageOffers, keyFor],
  );

  const addSavedOffer = useCallback(
    async (offer: Offer) => {
      const key = makeOfferKey(offer);
      const deduped = savedOffers.filter((o) => makeOfferKey(o) !== key);
      const newOffers = [...deduped, offer];
      setSavedOffers(newOffers);
      const k = keyFor("savedOffers");
      const [_, error] = await tryCatch(
        k
          ? AsyncStorage.setItem(k, JSON.stringify(newOffers))
          : Promise.resolve(),
      );
      if (error) console.error("failed to add saved offer: ", error);
    },
    [savedOffers, keyFor],
  );

  const removeSavedOffer = useCallback(
    async (offer: Offer) => {
      const key = makeOfferKey(offer);
      const newOffers = savedOffers.filter((o) => makeOfferKey(o) !== key);
      setSavedOffers(newOffers);
      const k = keyFor("savedOffers");
      const [_, error] = await tryCatch(
        k
          ? AsyncStorage.setItem(k, JSON.stringify(newOffers))
          : Promise.resolve(),
      );
      if (error) console.error("failed to remove saved offer: ", error);
    },
    [savedOffers, keyFor],
  );

  const resetOfferStorage = useCallback(async () => {
    setAcceptedOffers([]);
    setDeclinedOffers([]);
    setStorageOffers([]);
    setSavedOffers([]);
    const keys = [
      keyFor("acceptedOffers"),
      keyFor("declinedOffers"),
      keyFor("storageOffers"),
      keyFor("savedOffers"),
    ].filter(Boolean) as string[];
    const [_, error] = await tryCatch(
      keys.length ? AsyncStorage.multiRemove(keys) : Promise.resolve(),
    );
    if (error) console.error("failed to reset offer storage: ", error);
  }, [keyFor]);

  return {
    acceptedOffers,
    declinedOffers,
    storageOffers,
    savedOffers,
    addAcceptedOffer,
    removeAcceptedOffer,
    addDeclinedOffer,
    removeDeclinedOffer,
    addStorageOffer,
    removeStorageOffer,
    addSavedOffer,
    removeSavedOffer,
    resetOfferStorage,
  };
};

export default useOfferStorage;
