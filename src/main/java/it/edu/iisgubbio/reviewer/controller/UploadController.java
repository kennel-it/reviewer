package it.edu.iisgubbio.reviewer.controller;

import it.edu.iisgubbio.reviewer.model.JobStatusOperation;
import it.edu.iisgubbio.reviewer.model.Tester;
import it.edu.iisgubbio.reviewer.service.AnalysisWorker;
import it.edu.iisgubbio.reviewer.service.JobRegistry;
import it.edu.iisgubbio.reviewer.service.TesterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/****************************************************************************
 * Endpoint per il caricamento dei sorgenti Java da parte degli studenti.
 * Riceve uno o più file .java via POST /upload, li salva in una directory
 * di lavoro temporanea rispettando la struttura dei package, vi affianca
 * la classe tester appropriata (scelta tramite TesterRegistry) e avvia
 * l'analisi asincrona tramite AnalysisWorker.
 * Risponde con un UploadResponse contenente il jobId da usare per il
 * polling su GET /status/{jobId}.
 * La risposta viene inviata a termine caricamento (non a termine del lavoro
 * che sta ad analisysworker )
 ***************************************************************************/
@RestController
public class UploadController {

    private final AnalysisWorker analysisWorker;
    private final JobRegistry jobBroker;
    private final TesterRegistry testerFinder;

    public UploadController(AnalysisWorker analysisWorker, JobRegistry jobBroker, TesterRegistry testerFinder) {
        this.analysisWorker = analysisWorker;
        this.jobBroker = jobBroker;
        this.testerFinder = testerFinder;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> upload(
            @RequestParam String id,
            @RequestParam List<MultipartFile> files) {

        if (!id.matches("[a-zA-Z_]+")) {
            return ResponseEntity.badRequest()
                    .body(UploadResponse.error("ID non valido: usare solo lettere e underscore"));
        }

        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(UploadResponse.error("Nessun file caricato"));
        }

        String effectiveId = jobBroker.generateId(id);
        Path targetDir = Path.of(System.getProperty("java.io.tmpdir"), "reviewer", effectiveId);

        try {
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(UploadResponse.error("Impossibile creare la directory di lavoro"));
        }

        int saved = 0;
        String packageDir = "";
        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();
            if (originalName == null || !originalName.endsWith(".java")) {
                continue;
            }
            // Prende solo il nome del file, senza percorsi
            String safeName = Path.of(originalName).getFileName().toString();
            try {
                byte[] bytes = file.getBytes();
                // Estrae il nome del pacchetto dal primo file che ne abbia uno
                if (packageDir.isEmpty()) {
                    Matcher m = PACKAGE_PATTERN.matcher(new String(bytes, StandardCharsets.UTF_8));
                    if (m.find()){
                        packageDir = m.group(1);
                    }
                }
                Path fileDir = resolvePackageDir(bytes, targetDir);
                Files.createDirectories(fileDir);
                Files.write(fileDir.resolve(safeName), bytes);
                saved++;
            } catch (IOException e) {
                return ResponseEntity.internalServerError()
                        .body(UploadResponse.error("Errore nel salvataggio di " + safeName));
            }
        }

        if (saved == 0) {
            return ResponseEntity.badRequest()
                    .body(UploadResponse.error("Nessun file .java tra quelli caricati"));
        }

        // --- Copia ClassTestManager (utility usata dai Tester)
        Tester manager = testerFinder.getManager();
        if (manager != null) {
            Path managerDir = resolvePackageDir(manager.bytes(), targetDir);
            try {
                Files.createDirectories(managerDir);
                Files.write(managerDir.resolve(manager.nomeClasse() + ".java"), manager.bytes());
            } catch (IOException e) {
                return ResponseEntity.internalServerError()
                        .body(UploadResponse.error("Errore nel copiare ClassTestManager"));
            }
        }

        // --- Copia la classe di prova
        Tester tester = testerFinder.getTesterFor(packageDir);
        if(tester!=null){
            Path testerDir = resolvePackageDir(tester.bytes(), targetDir);
            try {
                Files.createDirectories(testerDir);
                Files.write(testerDir.resolve(tester.nomeClasse()+".java"), tester.bytes());
            } catch (IOException e) {
                return ResponseEntity.internalServerError()
                    .body(UploadResponse.error("Errore nel copiare la classe per eseguire i test"));
            }
        }else{
            return ResponseEntity.badRequest()
                    .body(UploadResponse.error("Nessun test per il pacchetto "+packageDir));
        }

        jobBroker.register(effectiveId);
        jobBroker.setPackageName(effectiveId, packageDir);
        jobBroker.addOperation(effectiveId, new JobStatusOperation("Test per "+packageDir, true));
        analysisWorker.analyze(targetDir, effectiveId, packageDir+"."+tester.nomeClasse());

        return ResponseEntity.ok(UploadResponse.ok(saved + " file salvati. Analisi avviata.", effectiveId));
    }

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("^\\s*package\\s+([\\w.]+)\\s*;", Pattern.MULTILINE);

    private static Path resolvePackageDir(byte[] source, Path base) {
        Matcher m = PACKAGE_PATTERN.matcher(new String(source, StandardCharsets.UTF_8));
        if (m.find()) {
            String subPath = m.group(1).replace('.', '/');
            return base.resolve(subPath);
        }
        return base;
    }
}
