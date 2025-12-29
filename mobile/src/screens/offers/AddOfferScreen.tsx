import React, { useEffect, useMemo, useState } from "react";
import {
  View,
  ScrollView,
  StyleSheet,
  Dimensions,
  KeyboardAvoidingView,
  Platform,
  Image,
  TouchableOpacity,
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
  Icon,
} from "react-native-paper";
import { useNavigation } from "@react-navigation/native";
import { useTranslation } from "react-i18next";
import { Offer } from "../../types/Offer";
import { Tag } from "../../types/Tag";
import Input from "../../components/reusable/Input";
import { fieldsAddOffer } from "../../constans/formFields";
import CardContent from "../../components/main/CardContent";
import { useAuth } from "../../contexts/AuthContext";
import { createOffer } from "../../api/offers/handleOffersApi";
import { getAllTags } from "../../api/filter/handleTags";
import { useOfferStorageContext } from "../../contexts/OfferStorageContext";
import PhotoPickerModal from "../../components/pre-login/PhotoPickerModal";
import { uploadCameraImage, uploadGalleryImage } from "../../utils/pickerUtils";

const { width, height } = Dimensions.get("window");
interface FormState {
  offerPhoto?: string;
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
  const { userInfo } = useAuth();
  const [availableTags, setAvailableTags] = useState<Tag[]>([]);
  const [form, setForm] = useState<FormState>({
    offerPhoto: undefined,
    title: "",
    description: "",
    salary: "",
    maxParticipants: "",
    owner: userInfo?.userId ?? null,
    tags: [],
    tagInput: "",
  });
  const [showPreview, setShowPreview] = useState(false);
  const [date, setDate] = useState(new Date());
  const [isPhotoAvailable, setIsPhotoAvailable] = useState<boolean>(false);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const { refreshOffers } = useOfferStorageContext();

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const { body } = await getAllTags();
        const content = body?.data?.content ?? [];
        if (mounted && Array.isArray(content)) {
          setAvailableTags(
            content.map((t: any) => ({
              id: String(t.id),
              name: String(t.name),
              categoryName: String(t.categoryName ?? t.category?.name ?? ""),
              categoryColor: String(
                t.categoryColor ?? t.category?.color ?? "#999999",
              ),
            })),
          );
        }
      } catch (e) {
        console.warn("Nie udało się pobrać tagów", e);
      }
    })();
    return () => {
      mounted = false;
    };
  }, []);

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

  const formatLocalDateTime = (date: Date) => {
    const pad = (n: number) => n.toString().padStart(2, "0");
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
  };

  const previewItem: Offer = useMemo(() => {
    const resolvedTags = availableTags.filter((t) => form.tags.includes(t.id));
    return {
      title: form.title.trim(),
      description: form.description.trim(),
      salary: Number(form.salary) || 0,
      maxParticipants: Number(form.maxParticipants) || 0,
      tags: resolvedTags,
      offerPhoto: form.offerPhoto || undefined,
      dateAndTime: formatLocalDateTime(date),
    } as unknown as Offer;
  }, [form, availableTags, date]);

  const filteredTags = useMemo(() => {
    const q = form.tagInput?.trim().toLowerCase();
    if (!q) return [] as Tag[];
    return availableTags.filter(
      (t) => t.name.toLowerCase().includes(q) && !form.tags.includes(t.id),
    );
  }, [form.tagInput, form.tags, availableTags]);

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
      dateAndTime: formatLocalDateTime(date),
      salary: Number(form.salary) || 0,
      maxParticipants: Number(form.maxParticipants) || 0,
      tags: availableTags.filter((t) => form.tags.includes(t.id)),
      offerPhoto: form.offerPhoto || undefined,
    } as unknown as Offer;
    const createPayload = {
      title: form.title.trim(),
      description: form.description.trim(),
      salary: Number(form.salary) || 0,
      dateAndTime: formatLocalDateTime(date),
      maxParticipants: Number(form.maxParticipants) || 0,
      ownerId: form.owner ?? "",
      tags: form.tags,
      offerPhoto: form.offerPhoto,
    };
    await createOffer(createPayload);
    refreshOffers();
    navigation.goBack();
  };
  const handlePickCamera = async () => {
    try {
      const uri = await uploadCameraImage();
      if (uri) {
        setIsPhotoAvailable(true);
        setForm((prev) => ({ ...prev, offerPhoto: uri }));
      }
    } finally {
      setModalVisible(false);
    }
  };
  const handlePickGallery = async () => {
    try {
      const uri = await uploadGalleryImage();
      if (uri) {
        setIsPhotoAvailable(true);
        setForm((prev) => ({ ...prev, offerPhoto: uri }));
      }
    } finally {
      setModalVisible(false);
    }
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
              case "offerPhoto":
                return (
                  <React.Fragment key={key}>
                    <View style={styles.photoContainer}>
                      {!isPhotoAvailable ? (
                        <TouchableOpacity
                          style={[styles.logoWrapper, styles.placeholderLogo]}
                          onPress={() => setModalVisible(true)}
                        >
                          <Icon source="camera" size={50} />
                        </TouchableOpacity>
                      ) : (
                        <TouchableOpacity
                          style={styles.logoWrapper}
                          onPress={() => setModalVisible(true)}
                        >
                          <Image
                            source={{ uri: form.offerPhoto as string }}
                            style={{
                              width: 120,
                              height: 120,
                              borderRadius: 15,
                            }}
                          />
                        </TouchableOpacity>
                      )}
                    </View>
                    {modalVisible && (
                      <PhotoPickerModal
                        visible={modalVisible}
                        onClose={() => setModalVisible(false)}
                        onPickCamera={() => handlePickCamera()}
                        onPickGallery={() => handlePickGallery()}
                      />
                    )}
                  </React.Fragment>
                );
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
                  <ScrollView
                    style={styles.dropdownScroll}
                    contentContainerStyle={styles.dropdownContent}
                    keyboardShouldPersistTaps="handled"
                    nestedScrollEnabled
                  >
                    {filteredTags.map((tag) => (
                      <Chip
                        key={tag.id}
                        onPress={() => {
                          onAddTagById(tag.id);
                          setForm((prev) => ({ ...prev, tagInput: "" }));
                        }}
                        style={[
                          styles.dropdownChip,
                          { backgroundColor: colors.primary },
                        ]}
                      >
                        <Text style={{ color: colors.onPrimary }}>
                          {tag.name}
                        </Text>
                      </Chip>
                    ))}
                  </ScrollView>
                </View>
              )}
            </View>
          </View>
          <View style={styles.chipsWrap}>
            {form.tags.map((id) => {
              const name = availableTags.find((t) => t.id === id)?.name ?? id;
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
    backgroundColor: "#fff",
    borderRadius: 10,
    paddingVertical: 8,
    paddingHorizontal: 12,
    zIndex: 10,
  },
  dropdownScroll: {
    maxHeight: 200,
  },
  dropdownContent: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 6,
  },
  dropdownChip: {
    marginBottom: 6,
    backgroundColor: "#f0f0f0",
    borderRadius: 16,
    paddingHorizontal: 10,
    paddingVertical: 4,
  },
  photoContainer: {
    marginTop: 12,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
  },
  logoWrapper: {
    width: 120,
    height: 120,
    borderRadius: 15,
    borderWidth: 1,
    overflow: "hidden",
    alignSelf: "center",
    alignItems: "center",
    justifyContent: "center",
  },
  placeholderLogo: {
    borderStyle: "dashed",
    opacity: 0.75,
    backgroundColor: "white",
  },
});

export default AddOfferScreen;
