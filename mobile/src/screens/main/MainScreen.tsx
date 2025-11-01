import React, { useState } from "react";
import { View, Text, StyleSheet } from "react-native";
import { Button } from "react-native-paper";
import { useAuth } from "../../contexts/AuthContext";
import { useNavigation } from "@react-navigation/native";
import { getTokens } from "../../utils/getTokens";
import Error from "../../components/reusable/Error";

const MainScreen = () => {
  const { isAuthenticated, user, signOut } = useAuth();
  const navigation = useNavigation<any>();
  const [error, setError] = useState<string>("");

  const logTokens = async () => {
    await getTokens(setError);
  };

  const handleSignOut = async () => {
    try {
      await signOut();
      navigation.reset({ index: 0, routes: [{ name: "Home" }] });
    } catch (e) {
      console.error("[MainScreen] Sign out failed:", e);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>MainScreen</Text>
      <Text style={styles.info}>
        isAuthenticated: {String(isAuthenticated)}
      </Text>
      {error && <Error error={error} />}
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
