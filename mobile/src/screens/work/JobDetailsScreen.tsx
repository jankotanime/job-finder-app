import React, { useCallback, useEffect, useMemo, useState } from "react";
import {
  StyleSheet,
  View,
  Image,
  Dimensions,
  ScrollView,
  RefreshControl,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { RouteProp, useNavigation, useRoute } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { useTranslation } from "react-i18next";
import {
  ActivityIndicator,
  Button,
  Card,
  Divider,
  Text,
  useTheme,
} from "react-native-paper";

import type { RootStackParamList } from "../../types/RootStackParamList";
import type { Job } from "../../types/Job";
import {
  getJobById,
  getJobDispatcher,
  getJobsAsContractor,
  getJobsAsOwner,
  startJob,
} from "../../api/jobs/handleJobApi";
import { buildPhotoUrl } from "../../utils/photoUrl";

type JobDetailsRoute = RouteProp<RootStackParamList, "JobDetails">;

type Nav = NativeStackNavigationProp<RootStackParamList, "JobDetails">;

type Candidate = {
  id: string;
  firstName: string;
  lastName: string;
  username: string;
};

const toArray = <T,>(value: T | T[] | null | undefined): T[] => {
  if (!value) return [];
  return Array.isArray(value) ? value : [value];
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
  if (item.id == null) return null;
  return String(item.id);
};

const extractAcceptedCandidatesFromOwnerJob = (ownerJob: any): Candidate[] => {
  const chosenRaw = toArray(ownerJob?.contractor);
  const chosen = chosenRaw.filter((c): c is Candidate => Boolean(c));

  const appsRaw = toArray(ownerJob?.applications);
  const acceptedFromApps = appsRaw
    .filter((a: any) => {
      const status = typeof a?.status === "string" ? a.status : undefined;
      return (
        a?.accepted === true ||
        a?.isAccepted === true ||
        a?.chosen === true ||
        a?.isChosen === true ||
        status === "ACCEPTED" ||
        status === "CHOSEN" ||
        status === "SELECTED"
      );
    })
    .filter((c): c is Candidate => Boolean(c));

  const combined = (chosen.length ? chosen : acceptedFromApps).filter(Boolean);
  const dedup = new Map<string, Candidate>();
  for (const c of combined) {
    const key =
      c.username ?? c.id ?? `${c.firstName ?? ""}:${c.lastName ?? ""}`;
    if (!dedup.has(key)) dedup.set(key, c);
  }
  return [...dedup.values()];
};

const getJobFromPayload = (payload: any): Job | null => {
  const data = payload?.body?.data;
  if (data && typeof data === "object") return data as Job;
  if (payload?.body && typeof payload.body === "object")
    return payload.body as Job;
  return null;
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

const statusKey = (status: Job["status"]) => {
  switch (status) {
    case "UNREADY":
      return "jobs.details.status.unReady";
    case "READY":
      return "jobs.details.status.ready";
    case "IN_PROGRESS":
      return "jobs.details.status.inProgress";
    case "FINISHED_SUCCESS":
      return "jobs.details.status.finishedSuccess";
    case "FINISHED_FAILURE":
      return "jobs.details.status.finishedFailure";
    default:
      return "jobs.details.status.unknown";
  }
};

const JobDetailsScreen = () => {
  const { colors } = useTheme();
  const { t } = useTranslation();
  const imageHeight = useMemo(
    () => Math.round(Dimensions.get("window").width * 0.55),
    [],
  );

  const route = useRoute<JobDetailsRoute>();
  const navigation = useNavigation<Nav>();
  const { jobId, role } = route.params;

  const [job, setJob] = useState<Job | null>(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [acceptedCandidates, setAcceptedCandidates] = useState<Candidate[]>([]);
  const [acceptedLoading, setAcceptedLoading] = useState(false);

  const fetchJob = useCallback(async () => {
    setErrorMessage(null);
    setAcceptedCandidates([]);

    const shouldLoadAcceptedOwner = role === "owner";
    const shouldLoadAcceptedContractor = role === "contractor";
    const shouldLoadAccepted =
      shouldLoadAcceptedOwner || shouldLoadAcceptedContractor;
    setAcceptedLoading(shouldLoadAccepted);

    const jobRes = await getJobById(jobId);
    const jobsRes = shouldLoadAcceptedOwner
      ? await getJobsAsOwner()
      : shouldLoadAcceptedContractor
        ? await getJobsAsContractor()
        : null;

    const jobs = jobsRes ? getJobsArrayFromPayload(jobsRes) : [];
    const matchingFromList = jobs.find((j: any) => {
      const candidateId = getIdFromListItem(j);
      return candidateId != null && candidateId === String(jobId);
    });
    const fallbackJobFromList = matchingFromList
      ? getJobFromListItem(matchingFromList)
      : null;

    if (!jobRes?.response?.ok) {
      const code = jobRes?.body?.code;
      if (code === "USER_NOT_CONTRACTOR_OR_OWNER") {
        if (fallbackJobFromList) {
          if (shouldLoadAcceptedOwner && matchingFromList) {
            setAcceptedCandidates(
              extractAcceptedCandidatesFromOwnerJob(matchingFromList),
            );
          }
          setJob(fallbackJobFromList);
          setAcceptedLoading(false);
          setErrorMessage(null);
          return;
        }

        setJob(null);
        setAcceptedLoading(false);
        setErrorMessage(t("jobs.common.noAccess"));
        return;
      }

      setJob(null);
      setAcceptedLoading(false);
      setErrorMessage(jobRes?.body?.message ?? t("jobs.common.loadError"));
      return;
    }

    const parsed = getJobFromPayload(jobRes);
    if (!parsed) throw new Error("Invalid job payload");

    let finalJob: Job = parsed;

    if (shouldLoadAccepted && jobsRes) {
      if (matchingFromList) {
        if (shouldLoadAcceptedOwner) {
          setAcceptedCandidates(
            extractAcceptedCandidatesFromOwnerJob(matchingFromList),
          );
        }

        if (shouldLoadAcceptedContractor) {
          const contractorCandidate =
            matchingFromList?.contractor ??
            matchingFromList?.job?.contractor ??
            matchingFromList?.offer?.chosenCandidate ??
            matchingFromList?.offer?.contractor;

          if (contractorCandidate && !finalJob?.contractor) {
            finalJob = {
              ...finalJob,
              contractor: contractorCandidate,
            };
          }
        }
      }
    }
    setJob(finalJob);

    setAcceptedLoading(false);
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
    const unsubscribe = navigation.addListener("focus", () => {
      fetchJob().catch(() => {
        setErrorMessage(t("jobs.common.loadError"));
      });
    });
    return unsubscribe;
  }, [fetchJob, navigation, t]);

  const canStart = useMemo(() => {
    return role === "owner" && job?.status === "READY";
  }, [job?.status, role]);

  const canStartContractor = useMemo(() => {
    return role === "contractor" && job?.status === "IN_PROGRESS";
  }, [job?.status, role]);

  const onStart = useCallback(async () => {
    if (!job) return;
    try {
      setSubmitting(true);
      setErrorMessage(null);
      const response = await startJob(job.id);
      if (!response?.response?.ok) {
        setErrorMessage(
          response?.body?.message ?? t("jobs.common.actionError"),
        );
        return;
      }
      const jobDispatcherId = response.body.data.jobDispatcherId;
      if (!jobDispatcherId) {
        setErrorMessage(t("jobs.common.actionError"));
        return;
      }
      navigation.navigate("JobRun", {
        jobId: job.id,
        jobDispatcherId,
        role,
      });
    } catch {
      setErrorMessage(t("jobs.common.actionError"));
    } finally {
      setSubmitting(false);
    }
  }, [job, navigation, role, t]);

  const onStartAsContractor = useCallback(async () => {
    if (!job) return;
    if (!canStartContractor) return;
    try {
      setSubmitting(true);
      setErrorMessage(null);

      console.log("[JobDetailsAction] contractor confirm start pressed", {
        jobId: job.id,
        role,
      });

      const dispatcherRes = await getJobDispatcher(job.id);
      if (!dispatcherRes?.response?.ok) {
        console.warn("[JobDetailsAction] getJobDispatcher check failed", {
          jobId: job.id,
          response: dispatcherRes?.body,
        });
        setErrorMessage(t("jobs.common.actionError"));
        return;
      }

      const jobDispatcherId = dispatcherRes.body.data.jobDispatcherId;
      if (!jobDispatcherId) {
        console.error(
          "[JobDetailsAction] no jobDispatcherId from getJobDispatcher",
          {
            jobId: job.id,
            response: dispatcherRes?.body,
          },
        );
        setErrorMessage(t("jobs.common.actionError"));
        return;
      }

      console.log("[JobDetailsAction] contractor navigating to JobRun", {
        jobId: job.id,
        jobDispatcherId,
      });

      navigation.navigate("JobRun", {
        jobId: job.id,
        jobDispatcherId,
        role: "contractor",
      });
    } catch (err) {
      setErrorMessage(t("jobs.common.actionError"));
      console.error("[JobDetailsAction] contractor confirm crashed", {
        jobId: job?.id,
        role,
        error: err,
      });
    } finally {
      setSubmitting(false);
    }
  }, [canStartContractor, job, navigation, role, t]);

  const onRefresh = useCallback(async () => {
    try {
      setRefreshing(true);
      await fetchJob();
    } catch {
      setErrorMessage(t("jobs.common.loadError"));
    } finally {
      setRefreshing(false);
    }
  }, [fetchJob, t]);

  const onOpenRun = useCallback(async () => {
    if (!job) return;
    try {
      setSubmitting(true);
      setErrorMessage(null);

      console.log("[JobDetailsAction] open run pressed", {
        jobId: job.id,
        role,
      });

      const response = await startJob(jobId);
      if (!response?.response?.ok) {
        setErrorMessage(response.body.message ?? t("jobs.common.actionError"));
        console.warn("[JobDetailsAction] startJob failed", {
          jobId: job.id,
          response: response?.body,
        });
        return;
      }

      const jobDispatcherId = response?.body?.data?.jobDispatcherId;
      if (!jobDispatcherId) {
        setErrorMessage(t("jobs.common.actionError"));
        console.error("[JobDetailsAction] no jobDispatcherId in response", {
          jobId: job.id,
          response: response?.body,
        });
        return;
      }

      console.log("[JobDetailsAction] startJob API success", {
        jobId: job.id,
        jobDispatcherId,
      });

      navigation.navigate("JobRun", {
        jobId: job.id,
        jobDispatcherId,
        role,
      });
    } catch (err) {
      setErrorMessage(t("jobs.common.actionError"));
      console.error("[JobDetailsAction] onOpenRun crash", {
        jobId: job?.id,
        role,
        error: err,
      });
    } finally {
      setSubmitting(false);
    }
  }, [job, jobId, navigation, role, t]);

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
            onPress={() => navigation.goBack()}
          >
            {t("jobs.common.back")}
          </Button>
        </View>
      </SafeAreaView>
    );
  }

  const photoUri = buildPhotoUrl(job?.photo?.storageKey ?? undefined);
  return (
    <SafeAreaView
      style={[styles.screen, { backgroundColor: colors.background }]}
    >
      <ScrollView
        showsVerticalScrollIndicator={false}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            tintColor={colors.primary}
            colors={[colors.primary]}
          />
        }
        contentContainerStyle={styles.scrollContent}
      >
        <Text variant="headlineSmall" style={styles.header}>
          {t("jobs.details.title")}
        </Text>
        {errorMessage ? (
          <Text style={{ color: colors.error, marginBottom: 8 }}>
            {errorMessage}
          </Text>
        ) : null}
        {photoUri ? (
          <Image
            source={{ uri: photoUri }}
            style={[styles.image, { height: imageHeight }]}
          />
        ) : (
          <View
            style={[
              styles.image,
              { height: imageHeight, backgroundColor: colors.surfaceVariant },
            ]}
          />
        )}
        <Card style={[styles.card, { backgroundColor: colors.surface }]}>
          <Card.Content>
            <Text variant="titleLarge" style={{ fontWeight: "800" }}>
              {job.title}
            </Text>
            <Text style={{ color: colors.onSurfaceVariant, marginTop: 8 }}>
              {job.description}
            </Text>
            <Divider style={{ marginVertical: 14 }} />
            <Text variant="titleMedium" style={{ fontWeight: "700" }}>
              {t("jobs.details.statusLabel")}
            </Text>
            <Text style={{ marginTop: 4 }}>{t(statusKey(job.status))}</Text>
          </Card.Content>
        </Card>
        <Card style={[styles.card, { backgroundColor: colors.surface }]}>
          <Card.Content>
            <Text variant="titleMedium" style={{ fontWeight: "700" }}>
              {t("jobs.details.acceptedApplicants")}
            </Text>
            <Text style={{ color: colors.onSurfaceVariant, marginTop: 6 }}>
              {t("jobs.details.acceptedApplicantsHint")}
            </Text>
            <Divider style={{ marginVertical: 12 }} />
            {role === "owner" ? (
              acceptedLoading ? (
                <ActivityIndicator size="small" color={colors.primary} />
              ) : acceptedCandidates.length ? (
                acceptedCandidates.map((c, idx) => (
                  <Text key={`${c.username ?? c.id ?? idx}`}>
                    {c.firstName ?? ""} {c.lastName ?? ""}
                    {c.username ? ` (@${c.username})` : ""}
                  </Text>
                ))
              ) : (
                <Text style={{ color: colors.onSurfaceVariant }}>
                  {t("jobs.details.noAcceptedApplicants")}
                </Text>
              )
            ) : (
              <>
                <Text style={{ color: colors.onSurfaceVariant }}>
                  {t("jobs.details.contractor")}
                </Text>
                <Text>
                  {job.contractor?.firstName} {job.contractor?.lastName} (@
                  {job.contractor?.username})
                </Text>
              </>
            )}
          </Card.Content>
        </Card>
        <View style={styles.actions}>
          {canStart ? (
            <Button
              mode="contained"
              onPress={onStart}
              loading={submitting}
              disabled={submitting}
            >
              {t("jobs.details.startJob")}
            </Button>
          ) : role === "contractor" && job.status === "UNREADY" ? (
            <Button mode="contained" disabled>
              {t("jobs.details.waitForOwnerStart")}
            </Button>
          ) : canStartContractor ? (
            <Button
              mode="contained"
              onPress={onStartAsContractor}
              loading={submitting}
              disabled={submitting}
            >
              {t("jobs.details.startJob")}
            </Button>
          ) : (
            <Button mode="contained" onPress={onOpenRun}>
              {t("jobs.details.startJob")}
            </Button>
          )}
          <Button mode="outlined" onPress={() => navigation.goBack()}>
            {t("jobs.common.back")}
          </Button>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

export default JobDetailsScreen;

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    paddingHorizontal: 16,
    paddingTop: 16,
  },
  scrollContent: {
    paddingBottom: 24,
    gap: 12,
  },
  header: {
    fontWeight: "700",
  },
  image: {
    width: "100%",
    borderRadius: 14,
  },
  card: {
    borderRadius: 14,
  },
  actions: {
    marginTop: 4,
    gap: 10,
  },
  center: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: 16,
  },
});
