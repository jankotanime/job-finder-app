import React, { useRef, useState } from "react";
import {
  StyleSheet,
  TouchableOpacity,
  View,
  Animated,
  Dimensions,
  Text,
  ScrollView,
  Switch,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import { useTheme } from "react-native-paper";
import { useThemeContext } from "../../contexts/ThemeContext";
import { useTranslation } from "react-i18next";
import { createAnimation } from "../../utils/animationHelper";
import { Linking } from "react-native";
import { useAuth } from "../../contexts/AuthContext";
import Entypo from "@expo/vector-icons/Entypo";

const { width, height } = Dimensions.get("window");

const Menu = () => {
  const firstBarRotation = useRef(new Animated.Value(0)).current;
  const [isActive, setIsActive] = useState<boolean>(false);
  const secondBarRotation = useRef(new Animated.Value(0)).current;
  const moveThirdBarY = useRef(new Animated.Value(0)).current;
  const moveThirdBarX = useRef(new Animated.Value(0)).current;
  const menuDisplay = useRef(new Animated.Value(0)).current;
  const navigation = useNavigation<any>();
  const { colors } = useTheme();
  const { isDarkMode, toggleTheme } = useThemeContext();
  const { t } = useTranslation();
  const { signOut } = useAuth();

  const handleSignOut = async () => {
    await signOut();
    navigation.navigate("Auth");
  };

  const firstRotate = () => {
    setIsActive((prev) => !prev);
    if (isActive) {
      Animated.parallel([
        createAnimation(firstBarRotation, 0),
        createAnimation(secondBarRotation, 0),
        createAnimation(moveThirdBarY, 0),
        createAnimation(moveThirdBarX, 0),
        createAnimation(menuDisplay, 0, 300),
      ]).start();
    } else {
      Animated.parallel([
        createAnimation(firstBarRotation, 1),
        createAnimation(secondBarRotation, 1),
        createAnimation(moveThirdBarY, -10),
        createAnimation(moveThirdBarX, 10),
        createAnimation(menuDisplay, -height, 300),
      ]).start();
    }
  };

  const turnLeft = firstBarRotation.interpolate({
    inputRange: [0, 1],
    outputRange: ["0deg", "45deg"],
  });
  const turnRight = secondBarRotation.interpolate({
    inputRange: [0, 1],
    outputRange: ["0deg", "-45deg"],
  });
  return (
    <>
      <TouchableOpacity
        style={[styles.menu, { zIndex: isActive ? 16 : 13 }]}
        onPress={firstRotate}
      >
        <Animated.View
          style={[
            styles.bar,
            { transform: [{ rotate: turnLeft }] },
            { backgroundColor: colors.primary },
          ]}
        />
        <Animated.View
          style={[
            styles.bar,
            {
              opacity: firstBarRotation.interpolate({
                inputRange: [0, 1],
                outputRange: [1, 0],
              }),
            },
            { backgroundColor: colors.primary },
          ]}
        />
        <Animated.View
          style={[
            styles.bar,
            {
              transform: [
                { rotate: turnRight },
                { translateY: moveThirdBarY },
                { translateX: moveThirdBarX },
              ],
            },
            { backgroundColor: colors.primary },
          ]}
        />
      </TouchableOpacity>
      <Animated.View
        style={[
          styles.menuDisplay,
          { backgroundColor: colors.background },
          {
            transform: [{ translateY: menuDisplay }],
          },
        ]}
      >
        <ScrollView style={styles.menuContent}>
          <Text style={[styles.menuTitle, { color: colors.onSurface }]}>
            {t("menu.profile")}
          </Text>
          <View style={[styles.menuBox, { backgroundColor: colors.surface }]}>
            <TouchableOpacity
              onPress={() => navigation.navigate("MyProfile")}
              style={[styles.menuItem, { backgroundColor: colors.surface }]}
            >
              <Ionicons
                name="person-circle-outline"
                size={24}
                color={colors.primary}
              />
              <Text style={[styles.menuItemText, { color: colors.onSurface }]}>
                {t("menu.myProfile")}
              </Text>
              <Ionicons
                name="chevron-forward"
                size={20}
                color={colors.primary}
              />
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.menuItem, { backgroundColor: colors.surface }]}
              onPress={() => navigation.navigate("Storage")}
            >
              <Entypo
                name="box"
                size={20}
                color={colors.primary}
                style={{ marginLeft: 2 }}
              />
              <Text style={[styles.menuItemText, { color: colors.onSurface }]}>
                {t("menu.storage")}
              </Text>
              <Ionicons
                name="chevron-forward"
                size={20}
                color={colors.primary}
              />
            </TouchableOpacity>
          </View>
          <Text style={[styles.menuTitle, { color: colors.onSurface }]}>
            {t("menu.settings")}
          </Text>
          <View style={[styles.menuBox, { backgroundColor: colors.surface }]}>
            <View
              style={[styles.menuItem, { backgroundColor: colors.surface }]}
            >
              <Ionicons name="moon-outline" size={24} color={colors.primary} />
              <Text style={[styles.menuItemText, { color: colors.onSurface }]}>
                {t("menu.darkMode")}
              </Text>
              <Switch value={isDarkMode} onValueChange={toggleTheme} />
            </View>
            <TouchableOpacity
              style={[styles.menuItem, { backgroundColor: colors.surface }]}
              onPress={() => navigation.navigate("LanguageMenu")}
            >
              <Ionicons
                name="language-outline"
                size={24}
                color={colors.primary}
              />
              <Text style={[styles.menuItemText, { color: colors.onSurface }]}>
                {t("menu.language")}
              </Text>
              <Ionicons
                name="chevron-forward"
                size={20}
                color={colors.primary}
              />
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.menuItem, { backgroundColor: colors.surface }]}
              onPress={() => handleSignOut()}
            >
              <Ionicons name="log-out-outline" size={24} color={colors.error} />
              <Text style={[styles.menuItemText, { color: colors.error }]}>
                {t("menu.logout")}
              </Text>
              <Ionicons name="chevron-forward" size={20} color={colors.error} />
            </TouchableOpacity>
          </View>
          <Text style={[styles.menuTitle, { color: colors.onSurface }]}>
            {t("menu.support")}
          </Text>
          <View style={[styles.menuBox, { backgroundColor: colors.surface }]}>
            <TouchableOpacity
              style={[styles.menuItem, { backgroundColor: colors.surface }]}
              onPress={() =>
                Linking.openURL(process.env.EXPO_PUBLIC_SUPPORT_MAIL)
              }
            >
              <Ionicons name="mail-outline" size={24} color={colors.primary} />
              <Text style={[styles.menuItemText, { color: colors.onSurface }]}>
                {t("menu.contactUs")}
              </Text>
              <Ionicons
                name="chevron-forward"
                size={20}
                color={colors.primary}
              />
            </TouchableOpacity>
          </View>
        </ScrollView>
      </Animated.View>
    </>
  );
};

export default Menu;

const styles = StyleSheet.create({
  menu: {
    position: "absolute",
    top: height * 0.09,
    right: 30,
  },
  bar: {
    width: 25,
    height: 3.2,
    backgroundColor: "#5F5100",
    marginBottom: 4,
    borderRadius: 20,
  },
  menuDisplay: {
    width: width,
    height: height,
    zIndex: 15,
    position: "absolute",
    top: height,
  },
  menuContent: {
    flex: 1,
    paddingTop: 110,
    paddingHorizontal: 20,
  },
  menuTitle: {
    fontFamily: "Roboto-SemiBold",
    fontSize: 22,
    marginBottom: 15,
  },
  menuBox: {
    width: width * 0.9,
    borderRadius: 15,
    marginBottom: 25,
    shadowColor: "#000",
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 5,
    overflow: "hidden",
  },
  menuItem: {
    flexDirection: "row",
    alignItems: "center",
    paddingVertical: 16,
    paddingHorizontal: 20,
  },
  menuItemText: {
    flex: 1,
    marginLeft: 15,
    fontSize: 16,
    fontFamily: "Roboto-Medium",
  },
});
