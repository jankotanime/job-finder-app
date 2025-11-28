import React, { useState } from "react";
import {
  View,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Text,
  Dimensions,
  KeyboardAvoidingView,
  Platform,
} from "react-native";
import Input from "../reusable/Input";
import { fieldsProfileCompletion } from "../../constans/formFields";
import { useTranslation } from "react-i18next";
import { useTheme, Icon, Button } from "react-native-paper";
import { SafeAreaView } from "react-native-safe-area-context";
import Error from "../../components/reusable/Error";

interface FormState {
  firstName: string;
  lastName: string;
  location: string;
  description: string;
  profilePhoto: string;
  cv: string;
}
const { height, width } = Dimensions.get("window");
const ProfileCompletionFormScreen = () => {
  const [formState, setFormState] = useState<FormState>({
    firstName: "",
    lastName: "",
    location: "",
    description: "",
    profilePhoto: "",
    cv: "",
  });
  const { t } = useTranslation();
  const { colors } = useTheme();
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");
  return (
    <SafeAreaView
      style={[styles.container, { backgroundColor: colors.background }]}
    >
      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === "ios" ? "padding" : "height"}
        keyboardVerticalOffset={Platform.OS === "ios" ? 230 : 30}
      >
        <ScrollView contentContainerStyle={styles.scrollContent}>
          <View style={styles.photoContainer}>
            <TouchableOpacity
              style={[styles.logoWrapper, styles.placeholderLogo]}
            >
              <Icon source="camera" size={50} />
            </TouchableOpacity>
            <Text style={[styles.photoText, { color: colors.onSurface }]}>
              {t("profileCompletion.choosePicture")}
            </Text>
          </View>
          {fieldsProfileCompletion(t).map((field) => {
            if (field.key === "description") {
              return (
                <View key={field.key}>
                  <Input
                    multiline
                    numberOfLines={6}
                    placeholder={field.placeholder}
                    value={formState.description}
                    onChangeText={(text) =>
                      setFormState((prev) => ({ ...prev, description: text }))
                    }
                    mode="outlined"
                    style={{ top: height * 0.01 }}
                  />
                </View>
              );
            }
            if (field.key === "cv") {
              return (
                <TouchableOpacity key={field.key} style={styles.uploadButton}>
                  <Text style={styles.uploadButtonText}>
                    {field.placeholder}
                  </Text>
                </TouchableOpacity>
              );
            }
            return (
              <View key={field.key} style={styles.inputContainer}>
                <Input
                  placeholder={field.placeholder}
                  value={formState[field.key as keyof FormState]}
                  onChangeText={(text) =>
                    setFormState((prev) => ({
                      ...prev,
                      [field.key]: text,
                    }))
                  }
                  mode="outlined"
                  style={{ top: height * 0.01 }}
                />
              </View>
            );
          })}
          {error ? <Error error={error} /> : null}
          <Button
            mode="contained"
            style={styles.completeButton}
            contentStyle={{ height: 48 }}
            onPress={() => {
              // handleProfileCompletionSubmit({
              //     formState,
              //     setError,
              //     setIsLoading,
              //     navigation,
              //     t,
              // });
            }}
            disabled={
              isLoading ||
              Object.values(formState).some((value) => value.trim() === "")
            }
            loading={isLoading}
          >
            {isLoading
              ? t("profileCompletion.moving_forward")
              : t("profileCompletion.move_forward")}
          </Button>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
};

export default ProfileCompletionFormScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scrollContent: {
    padding: 16,
  },
  inputContainer: {
    top: 0,
  },
  logoWrapper: {
    width: 180,
    height: 180,
    borderRadius: 12,
    borderWidth: 1,
    overflow: "hidden",
    alignSelf: "center",
    alignItems: "center",
    justifyContent: "center",
  },
  placeholderLogo: {
    borderStyle: "dashed",
    opacity: 0.6,
  },
  photoText: {
    fontSize: 18,
    marginTop: 15,
  },
  photoContainer: {
    display: "flex",
    flexDirection: "column",
    alignSelf: "center",
  },
  uploadButton: {
    padding: 14,
    borderRadius: 15,
    borderWidth: 1,
    borderStyle: "dashed",
    alignSelf: "center",
    alignItems: "center",
    marginTop: height * 0.03,
    width: width * 0.8,
  },
  uploadButtonText: {
    fontSize: 16,
    opacity: 0.7,
  },
  completeButton: {
    width: width * 0.8,
    height: 48,
    alignSelf: "center",
    marginTop: height * 0.02,
  },
});
