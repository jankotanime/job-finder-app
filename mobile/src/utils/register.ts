import { tryCatch } from "./try-catch"

export const register = async (email: string, password: string) => {
    const [response, error] = await tryCatch(fetch(`${process.env.EXPO_PUBLIC_API_URL}/auth/register`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                username: 'test23',
                email: email,
                password: password,
                phoneNumber: "123456785"
            }),
            credentials: 'include'
        }))
    if (error || !response) return {error: error?.message || String(error)}
    if (!response.ok) {
        const errorBody = await response.json()
        return {error: errorBody.err}
    }
    const data = await response.json()
    return data
}