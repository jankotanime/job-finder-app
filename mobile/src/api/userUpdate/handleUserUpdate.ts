import { tryCatch } from "../../utils/try-catch";
import { apiFetch } from "../client";

export const updateUserEmail = async (payload: Record<string, any>) => {
  const [result, error] = await tryCatch(
    apiFetch(
      "/user/update/email",
      {
        method: "PATCH",
        body: JSON.stringify(payload),
      },
      true,
    ),
  );
  if (error) console.error("update email error:", error);
  if (!result) throw new Error("No response received");
  return result;
};

export const updateUserPhoneNumber = async (payload: Record<string, any>) => {
  const [result, error] = await tryCatch(
    apiFetch(
      "/user/update/phone-number",
      {
        method: "PATCH",
        body: JSON.stringify(payload),
      },
      true,
    ),
  );
  if (error) console.error("update phone-number error:", error);
  if (!result) throw new Error("No response received");
  return result;
};

type UpdateUserDataParams = {
  profilePhoto?: string;
  newUsername: string;
  newFirstName: string;
  newLastName: string;
  newProfileDescription?: string;
  password?: string;
};

const appendPhotoToForm = (form: FormData, uri?: string) => {
  if (!uri) return;
  const lastSlash = uri.lastIndexOf("/");
  const filename = lastSlash >= 0 ? uri.substring(lastSlash + 1) : "photo.jpg";
  const ext = filename.split(".").pop()?.toLowerCase();
  const mime =
    ext === "png"
      ? "image/png"
      : ext === "jpg" || ext === "jpeg"
        ? "image/jpeg"
        : "application/octet-stream";
  form.append("profilePhoto", { uri, name: filename, type: mime } as any);
};

export const updateUserData = async (params: UpdateUserDataParams) => {
  const form = new FormData();
  appendPhotoToForm(form, params.profilePhoto);
  form.append("newUsername", params.newUsername);
  form.append("newFirstName", params.newFirstName);
  form.append("newLastName", params.newLastName);
  if (typeof params.newProfileDescription === "string") {
    const desc = params.newProfileDescription.trim();
    if (desc.length > 0) form.append("newProfileDescription", desc);
  }
  if (params.password) {
    form.append("password", params.password);
  }
  const [result, error] = await tryCatch(
    apiFetch(
      "/user/update/user-data",
      {
        method: "PATCH",
        body: form,
      },
      true,
    ),
  );
  if (error) console.error("update user-data error:", error);
  if (!result) throw new Error("No response received");
  return result;
};

export default {
  updateUserEmail,
  updateUserPhoneNumber,
  updateUserData,
};
