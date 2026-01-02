import AsyncStorage from "@react-native-async-storage/async-storage";
import { useState, useEffect, useCallback } from "react";
import { tryCatch } from "../utils/try-catch";
import { Offer } from "../types/Offer";

export const useOfferStorage = () => {
  const [acceptedOffers, setAcceptedOffers] = useState<Offer[]>([]);
  const [declinedOffers, setDeclinedOffers] = useState<Offer[]>([]);
  const [storageOffers, setStorageOffers] = useState<Offer[]>([]);
  const [savedOffers, setSavedOffers] = useState<Offer[]>([]);

  const makeOfferKey = (offer: Offer) => `${offer.title}|${offer.dateAndTime}`;

  useEffect(() => {
    const loadOffers = async () => {
      const [acceptedJson, error] = await tryCatch(
        AsyncStorage.getItem("acceptedOffers"),
      );
      if (acceptedJson) setAcceptedOffers(JSON.parse(acceptedJson));
      if (error) console.error("failed to load accepted offers: ", error);

      const [declinedJson, err] = await tryCatch(
        AsyncStorage.getItem("declinedOffers"),
      );
      if (declinedJson) setDeclinedOffers(JSON.parse(declinedJson));
      if (err) console.error("failed to load declined offers: ", err);

      const [storageJson, errS] = await tryCatch(
        AsyncStorage.getItem("storageOffers"),
      );
      if (storageJson) setStorageOffers(JSON.parse(storageJson));
      if (errS) console.error("failed to load storage offers: ", errS);

      const [savedJson, errSaved] = await tryCatch(
        AsyncStorage.getItem("savedOffers"),
      );
      if (savedJson) setSavedOffers(JSON.parse(savedJson));
      if (errSaved) console.error("failed to load saved offers: ", errSaved);
    };
    loadOffers();
  }, []);
  const addAcceptedOffer = useCallback(
    async (offer: Offer) => {
      const newOffers = [...acceptedOffers, offer];
      setAcceptedOffers(newOffers);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem("acceptedOffers", JSON.stringify(newOffers)),
      );
      if (error) console.error("failed to add accepted offer: ", error);
    },
    [acceptedOffers],
  );

  const removeAcceptedOffer = useCallback(
    async (offer: Offer) => {
      const newOffers = acceptedOffers.filter(
        (acceptedOffer) => acceptedOffer !== offer,
      );
      setAcceptedOffers(newOffers);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem("acceptedOffers", JSON.stringify(newOffers)),
      );
      if (error) console.error("failed to remove accepted offer: ", error);
    },
    [acceptedOffers],
  );

  const addDeclinedOffer = useCallback(
    async (offer: Offer) => {
      const newOffers = [...declinedOffers, offer];
      setDeclinedOffers(newOffers);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem("declinedOffers", JSON.stringify(newOffers)),
      );
      if (error) console.error("failed to add declined offer: ", error);
    },
    [declinedOffers],
  );

  const removeDeclinedOffer = useCallback(
    async (offer: Offer) => {
      const newOffers = declinedOffers.filter(
        (declinedOffer) => declinedOffer !== offer,
      );
      setDeclinedOffers(newOffers);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem("declinedOffers", JSON.stringify(newOffers)),
      );
      if (error) console.error("failed to remove declined offer: ", error);
    },
    [declinedOffers],
  );

  const addStorageOffer = useCallback(
    async (offer: Offer) => {
      const newOffers = [...storageOffers, offer];
      setStorageOffers(newOffers);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem("storageOffers", JSON.stringify(newOffers)),
      );
      if (error) console.error("failed to add storage offer: ", error);
    },
    [storageOffers],
  );

  const removeStorageOffer = useCallback(
    async (offer: Offer) => {
      const newOffers = storageOffers.filter(
        (storageOffer) => storageOffer !== offer,
      );
      setStorageOffers(newOffers);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem("storageOffers", JSON.stringify(newOffers)),
      );
      if (error) console.error("failed to remove storage offer: ", error);
    },
    [storageOffers],
  );

  const addSavedOffer = useCallback(
    async (offer: Offer) => {
      const key = makeOfferKey(offer);
      const deduped = savedOffers.filter((o) => makeOfferKey(o) !== key);
      const newOffers = [...deduped, offer];
      setSavedOffers(newOffers);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem("savedOffers", JSON.stringify(newOffers)),
      );
      if (error) console.error("failed to add saved offer: ", error);
    },
    [savedOffers],
  );

  const removeSavedOffer = useCallback(
    async (offer: Offer) => {
      const key = makeOfferKey(offer);
      const newOffers = savedOffers.filter((o) => makeOfferKey(o) !== key);
      setSavedOffers(newOffers);
      const [_, error] = await tryCatch(
        AsyncStorage.setItem("savedOffers", JSON.stringify(newOffers)),
      );
      if (error) console.error("failed to remove saved offer: ", error);
    },
    [savedOffers],
  );

  const resetOfferStorage = useCallback(async () => {
    setAcceptedOffers([]);
    setDeclinedOffers([]);
    setStorageOffers([]);
    setSavedOffers([]);
    const [_, error] = await tryCatch(
      AsyncStorage.multiRemove([
        "acceptedOffers",
        "declinedOffers",
        "storageOffers",
        "savedOffers",
      ]),
    );
    if (error) console.error("failed to reset offer storage: ", error);
  }, []);

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
