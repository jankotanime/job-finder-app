import {
  MD3LightTheme as LightTheme,
  MD3DarkTheme as DarkTheme,
} from "react-native-paper";

export const themeLight = {
  ...LightTheme,
  colors: {
    ...LightTheme.colors,
    primary: "#337EFF",
    onPrimary: "#ffffffff",
    primaryContainer: "#005effff",
    onPrimaryContainer: "#ffffffff",
    secondary: "#1B6EFF",
    onSecondary: "#1145a0ff",
    background: "#ebeeffff",
    onBackground: "#f6f8ffff",
    onSurface: "#000000",
    error: "#B00020",
    onError: "#ffffffff",
    onTertiary: "#6ea1f9ff",
  },
};
export const themeDark = {
  ...DarkTheme,
  colors: {
    ...DarkTheme.colors,
    primary: "#BB86FC",
    onPrimary: "#ffffffff",
    primaryContainer: "#3700B3",
    secondary: "#03DAC6",
    onSecondary: "#000000",
    background: "#121212",
    onBackground: "#322c3aff",
    surface: "#121212",
    onSurface: "#ffffffff",
    error: "#CF6679",
    onError: "#000000",
    onTertiary: "#cdaaf8ff",
  },
};
export default themeLight;
