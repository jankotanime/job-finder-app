import React from "react";
import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { useTheme } from "react-native-paper";
import JobGrid from "../../components/main/JobGrid";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import { useTranslation } from "react-i18next";

const StorageScreen = () => {
  const { colors } = useTheme();
  const navigation = useNavigation();
  const { t } = useTranslation();

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
          {t("menu.saved_jobs")}
        </Text>
      </View>
      <JobGrid />
    </SafeAreaView>
  );
};

export default StorageScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
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
