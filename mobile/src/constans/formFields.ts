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
  //! Do zaimplementowania w MVP3
  // {
  //   key: "location",
  //   placeholder: t("profileCompletion.location"),
  //   secure: false,
  // },
  {
    key: "description",
    placeholder: t("profileCompletion.description"),
    secure: false,
  },
];
export const fieldsProfileCompletionGoogle = (t: (text: string) => string) => [
  { key: "username", placeholder: t("register.username"), secure: false },
  {
    key: "phoneNumber",
    placeholder: t("register.phone_number"),
    secure: false,
  },
];
export const fieldsSmsGoogleCode = (t: (text: string) => string) => [
  { key: "smsCode", placeholder: t("google.sms_code"), secure: false },
];

export const fieldsAddOffer = (t: (text: string) => string) => [
  { key: "offerPhoto", placeholder: t("offer.offerPhotoUrl"), secure: false },
  { key: "title", placeholder: t("offer.title"), secure: false },
  { key: "description", placeholder: t("offer.description"), secure: false },
  { key: "dateAndTime", placeholder: t("offer.dateAndTime"), secure: false },
  { key: "salary", placeholder: t("offer.salary"), secure: false },
  {
    key: "maxParticipants",
    placeholder: t("offer.maxParticipants"),
    secure: false,
  },
  { key: "location", placeholder: t("offer.location"), secure: false },
  { key: "owner", placeholder: t("offer.owner"), secure: false },
  {
    key: "chosenCandidate",
    placeholder: t("offer.chosenCandidate"),
    secure: false,
  },
];

export const fieldsEditProfile = (t: (text: string) => string) => [
  { key: "newUsername", placeholder: t("profile.username"), secure: false },
  { key: "newFirstName", placeholder: t("profile.firstName"), secure: false },
  { key: "newLastName", placeholder: t("profile.lastName"), secure: false },
  {
    key: "newProfileDescription",
    placeholder: t("profile.description"),
    secure: false,
  },
];
