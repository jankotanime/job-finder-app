import React from "react";
import { View, Text, StyleSheet, FlatList, Dimensions } from "react-native";
import { useTheme } from "react-native-paper";
import useJobStorage from "../../hooks/useJobStorage";

const { width, height } = Dimensions.get("window");

const JobGrid = () => {
  const { colors } = useTheme();
  const { storageJobs } = useJobStorage();

  const renderItem = ({ item }: any) => {
    return (
      <View style={[styles.card, { backgroundColor: colors.onBackground }]}>
        <Text style={[styles.title, { color: colors.primary }]}>
          {item.title}
        </Text>
        <Text style={[styles.salary, { color: colors.onSurface }]}>
          ${item.salary}
        </Text>
        <Text style={[styles.location, { color: colors.secondary }]}>
          {item.location}
        </Text>
      </View>
    );
  };

  return (
    <FlatList
      data={storageJobs}
      keyExtractor={(item) => item.id}
      renderItem={renderItem}
      numColumns={2}
      columnWrapperStyle={styles.row}
      contentContainerStyle={[
        styles.container,
        { backgroundColor: colors.background, paddingBottom: height * 0.129 },
      ]}
    />
  );
};

export default JobGrid;

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 10,
    paddingTop: height * 0.15,
  },
  row: {
    justifyContent: "space-between",
    marginBottom: 15,
  },
  card: {
    width: (width - 30) / 2,
    borderRadius: 12,
    padding: 20,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  title: {
    fontSize: 16,
    fontWeight: "bold",
    marginBottom: 8,
  },
  salary: {
    fontSize: 14,
    marginBottom: 5,
  },
  location: {
    fontSize: 12,
  },
});
