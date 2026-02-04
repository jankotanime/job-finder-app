import React, { useState } from "react";
import {
  View,
  StyleSheet,
  TouchableOpacity,
  Text,
  Dimensions,
  KeyboardAvoidingView,
  Platform,
} from "react-native";
import { useTheme, Button, TextInput } from "react-native-paper";
import { useRoute, useNavigation } from "@react-navigation/native";
import { uploadPDF } from "../../utils/pickerUtils";
import { uploadCv } from "../../api/cv/handleCvApi";
import { tryCatch } from "../../utils/try-catch";
import useSelectCv from "../../hooks/useSelectCv";
import useCvNames from "../../hooks/useCvNames";
import type { RouteProp } from "@react-navigation/native";
import type { RootStackParamList } from "../../types/RootStackParamList";
import Input from "../../components/reusable/Input";

const { width, height } = Dimensions.get("window");

const CvSelectScreen = () => {
  const { colors } = useTheme();
  const route = useRoute<RouteProp<RootStackParamList, "CvSelect">>();
  const navigation = useNavigation<any>();
  const [cvUri, setCvUri] = useState<string>("");
  const [cvName, setCvName] = useState<string>("");
  const [name, setNameInput] = useState<string>("");
  const { reload: reloadSelection } = useSelectCv();
  const { setName: setCvNameForId } = useCvNames();
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
    navigation.replace("Main");
  };

  const handleSave = async () => {
    const trimmed = String(name || "").trim();
    if (!cvUri) return;
    if (!trimmed) {
      alert("Podaj nazwę CV");
      return;
    }
    const [resp, err] = await tryCatch(uploadCv(cvUri));
    if (err || !resp) {
      alert("Nie udało się zapisać CV");
      return;
    }
    const created = resp.body?.data;
    const cvId: string | undefined =
      created?.id || created?.cvId || created?.uuid;
    if (!cvId) {
      alert("Nie udało się zapisać CV");
      return;
    }
    setCvNameForId(String(cvId), trimmed);
    await reloadSelection();
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
      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : "height"}
        keyboardVerticalOffset={Platform.OS === "ios" ? 80 : 0}
      >
        <Input
          placeholder="Nazwa CV"
          mode="outlined"
          value={name}
          onChangeText={setNameInput}
          style={{ top: -height * 0.025 }}
        />
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
          onPress={handleSave}
          disabled={!cvUri || !String(name || "").trim()}
        >
          Zapisz
        </Button>
      </KeyboardAvoidingView>
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
