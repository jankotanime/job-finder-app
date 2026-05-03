import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const getSockJsUrl = () => {
  const brokerUrl = process.env.EXPO_PUBLIC_BROKER_URL;
  if (!brokerUrl) return "";
  return brokerUrl.replace(/^ws(s)?:\/\//, "http$1://");
};

export const createWsClient = () =>
  new Client({
    webSocketFactory: () => new SockJS(getSockJsUrl()),
    reconnectDelay: 0,
    heartbeatIncoming: 0,
    heartbeatOutgoing: 0,
    debug: (str) => console.log("STOMP: ", str),
  });
