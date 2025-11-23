import React from "react";
import { Job } from "../../types/Job";
import Card from "./Card";
import { View, StyleSheet, Animated, ScrollView } from "react-native";
import CardContent from "./CardContent";

interface JobCardProps {
  item: Job;
  expandAnim: Animated.Value;
  isActive: boolean;
}
const JobCard = ({ item, expandAnim, isActive }: JobCardProps) => {
  return (
    <Card key={item.id} expandAnim={expandAnim}>
      {isActive ? (
        <ScrollView
          style={styles.container}
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
        >
          <CardContent item={item} isActive={isActive} />
        </ScrollView>
      ) : (
        <View style={styles.container2}>
          <CardContent item={item} isActive={isActive} />
        </View>
      )}
    </Card>
  );
};

export default JobCard;

const styles = StyleSheet.create({
  container: {
    width: "100%",
    paddingHorizontal: 0,
    paddingTop: 0,
    flex: 1,
  },
  container2: {
    width: "100%",
    paddingHorizontal: 0,
    alignItems: "center",
    paddingTop: 0,
    flex: 1,
  },
  scrollContent: {
    alignItems: "center",
    paddingBottom: 120,
  },
});
