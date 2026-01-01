import React from "react";
import { View, StyleSheet, ScrollView, Dimensions } from "react-native";
import {
  Avatar,
  Text,
  Button,
  Card,
  IconButton,
  Divider,
  useTheme,
} from "react-native-paper";
import { SafeAreaView } from "react-native-safe-area-context";
import { useAuth } from "../../contexts/AuthContext";
import { buildPhotoUrl } from "../../utils/photoUrl";
import Feather from "@expo/vector-icons/Feather";
import { useTranslation } from "react-i18next";

const { height } = Dimensions.get("window");
const MyProfile = () => {
  const { colors } = useTheme();
  const { userInfo } = useAuth();
  const photoUrl = buildPhotoUrl(userInfo?.profilePhoto ?? undefined);
  const { t } = useTranslation();
  return (
    <SafeAreaView
      style={[styles.container, { backgroundColor: colors.background }]}
    >
      <ScrollView style={styles.scroll}>
        <View style={styles.header}>
          <View style={styles.avatarWrapper}>
            {photoUrl ? (
              <Avatar.Image
                size={120}
                source={{ uri: photoUrl }}
                style={{ backgroundColor: colors.background }}
              />
            ) : (
              <Avatar.Icon
                size={120}
                style={{
                  backgroundColor: colors.background,
                  borderColor: colors.primary,
                  borderWidth: 1,
                }}
                icon={() => (
                  <Feather name="user" size={60} color={colors.primary} />
                )}
              />
            )}
            <IconButton
              icon="camera"
              mode="contained"
              containerColor={colors.primary}
              iconColor={colors.onPrimary}
              size={20}
              style={styles.editIcon}
              onPress={() => {}}
            />
          </View>
          <Text variant="headlineMedium" style={styles.name}>
            {userInfo?.firstName} {userInfo?.lastName}
          </Text>
          <Text variant="titleMedium" style={{ color: colors.primary }}>
            {userInfo?.email}
          </Text>
        </View>
        <View style={styles.buttonContainer}>
          <Button
            mode="contained"
            icon="account-edit"
            onPress={() => {}}
            style={styles.mainButton}
          >
            {t("profile.edit")}
          </Button>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  topBar: {
    flexDirection: "row",
    justifyContent: "flex-end",
    paddingHorizontal: 16,
    paddingTop: 40,
  },
  header: {
    alignItems: "center",
    paddingBottom: 24,
  },
  avatarWrapper: {
    position: "relative",
    marginBottom: 16,
  },
  editIcon: {
    position: "absolute",
    bottom: -5,
    right: -5,
    margin: 0,
  },
  name: {
    fontWeight: "bold",
  },
  infoCard: {
    marginHorizontal: 20,
    borderRadius: 16,
  },
  statsRow: {
    flexDirection: "row",
    justifyContent: "space-around",
    alignItems: "center",
  },
  statItem: {
    alignItems: "center",
  },
  divider: {
    height: 30,
    width: 1,
  },
  buttonContainer: {
    padding: 20,
    gap: 10,
  },
  mainButton: {
    borderRadius: 12,
  },
  scroll: {
    flex: 1,
    marginTop: height * 0.05,
  },
});

export default MyProfile;
