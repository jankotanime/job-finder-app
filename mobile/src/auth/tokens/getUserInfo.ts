export type User = {
  userId: string | null;
  username: string | null;
  firstName: string | null;
  lastName: string | null;
  phoneNumber: string | null;
};

export const getUserInfo = (access?: string | null): User | null => {
  if (!access || typeof access !== "string") return null;
  try {
    const parts = access.split(".");
    if (parts.length !== 3) return null;
    const payload = parts[1].replace(/-/g, "+").replace(/_/g, "/");
    const { Buffer } = require("buffer");
    const decoded = Buffer.from(payload, "base64").toString("utf8");
    const parsedPayload = JSON.parse(decoded);
    const username: string | null =
      parsedPayload?.username ?? parsedPayload?.sub ?? null;
    const userId: string | null =
      parsedPayload?.userId ?? parsedPayload?.sub ?? null;
    const firstName: string | null =
      parsedPayload?.firstName ?? parsedPayload?.sub ?? null;
    const lastName: string | null =
      parsedPayload?.lastName ?? parsedPayload?.sub ?? null;
    let phoneNumberVal: string | null =
      parsedPayload?.phoneNumber ?? parsedPayload?.sub ?? null;
    if (phoneNumberVal != null && typeof phoneNumberVal !== "string") {
      phoneNumberVal = String(phoneNumberVal);
    }
    const user: User = {
      userId,
      username,
      firstName,
      lastName,
      phoneNumber: phoneNumberVal,
    };
    return user || null;
  } catch (e) {
    return null;
  }
};

export default getUserInfo;
