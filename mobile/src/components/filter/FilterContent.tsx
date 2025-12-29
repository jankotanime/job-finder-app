import React, { useState, useEffect } from "react";
import {
  StyleSheet,
  View,
  ScrollView,
  Dimensions,
  ActivityIndicator,
} from "react-native";
import { useTheme, Button } from "react-native-paper";
import { SafeAreaView } from "react-native-safe-area-context";
import { getAllTags } from "../../api/filter/handleTags";
import CollapsibleSection from "./FilterCollapsibleSection";
import { useTranslation } from "react-i18next";
import { handleFilterOffers } from "../../api/filter/handleFilterOffers";
import { Offer } from "../../types/Offer";
import { buildPhotoUrl } from "../../utils/photoUrl";
import { useAuth } from "../../contexts/AuthContext";

const { height, width } = Dimensions.get("window");

interface ApiTag {
  categoryColor: string;
  categoryName: string;
  id: string;
  name: string;
}
interface GroupedCategory {
  title: string;
  tags: { id: string; name: string }[];
}
interface FilterContentProps {
  setOffersData: (data: Offer[]) => void;
  onClose?: () => void;
}

const FilterContent = ({ setOffersData, onClose }: FilterContentProps) => {
  const theme = useTheme();
  const [categories, setCategories] = useState<GroupedCategory[]>([]);
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const { t } = useTranslation();
  const { userInfo } = useAuth();

  useEffect(() => {
    fetchTags();
  }, []);

  const fetchTags = async () => {
    try {
      setLoading(true);
      const response = await getAllTags();
      const bodyTags: ApiTag[] = response.body.data.content;
      const grouped = bodyTags.reduce((acc: GroupedCategory[], current) => {
        const category = acc.find(
          (item) => item.title === current.categoryName,
        );
        if (category) {
          category.tags.push({ id: current.id, name: current.name });
        } else {
          acc.push({
            title: current.categoryName,
            tags: [{ id: current.id, name: current.name }],
          });
        }
        return acc;
      }, []);

      setCategories(grouped);
    } catch (error) {
      console.error("error while getting tags:", error);
    } finally {
      setLoading(false);
    }
  };

  const toggleTag = (tagId: string) => {
    setSelectedTags((prev) =>
      prev.includes(tagId)
        ? prev.filter((id) => id !== tagId)
        : [...prev, tagId],
    );
  };

  const handleApply = async () => {
    const page = await handleFilterOffers({ tags: selectedTags });
    const body = Array.isArray(page?.body?.data?.content)
      ? page.body.data.content
      : [];
    const normalized = body.map((it: any) => ({
      ...it,
      offerPhoto: buildPhotoUrl(it?.photo?.storageKey),
    }));
    const filtered = normalized.filter(
      (it: any) => it?.owner?.id !== userInfo?.userId,
    );
    setOffersData(filtered);
    onClose && onClose();
  };

  if (loading) {
    return (
      <View style={[styles.mainContainer, styles.center]}>
        <ActivityIndicator color={theme.colors.primary} size="large" />
      </View>
    );
  }

  return (
    <SafeAreaView
      style={[styles.safeArea, { backgroundColor: theme.colors.background }]}
    >
      <View style={styles.mainContainer}>
        <ScrollView
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
        >
          {categories.map((section) => (
            <CollapsibleSection
              key={section.title}
              section={section}
              selectedTags={selectedTags}
              toggleTag={toggleTag}
              theme={theme}
            />
          ))}
        </ScrollView>

        <View style={styles.footer}>
          <Button
            mode="contained"
            onPress={handleApply}
            style={styles.applyButton}
            contentStyle={styles.buttonInner}
          >
            {t("filter.apply")}
          </Button>
        </View>
      </View>
    </SafeAreaView>
  );
};

export default FilterContent;

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    marginTop: height * 0.07,
  },
  mainContainer: {
    flex: 1,
    width: width * 0.95,
  },
  center: {
    justifyContent: "center",
    alignItems: "center",
  },
  scrollContent: {
    paddingHorizontal: 20,
    paddingTop: 20,
    paddingBottom: 100,
  },
  overflowHidden: {
    overflow: "hidden",
    borderBottomRightRadius: 20,
    borderBottomLeftRadius: 20,
  },
  footer: {
    padding: 20,
    backgroundColor: "transparent",
  },
  applyButton: {
    borderRadius: 15,
  },
  buttonInner: {
    height: 54,
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
