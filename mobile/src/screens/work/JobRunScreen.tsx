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
  deleteJob,
  finishJob,
  getJobDispatcher,
  getJobById,
  getJobsAsContractor,
  getJobsAsOwner,
  reportProblemFalse,
  reportProblemTrue,
  startJob,
} from "../../api/jobs/handleJobApi";
import { uploadCameraImage, uploadGalleryImage } from "../../utils/pickerUtils";
import {
  clearContractorFinishedLocally,
  getContractorFinishedLocally,
  setContractorFinishedLocally,
} from "../../utils/jobLocalCompletion";
import {
  type JobWebSocketMessage,
  useWebSockets,
} from "../../hooks/useWebSocket";

type JobRunRoute = RouteProp<RootStackParamList, "JobRun">;

type Nav = NativeStackNavigationProp<RootStackParamList, "JobRun">;

const getJobFromPayload = (payload: any): Job | null => {
  const data = payload?.body?.data;
  if (data && typeof data === "object") return data as Job;
  if (payload?.body && typeof payload.body === "object")
    return payload.body as Job;
  return null;
};

const getJobsArrayFromPayload = (payload: any): any[] => {
  const data = payload?.body?.data;
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.content)) return data.content;
  if (Array.isArray(payload?.body)) return payload.body;
  if (Array.isArray(payload)) return payload;
  return [];
};

const getIdFromListItem = (item: any): string | null => {
  const raw = item.id;
  if (raw == null) return null;
  return String(raw);
};

const getJobFromListItem = (item: any): Job | null => {
  const first = item?.job ?? item?.offer?.job ?? item?.offer ?? item;
  if (!first || typeof first !== "object") return null;

  const direct = first as any;
  if (direct?.id && typeof direct?.title === "string") return direct as Job;

  const nested = direct?.job;
  if (nested?.id && typeof nested?.title === "string") return nested as Job;

  return null;
};

type JobDispatcher = {
  finishedAt?: string | null;
  startedAt?: string | number | null;
  startAt?: string | number | null;
  started?: boolean | null;
};

const dispatcherIndicatesStarted = (d: JobDispatcher | null): boolean => {
  if (!d) return false;
  const startedAt = (d as any)?.startedAt ?? (d as any)?.startAt ?? null;
  const startedFlag = (d as any)?.started;
  if (startedFlag === true) return true;
  return Boolean(startedAt);
};

const getDispatcherFromPayload = (payload: any): JobDispatcher | null => {
  const data = payload?.body?.data;
  if (data && typeof data === "object") return data as JobDispatcher;
  if (payload?.body && typeof payload.body === "object")
    return payload.body as JobDispatcher;
  return null;
};

const formatDuration = (valueMs: number | null) => {
  if (valueMs == null || !Number.isFinite(valueMs) || valueMs < 0) {
    return "--:--:--";
  }

  const totalSeconds = Math.floor(valueMs / 1000);
  const hours = Math.floor(totalSeconds / 3600)
    .toString()
    .padStart(2, "0");
  const minutes = Math.floor((totalSeconds % 3600) / 60)
    .toString()
    .padStart(2, "0");
  const seconds = (totalSeconds % 60).toString().padStart(2, "0");
  return `${hours}:${minutes}:${seconds}`;
};

