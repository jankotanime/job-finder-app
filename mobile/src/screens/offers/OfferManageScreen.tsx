import React, { useMemo, useEffect } from "react";
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
import { Text, useTheme } from "react-native-paper";
import { Ionicons } from "@expo/vector-icons";
import { FontAwesome } from "@expo/vector-icons";
import { useNavigation, useRoute } from "@react-navigation/native";
import type { RouteProp } from "@react-navigation/native";
import type { RootStackParamList } from "../../types/RootStackParamList";
import { buildPhotoUrl } from "../../utils/photoUrl";
import { getOfferById } from "../../api/offers/handleOffersApi";

const { width } = Dimensions.get("window");

type Applicant = {
  id: string;
  name: string;
  appliedAt: string;
};

const mockApplicants: Applicant[] = [
  { id: "1", name: "Jan Kowalski", appliedAt: "2025-12-20" },
  { id: "2", name: "Anna Nowak", appliedAt: "2025-12-21" },
  { id: "3", name: "Piotr Zieliński", appliedAt: "2025-12-22" },
  { id: "4", name: "Kasia Wiśniewska", appliedAt: "2025-12-22" },
  { id: "5", name: "Michał Krawczyk", appliedAt: "2025-12-23" },
  { id: "6", name: "Ola Dąbrowska", appliedAt: "2025-12-24" },
];

const getCandidates = async (id: string) => {
  const response = await getOfferById(id);
  console.log(response.body.data);
};

const OfferManageScreen = () => {
  const navigation = useNavigation<any>();
  const { colors } = useTheme();
  const imageHeight = useMemo(() => Math.round(width * 0.55), []);
  const route = useRoute<RouteProp<RootStackParamList, "OfferManage">>();
  const offer = route.params?.offer;
  console.log("offer: ", offer);
  const photoUri = buildPhotoUrl(offer?.photo?.storageKey ?? undefined);
  const offerId = offer?.id as string | undefined;
  const onPressApplicant = (a: Applicant) => {
    Alert.alert("Kandydat", `${a.name} — kliknięty`);
  };
  const renderApplicant = ({ item }: { item: Applicant }) => (
    <TouchableOpacity
      onPress={() => onPressApplicant(item)}
      style={[styles.applicantItem, { backgroundColor: colors.surface }]}
    >
      <View style={[styles.avatar, { backgroundColor: colors.background }]} />
      <View style={styles.applicantContent}>
        <Text variant="titleMedium" numberOfLines={1}>
          {item.name}
        </Text>
        <Text variant="labelSmall" style={{ opacity: 0.7 }}>
          Zgłoszono: {item.appliedAt}
        </Text>
      </View>
      <Ionicons name="chevron-forward" size={20} color={colors.primary} />
    </TouchableOpacity>
  );

  return (
    <SafeAreaView
      style={[styles.container, { backgroundColor: colors.background }]}
    >
      <FlatList
        data={mockApplicants}
        keyExtractor={(item) => item.id}
        renderItem={renderApplicant}
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
              <Text variant="titleMedium" style={styles.subHeader}>
                Zgłoszeni kandydaci
              </Text>
              <TouchableOpacity
                onPress={() => offerId && getCandidates(offerId)}
                style={styles.refreshButton}
              >
                <FontAwesome name="refresh" size={20} color={colors.primary} />
              </TouchableOpacity>
            </View>
          </View>
        }
        contentContainerStyle={{ paddingBottom: 24, paddingHorizontal: 12 }}
      />
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
});
