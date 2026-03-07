# reviewer

Lo studente carica uno o più file `.java` tramite browser su questo server.
In base al pacchetto il server esegue una classe di test predefinita e mostra
un report in una pagina HTML.

---
## Come si avvia

Dai sorgenti `./mvnw spring-boot:run`,
oppure scaricando il paccheto `java -jar reviever-x.y.jar`

---

## Come funziona

## Vincoli di progetto

- Nessun database: tutto in memoria, nessuna persistenza tra riavvii
- Nessuna autenticazione: uso esclusivamente locale/LAN
- File temporanei in `/$TMPDIR/reviewer/` (non ripuliti automaticamente)

## Stack tecnologico

| Strato | Tecnologia |
|---|---|
| Backend | JDK Java 25 + Spring Boot 4|
| Frontend | HTML5 + JS vanilla + CSS |
| Build | Maven 4 |


## Architettura

### Flusso principale

1. Lo studente apre `http://ip_server:8080` e carica i file `.java` con un ID sessione
2. `POST /upload` valida i file, li salva sotto `/$TMPDIR/reviewer/{jobId}/` rispettando la struttura dei package, e lancia l'analisi in background
3. Il frontend viene reindirizzato a `/status.html?jobId=...` e fa polling su `GET /status/{jobId}`
4. Lo stato del lavoro viene aggiornato progressivamente

### Frontend

- **`index.html`** — form con drag-and-drop per i file `.java`; impedisce duplicati per nome; (aggiunge un progressivo) redirige al completamento dell'upload
- **`recent.html`** — mostra gli ultimi 20 valori ricevuti
- **`status.html`** — pagina di avanzamento con polling su `/status/{jobId}`
- **`testers.html`** — mostra le classi di test disponibili
- **`/mobilita`** — mostra la vista javadoc dell'API da creare per il pacchetto mobilita
- **`/sport`** — mostra la vista javadoc dell'API da creare per il pacchetto sport
