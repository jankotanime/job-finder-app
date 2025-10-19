export const fieldsLogin = (t: (text: string) => string) => [
    { key: 'email', placeholder: t('email'), secure: false },
    { key: 'password', placeholder: t('password'), secure: true }
] as const
export const fieldsRegister = (t: (text: string) => string) => [
    { key: 'username', placeholder: t('register.username'), secure: false },
    { key: 'email', placeholder: t('email'), secure: false },
    { key: 'phone_number', placeholder: t('register.phone_number'), secure: false },
    { key: 'password', placeholder: t('password'), secure: true },
    { key: 'repeatPassword', placeholder: t('repeat_password'), secure: true}
] as const