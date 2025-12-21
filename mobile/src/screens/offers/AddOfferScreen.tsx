import React, { useMemo, useState } from "react";
import {
  View,
  ScrollView,
  StyleSheet,
  Dimensions,
  KeyboardAvoidingView,
  Platform,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import {
  Button,
  HelperText,
  Chip,
  Text,
  useTheme,
  Portal,
  Dialog,
} from "react-native-paper";
import { useNavigation } from "@react-navigation/native";
import { useTranslation } from "react-i18next";
import useOfferStorage from "../../hooks/useOfferStorage";
import { Offer } from "../../types/Offer";
import { Tag } from "../../types/Tag";
import Input from "../../components/reusable/Input";
import { fieldsAddOffer } from "../../constans/formFields";
import CardContent from "../../components/main/CardContent";
import { useAuth } from "../../contexts/AuthContext";
import { createOffer } from "../../api/offers/handleOffersApi";
import { getAllTags } from "../../api/filter/handleTags";

const { width, height } = Dimensions.get("window");
interface FormState {
  title: string;
  description: string;
  salary: string;
  maxParticipants: string;
  owner: string | null;
  tags: string[];
  tagInput: string;
}
const AddOfferScreen = () => {
  const { colors } = useTheme();
  const navigation = useNavigation<any>();
  const { t } = useTranslation();
  const { addStorageOffer } = useOfferStorage();
  const { userInfo } = useAuth();
  console.log(userInfo);
  const mockAvailableTags: Tag[] = [
    { id: "tag-edu", name: "Edukacja" },
    { id: "tag-it", name: "IT" },
    { id: "tag-health", name: "Zdrowie" },
    { id: "tag-event", name: "Eventy" },
  ];
  const [form, setForm] = useState<FormState>({
    title: "",
    description: "",
    salary: "",
    maxParticipants: "",
    owner: userInfo?.userId ?? null,
    tags: [],
    tagInput: "",
  });

  const [showPreview, setShowPreview] = useState(false);

  const errors = useMemo(() => {
    const e: Record<string, string> = {};
    if (!form.title.trim()) e.title = t("offer.errors.titleRequired");
    if (!form.description.trim())
      e.description = t("offer.errors.descriptionRequired");
    if (!form.salary.trim() || isNaN(Number(form.salary)))
      e.salary = t("offer.errors.salaryInvalid");
    if (!form.maxParticipants.trim() || isNaN(Number(form.maxParticipants)))
      e.maxParticipants = t("offer.errors.maxParticipantsInvalid");
    return e;
  }, [form, t]);

  const previewItem: Offer = useMemo(() => {
    const resolvedTags = mockAvailableTags.filter((t) =>
      form.tags.includes(t.id),
    );
    return {
      title: form.title.trim(),
      description: form.description.trim(),
      salary: Number(form.salary) || 0,
      maxParticipants: Number(form.maxParticipants) || 0,
      owner: form.owner ?? "",
      tags: resolvedTags,
    } as unknown as Offer;
  }, [form]);

  const filteredTags = useMemo(() => {
    const q = form.tagInput?.trim().toLowerCase();
    if (!q) return [] as Tag[];
    return mockAvailableTags.filter(
      (t) => t.name.toLowerCase().includes(q) && !form.tags.includes(t.id),
    );
  }, [form.tagInput, form.tags]);

  const onAddTag = () => {
    const name = form.tagInput.trim();
    if (!name) return;
    const found = mockAvailableTags.find(
      (t) => t.name.toLowerCase() === name.toLowerCase(),
    );
    const idToAdd =
      found?.id ?? `tmp-${Math.random().toString(36).slice(2, 8)}`;
    if (form.tags.includes(idToAdd)) {
      setForm((prev) => ({ ...prev, tagInput: "" }));
      return;
    }
    setForm((prev) => ({
      ...prev,
      tags: [...prev.tags, idToAdd],
      tagInput: "",
    }));
  };

  const onAddTagById = (id: string) => {
    if (!id) return;
    if (form.tags.includes(id)) return;
    setForm((prev) => ({ ...prev, tags: [...prev.tags, id] }));
  };

  const onRemoveTag = (id: string) => {
    setForm((prev) => ({
      ...prev,
      tags: prev.tags.filter((tId) => tId !== id),
    }));
  };

  const onSubmit = async () => {
    if (Object.keys(errors).length > 0) return;
    const offer: Offer = {
      title: form.title.trim(),
      description: form.description.trim(),
      salary: Number(form.salary) || 0,
      maxParticipants: Number(form.maxParticipants) || 0,
      owner: form.owner ?? "",
      tags: mockAvailableTags.filter((t) => form.tags.includes(t.id)),
    } as unknown as Offer;
    addStorageOffer(offer);
    const createPayload = {
      title: form.title.trim(),
      description: form.description.trim(),
      salary: Number(form.salary) || 0,
      maxParticipants: Number(form.maxParticipants) || 0,
      ownerId: form.owner ?? "",
      tags: form.tags,
    };
    const response = await createOffer(createPayload as any);
    console.log(response);
    navigation.goBack();
  };

  return (
    <SafeAreaView
      edges={["top", "bottom"]}
      style={{ flex: 1, backgroundColor: colors.background }}
    >
      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === "ios" ? "padding" : "height"}
        keyboardVerticalOffset={Platform.OS === "ios" ? 0 : 30}
      >
        <ScrollView
          contentContainerStyle={[
            styles.container,
            { backgroundColor: colors.background },
          ]}
          keyboardShouldPersistTaps="handled"
        >
          <Text
            variant="titleLarge"
            style={[styles.header, { color: colors.primary }]}
          >
            {t("offer.addHeader")}
          </Text>
          {fieldsAddOffer(t).map(({ key, placeholder, secure }) => {
            const commonProps = {
              placeholder,
              mode: "outlined" as const,
              secure,
            };
            switch (key) {
              case "title":
                return (
                  <View style={styles.formGroup} key={key}>
                    <Input
                      {...commonProps}
                      value={form.title}
                      onChangeText={(v) =>
                        setForm((prev) => ({ ...prev, title: v }))
                      }
                      style={styles.inputStyle}
                    />
                    <HelperText type="error" visible={!!errors.title}>
                      {errors.title}
                    </HelperText>
                  </View>
                );
              case "description":
                return (
                  <View style={styles.formGroup} key={key}>
                    <Input
                      {...commonProps}
                      value={form.description}
                      onChangeText={(v) =>
                        setForm((prev) => ({ ...prev, description: v }))
                      }
                      multiline
                      numberOfLines={5}
                      style={styles.inputStyle}
                    />
                    <HelperText type="error" visible={!!errors.description}>
                      {errors.description}
                    </HelperText>
                  </View>
                );
              case "salary":
                return (
                  <View style={styles.formGroup} key={key}>
                    <Input
                      {...commonProps}
                      value={form.salary}
                      onChangeText={(v) =>
                        setForm((prev) => ({ ...prev, salary: v }))
                      }
                      style={styles.inputStyle}
                      keyboardType="numeric"
                    />
                    <HelperText type="error" visible={!!errors.salary}>
                      {errors.salary}
                    </HelperText>
                  </View>
                );
              case "maxParticipants":
                return (
                  <View style={styles.formGroup} key={key}>
                    <Input
                      {...commonProps}
                      value={form.maxParticipants}
                      onChangeText={(v) =>
                        setForm((prev) => ({ ...prev, maxParticipants: v }))
                      }
                      style={styles.inputStyle}
                      keyboardType="numeric"
                    />
                    <HelperText type="error" visible={!!errors.maxParticipants}>
                      {errors.maxParticipants}
                    </HelperText>
                  </View>
                );
              // case "location":
              //     return (
              //         <View style={styles.formGroup} key={key}>
              //             <Input {...commonProps} value={form.location} onChangeText={(v) => updateForm("location", v)} style={styles.inputStyle} />
              //             <HelperText type="error" visible={!!errors.location}>{errors.location}</HelperText>
              //         </View>
              //     );
              // case "offerPhoto":
              //     return (
              //         <View style={styles.formGroup} key={key}>
              //             <Input {...commonProps} value={form.offerPhoto} onChangeText={(v) => updateForm("offerPhoto", v)} style={styles.inputStyle} />
              //         </View>
              //     );
              // default:
              //     return null;
            }
          })}
          <View style={styles.row}>
            <View style={styles.flexItem}>
              <Input
                placeholder={t("offer.addTag")}
                value={form.tagInput}
                onChangeText={(v) =>
                  setForm((prev) => ({ ...prev, tagInput: v }))
                }
                mode="outlined"
                style={styles.inputStyle}
              />
              {filteredTags.length > 0 && (
                <View
                  style={[
                    styles.dropdown,
                    {
                      borderColor: colors.primary,
                      backgroundColor: colors.surface,
                    },
                  ]}
                >
                  {filteredTags.map((tag) => (
                    <Chip
                      key={tag.id}
                      onPress={() => {
                        onAddTagById(tag.id);
                        setForm((prev) => ({ ...prev, tagInput: "" }));
                      }}
                      style={styles.dropdownChip}
                    >
                      {tag.name}
                    </Chip>
                  ))}
                </View>
              )}
            </View>
            <Button
              style={{ marginTop: height * 0.015 }}
              mode="contained"
              onPress={onAddTag}
            >
              {t("offer.addTag")}
            </Button>
          </View>
          <View style={styles.chipsWrap}>
            {form.tags.map((id) => {
              const name =
                mockAvailableTags.find((t) => t.id === id)?.name ?? id;
              return (
                <Chip
                  key={id}
                  onClose={() => onRemoveTag(id)}
                  style={styles.chip}
                >
                  {name}
                </Chip>
              );
            })}
          </View>
          <View style={[styles.row, { justifyContent: "space-between" }]}>
            <Button
              style={{ width: width * 0.39 }}
              mode="contained"
              disabled={Object.keys(errors).length > 0}
              onPress={onSubmit}
            >
              {t("offer.save")}
            </Button>
            <Button mode="outlined" onPress={() => setShowPreview(true)}>
              {t("offer.preview")}
            </Button>
          </View>
          <Portal>
            <Dialog
              visible={showPreview}
              onDismiss={() => setShowPreview(false)}
              style={{ margin: 0 }}
            >
              <Dialog.Content style={{ padding: 0 }}>
                <ScrollView>
                  <CardContent item={previewItem} isActive preview={true} />
                </ScrollView>
              </Dialog.Content>
              <Dialog.Actions>
                <Button onPress={() => setShowPreview(false)}>
                  {t("offer.close")}
                </Button>
              </Dialog.Actions>
            </Dialog>
          </Portal>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 16,
    paddingTop: 20,
  },
  header: {
    fontWeight: "700",
    marginBottom: 12,
  },
  formGroup: {
    marginBottom: -5,
  },
  row: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    marginTop: 15,
  },
  flexItem: {
    flex: 1,
  },
  nowBtn: {
    alignSelf: "flex-end",
  },
  radioRow: {
    flexDirection: "row",
    justifyContent: "space-between",
  },
  chipsWrap: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
    marginTop: 10,
  },
  chip: {
    marginRight: 6,
  },
  divider: {
    marginVertical: 8,
  },
  inputStyle: {
    width: "100%",
    alignSelf: "stretch",
    top: 0,
  },
  dropdown: {
    marginTop: 6,
    borderWidth: 1,
    borderRadius: 8,
    paddingHorizontal: 8,
    paddingVertical: 6,
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 6,
  },
  dropdownChip: {
    marginRight: 6,
    marginBottom: 6,
  },
});

export default AddOfferScreen;
