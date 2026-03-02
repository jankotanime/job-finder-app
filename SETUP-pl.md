# Fuchacz Setup - plik pomocniczy w uruchomieniu

## Spis treści
- [Wymagane narzędzia](#wymagane-narzędzia)
- [Pobieranie aplikacji](#pobieranie-aplikacji)
- [Uruchamianie aplikacji](#uruchamianie-aplikacji)
- [Eksport aktualnych danych](#eksport-aktualnych-danych)
- [Najczęstsze problemy z bazą danych](#najczęstsze-problemy-z-bazą-danych)
- [Workflow gałęzi: `main` → `production`](#workflow-gałęzi-main--production)
- [FAQ](#faq)

## Wymagane narzędzia
- Docker i Docker Compose (zalecana najnowsza wersja)
- Git
- Bash (do uruchamiania skryptów)
- react native

## Pobieranie aplikacji

### Klonowanie repozytorium
1. **Sklonuj repozytorium:**

    ```sh
    git clone clone-url
    cd JobFinderApp
    ```

2. **Dodaj zmienne:**
   + Utwórz folder z sekretami `secrets` zgodnie z folderem `secrets.template`. Każdy plik powinien zawierać sekretny ciąg znaków w jednej linii (np. example).
   + Utwórz folder z certyfikatami `certs` zgodnie z folderem `certs.template`. Umieść własne pliki `cert.pem` i `key.pem` lub wygeneruj je za pomocą narzędzia (np. OpenSSL).
   + Utwórz plik `.env` zgodnie z plikiem `.env.template`. Każda zmienna powinna przechowywać odpowiednią wartość.

3. **Sklonuj admin-panel:**
   + Pobranie podmodułu admin panelu:
   ```sh
   git submodule update --init --recursive
   ```

   + Aktulizowanie admin panelu:
    ```sh
    git submodule update
    ```

## Uruchamianie aplikacji

### Uruchamianie kontenerów

1. **Pierwsze uruchomienie** z załączeniem bazy danych z `data.sql` za pomocą usługi `db-update`:
   ```sh
   docker compose --profile init up --build
   ```

2. Każde następne uruchomienie wykonujemy za pomocą:
   ```sh
   docker compose up --build
   ```

3. Aby uruchomić serwis `spring-boot-service` oraz `website` z *`hot reload`* wykonujemy
   ```sh
   docker compose up --build --watch
   ```

4. Aby uruchomić aktulizator bazy danych z `data.sql` wykonujemy (kontenery muszą być uruchomione):
   ```sh
   docker compose up db-update
   ```

### Uruchamianie aplikacji mobilnej

Uruchamianie za pomoca npx expo run:ios dla symulatora ios i :android dla androida, mozna uruchomic na swoim telefonie:
   1. Wejsc w folder mobile/
   2. ```npm install```
   3. Wlaczyc w ustawieniach telefonu tryb dewelopera
   4. Podlaczyc telefon do laptopa przez kabel
   5. Wpisac npx expo run:(ios/android) --device i zaznaczyc swoj telefon
   6. Dla macOS: npx expo run:ios wlaczy symulator

Zobacz dokumentację uruchomienia aplikacji mobilnej [tutaj](mobile/README.md)

## Eksport aktualnych danych

Aby zrzucić aktualne dane z bazy danych do pliku `data.sql` wykonujemy:
   ```sh
   ./scripts/extract_db.sh
   ```

## Najczęstsze problemy z bazą danych

- **Brak danych po restarcie kontenera:** Upewnij się, że wolumen `db-volume` jest skonfigurowany w `docker-compose.yml`.
- **Brak nowych rekordów po zmianie `data.sql`:** Uruchom usługę `db-update` lub wykonaj załadunek ręcznie.
- **Konflikty danych:** Przed eksportem danych wykonaj pull repozytorium, aby nie nadpisać zmian innych osób.

## Workflow gałęzi: `main` → `production`

Ten fragment opisuje sposób aktualizowania gałęzi `production`, która służy **wyłącznie do deployu**.

### Struktura gałęzi

---

### `main`
- gałąź **rozwojowa**
- trafiają tu wszystkie nowe funkcjonalności i poprawki
- na niej pracuje cały zespół

### `production`
- gałąź **deployowa**
- **nikt nie pracuje na niej bezpośrednio**
- zawiera **kod aktualnie wdrażany na produkcję**
- jest aktualizowana wyłącznie na podstawie `main`

###  Aktualizacja `production`

   ```sh
   git fetch origin
   git checkout production
   git reset --hard origin/main
   git push origin production --force
   ```

## FAQ

**Jak mogę sprawdzić dane w bazie z terminala?**

   ```sh
   docker exec -it database psql -U admin jobFinderApp
   SELECT * FROM nazwa_tabeli;
   ```

**Jak zresetować bazę do stanu z `data.sql`?**

- Usuń wolumen:
  ```sh
  docker compose down -v
  docker compose up --build
  ```
