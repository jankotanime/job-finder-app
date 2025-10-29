import React from "react";
import { View, Text, StyleSheet } from "react-native";
import { Button } from "react-native-paper";
import EncryptedStorage from "react-native-encrypted-storage";
import { useAuth } from "../../contexts/AuthContext";
import { useNavigation } from "@react-navigation/native";

const MainScreen = () => {
  const { isAuthenticated, user, signOut } = useAuth();
  const navigation = useNavigation<any>();

  const logTokens = async () => {
    try {
      const saved = await EncryptedStorage.getItem("auth");
      if (!saved) {
        console.log("[MainScreen] No tokens stored under 'auth'.");
        return;
      }
      const parsed = JSON.parse(saved);
      console.log("[MainScreen] Stored tokens (parsed):", parsed);
    } catch (e) {
      console.log("[MainScreen] Failed to read tokens:", e);
    }
  };

  const handleSignOut = async () => {
    try {
      await signOut();
      console.log("[MainScreen] Signed out, navigating to Home");
      navigation.reset({ index: 0, routes: [{ name: "Home" }] });
    } catch (e) {
      console.log("[MainScreen] Sign out failed:", e);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>MainScreen</Text>
      <Text style={styles.info}>
        isAuthenticated: {String(isAuthenticated)}
      </Text>
      <Text style={styles.info}>user: {user || "-"}</Text>
      <Button mode="contained" onPress={logTokens} style={styles.button}>
        Log tokens to console
      </Button>
      <Button
        mode="outlined"
        onPress={handleSignOut}
        style={styles.buttonSecondary}
      >
        Wyloguj
      </Button>
    </View>
  );
};

export default MainScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    padding: 16,
  },
  title: {
    fontSize: 22,
    fontWeight: "700",
    marginBottom: 12,
  },
  info: {
    marginBottom: 8,
  },
  button: {
    marginTop: 12,
    width: 220,
  },
  buttonSecondary: {
    marginTop: 12,
    width: 220,
  },
});
