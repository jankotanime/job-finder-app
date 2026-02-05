import { createContext, useContext, useState } from "react";
import useOfferStorage from "../hooks/useOfferStorage";

type OfferStorageContextType = ReturnType<typeof useOfferStorage> & {
  offersVersion: number;
  refreshOffers: () => void;
};

const OfferStorageContext = createContext<OfferStorageContextType | null>(null);

export const OfferStorageProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const [offersVersion, setOffersVersion] = useState(0);
  const storage = useOfferStorage(offersVersion);

  const refreshOffers = () => {
    setOffersVersion((v) => v + 1);
  };
  return (
    <OfferStorageContext.Provider
      value={{
        ...storage,
        offersVersion,
        refreshOffers,
      }}
    >
      {children}
    </OfferStorageContext.Provider>
  );
};

export const useOfferStorageContext = () => {
  const ctx = useContext(OfferStorageContext);
  if (!ctx)
    throw new Error("useOfferStorageContext must be used inside provider");
  return ctx;
};
