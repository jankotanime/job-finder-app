import React from "react";
import { Dimensions, ScrollView, StyleSheet, View } from "react-native";
import { useTranslation } from "react-i18next";
import { useNavigation } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { Button, Card, Text, useTheme } from "react-native-paper";
import { MaterialCommunityIcons } from "@expo/vector-icons";
import type { RootStackParamList } from "../../types/RootStackParamList";

const { height, width } = Dimensions.get("window");

type Nav = NativeStackNavigationProp<RootStackParamList, "ChooseJobScreen">;

const ChooseJobScreen = () => {
  const { colors } = useTheme();
  const { t } = useTranslation();
  const navigation = useNavigation<Nav>();

  return (
    <View style={[styles.screen, { backgroundColor: colors.background }]}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.header}>
          <Text variant="headlineMedium" style={styles.title}>
            {t("jobs.chooseRole.title")}
          </Text>
          <Text
            variant="bodyMedium"
            style={[styles.subtitle, { color: colors.onSurfaceVariant }]}
          >
            {t("jobs.chooseRole.subtitle")}
          </Text>
        </View>

        <Card
          style={[styles.card, { backgroundColor: colors.onBackground }]}
          onPress={() => navigation.navigate("JobsContractor")}
        >
          <Card.Content style={styles.cardContent}>
            <View
              style={[styles.iconWrap, { backgroundColor: colors.background }]}
            >
              <MaterialCommunityIcons
                name="account-wrench"
                size={34}
                color={colors.primary}
              />
            </View>
            <View style={styles.cardText}>
              <Text variant="titleMedium">
                {t("jobs.chooseRole.contractor.title")}
              </Text>
              <Text
                variant="bodySmall"
                style={{ color: colors.onSurfaceVariant }}
              >
                {t("jobs.chooseRole.contractor.description")}
              </Text>
            </View>
          </Card.Content>
          <Card.Actions>
            <Button
              onPress={() => navigation.navigate("JobsContractor")}
              mode="contained"
            >
              {t("jobs.chooseRole.cta")}
            </Button>
          </Card.Actions>
        </Card>

        <Card
          style={[styles.card, { backgroundColor: colors.onBackground }]}
          onPress={() => navigation.navigate("JobsOwner")}
        >
          <Card.Content style={styles.cardContent}>
            <View
              style={[styles.iconWrap, { backgroundColor: colors.background }]}
            >
              <MaterialCommunityIcons
                name="briefcase-account"
                size={34}
                color={colors.primary}
              />
            </View>
            <View style={styles.cardText}>
              <Text variant="titleMedium">
                {t("jobs.chooseRole.owner.title")}
              </Text>
              <Text
                variant="bodySmall"
                style={{ color: colors.onSurfaceVariant }}
              >
                {t("jobs.chooseRole.owner.description")}
              </Text>
            </View>
          </Card.Content>
          <Card.Actions>
            <Button
              onPress={() => navigation.navigate("JobsOwner")}
              mode="contained"
            >
              {t("jobs.chooseRole.cta")}
            </Button>
          </Card.Actions>
        </Card>
      </ScrollView>
    </View>
  );
};

export default ChooseJobScreen;

const styles = StyleSheet.create({
  screen: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
    justifyContent: "center",
    paddingHorizontal: width * 0.06,
    paddingVertical: height * 0.04,
    gap: 14,
  },
  header: {
    marginBottom: 6,
  },
  title: {
    fontWeight: "700",
  },
  subtitle: {
    marginTop: 6,
  },
  card: {
    borderRadius: 16,
  },
  cardContent: {
    flexDirection: "row",
    gap: 12,
    alignItems: "center",
  },
  iconWrap: {
    width: 54,
    height: 54,
    borderRadius: 14,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "rgba(0,0,0,0.04)",
  },
  cardText: {
    flex: 1,
    gap: 4,
  },
});
