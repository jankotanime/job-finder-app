import * as ImagePicker from "expo-image-picker";
import { Alert } from "react-native";
import i18n from "../locales/i18n";
import * as DocumentPicker from "expo-document-picker";

export async function uploadCameraImage(): Promise<string | null> {
  try {
    const cam = await ImagePicker.getCameraPermissionsAsync();
    if (!cam.granted) {
      const req = await ImagePicker.requestCameraPermissionsAsync();
      if (!req.granted) {
        Alert.alert(i18n.t("pickerErrors.camera_no_permission"));
        return null;
      }
    }
    const result = await ImagePicker.launchCameraAsync({
      cameraType: ImagePicker.CameraType.front,
      allowsEditing: true,
      aspect: [1, 1],
      quality: 1,
    });
    const canceled =
      (result as any)?.canceled === true || (result as any)?.cancelled === true;
    if (canceled) return null;
    let uri: string | undefined;
    if (
      (result as any)?.assets &&
      Array.isArray((result as any).assets) &&
      (result as any).assets.length > 0
    ) {
      uri = (result as any).assets[0]?.uri;
    } else if ((result as any)?.uri) {
      uri = (result as any).uri;
    }
    if (!uri) {
      Alert.alert(i18n.t("pickerErrors.no_photo_uri_camera"));
      return null;
    }
    return uri;
  } catch (e) {
    console.error("camera error util: ", e);
    return null;
  }
}

export async function uploadGalleryImage(): Promise<string | null> {
  try {
    const cam = await ImagePicker.getMediaLibraryPermissionsAsync();
    if (!cam.granted) {
      const req = await ImagePicker.requestMediaLibraryPermissionsAsync();
      if (!req.granted) {
        Alert.alert(i18n.t("pickerErrors.gallery_no_permission"));
        return null;
      }
    }
    const result = await ImagePicker.launchImageLibraryAsync({
      allowsEditing: true,
      aspect: [1, 1],
      quality: 1,
    });
    const canceled =
      (result as any)?.canceled === true || (result as any)?.cancelled === true;
    if (canceled) return null;
    let uri: string | undefined;
    if (
      (result as any)?.assets &&
      Array.isArray((result as any).assets) &&
      (result as any).assets.length > 0
    ) {
      uri = (result as any).assets[0]?.uri;
    } else if ((result as any)?.uri) {
      uri = (result as any).uri;
    }
    if (!uri) {
      Alert.alert(i18n.t("pickerErrors.no_photo_uri_gallery"));
      return null;
    }
    return uri;
  } catch (e) {
    console.error("gallery error util: ", e);
    return null;
  }
}
export async function uploadPDF(): Promise<{ uri: string; name: string }> {
  try {
    const result = await DocumentPicker.getDocumentAsync({
      type: "application/pdf",
      copyToCacheDirectory: true,
    });
    if (result.canceled) return { uri: "", name: "" };
    return { uri: result.assets[0].uri, name: result.assets[0].name };
  } catch (err) {
    console.error("PDF picker error:", err);
    return { uri: "", name: "" };
  }
}
