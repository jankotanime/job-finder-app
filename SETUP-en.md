# Fuchacz Setup - helper file for running the app

## Table of contents
- [Required tools](#required-tools)
- [Getting the application](#getting-the-application)
- [Running the application](#running-the-application)
- [Exporting current data](#exporting-current-data)
- [Common database issues](#common-database-issues)
- [Branch workflow: `main` → `production`](#branch-workflow-main--production)
- [FAQ](#faq)

## Required tools
- Docker and Docker Compose (latest version recommended)
- Git
- Bash (for running scripts)
- React Native

## Getting the application

### Cloning the repository
1. **Clone the repository:**
   ```sh
   git clone clone-url
   cd JobFinderApp
   ```

2. **Add variables:**
   + Create a `secrets` folder based on `secrets.template`. Each file should contain a secret string in a single line (e.g., example).
   + Create a `certs` folder based on `certs.template`. Provide your own `cert.pem` and `key.pem` files or generate them using a tool such as OpenSSL.
   + Create a `.env` file based on `.env.template`. Each variable should store the appropriate value.

3. **Clone the admin panel:**
   + Initialize the admin panel submodule:
   ```sh
   git submodule update --init --recursive
   ```

   + Update the admin panel:
    ```sh
    git submodule update
    ```

## Running the application

### Running containers
1. **First run** with database initialization from `data.sql` using the `db-update` service:
   ```sh
   docker compose --profile init up --build
   ```

2. Every next run:
   ```sh
   docker compose up --build
   ```

3. To run `spring-boot-service` and `website` with *`hot reload`*:
   ```sh
   docker compose up --build --watch
   ```

4. To run the database updater with `data.sql` (containers must be running):
   ```sh
   docker compose up db-update
   ```

### Running the mobile app
---
### IOS
Run with npx expo run:ios for iOS simulator and :android for Android; you can also run it on your phone:
   1. Go into the mobile/ folder
   2. ```npm install```
   3. Enable developer mode on your phone
   4. Connect the phone to the laptop with a cable
   5. Run npx expo run:(ios/android) --device and select your phone
   6. On macOS: npx expo run:ios will launch the simulator
See the mobile setup [here](mobile/README.md)

## Exporting current data
To dump current database data into `data.sql` run:
   ```sh
   ./scripts/extract_db.sh
   ```

## Common database issues

- **No data after container restart:** Make sure the `db-volume` volume is configured in `docker-compose.yml`.
- **No new records after changing `data.sql`:** Run the `db-update` service or load manually.
- **Data conflicts:** Before exporting data, pull the repository to avoid overwriting others' changes.

## Branch workflow: `main` → `production`

This section describes how to update the `production` branch, which is used **only for deployment**.

### Branch structure

---

### `main`
- **development** branch
- all new features and fixes land here
- the whole team works on it

### `production`
- **deployment** branch
- **no one works on it directly**
- contains **code currently being deployed to production**
- updated only based on `main`

### Updating `production`

   ```sh
   git fetch origin
   git checkout production
   git reset --hard origin/main
   git push origin production --force
   ```

## FAQ

**How can I check database data from the terminal?**

   ```sh
   docker exec -it database psql -U admin jobFinderApp
   SELECT * FROM table_name;
   ```

**How to reset the database to the state from `data.sql`?**

- Remove the volume:
  ```sh
  docker compose down -v
  docker compose up --build
  ```
