import React from "react";
import {
  StyleSheet,
  View,
  Dimensions,
  FlatList,
  TouchableOpacity,
  Alert,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Text, useTheme } from "react-native-paper";
import { FontAwesome } from "@expo/vector-icons";
import { useNavigation, useRoute } from "@react-navigation/native";
import { useApplicants } from "../../hooks/useApplicants";
import type { ApplicationItem } from "../../types/Applicants";
import { getCvById } from "../../api/cv/handleCvApi";
import { buildPhotoUrl } from "../../utils/photoUrl";
import { useTranslation } from "react-i18next";
import type { RouteProp } from "@react-navigation/native";
import type { RootStackParamList } from "../../types/RootStackParamList";

const ChosenApplicantsScreen: React.FC = () => {
  const { colors } = useTheme();
  const { t } = useTranslation();
  const navigation = useNavigation<any>();
  const route = useRoute<RouteProp<RootStackParamList, "ChosenApplicants">>();
  const offerId = route.params?.offerId;
  const { chosenApplicants, removeApplicant } = useApplicants({ offerId });

  const onOpenCv = async (a: ApplicationItem) => {
    try {
      const storageKey = a?.chosenCv?.storageKey;
      let cvUri = storageKey ? buildPhotoUrl(storageKey) : undefined;
      if (!cvUri && a?.chosenCv?.id) {
        const { body } = await getCvById(String(a.chosenCv.id));
        const key = (body?.data as any)?.storageKey;
        if (key) cvUri = buildPhotoUrl(key);
      }
      if (!cvUri) {
        Alert.alert(t("cv.title"), t("cv.openError"));
        return;
      }
      navigation.navigate("CvPreview", {
        cvUri,
        cvName: t("cv.title"),
        manage: false,
      });
    } catch (e) {
      console.error("failed to open candidate cv", e);
      Alert.alert(t("cv.title"), t("cv.openErrorGeneric"));
    }
  };

  const renderItem = ({ item }: { item: ApplicationItem }) => {
    const name =
      item?.candidate?.name ||
      [item?.candidate?.firstName, item?.candidate?.lastName]
        .filter(Boolean)
        .join(" ") ||
      item?.candidate?.username ||
      t("offerExtra.candidate");
    const status = item?.status ? String(item.status) : "";

    return (
      <TouchableOpacity
        onPress={() => onOpenCv(item)}
        style={[styles.item, { backgroundColor: colors.onBackground }]}
      >
        <View style={[styles.avatar, { backgroundColor: colors.background }]} />
        <View style={styles.content}>
          <Text variant="titleMedium" numberOfLines={1}>
            {name}
          </Text>
          <Text variant="labelSmall" style={{ opacity: 0.7 }}>
            {t("offer.status")}: {status}
          </Text>
        </View>
        <TouchableOpacity
          onPress={() => removeApplicant(item.id)}
          style={styles.trashButton}
        >
          <FontAwesome name="trash" size={20} color={colors.error} />
        </TouchableOpacity>
      </TouchableOpacity>
    );
  };

  return (
    <SafeAreaView
      style={[styles.container, { backgroundColor: colors.background }]}
    >
      <View style={styles.header}>
        <Text variant="titleLarge">{t("chosenApplicants.header")}</Text>
      </View>
      <FlatList
        data={chosenApplicants}
        keyExtractor={(item) => String(item.id)}
        renderItem={renderItem}
        contentContainerStyle={{ paddingHorizontal: 12, paddingBottom: 24 }}
        ListEmptyComponent={
          <View style={{ padding: 16 }}>
            <Text style={{ opacity: 0.7 }}>{t("common.emptyApplicants")}</Text>
          </View>
        }
      />
    </SafeAreaView>
  );
};

export default ChosenApplicantsScreen;

const styles = StyleSheet.create({
  container: { flex: 1 },
  header: { paddingHorizontal: 12, paddingVertical: 12 },
  item: {
    flexDirection: "row",
    alignItems: "center",
    borderRadius: 10,
    padding: 10,
    marginBottom: 8,
  },
  avatar: {
    width: 40,
    height: 40,
    borderRadius: 20,
    marginRight: 10,
  },
  content: { flex: 1 },
  trashButton: { paddingHorizontal: 8, paddingVertical: 4, borderRadius: 8 },
});
