import React, { useState } from "react";
import {
  View,
  StyleSheet,
  TouchableOpacity,
  Text,
  Dimensions,
} from "react-native";
import { useTheme, Button } from "react-native-paper";
import { useRoute, useNavigation } from "@react-navigation/native";
import { uploadPDF } from "../../utils/pickerUtils";
import { uploadCv } from "../../api/cv/handleCvApi";
import type { RouteProp } from "@react-navigation/native";
import type { RootStackParamList } from "../../types/RootStackParamList";

const { width, height } = Dimensions.get("window");

const CvSelectScreen = () => {
  const { colors } = useTheme();
  const route = useRoute<RouteProp<RootStackParamList, "CvSelect">>();
  const navigation = useNavigation<any>();
  const [cvUri, setCvUri] = useState<string>("");
  const [cvName, setCvName] = useState<string>("");
  const disabled = route.params.disableSkip;

  const handlePickPDF = async () => {
    const pdf = await uploadPDF();
    if (pdf.uri && pdf.name) {
      setCvUri(pdf.uri);
      setCvName(pdf.name);
    }
  };

  const handleNext = () => {
    if (!cvUri) return;
    navigation.navigate("CvPreview", { cvUri, cvName });
  };

  const handleSkip = () => {
    if (navigation.canGoBack()) navigation.goBack();
    else navigation.replace("Main");
  };

  const handleApplyCv = async (fileUri: string) => {
    await uploadCv(fileUri);
    !disabled ? navigation.replace("Main") : navigation.goBack();
  };

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      <TouchableOpacity
        style={[styles.uploadButton, { borderColor: colors.onSurface }]}
        onPress={handlePickPDF}
      >
        <Text style={[styles.uploadButtonText, { color: colors.onSurface }]}>
          {cvName ? cvName : "Załącz CV (PDF)"}
        </Text>
      </TouchableOpacity>
      <Button
        mode="contained"
        style={styles.nextButton}
        onPress={handleNext}
        disabled={!cvUri}
      >
        Podgląd CV
      </Button>
      <Button
        mode="contained"
        style={styles.applyButton}
        onPress={() => handleApplyCv(cvUri)}
        disabled={!cvUri}
      >
        Zapisz
      </Button>
      {!disabled ? (
        <TouchableOpacity style={styles.skip} onPress={handleSkip}>
          <Text style={{ color: colors.primary, fontWeight: 700 }}>
            Pomiń na razie
          </Text>
        </TouchableOpacity>
      ) : null}
    </View>
  );
};

export default CvSelectScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    padding: 16,
  },
  uploadButton: {
    width: width * 0.8,
    padding: 14,
    borderRadius: 15,
    borderWidth: 1,
    borderStyle: "dashed",
    alignItems: "center",
    justifyContent: "center",
    marginBottom: height * 0.03,
  },
  uploadButtonText: {
    fontSize: 16,
    opacity: 0.8,
  },
  nextButton: {
    width: width * 0.8,
  },
  applyButton: {
    width: width * 0.8,
    marginTop: 10,
  },
  skip: {
    position: "absolute",
    right: width * 0.08,
    bottom: height * 0.04,
  },
});
