import { MD3LightTheme as LightTheme, MD3DarkTheme as DarkTheme } from 'react-native-paper'

export const themeLight = {
    ...LightTheme,
    colors: {
        ...LightTheme.colors,
        primary: '#337EFF',
        secondary: '#1B6EFF',
        onSecondary: '#1145a0ff'
    }
}
export const themeDark = {
    ...DarkTheme,
    colors: {
        ...DarkTheme.colors,
        primary: '#1b4791ff',
        secondary: '#0e3c8aff',
        onSecondary: '#1B6EFF'
    }
}
export default themeLight