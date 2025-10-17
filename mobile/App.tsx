import './src/locales/i18n'
import React from 'react'
import { NavigationContainer } from '@react-navigation/native'
import { createNativeStackNavigator } from '@react-navigation/native-stack'
import HomeScreen from './src/screens/pre-login/HomeScreen'
import { ThemeProvider } from './src/contexts/ThemeContext'
import LoginScreen from './src/screens/pre-login/LoginScreen'
import RegisterScreen from './src/screens/pre-login/RegisterScreen'

const Stack = createNativeStackNavigator()
export default function App() {
  return (
    <ThemeProvider>
      <NavigationContainer>
        <Stack.Navigator initialRouteName="Home" screenOptions={{ headerShown: false }}>
          <Stack.Screen name="Home" component={HomeScreen} />
          <Stack.Screen name="Login" component={LoginScreen} />
          <Stack.Screen name="Register" component={RegisterScreen} />
        </Stack.Navigator>
      </NavigationContainer>
    </ThemeProvider>
  )
}