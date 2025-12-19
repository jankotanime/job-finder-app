import { tryCatch } from "../../utils/try-catch";
interface FormState {
  firstName: string;
  lastName: string;
  location: string;
  description: string;
  profilePhoto: string;
  cv: string;
}
interface ProfileCompletionProps {
  formState: FormState;
  setError: React.Dispatch<React.SetStateAction<string>>;
  t: (text: string) => string;
}
export async function handleProfileCompletionSubmit({
  formState,
  setError,
  t,
}: ProfileCompletionProps) {
  const [response, backendError] = await tryCatch(
    fetch(`${process.env.EXPO_PUBLIC_API_UR}/profile-completion-form`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        firstName: formState.firstName,
        lastName: formState.lastName,
        profileDescription: formState.description,
      }),
    }),
  );
  if (backendError || !response) {
    return { ok: false, error: backendError || response };
  }
  const [data, jsonError] = await tryCatch(response.json());
  if (jsonError || !response.ok) {
    return { ok: false, error: data };
  }
  console.log("profileCompletionForm: ", data);
  return { ok: true, data: data };
}
