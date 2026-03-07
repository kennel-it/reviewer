# reviewer — guida per Samuele

## Cos'è

Un'applicazione web che prende i file `.java` di uno studente, li compila al volo,
esegue una classe di test predefinita e mostra i risultati in una pagina HTML.
Nessun database, nessuna autenticazione, gira sulla LAN scolastica.

---

## Come si avvia

```bash
./mvnw spring-boot:run
```

Poi apri `http://localhost:8080` nel browser. Serve il **JDK** (non solo la JRE):
il backend usa `javax.tools.JavaCompiler`, che è disponibile solo con il JDK installato.

---

## Cosa succede quando uno studente carica i file

```
Browser                     Backend
  |                            |
  |-- POST /upload ----------->|  1. valida l'ID e i file
  |                            |  2. salva i .java in /tmp/reviewer/{jobId}/
  |                            |     rispettando la struttura dei package
  |                            |  3. copia lì dentro la classe Tester giusta
  |                            |  4. avvia l'analisi in background (@Async)
  |<-- { jobId: "mario_1" } ---|
  |                            |
  |-- GET /status/{jobId} ---->|  polling ogni secondo
  |<-- { stato, operazioni } --|
  |                            |
  ... ripete finché DONE o ERROR
```

L'upload risponde subito con un `jobId`. Il frontend viene reindirizzato a
`/status.html?jobId=...` e fa polling finché lo stato non diventa `DONE` o `ERROR`.

---

## Cosa fa il worker in background

`AnalysisWorker.analyze()` — eseguito in un thread separato grazie a `@Async`:

1. **Compilazione** — raccoglie tutti i `.java` presenti nella directory di lavoro
   (sorgenti dello studente + classe Tester) e li compila in un'unica passata con
   `javax.tools.JavaCompiler`. I `.class` finiscono nella stessa cartella.
   Per ogni file produce un'operazione `Compilato: X.java` (verde) o
   `Errore compilazione: X.java` (rosso). Se anche uno solo fallisce, si ferma.

2. **Esecuzione del Tester** — carica i `.class` appena compilati con un
   `URLClassLoader`, trova il metodo `main` della classe Tester tramite reflection
   e lo invoca. L'output su `System.out` viene catturato riga per riga.

3. **Parsing dell'output** — ogni riga diventa un'operazione visibile nella pagina:
   - finisce con `OK` → verde
   - finisce con `ERRORE` → rosso
   - contiene `ERROR` o `errori` → rosso
   - altro → verde

---

## Come funzionano le classi Tester

Stanno in `src/main/resources/data/` e vengono caricate all'avvio da `TesterRegistry`.
Il registry le indicizza per **package**: quando arriva un upload, guarda il package
dichiarato nei file dello studente e sceglie il Tester corrispondente.

Ogni Tester ha dentro una classe helper `ClassTestManager` che usa la reflection per:
- **creare oggetti** (`crea(...)`) — chiama il costruttore con i tipi giusti
- **chiamare metodi** (`chiama(...)`) — invoca metodi per nome
- **confrontare risultati** (`stampa(...)`) — stampa `NomeClasse metodo OK` oppure
  `NomeClasse metodo {atteso:"X" ricevuto:"Y"} ERRORE`

Il confronto tra stringhe usa la **distanza di Levenshtein** (tolleranza ≤ 2 caratteri)
quando viene passato `true` come ultimo parametro: piccoli errori di formattazione
del `toString()` non fanno fallire il test.

---

## Struttura del codice

```
src/main/java/.../reviewer/
  controller/
    UploadController.java   — POST /upload
    StatusController.java   — GET /status/{jobId}
    UploadResponse.java     — DTO risposta upload
  service/
    AnalysisWorker.java     — compilazione + esecuzione asincrona
    JobRegistry.java        — registro in-memory dei job attivi
    TesterRegistry.java     — carica e indicizza le classi Tester
  model/
    JobStatus.java          — stato del job (PENDING/RUNNING/DONE/ERROR)
    JobStatusOperation.java — singola operazione con esito ok/ko
    Tester.java             — record con nome classe e bytes del sorgente

src/main/resources/
  data/
    TesterMobilita.java     — test per it.edu.iisgubbio.oggetti.mobilita
    TesterFattoria.java     — test per ...
    TesterSport.java        — test per ...
  static/
    index.html              — form upload con drag-and-drop
    status.html             — pagina di avanzamento con polling
    style.css               — tema dark
```

---

## Come aggiungere un nuovo Tester

1. Crea `src/main/resources/data/TesterNomeNuovo.java`
2. Metti `package it.edu.iisgubbio.oggetti.nuovopacchetto;` come prima riga
3. Scrivi un `main` che usa `ClassTestManager` per testare le classi
4. Riavvia l'applicazione — `TesterRegistry` lo carica in automatico

Gli studenti che caricano file con quel package vedranno i test nuovi senza
nessun'altra modifica.

---

## Limiti da tenere presenti

- I file temporanei in `/tmp/reviewer/` non vengono mai ripuliti automaticamente.
- Il `System.out` viene reindirizzato durante l'esecuzione del Tester: se più job
  girano in parallelo nello stesso momento possono interferire (non c'è sincronizzazione
  su `System.setOut`).
- L'ID studente accetta solo lettere e underscore. In caso di collisione viene
  aggiunto un suffisso numerico (`mario_1`, `mario_2`, ...).
