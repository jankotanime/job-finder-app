import React, {
  useRef,
  useState,
  useCallback,
  useEffect,
  useMemo,
} from "react";
import {
  View,
  StyleSheet,
  Dimensions,
  Animated,
  Text,
  Alert,
} from "react-native";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import { Swiper, type SwiperCardRefType } from "rn-swiper-list";
import { useFocusEffect } from "@react-navigation/native";
import { useAuth } from "../../contexts/AuthContext";
import { applyForOffer } from "../../api/offers/handleOffersApi";
import { useTheme } from "react-native-paper";
import { Offer } from "../../types/Offer";
import OfferCard from "../../components/main/RenderCard";
import Menu from "../../components/reusable/Menu";
import { createAnimation } from "../../utils/animationHelper";
import { makeExpandHandlers } from "../../utils/expandController";
import OnSwipeRight from "../../components/main/swipe/OnSwipeRight";
import OnSwipeLeft from "../../components/main/swipe/OnSwipeLeft";
import OnSwipeBottom from "../../components/main/swipe/OnSwipeBottom";
import Filter from "../../components/filter/Filter";
import AddOfferButton from "../../components/main/AddOfferButton";
import { ActivityIndicator } from "react-native-paper";
import { useTranslation } from "react-i18next";
import { useOfferStorageContext } from "../../contexts/OfferStorageContext";
import useFilter from "../../hooks/useFilter";
import CvChoseButton from "../../components/main/CvChoseButton";
import useSelectCv from "../../hooks/useSelectCv";
import Footer from "../../components/main/Footer";
import CvInfo from "../../components/main/CvInfo";
import ErrorNotification from "../../components/reusable/ErrorNotification";
import ActiveJobTimerFloating from "../../components/jobs/ActiveJobTimerFloating";
import useMainOffersDeck from "../../hooks/useMainOffersDeck";

const { width, height } = Dimensions.get("window");

