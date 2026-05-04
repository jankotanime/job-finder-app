import type { Client, StompSubscription } from "@stomp/stompjs";
import { useEffect, useRef } from "react";
import { createWsClient } from "../api/websocketClient";

export type JobWebSocketMessage = {
  signalType?: string;
  timePassedMilisecods?: number;
  time?: string;
};

type UseWebSocketsOptions = {
  onMessage?: (message: JobWebSocketMessage) => void;
};

export const useWebSockets = (
  jobDispatcherId: string,
  options: UseWebSocketsOptions = {},
) => {
  const subscriptionRef = useRef<StompSubscription | null>(null);
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    let cancelled = false;
    const topicDestination = `/job-dispatch/${jobDispatcherId}`;
    const subscribeDestination = `/app/job-dispatch/${jobDispatcherId}/subscribe`;
    const wsClient = createWsClient();
    clientRef.current = wsClient;

    const setupConnection = async () => {
      if (cancelled) return;

      wsClient.onConnect = () => {
        if (subscriptionRef.current) {
          subscriptionRef.current.unsubscribe();
        }

        subscriptionRef.current = wsClient.subscribe(
          topicDestination,
          (message) => {
            let parsedMessage: JobWebSocketMessage | null = null;

            try {
              parsedMessage = JSON.parse(message.body) as JobWebSocketMessage;
            } catch {}

            if (parsedMessage) {
              options.onMessage?.(parsedMessage);
            }
          },
        );
        wsClient.publish({
          destination: subscribeDestination,
        });
      };

      if (!wsClient.active && !wsClient.connected) {
        wsClient.activate();
      }
    };

    setupConnection();

    return () => {
      cancelled = true;
      if (subscriptionRef.current) {
        subscriptionRef.current.unsubscribe();
        subscriptionRef.current = null;
      }
      wsClient.deactivate();
      if (clientRef.current === wsClient) {
        clientRef.current = null;
      }
    };
  }, [jobDispatcherId]);

  return {};
};
