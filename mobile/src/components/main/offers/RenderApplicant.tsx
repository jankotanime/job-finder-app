import React from "react";
import { View, TouchableOpacity, StyleSheet } from "react-native";
import { Text, useTheme } from "react-native-paper";
import { Ionicons } from "@expo/vector-icons";
import { useTranslation } from "react-i18next";

type ApplicationItem = {
  id: string;
  status?: string;
  candidate?: {
    id?: string;
    name?: string;
    firstName?: string;
    lastName?: string;
    username?: string;
  };
  chosenCv?: {
    id?: string;
    storageKey?: string;
  };
};

type Props = {
  item: ApplicationItem;
  onPress: (item: ApplicationItem) => void;
  setChosenApplicants?: React.Dispatch<React.SetStateAction<ApplicationItem[]>>;
  selected?: boolean;
};

const RenderApplicant = ({ item, onPress, selected }: Props) => {
  const { colors } = useTheme();
  const { t } = useTranslation();
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
      onPress={() => onPress(item)}
      style={[
        styles.applicantItem,
        { backgroundColor: selected ? "#8cf38cff" : colors.surface },
      ]}
    >
      <View style={[styles.avatar, { backgroundColor: colors.background }]} />
      <View style={styles.applicantContent}>
        <Text variant="titleMedium" numberOfLines={1}>
          {name}
        </Text>
        <Text variant="labelSmall" style={{ opacity: 0.7 }}>
          {t("offer.status")}: {status}
        </Text>
      </View>
      <Ionicons name="chevron-forward" size={20} color={colors.primary} />
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
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

export default RenderApplicant;
