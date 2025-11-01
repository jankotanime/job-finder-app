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
  t,
  signIn,
}: {
  formState: FormState;
  setError: (err: string) => void;
  setIsLoading: (loading: boolean) => void;
  navigation?: any;
  t: (text: string) => string;
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
  if (error) setError(error?.message || t("errors.login_failed"));
  else if (!result?.ok) setError(result?.error || t("errors.login_failed"));
  else navigation?.reset({ index: 0, routes: [{ name: "Main" }] });
  setIsLoading(false);
}
