export const extractTokens = (
  response: any,
): { accessToken?: string; refreshToken?: string } => {
  const tokens = response?.data;
  if (!tokens || typeof tokens !== "object") return {};
  return {
    accessToken: tokens.accessToken,
    refreshToken: tokens.refreshToken,
  };
};
