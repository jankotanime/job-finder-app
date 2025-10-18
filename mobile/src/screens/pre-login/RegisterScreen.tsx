import React, { useState } from "react"
import { View, Text, StyleSheet} from "react-native"
import ImageBackground from "../../components/reusable/ImageBackground"
import WhiteCard from "../../components/pre-login/WhiteCard"
import { useTranslation } from "react-i18next"
import { useTheme } from "react-native-paper"
import Input from "../../components/reusable/Input"
import { fieldsRegister } from "../../constans/formFields"

interface FormState {
    email: string
    password: string
    repeatPassword: string
}
const RegisterScreen = () => {
    const { t } = useTranslation()
    const theme = useTheme()
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
                <View>
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
    }
})