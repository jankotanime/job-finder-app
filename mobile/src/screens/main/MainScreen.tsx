import React, { useRef, useState, useEffect } from "react";
import { View, StyleSheet, Dimensions, Animated, Text } from "react-native";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import { Swiper, type SwiperCardRefType } from "rn-swiper-list";
import useJobStorage from "../../hooks/useJobStorage";
import { data } from "../../constans/jobsDataTest";
import { useTheme } from "react-native-paper";
import { Job } from "../../types/Job";
import JobCard from "../../components/main/RenderCard";
import Menu from "../../components/reusable/Menu";
import { createAnimation } from "../../utils/animationHelper";
import { makeExpandHandlers } from "../../utils/expandController";
import OnSwipeRight from "../../components/main/swipe/OnSwipeRight";
import OnSwipeLeft from "../../components/main/swipe/OnSwipeLeft";
import OnSwipeBottom from "../../components/main/swipe/OnSwipeBottom";
import Filter from "../../components/main/Filter";

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
  const [currentIndex, setCurrentIndex] = useState<number>(0);
  const [isActivePressAnim, setIsActivePressAnim] = useState<boolean>(false);
  const expandAnim = useRef(new Animated.Value(0)).current;
  const [finalizeHideForId, setFinalizeHideForId] = useState<
    string | number | null
  >(null);
  const isAnimatingRef = useRef<boolean>(false);
  const animatingCardIndexRef = useRef<number | null>(null);

  const { onExpand, collapseCard } = makeExpandHandlers({
    expandAnim,
    getIsActive: () => isActivePressAnim,
    setIsActive: setIsActivePressAnim,
    isAnimatingRef,
    animatingCardIndexRef,
    getCurrentIndex: () => currentIndex,
  });

  useEffect(() => {
    setJobsData(data);
  }, []);

  return (
    <View style={{ flex: 1 }}>
      <GestureHandlerRootView
        style={[styles.container, { backgroundColor: colors.background }]}
      >
        <Filter />
        <Menu />
        <View style={styles.subContainer}>
          <Swiper
            ref={swiperRef}
            data={jobsData}
            initialIndex={0}
            cardStyle={styles.cardStyle}
            renderCard={(item) => (
              <JobCard
                item={item}
                expandAnim={expandAnim}
                isActive={isActivePressAnim}
                onDescriptionHidden={() => {
                  if (!isAnimatingRef.current) isAnimatingRef.current = true;
                  createAnimation(expandAnim, 0, 300).start(() => {
                    setFinalizeHideForId(item.id);
                    setTimeout(() => setFinalizeHideForId(null), 120);
                    isAnimatingRef.current = false;
                  });
                }}
                finalizeHide={finalizeHideForId === item.id}
              />
            )}
            onIndexChange={(index) => {
              setCurrentIndex(index);
              if (
                animatingCardIndexRef.current !== null &&
                animatingCardIndexRef.current !== index
              ) {
                isAnimatingRef.current = false;
                animatingCardIndexRef.current = null;
              }
            }}
            OverlayLabelRight={() => (
              <OnSwipeRight isActive={isActivePressAnim} />
            )}
            OverlayLabelLeft={() => (
              <OnSwipeLeft isActive={isActivePressAnim} />
            )}
            OverlayLabelBottom={() => (
              <OnSwipeBottom isActive={isActivePressAnim} />
            )}
            onSwipeRight={() => {
              collapseCard();
            }}
            onPress={() => {
              onExpand();
            }}
            onSwipedAll={() => {
              // do zrobienia pozniej
            }}
            disableTopSwipe
            onSwipeLeft={() => {
              collapseCard();
            }}
            onSwipeBottom={(cardIndex) => {
              addStorageJob(jobsData[cardIndex]);
              collapseCard();
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