const MainScreen = () => {
  const swiperRef = useRef<SwiperCardRefType | null>(null);
  const { colors } = useTheme();
  const { addStorageOffer, offersVersion } = useOfferStorageContext();
  const { tokens, loading, userInfo } = useAuth();
  const [isActivePressAnim, setIsActivePressAnim] = useState<boolean>(false);
  const [errorText, setErrorText] = useState<string | null>(null);
  const expandAnim = useRef(new Animated.Value(0)).current;
  const [finalizeHideForCardId, setFinalizeHideForCardId] = useState<
    string | null
  >(null);
  const isAnimatingRef = useRef<boolean>(false);
  const animatingCardIndexRef = useRef<number | null>(null);
  const { t } = useTranslation();
  const { filters, setFiltersList, clearFilters } = useFilter();
  const { selectedIds, reload } = useSelectCv();
  const cvId = selectedIds?.[0];

  const {
    offersData,
    setOffersData,
    currentCardIndex,
    setCurrentCardIndex,
    swipedCount,
    setSwipedCount,
    markSwiped,
    last,
    fetching,
  } = useMainOffersDeck({
    userId: userInfo?.userId ?? undefined,
    filters,
    offersVersion,
    enabled: Boolean(tokens && !loading && userInfo?.userId),
  });

  const resetCardUiState = useCallback(() => {
    setCurrentCardIndex(0);
    setIsActivePressAnim(false);
    setFinalizeHideForCardId(null);
    isAnimatingRef.current = false;
    animatingCardIndexRef.current = null;
    expandAnim.stopAnimation(() => {
      expandAnim.setValue(0);
    });
  }, [expandAnim, setCurrentCardIndex]);

  const { onExpand, collapseCard } = makeExpandHandlers({
    expandAnim,
    getIsActive: () => isActivePressAnim,
    setIsActive: setIsActivePressAnim,
    isAnimatingRef,
    animatingCardIndexRef,
    getCurrentIndex: () => currentCardIndex,
  });

  const uniqueOffersData = useMemo(() => {
    const seen = new Set<string>();
    return offersData.filter((offer) => {
      const rawId = (offer as any)?.id ?? offer?.dateAndTime;
      const id =
        typeof rawId === "string"
          ? rawId
          : typeof rawId === "number" && Number.isFinite(rawId)
            ? String(rawId)
            : "";
      if (!id) return true;
      if (seen.has(id)) return false;
      seen.add(id);
      return true;
    });
  }, [offersData]);

  const swiperKey = useMemo(() => {
    const ids = (uniqueOffersData as any[]).map((o) => o?.id ?? "").join("|");
    return `${userInfo?.userId ?? "anon"}-${ids}`;
  }, [userInfo?.userId, uniqueOffersData]);
  const currentCardId = useMemo(() => {
    const currentOffer =
      uniqueOffersData[Math.max(currentCardIndex, swipedCount)];
    return currentOffer?.id ?? currentOffer?.dateAndTime ?? null;
  }, [uniqueOffersData, currentCardIndex, swipedCount]);

  useFocusEffect(
    useCallback(() => {
      reload();
    }, [reload]),
  );

  useEffect(() => {
    if (!errorText) return;
    const id = setTimeout(() => setErrorText(null), 7200);
    return () => clearTimeout(id);
  }, [errorText]);

  useEffect(() => {
    if (swipedCount === currentCardIndex) return;
    if (swipedCount < 0 || swipedCount > uniqueOffersData.length) return;
    setCurrentCardIndex(swipedCount);
  }, [
    swipedCount,
    currentCardIndex,
    uniqueOffersData.length,
    setCurrentCardIndex,
  ]);

  return (
    <View style={{ flex: 1 }}>
      <GestureHandlerRootView
        style={[styles.container, { backgroundColor: colors.background }]}
      >
        <Filter
          setOffersData={setOffersData}
          filters={filters}
          setFiltersList={setFiltersList}
          clearFilters={clearFilters}
        />
        {errorText && <ErrorNotification error={errorText} />}
        <CvInfo />
        <Menu />
        {(loading || !uniqueOffersData || uniqueOffersData.length === 0) && (
          <View style={styles.loadingContainer}>
            <ActivityIndicator size="large" color={colors.primary} />
            <Text style={{ color: colors.onSurfaceVariant, marginTop: 10 }}>
              {t("main.offers_loading")}
            </Text>
          </View>
        )}
        <View style={styles.subContainer}>
          <Swiper
            key={swiperKey}
            keyExtractor={(item: Offer) => {
              const rawId = (item as any)?.id ?? item.dateAndTime;
              return String(rawId);
            }}
            ref={swiperRef}
            data={uniqueOffersData}
            initialIndex={currentCardIndex}
            cardStyle={styles.cardStyle}
            disableRightSwipe={!cvId}
            disableLeftSwipe={!cvId}
            onSwipeActive={() => {
              if (!cvId) setErrorText(t("cv.selectionCvMissing"));
              return;
            }}
            renderCard={(item: Offer) => {
              const cardId =
                (item as Offer)?.id ?? (item as Offer)?.dateAndTime;
              const isCurrentCard = cardId === currentCardId;

              return (
                <OfferCard
                  item={item}
                  expandAnim={isCurrentCard ? expandAnim : undefined}
                  isActive={isActivePressAnim && isCurrentCard}
                  onDescriptionHidden={
                    isCurrentCard
                      ? () => {
                          if (!isAnimatingRef.current)
                            isAnimatingRef.current = true;
                          createAnimation(expandAnim, 0, 300).start(() => {
                            setFinalizeHideForCardId(currentCardId);
                            setTimeout(
                              () => setFinalizeHideForCardId(null),
                              120,
                            );
                            isAnimatingRef.current = false;
                          });
                        }
                      : undefined
                  }
                  finalizeHide={
                    Boolean(isCurrentCard) && finalizeHideForCardId === cardId
                  }
                />
              );
            }}
            onIndexChange={(index) => {
              setCurrentCardIndex(index);
              expandAnim.stopAnimation(() => {
                expandAnim.setValue(0);
              });
              setIsActivePressAnim(false);
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
            onSwipeRight={async (index) => {
              const swipedOffer = uniqueOffersData[index];
              collapseCard();
              markSwiped(swipedOffer);
              try {
                if (!cvId) {
                  Alert.alert(t("cv.title"), t("cv.selectionCvMissing"));
                  return;
                }
                const offerIdToApply = swipedOffer?.id;
                if (!offerIdToApply) {
                  console.warn("no offer id");
                  return;
                }
                await applyForOffer(offerIdToApply, { cvId });
              } catch (e) {
                console.error("failed to apply for offer", e);
              }
            }}
            onPress={() => {
              onExpand();
            }}
            onSwipedAll={() => {
              if (fetching || last) return;
              setOffersData([]);
              setCurrentCardIndex(0);
              setSwipedCount(0);
              resetCardUiState();
            }}
            disableTopSwipe
            onSwipeLeft={(index) => {
              const swipedOffer = uniqueOffersData[index];
              markSwiped(swipedOffer);
              collapseCard();
            }}
            onSwipeBottom={(cardIndex) => {
              const offer = uniqueOffersData[cardIndex];
              if (offer) addStorageOffer(offer);
              markSwiped(offer);
              collapseCard();
            }}
          />
        </View>
        <Footer />

        <ActiveJobTimerFloating />
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
    height: height * 0.75,
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
  loadingContainer: {
    position: "absolute",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    justifyContent: "center",
    alignItems: "center",
    zIndex: 0,
  },
  footer: {
    position: "absolute",
    left: 0,
    right: 0,
    bottom: 24,
    paddingHorizontal: 12,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    zIndex: 20,
    pointerEvents: "box-none",
  },
});
