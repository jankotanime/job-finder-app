import React, { useCallback } from "react";
import { View, StyleSheet, Dimensions } from "react-native";
import { Text, useTheme } from "react-native-paper";
import { useTranslation } from "react-i18next";
import { useFocusEffect } from "@react-navigation/native";
import useSelectCv from "../../hooks/useSelectCv";
import useCvNames from "../../hooks/useCvNames";

const { width, height } = Dimensions.get("window");
const CvInfo = () => {
  const { t } = useTranslation();
  const { colors } = useTheme();
  const { selectedIds, reload } = useSelectCv();
  const { namesMap, reload: reloadNames } = useCvNames();

  useFocusEffect(
    useCallback(() => {
      reload();
      reloadNames();
      return () => {};
    }, [reload, reloadNames]),
  );

  const formatName = (value: string, max: number = 10) => {
    const v = String(value || "").trim();
    if (v.length > max) return v.slice(0, max) + "...";
    return v;
  };

  if (!selectedIds || selectedIds.length === 0) return null;
  const rawName = namesMap[selectedIds[0]] || t("cv.title");
  const name = formatName(rawName, 10);
  return (
    <View style={[styles.container, { backgroundColor: colors.onBackground }]}>
      <Text
        variant="labelLarge"
        style={{ color: colors.primary, fontWeight: 600 }}
      >
        {t("cv.applyWith")} {name}
      </Text>
    </View>
  );
};

export default CvInfo;

const styles = StyleSheet.create({
  container: {
    position: "absolute",
    top: height * 0.08,
    flexDirection: "row",
    justifyContent: "space-around",
    alignItems: "center",
    alignSelf: "center",
    width: width * 0.5,
    height: height * 0.04,
    borderRadius: 20,
  },
});
