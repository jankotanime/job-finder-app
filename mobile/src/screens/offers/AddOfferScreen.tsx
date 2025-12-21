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

const { width, height } = Dimensions.get("window");
interface FormState {
  title: string;
  description: string;
  salary: string;
  maxParticipants: string;
  owner: string | null;
  tags: Tag[];
}
const AddOfferScreen = () => {
  const { colors } = useTheme();
  const navigation = useNavigation<any>();
  const { t } = useTranslation();
  const { addStorageOffer } = useOfferStorage();
  const { userInfo } = useAuth();
  console.log(userInfo);
  const [form, setForm] = useState<FormState>({
    title: "",
    description: "",
    salary: "",
    maxParticipants: "",
    owner: userInfo?.userId ?? null,
    tags: [],
  });
  const updateForm = <K extends keyof FormState>(
    key: K,
    value: FormState[K],
  ) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

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
    return {
      title: form.title.trim(),
      description: form.description.trim(),
      salary: Number(form.salary) || 0,
      maxParticipants: Number(form.maxParticipants) || 0,
      owner: form.owner ?? "",
      tags: form.tags,
    } as unknown as Offer;
  }, [form]);

  // const onAddTag = () => {
  //     const name = form.tagInput.trim();
  //     if (!name) return;
  //     if (form.tags.some((t) => t.name.toLowerCase() === name.toLowerCase())) {
  //       updateForm("tagInput", "");
  //       return;
  //     }
  //     updateForm("tags", [...form.tags, { name }]);
  //     updateForm("tagInput", "");
  // };

  const onRemoveTag = (name: string) => {
    updateForm(
      "tags",
      form.tags.filter((t) => t.name !== name),
    );
  };

  const onSubmit = async () => {
    if (Object.keys(errors).length > 0) return;
    const offer: Offer = {
      title: form.title.trim(),
      description: form.description.trim(),
      salary: Number(form.salary) || 0,
      maxParticipants: Number(form.maxParticipants) || 0,
      owner: form.owner ?? "",
      tags: form.tags,
    } as unknown as Offer;
    addStorageOffer(offer);
    const response = await createOffer(offer);
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
                      onChangeText={(v) => updateForm("title", v)}
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
                      onChangeText={(v) => updateForm("description", v)}
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
                      onChangeText={(v) => updateForm("salary", v)}
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
                      onChangeText={(v) => updateForm("maxParticipants", v)}
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
              {/* <Input
                    placeholder={t("offer.addTag")}
                    value={form.tagInput}
                    onChangeText={(v) => updateForm("tagInput", v)}
                    mode="outlined"
                    style={styles.inputStyle}
                /> */}
            </View>
            {/* <Button style={{ marginTop: height * 0.015 }} mode="contained" onPress={onAddTag}>
                    {t("offer.addTag")}
                </Button> */}
          </View>
          <View style={styles.chipsWrap}>
            {form.tags.map((tag, idx) => (
              <Chip
                key={`${tag.name}-${idx}`}
                onClose={() => onRemoveTag(tag.name)}
                style={styles.chip}
              >
                {tag.name}
              </Chip>
            ))}
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
});

export default AddOfferScreen;
