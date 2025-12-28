import React, { useRef, useEffect, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  Image,
  Dimensions,
  Animated,
} from "react-native";
import { useTheme, Icon } from "react-native-paper";
import { Offer } from "../../types/Offer";
import { getTagColor } from "../../constans/tagColorsDictionairy";
import { createAnimation } from "../../utils/animationHelper";

const { width, height } = Dimensions.get("window");

const CardContent = ({
  item,
  isActive = false,
  onFadeOutComplete,
  finalizeHide = false,
  preview,
}: {
  item: Offer;
  isActive?: boolean;
  onFadeOutComplete?: () => void;
  finalizeHide?: boolean;
  preview?: boolean;
}) => {
  const { colors } = useTheme();
  const fadeAnim = useRef(new Animated.Value(0)).current;
  const [shouldRenderDescription, setShouldRenderDescription] =
    useState(isActive);

  useEffect(() => {
    if (isActive) {
      setShouldRenderDescription(true);
      createAnimation(fadeAnim, 1, 250).start();
    } else {
      createAnimation(fadeAnim, 0, 250).start(() => {
        onFadeOutComplete && onFadeOutComplete();
      });
    }
  }, [isActive]);

  useEffect(() => {
    if (finalizeHide) {
      setShouldRenderDescription(false);
    }
  }, [finalizeHide]);

  return (
    <View style={styles.container}>
      {item.offerPhoto ? (
        <View style={[styles.logoWrapper, { borderColor: colors.primary }]}>
          <Image
            source={{ uri: String(item.offerPhoto) }}
            style={styles.logo}
            resizeMode="contain"
          />
        </View>
      ) : (
        <View
          style={[
            styles.logoWrapper,
            styles.placeholderLogo,
            { borderColor: colors.onSurface, backgroundColor: colors.surface },
          ]}
        >
          <Icon source="camera" size={50} />
        </View>
      )}
      {/* {item.location && (
        <Text style={[styles.meta, { color: colors.onSurface }]}>
          {item.location}
        </Text>
      )} */}
      {item.title && (
        <Text style={[styles.title, { color: colors.onSurface }]}>
          {item.title}
        </Text>
      )}
      {Number.isFinite(Number(item.salary)) && Number(item.salary) > 0 && (
        <View style={styles.salaryContainer}>
          <Text style={[styles.salary, { color: colors.onSurface }]}>
            {String(item.salary)} zł
          </Text>
        </View>
      )}
      {item.tags && item.tags.length > 0 && (
        <View style={styles.tagsWrap}>
          {item.tags.map((tag, idx) => {
            const bg = getTagColor((tag as any).categoryColor);
            return (
              <View
                key={tag.id ?? `${tag.id}-${idx}`}
                style={[styles.tag, { backgroundColor: bg }]}
              >
                <Text style={styles.tagText}>{tag.name}</Text>
              </View>
            );
          })}
        </View>
      )}
      {item.description && shouldRenderDescription && (
        <Animated.View
          style={[
            styles.descriptionContainer,
            {
              opacity: fadeAnim,
              maxHeight: fadeAnim.interpolate({
                inputRange: [0, 1],
                outputRange: [0, 500],
              }),
              overflow: "hidden",
              ...(preview ? { marginLeft: 20 } : {}),
            },
          ]}
        >
          <Text style={[styles.salary, { color: colors.onSurface }]}>
            {item.description}
          </Text>
        </Animated.View>
      )}
    </View>
  );
};

export default CardContent;

const styles = StyleSheet.create({
  container: {
    width: "100%",
    paddingHorizontal: 0,
    alignItems: "center",
    paddingTop: 0,
    flex: 1,
  },
  scrollContent: {
    alignItems: "center",
    paddingBottom: 120,
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
  tagsWrap: {
    position: "relative",
    width: width * 0.8,
    bottom: -20,
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
    color: "#FFFFFF",
  },
  descriptionContainer: {
    flexDirection: "row",
    alignItems: "center",
    marginTop: 58,
    width: width * 0.8,
  },
});
