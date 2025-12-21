import EncryptedStorage from "react-native-encrypted-storage";
import { tryCatch } from "../../utils/try-catch";

export const getTokens = async (setError?: (err: string) => void) => {
  const [tokens, error] = await tryCatch(EncryptedStorage.getItem("auth"));
  if (error) {
    setError?.(error.message);
    return;
  }
  return tokens;
};
