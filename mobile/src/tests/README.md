# Test structure

Tests are grouped by feature area so it is easier to find related coverage quickly.

## Folders

- `components/reusable` - shared UI components used across many screens
- `components/pre-login` - components used before authentication
- `components/main` - components used in authenticated main views
- `components/jobs` - components related to running and timing jobs
- `components/filter` - offer filtering UI and filtering logic components

`components/main` also contains grouped test subfolders:

- `main/swipe` - swipe feedback components
- `main/offers` - storage offers and applicants UI

## Current key component coverage

- `Input`
- `Error`
- `ErrorNotification`
- `ImageBackground`
- `HomeLoginButton`
- `PhotoPickerModal`
- `GoogleButton`
- `GoogleLoginButton`
- `WhiteCard`
- `AddOfferButton`
- `CvChoseButton`
- `JobManageButton`
- `ActiveJobTimerFloating`
- `Filter`
- `FilterCollapsibleSection`
- `FilterContent`
- `OnSwipeLeft`
- `OnSwipeRight`
- `OnSwipeBottom`
- `OfferGrid`
- `RenderApplicant`
