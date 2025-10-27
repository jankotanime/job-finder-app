import { tryCatch } from "./try-catch";
import { login } from "./login";

export interface FormState {
  loginData: string;
  password: string;
}
export async function handleLoginSubmit({
  formState,
  setError,
  setIsLoading,
}: {
  formState: FormState;
  setError: (err: string) => void;
  setIsLoading: (loading: boolean) => void;
}) {
  setError("");
  setIsLoading(true);
  const [response, error] = await tryCatch(login(formState));
  if (error) setError(error.message);
  if (response.error) setError(response.error);
  setIsLoading(false);
}
