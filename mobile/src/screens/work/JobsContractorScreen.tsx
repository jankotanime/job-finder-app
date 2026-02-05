import React, { useCallback, useEffect, useMemo, useState } from "react";
import {
  FlatList,
  RefreshControl,
  StyleSheet,
  View,
  Dimensions,
} from "react-native";
import { useTranslation } from "react-i18next";
import { Card, Text, useTheme, ActivityIndicator } from "react-native-paper";
import { getJobsAsContractor } from "../../api/jobs/handleJobApi";
import type { Job } from "../../types/Job";
import { SafeAreaView } from "react-native-safe-area-context";

const toJobsArray = (payload: any): Job[] => {
  const data = payload?.body?.data;
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.content)) return data.content;
  if (Array.isArray(payload?.body)) return payload.body;
  return [];
};

const { width, height } = Dimensions.get("window");
const JobsContractorScreen = () => {
  const { colors } = useTheme();
  const { t } = useTranslation();

  const [jobs, setJobs] = useState<Job[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const fetchJobs = useCallback(async () => {
    setErrorMessage(null);
    const res = await getJobsAsContractor();
    setJobs(toJobsArray(res));
  }, []);

  useEffect(() => {
    (async () => {
      try {
        setLoading(true);
        await fetchJobs();
      } catch (e) {
        setErrorMessage(t("jobs.common.loadError"));
      } finally {
        setLoading(false);
      }
    })();
  }, [fetchJobs, t]);

  const onRefresh = useCallback(async () => {
    try {
      setRefreshing(true);
      await fetchJobs();
    } catch {
      setErrorMessage(t("jobs.common.loadError"));
    } finally {
      setRefreshing(false);
    }
  }, [fetchJobs, t]);

  const empty = useMemo(() => {
    if (loading) return null;
    if (errorMessage) {
      return (
        <View style={styles.center}>
          <Text variant="titleMedium">{errorMessage}</Text>
        </View>
      );
    }
    return (
      <View style={styles.center}>
        <Text variant="titleMedium">{t("jobs.contractor.empty")}</Text>
      </View>
    );
  }, [errorMessage, loading, t]);

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
      <Text variant="headlineSmall" style={styles.header}>
        {t("jobs.contractor.title")}
      </Text>

      <FlatList
        data={jobs}
        keyExtractor={(item, idx) => `${item?.title ?? "job"}-${idx}`}
        contentContainerStyle={styles.listContent}
        ListEmptyComponent={empty}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            tintColor={colors.primary}
            colors={[colors.primary]}
          />
        }
        renderItem={({ item }) => (
          <Card style={[styles.card, { backgroundColor: colors.surface }]}>
            <Card.Content>
              <Text variant="titleMedium">{item.title}</Text>
              <Text
                variant="bodySmall"
                style={{ color: colors.onSurfaceVariant, marginTop: 6 }}
              >
                {item.description}
              </Text>
            </Card.Content>
          </Card>
        )}
      />
    </SafeAreaView>
  );
};

export default JobsContractorScreen;

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    paddingHorizontal: 16,
    paddingTop: 16,
  },
  header: {
    fontWeight: "700",
    marginBottom: 12,
  },
  listContent: {
    paddingBottom: 24,
    gap: 12,
  },
  card: {
    borderRadius: 14,
    width: width * 0.9,
    marginLeft: 5,
    marginRight: 5,
  },
  center: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: 16,
  },
});
