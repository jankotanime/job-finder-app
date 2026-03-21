/// <reference types="jest" />

import { render } from "@testing-library/react-native";
import Error from "../../../components/reusable/Error";

jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key: string) => `translated:${key}`,
  }),
}));

describe("Error component", () => {
  it("renders correctly with a raw error string", () => {
    const { toJSON, getByText } = render(<Error error={"This is an error"} />);
    expect(getByText("This is an error")).toBeTruthy();
    expect(toJSON()).toMatchSnapshot();
  });

  it("renders correctly with a translation key", () => {
    const { toJSON, getByText } = render(
      <Error error={"translated:errors.login_failed"} />,
    );
    expect(getByText("translated:errors.login_failed")).toBeTruthy();
    expect(toJSON()).toMatchSnapshot();
  });
});
