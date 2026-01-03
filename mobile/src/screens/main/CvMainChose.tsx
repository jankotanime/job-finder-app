import React, { useCallback, useEffect, useMemo, useState } from "react";
import {
  StyleSheet,
  View,
  FlatList,
  TouchableOpacity,
  Alert,
  Dimensions,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import {
  Text,
  useTheme,
  Button,
  Card,
  Checkbox,
  IconButton,
} from "react-native-paper";
import { useTranslation } from "react-i18next";
import { useNavigation, useFocusEffect } from "@react-navigation/native";
import { getCvsByUser, deleteCv } from "../../api/cv/handleCvApi";
import { buildPhotoUrl } from "../../utils/photoUrl";
import useSelectCv from "../../hooks/useSelectCv";
import { useAuth } from "../../contexts/AuthContext";

export type CvItem = {
  id: string;
  storageKey?: string;
  name?: string;
};

const { width, height } = Dimensions.get("window");

const CvMainChose = () => {
  const { colors } = useTheme();
  const { t } = useTranslation();
  const navigation = useNavigation<any>();
  const [cvs, setCvs] = useState<CvItem[]>([]);
  const maxSelectable = 1;
  const { selectedIds, selectCv, unselectCv, reload } = useSelectCv({
    limit: 1,
  });
  const { userInfo } = useAuth();
  const isPremium = Boolean(
    (userInfo as any)?.isPremium ?? (userInfo as any)?.premium ?? false,
  );

  const loadCvs = useCallback(async () => {
    try {
      const { body } = await getCvsByUser();
      const list: CvItem[] = Array.isArray(body?.data)
        ? (body?.data as any[]).map((it) => ({
            id: String(it?.id),
            storageKey: it?.storageKey,
            name: it?.name,
          }))
        : Array.isArray(body)
          ? (body as any[]).map((it) => ({
              id: String((it as any)?.id),
              storageKey: (it as any)?.storageKey,
              name: (it as any)?.name,
            }))
          : [];
      setCvs(list);
    } catch (e) {
      console.warn("CvMainChose: failed to load cvs", e);
    }
  }, []);

  useEffect(() => {
    loadCvs();
  }, [loadCvs]);

  useFocusEffect(
    useCallback(() => {
      loadCvs();
      return () => {};
    }, [loadCvs]),
  );

  const toggleSelect = useCallback(
    (id: string) => {
      const exists = selectedIds.includes(id);
      if (exists) unselectCv(id);
      else selectCv(id);
    },
    [selectedIds, selectCv, unselectCv],
  );
  const onConfirm = useCallback(() => {
    navigation.goBack();
  }, [navigation, t]);

  const onPreview = useCallback(
    (item: CvItem) => {
      const uri = item.storageKey ? buildPhotoUrl(item.storageKey) : undefined;
      if (!uri) {
        Alert.alert(t("cv.title"), t("cv.openError"));
        return;
      }
      navigation.navigate("CvPreview", {
        cvUri: uri,
        cvName: item.name ?? t("cv.title"),
        manage: false,
      });
    },
    [navigation, t],
  );

  const onDelete = useCallback(
    async (item: CvItem) => {
      try {
        const resp = await deleteCv(item.id);
        const httpStatus = resp?.response?.status;
        const ok =
          typeof httpStatus === "number"
            ? httpStatus >= 200 && httpStatus < 300
            : true;
        if (!ok) {
          Alert.alert(t("cv.title"), "Nie udało się usunąć CV");
          return;
        }
        if (selectedIds.includes(item.id)) unselectCv(item.id);
        await reload();
        await loadCvs();
      } catch (e) {
        console.warn("CvMainChose: failed to delete cv", e);
      }
    },
    [selectedIds, unselectCv, reload, loadCvs, t],
  );

  const renderItem = ({ item, index }: { item: CvItem; index: number }) => {
    const checked = selectedIds.includes(item.id);
    return (
      <Card style={[styles.card, { backgroundColor: colors.surface }]}>
        <TouchableOpacity
          style={[
            styles.cardRow,
            checked
              ? { backgroundColor: "#8cf38cff" }
              : { backgroundColor: colors.onBackground },
          ]}
          onPress={() => toggleSelect(item.id)}
        >
          <View style={{ flex: 1, marginLeft: 10 }}>
            <Text
              variant="titleMedium"
              numberOfLines={1}
            >{`${t("cv.item")} ${index + 1}`}</Text>
          </View>
          <IconButton
            icon="eye"
            selected={false}
            onPress={() => onPreview(item)}
          />
          <IconButton
            icon="delete"
            selected={false}
            onPress={() => onDelete(item)}
          />
        </TouchableOpacity>
      </Card>
    );
  };

  return (
    <SafeAreaView
      style={[styles.container, { backgroundColor: colors.background }]}
    >
      <View
        style={[
          styles.headerRow,
          {
            flexDirection: "row",
            alignItems: "center",
            justifyContent: "space-between",
          },
        ]}
      >
        <View>
          <Text
            variant="titleLarge"
            style={{ color: colors.primary, fontWeight: 600 }}
          >
            {t("cv.chooseHeader")}
          </Text>
          <Text
            variant="labelMedium"
            style={{ opacity: 0.7, color: colors.primary, fontWeight: 600 }}
          >
            {t("cv.selectedCount", {
              count: selectedIds.length,
              max: maxSelectable,
            })}
          </Text>
        </View>
        <Button
          mode="outlined"
          onPress={() => navigation.navigate("CvSelect", { disableSkip: true })}
        >
          {t("cv.add")}
        </Button>
      </View>
      <FlatList
        data={cvs}
        keyExtractor={(item) => item.id}
        renderItem={renderItem}
        style={{ flex: 1 }}
        contentContainerStyle={{ paddingHorizontal: 12, paddingBottom: 120 }}
        ListEmptyComponent={
          <View style={{ padding: 16 }}>
            <Text style={{ opacity: 0.7 }}>{t("cv.emptyList")}</Text>
          </View>
        }
      />
      <View style={styles.footerFixed}>
        <Button
          mode="contained"
          disabled={selectedIds.length === 0}
          onPress={onConfirm}
          style={{ flex: 1 }}
        >
          {t("cv.confirmSelection")}
        </Button>
      </View>
    </SafeAreaView>
  );
};

export default CvMainChose;

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  headerRow: {
    paddingHorizontal: 12,
    paddingVertical: 12,
  },
  footerFixed: {
    position: "absolute",
    alignSelf: "center",
    width: width * 0.9,
    bottom: height * 0.05,
  },
  card: {
    marginHorizontal: 12,
    marginBottom: 8,
  },
  cardRow: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 8,
    paddingVertical: 8,
    borderRadius: 10,
  },
});
