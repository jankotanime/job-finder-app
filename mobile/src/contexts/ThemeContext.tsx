import React, { useState, useEffect } from 'react'
import { MD3LightTheme as DefaultTheme, PaperProvider } from 'react-native-paper'
import { createContext, useContext } from 'react'
import { useColorScheme } from 'react-native'
import { themeDark, themeLight } from '../constans/theme'
import AsyncStorage from '@react-native-async-storage/async-storage'
import { tryCatch } from '../utils/try-catch'

type ThemeContextType = {
    theme: typeof DefaultTheme,
    toggleTheme: () => void,
    isDarkMode: boolean
}
const defaultTheme: ThemeContextType = {
    theme: DefaultTheme,
    toggleTheme: () => {},
    isDarkMode: false
}
const ThemeContext = createContext<ThemeContextType>(defaultTheme)

export const ThemeProvider = ({ children }: { children: React.ReactNode}) => {
    const systemTheme = useColorScheme()
    const [isDarkMode, setIsDarkMode] = useState<boolean>(systemTheme === 'dark')
    const theme = isDarkMode ? themeDark : themeLight
    const [isLoading, setIsLoading] = useState<boolean>(true)

    useEffect(() => {
         (async () => {
            const [userTheme, error] = await tryCatch(AsyncStorage.getItem("isDarkMode"))
            if (userTheme !== null) setIsDarkMode(JSON.parse(userTheme))
            if (error) console.error("error during saving theme: ", error)
            setIsLoading(false)
        })()
    }, [theme])

    const toggleTheme = async () => {
        const newThemeState = !isDarkMode
        setIsDarkMode(newThemeState)
        const [, error] = await tryCatch(AsyncStorage.setItem("isDarkMode", JSON.stringify(newThemeState)))
        if (error) console.error("error during toggling theme: ", error)
    }
    if (isLoading) return null
    return (
        <ThemeContext.Provider value={{ theme, toggleTheme, isDarkMode}}>
            <PaperProvider theme={theme}>
                {children}
            </PaperProvider>
        </ThemeContext.Provider>
    )
}
export const useTheme = () => useContext(ThemeContext)