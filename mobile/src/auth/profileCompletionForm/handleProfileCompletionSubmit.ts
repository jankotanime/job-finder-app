import { tryCatch } from "../../utils/try-catch";
import { getTokens } from "../tokens/getTokens";
import { AuthStatus } from "../../enums/authStatus";
import getUsernameFromAccessToken from "../tokens/getUsernameFromAccessToken";
interface FormState {
  firstName: string;
  lastName: string;
  // location: string;
  description: string;
  profilePhoto: string;
}
interface ProfileCompletionProps {
  formState: FormState;
  setError: React.Dispatch<React.SetStateAction<string>>;
  t: (text: string) => string;
  completeFinalRegistration: (
    accessToken: string,
    refreshToken: string,
    refreshTokenId: string,
    username: string | null,
  ) => Promise<{ ok: boolean; status: AuthStatus }>;
}
export async function handleProfileCompletionSubmit({
  formState,
  setError,
  t,
  completeFinalRegistration,
}: ProfileCompletionProps) {
  const tokensStr = await getTokens();
  if (!tokensStr) return;
  let tokensObj;
  try {
    tokensObj = JSON.parse(tokensStr);
  } catch (err) {
    setError?.("Invalid token format");
    return { ok: false, error: err };
  }
  const { accessToken, refreshToken, refreshTokenId } = tokensObj;
  const username = getUsernameFromAccessToken(accessToken);
  const [response, backendError] = await tryCatch(
    fetch(`${process.env.EXPO_PUBLIC_API_URL}/profile-completion-form`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify({
        firstName: formState.firstName,
        lastName: formState.lastName,
        profileDescription: formState.description,
        profilePhoto: formState.profilePhoto,
      }),
    }),
  );
  const data = await response?.json();
  console.log("data: ", data);
  if (backendError || !response) {
    return { ok: false, error: backendError || response };
  }
  const responseFinalRegistration = await completeFinalRegistration(
    data.data.accessToken,
    refreshToken,
    refreshTokenId,
    username,
  );
  if (!responseFinalRegistration.ok) {
    return { ok: false, data: data };
  }
  return { ok: true, data: data };
}
