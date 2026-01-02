import React from "react";
import { View, StyleSheet, Dimensions, TouchableOpacity } from "react-native";
import { Text, useTheme, Button } from "react-native-paper";
import { useTranslation } from "react-i18next";
import { useRoute, useNavigation } from "@react-navigation/native";
import type { RouteProp } from "@react-navigation/native";
import type { RootStackParamList } from "../../types/RootStackParamList";
import PDFPreview from "../../components/pre-login/PdfPreview";
import { SafeAreaView } from "react-native-safe-area-context";

const { height, width } = Dimensions.get("window");

const CvPreviewScreen = () => {
  const { colors } = useTheme();
  const route = useRoute<RouteProp<RootStackParamList, "CvPreview">>();
  const navigation = useNavigation<any>();
  const { t } = useTranslation();
  const { cvUri, cvName } = route.params;

  return (
    <SafeAreaView
      style={[styles.container, { backgroundColor: colors.background }]}
    >
      <View style={styles.previewContainer}>
        <PDFPreview uri={cvUri} />
      </View>
      <Button
        mode="contained"
        onPress={() => navigation.goBack()}
        style={{ marginTop: 25, width: width * 0.8 }}
      >
        {t("offer.close")}
      </Button>
    </SafeAreaView>
  );
};

export default CvPreviewScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
  },
  previewContainer: {
    height: height * 0.8,
  },
});
