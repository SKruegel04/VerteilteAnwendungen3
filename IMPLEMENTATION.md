Vorbereitung:
- DB Browser und Redis Helper in IntelliJ installieren, ansonsten alternative Tools nutzen

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

Applikation ausführen mit:

```shell
java -jar target/verteilte-anwendungen-redis-1.0.0-SNAPSHOT-runner.jar
```

Zur Präsentation:
- In Postman ein Item zum Basket hinzufügen
- In IntelliJ "Redis Helper" öffnen (Vorher installieren)
  - Verbinden mit Host "localhost", Port "6379", Benutzername und Passwort leer
- DB0 doppelklicken (dort wo eine andere Zahl als 0 hintersteht)
- JSON-Array (Basket) in Redis zeigen

# Aufgabe 4

- Neues Changeset in liquibase-changelog.xml hinzugefügt
- Neue Tabelle `ORDER` mit den Spalten `ID`, `ITEMS`, `TOTAL`, `CREATED_AT`, `MODIFIED_AT` hinzugefügt
- Neue Tabelle `ITEM` mit den Spalten `ID`, `ORDER_ID`, `PRODUCT_NAME`, `PRODUCT_ID`, `COUNT`, `CREATED_AT`, `MODIFIED_AT` hinzugefügt
- ORDER und ITEM sind in einer Many-to-One Relation (Eine Order - Viele Items)
  - Dies wurde durch "<constraint foreignKeyName ..." ausgedrückt

# Aufgabe 5

- Entities "OrderEntity" und "ItemEntity" hinzugefügt
- Entities sind in einer Many-to-One Relation (Eine Order - Viele Items)
  - Dies wurde durch "@OneToMany" bzw. "@ManyToOne" ausgedrückt
- UserController hinzugefügt, um die User Balance beim Checkout anzupassen
- OrderController erweitert
  - getCompletedOrders wirft alle Bestellungen zurück
  - create erstellt eine neue Bestellung/speichert diese in der Datenbank
  - Die Methoden darunter sind nur für das Mapping von DTO zu Entity und zurück zuständig
- OrderResource erweitert
  - getCompletedOrders gibt alle Bestellungen zurück
- BasketController erweitert
  - checkout ruft die create Methode des OrderControllers auf um die Order in der Datenbank zu speichern
- 415er Status Codes in den Tests entfernt ("NOT_IMPLEMENTED")

Für die Präsentation:
- In Postman eine Bestellung aufgeben (Checkout)
- DB Browser in IntelliJ öffnen (Vorher installieren)
- Zur Datenbank verbinden (+ im DB Browser drücken, MySQL auswählen, Daten übergeben(siehe unten)) falls noch nicht geschehen
  - Host: localhost, Port: 3306, User: root, Password: geheim, Database: VA_APP
- Zur Tabelle "ORDER" navigieren (In DB Browser "Connection" -> "Schemas" -> "VA_APP" -> "ORDER")
  - Doppelklicken um die Daten zu sehen (GGF als Filter "ID > 0" einstellen!!)
- Zur Tabelle "ITEM" navigieren (In DB Browser "Connection" -> "Schemas" -> "VA_APP" -> "ITEM")
  - Doppelklicken um die Daten zu sehen (GGF als Filter "ID > 0" einstellen!!)