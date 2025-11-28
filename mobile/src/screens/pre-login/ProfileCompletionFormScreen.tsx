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
  Image,
  Modal,
} from "react-native";
import Input from "../../components/reusable/Input";
import { fieldsProfileCompletion } from "../../constans/formFields";
import { useTranslation } from "react-i18next";
import { useTheme, Icon, Button } from "react-native-paper";
import Error from "../../components/reusable/Error";
import WhiteCard from "../../components/pre-login/WhiteCard";
import ImageBackground from "../../components/reusable/ImageBackground";
import PhotoPickerModal from "../../components/pre-login/PhotoPickerModal";
import {
  uploadCameraImage,
  uploadGalleryImage,
  uploadPDF,
} from "../../utils/pickerUtils";
import { Ionicons } from "@expo/vector-icons";
import PDFPreview from "../../components/pre-login/PdfPreview";

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
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [isPhotoAvailable, setIsPhotoAvailable] = useState<boolean>(false);
  const [isCvAvailable, setIsCvAvailable] = useState<boolean>(false);
  const [cvName, setCvName] = useState("");
  const [pdfModalVisible, setPdfModalVisible] = useState<boolean>(false);

  const handlePickCamera = async () => {
    try {
      const uri = await uploadCameraImage();
      if (uri) {
        setIsPhotoAvailable(true);
        setFormState((prev) => ({ ...prev, profilePhoto: uri }));
      }
    } finally {
      setModalVisible(false);
    }
  };
  const handlePickGallery = async () => {
    try {
      const uri = await uploadGalleryImage();
      if (uri) {
        setIsPhotoAvailable(true);
        setFormState((prev) => ({ ...prev, profilePhoto: uri }));
      }
    } finally {
      setModalVisible(false);
    }
  };
  const handlePickPDF = async () => {
    const pdf = await uploadPDF();
    if (pdf.uri != "" && pdf.name != "") {
      setFormState((prev) => ({
        ...prev,
        cv: pdf.uri,
      }));
      setCvName(pdf.name);
      setIsCvAvailable(true);
    }
  };
  return (
    <>
      <ImageBackground />
      <View style={[styles.container, { backgroundColor: colors.background }]}>
        <WhiteCard>
          <KeyboardAvoidingView
            style={{ flex: 1 }}
            behavior={Platform.OS === "ios" ? "padding" : "height"}
            keyboardVerticalOffset={Platform.OS === "ios" ? 230 : 30}
          >
            <View style={styles.photoContainer}>
              {!isPhotoAvailable ? (
                <TouchableOpacity
                  style={[styles.logoWrapper, styles.placeholderLogo]}
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
                    source={{ uri: formState.profilePhoto }}
                    style={{ width: 160, height: 160, borderRadius: 160 }}
                  />
                </TouchableOpacity>
              )}
            </View>
            {modalVisible && (
              <PhotoPickerModal
                visible={modalVisible}
                onClose={() => setModalVisible(false)}
                onPickCamera={() => handlePickCamera()}
                onPickGallery={() => handlePickGallery()}
              />
            )}
            <ScrollView contentContainerStyle={styles.scrollContent}>
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
                          setFormState((prev) => ({
                            ...prev,
                            description: text,
                          }))
                        }
                        mode="outlined"
                        style={{ top: height * 0.01 }}
                      />
                    </View>
                  );
                }
                if (field.key === "cv" && !isCvAvailable) {
                  return (
                    <TouchableOpacity
                      key={field.key}
                      style={styles.uploadButton}
                      onPress={() => handlePickPDF()}
                    >
                      <Text style={styles.uploadButtonText}>
                        {field.placeholder}
                      </Text>
                    </TouchableOpacity>
                  );
                } else if (field.key === "cv" && isCvAvailable) {
                  return (
                    <TouchableOpacity
                      key={field.key}
                      style={styles.uploadButton}
                      onPress={() => handlePickPDF()}
                    >
                      <Text style={styles.uploadButtonText}>{cvName}</Text>
                      <TouchableOpacity
                        onPress={() => setPdfModalVisible(true)}
                      >
                        <Ionicons
                          name="eye-outline"
                          size={25}
                          style={{ marginLeft: 20 }}
                        />
                      </TouchableOpacity>
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
        </WhiteCard>
        {pdfModalVisible && (
          <Modal visible={pdfModalVisible} animationType="slide">
            <PDFPreview uri={formState.cv} />
            <Button
              onPress={() => setPdfModalVisible(false)}
              style={styles.exitPdfButton}
              labelStyle={{ fontSize: 20, marginTop: 20 }}
            >
              {t("profileCompletion.exit")}
            </Button>
          </Modal>
        )}
      </View>
    </>
  );
};

export default ProfileCompletionFormScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: height * 0.04,
  },
  scrollContent: {
    padding: 16,
  },
  inputContainer: {
    top: 0,
  },
  logoWrapper: {
    width: 160,
    height: 160,
    borderRadius: 160,
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
  photoText: {
    fontSize: 18,
    marginTop: 15,
  },
  photoContainer: {
    position: "absolute",
    top: -height * 0.21,
    display: "flex",
    flexDirection: "column",
    alignSelf: "center",
    backgroundColor: "transparent",
    zIndex: 2,
  },
  uploadButton: {
    display: "flex",
    flexDirection: "row",
    padding: 14,
    borderRadius: 15,
    borderWidth: 1,
    borderStyle: "dashed",
    alignSelf: "center",
    alignItems: "center",
    justifyContent: "center",
    marginTop: height * 0.03,
    width: width * 0.8,
  },
  uploadButtonText: {
    fontSize: 16,
    opacity: 0.7,
    width: width * 0.6,
    textAlign: "center",
  },
  completeButton: {
    width: width * 0.8,
    height: 48,
    alignSelf: "center",
    marginTop: height * 0.05,
  },
  exitPdfButton: {
    height: height * 0.08,
  },
});
