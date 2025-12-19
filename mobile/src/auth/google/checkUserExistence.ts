export async function checkUserExistence(idToken: string | null) {
  const response = await fetch(
    `${process.env.EXPO_PUBLIC_API_GOOGLE_AUTH_URL}/check-user-existence`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ googleToken: idToken }),
    },
  );
  const result = await response.json();
  if (result.data.exist === "USER_NOT_EXIST") {
    return "USER_NOT_EXIST";
  }
  if (result.data.exist === "USER_EXIST_WITH_EMAIL") {
    return "USER_EXIST_WITH_EMAIL";
  }
  return "USER_EXIST";
}
