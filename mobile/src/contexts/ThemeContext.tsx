import React, { useState } from 'react'
import { MD3LightTheme as DefaultTheme, MD3DarkTheme as DarkTheme, PaperProvider } from 'react-native-paper'
import { createContext, useContext } from 'react'
import { useColorScheme } from 'react-native'

type ThemeContextType = {
    theme: typeof DefaultTheme,
    toggleTheme: () => void,
    isDarkMode: boolean
}
const defaultTheme: ThemeContextType = {
    theme: DefaultTheme,
    toggleTheme: () => {
        console.warn("useTheme hook called outside ThemeProvider")
    },
    isDarkMode: false
}
const ThemeContext = createContext<ThemeContextType>(defaultTheme)

export const ThemeProvider = ({ children }: { children: React.ReactNode}) => {
    const systemTheme = useColorScheme()
    const [isDarkMode, setIsDarkMode] = useState<boolean>(systemTheme === 'dark')
    const toggleTheme = () => setIsDarkMode(prev => !prev)
    const theme = isDarkMode ? DarkTheme : DefaultTheme

    return (
        <ThemeContext.Provider value={{ theme, toggleTheme, isDarkMode}}>
            <PaperProvider theme={theme}>
                {children}
            </PaperProvider>
        </ThemeContext.Provider>
    )
}
export const useTheme = () => useContext(ThemeContext)