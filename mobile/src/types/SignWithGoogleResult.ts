export type SignWithGoogleResult =
  | { status: "LOGGED_IN" }
  | { status: "REGISTER_REQUIRED" }
  | { status: "REGISTERED" }
  | { status: "ERROR"; error: string };
