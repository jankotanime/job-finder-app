/// <reference types="jest" />

import { render } from "@testing-library/react-native";
import Error from "../components/reusable/Error";
import { useTranslation } from "react-i18next";

jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key: string) => `translated:${key}`,
  }),
}));

describe("Error component", () => {
  it("renders correctly with a raw error string", () => {
    const { toJSON } = render(<Error error={"This is an error"} />);
    expect(toJSON()).toMatchSnapshot();
  });
  it("renders correctly with a translation key", () => {
    const { t } = useTranslation();
    const { toJSON } = render(<Error error={t("errors.login_failed")} />);
    expect(toJSON()).toMatchSnapshot();
  });
});
