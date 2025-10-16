import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'
import AsyncStorage from '@react-native-async-storage/async-storage'
import * as RNLocalize from 'react-native-localize'
import en from './en.json'
import pl from './pl.json'

const LANGUAGE_DETECTOR = {
    type: 'languageDetector' as const,
    async: true,
    detect: async (callback: (lng: string) => void) => {
        try {
            const savedLanguage = await AsyncStorage.getItem('user-language')
            if (savedLanguage) {
                callback(savedLanguage)
                return
            }
            const deviceLanguage = RNLocalize.getLocales()[0]?.languageCode || 'en'
            callback(deviceLanguage)
        } catch (error) {
            console.error('Error detecting language:', error)
            callback('en')
        }
    },
    init: () => {},
    cacheUserLanguage: async (lng: string) => {
        try {
            await AsyncStorage.setItem('user-language', lng)
        } catch (error) {
            console.error('Error saving language:', error)
        }
    }
}

i18n
  .use(LANGUAGE_DETECTOR)
  .use(initReactI18next)
  .init({
    compatibilityJSON: 'v4',
    resources: {
      en: { translation: en },
      pl: { translation: pl }
    },
    fallbackLng: 'en',
    debug: false,
    interpolation: {
      escapeValue: false
    }
  })

export default i18n