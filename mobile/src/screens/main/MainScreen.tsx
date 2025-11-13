import React, { useRef } from "react";
import { View, StyleSheet, Text, Dimensions } from "react-native";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import Card from "../../components/main/Card";
import { Swiper, type SwiperCardRefType } from "rn-swiper-list";

const data = [
  { id: 1, name: "karta1" },
  { id: 2, name: "karta2" },
  { id: 3, name: "karta3" },
];
const { width, height } = Dimensions.get("window");

const MainScreen = () => {
  const swiperRef = useRef<SwiperCardRefType | null>(null);

  const renderCard = (item: any) => {
    return (
      <Card key={item.id}>
        <Text>{item.name}</Text>
      </Card>
    );
  };

  return (
    <GestureHandlerRootView style={styles.container}>
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
          onSwipeLeft={(cardIndex) => {
            console.log("onSwipeLeft", cardIndex);
          }}
          onSwipeTop={(cardIndex) => {
            console.log("onSwipeTop", cardIndex);
          }}
          onSwipeBottom={(cardIndex) => {
            console.log("onSwipeBottom", cardIndex);
          }}
          onSwipeActive={() => {
            console.log("onSwipeActive");
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
  },
  cardStyle: {
    width: width,
    height: height * 0.7,
  },
});
