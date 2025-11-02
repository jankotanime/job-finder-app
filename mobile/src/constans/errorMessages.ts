export const errorMessages: Record<string, string> = {
  "Job not found": "errors.job_not_found",
  "Wrong login data": "errors.wrong_login_data",
  "Wrong password": "errors.wrong_password",
  "Invalid username": "errors.invalid_username",
  "Invalid email": "errors.invalid_email",
  "Invalid phone number": "errors.invalid_phone_number",
  "Invalid password": "errors.invalid_password",
  "User with this username exists": "errors.username_already_taken",
  "User with this email exists": "errors.email_already_taken",
  "User with this phone number exists": "errors.phone_number_already_taken",
  "Invalid refresh token": "errors.invalid_refresh_token",
};
export const getErrorMessage = (code: string, t: (text: string) => string) => {
  return t(errorMessages[code]) || "error";
};
