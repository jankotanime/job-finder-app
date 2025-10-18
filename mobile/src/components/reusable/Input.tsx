import React from "react"
import { TextInput } from "react-native-paper"
import { StyleSheet, Dimensions } from "react-native"

interface TextInputProps {
    placeholder: string
    secure?: boolean
    onChangeText?: (text: string) => void
    mode: "flat" | "outlined"
    value: string
}
const { width, height } = Dimensions.get("window")
const Input = (props: TextInputProps) => {
    return (
        <TextInput
            label={props.placeholder}
            value={props.value}
            mode={props.mode}
            onChangeText={props.onChangeText}
            style={styles.input}
            secureTextEntry={props.secure}
            theme={{ roundness: 20 }}
        />
    )
}

export default Input

const styles = StyleSheet.create({
    input: {
        alignSelf: 'center',
        width: width * 0.85,
        borderRadius: 12,
        marginTop: 10,
        top: height * 0.06
    }
})