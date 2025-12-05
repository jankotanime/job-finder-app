import React, { useState } from "react";
import {
  StyleSheet,
  ScrollView,
  Dimensions,
  KeyboardAvoidingView,
  Platform,
} from "react-native";
import WhiteCard from "../../components/pre-login/WhiteCard";
import ImageBackground from "../../components/reusable/ImageBackground";
import Input from "../../components/reusable/Input";
import { fieldsProfileCompletionGoogle } from "../../constans/formFields";
import { useTranslation } from "react-i18next";
import { Button } from "react-native-paper";

interface FormState {
  phoneNumber: string;
  username: string;
}
const { width, height } = Dimensions.get("window");
const ProfileCompletionGoogle = () => {
  const { t } = useTranslation();
  const [formState, setFormState] = useState<FormState>({
    phoneNumber: "",
    username: "",
  });
  const [isLoading, setIsLoading] = useState<boolean>(false);
  return (
    <>
      <ImageBackground />
      <WhiteCard>
        <KeyboardAvoidingView
          style={{ flex: 1 }}
          behavior={Platform.OS === "ios" ? "padding" : "height"}
          keyboardVerticalOffset={Platform.OS === "ios" ? 230 : 30}
        >
          <ScrollView>
            {fieldsProfileCompletionGoogle(t).map((field) => {
              return (
                <Input
                  key={field.key}
                  placeholder={field.placeholder}
                  value={formState[field.key as keyof FormState]}
                  onChangeText={(text) =>
                    setFormState((prev) => ({
                      ...prev,
                      [field.key]: text,
                    }))
                  }
                  mode="outlined"
                />
              );
            })}
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
      </WhiteCard>
    </>
  );
};

export default ProfileCompletionGoogle;

const styles = StyleSheet.create({
  completeButton: {
    width: width * 0.8,
    height: 48,
    alignSelf: "center",
    marginTop: height * 0.1,
  },
});
