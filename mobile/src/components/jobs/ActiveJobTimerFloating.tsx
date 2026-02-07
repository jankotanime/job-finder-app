import React, {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";
import { Animated, Easing, Pressable, StyleSheet, View } from "react-native";
import { useFocusEffect, useNavigation } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { IconButton, Text, useTheme } from "react-native-paper";
import { useTranslation } from "react-i18next";

import type { RootStackParamList } from "../../types/RootStackParamList";
import {
  type ActiveJobTimer,
  getActiveJobTimer,
} from "../../utils/jobTimerStorage";

const pad2 = (n: number) => String(n).padStart(2, "0");

const formatElapsed = (ms: number) => {
  const totalSeconds = Math.max(0, Math.floor(ms / 1000));
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;
  return `${pad2(hours)}:${pad2(minutes)}:${pad2(seconds)}`;
};

const CLOSED_SIZE = 62;
const OPEN_WIDTH = 240;
const HEIGHT = 60;

const ActiveJobTimerFloating = () => {
  const { colors } = useTheme();
  const { t } = useTranslation();
  const navigation =
    useNavigation<NativeStackNavigationProp<RootStackParamList>>();

  const [active, setActive] = useState<ActiveJobTimer | null>(null);
  const [now, setNow] = useState<number>(() => Date.now());
  const [open, setOpen] = useState(false);

  const widthAnim = useRef(new Animated.Value(0)).current; // layout (no native driver)
  const contentAnim = useRef(new Animated.Value(0)).current; // opacity/transform (native driver)

  const refresh = useCallback(async () => {
    try {
      const next = await getActiveJobTimer();
      setActive(next);
      if (!next) setOpen(false);
    } catch {
      setActive(null);
      setOpen(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      refresh();
      return () => {};
    }, [refresh]),
  );

  useEffect(() => {
    if (!active?.startedAt) return;
    setNow(Date.now());
    const id = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(id);
  }, [active?.startedAt]);

  useEffect(() => {
    const widthTiming = Animated.timing(widthAnim, {
      toValue: open ? 1 : 0,
      duration: 240,
      easing: Easing.out(Easing.cubic),
      useNativeDriver: false,
    });

    if (open) {
      Animated.parallel([
        widthTiming,
        Animated.timing(contentAnim, {
          toValue: 1,
          duration: 260,
          delay: 90,
          easing: Easing.out(Easing.cubic),
          useNativeDriver: true,
        }),
      ]).start();
      return;
    }

    Animated.sequence([
      Animated.timing(contentAnim, {
        toValue: 0,
        duration: 140,
        easing: Easing.in(Easing.quad),
        useNativeDriver: true,
      }),
      widthTiming,
    ]).start();
  }, [contentAnim, open, widthAnim]);

  const elapsedMs = useMemo(() => {
    if (!active?.startedAt) return 0;
    return now - active.startedAt;
  }, [active?.startedAt, now]);

  const width = widthAnim.interpolate({
    inputRange: [0, 1],
    outputRange: [CLOSED_SIZE, OPEN_WIDTH],
  });

  const contentOpacity = contentAnim.interpolate({
    inputRange: [0, 1],
    outputRange: [0, 1],
  });

  const contentTranslateX = contentAnim.interpolate({
    inputRange: [0, 1],
    outputRange: [14, 0],
  });

  const contentScale = contentAnim.interpolate({
    inputRange: [0, 1],
    outputRange: [0.98, 1],
  });

  const timerTextOpacity = contentAnim.interpolate({
    inputRange: [0, 0.5, 1],
    outputRange: [0, 0.75, 1],
  });

  const openJob = useCallback(() => {
    if (!active) return;
    navigation.navigate("JobRun", {
      jobId: active.jobId,
      role: active.role,
      startedAt: active.startedAt,
    });
  }, [active, navigation]);

  const toggle = useCallback(() => {
    if (!active) return;
    setOpen((v) => !v);
  }, [active]);

  if (!active) return null;

  return (
    <View pointerEvents="box-none" style={styles.root}>
      <Animated.View
        style={[
          styles.container,
          {
            width,
            height: HEIGHT,
            borderColor: colors.primary,
            backgroundColor: colors.primary,
          },
        ]}
      >
        <Pressable
          onPress={toggle}
          style={[styles.closedButton, { width: CLOSED_SIZE, height: HEIGHT }]}
          accessibilityRole="button"
          accessibilityLabel={t("jobs.timerBanner.title")}
        >
          <IconButton
            icon={open ? "close" : "clock-outline"}
            iconColor="white"
            size={26}
            style={styles.button}
          />
        </Pressable>

        <Animated.View
          style={[
            styles.content,
            {
              opacity: contentOpacity,
              transform: [
                { translateX: contentTranslateX },
                { scale: contentScale },
              ],
            },
          ]}
          pointerEvents={open ? "auto" : "none"}
        >
          <View style={{ flex: 1 }}>
            <Text style={{ color: "white", fontSize: 12, opacity: 0.9 }}>
              {t("jobs.timerBanner.title")}
            </Text>
            <Animated.Text
              style={{
                color: "white",
                fontWeight: "900",
                opacity: timerTextOpacity,
              }}
            >
              {formatElapsed(elapsedMs)}
            </Animated.Text>
          </View>

          <Pressable
            onPress={openJob}
            style={({ pressed }) => [
              styles.cta,
              {
                backgroundColor: pressed
                  ? "rgba(255,255,255,0.22)"
                  : "rgba(255,255,255,0.16)",
              },
            ]}
            accessibilityRole="button"
            accessibilityLabel={t("jobs.timerBanner.cta")}
          >
            <Text style={{ color: "white", fontWeight: "800" }}>
              {t("jobs.timerBanner.cta")}
            </Text>
          </Pressable>
        </Animated.View>
      </Animated.View>
    </View>
  );
};

export default ActiveJobTimerFloating;

const styles = StyleSheet.create({
  root: {
    position: "absolute",
    right: -15,
    top: "50%",
    transform: [{ translateY: -HEIGHT / 2 }],
    zIndex: 100,
    elevation: 10,
  },
  container: {
    flexDirection: "row",
    alignItems: "center",
    borderRadius: 14,
    borderWidth: 1,
    overflow: "hidden",
  },
  closedButton: {
    justifyContent: "center",
    alignItems: "center",
  },
  content: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    paddingRight: 10,
    paddingLeft: 2,
  },
  cta: {
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 10,
    marginRight: 10,
  },
  button: {
    marginRight: 15,
  },
});
