import React, { useState } from "react"
import { View, StyleSheet, Dimensions, Pressable } from "react-native"
import ImageBackground from "../../components/reusable/ImageBackground"
import WhiteCard from "../../components/pre-login/WhiteCard"
import { useTranslation } from "react-i18next"
import { useTheme, Button, Text } from "react-native-paper"
import Input from "../../components/reusable/Input"
import { fieldsLogin } from "../../constans/formFields"
import { useNavigation } from "@react-navigation/native"

interface FormState {
    email: string
    password: string
}
const { width, height } = Dimensions.get("window")
const LoginScreen = () => {
    const { t } = useTranslation()
    const theme = useTheme()
    const navigation = useNavigation<any>()
    const [formState, setFormState] = useState<FormState>({
        email: "",
        password: ""
    })
    return (
        <View>
            <ImageBackground />
            <WhiteCard>
                <View style={styles.header}>
                    <Text style={[styles.headerText, {color: theme.colors.primary}]}>
                        {t('login.welcome_back')}
                    </Text>
                </View>
                <View style={styles.main}>
                    {fieldsLogin(t).map((field) => (
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
                <View style={styles.forgot}>
                        <Pressable>
                            {({ pressed }) => (
                                <Text style={{
                                    color: pressed ? theme.colors.onSecondary : theme.colors.primary,
                                    fontWeight: '600',
                                    marginLeft: 5
                                }}>
                                    {t('login.forgot_password')}
                                </Text>
                            )}
                        </Pressable>
                    </View>
                <Button
                    mode="contained"
                    style={styles.signInButton}
                    contentStyle={{ height: 48 }}
                >
                    {t('signin')}
                </Button>
                <View style={styles.footer}>
                    <Text style={{ color: theme.colors.primary }}>
                        {t('login.sign_up_question')}
                    </Text>
                    <Pressable onPress={() => navigation.navigate("Register")}>
                        {({ pressed }) => (
                            <Text style={{
                                color: pressed ? theme.colors.primary : theme.colors.onSecondary,
                                fontWeight: '600',
                                marginLeft: 5
                            }}>
                                {t('signup')}
                            </Text>
                        )}
                    </Pressable>
                </View>
            </WhiteCard>
        </View>
    )
}

export default LoginScreen

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
        height: height * 0.3
    },
    signInButton: {
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
    forgot: {
        position: 'absolute',
        alignItems: 'flex-end',
        width: width * 0.88,
        top: height * 0.265
    }
})