const JobRunScreen = () => {
  const { colors } = useTheme();
  const { t } = useTranslation();
  const { height: windowHeight } = useWindowDimensions();

  const route = useRoute<JobRunRoute>();
  const navigation = useNavigation<Nav>();
  const { jobId, jobDispatcherId, role } = route.params;

  const [job, setJob] = useState<Job | null>(null);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [elapsedMs, setElapsedMs] = useState<number | null>(null);
  const [timerStartedAtMs, setTimerStartedAtMs] = useState<number | null>(null);

  const [contractorFinishedAt, setContractorFinishedAt] = useState<
    string | null
  >(null);
  const [contractorFinishSent, setContractorFinishSent] = useState(false);
  const [ownerFinalizedSeen, setOwnerFinalizedSeen] = useState(false);

  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogMode, setDialogMode] = useState<
    "problem" | "noProblem" | "finish"
  >("problem");
  const [description, setDescription] = useState("");
  const [photoUri, setPhotoUri] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const handleWebSocketMessage = useCallback((message: JobWebSocketMessage) => {
    if (message.signalType === "JOB_START") {
      const serverTimeMs = message.time ? Date.parse(message.time) : NaN;
      const elapsedFromServer = Number(message.timePassedMilisecods ?? 0);
      const startedAtMs = Number.isFinite(serverTimeMs)
        ? serverTimeMs - elapsedFromServer
        : Date.now() - elapsedFromServer;

      setTimerStartedAtMs(startedAtMs);
      setElapsedMs(Math.max(0, Date.now() - startedAtMs));
      return;
    }

    if (
      message.signalType === "JOB_FINISH" ||
      message.signalType === "JOB_STOP"
    ) {
      setTimerStartedAtMs(null);
      setElapsedMs(null);
    }
  }, []);

  useWebSockets(jobDispatcherId, { onMessage: handleWebSocketMessage });

  const fetchJob = useCallback(async () => {
    setErrorMessage(null);
    const res = await getJobById(jobId);

    if (!res?.response?.ok) {
      const code = res?.body?.code;
      if (code === "USER_NOT_CONTRACTOR_OR_OWNER") {
        try {
          const primaryListRes =
            role === "contractor"
              ? await getJobsAsContractor()
              : await getJobsAsOwner();
          const primaryJobs = getJobsArrayFromPayload(primaryListRes);
          const primaryMatch = primaryJobs.find((j: any) => {
            const candidateId = getIdFromListItem(j);
            return candidateId != null && candidateId === String(jobId);
          });
          const primaryJob = primaryMatch
            ? getJobFromListItem(primaryMatch)
            : null;
          if (primaryJob) {
            setJob(primaryJob);
            setErrorMessage(null);
            return;
          }

          const secondaryListRes =
            role === "contractor"
              ? await getJobsAsOwner()
              : await getJobsAsContractor();
          const secondaryJobs = getJobsArrayFromPayload(secondaryListRes);
          const secondaryMatch = secondaryJobs.find((j: any) => {
            const candidateId = getIdFromListItem(j);
            return candidateId != null && candidateId === String(jobId);
          });
          const secondaryJob = secondaryMatch
            ? getJobFromListItem(secondaryMatch)
            : null;
          if (secondaryJob) {
            setJob(secondaryJob);
            setErrorMessage(null);
            return;
          }
        } catch {}

        setJob(null);
        setErrorMessage(t("jobs.common.noAccess"));
        return;
      }
      setJob(null);
      setErrorMessage(res?.body?.message ?? t("jobs.common.loadError"));
      return;
    }

    const parsed = getJobFromPayload(res);
    if (!parsed) throw new Error("Invalid job payload");
    setJob(parsed);
  }, [jobId, role, t]);

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
    if (!job) return;
    if (
      job.status === "FINISHED_FAILURE" ||
      job.status === "FINISHED_SUCCESS"
    ) {
      clearContractorFinishedLocally(jobId).catch(() => {});
    }
  }, [job, jobId]);

  useEffect(() => {
    setContractorFinishedAt(null);
    setContractorFinishSent(false);
    setOwnerFinalizedSeen(false);
    setElapsedMs(null);
    setTimerStartedAtMs(null);
  }, [jobId]);

  useEffect(() => {
    if (!job || job.status !== "IN_PROGRESS" || timerStartedAtMs == null) {
      return;
    }

    const tick = () => {
      setElapsedMs(Math.max(0, Date.now() - timerStartedAtMs));
    };

    tick();
    const interval = setInterval(tick, 1000);
    return () => clearInterval(interval);
  }, [job, timerStartedAtMs]);

  useEffect(() => {
    if (!job) return;

    if (job.status !== "IN_PROGRESS") {
      setTimerStartedAtMs(null);
      setElapsedMs(null);
      return;
    }

    if (timerStartedAtMs === null) {
      setTimerStartedAtMs(Date.now());
    }
  }, [job, timerStartedAtMs]);

  useEffect(() => {
    if (role !== "contractor") return;
    let cancelled = false;
    (async () => {
      try {
        const finishedLocal = await getContractorFinishedLocally(jobId);
        if (!cancelled && finishedLocal) {
          setContractorFinishSent(true);
        }
      } catch {}
    })();
    return () => {
      cancelled = true;
    };
  }, [jobId, role]);

  useEffect(() => {
    if (role !== "contractor") return;
    if (!contractorFinishSent) return;

    const interval = setInterval(() => {
      fetchJob().catch(() => {});
    }, 8000);

    return () => clearInterval(interval);
  }, [contractorFinishSent, fetchJob, role]);

  useEffect(() => {
    if (role !== "contractor") return;
    if (!contractorFinishSent) return;
    if (!job) return;

    if (
      job.status === "FINISHED_FAILURE" ||
      job.status === "FINISHED_SUCCESS"
    ) {
      setOwnerFinalizedSeen(true);

      const tId = setTimeout(() => {
        navigation.reset({
          index: 0,
          routes: [{ name: "Main" as any }],
        });
      }, 1400);

      return () => clearTimeout(tId);
    }
  }, [contractorFinishSent, job, navigation, role]);

  useEffect(() => {
    if (role !== "owner") return;
    if (!job) return;
    if (job.status !== "IN_PROGRESS") {
      setContractorFinishedAt(null);
      return;
    }

    const poll = async () => {
      try {
        const res = await getJobDispatcher(jobId);
        if (!res?.response?.ok) return;
        const parsed = getDispatcherFromPayload(res);
        const finishedAt = parsed?.finishedAt ?? null;
        setContractorFinishedAt(finishedAt ? String(finishedAt) : null);
      } catch {}
    };

    poll();
    const interval = setInterval(poll, 8000);
    return () => clearInterval(interval);
  }, [job, jobId, role]);

  useEffect(() => {
    if (role !== "contractor") return;
    if (!job) return;
    if (job.status !== "READY") return;

    let cancelled = false;

    const poll = async () => {
      try {
        const res = await getJobDispatcher(jobId);
        if (!res?.response?.ok) return;
        const parsed = getDispatcherFromPayload(res);
        if (!cancelled && dispatcherIndicatesStarted(parsed)) {
          setJob((prev) =>
            prev ? ({ ...prev, status: "IN_PROGRESS" as any } as Job) : prev,
          );
        }
      } catch {}
    };

    poll();
    const interval = setInterval(poll, 4000);
    return () => {
      cancelled = true;
      clearInterval(interval);
    };
  }, [job, jobId, role]);

  useEffect(() => {
    if (role !== "owner") return;
    const interval = setInterval(() => {
      fetchJob().catch(() => {});
    }, 8000);
    return () => clearInterval(interval);
  }, [fetchJob, role]);

  const isInProgress = job?.status === "IN_PROGRESS";
  const canReport =
    isInProgress && (role !== "contractor" || !contractorFinishSent);
  const canFinish =
    role === "owner"
      ? isInProgress && Boolean(contractorFinishedAt)
      : isInProgress && !contractorFinishSent;

  const canStart = role === "owner" && job?.status === "READY";
  const canConfirmAsContractor =
    role === "contractor" && isInProgress && !elapsedMs;

  const openDialog = useCallback((mode: "problem" | "noProblem" | "finish") => {
    setDialogMode(mode);
    setDescription("");
    setPhotoUri(null);
    setDialogOpen(true);
  }, []);

  const contractorFinishedInline = useMemo(() => {
    return (
      role === "owner" &&
      Boolean(contractorFinishedAt) &&
      job?.status === "IN_PROGRESS"
    );
  }, [contractorFinishedAt, job?.status, role]);

  const onPressFinish = useCallback(() => {
    if (!canFinish) return;
    openDialog("finish");
  }, [canFinish, openDialog]);

  const onPressConfirmAsContractor = useCallback(async () => {
    if (!canConfirmAsContractor) return;
    try {
      setSubmitting(true);
      setErrorMessage(null);

      console.log("[JobRunAction] contractor confirm start pressed", {
        jobId,
        role,
      });

      console.log(
        "[JobRunAction] contractor subscribed to job-dispatch topic via WS",
        { jobId },
      );
    } catch {
      setErrorMessage(t("jobs.common.actionError"));
      console.error("[JobRunAction] contractor confirm crashed", {
        jobId,
      });
    } finally {
      setSubmitting(false);
    }
  }, [canConfirmAsContractor, jobId, t]);

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

      let shouldGoToMainAfter = false;

      if (dialogMode === "problem") {
        console.log("[JobRunAction] problem report submit", {
          jobId,
          role,
          descriptionLength: trimmed.length,
          hasPhoto: Boolean(photoUri),
        });

        await reportProblemTrue(jobId, {
          description: trimmed,
          photoUri: photoUri ?? undefined,
        });
        console.log("[JobRunAction] reportProblemTrue sent via REST API", {
          jobId,
        });
      } else if (dialogMode === "noProblem") {
        console.log("[JobRunAction] no-problem report submit", {
          jobId,
          role,
          descriptionLength: trimmed.length,
          hasPhoto: Boolean(photoUri),
        });

        await reportProblemFalse(jobId, {
          description: trimmed,
          photoUri: photoUri ?? undefined,
        });
        console.log("[JobRunAction] reportProblemFalse sent via REST API", {
          jobId,
        });
      } else {
        console.log("[JobRunAction] finish submit", {
          jobId,
          role,
          descriptionLength: trimmed.length,
          hasPhoto: Boolean(photoUri),
        });

        const response = await finishJob(jobId, {
          description: trimmed,
          photoUri: photoUri ?? undefined,
        });
        if (!response?.response?.ok) {
          setErrorMessage(
            response?.body?.message ?? t("jobs.common.actionError"),
          );
          console.warn("[JobRunAction] finishJob failed", {
            jobId,
            response: response?.body,
          });
          return;
        }

        if (role === "contractor") {
          await setContractorFinishedLocally(jobId);
          setContractorFinishSent(true);
          shouldGoToMainAfter = false;
        } else {
          try {
            await deleteJob(jobId);
          } catch {}

          await clearContractorFinishedLocally(jobId);
          shouldGoToMainAfter = true;
        }

        console.log("[JobRunAction] finishJob sent via REST API", {
          jobId,
          role,
        });
      }

      setDialogOpen(false);
      if (shouldGoToMainAfter) {
        navigation.reset({
          index: 0,
          routes: [{ name: "Main" as any }],
        });
        return;
      }

      await fetchJob();
    } catch {
      setErrorMessage(t("jobs.common.actionError"));
      console.error("[JobRunAction] submit dialog crashed", {
        jobId,
        dialogMode,
        role,
      });
    } finally {
      setSubmitting(false);
    }
  }, [description, dialogMode, fetchJob, jobId, navigation, photoUri, role, t]);

  const timerTitle = useMemo(() => {
    if (role === "owner") return t("jobs.run.timerTitleOwner");
    return t("jobs.run.timerTitleContractor");
  }, [role, t]);

  const timerValue = useMemo(() => formatDuration(elapsedMs), [elapsedMs]);

  const contractorNickname = useMemo(() => {
    const username = job?.contractor?.username;
    if (username) return username;
    const first = job?.contractor?.firstName;
    const last = job?.contractor?.lastName;
    const full = [first, last].filter(Boolean).join(" ");
    return full || t("jobs.details.contractor");
  }, [
    job?.contractor?.firstName,
    job?.contractor?.lastName,
    job?.contractor?.username,
    t,
  ]);

  if (loading) {
    return (
      <View style={[styles.center, { backgroundColor: colors.background }]}>
        <ActivityIndicator size="large" color={colors.primary} />
      </View>
    );
  }

  if (!job) {
    return (
      <SafeAreaView
        style={[styles.screen, { backgroundColor: colors.background }]}
      >
        <View style={styles.center}>
          <Text variant="titleMedium">
            {errorMessage ?? t("jobs.common.loadError")}
          </Text>
          <Button mode="contained" style={{ marginTop: 12 }} onPress={fetchJob}>
            {t("jobs.common.retry")}
          </Button>
          <Button
            mode="text"
            style={{ marginTop: 4 }}
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
      </SafeAreaView>
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
                {timerValue}
              </Text>

              {role === "contractor" && job.status === "READY" ? (
                <Text style={{ color: colors.onSurfaceVariant, marginTop: 4 }}>
                  {t("jobs.run.waitingForOwnerStart")}
                </Text>
              ) : null}

              <Text style={{ color: colors.onSurfaceVariant, marginTop: 4 }}>
                {t("jobs.run.noStartInfo")}
              </Text>

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
            {role === "owner" && isInProgress ? (
              <Text
                style={{ color: colors.onSurfaceVariant, marginVertical: 8 }}
              >
                {t("jobs.run.waitingForContractorConfirm")}
              </Text>
            ) : null}

            {/* {canConfirmAsContractor ? (
              <Button
                mode="contained"
                onPress={onPressConfirmAsContractor}
                loading={submitting}
                disabled={submitting}
              >
                {t("jobs.run.confirmStart")}
              </Button>
            ) : null} */}

            <Button
              mode="contained"
              onPress={() => openDialog("problem")}
              disabled={!canReport}
            >
              {t("jobs.run.reportProblem")}
            </Button>

            <Button
              mode="contained"
              buttonColor={colors.error}
              onPress={onPressFinish}
              disabled={!canFinish}
            >
              {t("jobs.run.finishJob")}
            </Button>

            {contractorFinishedInline ? (
              <Text style={{ color: colors.onSurfaceVariant }}>
                {t("jobs.run.contractorFinishedInline")}
              </Text>
            ) : null}

            {role === "contractor" &&
            contractorFinishSent &&
            !ownerFinalizedSeen ? (
              <Text style={{ color: colors.onSurfaceVariant }}>
                {t("jobs.run.finishSentWaitingForOwner")}
              </Text>
            ) : null}

            {role === "contractor" && ownerFinalizedSeen ? (
              <Text style={{ color: colors.onSurfaceVariant }}>
                {t("jobs.run.ownerFinalizedInline")}
              </Text>
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
                {dialogMode === "finish" && role === "contractor" ? (
                  <Text
                    style={{ color: colors.onSurfaceVariant, marginBottom: 10 }}
                  >
                    {t("jobs.run.contractorFinishLocalHint")}
                  </Text>
                ) : null}

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
                    style={{
                      marginTop: 10,
                      color: colors.onSurfaceVariant,
                    }}
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
