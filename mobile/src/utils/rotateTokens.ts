import { tryCatch } from "./try-catch";
import { getErrorMessage } from "../constans/errorMessages";

export interface RotateTokensProps {
  refreshToken: string;
  refreshTokenId: string;
}
export const rotateTokens = async ({
  tokens,
  setError,
  t,
}: {
  tokens: RotateTokensProps;
  setError: (err: string) => void;
  t: (text: string) => string;
}) => {
  const [response, error] = await tryCatch(
    fetch(`${process.env.EXPO_PUBLIC_API_URL}/refresh-token/rotate`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        refreshToken: tokens.refreshToken,
        refreshTokenId: tokens.refreshTokenId,
      }),
    }),
  );
  console.log(response);
  if (error) {
    setError(getErrorMessage(error.message, t) || t("token_expired"));
    return null;
  }
  if (!response) {
    setError(t("token_expired"));
    return null;
  }
  const data = await response.json();
  console.log("rotated tokens: ", data);
  return data;
};
