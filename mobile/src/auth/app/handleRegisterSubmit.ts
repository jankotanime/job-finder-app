import { tryCatch } from "../../utils/try-catch";
import { getErrorMessage } from "../../constans/errorMessages";

export interface FormState {
  username: string;
  email: string;
  phoneNumber: string;
  password: string;
  repeatPassword: string;
}

export async function handleRegisterSubmit({
  formState,
  setError,
  setIsLoading,
  navigation,
  t,
  signUp,
}: {
  formState: FormState;
  setError: (err: string) => void;
  setIsLoading: (loading: boolean) => void;
  navigation?: any;
  t: (text: string) => string;
  signUp: (form: FormState) => Promise<{ ok: boolean; error?: string }>;
}): Promise<void> {
  setError("");
  setIsLoading(true);
  if (formState.password != formState.repeatPassword) {
    setError(t("errors.passwords_dont_match"));
    setIsLoading(false);
    return;
  }
  const [result, error] = await tryCatch(signUp(formState));
  if (error)
    setError(getErrorMessage(error?.message, t) || t("errors.register_failed"));
  else if (!result?.ok)
    setError(
      getErrorMessage(result?.error ?? "", t) || t("errors.register_failed"),
    );
  else navigation.navigate("ProfileCompletion");
  setIsLoading(false);
}
