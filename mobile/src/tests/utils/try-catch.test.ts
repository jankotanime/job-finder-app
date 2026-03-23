/// <reference types="jest" />

import { tryCatch } from "../../utils/try-catch";

describe("tryCatch utility", () => {
  it("returns [data, null] for resolved promises", async () => {
    const [data, error] = await tryCatch(Promise.resolve(123));

    expect(data).toBe(123);
    expect(error).toBeNull();
  });

  it("returns [null, error] for rejected promises", async () => {
    const originalError = new Error("failed");

    const [data, error] = await tryCatch(Promise.reject(originalError));

    expect(data).toBeNull();
    expect(error).toBe(originalError);
  });
});
