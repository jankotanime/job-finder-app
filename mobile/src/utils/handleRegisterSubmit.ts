import { tryCatch } from "./try-catch";

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
  signUp,
}: {
  formState: FormState;
  setError: (err: string) => void;
  setIsLoading: (loading: boolean) => void;
  navigation?: any;
  signUp: (form: FormState) => Promise<{ ok: boolean; error?: string }>;
}): Promise<void> {
  setError("");
  setIsLoading(true);
  if (formState.password != formState.repeatPassword) {
    setError("Passwords are not the same");
    setIsLoading(false);
    return;
  }
  const [result, error] = await tryCatch(signUp(formState));
  if (error) setError(error?.message || "Register failed");
  else if (!result?.ok) setError(result?.error || "Register failed");
  else navigation?.reset({ index: 0, routes: [{ name: "Main" }] });
  setIsLoading(false);
}
