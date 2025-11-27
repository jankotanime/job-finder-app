import { Animated } from "react-native";
import { createAnimation } from "./animationHelper";

type MakeExpandHandlersParams = {
  expandAnim: Animated.Value;
  getIsActive: () => boolean;
  setIsActive: (v: boolean) => void;
  isAnimatingRef: { current: boolean };
  animatingCardIndexRef: { current: number | null };
  getCurrentIndex: () => number;
};

export function makeExpandHandlers({
  expandAnim,
  getIsActive,
  setIsActive,
  isAnimatingRef,
  animatingCardIndexRef,
  getCurrentIndex,
}: MakeExpandHandlersParams) {
  const onExpand = () => {
    const currentIndex = getCurrentIndex();
    const isActive = getIsActive();
    if (
      isAnimatingRef.current &&
      animatingCardIndexRef.current === currentIndex
    )
      return;
    if (!isActive) {
      isAnimatingRef.current = true;
      animatingCardIndexRef.current = currentIndex;
      createAnimation(expandAnim, 1, 300).start(() => {
        setIsActive(true);
        isAnimatingRef.current = false;
        animatingCardIndexRef.current = null;
      });
    } else {
      isAnimatingRef.current = true;
      animatingCardIndexRef.current = currentIndex;
      setIsActive(false);
    }
  };

  const collapseCard = () => {
    const currentIndex = getCurrentIndex();
    if (
      isAnimatingRef.current &&
      animatingCardIndexRef.current === currentIndex
    )
      return;
    isAnimatingRef.current = true;
    animatingCardIndexRef.current = currentIndex;
    setIsActive(false);
  };

  return { onExpand, collapseCard };
}
