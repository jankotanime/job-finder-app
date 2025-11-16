import {
  MD3LightTheme as LightTheme,
  MD3DarkTheme as DarkTheme,
} from "react-native-paper";

export const themeLight = {
  ...LightTheme,
  colors: {
    ...LightTheme.colors,
    primary: "#337EFF",
    secondary: "#1B6EFF",
    onSecondary: "#1145a0ff",
    background: "#ebeeffff",
    onBackground: "#f3f5ffff",
    onSurface: "#000000",
    error: "#B00020",
    onError: "#ffffffff",
  },
};
export const themeDark = {
  ...DarkTheme,
  colors: {
    ...DarkTheme.colors,
    primary: "#BB86FC",
    onPrimary: "#3700B3",
    secondary: "#03DAC6",
    onSecondary: "#000000",
    background: "#121212",
    onBackground: "#1F1B24",
    surface: "#121212",
    onSurface: "#ffffffff",
    error: "#CF6679",
    onError: "#000000",
  },
};
export default themeLight;
