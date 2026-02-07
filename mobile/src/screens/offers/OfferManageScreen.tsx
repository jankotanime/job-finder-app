import React, { useMemo, useEffect, useState, useCallback } from "react";
import {
  StyleSheet,
  View,
  Image,
  Dimensions,
  FlatList,
  TouchableOpacity,
  Alert,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Button, Text, useTheme } from "react-native-paper";
import { useTranslation } from "react-i18next";
import { FontAwesome } from "@expo/vector-icons";
import {
  useNavigation,
  useRoute,
  useFocusEffect,
} from "@react-navigation/native";
import type { RouteProp } from "@react-navigation/native";
import type { RootStackParamList } from "../../types/RootStackParamList";
import { buildPhotoUrl } from "../../utils/photoUrl";
import { getOfferById } from "../../api/offers/handleOffersApi";
import { getCvById } from "../../api/cv/handleCvApi";
import RenderApplicant from "../../components/main/offers/RenderApplicant";
import { ApplicationItem } from "../../types/Applicants";
import { useApplicants } from "../../hooks/useApplicants";
import { createJob } from "../../api/jobs/handleJobApi";
import {
  acceptApplication,
  rejectApplication,
} from "../../api/applications/handleApplicationApi";
import { useOfferStorageContext } from "../../contexts/OfferStorageContext";

const { width, height } = Dimensions.get("window");

const getCandidates = async (id: string) => {
  const { body } = await getOfferById(id);
  const apps = Array.isArray((body?.data as any)?.applications)
    ? ((body?.data as any)?.applications as ApplicationItem[])
    : [];
  return apps;
};

const OfferManageScreen = () => {
  const navigation = useNavigation<any>();
  const { colors } = useTheme();
  const { t } = useTranslation();
  const imageHeight = useMemo(() => Math.round(width * 0.55), []);
  const route = useRoute<RouteProp<RootStackParamList, "OfferManage">>();
  const [applicants, setApplicants] = useState<ApplicationItem[]>([]);
  const offer = route.params?.offer;
  const photoUri = buildPhotoUrl(offer?.photo?.storageKey ?? undefined);
  const offerId = offer?.id as string;
  const { chosenApplicants, reload, isReady } = useApplicants({ offerId });
  const chosenApplicantsIds = chosenApplicants.map((app) => app.id);
  const { removeSavedOffer } = useOfferStorageContext();
  const notChosenApplicantsIds = applicants
    .filter((app) => !chosenApplicants.some((chosen) => chosen.id === app.id))
    .map((app) => app.id);

  useFocusEffect(
    useCallback(() => {
      reload();
      return () => {};
    }, [reload]),
  );
  useEffect(() => {
    if (isReady) reload();
  }, [isReady, reload]);

  const onPressApplicant = async (a: ApplicationItem) => {
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
        manage: true,
        offerId: offerId,
        applicant: a,
      });
    } catch (e) {
      console.error("failed to open candidate cv", e);
      Alert.alert(t("cv.title"), t("cv.openErrorGeneric"));
    }
  };
  const handleAccept = async (offerId: string) => {
    await Promise.all(
      chosenApplicantsIds.map((applicationId) =>
        acceptApplication(offerId, applicationId),
      ),
    );
    await Promise.all(
      notChosenApplicantsIds.map((applicationId) =>
        rejectApplication(offerId, applicationId),
      ),
    );
    await createJob(offerId);
    await removeSavedOffer(offer);
    navigation.goBack();
  };
  useEffect(() => {
    const apps = Array.isArray((offer as any)?.applications)
      ? ((offer as any).applications as ApplicationItem[])
      : [];
    setApplicants(apps);
    if ((!apps || apps.length === 0) && offerId) {
      (async () => {
        const fetched = await getCandidates(offerId);
        setApplicants(fetched);
      })();
    }
  }, [offer]);
  return (
    <SafeAreaView
      style={[styles.container, { backgroundColor: colors.background }]}
    >
      <FlatList
        data={applicants}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => {
          const isSelected = chosenApplicants.some((a) => a.id === item.id);
          return (
            <RenderApplicant
              item={item}
              onPress={onPressApplicant}
              selected={isSelected}
            />
          );
        }}
        showsVerticalScrollIndicator={false}
        ListHeaderComponent={
          <View>
            {photoUri ? (
              <Image
                source={{ uri: photoUri }}
                style={[styles.image, { height: imageHeight }]}
              />
            ) : (
              <View
                style={[
                  styles.image,
                  { height: imageHeight, backgroundColor: colors.surface },
                ]}
              />
            )}
            <View style={styles.section}>
              <Text variant="titleLarge" style={styles.title}>
                {offer.title}
              </Text>
              <Text variant="titleMedium" style={{ opacity: 0.8 }}>
                {typeof offer.salary === "number" ? `${offer.salary} zł` : ""}
              </Text>
              <Text variant="bodyMedium" style={styles.description}>
                {offer.description}
              </Text>
            </View>
            <View style={[styles.section, styles.candidatesHeaderRow]}>
              <View>
                <Text variant="titleMedium" style={styles.subHeader}>
                  {t("offerManage.applicantsHeader")}
                </Text>
                <Text>
                  {applicants.length} / {(offer as any).maxApplications}
                </Text>
              </View>
              <View style={{ flexDirection: "row", gap: 8 }}>
                <TouchableOpacity
                  onPress={async () => {
                    if (!offerId) return;
                    const fetched = await getCandidates(offerId);
                    setApplicants(fetched);
                  }}
                  style={styles.refreshButton}
                >
                  <FontAwesome
                    name="refresh"
                    size={20}
                    color={colors.primary}
                  />
                </TouchableOpacity>
                <TouchableOpacity
                  onPress={() =>
                    offerId &&
                    navigation.navigate("ChosenApplicants", { offerId })
                  }
                  style={styles.refreshButton}
                >
                  <FontAwesome name="list" size={20} color={colors.primary} />
                </TouchableOpacity>
              </View>
            </View>
          </View>
        }
        contentContainerStyle={{ paddingBottom: 24, paddingHorizontal: 12 }}
      />
      <Button
        mode="contained"
        style={styles.confirmButton}
        disabled={chosenApplicants.length <= 0}
        onPress={() => handleAccept(offerId)}
      >
        Zatwierdz chec wspolpracy
      </Button>
    </SafeAreaView>
  );
};

export default OfferManageScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  headerRow: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 12,
    paddingTop: 8,
    marginBottom: 8,
  },
  backButton: {
    padding: 6,
    marginRight: 12,
  },
  headerText: {
    fontSize: 24,
    fontWeight: "700",
  },
  image: {
    width: "100%",
    borderRadius: 25,
  },
  section: {
    paddingHorizontal: 12,
    paddingVertical: 12,
  },
  title: {
    marginBottom: 4,
  },
  description: {
    marginTop: 8,
  },
  subHeader: {
    marginBottom: 6,
    fontWeight: "600",
  },
  candidatesHeaderRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },
  refreshButton: {
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 8,
  },
  list: {
    paddingBottom: 24,
    gap: 8,
  },
  applicantItem: {
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
  applicantContent: {
    flex: 1,
  },
  confirmButton: {
    alignSelf: "center",
    width: width * 0.9,
    marginBottom: height * 0.02,
  },
});
