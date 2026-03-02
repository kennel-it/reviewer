package it.edu.iisgubbio.reviewer.controller;

import it.edu.iisgubbio.reviewer.AnalysisWorker;
import it.edu.iisgubbio.reviewer.JobBroker;
import it.edu.iisgubbio.reviewer.Tester;
import it.edu.iisgubbio.reviewer.TesterFinder;
import it.edu.iisgubbio.reviewer.dto.UploadResponse;
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

@RestController
public class Upload {

    private final AnalysisWorker analysisWorker;
    private final JobBroker jobBroker;
    private final TesterFinder testerFinder;

    public Upload(AnalysisWorker analysisWorker, JobBroker jobBroker, TesterFinder testerFinder) {
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
                    if (m.find()) packageDir = m.group(1);
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

        // --- Copia la classe di prova
        Tester tester = testerFinder.getTesterFor(packageDir);
        if(tester!=null){
            Path testerDir = resolvePackageDir(tester.bytes(), targetDir);
            System.out.println(">>>>>>> "+testerDir);
            try {
                Files.createDirectories(testerDir);
                Files.write(testerDir.resolve(tester.nomeClasse()+".java"), tester.bytes());
            } catch (IOException e) {
                return ResponseEntity.internalServerError()
                    .body(UploadResponse.error("Errore nel copiare la classe per eseguire i test"));
            }
        }

        if (saved == 0) {
            return ResponseEntity.badRequest()
                    .body(UploadResponse.error("Nessun file .java tra quelli caricati"));
        }

        jobBroker.register(effectiveId);
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
