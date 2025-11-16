import { useRef, useEffect } from "react";
import {
  Animated,
  View,
  StyleSheet,
  Dimensions,
  TouchableOpacity,
  Text,
} from "react-native";
import { useTheme } from "react-native-paper";
import JobGrid from "./JobGrid";

const { width, height } = Dimensions.get("window");

interface SwipeProps {
  enable: boolean;
  onClose: () => void;
}

const SwipeTopJobs = ({ enable, onClose }: SwipeProps) => {
  const { colors } = useTheme();
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
            backgroundColor: colors.surface,
          },
        ]}
      >
        <TouchableOpacity
          style={[
            styles.closeButton,
            {
              backgroundColor: colors.primary,
            },
          ]}
          onPress={disableAnim}
          accessibilityLabel="Close panel"
          hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
          activeOpacity={0.7}
        >
          <Text
            style={[
              styles.closeText,
              { color: colors.onPrimary, fontWeight: "bold" },
            ]}
          >
            ✕
          </Text>
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
    borderTopLeftRadius: 16,
    borderTopRightRadius: 16,
    overflow: "hidden",
    elevation: 6,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 6,
  },
  closeButton: {
    position: "absolute",
    top: 56,
    right: width * 0.04,
    width: 45,
    height: 45,
    borderRadius: 22,
    alignItems: "center",
    justifyContent: "center",
    elevation: 4,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
    zIndex: 20,
  },
  closeText: {
    fontSize: 20,
    lineHeight: 22,
  },
});
