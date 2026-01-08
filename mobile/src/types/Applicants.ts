export type ApplicationItem = {
  id: string;
  status?: string;
  candidate?: {
    id?: string;
    name?: string;
    firstName?: string;
    lastName?: string;
    username?: string;
  };
  chosenCv?: {
    id?: string;
    storageKey?: string;
  };
};
