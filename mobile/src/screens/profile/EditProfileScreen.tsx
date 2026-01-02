import React, { useMemo, useState } from "react";
import {
  View,
  ScrollView,
  StyleSheet,
  Dimensions,
  Image,
  TouchableOpacity,
  KeyboardAvoidingView,
  Platform,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Button, HelperText, Text, useTheme, Icon } from "react-native-paper";
import { useTranslation } from "react-i18next";
import { useAuth } from "../../contexts/AuthContext";
import PhotoPickerModal from "../../components/pre-login/PhotoPickerModal";
import { uploadCameraImage, uploadGalleryImage } from "../../utils/pickerUtils";
import Input from "../../components/reusable/Input";
import { fieldsEditProfile } from "../../constans/formFields";
import { updateUserData } from "../../api/userUpdate/handleUserUpdate";
import { refreshAccessToken } from "../../api/client";

const { width } = Dimensions.get("window");

const EditProfileScreen = () => {
  const { colors } = useTheme();
  const { t } = useTranslation();
  const { userInfo, refreshAuth } = useAuth();

  const [form, setForm] = useState({
    newUsername: String(userInfo?.username ?? ""),
    newFirstName: String(userInfo?.firstName ?? ""),
    newLastName: String(userInfo?.lastName ?? ""),
    newProfileDescription: "",
    profilePhoto: undefined as string | undefined,
  });
  const [modalVisible, setModalVisible] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const errors = useMemo(() => {
    const e: Record<string, string> = {};
    if (!form.newUsername.trim())
      e.newUsername = t("profile.errors.usernameRequired");
    if (!form.newFirstName.trim())
      e.newFirstName = t("profile.errors.firstNameRequired");
    if (!form.newLastName.trim())
      e.newLastName = t("profile.errors.lastNameRequired");
    return e;
  }, [form, t]);

  const handlePickCamera = async () => {
    try {
      const uri = await uploadCameraImage();
      if (uri) setForm((prev) => ({ ...prev, profilePhoto: uri }));
    } finally {
      setModalVisible(false);
    }
  };
  const handlePickGallery = async () => {
    try {
      const uri = await uploadGalleryImage();
      if (uri) setForm((prev) => ({ ...prev, profilePhoto: uri }));
    } finally {
      setModalVisible(false);
    }
  };

  const onSubmit = async () => {
    if (Object.keys(errors).length > 0) return;
    setSubmitting(true);
    try {
      await updateUserData({
        newUsername: form.newUsername.trim(),
        newFirstName: form.newFirstName.trim(),
        newLastName: form.newLastName.trim(),
        newProfileDescription: form.newProfileDescription.trim(),
        profilePhoto: form.profilePhoto,
      });
      await refreshAccessToken();
      await refreshAuth();
    } catch (e) {
      console.error("error while saving profile: ", e);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: colors.background }}>
      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === "ios" ? "padding" : "height"}
      >
        <ScrollView
          contentContainerStyle={[
            styles.container,
            { backgroundColor: colors.background },
          ]}
          keyboardShouldPersistTaps="handled"
        >
          <Text
            variant="titleLarge"
            style={[styles.header, { color: colors.primary }]}
          >
            {t("profile.editHeader", { defaultValue: "Edytuj profil" })}
          </Text>
          <View style={styles.photoContainer}>
            {!form.profilePhoto ? (
              <TouchableOpacity
                style={[
                  styles.logoWrapper,
                  styles.placeholderLogo,
                  {
                    backgroundColor: colors.background,
                    borderColor: colors.onSurface,
                  },
                ]}
                onPress={() => setModalVisible(true)}
              >
                <Icon source="camera" size={50} />
              </TouchableOpacity>
            ) : (
              <TouchableOpacity
                style={styles.logoWrapper}
                onPress={() => setModalVisible(true)}
              >
                <Image
                  source={{ uri: form.profilePhoto }}
                  style={{ width: 120, height: 120, borderRadius: 15 }}
                />
              </TouchableOpacity>
            )}
          </View>
          {modalVisible && (
            <PhotoPickerModal
              visible={modalVisible}
              onClose={() => setModalVisible(false)}
              onPickCamera={handlePickCamera}
              onPickGallery={handlePickGallery}
            />
          )}
          {fieldsEditProfile(t).map(({ key, placeholder, secure }) => {
            const commonProps = {
              placeholder,
              mode: "outlined" as const,
              secure,
            };
            switch (key) {
              case "newUsername":
                return (
                  <View style={styles.formGroup} key={key}>
                    <Input
                      {...commonProps}
                      value={form.newUsername}
                      onChangeText={(v) =>
                        setForm((p) => ({ ...p, newUsername: v }))
                      }
                      style={styles.inputStyle}
                    />
                    <HelperText type="error" visible={!!errors.newUsername}>
                      {errors.newUsername}
                    </HelperText>
                  </View>
                );
              case "newFirstName":
                return (
                  <View style={styles.formGroup} key={key}>
                    <Input
                      {...commonProps}
                      value={form.newFirstName}
                      onChangeText={(v) =>
                        setForm((p) => ({ ...p, newFirstName: v }))
                      }
                      style={styles.inputStyle}
                    />
                    <HelperText type="error" visible={!!errors.newFirstName}>
                      {errors.newFirstName}
                    </HelperText>
                  </View>
                );
              case "newLastName":
                return (
                  <View style={styles.formGroup} key={key}>
                    <Input
                      {...commonProps}
                      value={form.newLastName}
                      onChangeText={(v) =>
                        setForm((p) => ({ ...p, newLastName: v }))
                      }
                      style={styles.inputStyle}
                    />
                    <HelperText type="error" visible={!!errors.newLastName}>
                      {errors.newLastName}
                    </HelperText>
                  </View>
                );
              case "newProfileDescription":
                return (
                  <View style={styles.formGroup} key={key}>
                    <Input
                      {...commonProps}
                      value={form.newProfileDescription}
                      onChangeText={(v) =>
                        setForm((p) => ({ ...p, newProfileDescription: v }))
                      }
                      multiline
                      numberOfLines={4}
                      style={styles.inputStyle}
                    />
                  </View>
                );
              default:
                return null;
            }
          })}
          <View style={[styles.row, { justifyContent: "center" }]}>
            <Button
              style={{ width: "100%" }}
              mode="contained"
              disabled={Object.keys(errors).length > 0 || submitting}
              onPress={onSubmit}
            >
              {t("profile.save")}
            </Button>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 16,
    paddingTop: 20,
  },
  header: {
    fontWeight: "700",
    marginBottom: 12,
  },
  formGroup: {
    marginBottom: -5,
  },
  row: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    marginTop: 15,
  },
  inputStyle: {
    width: "100%",
    alignSelf: "stretch",
    top: 0,
  },
  photoContainer: {
    marginTop: 12,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
  },
  logoWrapper: {
    width: 120,
    height: 120,
    borderRadius: 15,
    borderWidth: 1,
    overflow: "hidden",
    alignSelf: "center",
    alignItems: "center",
    justifyContent: "center",
  },
  placeholderLogo: {
    borderStyle: "dashed",
    opacity: 0.75,
    backgroundColor: "white",
  },
});

export default EditProfileScreen;
