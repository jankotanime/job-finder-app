import type { Offer } from "./Offer";
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
  CvSelect: undefined;
  CvPreview: { cvUri: string; cvName?: string };
};
