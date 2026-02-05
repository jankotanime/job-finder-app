import React, { useCallback, useEffect, useMemo, useState } from "react";
import { StyleSheet, View, Image, Dimensions } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { RouteProp, useNavigation, useRoute } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import AsyncStorage from "@react-native-async-storage/async-storage";
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
import { getJobById, startJob } from "../../api/jobs/handleJobApi";
import { buildPhotoUrl } from "../../utils/photoUrl";
import { setActiveJobTimer } from "../../utils/jobTimerStorage";

type JobDetailsRoute = RouteProp<RootStackParamList, "JobDetails">;

type Nav = NativeStackNavigationProp<RootStackParamList, "JobDetails">;

const START_KEY = (jobId: string) => `jobStart:${jobId}`;

const getJobFromPayload = (payload: any): Job | null => {
  const data = payload?.body?.data;
  if (data && typeof data === "object") return data as Job;
  if (payload?.body && typeof payload.body === "object")
    return payload.body as Job;
  return null;
};

const statusKey = (status: Job["status"]) => {
  switch (status) {
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
  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

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

  const canStart = useMemo(() => {
    return role === "contractor" && job?.status === "READY";
  }, [job?.status, role]);

  const onStart = useCallback(async () => {
    if (!job) return;
    try {
      setSubmitting(true);
      const startedAt = Date.now();
      const response = await startJob(job.id);
      await AsyncStorage.setItem(START_KEY(job.id), String(startedAt));
      await setActiveJobTimer({ jobId: job.id, role, startedAt });
      navigation.navigate("JobRun", { jobId: job.id, role, startedAt });
    } catch (e) {
      setErrorMessage(t("jobs.common.actionError"));
    } finally {
      setSubmitting(false);
    }
  }, [job, navigation, role, t]);

  const onOpenRun = useCallback(() => {
    navigation.navigate("JobRun", { jobId, role });
  }, [jobId, navigation, role]);

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

          <Text style={{ color: colors.onSurfaceVariant }}>
            {t("jobs.details.contractor")}
          </Text>
          <Text>
            {job.contractor?.firstName} {job.contractor?.lastName} (@
            {job.contractor?.username})
          </Text>
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
        ) : (
          <Button mode="contained" onPress={onOpenRun}>
            {t("jobs.details.startJob")}
          </Button>
        )}

        <Button mode="outlined" onPress={() => navigation.goBack()}>
          {t("jobs.common.back")}
        </Button>
      </View>
    </SafeAreaView>
  );
};

export default JobDetailsScreen;

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    paddingHorizontal: 16,
    paddingTop: 16,
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
