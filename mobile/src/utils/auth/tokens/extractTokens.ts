export const extractTokens = (
  response: any,
): { accessToken?: string; refreshToken?: string; refreshTokenId?: string } => {
  const tokens = response?.data || response?.tokens;
  if (!tokens || typeof tokens !== "object") return {};
  return {
    accessToken: tokens.accessToken,
    refreshToken: tokens.refreshToken,
    refreshTokenId: tokens.refreshTokenId,
  };
};
