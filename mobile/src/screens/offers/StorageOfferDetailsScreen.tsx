import React, { useCallback, useMemo, useState } from "react";
import {
  Alert,
  Dimensions,
  Image,
  ScrollView,
  StyleSheet,
  View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Button, Text, useTheme } from "react-native-paper";
import {
  useFocusEffect,
  useNavigation,
  useRoute,
} from "@react-navigation/native";
import type { RouteProp } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import type { RootStackParamList } from "../../types/RootStackParamList";
import type { Offer } from "../../types/Offer";
import { buildPhotoUrl } from "../../utils/photoUrl";
import { useTranslation } from "react-i18next";
import { applyForOffer } from "../../api/offers/handleOffersApi";
import { useOfferStorageContext } from "../../contexts/OfferStorageContext";
import useSelectCv from "../../hooks/useSelectCv";

const { width } = Dimensions.get("window");

type Nav = NativeStackNavigationProp<RootStackParamList, "StorageOfferDetails">;

type R = RouteProp<RootStackParamList, "StorageOfferDetails">;

const getOfferPhotoUri = (offer?: Offer) => {
  const raw = (offer as any)?.offerPhoto ?? (offer as any)?.photo?.storageKey;
  return buildPhotoUrl(raw);
};

const StorageOfferDetailsScreen = () => {
  const navigation = useNavigation<Nav>();
  const route = useRoute<R>();
  const { colors } = useTheme();
  const { t } = useTranslation();

  const offer = route.params?.offer;
  const offerId = offer?.id ? String(offer.id) : null;

  const { removeStorageOffer, addAcceptedOffer, addDeclinedOffer } =
    useOfferStorageContext();

  const { selectedIds, reload } = useSelectCv();
  const cvId = selectedIds?.[0];

  const [busy, setBusy] = useState(false);

  useFocusEffect(
    useCallback(() => {
      reload();
      return () => {};
    }, [reload]),
  );

  const photoUri = useMemo(() => getOfferPhotoUri(offer), [offer]);
  const imageHeight = useMemo(() => Math.round(width * 0.55), []);

  const onDecline = useCallback(async () => {
    if (!offer) return;
    if (busy) return;
    setBusy(true);
    try {
      await Promise.all([removeStorageOffer(offer), addDeclinedOffer(offer)]);
      navigation.goBack();
    } catch (e) {
      console.error("decline saved offer failed", e);
      Alert.alert(
        t("storageOfferDetails.title"),
        t("storageOfferDetails.error"),
      );
    } finally {
      setBusy(false);
    }
  }, [offer, busy, removeStorageOffer, addDeclinedOffer, navigation, t]);

  const onAccept = useCallback(async () => {
    if (!offer || !offerId) return;
    if (busy) return;

    if (!cvId) {
      Alert.alert(
        t("storageOfferDetails.cvMissingTitle"),
        t("storageOfferDetails.cvMissingBody"),
        [
          { text: t("storageOfferDetails.cancel"), style: "cancel" },
          {
            text: t("storageOfferDetails.chooseCv"),
            onPress: () =>
              navigation.navigate("CvSelect", { disableSkip: true }),
          },
        ],
      );
      return;
    }

    setBusy(true);
    try {
      await applyForOffer(offerId, { cvId });
      await Promise.all([removeStorageOffer(offer), addAcceptedOffer(offer)]);
      navigation.goBack();
    } catch (e) {
      console.error("apply from saved offer failed", e);
      Alert.alert(
        t("storageOfferDetails.title"),
        t("storageOfferDetails.applyError"),
      );
    } finally {
      setBusy(false);
    }
  }, [
    offer,
    offerId,
    cvId,
    busy,
    navigation,
    t,
    removeStorageOffer,
    addAcceptedOffer,
  ]);

  if (!offer) {
    return (
      <SafeAreaView
        style={[styles.screen, { backgroundColor: colors.background }]}
      >
        <View style={styles.empty}>
          <Text variant="titleMedium">{t("storageOfferDetails.notFound")}</Text>
          <Button mode="contained" onPress={() => navigation.goBack()}>
            {t("storageOfferDetails.back")}
          </Button>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView
      style={[styles.screen, { backgroundColor: colors.background }]}
    >
      <View style={styles.content}>
        <ScrollView
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
        >
          {photoUri ? (
            <Image
              source={{ uri: photoUri }}
              style={[styles.image, { height: imageHeight }]}
            />
          ) : (
            <View
              style={[
                styles.image,
                { height: imageHeight, backgroundColor: colors.surfaceVariant },
              ]}
            />
          )}

          <View style={styles.section}>
            <Text variant="headlineSmall" style={styles.title}>
              {offer.title}
            </Text>
            {typeof offer.salary === "number" && (
              <Text variant="titleMedium" style={{ opacity: 0.8 }}>
                {offer.salary} zł
              </Text>
            )}
            <Text variant="bodyMedium" style={styles.description}>
              {offer.description}
            </Text>
          </View>
        </ScrollView>

        <View style={[styles.footer, { backgroundColor: colors.background }]}>
          <Button
            mode="contained"
            buttonColor={colors.error}
            textColor={colors.onError}
            style={styles.footerButton}
            disabled={busy}
            onPress={onDecline}
          >
            {t("storageOfferDetails.dontWant")}
          </Button>
          <Button
            mode="contained"
            buttonColor={colors.primary}
            textColor={colors.onPrimary}
            style={styles.footerButton}
            disabled={busy}
            onPress={onAccept}
          >
            {t("storageOfferDetails.want")}
          </Button>
        </View>
      </View>
    </SafeAreaView>
  );
};

export default StorageOfferDetailsScreen;

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    paddingHorizontal: 16,
    paddingTop: 16,
  },
  content: {
    flex: 1,
  },
  scrollContent: {
    paddingBottom: 120,
    gap: 12,
  },
  image: {
    width: "100%",
    borderRadius: 14,
  },
  section: {
    paddingTop: 12,
  },
  title: {
    fontWeight: "700",
  },
  description: {
    marginTop: 10,
    opacity: 0.9,
  },
  footer: {
    position: "absolute",
    left: 0,
    right: 0,
    bottom: 0,
    paddingHorizontal: 12,
    paddingTop: 10,
    paddingBottom: 18,
    flexDirection: "row",
    gap: 12,
  },
  footerButton: {
    flex: 1,
  },
  empty: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: 16,
    gap: 12,
  },
});
