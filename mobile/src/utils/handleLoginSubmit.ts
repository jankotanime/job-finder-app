import { tryCatch } from "./try-catch";

export interface FormState {
  loginData: string;
  password: string;
}
export async function handleLoginSubmit({
  formState,
  setError,
  setIsLoading,
  navigation,
  signIn,
}: {
  formState: FormState;
  setError: (err: string) => void;
  setIsLoading: (loading: boolean) => void;
  navigation?: any;
  signIn: (form: {
    loginData: string;
    password: string;
  }) => Promise<{ ok: boolean; error?: string }>;
}) {
  setError("");
  setIsLoading(true);
  const [result, error] = await tryCatch(
    signIn({
      loginData: formState.loginData,
      password: formState.password,
    }),
  );
  if (error) setError(error?.message || "Login failed");
  else if (!result?.ok) setError(result?.error || "Login failed");
  else navigation?.reset({ index: 0, routes: [{ name: "Main" }] });
  setIsLoading(false);
}
