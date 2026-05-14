import React, { useCallback, useEffect, useMemo, useState } from "react";
import {
  Image,
  Keyboard,
  KeyboardAvoidingView,
  Platform,
  Pressable,
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
import { useJobRunTimer } from "../../hooks/useJobRunTimer";
import {
  type JobWebSocketMessage,
  useWebSockets,
} from "../../hooks/useWebSocket";
import {
  dispatcherIndicatesStarted,
  formatDuration,
  getDispatcherFromPayload,
  getIdFromListItem,
  getJobFromListItem,
  getJobFromPayload,
  getJobsArrayFromPayload,
} from "../../utils/jobHelpers";

type JobRunRoute = RouteProp<RootStackParamList, "JobRun">;

type Nav = NativeStackNavigationProp<RootStackParamList, "JobRun">;

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
  const { elapsedMs, handleSignal: handleTimerSignal } = useJobRunTimer(
    job?.status,
  );

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

  const handleWebSocketMessage = useCallback(
    (message: JobWebSocketMessage) => {
      if (message.signalType === "JOB_START") {
        setJob((prev) =>
          prev && prev.status !== "IN_PROGRESS"
            ? ({ ...prev, status: "IN_PROGRESS" } as Job)
            : prev,
        );
      }
      if (message.signalType === "JOB_END_SUCCESSFULLY") {
        setJob((prev) =>
          prev && prev.status !== "FINISHED_SUCCESS"
            ? ({ ...prev, status: "FINISHED_SUCCESS" } as Job)
            : prev,
        );
      }
      if (message.signalType === "JOB_END_UNSUCCESSFULLY") {
        setJob((prev) =>
          prev && prev.status !== "FINISHED_FAILURE"
            ? ({ ...prev, status: "FINISHED_FAILURE" } as Job)
            : prev,
        );
      }

      handleTimerSignal(message);
    },
    [handleTimerSignal],
  );

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
        console.log("error Message: ", errorMessage);
        console.log("jobId: ", jobId);
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
    if (!loading && !job && errorMessage) {
      const timer = setTimeout(() => {
        navigation.reset({
          index: 0,
          routes: [{ name: "Main" as any }],
        });
      }, 500);
      return () => clearTimeout(timer);
    }
  }, [loading, job, errorMessage, navigation]);

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
  }, [jobId]);

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
        const reportProblemtrue = await reportProblemTrue(jobId, {
          description: trimmed,
          photoUri: photoUri ?? undefined,
        });
        console.log("report problem true: ", reportProblemtrue);
      } else if (dialogMode === "noProblem") {
        const reportProblemfalse = await reportProblemFalse(jobId, {
          description: trimmed,
          photoUri: photoUri ?? undefined,
        });
        console.log("report problem false: ", reportProblemfalse);
      } else {
        const response = await finishJob(jobId, {
          description: trimmed,
          photoUri: photoUri ?? undefined,
        });
        if (!response?.response?.ok) {
          setErrorMessage(
            response?.body?.message ?? t("jobs.common.actionError"),
          );
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
    } finally {
      setSubmitting(false);
    }
  }, [description, dialogMode, fetchJob, jobId, navigation, photoUri, role, t]);

  const timerTitle = useMemo(() => {
    if (role === "owner") return t("jobs.run.timerTitleOwner");
    return t("jobs.run.timerTitleContractor");
  }, [role, t]);

  const timerValue = useMemo(() => formatDuration(elapsedMs), [elapsedMs]);

  const statusConfig = useMemo(() => {
    const s = job?.status;
    if (s === "IN_PROGRESS") {
      return {
        label: t("jobs.details.status.inProgress"),
        bgColor: colors.primaryContainer,
        textColor: colors.onPrimaryContainer,
      };
    }
    if (s === "READY") {
      return {
        label: t("jobs.details.status.ready"),
        bgColor: colors.secondaryContainer,
        textColor: colors.onSecondaryContainer,
      };
    }
    if (s === "FINISHED_SUCCESS") {
      return {
        label: t("jobs.details.status.finishedSuccess"),
        bgColor: colors.primaryContainer,
        textColor: colors.onPrimaryContainer,
      };
    }
    if (s === "FINISHED_FAILURE") {
      return {
        label: t("jobs.details.status.finishedFailure"),
        bgColor: colors.errorContainer,
        textColor: colors.onErrorContainer,
      };
    }
    return {
      label: t("jobs.details.status.unknown"),
      bgColor: colors.surfaceVariant,
      textColor: colors.onSurfaceVariant,
    };
  }, [job?.status, colors, t]);

  const dialogIcon = useMemo(() => {
    if (dialogMode === "finish") return "flag-checkered";
    if (dialogMode === "noProblem") return "check-circle-outline";
    return "alert-circle-outline";
  }, [dialogMode]);

  if (loading || !job) {
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
          onScrollBeginDrag={Keyboard.dismiss}
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
        >
          <Text variant="headlineSmall" style={styles.header}>
            {t("jobs.run.title")}
          </Text>

          {errorMessage ? (
            <View
              style={[
                styles.messageBanner,
                { backgroundColor: colors.errorContainer },
              ]}
            >
              <Text style={{ color: colors.onErrorContainer }}>
                {errorMessage}
              </Text>
            </View>
          ) : null}
          <Card style={[styles.card, { backgroundColor: colors.surface }]}>
            <Card.Content style={styles.timerCardContent}>
              <View
                style={[
                  styles.statusBadge,
                  { backgroundColor: statusConfig.bgColor },
                ]}
              >
                <Text
                  style={[
                    styles.statusBadgeText,
                    { color: statusConfig.textColor },
                  ]}
                >
                  {statusConfig.label}
                </Text>
              </View>
              <Text
                style={[styles.timerLabel, { color: colors.onSurfaceVariant }]}
              >
                {timerTitle}
              </Text>
              <Text style={[styles.timer, { color: colors.primary }]}>
                {timerValue}
              </Text>
              {role === "contractor" && job.status === "READY" ? (
                <Text style={{ color: colors.onSurfaceVariant, marginTop: 2 }}>
                  {t("jobs.run.waitingForOwnerStart")}
                </Text>
              ) : null}
            </Card.Content>
          </Card>
          <Card style={[styles.card, { backgroundColor: colors.surface }]}>
            <Card.Content>
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
          {contractorFinishedInline ? (
            <View
              style={[
                styles.messageBanner,
                { backgroundColor: colors.primaryContainer },
              ]}
            >
              <Text style={{ color: colors.onPrimaryContainer }}>
                {t("jobs.run.contractorFinishedInline")}
              </Text>
            </View>
          ) : null}
          {role === "contractor" &&
          contractorFinishSent &&
          !ownerFinalizedSeen ? (
            <View
              style={[
                styles.messageBanner,
                { backgroundColor: colors.secondaryContainer },
              ]}
            >
              <Text style={{ color: colors.onSecondaryContainer }}>
                {t("jobs.run.finishSentWaitingForOwner")}
              </Text>
            </View>
          ) : null}

          {role === "contractor" && ownerFinalizedSeen ? (
            <View
              style={[
                styles.messageBanner,
                { backgroundColor: colors.primaryContainer },
              ]}
            >
              <Text style={{ color: colors.onPrimaryContainer }}>
                {t("jobs.run.ownerFinalizedInline")}
              </Text>
            </View>
          ) : null}
          <Card style={[styles.card, { backgroundColor: colors.surface }]}>
            <Card.Content style={styles.actionsContent}>
              <Button
                mode="contained-tonal"
                icon="alert-circle-outline"
                onPress={() => openDialog("problem")}
                disabled={!canReport}
                style={styles.actionButton}
                contentStyle={styles.actionButtonContent}
              >
                {t("jobs.run.reportProblem")}
              </Button>

              <Button
                mode="contained"
                icon="flag-checkered"
                buttonColor={colors.error}
                onPress={onPressFinish}
                disabled={!canFinish}
                style={styles.actionButton}
                contentStyle={styles.actionButtonContent}
              >
                {t("jobs.run.finishJob")}
              </Button>
            </Card.Content>
          </Card>
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
        </ScrollView>
      </KeyboardAvoidingView>
      <Portal>
        <Dialog
          visible={dialogOpen}
          onDismiss={Keyboard.dismiss}
          style={{ maxHeight: Math.round(windowHeight * 0.85) }}
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
                keyboardShouldPersistTaps="always"
                showsVerticalScrollIndicator={false}
                contentContainerStyle={{ paddingBottom: 6 }}
              >
                <Pressable onPress={Keyboard.dismiss}>
                  {dialogMode === "finish" && role === "contractor" ? (
                    <Text
                      style={{
                        color: colors.onSurfaceVariant,
                        marginBottom: 12,
                      }}
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
                    returnKeyType="done"
                    style={styles.dialogTextInput}
                  />
                  <View style={styles.photoButtons}>
                    <Button
                      mode="outlined"
                      icon="camera"
                      onPress={pickFromCamera}
                      style={styles.photoButton}
                      contentStyle={styles.photoButtonContent}
                    >
                      {t("jobs.run.photoCamera")}
                    </Button>
                    <Button
                      mode="outlined"
                      icon="image"
                      onPress={pickFromGallery}
                      style={styles.photoButton}
                      contentStyle={styles.photoButtonContent}
                    >
                      {t("jobs.run.photoGallery")}
                    </Button>
                  </View>
                  {photoUri ? (
                    <View style={styles.photoPreviewContainer}>
                      <Image
                        source={{ uri: photoUri }}
                        style={styles.photoPreview}
                        resizeMode="cover"
                      />
                    </View>
                  ) : null}
                </Pressable>
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
  },
  scrollContent: {
    flexGrow: 1,
    paddingBottom: 32,
    gap: 12,
  },
  header: {
    fontWeight: "700",
    marginBottom: 4,
  },
  card: {
    borderRadius: 16,
  },
  timerCardContent: {
    gap: 4,
    paddingVertical: 4,
  },
  statusBadge: {
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 10,
    marginBottom: 8,
    alignItems: "center",
  },
  statusBadgeText: {
    fontSize: 12,
    fontWeight: "600",
  },
  timerLabel: {
    fontSize: 13,
  },
  timer: {
    fontSize: 52,
    fontWeight: "900",
    letterSpacing: 2,
    marginVertical: 2,
  },
  messageBanner: {
    borderRadius: 12,
    paddingVertical: 12,
    paddingHorizontal: 16,
  },
  actionsContent: {
    gap: 10,
  },
  actionButton: {
    borderRadius: 10,
  },
  actionButtonContent: {
    paddingVertical: 4,
  },
  dialogTextInput: {
    marginBottom: 14,
  },
  photoButtons: {
    flexDirection: "row",
    gap: 10,
  },
  photoButton: {
    flex: 1,
    borderRadius: 10,
  },
  photoButtonContent: {
    paddingVertical: 2,
  },
  photoPreviewContainer: {
    marginTop: 14,
    borderRadius: 12,
    overflow: "hidden",
  },
  photoPreview: {
    width: "100%",
    height: 180,
  },
  center: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: 16,
  },
});
