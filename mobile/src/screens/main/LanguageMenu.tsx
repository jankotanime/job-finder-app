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

const LanguageMenu = () => {
  const { colors } = useTheme();
  const navigation = useNavigation<any>();
  const [currentLang, setCurrentLang] = useState(i18n.language);

  const languages = [
    { code: "en", name: "English", countryCode: "us" },
    { code: "pl", name: "Polski", countryCode: "pl" },
  ];

  const handleLanguageChange = async (lng: string) => {
    await i18n.changeLanguage(lng);
    setCurrentLang(lng);
  };

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      <View style={styles.header}>
        <TouchableOpacity
          onPress={() => navigation.goBack()}
          style={styles.backButton}
        >
          <Ionicons name="chevron-back" size={25} color={colors.primary} />
        </TouchableOpacity>
      </View>
      <ScrollView style={styles.content}>
        {languages.map((language) => (
          <TouchableOpacity
            key={language.code}
            style={[
              styles.languageItem,
              { backgroundColor: colors.surface },
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
    </View>
  );
};
export default LanguageMenu;

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  header: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 20,
    paddingTop: 50,
    paddingBottom: 20,
  },
  backButton: {
    padding: 5,
    marginRight: 15,
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
