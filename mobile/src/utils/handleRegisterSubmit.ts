import { tryCatch } from "./try-catch";
import { register } from "./register";

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
  t,
}: {
  formState: FormState;
  setError: (err: string) => void;
  setIsLoading: (loading: boolean) => void;
  t: (key: string) => string;
}) {
  setError("");
  setIsLoading(true);
  if (formState.password !== formState.repeatPassword) {
    setError(t("passwords_dont_match"));
    setIsLoading(false);
    return;
  }
  const [response, error] = await tryCatch(register(formState));
  if (error) setError(error.message);
  if (response.error) setError(response.error);
  setIsLoading(false);
}
