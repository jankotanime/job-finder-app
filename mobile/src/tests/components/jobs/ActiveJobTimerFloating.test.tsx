/// <reference types="jest" />

import React from "react";
import { render, fireEvent, waitFor } from "@testing-library/react-native";
import { Animated } from "react-native";
import ActiveJobTimerFloating from "../../../components/jobs/ActiveJobTimerFloating";

const mockNavigate = jest.fn();
const mockGetActiveJobTimer = jest.fn();
const mockGetContractorFinishedLocally = jest.fn();
const mockClearActiveJobTimer = jest.fn();

jest.mock("@react-navigation/native", () => ({
  useNavigation: () => ({
    navigate: mockNavigate,
  }),
  useFocusEffect: (cb: () => void) => cb(),
}));

jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

jest.mock("../../../contexts/AuthContext", () => ({
  useAuth: () => ({
    userInfo: { username: "john" },
    user: "john",
  }),
}));

jest.mock("../../../utils/jobTimerStorage", () => ({
  getActiveJobTimer: (...args: any[]) => mockGetActiveJobTimer(...args),
  clearActiveJobTimer: (...args: any[]) => mockClearActiveJobTimer(...args),
}));

jest.mock("../../../utils/jobLocalCompletion", () => ({
  getContractorFinishedLocally: (...args: any[]) =>
    mockGetContractorFinishedLocally(...args),
}));

describe("ActiveJobTimerFloating component", () => {
  let animatedTimingSpy: jest.SpyInstance;

  beforeEach(() => {
    mockNavigate.mockReset();
    mockGetActiveJobTimer.mockReset();
    mockGetContractorFinishedLocally.mockReset();
    mockClearActiveJobTimer.mockReset();

    animatedTimingSpy = jest.spyOn(Animated, "timing").mockReturnValue({
      start: (cb?: (result: { finished: boolean }) => void) =>
        cb && cb({ finished: true }),
      stop: jest.fn(),
      reset: jest.fn(),
    } as any);
  });

  afterEach(() => {
    animatedTimingSpy.mockRestore();
  });

  it("renders nothing when there is no active timer", async () => {
    mockGetActiveJobTimer.mockResolvedValue(null);

    const { queryByLabelText } = render(<ActiveJobTimerFloating />);

    await waitFor(() => {
      expect(queryByLabelText("jobs.timerBanner.title")).toBeNull();
    });
  });

  it("opens and navigates to running job", async () => {
    mockGetContractorFinishedLocally.mockResolvedValue(false);
    mockGetActiveJobTimer.mockResolvedValue({
      jobId: "job-1",
      role: "owner",
      startedAt: Date.now() - 5000,
    });

    const { getByLabelText, getByText } = render(<ActiveJobTimerFloating />);

    await waitFor(() => {
      expect(getByLabelText("jobs.timerBanner.title")).toBeTruthy();
    });

    fireEvent.press(getByLabelText("jobs.timerBanner.title"));
    fireEvent.press(getByText("jobs.timerBanner.cta"));

    expect(mockNavigate).toHaveBeenCalledWith("JobRun", {
      jobId: "job-1",
      role: "owner",
      startedAt: expect.any(Number),
    });
  });
});
