import type { Client, StompSubscription } from "@stomp/stompjs";
import EncryptedStorage from "react-native-encrypted-storage";
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
      console.log("[JobWS] init", {
        jobDispatcherId,
        topicDestination,
        subscribeDestination,
      });

      const authRaw = await EncryptedStorage.getItem("auth");
      let authorizationHeader = "";

      if (authRaw) {
        try {
          const parsedAuth = JSON.parse(authRaw);
          if (parsedAuth?.accessToken) {
            authorizationHeader = `Bearer ${parsedAuth.accessToken}`;
          }
        } catch (error) {
          console.warn("[JobWS] failed to parse auth storage", error);
        }
      }

      if (cancelled) return;

      wsClient.connectHeaders = authorizationHeader
        ? { Authorization: authorizationHeader }
        : {};

      console.log("[JobWS] client config", {
        brokerURL: process.env.EXPO_PUBLIC_BROKER_URL,
        connectHeaders: {
          ...wsClient.connectHeaders,
          Authorization: authorizationHeader ? "[set]" : "[missing]",
        },
      });

      wsClient.onConnect = (frame) => {
        console.log("CONNECTED OK: ", frame);
        console.log("[JobWS] connected", { topicDestination });

        if (subscriptionRef.current) {
          console.log("[JobWS] unsubscribe previous subscription", {
            topicDestination,
          });
          subscriptionRef.current.unsubscribe();
        }

        subscriptionRef.current = wsClient.subscribe(
          topicDestination,
          (message) => {
            let parsedMessage: JobWebSocketMessage | null = null;

            try {
              parsedMessage = JSON.parse(message.body) as JobWebSocketMessage;
            } catch (error) {
              console.warn("[JobWS] failed to parse message body", error);
            }

            console.log("[JobWS] incoming message from topic", {
              topicDestination,
              body: message.body,
            });

            if (parsedMessage) {
              options.onMessage?.(parsedMessage);
            }
          },
        );

        console.log("[JobWS] subscribed to topic", { topicDestination });

        console.log("[JobWS] sending subscribe signal", {
          subscribeDestination,
        });
        wsClient.publish({
          destination: subscribeDestination,
        });
        console.log("[JobWS] subscribe signal sent", { subscribeDestination });
      };

      wsClient.onStompError = (frame) => {
        console.error("[JobWS] STOMP ERROR", {
          body: frame.body,
          headers: frame.headers,
        });
      };

      wsClient.onWebSocketClose = (event) => {
        console.log("[JobWS] websocket closed", {
          code: event.code,
          reason: event.reason,
          wasClean: event.wasClean,
        });
        if (!cancelled && !wsClient.connected) {
          console.warn("[JobWS] socket closed before STOMP CONNECTED", {
            topicDestination,
            active: wsClient.active,
            connected: wsClient.connected,
          });
        }
      };

      wsClient.onWebSocketError = (event) => {
        console.log("[JobWS] websocket error", event);
      };

      if (!wsClient.active && !wsClient.connected) {
        console.log("[JobWS] activate client", {
          topicDestination,
          hasAuthorization: Boolean(authorizationHeader),
        });
        wsClient.activate();
      }
    };

    void setupConnection();

    return () => {
      cancelled = true;
      if (subscriptionRef.current) {
        console.log("[JobWS] cleanup unsubscribe", { topicDestination });
        subscriptionRef.current.unsubscribe();
        subscriptionRef.current = null;
      }
      console.log("[JobWS] deactivate client", { topicDestination });
      wsClient.deactivate();
      if (clientRef.current === wsClient) {
        clientRef.current = null;
      }
    };
  }, [jobDispatcherId]);

  return {};
};
