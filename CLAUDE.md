# CLAUDE.md — Progetto `reviewer`

## Descrizione

`reviewer` è un tool web per l'analisi statica di classi Java scritte da studenti.
Gli studenti caricano uno o più file `.java`, il tool analizza come si comportano
le classi in un sistema funzionanate e mostra un report a video.


## Stack tecnologico

| Strato | Tecnologia | Motivazione |
|---|---|---|
| Backend | **Java 25+** con **Spring Boot** | Standard de facto, embedded server, zero config infrastrutturale |
| Frontend | HTML+JS+CSS | sistema base senza complicazioni ne librerie aggiuntive|
| Build | **Maven 4** | Ci prepariamo per il futuro prossimo |

> Il progetto gira in ambiente a basso carico (poche richieste al minuto, pochi KB a richiesta).

---

## Comportamento atteso (flusso principale)

1. Uno studente apre il browser su `http://ip_del_server:8080`
2. Carica uno o più file `.java` tramite form
3. Il backend usa JavaParser per estrarre la struttura delle classi
4. Il browser mostra il report con campi, metodi e costruttori di ogni classe

---

## Vincoli e linee guida per lo sviluppo

- **Nessun database:** i dati non vengono persistiti
- **Configurazione minimale:** `application.properties` deve restare semplice, tuning non troppo fine
- **No autenticazione:** il tool è ad uso locale/LAN scolastica
- **Dipendenze esterne minime:** preferire ciò che Spring Boot porta già con sé
- **Codice leggibile:** priorità alla chiarezza rispetto all'ottimizzazione

---

## Note per Claude

- Questo progetto deve rispettare gli standard, è più un proof-of-concept che un sistema in produzione
- L'utente conosce Java e Maven: non servono spiegazioni di base
- Evitare over-engineering: no pattern sovra complessi, no Docker
- Non cancellare i commenti che ho inserito io
