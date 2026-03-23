/// <reference types="jest" />

import React from "react";
import { render, fireEvent, waitFor, act } from "@testing-library/react-native";
import { Animated } from "react-native";
import ActiveJobTimerFloating from "../../../components/jobs/ActiveJobTimerFloating";

const mockNavigate = jest.fn();
const mockGetActiveJobTimer = jest.fn();
const mockGetContractorFinishedLocally = jest.fn();
const mockClearActiveJobTimer = jest.fn();
const mockAuthState: { userInfo?: { username?: string }; user?: string } = {
  userInfo: { username: "john" },
  user: "john",
};

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
    userInfo: mockAuthState.userInfo,
    user: mockAuthState.user,
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
    mockAuthState.userInfo = { username: "john" };
    mockAuthState.user = "john";

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
    let resolveTimerFetch: (value: any) => void;
    mockGetActiveJobTimer.mockImplementation(
      () =>
        new Promise((resolve) => {
          resolveTimerFetch = resolve;
        }),
    );

    const { queryByLabelText } = render(<ActiveJobTimerFloating />);

    await act(async () => {
      resolveTimerFetch!(null);
      await Promise.resolve();
    });

    await waitFor(() => {
      expect(queryByLabelText("jobs.timerBanner.title")).toBeNull();
    });
  });

  it("opens and navigates to running job", async () => {
    mockGetContractorFinishedLocally.mockResolvedValue(false);
    let resolveTimerFetch: (value: any) => void;
    mockGetActiveJobTimer.mockImplementation(
      () =>
        new Promise((resolve) => {
          resolveTimerFetch = resolve;
        }),
    );

    const { getByLabelText, getByText } = render(<ActiveJobTimerFloating />);

    await act(async () => {
      resolveTimerFetch!({
        jobId: "job-1",
        role: "owner",
        startedAt: Date.now() - 5000,
      });
      await Promise.resolve();
    });

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

  it("clears contractor timer when job is finished locally", async () => {
    mockGetContractorFinishedLocally.mockResolvedValue(true);
    mockGetActiveJobTimer.mockResolvedValue({
      jobId: "job-2",
      role: "contractor",
      startedAt: Date.now() - 2000,
    });

    const { queryByLabelText } = render(<ActiveJobTimerFloating />);

    await waitFor(() => {
      expect(mockClearActiveJobTimer).toHaveBeenCalledWith("job-2", "john");
      expect(queryByLabelText("jobs.timerBanner.title")).toBeNull();
    });
  });

  it("hides banner when refresh throws", async () => {
    mockGetActiveJobTimer.mockRejectedValue(new Error("boom"));

    const { queryByLabelText } = render(<ActiveJobTimerFloating />);

    await waitFor(() => {
      expect(queryByLabelText("jobs.timerBanner.title")).toBeNull();
    });
  });

  it("uses trimmed fallback username from user when userInfo is missing", async () => {
    mockAuthState.userInfo = undefined;
    mockAuthState.user = "  jane  ";
    mockGetActiveJobTimer.mockResolvedValue(null);

    render(<ActiveJobTimerFloating />);

    await waitFor(() => {
      expect(mockGetActiveJobTimer).toHaveBeenCalledWith("jane");
    });
  });

  it("keeps timer visible for contractor when not finished locally", async () => {
    mockGetContractorFinishedLocally.mockResolvedValue(false);
    mockGetActiveJobTimer.mockResolvedValue({
      jobId: "job-3",
      role: "contractor",
      startedAt: Date.now() - 3000,
    });

    const { getByLabelText } = render(<ActiveJobTimerFloating />);

    await waitFor(() => {
      expect(getByLabelText("jobs.timerBanner.title")).toBeTruthy();
      expect(mockClearActiveJobTimer).not.toHaveBeenCalled();
    });
  });
});
