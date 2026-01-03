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
// import { getCvsByUser } from "../../api/cv/handleCvApi";
import CvChoseButton from "../../components/main/CvChoseButton";
import useSelectCv from "../../hooks/useSelectCv";

const { width, height } = Dimensions.get("window");

const MainScreen = () => {
  const swiperRef = useRef<SwiperCardRefType | null>(null);
  const { colors } = useTheme();
  const { addStorageOffer } = useOfferStorageContext();
  const { tokens, loading, userInfo } = useAuth();
  const [offersData, setOffersData] = useState<Offer[]>([]);
  const [currentIndex, setCurrentIndex] = useState<number>(0);
  const [isActivePressAnim, setIsActivePressAnim] = useState<boolean>(false);
  const expandAnim = useRef(new Animated.Value(0)).current;
  const [finalizeHideForIndex, setFinalizeHideForIndex] = useState<
    number | null
  >(null);
  const isAnimatingRef = useRef<boolean>(false);
  const animatingCardIndexRef = useRef<number | null>(null);
  const { t } = useTranslation();
  const { offersVersion } = useOfferStorageContext();
  const { filters } = useFilter();
  const { selectedIds } = useSelectCv();

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

  useEffect(() => {
    setOffersData([]);
    setCurrentIndex(0);
    console.log(selectedIds);
    if (!tokens || loading || !userInfo?.userId) {
      return;
    }
    let active = true;
    const load = async () => {
      let page;
      if (filters.length > 0) {
        const next = Array.from(new Set(filters));
        page = await handleFilterOffers({ tags: next });
      } else {
        page = await getAllOffers();
      }
      const items = Array.isArray(page?.body?.data?.content)
        ? page.body.data.content
        : [];

      const normalized = items.map((it: any) => ({
        ...it,
        offerPhoto: buildPhotoUrl(it?.photo?.storageKey),
      }));
      const filtered = normalized.filter(
        (it: any) => it?.owner?.id !== userInfo.userId,
      );
      if (active) setOffersData(filtered);
    };
    load();
    return () => {
      active = false;
    };
  }, [tokens, loading, userInfo?.userId, offersVersion, selectedIds]);
  return (
    <View style={{ flex: 1 }}>
      <GestureHandlerRootView
        style={[styles.container, { backgroundColor: colors.background }]}
      >
        <Filter setOffersData={setOffersData} />
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
                const cvId = selectedIds?.[0];
                if (!cvId) {
                  Alert.alert(t("cv.title"), t("cv.selectionUserMissing"));
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
              // do zrobienia pozniej
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
        </View>
        <CvChoseButton />
        <AddOfferButton />
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
});
