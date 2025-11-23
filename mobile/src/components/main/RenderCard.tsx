import React from "react";
import { Job } from "../../types/Job";
import Card from "./Card";
import {
  StyleSheet,
  Animated,
  ScrollView,
  Text,
  Dimensions,
} from "react-native";
import CardContent from "./CardContent";
import { useTheme } from "react-native-paper";
import { useTranslation } from "react-i18next";

interface JobCardProps {
  item: Job;
  expandAnim: Animated.Value;
  isActive: boolean;
  onDescriptionHidden?: () => void;
  finalizeHide?: boolean;
}
const JobCard = ({
  item,
  expandAnim,
  isActive,
  onDescriptionHidden,
  finalizeHide,
}: JobCardProps) => {
  const { colors } = useTheme();
  const { t } = useTranslation();
  return (
    <Card key={item.id} expandAnim={expandAnim}>
      <ScrollView
        style={styles.container}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        scrollEnabled={isActive}
      >
        <CardContent
          item={item}
          isActive={isActive}
          onFadeOutComplete={onDescriptionHidden}
          finalizeHide={finalizeHide}
        />
      </ScrollView>
      {!isActive && (
        <Text style={[styles.detailsPrompt, { color: colors.onSurface }]}>
          {t("main.card.details_check")}
        </Text>
      )}
    </Card>
  );
};

export default JobCard;

const styles = StyleSheet.create({
  container: {
    width: "100%",
    paddingHorizontal: 0,
    paddingTop: 0,
    flex: 1,
  },
  scrollContent: {
    alignItems: "center",
    paddingBottom: 120,
  },
  detailsPrompt: {
    position: "absolute",
    bottom: 20,
    left: 20,
    fontSize: 12,
    fontWeight: "500",
    opacity: 0.7,
  },
});
