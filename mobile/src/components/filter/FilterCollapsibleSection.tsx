import React, { useState, useRef } from "react";
import { StyleSheet, View, Animated, TouchableOpacity } from "react-native";
import { Text, useTheme } from "react-native-paper";
import { MaterialIcons } from "@expo/vector-icons";

const CollapsibleSection = ({
  section,
  selectedTags,
  toggleTag,
  theme,
}: any) => {
  const [expanded, setExpanded] = useState(false);
  const animationValue = useRef(new Animated.Value(0)).current;
  const { colors } = useTheme();
  const borderRadiusAnim = useRef(new Animated.Value(20)).current;

  const toggleSection = () => {
    setExpanded(!expanded);
    Animated.timing(animationValue, {
      toValue: expanded ? 0 : 1,
      duration: 350,
      useNativeDriver: false,
    }).start();
    if (!expanded) {
      Animated.timing(borderRadiusAnim, {
        toValue: 0,
        duration: 0,
        useNativeDriver: false,
      }).start();
    } else {
      setTimeout(() => {
        Animated.timing(borderRadiusAnim, {
          toValue: 20,
          duration: 250,
          useNativeDriver: false,
        }).start();
      }, 300);
    }
  };

  const arrowRotation = animationValue.interpolate({
    inputRange: [0, 1],
    outputRange: ["0deg", "90deg"],
  });

  const contentHeight = animationValue.interpolate({
    inputRange: [0, 1],
    outputRange: [0, 300],
  });

  return (
    <View style={styles.sectionContainer}>
      <Animated.View
        style={[
          styles.headerRow,
          {
            backgroundColor: colors.onBackground,
            borderBottomLeftRadius: borderRadiusAnim,
            borderBottomRightRadius: borderRadiusAnim,
          },
        ]}
      >
        <TouchableOpacity
          onPress={toggleSection}
          style={styles.touchable}
          activeOpacity={0.6}
        >
          <Text
            variant="titleMedium"
            style={{ color: theme.colors.primary, fontWeight: "700" }}
          >
            {section.title}
          </Text>
          <Animated.View style={{ transform: [{ rotate: arrowRotation }] }}>
            <MaterialIcons
              name="keyboard-arrow-right"
              size={26}
              color={theme.colors.primary}
            />
          </Animated.View>
        </TouchableOpacity>
      </Animated.View>
      <Animated.View
        style={[
          styles.overflowHidden,
          { maxHeight: contentHeight, backgroundColor: colors.onBackground },
        ]}
      >
        <View style={styles.tagGrid}>
          {section.tags.map((tag: any) => {
            const isSelected = selectedTags.includes(tag.id);
            return (
              <TouchableOpacity
                key={tag.id}
                onPress={() => toggleTag(tag.id)}
                style={[
                  styles.tagChip,
                  {
                    backgroundColor: isSelected
                      ? theme.colors.primary
                      : "#E0E0E0",
                  },
                ]}
              >
                <Text
                  style={[
                    styles.tagText,
                    { color: isSelected ? "#fff" : "#444" },
                  ]}
                >
                  {tag.name}
                </Text>
              </TouchableOpacity>
            );
          })}
        </View>
      </Animated.View>
    </View>
  );
};

export default CollapsibleSection;

const styles = StyleSheet.create({
  sectionContainer: {
    marginBottom: 5,
  },
  headerRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    paddingVertical: 15,
    paddingHorizontal: 15,
    borderTopRightRadius: 20,
    borderTopLeftRadius: 20,
  },
  overflowHidden: {
    overflow: "hidden",
    borderBottomRightRadius: 20,
    borderBottomLeftRadius: 20,
  },
  tagGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    paddingVertical: 5,
    paddingHorizontal: 5,
  },
  tagChip: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 20,
    margin: 4,
  },
  tagText: {
    fontSize: 14,
    fontWeight: "600",
  },
  touchable: {
    width: "100%",
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    borderTopRightRadius: 20,
    borderTopLeftRadius: 20,
  },
});
