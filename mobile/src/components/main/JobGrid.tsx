import React from "react";
import { View, Text, StyleSheet, FlatList, Dimensions } from "react-native";
import { data } from "../../constans/jobsDataTest";

const { width, height } = Dimensions.get("window");

const JobGrid = () => {
  const renderItem = ({ item }: any) => {
    return (
      <View style={styles.card}>
        <Text style={styles.title}>{item.title}</Text>
        <Text style={styles.salary}>${item.salary}</Text>
        <Text style={styles.location}>{item.location}</Text>
      </View>
    );
  };

  return (
    <FlatList
      data={data}
      keyExtractor={(item) => item.id}
      renderItem={renderItem}
      numColumns={2}
      columnWrapperStyle={styles.row}
      contentContainerStyle={styles.container}
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
    backgroundColor: "#f2f2f2",
    width: (width - 30) / 2,
    borderRadius: 8,
    padding: 20,
  },
  title: {
    fontSize: 16,
    fontWeight: "bold",
    marginBottom: 5,
  },
  salary: {
    fontSize: 14,
    color: "#333",
    marginBottom: 5,
  },
  location: {
    fontSize: 12,
    color: "#666",
  },
});
