package it.edu.iisgubbio.reviewer.reviewer.controller;

import it.edu.iisgubbio.reviewer.reviewer.AnalysisWorker;
import it.edu.iisgubbio.reviewer.reviewer.JobBroker;
import it.edu.iisgubbio.reviewer.reviewer.dto.UploadResponse;
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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class Upload {

    private final AnalysisWorker analysisWorker;
    private final JobBroker jobBroker;

    public Upload(AnalysisWorker analysisWorker, JobBroker jobBroker) {
        this.analysisWorker = analysisWorker;
        this.jobBroker = jobBroker;
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
        System.out.println("salvo in >>>"+targetDir);

        try {
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(UploadResponse.error("Impossibile creare la directory di lavoro"));
        }

        int saved = 0;
        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();
            if (originalName == null || !originalName.endsWith(".java")) {
                continue;
            }
            // Prende solo il nome del file, senza percorsi
            String safeName = Path.of(originalName).getFileName().toString();
            try {
                byte[] bytes = file.getBytes();
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

        jobBroker.register(effectiveId);
        analysisWorker.analyze(targetDir, effectiveId);

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
