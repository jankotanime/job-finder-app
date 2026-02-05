import type { Offer } from "./Offer";
import type { ApplicationItem } from "./Applicants";
export type RootStackParamList = {
  Auth: undefined;
  Home: undefined;
  Login: undefined;
  Register: undefined;
  Main: undefined;
  Storage: undefined;
  MyProfile: undefined;
  LanguageMenu: undefined;
  ProfileCompletion: undefined;
  ProfileCompletionGoogle: {
    idToken: string;
    onRegisterSuccess: (username: string, phoneNumber: string) => Promise<void>;
  };
  SmsGoogleCode: undefined;
  AddOffer: undefined;
  EditProfile: undefined;
  YourOffersScreen: undefined;
  OfferManage: { offer: Offer };
  CvSelect: {
    disableSkip?: boolean;
  };
  CvPreview: {
    cvUri: string;
    cvName?: string;
    manage?: boolean;
    offerId?: string;
    applicant?: ApplicationItem;
  };
  ChosenApplicants: { offerId: string };
  CvMain: undefined;
  ChooseJobScreen: undefined;
  JobsContractor: undefined;
  JobsOwner: undefined;
};
