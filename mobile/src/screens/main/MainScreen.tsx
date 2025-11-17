import React, { useRef, useState } from "react";
import { View, StyleSheet, Dimensions } from "react-native";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import { Swiper, type SwiperCardRefType } from "rn-swiper-list";
import { useAuth } from "../../contexts/AuthContext";
import { useNavigation } from "@react-navigation/native";
import { Button } from "react-native-paper";
import useJobStorage from "../../hooks/useJobStorage";
import { data } from "../../constans/jobsDataTest";
import { useTheme } from "react-native-paper";
import { useThemeContext } from "../../contexts/ThemeContext";
import renderCard from "../../components/main/RenderCard";

const { width, height } = Dimensions.get("window");

const MainScreen = () => {
  const swiperRef = useRef<SwiperCardRefType | null>(null);
  const { colors } = useTheme();
  const { toggleTheme } = useThemeContext();
  const { signOut } = useAuth();
  const navigation = useNavigation<any>();
  const {
    acceptedJobs,
    declinedJobs,
    addAcceptedJob,
    removeAcceptedJob,
    addDeclinedJob,
    removeDeclinedJob,
  } = useJobStorage();

  const handleSignOut = async () => {
    await signOut();
    navigation.navigate("Auth");
  };

  return (
    <View style={{ flex: 1 }}>
      <GestureHandlerRootView
        style={[styles.container, { backgroundColor: colors.background }]}
      >
        <Button
          mode="contained"
          onPress={handleSignOut}
          style={styles.signOutButton}
        >
          Sign out
        </Button>
        <Button
          mode="contained"
          onPress={toggleTheme}
          style={styles.darkModeButton}
        >
          Dark mode
        </Button>
        <View style={styles.subContainer}>
          <Swiper
            ref={swiperRef}
            data={data}
            initialIndex={0}
            cardStyle={styles.cardStyle}
            renderCard={renderCard}
            onIndexChange={(index) => {
              console.log("Current Active index", index);
            }}
            onSwipeRight={(cardIndex) => {
              console.log("cardIndex", cardIndex);
            }}
            onPress={() => {
              console.log("onPress");
            }}
            onSwipedAll={() => {
              console.log("onSwipedAll");
            }}
            disableBottomSwipe
            onSwipeLeft={(cardIndex) => {
              console.log("onSwipeLeft", cardIndex);
            }}
            onSwipeTop={(cardIndex) => {
              console.log("onSwipeTop", cardIndex);
            }}
            onSwipeActive={() => {
              // console.log("onSwipeActive");
            }}
            onSwipeStart={() => {
              console.log("onSwipeStart");
            }}
            onSwipeEnd={() => {
              console.log("onSwipeEnd");
            }}
          />
        </View>
      </GestureHandlerRootView>
    </View>
  );
};

export default MainScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    zIndex: -1,
  },
  subContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  cardStyle: {
    width: width,
    height: height * 0.7,
    backgroundColor: "transparent",
  },
  signOutButton: {
    position: "absolute",
    top: 65,
    right: 10,
  },
  darkModeButton: {
    position: "absolute",
    top: 65,
    right: 200,
  },
});
