import React, { useRef, useState, useCallback, useEffect } from "react";
import { View, StyleSheet, Dimensions, Animated, Text } from "react-native";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import { Swiper, type SwiperCardRefType } from "rn-swiper-list";
import useOfferStorageContext from "../../hooks/useOfferStorage";
import { useAuth } from "../../contexts/AuthContext";
import { getAllOffers } from "../../api/offers/handleOffersApi";
import { useTheme } from "react-native-paper";
import { Offer } from "../../types/Offer";
import OfferCard from "../../components/main/RenderCard";
import Menu from "../../components/reusable/Menu";
import { createAnimation } from "../../utils/animationHelper";
import { makeExpandHandlers } from "../../utils/expandController";
import OnSwipeRight from "../../components/main/swipe/OnSwipeRight";
import OnSwipeLeft from "../../components/main/swipe/OnSwipeLeft";
import OnSwipeBottom from "../../components/main/swipe/OnSwipeBottom";
import Filter from "../../components/main/Filter";
import AddOfferButton from "../../components/main/AddOfferButton";
import { ActivityIndicator } from "react-native-paper";
// useFocusEffect nieużywane — korzystamy wyłącznie z useEffect
import { useTranslation } from "react-i18next";
import { buildPhotoUrl } from "../../utils/photoUrl";

const { width, height } = Dimensions.get("window");

const MainScreen = () => {
  const swiperRef = useRef<SwiperCardRefType | null>(null);
  const { colors } = useTheme();
  const {
    acceptedOffers,
    declinedOffers,
    storageOffers,
    addAcceptedOffer,
    removeAcceptedOffer,
    addDeclinedOffer,
    removeDeclinedOffer,
    addStorageOffer,
    removeStorageOffer,
  } = useOfferStorageContext();
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
  const [ownerId, setOwnerId] = useState<string>("");

  const { onExpand, collapseCard } = makeExpandHandlers({
    expandAnim,
    getIsActive: () => isActivePressAnim,
    setIsActive: setIsActivePressAnim,
    isAnimatingRef,
    animatingCardIndexRef,
    getCurrentIndex: () => currentIndex,
  });

  useEffect(() => {
    let active = true;
    const load = async () => {
      if (!tokens || loading) return;
      const page = await getAllOffers();
      const items = Array.isArray(page?.body?.data?.content)
        ? page.body.data.content
        : [];
      if (active) {
        const resolvePhoto = (obj: any): string | undefined => {
          if (!obj) return undefined;
          if (typeof obj.storageKey === "string") return obj.storageKey;
          return undefined;
        };
        const normalized = (items as any[]).map((it) => {
          const firstPhoto =
            Array.isArray(it?.photos) && it.photos.length > 0
              ? resolvePhoto(it.photos[0])
              : undefined;
          const offerPhoto = buildPhotoUrl(firstPhoto);
          return { ...it, offerPhoto } as Offer;
        });
        const filtered = (normalized as any[]).filter((it) => {
          const ownerId = it?.owner?.id ?? it?.ownerId ?? null;
          const currentUserId = userInfo?.userId ?? null;
          if (!ownerId || !currentUserId) return true;
          return ownerId !== currentUserId;
        });
        setOffersData(filtered as Offer[]);
      }
    };
    load();
    return () => {
      active = false;
    };
  }, [tokens, loading, userInfo?.userId]);
  return (
    <View style={{ flex: 1 }}>
      <GestureHandlerRootView
        style={[styles.container, { backgroundColor: colors.background }]}
      >
        <Filter />
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
            key={offersData.length}
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
              addStorageOffer(offersData[cardIndex]);
              collapseCard();
            }}
          />
        </View>
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
