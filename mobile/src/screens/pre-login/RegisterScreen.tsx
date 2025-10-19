import React, { useState } from "react"
import { View, Text, StyleSheet, Dimensions, Pressable } from "react-native"
import ImageBackground from "../../components/reusable/ImageBackground"
import WhiteCard from "../../components/pre-login/WhiteCard"
import { useTranslation } from "react-i18next"
import { useTheme, Button } from "react-native-paper"
import Input from "../../components/reusable/Input"
import { fieldsRegister } from "../../constans/formFields"
import { useNavigation } from "@react-navigation/native"

interface FormState {
    email: string
    password: string
    repeatPassword: string
}
const { width, height } = Dimensions.get("window")
const RegisterScreen = () => {
    const { t } = useTranslation()
    const theme = useTheme()
    const navigation = useNavigation<any>()
    const [formState, setFormState] = useState<FormState>({
        email: "",
        password: "",
        repeatPassword: ""
    })
    return (
        <View>
            <ImageBackground />
            <WhiteCard>
                <View style={styles.header}>
                    <Text style={[styles.headerText, {color: theme.colors.primary}]}>
                        {t('register.get_started')}
                    </Text>
                </View>
                <View style={styles.main}>
                    {fieldsRegister(t).map((field) => (
                        <Input
                            key={field.key}
                            placeholder={field.placeholder}
                            value={formState[field.key as keyof FormState]}
                            onChangeText={(text) =>
                                setFormState((prev) => ({
                                    ...prev,
                                    [field.key]: text,
                                }))
                            }
                            mode='outlined'
                            secure={field.secure}
                        />
                    ))}
                </View>
                <Button
                    mode="contained"
                    style={styles.signUpButton}
                    contentStyle={{ height: 48 }}
                >
                    {t('signup')}
                </Button>
                <View style={styles.footer}>
                    <Text style={{ color: theme.colors.primary }}>
                        {t('register.sign_in_question')}
                    </Text>
                    <Pressable onPress={() => navigation.navigate("Login")}>
                        {({ pressed }) => (
                            <Text style={{
                                color: pressed ? theme.colors.primary : theme.colors.onSecondary,
                                fontWeight: '600',
                                marginLeft: 5
                            }}>
                                {t('signin')}
                            </Text>
                        )}
                    </Pressable>
                </View>
            </WhiteCard>
        </View>
    )
}

export default RegisterScreen

const styles = StyleSheet.create({
    header: {
        top: 30,
        justifyContent: 'center',
        alignItems: 'center',
    },
    headerText: {
        fontWeight: 'bold',
        textAlign: 'center',
        fontSize: 25,
    },
    main: {
        height: height * 0.35
    },
    signUpButton: {
        width: width * 0.8,
        height: 48,
        alignSelf: 'center'
    },
    footer: {
        flexDirection: 'row',
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: 15
    },
})