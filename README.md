# Verteilte Anwendungen Übungsaufgabe 3

Mögliche Punktzahl: 20 Punkte

## Deadlines

- 1. Zug 1. Gruppe: 09.01.2023
- 1. Zug 2. Gruppe: 12.01.2023
- 2. Zug 1. Gruppe: 10.01.2023
- 2. Zug 2. Gruppe: 10.01.2023 

## Aufgabestellung
In dieser Aufgabe erhalten Sie eine vorkonfigurierte Backend-Application, die von einem Webshop als Warenkorb sowie zum Abschluss einer Bestellung genutzt werden soll.
Die REST-Schnittstelle wurde von dem Auftraggeber spezifiziert und bereits in den Klassen
[BasketResource](src/main/java/de/berlin/htw/boundary/BasketResource.java) und
[OrderResource](src/main/java/de/berlin/htw/boundary/OrderResource.java)
umgesetzt.
Sie dürfen zwar die Methoden-Signatur ändern, jedoch nicht das spezifizierte Antwortverhalten über HTTP!

Das Geschäftsmodell des Auftraggebers basiert auf einer
Prepaid-Zahlungsmethode; d.h. die Kunden müssen erst ihr Kundenkonto aufladen bevor Sie das Geld für einen Artikel ausgeben können. 
Stellen Sie daher sicher, dass nielams ein Artikel zum Warenkorb hinzugefügt werden kann bzw. eine Bestellung aufgegeben werden kann, wenn das entsprechende Kundenkonto nicht gedeckt ist.

Zur Realisierung des Warenkorbs kommen zwei Technologien zum Einsatz:
- MySQL Datenbank: Die Datenbank wird zum langfristigen Speichern von Daten verwendet,
d.h. für Benutzerdaten inkl. dem Kontostand und für abgeschlossene Bestellungen.
- Redis Distributed Cache: Der Distributed Cache wird zum Speichern von sich schnell ändernden
sowie kurzlebigen Daten verwendet, d.h. für den Warenkorbinhalt der Benutzer.

1.  **(2P)** Wie Sie sicherlich bereits festgestellt haben, lässt sich das Projekt nicht so einfach mittels ``$ mvn package`` bauen und mit ``$ java -jar target/verteilte-anwendung-runner.jar`` starten. Dies liegt zum Einen an der fehlenden Datenbank und zum Anderen an dem fehlenden Redis Server! Schreiben Sie daher ein Docker Compose File mit dem Sie das
[offizielle Image der MySQL aus Docker Hub](https://hub.docker.com/_/mysql) sowie das
[offizielle Image des Redis aus Docker Hub](https://hub.docker.com/_/redis)
starten. Beachten Sie bitte die bereits vorhandenen Konfigurationen in der 
[application.properties](src/main/resources/application.properties). Wenn Sie die MySQL und den Redis Server korrekt gestartet haben, dann sollten Sie das Projekt bauen und die Integrationstests ausführen können.

2.  **(4P)** Eines der wichtigsten Aspekte einer Applikation im Internet ist die Sicherheit. Um auch diese Backend-Applikation vor Angreifern zu schützen, sollten Sie sämtliche Eingaben validieren. Dies kann auf unterschiedliche Arten geschehen. Nutzen Sie bitte vorrangig die BeanValidation und stellen Sie Sicher, dass folgende Richtlinien einghalten werden:
    - Ein Artikelname kann nicht länger als 255 Zeichen sein
    - Die Artikelnummer besteht aus 6 Zahlen, die durch ein Bindestrich getrennt sind (Beispiel: '1-2-3-4-5-6')
    - Der Peis muss immer zwischen 10 und 100 Euro liegen
    - Es können sich nicht mehr als 10 Artikel im Wahrenkorb befinden

    Schreiben Sie bitte mindestens 5 Integrationstest mit RestAssured, 
    die die Validierung der Eingabedaten prüfen.

3.  **(6P)** Implementieren Sie bitte die Warenkorbfunktionalität in
[BasketResource](src/main/java/de/berlin/htw/boundary/BasketResource.java) und
[BasketController](src/main/java/de/berlin/htw/control/BasketController.java).
Entscheiden Sie sich für eine geeignete [Datenstruktur](https://redis.io/docs/data-types/),
um die Artikel des Warenkorbs in Redis zu speichern.
Stellen sie unbedingt sicher, dass die Nutzer nicht den Warenkorb anderer Nutzer sehen 
oder überschreiben können.
Da wir den Warenkorb der Nutzer nicht langfristig speichern woll,
soll der Warenkorb mit einer Ablauffrist belegt werden. 
D.h. wenn innerhalb von 2 Minuten keine Änderungen (Neue Artikel in den Warenkorb,
Ändern der Anzahl, Löschen eines Artikels) am Warenkorb durchgeführt werden,
dann wird der gesamte Warenkorb eines Benutzers automatisch gelöscht.

4.  **(2P)** Zusätzlich zu den Kundendaten sollen auch
die abgeschlossenen Bestellungen in der MySQL gespeichert werden.
Hierzu muss das bestehende Schema erweitert werden.
Erstellen Sie daher ein weiteres 
[Liquibase ChangeSet](https://docs.liquibase.com/concepts/changelogs/xml-format.html) in 
[liquibase-changelog.xml](backend/src/main/resources/META-INF/liquibase-changelog.xml), das das Speichern einer Bestellung ermöglicht.

5.  **(6P)** Implementieren Sie bitte die Auftragserteilung in
[BasketResource.checkout()](src/main/java/de/berlin/htw/boundary/BasketResource.java) 
sowie das Abfragen der abgeschlossenen Bestellungen in
[OrderResource](src/main/java/de/berlin/htw/boundary/OrderResource.java).
Stellen Sie sicher, dass der Warenkorb nach der Bestellung leer ist.
Achten Sie auf die Transaction Boundary, damit
das Guthabenkonto der Kunden korrekt belastet werden. 


# Quarkus Get Started

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
$mvn compile quarkus:dev
```

> **_NOTE:_**  Quarkus ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
$mvn package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `$java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
$mvn package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `$java -jar target/*-runner.jar`.
