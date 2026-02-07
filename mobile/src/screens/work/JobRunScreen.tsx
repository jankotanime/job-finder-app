import React, { useCallback, useEffect, useMemo, useState } from "react";
import {
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  View,
  useWindowDimensions,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { RouteProp, useNavigation, useRoute } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { useTranslation } from "react-i18next";
import {
  ActivityIndicator,
  Button,
  Card,
  Dialog,
  Divider,
  Portal,
  Text,
  TextInput,
  useTheme,
} from "react-native-paper";

import type { RootStackParamList } from "../../types/RootStackParamList";
import type { Job } from "../../types/Job";
import {
  finishJob,
  getJobById,
  reportProblemFalse,
  reportProblemTrue,
} from "../../api/jobs/handleJobApi";
import { uploadCameraImage, uploadGalleryImage } from "../../utils/pickerUtils";
import {
  clearActiveJobTimer,
  getActiveJobTimer,
  getJobStartAt,
  setActiveJobTimer,
  setJobStartAt,
} from "../../utils/jobTimerStorage";

type JobRunRoute = RouteProp<RootStackParamList, "JobRun">;

type Nav = NativeStackNavigationProp<RootStackParamList, "JobRun">;

const getJobFromPayload = (payload: any): Job | null => {
  const data = payload?.body?.data;
  if (data && typeof data === "object") return data as Job;
  if (payload?.body && typeof payload.body === "object")
    return payload.body as Job;
  return null;
};

const pad2 = (n: number) => String(n).padStart(2, "0");

const formatElapsed = (ms: number) => {
  const totalSeconds = Math.max(0, Math.floor(ms / 1000));
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;
  return `${pad2(hours)}:${pad2(minutes)}:${pad2(seconds)}`;
};

const JobRunScreen = () => {
  const { colors } = useTheme();
  const { t } = useTranslation();
  const { height: windowHeight } = useWindowDimensions();

  const route = useRoute<JobRunRoute>();
  const navigation = useNavigation<Nav>();
  const { jobId, role, startedAt: startedAtFromParams } = route.params;

  const [job, setJob] = useState<Job | null>(null);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const [startAt, setStartAt] = useState<number | null>(null);
  const [now, setNow] = useState<number>(() => Date.now());

  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogMode, setDialogMode] = useState<
    "problem" | "noProblem" | "finish"
  >("problem");
  const [description, setDescription] = useState("");
  const [photoUri, setPhotoUri] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const fetchJob = useCallback(async () => {
    setErrorMessage(null);
    const res = await getJobById(jobId);
    const parsed = getJobFromPayload(res);
    if (!parsed) throw new Error("Invalid job payload");
    setJob(parsed);
  }, [jobId]);

  useEffect(() => {
    (async () => {
      try {
        setLoading(true);
        await fetchJob();
      } catch {
        setErrorMessage(t("jobs.common.loadError"));
      } finally {
        setLoading(false);
      }
    })();
  }, [fetchJob, t]);

  useEffect(() => {
    (async () => {
      try {
        if (
          typeof startedAtFromParams === "number" &&
          Number.isFinite(startedAtFromParams)
        ) {
          setStartAt(startedAtFromParams);
          await setActiveJobTimer({
            jobId,
            role,
            startedAt: startedAtFromParams,
          });
          return;
        }

        const fromKey = await getJobStartAt(jobId);
        if (fromKey) {
          setStartAt(fromKey);
          await setActiveJobTimer({ jobId, role, startedAt: fromKey });
          return;
        }

        const active = await getActiveJobTimer();
        if (active?.jobId === jobId && typeof active.startedAt === "number") {
          setStartAt(active.startedAt);
          return;
        }

        const fallback = Date.now();
        setStartAt(fallback);
        await setJobStartAt(jobId, fallback);
        await setActiveJobTimer({ jobId, role, startedAt: fallback });
      } catch (e) {}
    })();
  }, [jobId]);

  useEffect(() => {
    const interval = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(interval);
  }, []);

  const elapsedMs = useMemo(() => {
    if (!startAt) return 0;
    return now - startAt;
  }, [now, startAt]);

  const openDialog = useCallback((mode: "problem" | "noProblem" | "finish") => {
    setDialogMode(mode);
    setDescription("");
    setPhotoUri(null);
    setDialogOpen(true);
  }, []);

  const pickFromCamera = useCallback(async () => {
    const uri = await uploadCameraImage();
    if (uri) setPhotoUri(uri);
  }, []);

  const pickFromGallery = useCallback(async () => {
    const uri = await uploadGalleryImage();
    if (uri) setPhotoUri(uri);
  }, []);

  const submitDialog = useCallback(async () => {
    const trimmed = description.trim();
    if (!trimmed) {
      setErrorMessage(t("jobs.run.descriptionRequired"));
      return;
    }

    try {
      setSubmitting(true);
      setErrorMessage(null);

      if (dialogMode === "problem") {
        await reportProblemTrue(jobId, {
          description: trimmed,
          photoUri: photoUri ?? undefined,
        });
      } else if (dialogMode === "noProblem") {
        await reportProblemFalse(jobId, {
          description: trimmed,
          photoUri: photoUri ?? undefined,
        });
      } else {
        await finishJob(jobId, {
          description: trimmed,
          photoUri: photoUri ?? undefined,
        });
        await clearActiveJobTimer(jobId);
      }

      setDialogOpen(false);
      await fetchJob();
    } catch {
      setErrorMessage(t("jobs.common.actionError"));
    } finally {
      setSubmitting(false);
    }
  }, [description, dialogMode, fetchJob, jobId, photoUri, t]);

  const timerTitle = useMemo(() => {
    if (role === "owner") return t("jobs.run.timerTitleOwner");
    return t("jobs.run.timerTitleContractor");
  }, [role, t]);

  if (loading) {
    return (
      <View style={[styles.center, { backgroundColor: colors.background }]}>
        <ActivityIndicator size="large" color={colors.primary} />
      </View>
    );
  }

  return (
    <SafeAreaView
      style={[styles.screen, { backgroundColor: colors.background }]}
    >
      <KeyboardAvoidingView
        style={styles.flex}
        behavior={Platform.OS === "ios" ? "padding" : "height"}
      >
        <ScrollView
          keyboardShouldPersistTaps="handled"
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
        >
          <Text variant="headlineSmall" style={styles.header}>
            {t("jobs.run.title")}
          </Text>

          {errorMessage ? (
            <Text style={{ color: colors.error, marginBottom: 8 }}>
              {errorMessage}
            </Text>
          ) : null}

          <Card style={[styles.card, { backgroundColor: colors.surface }]}>
            <Card.Content>
              <Text style={{ color: colors.onSurfaceVariant }}>
                {timerTitle}
              </Text>
              <Text style={[styles.timer, { color: colors.primary }]}>
                {formatElapsed(elapsedMs)}
              </Text>

              {!startAt ? (
                <Text style={{ color: colors.onSurfaceVariant, marginTop: 4 }}>
                  {t("jobs.run.noStartInfo")}
                </Text>
              ) : null}

              <Divider style={{ marginVertical: 14 }} />

              <Text variant="titleMedium" style={{ fontWeight: "700" }}>
                {job?.title ?? t("jobs.run.job")}
              </Text>
              {job?.description ? (
                <Text style={{ color: colors.onSurfaceVariant, marginTop: 6 }}>
                  {job.description}
                </Text>
              ) : null}
            </Card.Content>
          </Card>

          <View style={styles.actions}>
            <Button mode="contained" onPress={() => openDialog("problem")}>
              {t("jobs.run.reportProblem")}
            </Button>
            {role === "owner" ? (
              <Button
                mode="contained"
                buttonColor={colors.error}
                onPress={() => openDialog("finish")}
              >
                {t("jobs.run.finishJob")}
              </Button>
            ) : null}

            <Button
              mode="text"
              onPress={() =>
                navigation.reset({
                  index: 0,
                  routes: [{ name: "Main" as any }],
                })
              }
            >
              {t("jobs.common.back")}
            </Button>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
      <Portal>
        <Dialog
          visible={dialogOpen}
          onDismiss={() => setDialogOpen(false)}
          style={{ maxHeight: Math.round(windowHeight * 0.8) }}
        >
          <KeyboardAvoidingView
            behavior={Platform.OS === "ios" ? "padding" : "height"}
            keyboardVerticalOffset={Platform.OS === "ios" ? 24 : 0}
            style={styles.dialogKav}
          >
            <Dialog.Title>
              {dialogMode === "finish"
                ? t("jobs.run.finishDialogTitle")
                : dialogMode === "noProblem"
                  ? t("jobs.run.noProblemDialogTitle")
                  : t("jobs.run.problemDialogTitle")}
            </Dialog.Title>

            <Dialog.Content style={styles.dialogContent}>
              <ScrollView
                keyboardShouldPersistTaps="handled"
                showsVerticalScrollIndicator={false}
                contentContainerStyle={{ paddingBottom: 6 }}
              >
                <TextInput
                  mode="outlined"
                  label={t("jobs.run.descriptionLabel")}
                  value={description}
                  onChangeText={setDescription}
                  multiline
                  style={{ marginBottom: 12 }}
                />

                <View style={{ flexDirection: "row", gap: 10 }}>
                  <Button mode="outlined" onPress={pickFromCamera}>
                    {t("jobs.run.photoCamera")}
                  </Button>
                  <Button mode="outlined" onPress={pickFromGallery}>
                    {t("jobs.run.photoGallery")}
                  </Button>
                </View>

                {photoUri ? (
                  <Text
                    style={{ marginTop: 10, color: colors.onSurfaceVariant }}
                  >
                    {t("jobs.run.photoSelected")}
                  </Text>
                ) : null}
              </ScrollView>
            </Dialog.Content>

            <Dialog.Actions>
              <Button onPress={() => setDialogOpen(false)}>
                {t("jobs.run.cancel")}
              </Button>
              <Button
                loading={submitting}
                disabled={submitting}
                onPress={submitDialog}
              >
                {t("jobs.run.send")}
              </Button>
            </Dialog.Actions>
          </KeyboardAvoidingView>
        </Dialog>
      </Portal>
    </SafeAreaView>
  );
};

export default JobRunScreen;

const styles = StyleSheet.create({
  flex: {
    flex: 1,
  },
  dialogKav: {
    flexGrow: 1,
  },
  dialogContent: {
    flexGrow: 1,
  },
  screen: {
    flex: 1,
    paddingHorizontal: 16,
    paddingTop: 16,
    gap: 12,
  },
  scrollContent: {
    flexGrow: 1,
    paddingBottom: 24,
    gap: 12,
  },
  header: {
    fontWeight: "700",
  },
  card: {
    borderRadius: 14,
  },
  timer: {
    fontSize: 48,
    fontWeight: "900",
    marginTop: 6,
    letterSpacing: 1,
  },
  actions: {
    gap: 10,
    marginTop: 4,
  },
  center: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: 16,
  },
});
