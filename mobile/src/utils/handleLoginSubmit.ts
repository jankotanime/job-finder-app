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
    signIn({ loginData: formState.loginData, password: formState.password }),
  );
  if (result?.ok) {
    navigation?.reset({ index: 0, routes: [{ name: "Main" }] });
  } else {
    setError(result?.error || "Login failed");
  }
  if (error) setError(error?.message);
  setIsLoading(false);
}
