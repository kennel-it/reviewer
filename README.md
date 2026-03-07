# reviewer

Lo studente carica uno o più file `.java` tramite browser su questo server. Il backend analizza la struttura delle classi — campi, metodi, costruttori — e mostra un report a video. Il processo avviene in modo asincrono: il frontend riceve subito un Job ID e può seguire l'avanzamento in tempo reale.


## Stack tecnologico

| Strato | Tecnologia |
|---|---|
| Backend | Java 25 + Spring Boot 4 |
| Frontend | HTML5 + JS vanilla + CSS |
| Build | Maven 4 |
| Analisi | TODO |


## Architettura

### Flusso principale

1. Lo studente apre `http://ip_server:8080` e carica i file `.java` con un ID sessione
2. `POST /upload` valida i file, li salva sotto `/$TMPDIR/reviewer/{jobId}/` rispettando la struttura dei package, e lancia l'analisi in background
3. Il frontend viene reindirizzato a `/status.html?jobId=...` e fa polling su `GET /status/{jobId}`
4. Il worker aggiorna progressivamente lo stato del job; al termine il report è disponibile

### Componenti backend

- **`Upload`** — controller REST che gestisce il caricamento, estrae il package da ogni file e crea la struttura di directory corrispondente
- **`Status`** — controller REST che espone lo stato corrente del job (PENDING / RUNNING / DONE / ERROR) e la lista delle operazioni completate
- **`JobBroker`** — registro in-memory (`ConcurrentHashMap`) dei job attivi; genera ID univoci con suffisso progressivo in caso di collisione
- **`JobStatus`** — modello thread-safe del job: stato volatile + lista sincronizzata di operazioni
- **`AnalysisWorker`** — `@Async` service che esegue l'analisi nel proprio thread; aggiorna il broker a ogni passo

### Frontend

- **`index.html`** — form con drag-and-drop per i file `.java`; impedisce duplicati per nome; redirige al completamento dell'upload
- **`status.html`** — pagina di avanzamento con polling su `/status/{jobId}`
- **`style.css`** — tema dark, layout a card centrata, responsive

---

## Avvio rapido

```bash
./mvnw spring-boot:run
```

Il server si avvia sulla porta `8080`.

---

## TODO

- **Eliminare il redirect globale di `System.out` in `AnalysisWorker`**: i Tester dovrebbero raccogliere i risultati in una struttura dati propria invece di scrivere sullo standard output. `AnalysisWorker` leggerebbe quella struttura via reflection, senza bisogno di lock né di redirigere l'output globale della JVM. Ogni job gira già in un classloader isolato, quindi i dati sarebbero naturalmente separati e i job potrebbero girare in parallelo.

---

## Vincoli di progetto

- Nessun database: tutto in memoria, nessuna persistenza tra riavvii
- Nessuna autenticazione: uso esclusivamente locale/LAN
- File temporanei in `/$TMPDIR/reviewer/` (non ripuliti automaticamente)
