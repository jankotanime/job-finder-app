# — Developer guide

This README gathers quick developer instructions for the `/` part of the project (Expo + React Native + TypeScript).

---

## Prerequisites

- Node.js (recommended LTS)
- npm (or yarn)
- Expo CLI if you run the app locally: `npm install -g expo-cli` (optional if using `npx expo`)
- Xcode / Android Studio for device simulators (only if you run native builds)

## Install dependencies

Run from `/` directory:

```bash
npm ci
```

## Run the Expo app (development)

```bash
npm --prefix start
```

This runs the Expo bundler. Use QR code or simulator buttons to open on device/emulator.

## Run tests

We use Jest + jest-expo + @testing-library/react-native for tests.

```bash
npm --prefix test

# update snapshots
npm --prefix test -- -u
```

If tests fail locally, first run `npm --prefix ci` to ensure dependencies match.

## Lint and types

If available, run project lint and typecheck scripts (check `/package.json` for exact commands):

```bash
npm --prefix run lint
npm --prefix run typecheck
```

If those scripts are missing, the repo often uses `eslint` + `prettier`. Add scripts in `package.json` for convenience.

## Husky & pre-commit hooks

This project uses Husky to run checks before committing. The hook will run the commands defined in `/.husky/pre-commit` (or the root `.husky` if present).

If your local commits fail due to Husky, confirm:

1. You have installed dependencies: `npm --prefix ci`
2. You have the same Node/npm versions as the project (use `nvm` if needed)

Useful command to run hooks manually (from repo root):

```bash
SH=/.husky/pre-commit bash -e
```

or simply run the same scripts Husky would run, e.g. `npm --prefix test`.

## Auth notes

- Tokens are stored using `react-native-encrypted-storage` (key: `auth`).
- `AuthContext` exposes `signIn`, `signUp`, `signOut`. These return an object like `{ ok: boolean, error?: string }`.
- The client expects the server to return either a token object or an error object. If your API returns a 200 with an error body, the client will parse and surface it — prefer returning standard HTTP error codes (4xx/5xx) or a consistent body `{ ok: false, error: string, i18nKey?: string }`.

## i18n (localization)

- Translations are located in `/src/locales/` (e.g. `en.json`, `pl.json`).
- Prefer backend returning an `i18nKey` for any user-visible error; frontend will translate it using `react-i18next`.

How to add a translation:

1. Add key to both `en.json` and `pl.json`.
2. Use `t('path.to.key')` in components.

Example test command:

```bash
npm --prefix test -- /src/tests/Input.test.tsx
```
