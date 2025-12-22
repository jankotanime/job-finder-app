import { createContext, useContext } from "react";
import useOfferStorage from "../hooks/useOfferStorage";

type OfferStorageContextType = ReturnType<typeof useOfferStorage>;

const OfferStorageContext = createContext<OfferStorageContextType | null>(null);

export const OfferStorageProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const storage = useOfferStorage();
  return (
    <OfferStorageContext.Provider value={storage}>
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
