import { useCallback, useEffect, useState } from "react";

import type { JobWebSocketMessage } from "./useWebSocket";
import type { Job } from "../types/Job";

type TimerSignal = Pick<
  JobWebSocketMessage,
  "signalType" | "time" | "timePassedMilisecods"
>;

const computeStartedAtMs = (message: TimerSignal) => {
  const serverTimeMs = message.time ? Date.parse(message.time) : NaN;
  const elapsedFromServer = Number(message.timePassedMilisecods ?? 0);

  return Number.isFinite(serverTimeMs)
    ? serverTimeMs - elapsedFromServer
    : Date.now() - elapsedFromServer;
};

export const useJobRunTimer = (jobStatus: Job["status"] | null | undefined) => {
  const [elapsedMs, setElapsedMs] = useState<number | null>(null);
  const [startedAtMs, setStartedAtMs] = useState<number | null>(null);

  const resetTimer = useCallback(() => {
    setStartedAtMs(null);
    setElapsedMs(null);
  }, []);

  const handleSignal = useCallback(
    (message: TimerSignal) => {
      if (message.signalType === "JOB_START") {
        const startedAt = computeStartedAtMs(message);
        setStartedAtMs(startedAt);
        setElapsedMs(Math.max(0, Date.now() - startedAt));
        return;
      }

      if (
        message.signalType === "JOB_END_SUCCESSFULLY" ||
        message.signalType === "JOB_END_UNSUCCESSFULLY"
      ) {
        resetTimer();
      }
    },
    [resetTimer],
  );

  useEffect(() => {
    if (jobStatus !== "IN_PROGRESS") {
      resetTimer();
      return;
    }

    if (startedAtMs === null) {
      setStartedAtMs(Date.now());
    }
  }, [jobStatus, resetTimer, startedAtMs]);

  useEffect(() => {
    if (jobStatus !== "IN_PROGRESS" || startedAtMs == null) {
      return;
    }

    const tick = () => {
      setElapsedMs(Math.max(0, Date.now() - startedAtMs));
    };

    tick();
    const interval = setInterval(tick, 1000);
    return () => clearInterval(interval);
  }, [jobStatus, startedAtMs]);

  return {
    elapsedMs,
    handleSignal,
    resetTimer,
  };
};
