export const fieldsLogin = (t: (text: string) => string) =>
  [
    { key: "loginData", placeholder: t("emailOrLogin"), secure: false },
    { key: "password", placeholder: t("password"), secure: true },
  ] as const;
export const fieldsRegister = (t: (text: string) => string) =>
  [
    { key: "username", placeholder: t("register.username"), secure: false },
    { key: "email", placeholder: t("email"), secure: false },
    {
      key: "phoneNumber",
      placeholder: t("register.phone_number"),
      secure: false,
    },
    { key: "password", placeholder: t("password"), secure: true },
    { key: "repeatPassword", placeholder: t("repeat_password"), secure: true },
  ] as const;
export const fieldsProfileCompletion = (t: (text: string) => string) => [
  {
    key: "firstName",
    placeholder: t("profileCompletion.firstName"),
    secure: false,
  },
  {
    key: "lastName",
    placeholder: t("profileCompletion.lastName"),
    secure: false,
  },
  {
    key: "location",
    placeholder: t("profileCompletion.location"),
    secure: false,
  },
  {
    key: "description",
    placeholder: t("profileCompletion.description"),
    secure: false,
  },
  { key: "cv", placeholder: t("profileCompletion.cv") },
];
