# Aufgabe 1

- `docker-compose.yml` Datei im Root erstellt
- Docker muss laufen!
- MySQL Server eingefügt, Konfiguration folgt [application.properties](src/main/resources/application.properties) (ganz oben)
  
  ```yaml
  db:
    image: mysql
    # NOTE: use of "mysql_native_password" is not recommended: https://dev.mysql.com/doc/refman/8.0/en/upgrading-from-previous-series.html#upgrade-caching-sha2-password
    # (this is just an example, not intended to be a production configuration)
    command: --default-authentication-plugin=mysql_native_password
    environment:
      MYSQL_ROOT_PASSWORD: geheim
      MYSQL_DATABASE: VA_APP
    ports:
      - "3306:3306"
  ```
- Redis hinzugefügt, Konfiguration folgt auch [application.properties](src/main/resources/application.properties) (ganz unten)

  ```yaml
  redis:
    image: redis
    ports:
      - "6379:6379"
  ```
- Beides gestartet

  ```bash
  docker compose up -d
  ```
- Build ausgeführt (und damit die Integrationstests)

  ```bash
  mvn package
  ```
  
# Aufgabe 2

Zur Validierung wurden Validation-Annotations aus dem `jakarta.validation.constraints.`-Namespace verwendet

- `Item` hat folgende Annotations erhalten:
  - `@Size(max = 255)` für den Namen
    - Damit dürfen Artikelnamen nicht länger als 255 Zeichen sein
  - `@Pattern(regexp = "\\d-\\d-\\d-\\d-\\d-\\d")` für die Artikelnummer
    - Damit muss die Artikelnummer aus 6 Zahlen bestehen, die durch einen Bindestrich getrennt sind
  - `@Min(10)` und `@Max(100)` für den Preis
    - Damit muss der Preis zwischen 10 und 100 Euro liegen 
  - `@NotNull` für die o.g. Felder
    - Damit dürfen diese Felder nicht leer (null) sein
- `Basket` hat folgende Annotations erhalten:
  - `@Size(max = 10)` für die Anzahl der Artikel
    - Damit darf der Warenkorb nicht mehr als 10 Artikel enthalten
  - `@NotNull` für die o.g. Felder
    - Damit dürfen diese Felder nicht leer (null) sein

- In der `addItem` Methode in `BasketController` wurde die Validierung hinzugefügt
  - Damit wird die Validierung bei jedem Hinzufügen eines Artikels durchgeführt
  - Die Validierung wird durch die Annotation `@Valid` ausgelöst
  - Die Validierungsergebnisse werden in einem `Set<ConstraintViolation<Basket>>` gespeichert
  - Wenn das Set nicht leer ist, wird eine `ConstraintViolationException` geworfen

- Folgende Tests wurden in `BasketResourceTest` hinzugefügt
  - `testProductNameMaxLengthValidation`
  - `testProductIdValidationA`
  - `testProductIdValidationB`
  - `testPriceMinValidation`
  - `testPriceMaxValidation`

# Aufgabe 3

- `BasketController` wurde um die Methoden `getBasket`, `clearBasket`, `checkout`, `addItem`, `removeItem`, `changeItemCount` erweitert
  - `getBasket` gibt den Warenkorb des aktuellen Benutzers zurück
  - `clearBasket` löscht den Warenkorb des aktuellen Benutzers
  - `checkout` legt eine neue Bestellung an
    - Dazu wird der Warenkorb des aktuellen Benutzers ausgelesen
    - Die Artikel werden in die Bestellung übertragen
    - Die Bestellung wird in der Datenbank gespeichert
    - Der Warenkorb wird geleert
  - `addItem` fügt einen Artikel zum Warenkorb des aktuellen Benutzers hinzu
  - `removeItem` entfernt einen Artikel aus dem Warenkorb des aktuellen Benutzers
  - `changeCount` ändert die Anzahl eines Artikels im Warenkorb des aktuellen Benutzers
- Die Methoden spiegeln die entsprechenden Endpunkte in der `BasketResource` wieder
- In der `BasketResource` werden die entsprechenden Controller-Methoden aufgerufen, wenn er Endpunkt aufgerufen wird
- DATENTYP: Die Methoden speichern den Basket als eine Liste von Strings in Redis 
  - Die Liste ist eine Linked List und geht von Links nach Rechts
  - Die Liste enthält die Artikel als JSON-Strings, jeder String ist ein Artikel/Item im JSON-Format
- Die Liste wird in Redis gespeichert
  - Der Key inkooperiert den Username des Benutzers, z.B: "basket:testnutzer". So wird verhindert, dass die Nutzer Baskets anderer Nutzer sehen können.
  - Der Key hat eine Ablauffrist von 2 Minuten
  - Wenn der Key abläuft, wird der Warenkorb automatisch gelöscht
