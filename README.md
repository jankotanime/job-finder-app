# job-finder-app

# JobFinderApp – uruchamianie i synchronizacja bazy danych przez Docker Compose

## Spis treści

- [Wymagania](#wymagania)
- [Struktura projektu](#struktura-projektu)
- [Uruchamianie aplikacji i bazy danych](#uruchamianie-aplikacji-i-bazy-danych)
- [Uruchamianie aplikacji mobilnej](#uruchamianie-aplikacji-mobilnej)
- [Aktualizacja danych w bazie](#aktualizacja-danych-w-bazie)
- [Eksport aktualnych danych do pliku `data.sql`](#eksport-aktualnych-danych-do-pliku-datasql)
- [Synchronizacja danych w zespole](#synchronizacja-danych-w-zespole)
- [Najczęstsze problemy](#najczęstsze-problemy)

---

## Wymagania

- Docker i Docker Compose (zalecana najnowsza wersja)
- Git
- Bash (do uruchamiania skryptów)
- IntelliJ IDEA (lub inne IDE obsługujące Spring Boot i PostgreSQL)

---

## Struktura projektu

```
JobFinderApp/
├── docker-compose.yml
├── data.sql
├── scripts/
    ├── extract_db.sh
    ├── wait-for-it.sh
├── spring-boot-service/
│   └── ...
└── ...
```

- `docker-compose.yml` – konfiguracja usług Docker Compose (baza, aplikacja, inicjalizacja).
- `data.sql` – plik z przykładowymi danymi do inicjalizacji bazy.
- `extract_db.sh` – skrypt do eksportu danych z bazy do pliku `data.sql`.
- `wait-for-it.sh` - skrypt oczekujący na uruchomienie wybranego serwisu.

---

## Uruchamianie aplikacji i bazy danych

1. **Sklonuj repozytorium:**

    ```sh
    git clone repo-url
    cd JobFinderApp
    ```

2. **Uruchom usługi:**
  + Pierwsze uruchomienie: baza zostanie zainicjalizowana danymi z `data.sql` za pomocą usługi `db-update`.

      ```sh
      docker compose --profile init up --build
      ```
    
  + Każde następne uruchomienie wykonujemy (bez serwisu db-update)
      ```sh
      docker compose up --build
      ```

---

## Uruchamianie aplikacji mobilnej

Uruchamianie za pomoca npx expo run:ios dla symulatora ios i :android dla androida, mozna uruchomic na swoim telefonie:
   1. Wejsc w folder mobile/
   2. ```npm install```
   3. Wlaczyc w ustawieniach telefonu tryb dewelopera
   4. Podlaczyc telefon do laptopa przez kabel
   5. Wpisac npx expo run:(ios/android) --device i zaznaczyc swoj telefon
   6. Dla macOS: npx expo run:ios wlaczy symulator

---

## Aktualizacja danych w bazie

Po zmianie pliku `data.sql` (np. po spullowaniu z repozytorium):

1. **Załaduj nowe dane do bazy:**

   ```sh
   docker compose up db-update
   ```

   lub

   ```sh
   docker exec -i database psql -U admin -d jobFinderApp < data.sql
   ```

---

## Eksport aktualnych danych do pliku `data.sql`

Aby zrzucić aktualne dane z bazy do pliku `data.sql`:

1. **Uruchom skrypt eksportujący:**

   ```sh
   ./scripts/extract_db.sh
   ```

   - Skrypt utworzy/nadpisze plik `data.sql` aktualnymi danymi z bazy.
2. **Zacommituj plik na repo:**

   ```sh
   git add data.sql
   git commit -m "Aktualizacja danych w bazie"
   git push
   ```

---

## Synchronizacja danych w zespole

1. **Pulluj najnowszy kod i plik `data.sql`:**

   ```sh
   git pull
   ```
2. **Załaduj dane do swojej bazy:**

   ```sh
   docker compose up db-update
   ```

   lub

   ```sh
   docker exec -i database psql -U admin -d jobFinderApp < data.sql
   ```

**Efekt:**
Każdy członek zespołu ma lokalnie tę samą bazę danych i rekordy, co w repozytorium.

---

## Najczęstsze problemy

- **Brak danych po restarcie kontenera:** Upewnij się, że wolumen `db-volume` jest skonfigurowany w `docker-compose.yml`.
- **Brak nowych rekordów po zmianie `data.sql`:** Uruchom usługę `db-update` lub wykonaj załadunek ręcznie.
- **Konflikty danych:** Przed eksportem danych wykonaj pull repozytorium, aby nie nadpisać zmian innych osób.
- **Brak tabel lub błędy SQL:** Sprawdź, czy plik `data.sql` jest poprawny (średniki, składnia, typy pól).

---

## FAQ

**Jak mogę sprawdzić dane w bazie z terminala?**

```sh
    docker exec -it database psql -U admin jobFinderApp
    # W środku psql:
    SELECT * FROM nazwa_tabeli;
```

**Jak zresetować bazę do stanu z `data.sql`?**

- Usuń wolumen:
  ```sh
  docker compose down -v
  docker compose up --build
  ```

---

## Kontakt

W razie pytań lub problemów – skontaktuj się z właścicielami repozytorium lub zostaw issue w GitHub.

### Created by Jan Gasztold, Maciej Adamski and Mikołaj Kalejta
