import EncryptedStorage from "react-native-encrypted-storage";
import getUsernameFromAccessToken from "../../auth/tokens/getUsernameFromAccessToken";

export const getUsernameAsync = async (): Promise<string | null> => {
  try {
    const authRaw = await EncryptedStorage.getItem("auth");
    if (!authRaw) return null;
    const authData = JSON.parse(authRaw);
    console.log("authData: ", authData);
    if (!authData.accessToken) return null;

    const username = getUsernameFromAccessToken(authData.accessToken);
    return username || null;
  } catch (e) {
    console.error("Error getting username from storage:", e);
    return null;
  }
};
