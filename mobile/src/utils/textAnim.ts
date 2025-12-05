import { Animated } from "react-native";
import { createAnimation } from "./animationHelper";

interface TextAnimProps {
  text: string;
  setWords: React.Dispatch<React.SetStateAction<string[]>>;
  setAnimatedValues: React.Dispatch<React.SetStateAction<Animated.Value[]>>;
}
const makeTextAnim = ({ text, setWords, setAnimatedValues }: TextAnimProps) => {
  const split = text.trim().split(" ");
  setWords(split);
  const values = split.map(() => new Animated.Value(0));
  setAnimatedValues(values);
  const animations = values.map((value, index) => {
    return Animated.sequence([
      createAnimation(value, 1, 500, index * 150, true),
      Animated.delay(4000),
      createAnimation(value, 0, 500, 0, true),
    ]);
  });
  Animated.loop(Animated.stagger(150, animations)).start();
};
export default makeTextAnim;
