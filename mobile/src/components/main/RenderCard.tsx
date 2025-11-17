import React from "react";
import { Job } from "../../types/Job";
import Card from "./Card";
import { View, Text, StyleSheet, Image, Dimensions } from "react-native";
import { useTheme } from "react-native-paper";
import { Icon } from "react-native-paper";
import { useTranslation } from "react-i18next";
import { palette } from "../../constans/tagPalette";

const { width, height } = Dimensions.get("window");
const JobCard = (item: Job) => {
  const { colors } = useTheme();
  const metaColor = colors.onSurfaceVariant || "gray";
  const { t } = useTranslation();

  return (
    <Card key={item.id}>
      <View style={styles.container}>
        {item.logoUrl ? (
          <View style={[styles.logoWrapper, { borderColor: colors.primary }]}>
            <Image
              source={{ uri: item.logoUrl }}
              style={styles.logo}
              resizeMode="contain"
            />
          </View>
        ) : (
          <View
            style={[
              styles.logoWrapper,
              [styles.placeholderLogo, { borderColor: colors.onSurface }],
              { backgroundColor: colors.surface },
            ]}
          >
            <Icon source="camera" size={50} />
          </View>
        )}
        {item.owner && (
          <Text style={[styles.owner, { color: colors.onSurface }]}>
            {item.owner}
          </Text>
        )}
        {item.location && (
          <Text style={[styles.meta, { color: metaColor }]}>
            {item.location}
          </Text>
        )}
        {item.title && (
          <Text
            style={[styles.title, { color: colors.onSurface }]}
            numberOfLines={1}
            ellipsizeMode="tail"
          >
            {item.title}
          </Text>
        )}
        {item.description && (
          <View style={styles.salaryContainer}>
            <Text style={[styles.salary, { color: metaColor }]}>
              {item.description}
            </Text>
          </View>
        )}
        {item.salary && (
          <View style={styles.salaryContainer}>
            <Text style={[styles.salary, { color: metaColor }]}>
              {item.salary} zł
            </Text>
          </View>
        )}
        <Text style={[styles.detailsPrompt, { color: metaColor }]}>
          {t("main.card.details_check")}
        </Text>
        {item.tags && item.tags.length > 0 && (
          <View style={styles.tagsWrap}>
            {(item.tags || []).map((tag, idx) => {
              const bg = palette[idx % palette.length];
              return (
                <View
                  key={tag.id ?? idx}
                  style={[styles.tag, { backgroundColor: bg }]}
                >
                  <Text style={styles.tagText}>{tag.name}</Text>
                </View>
              );
            })}
          </View>
        )}
      </View>
    </Card>
  );
};

export default JobCard;

const styles = StyleSheet.create({
  container: {
    width: "100%",
    alignItems: "center",
    paddingHorizontal: 0,
    paddingTop: 0,
    flex: 1,
  },
  logoWrapper: {
    width: 180,
    height: 180,
    borderRadius: 12,
    borderWidth: 1,
    overflow: "hidden",
    marginBottom: 15,
    marginTop: 10,
    alignItems: "center",
    justifyContent: "center",
  },
  placeholderLogo: {
    borderStyle: "dashed",
    opacity: 0.6,
  },
  logo: {
    width: width,
    height: height,
  },
  owner: {
    fontSize: 18,
    fontWeight: "700",
    marginBottom: 4,
    textAlign: "center",
  },
  meta: {
    fontSize: 14,
    marginBottom: 15,
    textAlign: "center",
    maxWidth: "80%",
  },
  title: {
    fontSize: 22,
    fontWeight: "900",
    marginTop: 10,
    marginBottom: 10,
    textAlign: "center",
    maxWidth: "90%",
  },
  salaryContainer: {
    flexDirection: "row",
    alignItems: "center",
    marginTop: 18,
  },
  salaryIcon: {
    marginRight: 5,
  },
  salary: {
    fontSize: 16,
    fontWeight: "600",
    marginLeft: 5,
  },
  detailsPrompt: {
    position: "absolute",
    bottom: 0,
    left: 0,
    fontSize: 12,
    fontWeight: "500",
    opacity: 0.7,
  },
  tagsWrap: {
    position: "absolute",
    bottom: 40,
    left: 12,
    right: 12,
    flexDirection: "row",
    flexWrap: "wrap",
    justifyContent: "center",
    alignItems: "center",
  },
  tag: {
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 16,
    margin: 4,
  },
  tagText: {
    fontSize: 12,
    fontWeight: "600",
    color: "#0b1220",
  },
});
