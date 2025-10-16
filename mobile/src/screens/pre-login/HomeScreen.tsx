import React from "react"
import { View, Text, StyleSheet } from "react-native"
import ImageBackground from "../../components/reusable/ImageBackground"
import { useTranslation } from "react-i18next"
import { Button } from "react-native-paper"

const HomeScreen = () => {
    const { t } = useTranslation()
    return (
        <View style={styles.container}>
            <ImageBackground />
            <Text style={styles.text}>{t('welcome')}</Text>
        </View>
    )
}

export default HomeScreen

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    text: {
        position: 'absolute',
        top: 60,
        left: 20,
        right: 20,
        textAlign: 'center',
        color: 'white',
        fontSize: 24,
        fontWeight: '700',
    }
})