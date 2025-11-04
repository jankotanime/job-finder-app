export const getUsernameFromAccessToken = (
  access?: string | null,
): string | null => {
  if (!access || typeof access !== "string") return null;
  try {
    const parts = access.split(".");
    if (parts.length !== 3) return null;
    const payload = parts[1].replace(/-/g, "+").replace(/_/g, "/");
    const { Buffer } = require("buffer");
    const decoded = Buffer.from(payload, "base64").toString("utf8");
    const parsedPayload = JSON.parse(decoded);
    const username = parsedPayload?.username || parsedPayload?.sub || null;
    return username || null;
  } catch (e) {
    return null;
  }
};

export default getUsernameFromAccessToken;
