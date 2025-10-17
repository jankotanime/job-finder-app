type SuccessReesult<T> = readonly [T, null]
type ErrorResult<E = Error> = readonly [null , E]
type Result <T, E = Error> = SuccessReesult<T> | ErrorResult<E>

export async function tryCatch<T, E = Error>(
    promise: Promise<T>
): Promise<Result<T, E>> {
    try {
        const data = await promise
        return [data, null] as const
    } catch (e) {
        return [null, e as E] as const
    }
}