import React, { useRef, useState, useEffect } from "react";
import { View, StyleSheet, Dimensions } from "react-native";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import { Swiper, type SwiperCardRefType } from "rn-swiper-list";
import useJobStorage from "../../hooks/useJobStorage";
import { data } from "../../constans/jobsDataTest";
import { useTheme } from "react-native-paper";
import { Job } from "../../types/Job";
import JobCard from "../../components/main/RenderCard";
import Menu from "../../components/reusable/Menu";

const { width, height } = Dimensions.get("window");

const MainScreen = () => {
  const swiperRef = useRef<SwiperCardRefType | null>(null);
  const { colors } = useTheme();
  const {
    acceptedJobs,
    declinedJobs,
    storageJobs,
    addAcceptedJob,
    removeAcceptedJob,
    addDeclinedJob,
    removeDeclinedJob,
    addStorageJob,
    removeStorageJob,
  } = useJobStorage();
  const [jobsData, setJobsData] = useState<Job[]>([]);

  useEffect(() => {
    setJobsData(data);
  }, []);

  return (
    <View style={{ flex: 1 }}>
      <GestureHandlerRootView
        style={[styles.container, { backgroundColor: colors.background }]}
      >
        <Menu />
        <View style={styles.subContainer}>
          <Swiper
            ref={swiperRef}
            data={jobsData}
            initialIndex={0}
            cardStyle={styles.cardStyle}
            renderCard={(item) => <JobCard item={item} />}
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
            disableTopSwipe
            onSwipeLeft={(cardIndex) => {
              console.log("onSwipeLeft", cardIndex);
            }}
            onSwipeBottom={(cardIndex) => {
              addStorageJob(jobsData[cardIndex]);
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
  },
  subContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    zIndex: 14,
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
