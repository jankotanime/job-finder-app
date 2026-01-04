import { View, StyleSheet, Dimensions } from "react-native";
import AddOfferButton from "./AddOfferButton";
import CvChoseButton from "./CvChoseButton";
import JobManageButton from "./JobManageButton";
import { SafeAreaView } from "react-native-safe-area-context";
import { useTheme } from "react-native-paper";

const { width, height } = Dimensions.get("window");
const Footer = () => {
  const { colors } = useTheme();
  return (
    <View style={[styles.footer, { backgroundColor: colors.onBackground }]}>
      <CvChoseButton />
      <AddOfferButton />
      <JobManageButton />
    </View>
  );
};

export default Footer;

const styles = StyleSheet.create({
  footer: {
    position: "absolute",
    bottom: height * 0.03,
    flexDirection: "row",
    justifyContent: "space-around",
    alignItems: "center",
    alignSelf: "center",
    width: width * 0.9,
    height: height * 0.07,
    borderRadius: 20,
  },
});
