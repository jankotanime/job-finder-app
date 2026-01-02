export type User = {
  userId: string | null;
  username: string | null;
  firstName: string | null;
  lastName: string | null;
  phoneNumber: string | null;
  email: string | null;
  profilePhoto: string | null;
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
    const username: string | null = parsedPayload?.username ?? null;
    const userId: string | null =
      parsedPayload?.userId ?? parsedPayload?.sub ?? null;
    const firstName: string | null = parsedPayload?.firstName ?? null;
    const email: string | null = parsedPayload?.email ?? null;
    const profilePhoto: string | null = parsedPayload?.profilePhoto ?? null;
    const lastName: string | null = parsedPayload?.lastName ?? null;
    let phoneNumberVal: string | null = parsedPayload?.phoneNumber ?? null;
    if (phoneNumberVal != null && typeof phoneNumberVal !== "string") {
      phoneNumberVal = String(phoneNumberVal);
    }
    const user: User = {
      userId,
      username,
      firstName,
      lastName,
      phoneNumber: phoneNumberVal,
      profilePhoto,
      email,
    };
    return user || null;
  } catch (e) {
    return null;
  }
};

export default getUserInfo;
