import { useRef, useEffect } from "react";
import {
  Animated,
  View,
  StyleSheet,
  Dimensions,
  TouchableOpacity,
  Text,
} from "react-native";
import JobGrid from "./JobGrid";

const { width, height } = Dimensions.get("window");
interface SwipeProps {
  enable: boolean;
  onClose: () => void;
}
const SwipeTopJobs = ({ enable, onClose }: SwipeProps) => {
  const slideAnim = useRef(new Animated.Value(height + 100)).current;
  const enableAnim = () => {
    Animated.timing(slideAnim, {
      toValue: 0,
      duration: 300,
      useNativeDriver: true,
    }).start();
  };
  const disableAnim = () => {
    Animated.timing(slideAnim, {
      toValue: height + 100,
      duration: 300,
      useNativeDriver: true,
    }).start(() => {
      onClose();
    });
  };
  useEffect(() => {
    if (enable) enableAnim();
  }, [enable]);
  return (
    <View style={{ flex: 1 }}>
      <Animated.View
        style={[
          styles.box,
          {
            transform: [{ translateY: slideAnim }],
          },
        ]}
      >
        <TouchableOpacity
          style={styles.closeButton}
          onPress={() => disableAnim()}
          accessibilityLabel="Close panel"
          hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
        >
          <Text style={styles.closeText}>✕</Text>
        </TouchableOpacity>
        <JobGrid />
      </Animated.View>
    </View>
  );
};

export default SwipeTopJobs;

const styles = StyleSheet.create({
  box: {
    position: "absolute",
    height: height,
    left: -width * 0.5001,
    width: width,
    backgroundColor: "gray",
  },
  closeButton: {
    position: "absolute",
    top: 56,
    right: 18,
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: "#ffffffee",
    alignItems: "center",
    justifyContent: "center",
    zIndex: 20,
    elevation: 6,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
  },
  closeText: {
    fontSize: 18,
    color: "#222",
    lineHeight: 20,
  },
});
