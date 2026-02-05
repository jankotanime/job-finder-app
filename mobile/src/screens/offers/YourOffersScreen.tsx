import React, { useCallback, useMemo, useState } from "react";
import {
  View,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Image,
  Dimensions,
  RefreshControl,
} from "react-native";
import { useNavigation } from "@react-navigation/native";
import { useTheme, Text } from "react-native-paper";
import { useOfferStorageContext } from "../../contexts/OfferStorageContext";
import { useAuth } from "../../contexts/AuthContext";
import { Offer } from "../../types/Offer";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useTranslation } from "react-i18next";
import { buildPhotoUrl } from "../../utils/photoUrl";

const { width } = Dimensions.get("window");

const YourOfferScreen = () => {
  const navigation = useNavigation<any>();
  const { colors } = useTheme();
  const { savedOffers, refreshOffers } = useOfferStorageContext();
  const { userInfo } = useAuth();
  const { t } = useTranslation();
  const [refreshing, setRefreshing] = useState(false);
  const numColumns = 2;
  const tileWidth = useMemo(() => width * 0.5 - 24, []);

  const keyFor = (o: Offer) => `${o.title}|${o.dateAndTime}`;

  const renderItem = ({ item }: { item: Offer }) => {
    const photoUri = buildPhotoUrl(
      (item as any)?.offerPhoto ?? (item as any)?.photo?.storageKey,
    );
    return (
      <TouchableOpacity
        style={[
          styles.tile,
          { backgroundColor: colors.onBackground, width: tileWidth },
        ]}
        onPress={() => navigation.navigate("OfferManage", { offer: item })}
      >
        {photoUri ? (
          <Image source={{ uri: photoUri }} style={styles.image} />
        ) : (
          <View
            style={[styles.image, { backgroundColor: colors.onBackground }]}
          />
        )}
        <View style={styles.tileContent}>
          <Text variant="titleMedium" numberOfLines={1}>
            {item.title}
          </Text>
          {typeof item.salary === "number" && (
            <Text
              variant="labelMedium"
              style={{ opacity: 0.7 }}
              numberOfLines={1}
            >
              {item.salary} zł
            </Text>
          )}
        </View>
      </TouchableOpacity>
    );
  };
  const myOffers = useMemo(() => {
    const uid = userInfo?.userId ? String(userInfo.userId) : undefined;
    if (!uid) return savedOffers || [];
    return (savedOffers || []).filter((it: any) => String(it?.owner) === uid);
  }, [savedOffers, userInfo?.userId]);

  const onRefresh = useCallback(async () => {
    try {
      setRefreshing(true);
      refreshOffers();
    } finally {
      setRefreshing(false);
    }
  }, [refreshOffers]);
  console.log(myOffers);
  return (
    <SafeAreaView
      style={[styles.container, { backgroundColor: colors.background }]}
    >
      <View style={styles.header}>
        <TouchableOpacity
          onPress={() => navigation.goBack()}
          style={styles.backButton}
        >
          <Ionicons name="chevron-back" size={25} color={colors.primary} />
        </TouchableOpacity>
        <Text style={[styles.header, { color: colors.primary }]}>
          {t("menu.offers")}
        </Text>
      </View>
      <FlatList
        data={myOffers}
        keyExtractor={(item) => keyFor(item)}
        renderItem={renderItem}
        numColumns={numColumns}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            tintColor={colors.primary}
            colors={[colors.primary]}
          />
        }
        columnWrapperStyle={{
          justifyContent: "space-between",
          paddingHorizontal: 12,
        }}
        contentContainerStyle={styles.grid}
      />
    </SafeAreaView>
  );
};

export default YourOfferScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: 12,
  },
  grid: {
    paddingBottom: 24,
    gap: 12,
  },
  tile: {
    borderRadius: 12,
    overflow: "hidden",
    marginBottom: 12,
  },
  image: {
    width: "100%",
    height: 120,
  },
  tileContent: {
    padding: 10,
  },
  header: {
    display: "flex",
    flexDirection: "row",
    fontSize: 28,
    fontWeight: "bold",
    marginBottom: 15,
  },
  backButton: {
    padding: 5,
    marginRight: 15,
  },
});
