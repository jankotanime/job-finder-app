import "./src/locales/i18n";
import React, { useEffect } from "react";
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import HomeScreen from "./src/screens/pre-login/HomeScreen";
import { ThemeProvider } from "./src/contexts/ThemeContext";
import LoginScreen from "./src/screens/pre-login/LoginScreen";
import RegisterScreen from "./src/screens/pre-login/RegisterScreen";
import MainScreen from "./src/screens/main/MainScreen";
import { AuthProvider } from "./src/contexts/AuthContext";
import AuthLoadingScreen from "./src/components/pre-login/AuthLoadingScreen";
import StorageScreen from "./src/screens/main/StorageScreen";
import MyProfile from "./src/screens/main/MyProfile";
import LanguageMenu from "./src/screens/main/LanguageMenu";
import ProfileCompletionFormScreen from "./src/screens/pre-login/ProfileCompletionFormScreen";
import ProfileCompletionGoogle from "./src/screens/pre-login/ProfileCompletionGoogle";
import { GoogleSignin } from "@react-native-google-signin/google-signin";

const Stack = createNativeStackNavigator();
export default function App() {
  useEffect(() => {
    GoogleSignin.configure({
      iosClientId: process.env.EXPO_PUBLIC_API_GOOGLE_ID_IOS,
      webClientId: process.env.EXPO_PUBLIC_API_GOOGLE_ID_WEB,
    });
  });
  return (
    <AuthProvider>
      <ThemeProvider>
        <NavigationContainer>
          <Stack.Navigator
            initialRouteName="Auth"
            screenOptions={{ headerShown: false }}
          >
            <Stack.Screen name="Auth" component={AuthLoadingScreen} />
            <Stack.Screen name="Home" component={HomeScreen} />
            <Stack.Screen name="Login" component={LoginScreen} />
            <Stack.Screen name="Register" component={RegisterScreen} />
            <Stack.Screen name="Main" component={MainScreen} />
            <Stack.Screen name="Storage" component={StorageScreen} />
            <Stack.Screen name="MyProfile" component={MyProfile} />
            <Stack.Screen name="LanguageMenu" component={LanguageMenu} />
            <Stack.Screen
              name="ProfileCompletion"
              component={ProfileCompletionFormScreen}
            />
            <Stack.Screen
              name="ProfileCompletionGoogle"
              component={ProfileCompletionGoogle}
            />
          </Stack.Navigator>
        </NavigationContainer>
      </ThemeProvider>
    </AuthProvider>
  );
}
