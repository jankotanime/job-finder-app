import React, { useState } from "react";
import {
  View,
  StyleSheet,
  TouchableOpacity,
  Text,
  ScrollView,
} from "react-native";
import { useTheme } from "react-native-paper";
import { Ionicons } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import i18n from "../../locales/i18n";
import CountryFlag from "react-native-country-flag";
import { SafeAreaView } from "react-native-safe-area-context";
import { useTranslation } from "react-i18next";
import { languages } from "../../constans/languages";

const LanguageMenu = () => {
  const { colors } = useTheme();
  const navigation = useNavigation<any>();
  const [currentLang, setCurrentLang] = useState(i18n.language);
  const { t } = useTranslation();

  const handleLanguageChange = async (lng: string) => {
    await i18n.changeLanguage(lng);
    setCurrentLang(lng);
  };

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
          {t("menu.language")}
        </Text>
      </View>
      <ScrollView style={styles.content}>
        {languages.map((language) => (
          <TouchableOpacity
            key={language.code}
            style={[
              styles.languageItem,
              { backgroundColor: colors.onBackground },
              currentLang === language.code && {
                borderColor: colors.primary,
                borderWidth: 2,
              },
            ]}
            onPress={() => handleLanguageChange(language.code)}
          >
            <View style={styles.languageInfo}>
              <CountryFlag
                isoCode={language.countryCode}
                size={20}
                style={styles.flagIcon}
              />
              <Text style={[styles.languageName, { color: colors.onSurface }]}>
                {language.name}
              </Text>
            </View>
            {currentLang === language.code && (
              <Ionicons name="checkmark" size={24} color={colors.primary} />
            )}
          </TouchableOpacity>
        ))}
      </ScrollView>
    </SafeAreaView>
  );
};
export default LanguageMenu;

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
    paddingHorizontal: 20,
  },
  headerTitle: {
    fontSize: 24,
    fontWeight: "bold",
  },
  content: {
    flex: 1,
    paddingHorizontal: 20,
  },
  languageItem: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    padding: 16,
    marginBottom: 12,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "transparent",
  },
  languageInfo: {
    flexDirection: "row",
    alignItems: "center",
  },
  flagIcon: {
    marginRight: 12,
  },
  languageName: {
    fontSize: 18,
    fontWeight: "500",
  },
});
