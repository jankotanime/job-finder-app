import React from "react"
import { Button } from "react-native-paper"

interface ButtonProps {
    text: string
    onPress?: () => void
    styles: object
    white?: boolean
}
const HomeLoginButton = (props: ButtonProps) => {
    return (
        <Button
            mode="text"
            style={props.styles}
            labelStyle={{ fontSize: 20 }}
            textColor={props.white ? 'white' : ''}
            onPress={props.onPress}
        >
            {props.text}
        </Button>
    )
}
export default HomeLoginButton