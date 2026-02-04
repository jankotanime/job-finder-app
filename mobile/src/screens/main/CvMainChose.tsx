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
import useCvNames from "../../hooks/useCvNames";
import { useAuth } from "../../contexts/AuthContext";
import { tryCatch } from "../../utils/try-catch";
import AsyncStorage from "@react-native-async-storage/async-storage";

export type CvItem = {
  id: string;
  storageKey?: string;
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
  const { namesMap, reload: reloadNames } = useCvNames();
  const { userInfo } = useAuth();
  const userId = userInfo?.userId ? String(userInfo.userId) : null;
  const deletedKey = useMemo(
    () => (userId ? `deletedCvs:${userId}` : null),
    [userId],
  );
  const [deletedIds, setDeletedIds] = useState<string[]>([]);
  useEffect(() => {
    const loadDeleted = async () => {
      if (!deletedKey) return;
      const [raw] = await tryCatch(AsyncStorage.getItem(deletedKey));
      const arr: unknown = raw ? JSON.parse(raw) : null;
      if (Array.isArray(arr)) {
        setDeletedIds(arr.map((x) => String(x)));
      }
    };
    setDeletedIds([]);
    loadDeleted();
  }, [deletedKey]);
  const loadCvs = useCallback(async () => {
    const [resp, err] = await tryCatch(getCvsByUser());
    if (err || !resp) {
      console.warn("CvMainChose: failed to load cvs", err);
      setCvs([]);
      return;
    }
    const body = resp.body;
    const list: CvItem[] = body.data;
    const filtered = list.filter((x) => !deletedIds.includes(x.id));
    setCvs(filtered);
    if (deletedKey) {
      const backendIds = new Set(list.map((x) => x.id));
      const cleaned = deletedIds.filter((id) => backendIds.has(id));
      if (cleaned.length !== deletedIds.length) {
        setDeletedIds(cleaned);
        await tryCatch(
          AsyncStorage.setItem(deletedKey, JSON.stringify(cleaned)),
        );
      }
    }
  }, [deletedIds, deletedKey]);

  useEffect(() => {
    loadCvs();
    reloadNames();
  }, [loadCvs, reloadNames]);

  useFocusEffect(
    useCallback(() => {
      loadCvs();
      reloadNames();
      return () => {};
    }, [loadCvs, reloadNames]),
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
        cvUri: uri ?? t("cv.title"),
        manage: false,
      });
    },
    [navigation, t],
  );

  const onDelete = useCallback(
    async (item: CvItem) => {
      const [resp, err] = await tryCatch(deleteCv(item.id));
      if (err || !resp) {
        console.warn("CvMainChose: failed to delete cv", err);
        Alert.alert(t("cv.title"), t("cv.openError"));
        return;
      }
      if (resp?.response?.status !== 200) {
        Alert.alert(t("cv.title"), t("cv.openError"));
        return;
      }
      setCvs((prev) => prev.filter((x) => x.id !== item.id));
      if (deletedKey) {
        const next = Array.from(new Set([...deletedIds, item.id]));
        setDeletedIds(next);
        await tryCatch(AsyncStorage.setItem(deletedKey, JSON.stringify(next)));
      }
      if (selectedIds.includes(item.id)) unselectCv(item.id);
      await reload();
      await new Promise((r) => setTimeout(r, 150));
      await loadCvs();
    },
    [selectedIds, unselectCv, reload, loadCvs, t, deletedIds, deletedKey],
  );
  const renderItem = ({ item, index }: { item: CvItem; index: number }) => {
    const checked = selectedIds.includes(item.id);
    const title = namesMap[item.id] || `${t("cv.item")} ${index + 1}`;
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
            <Text variant="titleMedium" numberOfLines={1}>
              {title}
            </Text>
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
