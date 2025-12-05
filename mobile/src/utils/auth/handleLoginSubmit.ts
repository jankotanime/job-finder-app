import { tryCatch } from "../try-catch";
import { getErrorMessage } from "../../constans/errorMessages";

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
  if (error)
    setError(getErrorMessage(error?.message, t) || t("errors.login_failed"));
  else if (!result?.ok)
    setError(
      getErrorMessage(result?.error ?? "", t) || t("errors.login_failed"),
    );
  else navigation?.reset({ index: 0, routes: [{ name: "Main" }] });
  setIsLoading(false);
}
