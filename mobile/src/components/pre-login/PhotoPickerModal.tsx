import React from "react";
import { View, Text, TouchableOpacity, StyleSheet, Modal } from "react-native";
import { Ionicons } from "@expo/vector-icons";

interface Props {
  visible: boolean;
  onClose: () => void;
  onPickCamera?: () => void;
  onPickGallery?: () => void;
}

const PhotoPickerModal = ({
  visible,
  onClose,
  onPickCamera,
  onPickGallery,
}: Props) => {
  return (
    <Modal
      transparent
      animationType="fade"
      visible={visible}
      onRequestClose={onClose}
    >
      <View style={styles.overlay}>
        <View style={styles.modalBox}>
          <Text style={styles.title}>Wybierz źródło zdjęcia</Text>
          <View style={styles.buttonsRow}>
            <TouchableOpacity style={styles.tile} onPress={onPickCamera}>
              <Ionicons name="camera" size={38} color="#000" />
              <Text style={styles.tileText}>Kamera</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.tile} onPress={onPickGallery}>
              <Ionicons name="images" size={38} color="#000" />
              <Text style={styles.tileText}>Zdjęcia</Text>
            </TouchableOpacity>
          </View>
          <TouchableOpacity onPress={onClose}>
            <Text style={styles.cancel}>Anuluj</Text>
          </TouchableOpacity>
        </View>
      </View>
    </Modal>
  );
};

export default PhotoPickerModal;

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.4)",
    justifyContent: "center",
    alignItems: "center",
  },
  modalBox: {
    width: "80%",
    backgroundColor: "#fff",
    borderRadius: 16,
    paddingVertical: 20,
    paddingHorizontal: 16,
    alignItems: "center",
  },
  title: {
    fontSize: 18,
    marginBottom: 20,
    fontWeight: "500",
  },
  buttonsRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    width: "100%",
    marginBottom: 20,
  },
  tile: {
    flex: 1,
    backgroundColor: "#f5f5f5",
    paddingVertical: 18,
    marginHorizontal: 6,
    borderRadius: 12,
    justifyContent: "center",
    alignItems: "center",
  },
  tileText: {
    marginTop: 8,
    fontSize: 15,
  },
  cancel: {
    color: "red",
    fontSize: 16,
    marginTop: 5,
  },
});
