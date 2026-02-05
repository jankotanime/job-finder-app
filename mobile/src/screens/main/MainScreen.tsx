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
import { applyForOffer, getAllOffers } from "../../api/offers/handleOffersApi";
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
import { buildPhotoUrl } from "../../utils/photoUrl";
import { useOfferStorageContext } from "../../contexts/OfferStorageContext";
import useFilter from "../../hooks/useFilter";
import { handleFilterOffers } from "../../api/filter/handleFilterOffers";
import CvChoseButton from "../../components/main/CvChoseButton";
import useSelectCv from "../../hooks/useSelectCv";
import Footer from "../../components/main/Footer";
import CvInfo from "../../components/main/CvInfo";
import ErrorNotification from "../../components/reusable/ErrorNotification";

const { width, height } = Dimensions.get("window");

const MainScreen = () => {
  const swiperRef = useRef<SwiperCardRefType | null>(null);
  const { colors } = useTheme();
  const { addStorageOffer } = useOfferStorageContext();
  const { tokens, loading, userInfo } = useAuth();
  const [offersData, setOffersData] = useState<Offer[]>([]);
  const [currentIndex, setCurrentIndex] = useState<number>(0);
  const [isActivePressAnim, setIsActivePressAnim] = useState<boolean>(false);
  const [errorText, setErrorText] = useState<string | null>(null);
  const expandAnim = useRef(new Animated.Value(0)).current;
  const [finalizeHideForIndex, setFinalizeHideForIndex] = useState<
    number | null
  >(null);
  const isAnimatingRef = useRef<boolean>(false);
  const animatingCardIndexRef = useRef<number | null>(null);
  const { t } = useTranslation();
  const { offersVersion } = useOfferStorageContext();
  const { filters } = useFilter();
  const { selectedIds, reload } = useSelectCv();
  const cvId = selectedIds?.[0];
  const [page, setPage] = useState(0);
  const [last, setLast] = useState(false);
  const [fetching, setFetching] = useState(false);
  const PAGE_SIZE = 20;

  const { onExpand, collapseCard } = makeExpandHandlers({
    expandAnim,
    getIsActive: () => isActivePressAnim,
    setIsActive: setIsActivePressAnim,
    isAnimatingRef,
    animatingCardIndexRef,
    getCurrentIndex: () => currentIndex,
  });
  const swiperKey = useMemo(() => {
    const ids = (offersData as any[]).map((o) => o?.id ?? "").join("|");
    return `${userInfo?.userId ?? "anon"}-${ids}`;
  }, [userInfo?.userId, offersData]);

  const loadOffers = useCallback(
    async (reset = false) => {
      if (fetching) return;
      if (!reset && last) return;
      setFetching(true);
      try {
        const currentPage = reset ? 0 : page;
        let response;
        if (filters.length > 0) {
          response = await handleFilterOffers(
            { tags: Array.from(new Set(filters)) },
            { page: currentPage, size: PAGE_SIZE },
          );
        } else {
          response = await getAllOffers({
            page: currentPage,
            size: PAGE_SIZE,
          });
        }
        const pageData = response?.body?.data;
        const items = Array.isArray(pageData?.content) ? pageData.content : [];
        const normalized = items
          .map((it: any) => ({
            ...it,
            offerPhoto: buildPhotoUrl(it?.photo?.storageKey),
          }))
          .filter((it: any) => it?.owner?.id !== userInfo?.userId);
        setOffersData((prev) =>
          reset ? normalized : [...prev, ...normalized],
        );
        setLast(pageData?.last ?? true);
        setPage(currentPage + 1);
      } catch (e) {
        console.error("failed to load offers", e);
      } finally {
        setFetching(false);
      }
    },
    [filters, page, last, fetching, userInfo?.userId],
  );

  useEffect(() => {
    if (!tokens || loading || !userInfo?.userId) return;
    setOffersData([]);
    setCurrentIndex(0);
    setPage(0);
    setLast(false);
    loadOffers(true);
  }, [tokens, loading, userInfo?.userId, offersVersion, selectedIds, filters]);

  useFocusEffect(
    useCallback(() => {
      reload();
      return () => {};
    }, [reload]),
  );

  useEffect(() => {
    if (!errorText) return;
    const id = setTimeout(() => setErrorText(null), 7200);
    return () => clearTimeout(id);
  }, [errorText]);
  return (
    <View style={{ flex: 1 }}>
      <GestureHandlerRootView
        style={[styles.container, { backgroundColor: colors.background }]}
      >
        <Filter setOffersData={setOffersData} />
        {errorText && <ErrorNotification error={errorText} />}
        <CvInfo />
        <Menu />
        {(loading || !offersData || offersData.length === 0) && (
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
            keyExtractor={(item) => (item as any)?.id ?? item.dateAndTime}
            ref={swiperRef}
            data={offersData}
            initialIndex={0}
            cardStyle={styles.cardStyle}
            disableRightSwipe={!cvId}
            disableLeftSwipe={!cvId}
            onSwipeActive={() => {
              if (!cvId) setErrorText(t("cv.selectionCvMissing"));
              return;
            }}
            renderCard={(item) => (
              <OfferCard
                item={item}
                expandAnim={expandAnim}
                isActive={isActivePressAnim}
                onDescriptionHidden={() => {
                  if (!isAnimatingRef.current) isAnimatingRef.current = true;
                  createAnimation(expandAnim, 0, 300).start(() => {
                    setFinalizeHideForIndex(currentIndex);
                    setTimeout(() => setFinalizeHideForIndex(null), 120);
                    isAnimatingRef.current = false;
                  });
                }}
                finalizeHide={finalizeHideForIndex === currentIndex}
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
            onSwipeRight={async (index) => {
              collapseCard();
              try {
                if (!cvId) {
                  Alert.alert(t("cv.title"), t("cv.selectionCvMissing"));
                  return;
                }
                const offerIdToApply = offersData[index]?.id;
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
              loadOffers();
            }}
            disableTopSwipe
            onSwipeLeft={() => {
              collapseCard();
            }}
            onSwipeBottom={(cardIndex) => {
              addStorageOffer(offersData[cardIndex]);
              collapseCard();
            }}
          />
          {fetching && (
            <View style={{ paddingVertical: 20 }}>
              <ActivityIndicator size="small" />
            </View>
          )}
        </View>
        <Footer />
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
    zIndex: 10,
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
