import React, { useCallback, useMemo, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  Dimensions,
  TouchableOpacity,
  Image,
  RefreshControl,
} from "react-native";
import { useTheme, Text as PaperText } from "react-native-paper";
import { useOfferStorageContext } from "../../../contexts/OfferStorageContext";

const { width, height } = Dimensions.get("window");

const OfferGrid = () => {
  const { colors } = useTheme();
  const { storageOffers, refreshOffers } = useOfferStorageContext();
  const [refreshing, setRefreshing] = useState(false);

  const onRefresh = useCallback(async () => {
    try {
      setRefreshing(true);
      refreshOffers();
    } finally {
      setRefreshing(false);
    }
  }, [refreshOffers]);

  const numColumns = 2;
  const tileWidth = useMemo(() => width * 0.5 - 24, []);
  const keyFor = (item: any) => `${item?.title}|${item?.dateAndTime}`;

  const renderItem = ({ item }: any) => (
    <TouchableOpacity
      style={[
        styles.tile,
        { backgroundColor: colors.onBackground, width: tileWidth },
      ]}
    >
      {item?.offerPhoto ? (
        <Image source={{ uri: item.offerPhoto }} style={styles.image} />
      ) : (
        <View style={[styles.image, { backgroundColor: colors.background }]} />
      )}
      <View style={styles.tileContent}>
        <PaperText variant="titleMedium" numberOfLines={1}>
          {item?.title}
        </PaperText>
        {typeof item?.salary === "number" && (
          <PaperText style={{ opacity: 0.7 }} numberOfLines={1}>
            {item.salary} zł
          </PaperText>
        )}
      </View>
    </TouchableOpacity>
  );

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      <FlatList
        data={storageOffers}
        keyExtractor={keyFor}
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
    </View>
  );
};

export default OfferGrid;

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
});
