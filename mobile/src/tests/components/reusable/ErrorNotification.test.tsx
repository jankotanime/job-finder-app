/// <reference types="jest" />

import { render } from "@testing-library/react-native";
import { fireEvent } from "@testing-library/react-native";
import ErrorNotification from "../../../components/reusable/ErrorNotification";

const mockCreateAnimation = jest.fn();

jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key: string) => `translated:${key}`,
  }),
}));

jest.mock("../../../utils/animationHelper", () => ({
  createAnimation: (...args: any[]) => mockCreateAnimation(...args),
}));

beforeEach(() => {
  mockCreateAnimation.mockReset();
  mockCreateAnimation.mockReturnValue({ start: jest.fn() });
});

describe("ErrorNotification component", () => {
  it("renders correctly with a raw error string", () => {
    const { toJSON } = render(<ErrorNotification error={"This is an error"} />);
    expect(toJSON()).toMatchSnapshot();
  });
  it("renders correctly with a translation key", () => {
    const { toJSON } = render(
      <ErrorNotification error={"translated:errors.login_failed"} />,
    );
    expect(toJSON()).toMatchSnapshot();
  });

  it("calls cancel on touch end", () => {
    const { getByTestId } = render(
      <ErrorNotification error={"This is an error"} />,
    );
    const el = getByTestId("error-notification");
    expect(el).toBeDefined();
    fireEvent(el, "onTouchEnd");
    expect(mockCreateAnimation).toHaveBeenCalledWith(
      expect.anything(),
      -200,
      500,
      0,
      true,
    );
  });
});
