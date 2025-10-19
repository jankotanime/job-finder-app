import React from "react"
import { View, StyleSheet, Dimensions } from "react-native"

const { width, height } = Dimensions.get("window")
const WhiteCard = ({ children }: { children: React.ReactNode }) => {
    return <View style={styles.card}>{children}</View>
}
export default WhiteCard

const styles = StyleSheet.create({
    card: {
        position: 'absolute',
        bottom: 0,
        height: height * 0.72,
        width: width,
        backgroundColor: 'white',
        borderTopLeftRadius: 45,
        borderTopRightRadius: 45,
    }
})