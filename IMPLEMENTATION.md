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